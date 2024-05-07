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
    private UUID id;
    private String srcName;
    private String fuelType;
    private String zoneName;
    private String branchName;
    private List<SsfcShortDTO> ssfcs;

    public SsfcsDTO(List<StandardSFC> ssfcs) {
        StandardSFC ssfc = ssfcs.getFirst();

        this.id = ssfc.getId();
        this.srcName = ssfc.getProperties().getId().getSource().getName();
        this.fuelType = ssfc.getFuelType().getName();
        this.zoneName = ssfc.getProperties().getTariffZone().getZoneName();
        this.branchName = ssfc.getProperties().getBranch().getBranchName();
        this.ssfcs = ssfcs.stream()
                .map(SsfcShortDTO::new)
                .sorted(Comparator.comparing(SsfcShortDTO::getMonth))
                .toList();
    }
}