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
public class SsfcSumTZBranch extends SsfcSummary {

    public SsfcSumTZBranch(List<StandardSFC> allSsfcs) {
        super("base", "base",
                allSsfcs.stream()
                        .collect(Collectors.groupingBy(ssfc -> ssfc.getProperties()
                                .getTariffZone().getId()))
                        .values().stream()
                        .sorted(Comparator.comparing(ssfcs -> ssfcs.getFirst().getProperties()
                                .getTariffZone().getId()))
                        .map(ssfc -> (SsfcSummary) new SsfcSumBranch(ssfc))
                        .toList(),
                Collections.emptyList()
        );
    }

    public SsfcSumTZBranch(List<StandardSFC> allSsfcs, String id, String name) {
        super(id, name,
                allSsfcs.stream()
                        .collect(Collectors.groupingBy(ssfc -> ssfc.getProperties()
                                .getTariffZone().getId()))
                        .values().stream()
                        .sorted(Comparator.comparing(ssfcs -> ssfcs.getFirst().getProperties()
                                .getTariffZone().getId()))
                        .map(ssfc -> (SsfcSummary) new SsfcSumBranch(ssfc))
                        .toList(),
                Collections.emptyList()
        );
    }
}
