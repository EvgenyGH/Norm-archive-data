package ru.bk.j3000.normarchivedata.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.bk.j3000.normarchivedata.exception.FileParseException;
import ru.bk.j3000.normarchivedata.exception.FileReadException;
import ru.bk.j3000.normarchivedata.model.TariffZone;
import ru.bk.j3000.normarchivedata.model.dto.TariffZoneDTO;
import ru.bk.j3000.normarchivedata.repository.TariffZoneRepository;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class TariffZoneServiceImpl implements TariffZoneService {
    private final String[] tzTemplateColumns = {"ID", "Название тарифной зоны", "Комментарии"};

    private final TariffZoneRepository tariffZoneRepository;

    @Override
    public void saveTariffZone(TariffZone tariffZone) {
        tariffZoneRepository.findById(tariffZone.getId())
                .ifPresentOrElse(
                        (tz) -> {
                            throw new EntityExistsException(String
                                    .format("Tariff zone id %s already exists.", tariffZone.getId()));
                        },
                        () -> tariffZoneRepository.save(tariffZone));

        log.info("Tariff zone saved ({}).", tariffZone);
    }

    @Override
    public void updateTariffZone(TariffZone tariffZone) {
        tariffZoneRepository.findById(tariffZone.getId())
                .ifPresentOrElse(tz -> tariffZoneRepository.save(tariffZone),
                        () -> {
                            throw new EntityNotFoundException(String
                                    .format("Tariff zone id %s not found.", tariffZone.getId()));
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
            var resource = new ClassPathResource("exceltemplates/tariffZonesTemplate.xlsm");

            log.info("TariffZones template resource created.");

            return resource;
        } catch (Exception e) {
            throw new FileReadException("Error reading template file.", "TariffZones template.");
        }
    }

    @Override
    @Transactional
    public void uploadTariffZones(MultipartFile file) {
        List<TariffZone> tariffZones = readTariffZonesFromFile(file);

        tariffZoneRepository.deleteAll();
        tariffZoneRepository.saveAll(tariffZones);

        log.info("Tariff zones loaded to database from file ({} in total).", tariffZones.size());
    }

    private List<TariffZone> readTariffZonesFromFile(MultipartFile file) {
        List<TariffZone> tariffZones;

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheet("tariffZones");

            checkHeaders(sheet.getRow(0));

            tariffZones = IntStream.rangeClosed(1, sheet.getLastRowNum())
                    .mapToObj(sheet::getRow)
                    .filter(row -> row.getCell(0) != null
                            && row.getCell(0).getNumericCellValue() > 0
                            && row.getCell(1) != null
                            && !row.getCell(1).getStringCellValue().isBlank())
                    .map(row -> new TariffZone(
                            (int) row.getCell(0).getNumericCellValue(),
                            row.getCell(1).getStringCellValue()))
                    .toList();

        } catch (IOException e) {
            throw new FileReadException("Error reading excel file.", "TariffZones template.");
        }

        log.info("Tariff zones read from file.");

        return tariffZones;
    }

    private void checkHeaders(Row row) {
        IntStream.rangeClosed(0, tzTemplateColumns.length - 1)
                .forEach(i -> {
                    if (!row.getCell(i).getStringCellValue().equals(tzTemplateColumns[i])) {
                        throw new FileParseException("Измененный шаблон.", "-", row.getRowNum());
                    }
                });

        log.info("Tariff zones template headers are OK.");
    }

    @Override
    public List<TariffZoneDTO> getAllTariffZonesDTO() {
        List<TariffZoneDTO> tariffZones = tariffZoneRepository.findAllDTO();

        log.info("All tariff zones DTO retrieved from database({} in total).", tariffZones.size());

        return tariffZones;
    }
}