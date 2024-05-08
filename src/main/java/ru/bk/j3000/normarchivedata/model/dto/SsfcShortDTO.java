package ru.bk.j3000.normarchivedata.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bk.j3000.normarchivedata.model.StandardSFC;

import java.util.UUID;

@Data
@NoArgsConstructor
public class SsfcShortDTO {
    private UUID id;
    private Integer year;
    private Integer month;
    private Double generation;
    private Double ownNeeds;
    private Double production;
    private Double ssfc;
    private Double ssfcg;

    public SsfcShortDTO(StandardSFC ssfc) {
        this.id = ssfc.getId();
        this.year = ssfc.getProperties().getId().getYear();
        this.month = ssfc.getMonth();
        this.generation = ssfc.getGeneration();
        this.ownNeeds = ssfc.getOwnNeeds();
        this.production = ssfc.getProduction();
        this.ssfc = ssfc.getSsfc();
        this.ssfcg = ssfc.getSsfcg();
    }
}

