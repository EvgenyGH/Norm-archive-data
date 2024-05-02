package ru.bk.j3000.normarchivedata.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.bk.j3000.normarchivedata.model.TariffZone;
import ru.bk.j3000.normarchivedata.model.dto.TariffZoneDTO;

@Component
@Slf4j
@RequiredArgsConstructor
public class TariffZoneMapper {
    public TariffZone toTariffZone(TariffZoneDTO dto) {
        TariffZone tz = new TariffZone(dto.getZoneId(), dto.getZoneName());

        log.info("Tariff zone created from DTO (id {}).", tz.getId());

        return tz;
    }
}
