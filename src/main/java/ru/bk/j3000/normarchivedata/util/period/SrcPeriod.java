package ru.bk.j3000.normarchivedata.util.period;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.bk.j3000.normarchivedata.model.FUEL_TYPE;
import ru.bk.j3000.normarchivedata.model.StandardSFC;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/*
 *  avgData structure
 *
 *              X              |                 |
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
@EqualsAndHashCode
@Getter
@ToString
public class SrcPeriod {
    private double[] avgData;
    private String srcName;
    private UUID srcId;
    private String zoneName;
    private Integer zoneId;
    private String branchName;
    private Integer branchId;
    private boolean isEmpty = true;

    private SrcPeriod() {
    }

    public SrcPeriod(List<StandardSFC> ssfcs) {
        super();
        initData(ssfcs);
    }

    private void initData(List<StandardSFC> ssfcs) {
        if (!ssfcs.isEmpty()) {
            var ssfc = ssfcs.getFirst();
            this.srcName = ssfc.getProperties().getId().getSource().getName();
            this.srcId = ssfc.getProperties().getId().getSource().getId();
            this.zoneName = ssfc.getProperties().getTariffZone().getZoneName();
            this.zoneId = ssfc.getProperties().getTariffZone().getId();
            this.branchName = ssfc.getProperties().getBranch().getBranchName();
            this.branchId = ssfc.getProperties().getBranch().getId();
            this.avgData = avgData(ssfcs);
            this.isEmpty = false;
        }
    }

    private double[] avgData(List<StandardSFC> ssfcs) {
        var avgData = new double[21];
        Arrays.fill(avgData, 0);

        ssfcs.forEach(ssfc -> {
            int addition = switch (ssfc.getFuelType()) {
                case FUEL_TYPE.GAS -> 1;
                case FUEL_TYPE.DIESEL -> 2;
            };

            // generation
            avgData[addition] += ssfc.getGeneration();
            avgData[0] = avgData[1] + avgData[2];

            // own needs
            avgData[3 + addition] += ssfc.getOwnNeeds();
            avgData[3] = avgData[4] + avgData[5];

            // own need %
            avgData[6 + addition] = avgData[addition] == 0 ? 0 : avgData[3 + addition] / avgData[addition] * 100d;
            avgData[6] = avgData[0] == 0 ? 0 : avgData[3] / avgData[0] * 100d;

            // production
            avgData[9 + addition] += ssfc.getProduction();
            avgData[9] = avgData[10] + avgData[11];

            //fuel
            avgData[18 + addition] += ssfc.getSsfcg() * ssfc.getGeneration();
            avgData[18] = avgData[19] + avgData[20];

            // ssfcg
            avgData[12] = avgData[0] == 0 ? 0 : avgData[18] / avgData[0];
            avgData[13] = avgData[1] == 0 ? 0 : avgData[19] / avgData[1];
            avgData[14] = avgData[2] == 0 ? 0 : avgData[20] / avgData[2];

            // ssfc
            avgData[15] = avgData[9] == 0 ? 0 : avgData[18] / avgData[9];
            avgData[16] = avgData[10] == 0 ? 0 : avgData[18] / avgData[10];
            avgData[17] = avgData[11] == 0 ? 0 : avgData[18] / avgData[11];
        });

        return avgData;
    }
}