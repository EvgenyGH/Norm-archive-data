package ru.bk.j3000.normarchivedata.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import ru.bk.j3000.normarchivedata.model.TariffZone;

import java.util.List;

public interface TariffZoneService {
    void saveTariffZone(TariffZone tariffZone);

    void updateTariffZone(TariffZone tariffZone);

    void deleteTariffZone(Integer id);

    List<TariffZone> getAllTariffZones();

    TariffZone getTariffZoneById(Integer id);

    Resource getTemplate();

    void uploadTariffZones(MultipartFile file);
}
