package ru.bk.j3000.normarchivedata.service;

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
import java.util.List;
import java.util.UUID;
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
    public void updateSsfc(SsfcDTO ssfcDTO) {

    }

    @Override
    public void addSsfc(SsfcDTO ssfcDTO) {

    }

    @Override
    public void deleteSsfcById(UUID id, Integer year) {

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
        List<StandardSFC> ssfcs;

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheet("ssfcs");

            checkHeaders(sheet.getRow(0));

            ssfcs = IntStream.iterate(1, i -> i + 7)
                    .limit((sheet.getLastRowNum() - 1) / 7)
                    .mapToObj(i -> {
                        var ref = new Object() {
                            Row row = sheet.getRow(i);
                        };

                        UUID srcId = UUID.fromString(ref.row.getCell(6).getStringCellValue());
                        Source src = sourceService.getSourceById(srcId).orElseThrow();

                        FUEL_TYPE fuelType = FUEL_TYPE.valueOf(ref.row.getCell(7).getStringCellValue());

                        List<Double> generations = IntStream.rangeClosed(8, 19)
                                .mapToObj(j -> ref.row.getCell(j).getNumericCellValue())
                                .toList();

                        ref.row = sheet.getRow(i + 1);
                        List<Double> ownNeeds = IntStream.rangeClosed(6, 19)
                                .mapToObj(j -> ref.row.getCell(j).getNumericCellValue())
                                .toList();

                        ref.row = sheet.getRow(i + 3);
                        List<Double> productions = IntStream.rangeClosed(6, 19)
                                .mapToObj(j -> ref.row.getCell(j).getNumericCellValue())
                                .toList();

                        ref.row = sheet.getRow(i + 4);
                        List<Double> ssfcgs = IntStream.rangeClosed(6, 19)
                                .mapToObj(j -> ref.row.getCell(j).getNumericCellValue())
                                .toList();

                        ref.row = sheet.getRow(i + 5);
                        List<Double> ssfcz = IntStream.rangeClosed(6, 19)
                                .mapToObj(j -> ref.row.getCell(j).getNumericCellValue())
                                .toList();

                        SrcPropertyId srcPropId = new SrcPropertyId(src, year);
                        SourceProperty sourceProperty = srcPropService.getSourcePropertyById(srcPropId);

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

                        return standardSFCS;
                    })
                    .flatMap(List::stream)
                    .toList();

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
