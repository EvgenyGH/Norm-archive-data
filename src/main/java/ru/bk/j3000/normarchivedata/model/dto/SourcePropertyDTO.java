package ru.bk.j3000.normarchivedata.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bk.j3000.normarchivedata.model.SourceProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SourcePropertyDTO {
    private SourceAlterDTO source;
    private Integer year;
    private BranchDTO branch;
    private TariffZoneDTO tariffZone;

    public SourcePropertyDTO(SourceProperty sourceProperty) {
        this.source = new SourceAlterDTO(sourceProperty.getId().getSource());
        this.year = sourceProperty.getId().getYear();
        this.branch = new BranchDTO(sourceProperty.getBranch());
        this.tariffZone = new TariffZoneDTO(sourceProperty.getTariffZone());
    }
}
