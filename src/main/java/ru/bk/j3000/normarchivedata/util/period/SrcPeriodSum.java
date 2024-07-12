package ru.bk.j3000.normarchivedata.util.period;

import lombok.Getter;
import lombok.ToString;
import ru.bk.j3000.normarchivedata.model.FUEL_TYPE;
import ru.bk.j3000.normarchivedata.model.SOURCE_TYPE;
import ru.bk.j3000.normarchivedata.model.StandardSFC;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

/*
 *  avgData structure
 *
 *              X              |     periods     |   total
 *  ---------------------------------------------------------
 *   0 generation total        |                 |
 *   1 generation gas          |                 |
 *   2 generation diesel       |                 |
 *  ---------------------------------------------------------
 *   3 own needs total         |                 |
 *   4 own needs gas           |                 |
 *   5 own needs diesel        |                 |
 *  ---------------------------------------------------------
 *   6 own needs, % total      |                 |
 *   7 own needs, % gas        |                 |
 *   8 own needs, % diesel     |                 |
 *  ---------------------------------------------------------
 *   9 production total        |                 |
 *   10 production gas         |                 |
 *   11 production diesel      |                 |
 *  ---------------------------------------------------------
 *   12 ssfcg total            |                 |
 *   13 ssfcg gas              |                 |
 *   14 ssfcg diesel           |                 |
 *  ---------------------------------------------------------
 *   15 ssfc total             |                 |
 *   16 ssfc gas               |                 |
 *   17 ssfc diesel            |                 |
 *  ---------------------------------------------------------
 *   18 special fuel total     |                 |
 *   19 special gas            |                 |
 *   20 special diesel         |                 |
 *  ---------------------------------------------------------
 *
 * */
@Getter
@ToString
public class SrcPeriodSum extends PeriodSumBase {
    private final String srcName;
    private final UUID srcId;
    private final String zoneName;
    private final Integer zoneId;
    private final String branchName;
    private final Integer branchId;
    private final SOURCE_TYPE type;

    //todo data validation
    public SrcPeriodSum(List<StandardSFC> ssfcs, List<YearMonth> yearMonths) {
        super(ssfcs.getFirst().getProperties().getId().getSource().getName());

        var ssfc = ssfcs.getFirst();
        this.srcName = ssfc.getProperties().getId().getSource().getName();
        this.srcId = ssfc.getProperties().getId().getSource().getId();
        this.zoneName = ssfc.getProperties().getTariffZone().getZoneName();
        this.zoneId = ssfc.getProperties().getTariffZone().getId();
        this.branchName = ssfc.getProperties().getBranch().getBranchName();
        this.branchId = ssfc.getProperties().getBranch().getId();
        this.type = ssfc.getProperties().getId().getSource().getSourceType();
        this.avgData = formAvgData(ssfcs, yearMonths);

        setPeriods(Collections.emptyList());
    }

