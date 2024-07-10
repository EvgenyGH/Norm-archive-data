package ru.bk.j3000.normarchivedata.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.bk.j3000.normarchivedata.exception.FileParseException;
import ru.bk.j3000.normarchivedata.exception.FileReadException;
import ru.bk.j3000.normarchivedata.exception.SsfcDataNotValidException;
import ru.bk.j3000.normarchivedata.model.*;
import ru.bk.j3000.normarchivedata.model.dto.SsfcsDTO;
import ru.bk.j3000.normarchivedata.repository.StandardSFCRepository;

import java.io.IOException;
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
            "Декабрь", "Год", "Комментарии"};

    private final String[] months = {"Январь", "Февраль", "Март",
            "Апрель", "Май", "Июнь", "Июль", "Август",
            "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};

    @Override
    public List<StandardSFC> findAllSsfcByYear(Integer year) {
        List<StandardSFC> ssfcs = ssfcRepository.findAllSsfcByYear(year);

        log.info("Found {} ssfcs for {} year.", ssfcs.size(), year);

        return ssfcs;
    }

    @Override
    public List<StandardSFC> findAllSsfcByYearAndSrcId(Integer year, UUID srcId) {
        List<StandardSFC> ssfcs = ssfcRepository.findAllSsfcByYearAndSrcId(year, srcId);

        log.info("Found {} ssfcs for {} year and source id {}.",
                ssfcs.size(), year, srcId.toString());

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
    public void updateSsfc(SsfcsDTO ssfcsDTO, Integer year, String originalFuelType) {
        List<StandardSFC> ssfcs = this.toStandartSFCs(ssfcsDTO);

        if (!originalFuelType.equals(ssfcsDTO.getFuelType())) {
            checkSsfcExists(ssfcsDTO.getSrcId(),
                    FUEL_TYPE.getByName(ssfcsDTO.getFuelType()), year);
        }

        ssfcRepository.saveAll(ssfcs);

        log.info("Updated {} ssfcs for source {} in {} year.",
                ssfcs.size(),
                ssfcs.getFirst().getProperties().getId().getSource().getName()
                , year);
    }

    @Override
    public List<StandardSFC> toStandartSFCs(SsfcsDTO ssfcsDTO) {
        List<StandardSFC> ssfcs = ssfcsDTO.getSsfcs().stream().map(dto -> new StandardSFC(dto.getId(),
                        dto.getGeneration(),
                        dto.getOwnNeeds(),
                        dto.getProduction(),
                        dto.getSsfc(),
                        dto.getSsfcg(),
                        srcPropService.getSourcePropertyById(
                                new SrcPropertyId(sourceService.getSourceById(ssfcsDTO.getSrcId())
                                        .orElseThrow(() -> new EntityNotFoundException(String
                                                .format("Source id %s not found.", ssfcsDTO.getSrcId())))
                                        , dto.getYear())),
                        dto.getMonth(),
                        FUEL_TYPE.getByName(ssfcsDTO.getFuelType())))
                .toList();

        log.info("List of StandardSFCs for source {} and year {} converted from DTO",
                ssfcs.getFirst().getProperties().getId().getSource().getName(),
                ssfcs.getFirst().getProperties().getId().getYear());

        return ssfcs;
    }

    @Override
    public void addSsfc(SsfcsDTO ssfcsDTO, Integer year) {
        checkSsfcExists(ssfcsDTO.getSrcId(),
                FUEL_TYPE.getByName(ssfcsDTO.getFuelType()), year);

        List<StandardSFC> ssfcs = this.toStandartSFCs(ssfcsDTO);

        ssfcRepository.saveAll(ssfcs);

        log.info("Added {} ssfcs for source id {} in {} year.",
                ssfcs.size(),
                ssfcs.getFirst().getProperties().getId().getSource().getId()
                , year);
    }

    private void checkSsfcExists(UUID srcId, FUEL_TYPE fuelType, Integer year) {
        List<StandardSFC> ssfcs = ssfcRepository.findAllBySrcIdAndFuelTypeAndYear(srcId, fuelType, year);

        if (!ssfcs.isEmpty()) {
            throw new EntityExistsException(String
                    .format("Ssfc for source %s (%s) fuel type %s for %s year already exists.",
                            ssfcs.getFirst().getProperties().getId().getSource().getName(),
                            ssfcs.getFirst().getProperties().getId().getSource().getId(),
                            ssfcs.getFirst().getFuelType().getName(), year));
        }

        log.info("Ssfcs for source id {} fuel type {} year {} not found. Check OK.",
                srcId, fuelType.name(), year);
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
    public void deleteSsfcBySrcIdAndYearAndFuelType(UUID srcId, Integer year, FUEL_TYPE fuelType) {
        ssfcRepository.deleteSsfcsBySrcIdAndYearAndFuelType(srcId, year, fuelType);

        log.info("Ssfcs deleted for year {} and sourceId {}.and fuel type {}",
                year, srcId, fuelType.getName());
    }

    @Override
    public Resource getSsfcTemplate() {
        Resource resource = new ClassPathResource("exceltemplates/ssfcsTemplate.xlsm");

        log.info("Ssfcs template resource created.");

        return resource;
    }

    @Override
    @Transactional
    public List<String> uploadSsfc(MultipartFile file, Integer year) {
        List<StandardSFC> ssfcs = readSsfcsFromFile(file, year);
        List<String> warns = checkSsfcsConsistents(ssfcs);
        List<String> errors = checkSsfcsConstrains(ssfcs);
        ssfcRepository.deleteAllByYear(year);
        ssfcRepository.saveAll(ssfcs);

        if (!errors.isEmpty()) {
            throw new SsfcDataNotValidException("Data constrains violation", "Uploaded file", errors);
        }

        log.info("Ssfcs loaded to database from file ({} in total).", ssfcs.size());

        return warns;
    }

    private List<String> checkSsfcsConstrains(List<StandardSFC> ssfcs) {
        List<String> errors = new LinkedList<>();

        ssfcs = ssfcs.stream()
                .sorted(Comparator.comparing(s -> s.getProperties().getId().getSource().getName()))
                .sorted(Comparator.comparing(StandardSFC::getMonth))
                .toList();

        try (ValidatorFactory vf = Validation.buildDefaultValidatorFactory()) {
            Validator validator = vf.getValidator();

            for (StandardSFC ssfc : ssfcs) {
                Set<ConstraintViolation<StandardSFC>> violations = validator.validate(ssfc);
                if (!violations.isEmpty()) {
                    violations.forEach(violation ->
                            errors.add(String.format("%s (%s). Недопустимое значение %.6f.",
                                    ssfc.getProperties().getId().getSource().getName(),
                                    months[ssfc.getMonth() - 1],
                                    (double) violation.getInvalidValue())
                            )
                    );
                }
            }
        }

        return errors;
    }

    private List<String> checkSsfcsConsistents(List<StandardSFC> ssfcs) {
        List<String> warns = new LinkedList<>();
        ssfcs = ssfcs.stream()
                .sorted(Comparator.comparing(s -> s.getProperties().getId().getSource().getName()))
                .sorted(Comparator.comparing(StandardSFC::getMonth))
                .toList();

        for (StandardSFC stSsfc : ssfcs) {
            double generation = stSsfc.getGeneration();
            double production = stSsfc.getProduction();
            double ownNeeds = stSsfc.getOwnNeeds();
            double ssfcg = stSsfc.getSsfcg();
            double ssfc = stSsfc.getSsfc();
            double balance = Math.abs(generation - production - ownNeeds);

            if (balance > 0.000001) {
                warns.add(String.format("%s (%s). Небаланс %.6f.",
                        stSsfc.getProperties().getId().getSource().getName(),
                        months[stSsfc.getMonth() - 1],
                        balance));
            }

            if (generation > 0 && ssfc < 142.86) {
                warns.add(String.format("%s (%s). %s",
                        stSsfc.getProperties().getId().getSource().getName(),
                        months[stSsfc.getMonth() - 1],
                        "УРУТ на отпуск т/э < 142.86 кг.у.т./Гкал."));
            }

            if (generation > 0 && ssfcg < 142.86) {
                warns.add(String.format("%s (%s). %s",
                        stSsfc.getProperties().getId().getSource().getName(),
                        months[stSsfc.getMonth() - 1],
                        "УРУТ на выработку т/э < 142.86 кг.у.т./Гкал."));
            }

            if (generation <= 0 && (production > 0 || ownNeeds > 0 || ssfcg > 0 || ssfc > 0)) {
                warns.add(String.format("%s (%s). %s",
                        stSsfc.getProperties().getId().getSource().getName(),
                        months[stSsfc.getMonth() - 1],
                        "При нулевой выработке т/э заведены ненулевые показатели."));
            }
        }

        log.info("Formed {} warns", warns.size());

        return warns;
    }

    private List<StandardSFC> readSsfcsFromFile(MultipartFile file, Integer year) {
        List<StandardSFC> ssfcs = new LinkedList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheet("ssfcs");

            checkHeaders(sheet.getRow(0));

            for (int i = 1; i < sheet.getLastRowNum(); i += 6) {
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

    @Override
    public List<Source> findAllDefinedSourcesByYear(Integer year) {
        List<Source> sources = ssfcRepository.findAllDefinedSourcesByYear(year);

        log.info("All sources with defined ssfc for {} year found. {} in total.",
                year, sources.size());

        return sources;
    }

    @Override
    public List<StandardSFC> findAllSsfcByYearAndSrcIdAndFuelType(Integer year, UUID srcId, FUEL_TYPE fuelType) {
        List<StandardSFC> ssfcs = ssfcRepository.findAllBySrcIdAndFuelTypeAndYear(srcId, fuelType, year);

        log.info("Found {} ssfcs for {} year and fuel type {}.", ssfcs.size(), year, fuelType.getName());

        return ssfcs;
    }

    @Override
    public List<StandardSFC> findAllSsfcByYearAndSrcIds(Integer year, List<UUID> srcIds) {
        List<StandardSFC> ssfcs = ssfcRepository.findAllSsfcByYearAndSrcIds(year, srcIds);

        log.info("Found {} ssfcs for {} year and {} sources.", ssfcs.size(), year, srcIds.size());

        return ssfcs;
    }

    @Override
    public List<StandardSFC> findAllSsfcByPeriods(Map<Integer, List<Integer>> periods) {
        return ssfcRepository.findAllSsfcByPeriods(periods, List.of());
    }

    @Override
    public List<StandardSFC> findAllSsfcByPeriodsAndSrcIds(Map<Integer, List<Integer>> periods, List<UUID> srcIds) {
        List<StandardSFC> ssfcs = ssfcRepository.findAllSsfcByPeriods(periods, srcIds);

        log.info("Found {} ssfcs for {} period(s) and {} source(s).", ssfcs.size(),
                periods.size(), srcIds.size());

        return ssfcs;
    }
}
