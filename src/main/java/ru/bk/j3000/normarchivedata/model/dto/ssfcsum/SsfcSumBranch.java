package ru.bk.j3000.normarchivedata.model.dto.ssfcsum;

import lombok.EqualsAndHashCode;
import ru.bk.j3000.normarchivedata.model.StandardSFC;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
public class SsfcSumBranch extends SsfcSummary {

    public SsfcSumBranch(List<StandardSFC> allSsfcs) {
        super(allSsfcs.getFirst().getProperties().getTariffZone().getId().toString(),
                allSsfcs.getFirst().getProperties().getTariffZone().getZoneName(),
                allSsfcs.stream()
                        .collect(Collectors.groupingBy(ssfc -> ssfc.getProperties()
                                .getBranch().getId()))
                        .values().stream()
                        .sorted(Comparator.comparing(ssfcs -> ssfcs.getFirst().getProperties()
                                .getBranch().getId()))
                        .map(ssfc -> (SsfcSummary) new SsfcSumSrc(ssfc,
                                ssfc.getFirst().getProperties().getBranch().getId().toString(),
                                ssfc.getFirst().getProperties().getBranch().getBranchName()))
                        .toList(),
                Collections.emptyList());
    }

    public SsfcSumBranch(List<StandardSFC> allSsfcs, String id, String name) {
        super(id, name, allSsfcs.stream()
                        .collect(Collectors.groupingBy(ssfc -> ssfc.getProperties()
                                .getBranch().getId()))
                        .values().stream()
                        .sorted(Comparator.comparing(ssfcs -> ssfcs.getFirst().getProperties()
                                .getBranch().getId()))
                        .map(ssfc -> (SsfcSummary) new SsfcSumSrc(ssfc,
                                ssfc.getFirst().getProperties().getBranch().getId().toString(),
                                ssfc.getFirst().getProperties().getBranch().getBranchName()))
                        .toList(),
                Collections.emptyList());
    }
}
