package ru.bk.j3000.normarchivedata.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.bk.j3000.normarchivedata.exception.FileParseException;
import ru.bk.j3000.normarchivedata.exception.FileReadException;
import ru.bk.j3000.normarchivedata.model.*;
import ru.bk.j3000.normarchivedata.model.dto.SsfcDTO;
import ru.bk.j3000.normarchivedata.repository.StandardSFCRepository;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.IntStream;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StandardSFCServiceImpl implements StandardSFCService {
    private final StandardSFCRepository ssfcRepository;
    private final SourceService sourceService;
    private final SourcePropertyService srcPropService;
    private final String[] ssfcsTemplateColumns = {"№ п/п",
            "Наименование источника тепловой энергии",
            "Вид топлива", "Наименование показателя",
            "Единицы измерения", "Id источника",
            "Id вида топлива", "Январь", "Февраль",
            "Март", "Апрель", "Май", "Июнь", "Июль",
            "Август", "Сентябрь", "Октябрь", "Ноябрь",
            "Декабрь", "2024 год", "Комментарии"};

    @Override
    public List<StandardSFC> findAllSsfcByYear(Integer reportYear) {
        List<StandardSFC> ssfcs = ssfcRepository.findAllSsfcByYear(reportYear);

        log.info("Found {} ssfcs for {} year.", ssfcs.size(), reportYear);

        return ssfcs;
    }

    @Override
    public StandardSFC getSsfcById(UUID id) {
        StandardSFC ssfc = ssfcRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Ssfc not found. Id=%s.", id)));

        log.info("Found ssfc with id {}.", id);

        return ssfc;
    }

    @Override
    public void updateSsfc(SsfcDTO ssfcDTO) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addSsfc(SsfcDTO ssfcDTO) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    @Transactional
    public void deleteSsfcById(UUID id) {
        StandardSFC ssfc = this.getSsfcById(id);
        ssfcRepository.deleteById(id);

        log.info("Ssfc id {} deleted. Name {}", id,
                ssfc.getProperties().getId().getSource().getName());
    }

    @Override
    @Transactional
    public void deleteSsfcByIds(List<UUID> ids) {
        ssfcRepository.deleteAllById(ids);

        log.info("Ssfcs deleted. {} in Total.", ids.size());
    }

    @Override
    @Transactional
    public void deleteSsfcBySrcIdAndYear(UUID srcId, Integer year) {
        ssfcRepository.deleteSsfcsBySrcIdAndYear(srcId, year);

        log.info("Ssfcs deleted for year {} and sourceId {}.", year, srcId);
    }

    @Override
    public Resource getSsfcTemplate() {
        Path path = Path.of("src/main/resources/exceltemplates/ssfcsTemplate.xlsm");
        try {
            Resource resource = new UrlResource(path.toUri());

            log.info("Ssfcs template resource created.");

            return resource;

        } catch (MalformedURLException e) {
            throw new FileReadException("Error reading template file.", "Ssfcs template.");
        }
    }

    @Override
    @Transactional
    public void uploadSsfc(MultipartFile file, Integer year) {
        List<StandardSFC> ssfcs = readSsfcsFromFile(file, year);
        ssfcRepository.deleteAllByYear(year);
        ssfcRepository.saveAll(ssfcs);

        log.info("Ssfcs loaded to database from file ({} in total).", ssfcs.size());
    }

    private List<StandardSFC> readSsfcsFromFile(MultipartFile file, Integer year) {
        List<StandardSFC> ssfcs = new LinkedList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheet("ssfcs");

            checkHeaders(sheet.getRow(0));

            for (int i = 1; i <= sheet.getLastRowNum(); i += 6) {
                Row row1 = sheet.getRow(i);
                Row row2 = sheet.getRow(i + 1);
                Row row4 = sheet.getRow(i + 3);
                Row row5 = sheet.getRow(i + 4);
                Row row6 = sheet.getRow(i + 5);

                String srcIdString = row1.getCell(5).getStringCellValue();

                if (Objects.isNull(srcIdString) || srcIdString.isBlank()) {
                    break;
                }

                UUID srcId = UUID.fromString(srcIdString);
                Source src = sourceService.getSourceById(srcId)
                        .orElseThrow(() -> new EntityNotFoundException(
                                String.format("Source id=%s not found.", srcId)));
                FUEL_TYPE fuelType = FUEL_TYPE.values()[(int) row1.getCell(6).getNumericCellValue()];
                SrcPropertyId srcPropId = new SrcPropertyId(src, year);
                SourceProperty sourceProperty = srcPropService.getSourcePropertyById(srcPropId);

                List<Double> generations = new ArrayList<>();
                List<Double> ownNeeds = new ArrayList<>();
                List<Double> productions = new ArrayList<>();
                List<Double> ssfcgs = new ArrayList<>();
                List<Double> ssfcz = new ArrayList<>();

                IntStream.rangeClosed(7, 18).forEach(j -> {
                    Double genCellValue = row1.getCell(j).getNumericCellValue();
                    generations.add(genCellValue);
                    Double ownNeedsCellValue = row2.getCell(j).getNumericCellValue();
                    ownNeeds.add(ownNeedsCellValue);
                    Double prodCellValue = row4.getCell(j).getNumericCellValue();
                    productions.add(prodCellValue);
                    Double ssfcgCellValue = row5.getCell(j).getNumericCellValue();
                    ssfcgs.add(ssfcgCellValue);
                    Double ssfcCellValue = row6.getCell(j).getNumericCellValue();
                    ssfcz.add(ssfcCellValue);
                });

                List<StandardSFC> standardSFCS = IntStream.rangeClosed(0, 11)
                        .mapToObj(k -> new StandardSFC(null,
                                generations.get(k),
                                ownNeeds.get(k),
                                productions.get(k),
                                ssfcz.get(k),
                                ssfcgs.get(k),
                                sourceProperty,
                                k + 1,
                                fuelType))
                        .toList();

                ssfcs.addAll(standardSFCS);
            }
        } catch (IOException e) {
            throw new FileReadException("Error reading excel file.", "Ssfcs template.");
        }

        log.info("Ssfcs read from file.");

        return ssfcs;
    }

    private void checkHeaders(Row row) {
        IntStream.rangeClosed(0, ssfcsTemplateColumns.length - 1)
                .forEach(i -> {
                    if (!row.getCell(i).getStringCellValue().equals(ssfcsTemplateColumns[i])) {
                        throw new FileParseException("Измененный шаблон.", "-", row.getRowNum());
                    }
                });

        log.info("Ssfcs template headers are OK.");
    }
}