    private Double[][] formAvgData(List<StandardSFC> ssfcs, List<YearMonth> yearMonths) {
        var avgData = new Double[21][yearMonths.size() + 1];

        IntStream.rangeClosed(0, yearMonths.size())
                .forEach(i -> {
                            List<StandardSFC> periodSsfcs = ssfcs.stream()
                                    .filter(ssfc -> ssfc.getProperties().getId()
                                            .getYear().equals(yearMonths.get(i).year()) &&
                                            ssfc.getMonth().equals(yearMonths.get(i).month()))
                                    .toList();

                            if (periodSsfcs.isEmpty()) {
                                IntStream.range(0, avgData.length).forEach(j -> avgData[j][i] = 0d);

                            } else {
                                periodSsfcs.forEach(ssfc -> {
                                    int addition = switch (ssfc.getFuelType()) {
                                        case FUEL_TYPE.GAS -> 1;
                                        case FUEL_TYPE.DIESEL -> 2;
                                    };

                                    // generation
                                    avgData[addition][i] += ssfc.getGeneration();
                                    avgData[0][i] = avgData[1][i] + avgData[2][i];

                                    avgData[addition][yearMonths.size()] += avgData[addition][i];
                                    avgData[0][yearMonths.size()] += avgData[0][i];

                                    // own needs
                                    avgData[3 + addition][i] += ssfc.getOwnNeeds();
                                    avgData[3][i] = avgData[4][i] + avgData[5][i];

                                    avgData[3 + addition][yearMonths.size()] += avgData[3 + addition][i];
                                    avgData[3][yearMonths.size()] += avgData[3][i];

                                    // own need %
                                    avgData[6 + addition][i] = avgData[addition][i] == 0
                                            ? 0 : avgData[3 + addition][i] / avgData[addition][i] * 100d;
                                    avgData[6][i] = avgData[0][i] == 0 ? 0 : avgData[3][i] / avgData[0][i] * 100d;

                                    avgData[6 + addition][yearMonths.size()] = avgData[addition][yearMonths.size()] == 0
                                            ? 0 : avgData[3 + addition][yearMonths.size()]
                                            / avgData[addition][yearMonths.size()] * 100d;
                                    avgData[6][yearMonths.size()] = avgData[0][yearMonths.size()] == 0
                                            ? 0 : avgData[3][yearMonths.size()]
                                            / avgData[0][yearMonths.size()] * 100d;

                                    // production
                                    avgData[9 + addition][i] += ssfc.getProduction();
                                    avgData[9][i] = avgData[10][i] + avgData[11][i];

                                    avgData[9 + addition][yearMonths.size()] = avgData[9 + addition][i];
                                    avgData[9][yearMonths.size()] = avgData[9][i];

                                    //fuel
                                    avgData[18 + addition][i] += ssfc.getSsfcg() * ssfc.getGeneration();
                                    avgData[18][yearMonths.size()] = avgData[19][i] + avgData[20][i];

                                    avgData[18 + addition][i] = avgData[18 + addition][i];
                                    avgData[18][yearMonths.size()] = avgData[18][i];

                                    // ssfcg
                                    avgData[12][i] = avgData[0][i] == 0 ? 0 : avgData[18][i] / avgData[0][i];
                                    avgData[13][i] = avgData[1][i] == 0 ? 0 : avgData[19][i] / avgData[1][i];
                                    avgData[14][i] = avgData[2][i] == 0 ? 0 : avgData[20][i] / avgData[2][i];

                                    avgData[12][yearMonths.size()] = avgData[0][yearMonths.size()] == 0
                                            ? 0 : avgData[18][yearMonths.size()]
                                            / avgData[0][yearMonths.size()];
                                    avgData[13][yearMonths.size()] = avgData[1][yearMonths.size()] == 0
                                            ? 0 : avgData[19][yearMonths.size()]
                                            / avgData[1][yearMonths.size()];
                                    avgData[14][yearMonths.size()] = avgData[2][yearMonths.size()] == 0
                                            ? 0 : avgData[20][yearMonths.size()]
                                            / avgData[2][yearMonths.size()];

                                    // ssfc
                                    avgData[15][i] = avgData[9][i] == 0 ? 0 : avgData[18][i] / avgData[9][i];
                                    avgData[16][i] = avgData[10][i] == 0 ? 0 : avgData[18][i] / avgData[10][i];
                                    avgData[17][i] = avgData[11][i] == 0 ? 0 : avgData[18][i] / avgData[11][i];

                                    avgData[15][yearMonths.size()] = avgData[9][yearMonths.size()] == 0
                                            ? 0 : avgData[18][yearMonths.size()]
                                            / avgData[9][yearMonths.size()];
                                    avgData[16][yearMonths.size()] = avgData[10][yearMonths.size()] == 0
                                            ? 0 : avgData[18][yearMonths.size()]
                                            / avgData[10][yearMonths.size()];
                                    avgData[17][yearMonths.size()] = avgData[11][yearMonths.size()] == 0
                                            ? 0 : avgData[18][yearMonths.size()]
                                            / avgData[11][yearMonths.size()];
                                });
                            }
                        }
                );

        return avgData;
    }
}