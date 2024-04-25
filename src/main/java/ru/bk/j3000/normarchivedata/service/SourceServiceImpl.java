package ru.bk.j3000.normarchivedata.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.bk.j3000.normarchivedata.exception.FileParseException;
import ru.bk.j3000.normarchivedata.exception.FileReadException;
import ru.bk.j3000.normarchivedata.model.SOURCE_TYPE;
import ru.bk.j3000.normarchivedata.model.Source;
import ru.bk.j3000.normarchivedata.repository.SourceRepository;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class SourceServiceImpl implements SourceService {
    private final String[] srcTemplateColumns = {"UUID", "Источник", "Адрес источника",
            "Тип источника", "Комментарии"};
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
    public void uploadSources(MultipartFile file) {
        List<Source> sources = readSrcFromFile(file);
        deleteAllSources();
        saveSources(sources);

        log.info("Sources uploaded ({} in total).", sources.size());
    }

    @Override
    public void deleteAllSources() {
        sourceRepository.deleteAll();

        log.info("All sources deleted.");
    }

    @Override
    public void deleteSourcesById(List<UUID> ids) {
        sourceRepository.deleteAllById(ids);

        log.info("Deleted {} sources.", ids.size());
    }

    @Override
    public void updateSource(Source source) {
        sourceRepository.findById(source.getId())
                .ifPresentOrElse(src -> sourceRepository.save(source),
                        () -> {
                            throw new EntityNotFoundException(String
                                    .format("Source id %s not exists.", source.getId()));
                        });

        log.info("Source id {} updated ({}).", source.getId(), source);
    }

    @Override
    public List<Source> getAllSources() {
        List<Source> sources = sourceRepository.findAll()
                .stream()
                .sorted(Comparator.comparingInt(s -> s.getSourceType().ordinal()))
                .toList();

        log.info("Sources read from database ({} in total)", sources.size());

        return sources;
    }

    private List<Source> readSrcFromFile(MultipartFile file) {
        List<Source> sources;

        try (Workbook wb = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = wb.getSheet("sources");
            checkHeader(sheet.getRow(0));

            sources = IntStream.rangeClosed(1, sheet.getLastRowNum())
                    .mapToObj(sheet::getRow)
                    .map(row -> new Source(null,
                            row.getCell(1).getStringCellValue()
                                    .trim().replaceAll("[«»]", "\""),
                            row.getCell(2).getStringCellValue()
                                    .trim().replaceAll("[«»]", "\""),
                            getSrcType(row)))
                    .toList();

        } catch (IOException e) {
            throw new FileReadException(e.getMessage(), "Uploaded file sources");
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
            default -> throw new FileParseException(
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
            throw new FileParseException("Измененный шаблон.", "-", row.getRowNum());
        }

        log.debug("Headers OK!");
    }

    @Override
    public Optional<Source> getSourceById(UUID id) {
        Optional<Source> source = sourceRepository.findById(id);

        log.info("Source id {} requested from database. Result {}. ", id, source.isPresent());

        return source;
    }

    @Override
    public Resource getTemplate() {
        Path path = Path.of("src/main/resources/exceltemplates/sourcesTemplate.xlsm");
        try {
            Resource resource = new UrlResource(path.toUri());

            log.info("Sources template resource created.");

            return resource;
        } catch (MalformedURLException e) {
            throw new FileReadException("Error reading template file.", "Sources template.");
        }
    }
}
