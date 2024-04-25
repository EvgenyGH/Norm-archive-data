package ru.bk.j3000.normarchivedata.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.bk.j3000.normarchivedata.model.TariffZone;
import ru.bk.j3000.normarchivedata.repository.TariffZoneRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TariffZoneServiceImpl implements TariffZoneService {
    private final TariffZoneRepository tariffZoneRepository;

    @Override
    public void saveTariffZone(TariffZone tariffZone) {
        tariffZoneRepository.save(tariffZone);
    }

    @Override
    public void updateTariffZone(TariffZone tariffZone) {
        tariffZoneRepository. (tariffZone.getId())

        tariffZoneRepository.save(tariffZone);

        log.info("TariffZone id {} updated ({}).", tariffZone.getId(), tariffZone);
    }

    @Override
    public void deleteTariffZone(Integer id) {
        tariffZoneRepository.deleteById(id);

        log.info("Tariff zone id {} deleted from database.", id);
    }

    @Override
    public List<TariffZone> getAllTariffZones() {
        List<TariffZone> tariffZones = tariffZoneRepository.findAll(Sort.by("id"));

        log.info("All tariff zones retrieved from database({} in total).", tariffZones.size());

        return tariffZones;
    }

    @Override
    public TariffZone getTariffZoneById(Integer id) {
        TariffZone tariffZone = tariffZoneRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Tariff zone not found. Id " + id));

        log.info("Tariff zone id {} retrieved from database", id);

        return tariffZone;
    }
}
