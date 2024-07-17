package ru.bk.j3000.normarchivedata.util.period;

import lombok.Getter;

import java.util.List;
import java.util.stream.IntStream;

/*
 *  avgData structure
 *
 *              X              | Periods (0 - all) | total (last)
 *  -----------------------------------------------------------
 *   0 generation total        |                   |
 *   1 generation gas          |                   |
 *   2 generation diesel       |                   |
 *  -----------------------------------------------------------
 *   3 own needs total         |                   |
 *   4 own needs gas           |                   |
 *   5 own needs diesel        |                   |
 *  -----------------------------------------------------------
 *   6 own needs, % total      |                   |
 *   7 own needs, % gas        |                   |
 *   8 own needs, % diesel     |                   |
 *  -----------------------------------------------------------
 *   9 production total        |                   |
 *   10 production gas         |                   |
 *   11 production diesel      |                   |
 *  -----------------------------------------------------------
 *   12 ssfcg total            |                   |
 *   13 ssfcg gas              |                   |
 *   14 ssfcg diesel           |                   |
 *  -----------------------------------------------------------
 *   15 ssfc total             |                   |
 *   16 ssfc gas               |                   |
 *   17 ssfc diesel            |                   |
 *  -----------------------------------------------------------
 *   18 special fuel total     |                   |
 *   19 special gas            |                   |
 *   20 special diesel         |                   |
 *  -----------------------------------------------------------
 * */
public abstract class PeriodSumBase implements PeriodSum {
    protected List<PeriodSum> periods;

    @Getter
    protected double[][] avgData;

    @Getter
    private final String name;

    public PeriodSumBase(String name) {
        this.name = name;
    }

    protected double[][] calcAvgData() {
        var avgData = new double[21][this.periods.getFirst().getAvgData()[0].length];

        periods.forEach(period -> {
            var periodData = period.getAvgData();

            // generation
            // own needs
            // production
            // fuel
            IntStream.range(0, avgData.length)
                    .filter(i -> i < 12 || i > 17)
                    .forEach(i ->
                            IntStream.range(0, avgData[0].length)
                                    .forEach(j ->
                            avgData[i][j] += periodData[i][j]));


            IntStream.range(0, avgData[0].length).forEach(j -> {
                // own need %
                avgData[6][j] = avgData[0][j] == 0 ? 0 : avgData[3][j] / avgData[0][j] * 100;
                avgData[7][j] = avgData[1][j] == 0 ? 0 : avgData[4][j] / avgData[1][j] * 100;
                avgData[8][j] = avgData[2][j] == 0 ? 0 : avgData[5][j] / avgData[2][j] * 100;

                // ssfcg
                avgData[12][j] = avgData[0][j] == 0 ? 0 : avgData[18][j] / avgData[0][j];
                avgData[13][j] = avgData[1][j] == 0 ? 0 : avgData[19][j] / avgData[1][j];
                avgData[14][j] = avgData[2][j] == 0 ? 0 : avgData[20][j] / avgData[2][j];

                // ssfc
                avgData[15][j] = avgData[9][j] == 0 ? 0 : avgData[18][j] / avgData[9][j];
                avgData[16][j] = avgData[10][j] == 0 ? 0 : avgData[19][j] / avgData[10][j];
                avgData[17][j] = avgData[11][j] == 0 ? 0 : avgData[20][j] / avgData[11][j];
            });
        });

        return avgData;
    }

    @Override
    public boolean hasSubs() {
        return !this.periods.isEmpty();
    }

    @Override
    public List<PeriodSum> getPeriods() {
        return periods;
    }
}
