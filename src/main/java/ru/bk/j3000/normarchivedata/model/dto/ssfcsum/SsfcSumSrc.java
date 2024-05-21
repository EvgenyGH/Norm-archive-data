package ru.bk.j3000.normarchivedata.model.dto.ssfcsum;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.bk.j3000.normarchivedata.model.StandardSFC;

import java.util.Comparator;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class SsfcSumSrc extends SsfcSummary {
    private List<StandardSFC> allSsfcs;

    public SsfcSumSrc(List<StandardSFC> allSsfcs) {
        super(allSsfcs.getFirst().getProperties().getBranch().getId().toString(),
                allSsfcs.getFirst().getProperties().getBranch().getBranchName(),
                List.of());
        this.allSsfcs = allSsfcs.stream()
                .sorted(Comparator.comparing(StandardSFC::getFuelType))
                .sorted(Comparator.comparing(ssfc -> ssfc.getProperties()
                        .getId().getSource().getSourceType()))
                .sorted(Comparator.comparing(ssfc -> ssfc.getProperties()
                        .getId().getSource().getName()))
                .toList();
    }
}