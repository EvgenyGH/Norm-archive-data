package ru.bk.j3000.normarchivedata.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bk.j3000.normarchivedata.model.StandardSFC;

import java.util.UUID;

@Data
@NoArgsConstructor
public class SsfcDTO {
    private UUID id;
    private UUID srcId;
    private String srcName;
    private Double generation;
    private Double ownNeeds;
    private Double production;
    private Double ssfc;
    private Double ssfcg;
    private String fuelType;
    private String branchName;
    private String zoneName;
    private Integer year;
    private Integer month;


    public SsfcDTO(StandardSFC ssfc) {
        this.id = ssfc.getId();
        this.generation = ssfc.getGeneration();
        this.ownNeeds = ssfc.getOwnNeeds();
        this.production = ssfc.getProduction();
        this.ssfc = ssfc.getSsfc();
        this.ssfcg = ssfc.getSsfcg();
        this.fuelType = ssfc.getFuelType().getName();
        this.branchName = ssfc.getProperties().getBranch().getBranchName();
        this.zoneName = ssfc.getProperties().getTariffZone().getZoneName();
        this.year = ssfc.getProperties().getId().getYear();
        this.month = ssfc.getMonth();
        this.srcId = ssfc.getProperties().getId().getSource().getId();
        this.srcName = ssfc.getProperties().getId().getSource().getName();
    }
}

