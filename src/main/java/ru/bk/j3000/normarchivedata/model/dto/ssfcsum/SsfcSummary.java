package ru.bk.j3000.normarchivedata.model.dto.ssfcsum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bk.j3000.normarchivedata.model.StandardSFC;

import java.util.List;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SsfcSummary {
    private static final Logger log = LoggerFactory.getLogger(SsfcSummary.class);
    private String id;
    private String name;
    private List<SsfcSummary> subSsfcs;
    private List<StandardSFC> allSsfcs;

    public boolean hasSub() {
        return Objects.nonNull(subSsfcs) && !subSsfcs.isEmpty();
    }

    // methods

    public double[][] avgData() {
        var avgData = new double[6][13];

        if (this.hasSub()) {
            avgData = subSsfcs.stream()
                    .map(SsfcSummary::avgData)
                    .reduce(new double[6][12],
                            (total, gen) -> {
                                IntStream.range(0, 12)
                                        .forEach(index -> {
                                            total[0][index] =
                                                    total[0][index] + gen[0][index];
                                        });
                                return total;
                            });
        } else {
            var months = allSsfcs.stream()
                    .collect(Collectors.groupingBy(StandardSFC::getMonth, TreeMap::new,
                            Collectors.toList()))
                    .values().stream().toList();

            for (int i = 0; i < 12; i++) {
                List<StandardSFC> month = months.get(i);

                // generation
                avgData[0][i] = month.stream()
                        .mapToDouble(StandardSFC::getGeneration)
                        .sum();
                avgData[0][12] += avgData[0][i];

                // own needs
                avgData[1][i] = month.stream()
                        .mapToDouble(StandardSFC::getOwnNeeds)
                        .sum();
                avgData[1][12] += avgData[1][i];

                // own need %
                avgData[2][i] = avgData[0][i] == 0 ? 0 : avgData[1][i] / avgData[0][i] * 100d;

                // production
                avgData[3][i] = month.stream()
                        .mapToDouble(StandardSFC::getProduction)
                        .sum();
                avgData[3][12] += avgData[3][i];

                // ssfcg
                avgData[4][i] = month.stream()
                        .reduce(new Object() {
                                    double mult = 0d;
                                    double gen = 0d;
                                    double total = 0d;
                                },
                                (result, next) -> {
                                    result.mult += next.getSsfcg() * next.getGeneration();
                                    result.gen += next.getGeneration();
                                    result.total = result.mult / result.gen;
                                    return result;
                                },
                                (r1, r2) -> {
                                    r1.gen += r2.gen;
                                    r1.mult += r2.mult;
                                    r1.total = r1.mult / r1.gen;
                                    return r1;
                                })
                        .total;


                // ssfc
                avgData[5][i] = month.stream()
                        .reduce(new Object() {
                                    double mult = 0d;
                                    double prod = 0d;
                                    double total = 0d;
                                },
                                (result, next) -> {
                                    result.mult += next.getSsfc() * next.getProduction();
                                    result.prod += next.getProduction();
                                    result.total = result.mult / result.prod;
                                    return result;
                                },
                                (r1, r2) -> {
                                    r1.prod += r2.prod;
                                    r1.mult += r2.mult;
                                    r1.total = r1.mult / r1.prod;
                                    return r1;
                                })
                        .total;

                // avgData[5][13] - temporary collecting fuel consumption
                avgData[5][12] += avgData[3][i] * avgData[5][i];
            }

            // total own needs %
            avgData[2][12] = avgData[0][12] == 0 ? 0 : avgData[1][12] / avgData[0][12] * 100d;

            // total ssfcg
            avgData[4][12] = avgData[5][12] / avgData[0][12];

            // total ssfc
            avgData[5][12] = avgData[5][12] / avgData[3][12];
        }

        return avgData;
    }

    private double[][] getDefaultArray(int length) {
        return new double[6][length];
    }
}
