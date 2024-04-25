package ru.bk.j3000.normarchivedata.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.bk.j3000.normarchivedata.exception.FileReadException;
import ru.bk.j3000.normarchivedata.model.TariffZone;
import ru.bk.j3000.normarchivedata.repository.TariffZoneRepository;

import java.net.MalformedURLException;
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
        tariffZoneRepository.findById(tariffZone.getId())
                .ifPresentOrElse(tz -> tariffZoneRepository.save(tariffZone),
                        () -> {
                            throw new EntityExistsException(String
                                    .format("Tariff zone id %s already exists.", tariffZone.getId()));
                        });

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

    @Override
    public Resource getTemplate() {
        try {
            var resource = new FileUrlResource("/exceltemplates/tariffZoneTemplate.xlsm");

            log.info("TariffZones template resource created.");

            return resource;
        } catch (MalformedURLException e) {
            throw new FileReadException("Error reading template file.", "TariffZones template.");
        }
    }

    @Override
    public void uploadTariffZones(MultipartFile file) {
        log.warn("----> Got it! In progress");
    }
}