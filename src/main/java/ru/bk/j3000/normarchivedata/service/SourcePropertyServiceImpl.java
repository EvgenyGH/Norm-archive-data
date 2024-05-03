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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.bk.j3000.normarchivedata.exception.FileParseException;
import ru.bk.j3000.normarchivedata.exception.FileReadException;
import ru.bk.j3000.normarchivedata.model.SourceProperty;
import ru.bk.j3000.normarchivedata.model.SrcPropertyId;
import ru.bk.j3000.normarchivedata.model.dto.SourcePropertyDTO;
import ru.bk.j3000.normarchivedata.repository.SourcePropertyRepository;
import ru.bk.j3000.normarchivedata.util.SourcePropertiesMapper;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class SourcePropertyServiceImpl implements SourcePropertyService {
    private final SourcePropertyRepository srcPropRepository;
    private final SourcePropertiesMapper mapper;
    private final SourceService sourceService;
    private final TariffZoneService tariffZoneService;
    private final BranchService branchService;
    private final String[] srcPropTemplateColumns = {
            "ID источника",
            "ID Филиала",
            "ID тарифной зоны",
            "Комментарии"};

    @Override
    public List<SourcePropertyDTO> findAllPropDTOByYear(Integer reportYear) {
        List<SourcePropertyDTO> properties = srcPropRepository.findAllPropDTOByYear(reportYear);

        log.info("Found {} source properties (DTO) for {} year.", properties.size(), reportYear);

        return properties;
    }

    @Override
    public List<SourceProperty> findAllPropByYear(Integer reportYear) {
        List<SourceProperty> properties = srcPropRepository.findAllPropByYear(reportYear);

        log.info("Found {} source properties for {} year.", properties.size(), reportYear);

        return properties;
    }

    @Override
    @Transactional
    public void deleteSrcPropertyById(UUID srcId, Integer year) {
        srcPropRepository.deleteBySrcIdAndYear(srcId, year);

        log.info("Deleted source property. Source id {}, year {}.", srcId, year);
    }

    @Override
    public SourcePropertyDTO getSourcePropertyById(UUID srcId, Integer year) {
        SourcePropertyDTO property = srcPropRepository.getSourcePropertyDTOByIdAndYear(srcId, year);

        log.info("Found source property. Source name {}, source id {}, year {}.",
                property.getSource().getSourceName(),
                property.getSource().getSourceId(),
                year);

        return property;
    }

    @Override
    public void updateSourceProperty(SourcePropertyDTO sourcePropertyDTO) {
        SourceProperty property = mapper.toSourceProperty(sourcePropertyDTO);

        srcPropRepository.findById(property.getId())
                .ifPresentOrElse(p -> srcPropRepository.save(property),
                        () -> {
                            throw new EntityNotFoundException(String
                                    .format("Source property not found. Id %s", property.getId()));
                        });

        log.info("Source property for source {} id {} year {} updated.",
                property.getId().getSource().getName(),
                property.getId().getSource().getId(),
                property.getId().getYear());
    }

    @Override
    public void addSourceProperty(SourcePropertyDTO sourcePropertyDTO) {
        SourceProperty property = mapper.toSourceProperty(sourcePropertyDTO);

        srcPropRepository.findById(property.getId())
                .ifPresentOrElse(p -> {
                            throw new EntityExistsException(String
                                    .format("Source property already exists. Id %s", p.getId()));
                        },
                        () -> srcPropRepository.save(property));

        log.info("Source property already exists. ({}) Id {}.",
                property.getId().getSource().getName(),
                property.getId());
    }

    @Override
    public Resource getSourcePropertyTemplate() {
        Resource resource = new ClassPathResource("exceltemplates/sourcePropertiesTemplate.xlsm");

        log.info("Source properties template resource created.");

        return resource;
    }

    @Override
    @Transactional
    public void uploadSourceProperties(MultipartFile file, Integer year) {
        List<SourceProperty> properties = readSourcePropertiesFromFile(file, year);
        srcPropRepository.deleteAllByYear(year);
        srcPropRepository.saveAll(properties);

        log.info("Source properties loaded to database from file ({} in total).", properties.size());
    }

    private List<SourceProperty> readSourcePropertiesFromFile(MultipartFile file, Integer year) {
        List<SourceProperty> properties;

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheet("sourceProperties");

            checkHeaders(sheet.getRow(0));

            properties = IntStream.rangeClosed(1, sheet.getLastRowNum())
                    .mapToObj(sheet::getRow)
                    .filter(row -> row.getCell(0) != null
                            && !row.getCell(0).getStringCellValue().isBlank()
                            && row.getCell(1) != null
                            && row.getCell(1).getNumericCellValue() > 0
                            && row.getCell(2) != null
                            && row.getCell(2).getNumericCellValue() > 0)
                    .map(row -> new SourceProperty(new SrcPropertyId(
                            sourceService.getSourceById(
                                            UUID.fromString(row
                                                    .getCell(0)
                                                    .getStringCellValue()))
                                    .orElseThrow(),
                            year),
                            branchService.getBranchById(
                                    Double.valueOf(row
                                                    .getCell(1)
                                                    .getNumericCellValue())
                                            .intValue()),
                            tariffZoneService.getTariffZoneById(
                                    Double.valueOf(row
                                                    .getCell(2)
                                                    .getNumericCellValue())
                                            .intValue())))
                    .toList();

        } catch (IOException e) {
            throw new FileReadException("Error reading excel file.", "SourceProperties template.");
        }

        log.info("Source properties read from file.");

        return properties;
    }

    private void checkHeaders(Row row) {
        IntStream.rangeClosed(0, srcPropTemplateColumns.length - 1)
                .forEach(i -> {
                    if (!row.getCell(i).getStringCellValue().equals(srcPropTemplateColumns[i])) {
                        throw new FileParseException("Измененный шаблон.", "-", row.getRowNum());
                    }
                });

        log.info("Source properties template headers are OK.");
    }
}
