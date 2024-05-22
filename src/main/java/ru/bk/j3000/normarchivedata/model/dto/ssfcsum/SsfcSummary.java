package ru.bk.j3000.normarchivedata.model.dto.ssfcsum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bk.j3000.normarchivedata.model.FUEL_TYPE;
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

    /*
     *  avgData structure
     *
     *              X            | months (0 - 11) | year (12)
     * -------------------------------------------------------
     *   0 generation total        |                 |
     *   1 generation gas          |                 |
     *   2 generation diesel       |                 |
     * -------------------------------------------------------
     *   3 own needs total         |                 |
     *   4 own needs gas           |                 |
     *   5 own needs diesel        |                 |
     * -------------------------------------------------------
     *   6 own needs, % total      |                 |
     *   7 own needs, % gas        |                 |
     *   8 own needs, % diesel     |                 |
     * -------------------------------------------------------
     *   9 production total        |                 |
     *   10 production gas         |                 |
     *   11 production diesel      |                 |
     * -------------------------------------------------------
     *   12 ssfcg total            |                 |
     *   13 ssfcg gas              |                 |
     *   14 ssfcg diesel           |                 |
     * -------------------------------------------------------
     *   15 ssfc total             |                 |
     *   16 ssfc gas               |                 |
     *   17 ssfc diesel            |                 |
     * -------------------------------------------------------
     * */

    public double[][] avgData() {
        var avgData = new double[18][13];

        if (this.hasSub()) {
            avgData = subSsfcs.stream()
                    .map(SsfcSummary::avgData)
                    .reduce(new double[18][13],
                            (total, data) -> {
                                // put temporary fuel consumption to ssfc cells (15-17)
                                IntStream.range(0, total[0].length)
                                        .forEach(k -> {
                                            total[16][k] = total[16][k] * total[10][k];
                                            total[17][k] = total[17][k] * total[11][k];
                                            total[15][k] = total[16][k] * total[17][k];
                                        });

                                IntStream.range(0, total.length)
                                        .forEach(i -> {
                                            if (i >= 15) {
                                                IntStream.range(0, total[i].length)
                                                        .forEach(j -> {
                                                            double productionSum = total[i - 6][j]
                                                                    + data[i - 6][j];
                                                            double fuelConsumption = total[i][j]
                                                                    + data[i - 6][j] * data[i][j];
                                                            total[i][j] = productionSum == 0 ? 0
                                                                    : fuelConsumption / productionSum * 100d;
                                                        });
                                            } else if (i >= 12) {
                                                IntStream.range(0, total[i].length)
                                                        .forEach(j -> {
                                                            double generationSum = total[i - 12][j]
                                                                    + data[i - 12][j];
                                                            double fuelConsumption = total[i][j]
                                                                    + data[i - 3][j] * data[i + 3][j];
                                                            total[i][j] = generationSum == 0 ? 0
                                                                    : fuelConsumption / generationSum * 100d;
                                                        });
                                            } else if (i >= 6 && i <= 8) {
                                                IntStream.range(0, total[i].length)
                                                        .forEach(j ->
                                                                total[i][j] = total[i - 6][j] == 0 ? 0
                                                                        : total[i - 3][i] / total[i - 6][i] * 100d);
                                            } else {
                                                IntStream.range(0, total[i].length)
                                                        .forEach(j -> {
                                                            total[i][j] += data[i][j];
                                                        });
                                            }
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
                avgData[1][i] = month.stream()
                        .filter(ssfc -> ssfc.getFuelType().equals(FUEL_TYPE.GAS))
                        .mapToDouble(StandardSFC::getGeneration)
                        .sum();
                avgData[1][12] += avgData[1][i];

                avgData[2][i] = month.stream()
                        .filter(ssfc -> ssfc.getFuelType().equals(FUEL_TYPE.DIESEL))
                        .mapToDouble(StandardSFC::getGeneration)
                        .sum();
                avgData[2][12] += avgData[2][i];

                avgData[0][i] = avgData[1][i] + avgData[2][i];
                avgData[0][12] += avgData[0][i];

                // own needs
                avgData[4][i] = month.stream()
                        .filter(ssfc -> ssfc.getFuelType().equals(FUEL_TYPE.GAS))
                        .mapToDouble(StandardSFC::getOwnNeeds)
                        .sum();
                avgData[4][12] += avgData[4][i];

                avgData[5][i] = month.stream()
                        .filter(ssfc -> ssfc.getFuelType().equals(FUEL_TYPE.DIESEL))
                        .mapToDouble(StandardSFC::getOwnNeeds)
                        .sum();
                avgData[5][12] += avgData[5][i];

                avgData[3][i] = avgData[4][i] + avgData[5][i];
                avgData[3][12] += avgData[3][i];

                // own need %
                avgData[6][i] = avgData[0][i] == 0 ? 0 : avgData[3][i] / avgData[0][i] * 100d;
                avgData[7][i] = avgData[1][i] == 0 ? 0 : avgData[4][i] / avgData[1][i] * 100d;
                avgData[8][i] = avgData[2][i] == 0 ? 0 : avgData[5][i] / avgData[2][i] * 100d;

                // production
                avgData[10][i] = month.stream()
                        .filter(ssfc -> ssfc.getFuelType().equals(FUEL_TYPE.GAS))
                        .mapToDouble(StandardSFC::getProduction)
                        .sum();
                avgData[10][12] += avgData[10][i];

                avgData[11][i] = month.stream()
                        .filter(ssfc -> ssfc.getFuelType().equals(FUEL_TYPE.DIESEL))
                        .mapToDouble(StandardSFC::getProduction)
                        .sum();
                avgData[11][12] += avgData[11][i];

                avgData[9][i] = avgData[10][i] + avgData[11][i];
                avgData[9][12] += avgData[9][i];

                // ssfcg
                avgData[12][i] = month.stream()
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

                avgData[13][i] = month.stream()
                        .filter(ssfc -> ssfc.getFuelType().equals(FUEL_TYPE.GAS))
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

                avgData[14][i] = month.stream()
                        .filter(ssfc -> ssfc.getFuelType().equals(FUEL_TYPE.DIESEL))
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
                avgData[15][i] = month.stream()
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

                avgData[16][i] = month.stream()
                        .filter(ssfc -> ssfc.getFuelType().equals(FUEL_TYPE.GAS))
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

                avgData[17][i] = month.stream()
                        .filter(ssfc -> ssfc.getFuelType().equals(FUEL_TYPE.DIESEL))
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

                 /*
                 avgData[15][12] - temporary collecting fuel consumption total
                 avgData[16][12] - temporary collecting fuel consumption gas
                 avgData[17][12] - temporary collecting fuel consumption diesel
                 */
                avgData[15][12] += avgData[15][i] * avgData[9][i];
                avgData[16][12] += avgData[16][i] * avgData[10][i];
                avgData[17][12] += avgData[17][i] * avgData[11][i];
            }

            // total own needs %
            avgData[6][12] = avgData[0][12] == 0 ? 0 : avgData[3][12] / avgData[0][12] * 100d;
            avgData[7][12] = avgData[1][12] == 0 ? 0 : avgData[4][12] / avgData[1][12] * 100d;
            avgData[8][12] = avgData[2][12] == 0 ? 0 : avgData[5][12] / avgData[2][12] * 100d;

            // total ssfcg
            avgData[12][12] = avgData[0][12] == 0 ? 0 : avgData[15][12] / avgData[0][12];
            avgData[13][12] = avgData[1][12] == 0 ? 0 : avgData[16][12] / avgData[1][12];
            avgData[14][12] = avgData[2][12] == 0 ? 0 : avgData[17][12] / avgData[2][12];

            // total ssfc
            avgData[15][12] = avgData[0][12] == 0 ? 0 : avgData[15][12] / avgData[9][12];
            avgData[16][12] = avgData[1][12] == 0 ? 0 : avgData[16][12] / avgData[10][12];
            avgData[17][12] = avgData[2][12] == 0 ? 0 : avgData[17][12] / avgData[11][12];
        }

        return avgData;
    }

    private double[][] getDefaultArray(int length) {
        return new double[6][length];
    }
}
