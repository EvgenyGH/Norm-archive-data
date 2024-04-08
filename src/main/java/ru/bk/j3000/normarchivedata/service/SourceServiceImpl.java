package ru.bk.j3000.normarchivedata.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import ru.bk.j3000.normarchivedata.exception.fileParseException;
import ru.bk.j3000.normarchivedata.model.SOURCE_TYPE;
import ru.bk.j3000.normarchivedata.model.Source;
import ru.bk.j3000.normarchivedata.repository.SourceRepository;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class SourceServiceImpl implements SourceService {
    private final String[] srcTemplateColumns = {"UUID", "Источник", "Адрес источника",
            "Тип источника", "Комментарии" };
    private final SourceRepository sourceRepository;

    @Transactional
    @Override
    public void saveSources(List<Source> sources) {
        sourceRepository.saveAll(sources);
        log.info("Saved {} sources.", sources.size());
    }

    @Override
    public void saveSource(Source source) {
        sourceRepository.save(source);
        log.info("Saved source {}.", source.getName());
    }

    @Override
    public void uploadSources(File file) {
        List<Source> sources = readSrcFromFile(file);
        saveSources(sources);

        log.info("Sources uploaded ({} in total).", sources.size());
    }

    @Override
    public List<Source> getAllSources() {
        List<Source> sources = sourceRepository.findAll();

        log.info("Sources read from database ({} in total)", sources.size());

        return sources;
    }

    private List<Source> readSrcFromFile(File file) {
        List<Source> sources;

        try (Workbook wb = new XSSFWorkbook(file)) {
            Sheet sheet = wb.getSheet("sources");
            checkHeader(sheet.getRow(0));

            sources = IntStream.rangeClosed(1, sheet.getLastRowNum())
                    .mapToObj(sheet::getRow)
                    .map(row -> new Source(null,
                            row.getCell(1).getStringCellValue().trim(),
                            row.getCell(2).getStringCellValue().trim(),
                            getSrcType(row)))
                    .toList();

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InvalidFormatException e) {
            throw new RuntimeException(e);
        }

        log.info("Sources read from file ({} in total).", sources.size());

        return sources;
    }

    private SOURCE_TYPE getSrcType(Row row) {
        String srcTypeStr = row.getCell(3).getStringCellValue().trim();

        SOURCE_TYPE srcType = switch (srcTypeStr) {
            case "РТС" -> SOURCE_TYPE.RTS;
            case "КТС" -> SOURCE_TYPE.KTS;
            case "МК" -> SOURCE_TYPE.MK;
            case "ПК" -> SOURCE_TYPE.PK;
            case "АИТ" -> SOURCE_TYPE.AIT;
            default -> throw new fileParseException(
                    String.format("Неверный тип источника (%s).", srcTypeStr),
                    row.getCell(1).getStringCellValue().trim(),
                    row.getRowNum());
        };

        log.debug("Source type read: {}. Source {}",
                srcType, row.getCell(1).getStringCellValue().trim());

        return srcType;
    }

    private void checkHeader(Row row) {
        if (IntStream.rangeClosed(0, row.getLastCellNum() - 1)
                .anyMatch(num -> !row.getCell(num).getStringCellValue().equals(srcTemplateColumns[num]))) {
            throw new fileParseException("Измененный шаблон.", "-", row.getRowNum());
        }

        log.debug("Headers OK!");
    }
}
