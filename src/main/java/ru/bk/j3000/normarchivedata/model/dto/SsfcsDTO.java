package ru.bk.j3000.normarchivedata.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bk.j3000.normarchivedata.model.StandardSFC;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class SsfcsDTO {
    private String srcName;
    private UUID srcId;
    private Integer sourceType;
    private String fuelType;
    private String zoneName;
    private String branchName;
    private List<SsfcShortDTO> ssfcs;

    public SsfcsDTO(List<StandardSFC> ssfcs) {
        StandardSFC ssfc = ssfcs.getFirst();

        this.srcName = ssfc.getProperties().getId().getSource().getName();
        this.srcId = ssfc.getProperties().getId().getSource().getId();
        this.sourceType = ssfc.getProperties().getId().getSource().getSourceType().ordinal();
        this.fuelType = ssfc.getFuelType().getName();
        this.zoneName = ssfc.getProperties().getTariffZone().getZoneName();
        this.branchName = ssfc.getProperties().getBranch().getBranchName();
        this.ssfcs = ssfcs.stream()
                .map(SsfcShortDTO::new)
                .sorted(Comparator.comparing(SsfcShortDTO::getMonth))
                .sorted(Comparator.comparing(SsfcShortDTO::getYear))
                .toList();
    }

    public List<UUID> getIds() {
        return ssfcs.stream()
                .map(SsfcShortDTO::getId)
                .toList();
    }

    public Double avgGeneration() {
        return ssfcs.stream()
                .mapToDouble(SsfcShortDTO::getGeneration)
                .sum();
    }

    public Double avgOwnNeeds() {
        return ssfcs.stream()
                .mapToDouble(SsfcShortDTO::getOwnNeeds)
                .sum();
    }

    public Double avgProduction() {
        return ssfcs.stream()
                .mapToDouble(SsfcShortDTO::getProduction)
                .sum();
    }

    public Double avgSsfc() {
        return ssfcs.stream()
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
    }

    public Double avgSsfcg() {
        return ssfcs.stream()
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
    }

    public Double avgPercentOwnNeeds() {
        Double generation = avgGeneration();

        return generation == 0 ? 0 : avgOwnNeeds() / generation * 100d;
    }
}