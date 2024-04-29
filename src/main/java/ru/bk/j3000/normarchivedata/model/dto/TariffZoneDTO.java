package ru.bk.j3000.normarchivedata.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bk.j3000.normarchivedata.model.TariffZone;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TariffZoneDTO {
    private Integer zoneId;
    private String zoneName;

    public TariffZoneDTO(TariffZone tariffZone) {
        this.zoneId = tariffZone.getId();
        this.zoneName = tariffZone.getZoneName();
    }
}
