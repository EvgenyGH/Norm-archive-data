package ru.bk.j3000.normarchivedata.model.dto.ssfcsum;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.bk.j3000.normarchivedata.model.StandardSFC;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = true)
public class SsfcSumBranch extends SsfcSummary {

    public SsfcSumBranch(List<StandardSFC> allSsfcs) {
        super(allSsfcs.getFirst().getProperties().getBranch().getId().toString(),
                allSsfcs.getFirst().getProperties().getBranch().getBranchName(),
                allSsfcs.stream()
                        .collect(Collectors.groupingBy(ssfc -> ssfc.getProperties()
                                .getBranch().getId()))
                        .values().stream()
                        .sorted(Comparator.comparing(ssfcs -> ssfcs.getFirst().getProperties()
                                .getBranch().getId()))
                        .map(ssfc -> (SsfcSummary) new SsfcSumSrc(ssfc))
                        .toList());
    }
}
