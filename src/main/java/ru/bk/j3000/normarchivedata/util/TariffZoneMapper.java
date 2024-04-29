package ru.bk.j3000.normarchivedata.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.bk.j3000.normarchivedata.model.TariffZone;
import ru.bk.j3000.normarchivedata.model.dto.TariffZoneDTO;
import ru.bk.j3000.normarchivedata.service.TariffZoneService;

@Component
@Slf4j
@RequiredArgsConstructor
public class TariffZoneMapper {
    private final TariffZoneService tzService;

    public TariffZone toTariffZone(TariffZoneDTO dto) {
        TariffZone tz = tzService.getTariffZoneById(dto.getZoneId());

        log.info("Tariff zone created from DTO (id {}).", tz.getId());

        return tz;
    }
}
