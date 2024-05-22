package ru.bk.j3000.normarchivedata.model.dto.ssfcsum;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.bk.j3000.normarchivedata.model.StandardSFC;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = true)
public class SsfcSumTZ extends SsfcSummary {

    public SsfcSumTZ(List<StandardSFC> allSsfcs) {
        super("tariff zone", "tariff zone",
                allSsfcs.stream()
                        .collect(Collectors.groupingBy(ssfc -> ssfc.getProperties()
                                .getTariffZone().getId()))
                        .values().stream()
                        .sorted(Comparator.comparing(ssfcs -> ssfcs.getFirst().getProperties()
                                .getTariffZone().getId()))
                        .map(ssfc -> (SsfcSummary) new SsfcSumSrc(ssfc,
                                ssfc.getFirst().getProperties().getTariffZone().getId().toString(),
                                ssfc.getFirst().getProperties().getTariffZone().getZoneName()))
                        .toList(),
                Collections.emptyList());
    }

    public SsfcSumTZ(List<StandardSFC> allSsfcs, String id, String name) {
        super(id, name,
                allSsfcs.stream()
                        .collect(Collectors.groupingBy(ssfc -> ssfc.getProperties()
                                .getTariffZone().getId()))
                        .values().stream()
                        .sorted(Comparator.comparing(ssfcs -> ssfcs.getFirst().getProperties()
                                .getTariffZone().getId()))
                        .map(ssfc -> (SsfcSummary) new SsfcSumSrc(ssfc,
                                ssfc.getFirst().getProperties().getTariffZone().getId().toString(),
                                ssfc.getFirst().getProperties().getTariffZone().getZoneName()))
                        .toList(),
                Collections.emptyList());
    }
}
