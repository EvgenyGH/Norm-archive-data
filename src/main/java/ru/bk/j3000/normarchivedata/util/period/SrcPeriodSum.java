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
        this.periods = Collections.emptyList();
    }

    private double[][] formAvgData(List<StandardSFC> ssfcs, List<YearMonth> yearMonths) {
        var avgData = new double[21][yearMonths.size() + 1];

        // all periods data (no sum)
        IntStream.range(0, yearMonths.size())
                .forEach(i -> {
                            List<StandardSFC> periodSsfcs = ssfcs.stream()
                                    .filter(ssfc -> {
                                        if (yearMonths.get(i).month().equals(0)) {
                                            return ssfc.getProperties().getId()
                                                    .getYear().equals(yearMonths.get(i).year());
                                        } else {
                                            return ssfc.getProperties().getId()
                                                    .getYear().equals(yearMonths.get(i).year()) &&
                                                    ssfc.getMonth().equals(yearMonths.get(i).month());
                                        }
                                    })
                                    .toList();

                    periodSsfcs.forEach(ssfc -> {
                        int addition = switch (ssfc.getFuelType()) {
                            case FUEL_TYPE.GAS -> 1;
                            case FUEL_TYPE.DIESEL -> 2;
                        };

                        // generation
                        avgData[addition][i] += ssfc.getGeneration();
                        avgData[0][i] = avgData[1][i] + avgData[2][i];

                        // own needs
                        avgData[3 + addition][i] += ssfc.getOwnNeeds();
                        avgData[3][i] = avgData[4][i] + avgData[5][i];

                        // own need %
                        avgData[6 + addition][i] = avgData[addition][i] == 0
                                ? 0 : avgData[3 + addition][i] / avgData[addition][i] * 100d;
                        avgData[6][i] = avgData[0][i] == 0 ? 0 : avgData[3][i] / avgData[0][i] * 100d;

                        // production
                        avgData[9 + addition][i] += ssfc.getProduction();
                        avgData[9][i] = avgData[10][i] + avgData[11][i];

                        //fuel
                        avgData[18 + addition][i] += ssfc.getSsfcg() * ssfc.getGeneration();
                        avgData[18][i] = avgData[19][i] + avgData[20][i];

                        // ssfcg
                        avgData[12][i] = avgData[0][i] == 0 ? 0 : avgData[18][i] / avgData[0][i];
                        avgData[13][i] = avgData[1][i] == 0 ? 0 : avgData[19][i] / avgData[1][i];
                        avgData[14][i] = avgData[2][i] == 0 ? 0 : avgData[20][i] / avgData[2][i];

                        // ssfc
                        avgData[15][i] = avgData[9][i] == 0 ? 0 : avgData[18][i] / avgData[9][i];
                        avgData[16][i] = avgData[10][i] == 0 ? 0 : avgData[18][i] / avgData[10][i];
                        avgData[17][i] = avgData[11][i] == 0 ? 0 : avgData[18][i] / avgData[11][i];
                    });
                        }

                );

        // all periods data (sum)
        IntStream.of(0, 1, 2, 3, 4, 5, 9, 10, 11, 18, 19, 20).forEach(i -> {
            // generation
            // own needs
            // production
            // fuel
            IntStream.range(0, avgData[0].length - 1).forEach(j -> {
                avgData[i][avgData[0].length - 1] += avgData[i][j];
            });

            int totalColl = avgData[0].length - 1;

            // own need %
            avgData[6][totalColl] = avgData[0][totalColl] == 0
                    ? 0 : avgData[3][totalColl] / avgData[0][totalColl] * 100d;
            avgData[7][totalColl] = avgData[0][totalColl] == 0
                    ? 0 : avgData[4][totalColl] / avgData[1][totalColl] * 100d;
            avgData[8][totalColl] = avgData[0][totalColl] == 0
                    ? 0 : avgData[5][totalColl] / avgData[2][totalColl] * 100d;

            // ssfcg
            avgData[12][totalColl] = avgData[0][totalColl] == 0
                    ? 0 : avgData[18][totalColl] / avgData[0][totalColl];
            avgData[13][totalColl] = avgData[1][totalColl] == 0
                    ? 0 : avgData[19][totalColl] / avgData[1][totalColl];
            avgData[14][totalColl] = avgData[2][totalColl] == 0
                    ? 0 : avgData[20][totalColl] / avgData[2][totalColl];

            // ssfc
            avgData[15][totalColl] = avgData[9][totalColl] == 0
                    ? 0 : avgData[18][totalColl] / avgData[9][totalColl];
            avgData[16][totalColl] = avgData[10][totalColl] == 0
                    ? 0 : avgData[18][totalColl] / avgData[10][totalColl];
            avgData[17][totalColl] = avgData[11][totalColl] == 0
                    ? 0 : avgData[18][totalColl] / avgData[11][totalColl];
        });

        return avgData;
    }
}