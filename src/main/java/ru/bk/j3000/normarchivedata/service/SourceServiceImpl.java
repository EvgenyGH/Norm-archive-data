package ru.bk.j3000.normarchivedata.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import ru.bk.j3000.normarchivedata.model.SOURCE_TYPE;
import ru.bk.j3000.normarchivedata.model.Source;
import ru.bk.j3000.normarchivedata.repository.SourceRepository;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SourceServiceImpl implements SourceService {

    private final SourceRepository sourceRepository;

    @Transactional
    @Override
    public void saveSources(List<Source> sources) {
        sourceRepository.saveAll(sources);
    }

    @Override
    public void saveSource(Source source) {
        sourceRepository.save(source);
    }

    @Override
    public void uploadSources(File file) {
        List<Source> sources = readSrcFromFile(file);
        saveSources(sources);
    }

    private List<Source> readSrcFromFile(File file) {
        List<Source> sources;

        try (Workbook wb = new XSSFWorkbook(file)) {
            Sheet sheet = wb.getSheet("sources");
            checkHeader(sheet.getRow(0));
            sources = new ArrayList<>(sheet.getLastRowNum());

            for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);
                sources.add(new Source(null,
                        row.getCell(1).getStringCellValue().trim(),
                        row.getCell(2).getStringCellValue().trim(),
                        getSrcType(row.getCell(3).getStringCellValue().trim())));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InvalidFormatException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new ParseException();
        }

        return sources;
    }

    private SOURCE_TYPE getSrcType(String srcType) throws ParseException {
        return switch (srcType) {
            case "РТС" -> SOURCE_TYPE.RTS;
            case "КТС" -> SOURCE_TYPE.KTS;
            case "МК" -> SOURCE_TYPE.MK;
            case "ПК" -> SOURCE_TYPE.PK;
            case "АИТ" -> SOURCE_TYPE.AIT;
            default -> throw new ParseException("Invalid source type", 0);
        };
    }

    private void checkHeader(Row row) {

    }

    @Override
    public File saveSrcToFile(List<Source> sources) {
        return null;
    }
}
