package ru.bk.j3000.normarchivedata.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import ru.bk.j3000.normarchivedata.exception.ReportIOException;
import ru.bk.j3000.normarchivedata.model.*;
import ru.bk.j3000.normarchivedata.model.dto.SsfcShortDTO;
import ru.bk.j3000.normarchivedata.model.dto.SsfcsDTO;
import ru.bk.j3000.normarchivedata.model.dto.ssfcsum.SsfcSumTZBranch;
import ru.bk.j3000.normarchivedata.model.dto.ssfcsum.SsfcSummary;
import ru.bk.j3000.normarchivedata.util.Counter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.apache.poi.ss.util.CellUtil.createCell;


@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ReportServiceImpl implements ReportService {
    private final String[] srcTemplateColumns = {"UUID", "Источник", "Адрес источника",
            "Тип источника", "Комментарии"};
    private final String[] allSrcColumns = {"№ п/п", "Тип источника", "Источник", "Адрес источника"};

    private final String[] brTemplateColumns = {"ID", "Название филиала", "Комментарии"};
    private final String[] allBranchesColumns = {"№ п./п.", "Название филиала"};

    private final String[] tzTemplateColumns = {"ID", "Название тарифной зоны", "Комментарии"};
    private final String[] allTariffZonesColumns = {"№ п./п.", "Название тарифной зоны"};

    private final String[] srcPropTemplateColumns = {"ID источника", "ID Филиала",
            "ID тарифной зоны", "Комментарии"};
    private final String[] allSrcPropColumns = {"№ п./п.", "Источник", "Тарифная зона", "Филиал"};

    private final String[] ssfcsTemplateColumns = {"№ п/п",
            "Наименование источника тепловой энергии",
            "Вид топлива", "Наименование показателя",
            "Единицы измерения", "Id источника",
            "Id вида топлива", "Январь", "Февраль",
            "Март", "Апрель", "Май", "Июнь", "Июль",
            "Август", "Сентябрь", "Октябрь", "Ноябрь",
            "Декабрь", "2024 год", "Комментарии"};
    private final String[] allSsfcsColumns = {"№ п/п",
            "Наименование источника тепловой энергии",
            "Вид топлива", "Наименование показателя",
            "Единицы измерения", "2024 год",
            "Январь", "Февраль", "Март", "Апрель",
            "Май", "Июнь", "Июль", "Август", "Сентябрь",
            "Октябрь", "Ноябрь", "Декабрь"};

    private final String[] ssfcRows = {"Выработка тепловой энергии",
            "Тепловая энергия на собственные нужды",
            "Тепловая энергия на собственные нужды",
            "Отпуск тепловой энергии с коллекторов источников",
            "УРУТ на выработу тепловой энергии",
            "УРУТ на отпуск тепловой энергии"};

    private final String[] ssfcUnits = {"тыс. Гкал", "тыс. Гкал", "%", "тыс. Гкал", "кг у.т./Гкал", "кг у.т./Гкал"};

    // services
    private final SourceService sourceService;
    private final BranchService branchService;
    private final TariffZoneService tzService;
    private final SourcePropertyService srcPropService;
    private final StandardSFCService ssfcService;


    @Override
    public Resource getSourcesReport(String type) {
        Resource resource = switch (type) {
            case "template" -> getSrcTemplateReport();
            case "standard" -> getAllSourcesReport();
            default -> throw new InvalidParameterException(String.format("Invalid source report type <%s>", type));
        };

        log.info("Source report formed. Type {}", type);

        return resource;
    }

    @Override
    public Resource getBranchesReport(String type) {
        Resource resource = switch (type) {
            case "template" -> getBranchTemplateReport();
            case "standard" -> getAllBranchesReport();
            default -> throw new InvalidParameterException(String.format("Invalid branch report type <%s>", type));
        };

        log.info("Branch report formed. Type {}", type);

        return resource;
    }

    @Override
    public Resource getTariffZonesReport(String type) {
        Resource resource = switch (type) {
            case "template" -> getTZTemplateReport();
            case "standard" -> getAllTZReport();
            default -> throw new InvalidParameterException(String.format("Invalid tariff zone report type <%s>",
                    type));
        };

        log.info("Tariff zone report formed. Type {}", type);

        return resource;
    }

    @Override
    public Resource getSrcPropertyReport(String type, Integer year) {
        Resource resource = switch (type) {
            case "template" -> getSrcPropTemplateReport(year);
            case "standard" -> getAllSrcPropReport(year);
            default -> throw new InvalidParameterException(String.format("Invalid source property report type <%s>",
                    type));
        };

        log.info("Source property report formed. Type {}", type);

        return resource;
    }

    @Override
    public Resource getSsfcsReport(String type, String selection, Integer year, List<UUID> srcIds) {
        Resource resource = switch (type) {
            case "template" -> getSsfcTemplateReport(year, selection, srcIds);
            case "standard" -> getAllSsfcReport(year, selection, srcIds);
            default -> throw new InvalidParameterException(String.format("Invalid ssfc report type <%s>",
                    type));
        };

        log.info("Ssfc report formed. Type {}", type);

        return resource;
    }

    @Override
    public Resource getSrcTemplateReport() {
        Resource resource;

        List<Source> sources = sourceService.getAllSources().stream()
                .sorted(Comparator.comparing(Source::getName))
                .sorted(Comparator.comparing(Source::getSourceType))
                .toList();

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Sources");
            Font fontHeader = wb.createFont();
            Font fontData = wb.createFont();

            CellStyle headerStylePrimary = getPrimaryHeaderStyle(wb.createCellStyle(), fontHeader);
            CellStyle headerStyleSecondary = getSecondaryHeaderStyle(wb.createCellStyle(), fontHeader);
            CellStyle stringStyle = getStringStyle(wb.createCellStyle(), fontData);

            // set headers
            Row headerRow = sheet.createRow(0);
            IntStream.rangeClosed(0, srcTemplateColumns.length - 1)
                    .forEach(i -> createCell(headerRow, i, srcTemplateColumns[i],
                            i == 0 || i == 4 ? headerStyleSecondary : headerStylePrimary));

            // set data
            for (int i = 0; i < sources.size(); i++) {
                Row row = sheet.createRow(i + 1);
                Source source = sources.get(i);

                createCell(row, 0, source.getId().toString(), stringStyle);
                createCell(row, 1, source.getName(), stringStyle);
                createCell(row, 2, source.getAddress(), stringStyle);
                createCell(row, 3, source.getSourceType().getName(), stringStyle);
                createCell(row, 4, "-", stringStyle);
            }

            // autosize columns
            IntStream.rangeClosed(0, srcTemplateColumns.length - 1)
                    .forEach(sheet::autoSizeColumn);

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            wb.write(out);

            resource = new ByteArrayResource(out.toByteArray());

        } catch (IOException e) {
            throw new ReportIOException("Ошибка формирования отчета",
                    "Источники в формате шаблона");
        }

        return resource;
    }

    @Override
    public Resource getAllSourcesReport() {
        Resource resource;

        List<Source> sources = sourceService.getAllSources().stream()
                .sorted(Comparator.comparing(Source::getName))
                .sorted(Comparator.comparing(Source::getSourceType))
                .toList();

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Sources");
            Font fontHeader = wb.createFont();
            Font fontData = wb.createFont();

            CellStyle headerStylePrimary = getPrimaryHeaderStyle(wb.createCellStyle(), fontHeader);
            CellStyle stringStyle = getStringStyle(wb.createCellStyle(), fontData);
            CellStyle integerStyle = getIntegerStyle(wb.createCellStyle(), wb.createDataFormat(), fontData);

            // set headers
            Row headerRow = sheet.createRow(0);
            IntStream.rangeClosed(0, allSrcColumns.length - 1)
                    .forEach(i -> createCell(headerRow, i, allSrcColumns[i], headerStylePrimary));

            // set data
            for (int i = 0; i < sources.size(); i++) {
                Row row = sheet.createRow(i + 1);
                Source source = sources.get(i);

                Cell cell = row.createCell(0);
                cell.setCellValue(i + 1);
                cell.setCellStyle(integerStyle);
                createCell(row, 1, source.getSourceType().getName(), stringStyle);
                createCell(row, 2, source.getName(), stringStyle);
                createCell(row, 3, source.getAddress(), stringStyle);
            }

            // autosize columns
            IntStream.rangeClosed(0, allSrcColumns.length - 1)
                    .forEach(sheet::autoSizeColumn);

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            wb.write(out);

            resource = new ByteArrayResource(out.toByteArray());

        } catch (
                IOException e) {
            throw new ReportIOException("Ошибка формирования отчета",
                    "Источники в стандартном формате");
        }

        log.info("Sources report type standard created.");

        return resource;
    }

    @Override
    public Resource getBranchTemplateReport() {
        Resource resource;

        List<Branch> branches = branchService.getAllBranches().stream()
                .sorted(Comparator.comparing(Branch::getId))
                .toList();

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Branches");
            Font fontHeader = wb.createFont();
            Font fontData = wb.createFont();

            CellStyle headerStylePrimary = getPrimaryHeaderStyle(wb.createCellStyle(), fontHeader);
            CellStyle headerStyleSecondary = getSecondaryHeaderStyle(wb.createCellStyle(), fontHeader);
            CellStyle stringStyle = getStringStyle(wb.createCellStyle(), fontData);
            CellStyle integerStyle = getIntegerStyle(wb.createCellStyle(), wb.createDataFormat(), fontData);

            // set headers
            Row headerRow = sheet.createRow(0);
            IntStream.rangeClosed(0, brTemplateColumns.length - 1)
                    .forEach(i -> createCell(headerRow, i, brTemplateColumns[i],
                            i == 2 ? headerStyleSecondary : headerStylePrimary));

            // set data
            for (int i = 0; i < branches.size(); i++) {
                Row row = sheet.createRow(i + 1);
                Branch branch = branches.get(i);

                Cell cell = row.createCell(0);
                cell.setCellValue(branch.getId());
                cell.setCellStyle(integerStyle);

                createCell(row, 1, branch.getBranchName(), stringStyle);
                createCell(row, 2, "-", stringStyle);
            }

            // autosize columns
            IntStream.rangeClosed(0, brTemplateColumns.length - 1)
                    .forEach(sheet::autoSizeColumn);

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            wb.write(out);

            resource = new ByteArrayResource(out.toByteArray());

        } catch (IOException e) {
            throw new ReportIOException("Ошибка формирования отчета",
                    "Филиал в формате шаблона");
        }

        return resource;
    }

    @Override
    public Resource getAllBranchesReport() {
        Resource resource;

        List<Branch> branches = branchService.getAllBranches().stream()
                .sorted(Comparator.comparing(Branch::getId))
                .toList();

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Branches");
            Font fontHeader = wb.createFont();
            Font fontData = wb.createFont();

            CellStyle headerStylePrimary = getPrimaryHeaderStyle(wb.createCellStyle(), fontHeader);
            CellStyle stringStyle = getStringStyle(wb.createCellStyle(), fontData);
            CellStyle integerStyle = getIntegerStyle(wb.createCellStyle(), wb.createDataFormat(), fontData);

            // set headers
            Row headerRow = sheet.createRow(0);
            IntStream.rangeClosed(0, allBranchesColumns.length - 1)
                    .forEach(i -> createCell(headerRow, i, allBranchesColumns[i], headerStylePrimary));

            // set data
            for (int i = 0; i < branches.size(); i++) {
                Row row = sheet.createRow(i + 1);
                Branch branch = branches.get(i);

                Cell cell = row.createCell(0);
                cell.setCellValue(branch.getId());
                cell.setCellStyle(integerStyle);

                createCell(row, 1, branch.getBranchName(), stringStyle);
            }

            // autosize columns
            IntStream.rangeClosed(0, allBranchesColumns.length - 1)
                    .forEach(sheet::autoSizeColumn);

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            wb.write(out);

            resource = new ByteArrayResource(out.toByteArray());

        } catch (IOException e) {
            throw new ReportIOException("Ошибка формирования отчета",
                    "Филиал в стандартном формате");
        }

        return resource;
    }

    @Override
    public Resource getTZTemplateReport() {
        Resource resource;

        List<TariffZone> tzs = tzService.getAllTariffZones().stream()
                .sorted(Comparator.comparing(TariffZone::getId))
                .toList();

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("TariffZones");
            Font fontHeader = wb.createFont();
            Font fontData = wb.createFont();

            CellStyle headerStylePrimary = getPrimaryHeaderStyle(wb.createCellStyle(), fontHeader);
            CellStyle headerStyleSecondary = getSecondaryHeaderStyle(wb.createCellStyle(), fontHeader);
            CellStyle stringStyle = getStringStyle(wb.createCellStyle(), fontData);
            CellStyle integerStyle = getIntegerStyle(wb.createCellStyle(), wb.createDataFormat(), fontData);

            // set headers
            Row headerRow = sheet.createRow(0);
            IntStream.rangeClosed(0, tzTemplateColumns.length - 1)
                    .forEach(i -> createCell(headerRow, i, tzTemplateColumns[i],
                            i == 2 ? headerStyleSecondary : headerStylePrimary));

            // set data
            for (int i = 0; i < tzs.size(); i++) {
                Row row = sheet.createRow(i + 1);
                TariffZone tz = tzs.get(i);

                Cell cell = row.createCell(0);
                cell.setCellValue(i + 1);
                cell.setCellStyle(integerStyle);

                createCell(row, 1, tz.getZoneName(), stringStyle);
                createCell(row, 2, "-", stringStyle);
            }

            // autosize columns
            IntStream.rangeClosed(0, tzTemplateColumns.length - 1)
                    .forEach(sheet::autoSizeColumn);

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            wb.write(out);

            resource = new ByteArrayResource(out.toByteArray());

        } catch (IOException e) {
            throw new ReportIOException("Ошибка формирования отчета",
                    "Тарифная зона в формате шаблона");
        }

        return resource;
    }

    @Override
    public Resource getAllTZReport() {
        Resource resource;

        List<TariffZone> tzs = tzService.getAllTariffZones().stream()
                .sorted(Comparator.comparing(TariffZone::getId))
                .toList();

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("TariffZones");
            Font fontHeader = wb.createFont();
            Font fontData = wb.createFont();

            CellStyle headerStylePrimary = getPrimaryHeaderStyle(wb.createCellStyle(), fontHeader);
            CellStyle stringStyle = getStringStyle(wb.createCellStyle(), fontData);
            CellStyle integerStyle = getIntegerStyle(wb.createCellStyle(), wb.createDataFormat(), fontData);

            // set headers
            Row headerRow = sheet.createRow(0);
            IntStream.rangeClosed(0, allTariffZonesColumns.length - 1)
                    .forEach(i -> createCell(headerRow, i, allTariffZonesColumns[i], headerStylePrimary));

            // set data
            for (int i = 0; i < tzs.size(); i++) {
                Row row = sheet.createRow(i + 1);
                TariffZone tz = tzs.get(i);

                Cell cell = row.createCell(0);
                cell.setCellValue(i + 1);
                cell.setCellStyle(integerStyle);

                createCell(row, 1, tz.getZoneName(), stringStyle);
            }

            // autosize columns
            IntStream.rangeClosed(0, allTariffZonesColumns.length - 1)
                    .forEach(sheet::autoSizeColumn);

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            wb.write(out);

            resource = new ByteArrayResource(out.toByteArray());

        } catch (IOException e) {
            throw new ReportIOException("Ошибка формирования отчета",
                    "Тарифная зона в стандартном формате");
        }

        return resource;
    }

    @Override
    public Resource getSrcPropTemplateReport(Integer year) {
        Resource resource;

        List<SourceProperty> srcProps = srcPropService.findAllPropByYear(year).stream()
                .sorted(Comparator.comparing(sp -> sp.getId().getSource().getName()))
                .sorted(Comparator.comparing(sp -> sp.getId().getSource().getSourceType()))
                .sorted(Comparator.comparing(sp -> sp.getBranch().getId()))
                .sorted(Comparator.comparing(sp -> sp.getTariffZone().getId()))
                .toList();

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("SourceProperties");
            Font fontHeader = wb.createFont();
            Font fontData = wb.createFont();
            Font fontTitle = wb.createFont();

            CellStyle headerStylePrimary = getPrimaryHeaderStyle(wb.createCellStyle(), fontHeader);
            CellStyle headerStyleSecondary = getSecondaryHeaderStyle(wb.createCellStyle(), fontHeader);
            CellStyle stringStyle = getStringStyle(wb.createCellStyle(), fontData);
            CellStyle integerStyle = getIntegerStyle(wb.createCellStyle(), wb.createDataFormat(), fontData);
            CellStyle titleStyle = getTitleStyle(wb.createCellStyle(), fontTitle);

            //set title
            Row titleRow = sheet.createRow(0);
            createCell(titleRow, 0, String.format("%s год", year), titleStyle);

            // set headers
            Row headerRow = sheet.createRow(1);

            IntStream.rangeClosed(0, srcPropTemplateColumns.length - 1)
                    .forEach(i -> createCell(headerRow, i, srcPropTemplateColumns[i],
                            i == 3 ? headerStyleSecondary : headerStylePrimary));

            // set data
            for (int i = 0; i < srcProps.size(); i++) {
                Row row = sheet.createRow(i + 2);
                SourceProperty srcProp = srcProps.get(i);

                createCell(row, 0, srcProp.getId().getSource().getId().toString(),
                        stringStyle);
                Cell cell = row.createCell(1);
                cell.setCellValue(srcProp.getBranch().getId());
                cell.setCellStyle(integerStyle);

                cell = row.createCell(2);
                cell.setCellValue(srcProp.getTariffZone().getId());
                cell.setCellStyle(integerStyle);

                createCell(row, 3, "-", stringStyle);
            }

            // autosize columns
            IntStream.rangeClosed(0, srcPropTemplateColumns.length - 1)
                    .forEach(sheet::autoSizeColumn);

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            wb.write(out);

            resource = new ByteArrayResource(out.toByteArray());

        } catch (IOException e) {
            throw new ReportIOException("Ошибка формирования отчета",
                    "Свойства источника в формате шаблона");
        }

        return resource;
    }

    @Override
    public Resource getAllSrcPropReport(Integer year) {
        Resource resource;

        List<SourceProperty> srcProps = srcPropService.findAllPropByYear(year).stream()
                .sorted(Comparator.comparing(sp -> sp.getId().getSource().getName()))
                .sorted(Comparator.comparing(sp -> sp.getId().getSource().getSourceType()))
                .sorted(Comparator.comparing(sp -> sp.getBranch().getId()))
                .sorted(Comparator.comparing(sp -> sp.getTariffZone().getId()))
                .toList();

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("SourceProperties");
            Font fontHeader = wb.createFont();
            Font fontData = wb.createFont();
            Font fontTitle = wb.createFont();

            CellStyle headerStylePrimary = getPrimaryHeaderStyle(wb.createCellStyle(), fontHeader);
            CellStyle stringStyle = getStringStyle(wb.createCellStyle(), fontData);
            CellStyle integerStyle = getIntegerStyle(wb.createCellStyle(), wb.createDataFormat(), fontData);
            CellStyle titleStyle = getTitleStyle(wb.createCellStyle(), fontTitle);

            //set title
            Row titleRow = sheet.createRow(0);
            createCell(titleRow, 0, String.format("%s год", year), titleStyle);

            // set headers
            Row headerRow = sheet.createRow(1);

            IntStream.rangeClosed(0, allSrcPropColumns.length - 1)
                    .forEach(i -> createCell(headerRow, i, allSrcPropColumns[i], headerStylePrimary));

            // set data
            for (int i = 0; i < srcProps.size(); i++) {
                Row row = sheet.createRow(i + 2);
                SourceProperty srcProp = srcProps.get(i);

                Cell cell = row.createCell(0);
                cell.setCellValue(i + 1);
                cell.setCellStyle(integerStyle);

                createCell(row, 1, srcProp.getId().getSource().getName(), stringStyle);
                createCell(row, 2, srcProp.getTariffZone().getZoneName(), stringStyle);
                createCell(row, 3, srcProp.getBranch().getBranchName(), stringStyle);
            }

            // autosize columns
            IntStream.rangeClosed(0, allSrcPropColumns.length - 1)
                    .forEach(sheet::autoSizeColumn);

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            wb.write(out);

            resource = new ByteArrayResource(out.toByteArray());

        } catch (IOException e) {
            throw new ReportIOException("Ошибка формирования отчета",
                    "Свойства источника в стандартном формате");
        }

        return resource;
    }

    @Override
    public Resource getSsfcTemplateReport(Integer year, String selection, List<UUID> srcIds) {
        Resource resource;

        List<StandardSFC> ssfcs = switch (selection) {
            case "all" -> ssfcService.findAllSsfcByYear(year);
            case "particular" -> ssfcService.findAllSsfcByYearAndSrcIds(year, srcIds);
            default -> throw new InvalidParameterException(String
                    .format("Invalid ssfc report type <%s>", selection));
        };

        List<SsfcsDTO> ssfcDTOs = ssfcs.stream()
                .collect(Collectors.groupingBy(ssfc ->
                        new SrcIdAndFuelType(ssfc.getProperties().getId().getSource().getId(),
                                ssfc.getFuelType())))
                .values()
                .stream()
                .sorted(Comparator.comparing(e -> e.getFirst()
                        .getFuelType().ordinal()))
                .sorted(Comparator.comparing(e -> e.getFirst()
                        .getProperties().getId().getSource().getName()))
                .sorted(Comparator.comparingInt(e -> e.getFirst()
                        .getProperties().getId().getSource().getSourceType().ordinal()))
                .sorted(Comparator.comparing(e -> e.getFirst().getProperties().getBranch().getId()))
                .sorted(Comparator.comparing(e -> e.getFirst().getProperties().getTariffZone().getId()))
                .map(SsfcsDTO::new)
                .toList();


        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Ssfcs");

            Font fontHeader = wb.createFont();
            Font fontData = wb.createFont();
            Font fontTitle = wb.createFont();

            CellStyle titleStyle = getTitleStyle(wb.createCellStyle(), fontTitle);
            CellStyle headerStylePrimary = getPrimaryHeaderStyle(wb.createCellStyle(), fontHeader);
            CellStyle headerStyleSecondary = getSecondaryHeaderStyle(wb.createCellStyle(), fontHeader);
            CellStyle stringStyle = getStringStyle(wb.createCellStyle(), fontData);
            CellStyle integerStyle = getIntegerStyle(wb.createCellStyle(), wb.createDataFormat(), fontData);
            CellStyle threeDigitsStyle = getDecimalStyle(wb.createCellStyle(),
                    wb.createDataFormat(), fontData, 3);
            CellStyle twoDigitsStyle = getDecimalStyle(wb.createCellStyle(),
                    wb.createDataFormat(), fontData, 2);

            // set title
            Row titleRow = sheet.createRow(0);
            createCell(titleRow, 0, String.format("%s год", year), titleStyle);

            // set headers
            Row headerRow = sheet.createRow(1);

            IntStream.rangeClosed(0, ssfcsTemplateColumns.length - 1)
                    .forEach(i -> createCell(headerRow, i, ssfcsTemplateColumns[i],
                            i <= 4 || i > 18 ? headerStyleSecondary : headerStylePrimary));

            // set data and merge cells
            for (int i = 0; i < ssfcDTOs.size(); i++) {
                SsfcsDTO srcSsfcsDTO = ssfcDTOs.get(i);

                IntStream.rangeClosed(ssfcRows.length * i + 2, ssfcRows.length * (i + 1) + 1)
                        .forEach(sheet::createRow);

                // merge cells
                for (int col : new int[]{0, 1, 2, 5, 6, 20}) {
                    var range = new CellRangeAddress(ssfcRows.length * i + 2,
                            ssfcRows.length * (i + 1) + 1, col, col);
                    sheet.addMergedRegion(range);
                    RegionUtil.setBorderLeft(BorderStyle.THIN, range, sheet);
                    RegionUtil.setBorderRight(BorderStyle.THIN, range, sheet);
                }

                // set data to merged cells
                Row currentRow = sheet.getRow(ssfcUnits.length * i + 2);
                Cell cell = currentRow.createCell(0);
                cell.setCellValue(i + 1);
                cell.setCellStyle(integerStyle);

                createCell(currentRow, 1, srcSsfcsDTO.getSrcName(), stringStyle);
                createCell(currentRow, 2, srcSsfcsDTO.getFuelType(), stringStyle);
                createCell(currentRow, 5, srcSsfcsDTO.getSrcId().toString(), stringStyle);

                cell = currentRow.createCell(6);
                cell.setCellValue(FUEL_TYPE.getByName(srcSsfcsDTO.getFuelType()).ordinal());
                cell.setCellStyle(integerStyle);

                createCell(currentRow, 20, "-", stringStyle);

                // set ssfc data row names and units
                for (int k = 0; k < ssfcRows.length; k++) {
                    createCell(sheet.getRow(ssfcRows.length * i + 2 + k), 3, ssfcRows[k], stringStyle);
                    createCell(sheet.getRow(ssfcUnits.length * i + 2 + k), 4, ssfcUnits[k], stringStyle);
                }

                // set ssfc data
                for (int k = 7; k < 19; k++) {
                    SsfcShortDTO ssfcMonth = srcSsfcsDTO.getSsfcs().get(k - 7);
                    setSsfcMonthDataCells(sheet, i, k,
                            ssfcMonth.getGeneration(),
                            ssfcMonth.getOwnNeeds(),
                            ssfcMonth.getPercentOwnNeeds(),
                            ssfcMonth.getProduction(),
                            ssfcMonth.getSsfcg(),
                            ssfcMonth.getSsfc(),
                            threeDigitsStyle,
                            twoDigitsStyle);
                }

                // set source summary data
                setSsfcMonthDataCells(sheet, i, 19,
                        srcSsfcsDTO.avgGeneration(),
                        srcSsfcsDTO.avgOwnNeeds(),
                        srcSsfcsDTO.avgPercentOwnNeeds(),
                        srcSsfcsDTO.avgProduction(),
                        srcSsfcsDTO.avgSsfcg(),
                        srcSsfcsDTO.avgSsfc(),
                        threeDigitsStyle, twoDigitsStyle);

                //set solid borders
                var range = new CellRangeAddress(ssfcRows.length * i + 2,
                        ssfcRows.length * (i + 1) + 1,
                        0, ssfcsTemplateColumns.length - 1);
                RegionUtil.setBorderBottom(BorderStyle.DOUBLE, range, sheet);
            }

            // autosize columns
            IntStream.rangeClosed(0, ssfcsTemplateColumns.length - 1)
                    .forEach(sheet::autoSizeColumn);

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            wb.write(out);

            resource = new ByteArrayResource(out.toByteArray());

        } catch (
                IOException e) {
            throw new ReportIOException("Ошибка формирования отчета",
                    "НУР в формате шаблона");
        }

        return resource;
    }

    @Override
    public Resource getAllSsfcReport(Integer year, String selection, List<UUID> srcIds) {
        //todo set fixed view for excel
        //todo add branch summary amd tariff zone summary
        Resource resource;

        List<StandardSFC> ssfcs = switch (selection) {
            case "all" -> ssfcService.findAllSsfcByYear(year);
            case "particular" -> ssfcService.findAllSsfcByYearAndSrcIds(year, srcIds);
            default -> throw new InvalidParameterException(String
                    .format("Invalid ssfc report type <%s>", selection));
        };

        SsfcSummary summary = new SsfcSumTZBranch(ssfcs);

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Ssfcs");

            Font fontHeader = wb.createFont();
            Font fontData = wb.createFont();
            Font fontTitle = wb.createFont();

            CellStyle titleStyle = getTitleStyle(wb.createCellStyle(), fontTitle);
            CellStyle headerStylePrimary = getPrimaryHeaderStyle(wb.createCellStyle(), fontHeader);
            CellStyle stringStyle = getStringStyle(wb.createCellStyle(), fontData);
            CellStyle integerStyle = getIntegerStyle(wb.createCellStyle(), wb.createDataFormat(), fontData);
            CellStyle threeDigitsStyle = getDecimalStyle(wb.createCellStyle(),
                    wb.createDataFormat(), fontData, 3);
            CellStyle twoDigitsStyle = getDecimalStyle(wb.createCellStyle(),
                    wb.createDataFormat(), fontData, 2);

            // set headers
            Row headerRow = sheet.createRow(1);
            IntStream.rangeClosed(0, allSsfcsColumns.length - 1)
                    .forEach(i -> createCell(headerRow, i, allSsfcsColumns[i], headerStylePrimary));

            // set data and merge cells

            BiConsumer<SsfcSummary, Counter> consumer = new BiConsumer<>() {
                @Override
                public void accept(SsfcSummary summary, Counter counter) {
                    Row row = sheet.createRow(counter.getAndIncrement());
                    row.createCell(0).setCellValue("start section" + summary.getName());

                    if (summary.hasSub()) {
                        summary.getSubSsfcs().forEach(sum -> this.accept(sum, counter));
                    } else {
                        summary.getAllSsfcs()
                                .stream()
                                .collect(Collectors.groupingBy(ssfc ->
                                        new SrcIdAndFuelType(ssfc.getProperties().getId().getSource().getId(),
                                                ssfc.getFuelType())))
                                .values().stream()
                                .sorted(Comparator.comparing(ssfcs -> ssfcs.getFirst().getProperties()
                                        .getId().getSource().getName()))
                                .sorted(Comparator.comparing(ssfcs -> ssfcs.getFirst().getProperties()
                                        .getId().getSource().getSourceType()))
                                .sorted(Comparator.comparing(ssfcs -> ssfcs.getFirst().getFuelType()))
                                .forEach(ssfcs -> {
                                    ssfcs = ssfcs.stream()
                                            .sorted(Comparator.comparing(StandardSFC::getMonth))
                                            .sorted(Comparator.comparing(ssfc -> ssfc.getProperties()
                                                    .getId().getYear())).toList();

                                    // create rows
                                    IntStream.rangeClosed(counter.getCounter(),
                                                    counter.getCounter() + ssfcRows.length)
                                            .forEach(sheet::createRow);

                                    // merge cells
                                    IntStream.of(0, 1, 2).forEach(col -> {
                                        var range = new CellRangeAddress(counter.getCounter(),
                                                counter.getCounter() + ssfcRows.length - 1,
                                                col, col);
                                        sheet.addMergedRegion(range);
                                        RegionUtil.setBorderLeft(BorderStyle.THIN, range, sheet);
                                        RegionUtil.setBorderRight(BorderStyle.THIN, range, sheet);
                                    });

                                    // set data to merged cells
                                    Row currentRow = sheet.getRow(counter.getCounter());
                                    Cell cell = currentRow.createCell(0);
                                    //todo finish numbers
                                    cell.setCellValue(1);
                                    cell.setCellStyle(integerStyle);
                                    createCell(currentRow, 1,
                                            ssfcs.getFirst().getProperties().getId().getSource().getName(),
                                            stringStyle);
                                    createCell(currentRow, 2, ssfcs.getFirst().getFuelType().getName(),
                                            stringStyle);

                                    // set ssfc data row names and units
                                    IntStream.range(0, ssfcRows.length).forEach(k -> {
                                        createCell(sheet.getRow(counter.getCounter() + k), 3,
                                                ssfcRows[k], stringStyle);
                                        createCell(sheet.getRow(counter.getCounter() + k), 4,
                                                ssfcUnits[k], stringStyle);
                                    });


                                    counter.increment(ssfcRows.length);
                                });

                    }


//
//                            // set source summary data
//                            setSsfcMonthDataCells(sheet, i, 5,
//                                    srcSsfcsDTO.avgGeneration(),
//                                    srcSsfcsDTO.avgOwnNeeds(),
//                                    srcSsfcsDTO.avgPercentOwnNeeds(),
//                                    srcSsfcsDTO.avgProduction(),
//                                    srcSsfcsDTO.avgSsfcg(),
//                                    srcSsfcsDTO.avgSsfc(),
//                                    threeDigitsStyle, twoDigitsStyle);
//
//                            // set ssfc data
//                            for (int k = 6; k <= 17; k++) {
//                                SsfcShortDTO ssfcMonth = srcSsfcsDTO.getSsfcs().get(k - 6);
//                                setSsfcMonthDataCells(sheet, i, k,
//                                        ssfcMonth.getGeneration(),
//                                        ssfcMonth.getOwnNeeds(),
//                                        ssfcMonth.getPercentOwnNeeds(),
//                                        ssfcMonth.getProduction(),
//                                        ssfcMonth.getSsfcg(),
//                                        ssfcMonth.getSsfc(),
//                                        threeDigitsStyle,
//                                        twoDigitsStyle);
//                            }
//
//                            //set solid borders
//                            var range = new CellRangeAddress(ssfcRows.length * i + 2,
//                                    ssfcRows.length * (i + 1) + 1,
//                                    0, allSsfcsColumns.length - 1);
//                            RegionUtil.setBorderBottom(BorderStyle.DOUBLE, range, sheet);
//                        }
//                    });
//                }
                    row = sheet.createRow(counter.getAndIncrement());
                    row.createCell(0).

                            setCellValue("end section" + summary.getName());
                }
            };

            Counter counter = new Counter(2);

            summary.getSubSsfcs().forEach(sum -> consumer.accept(sum, counter));

            Row row = sheet.createRow(counter.getAndIncrement());
            row.createCell(0).setCellValue("TOTAL SECTION");


            // autosize columns
            IntStream.rangeClosed(0, allSsfcsColumns.length - 1)
                            .

                    forEach(sheet::autoSizeColumn);

            // set title (first cell is too wide if before)
            Row titleRow = sheet.createRow(0);

            createCell(titleRow, 0, String.format("Нормативные удельные расходы топлива на единицу " +
                    "отпущенной тепловой энергии источников ПАО \"МОЭК\" на %s год", year), titleStyle);

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            wb.write(out);

            resource = new

                    ByteArrayResource(out.toByteArray());

        } catch (
                IOException e) {
            throw new ReportIOException("Ошибка формирования отчета",
                    "НУР в стандартном формате");
        }

        return resource;
    }

    private void setSsfcMonthDataCells(Sheet sheet, int elementNumber, int columnNumber,
                                       Double generation, Double ownNeeds, Double percentOwnNeeds,
                                       Double production, Double ssfcg, Double ssfc,
                                       CellStyle threeDigitsStyle, CellStyle twoDigitsStyle) {

        Row row = sheet.getRow(ssfcRows.length * elementNumber + 2);
        Cell cellSsfc = row.createCell(columnNumber);
        cellSsfc.setCellValue(generation);
        cellSsfc.setCellStyle(threeDigitsStyle);

        row = sheet.getRow(ssfcRows.length * elementNumber + 2 + 1);
        cellSsfc = row.createCell(columnNumber);
        cellSsfc.setCellValue(ownNeeds);
        cellSsfc.setCellStyle(threeDigitsStyle);

        row = sheet.getRow(ssfcRows.length * elementNumber + 2 + 2);
        cellSsfc = row.createCell(columnNumber);
        cellSsfc.setCellValue(percentOwnNeeds);
        cellSsfc.setCellStyle(twoDigitsStyle);

        row = sheet.getRow(ssfcRows.length * elementNumber + 2 + 3);
        cellSsfc = row.createCell(columnNumber);
        cellSsfc.setCellValue(production);
        cellSsfc.setCellStyle(threeDigitsStyle);

        row = sheet.getRow(ssfcRows.length * elementNumber + 2 + 4);
        cellSsfc = row.createCell(columnNumber);
        cellSsfc.setCellValue(ssfcg);
        cellSsfc.setCellStyle(twoDigitsStyle);

        row = sheet.getRow(ssfcRows.length * elementNumber + 2 + 5);
        cellSsfc = row.createCell(columnNumber);
        cellSsfc.setCellValue(ssfc);
        cellSsfc.setCellStyle(twoDigitsStyle);

        log.debug("Ssfc one month data for one source set to report file");
    }

    private CellStyle getIntegerStyle(CellStyle cellStyle, DataFormat dataFormat, Font font) {
        getBaseStyle(cellStyle, font)
                .setDataFormat(dataFormat.getFormat("# ##0"));

        return cellStyle;
    }

    private CellStyle getDecimalStyle(CellStyle cellStyle, DataFormat dataFormat, Font font, Integer digits) {
        getBaseStyle(cellStyle, font)
                .setDataFormat(dataFormat.getFormat(String.format("# ##0.%s", Strings.repeat("0", digits))));

        return cellStyle;
    }

    private CellStyle getStringStyle(CellStyle cellStyle, Font font) {
        getBaseStyle(cellStyle, font)
                .setDataFormat((short) BuiltinFormats.getBuiltinFormat("@"));

        return cellStyle;
    }

    private CellStyle getSecondaryHeaderStyle(CellStyle cellStyle, Font font) {
        font.setBold(true);
        getStringStyle(cellStyle, font);
        //fill
        cellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(244, 176, 132)
                , new DefaultIndexedColorMap()));
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        return cellStyle;
    }

    private CellStyle getPrimaryHeaderStyle(CellStyle cellStyle, Font font) {
        font.setBold(true);
        getStringStyle(cellStyle, font);
        //fill
        cellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(252, 228, 214)
                , new DefaultIndexedColorMap()));
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        return cellStyle;
    }

    private CellStyle getTitleStyle(CellStyle cellStyle, Font font) {
        setBaseFont(font);
        font.setBold(true);
        cellStyle.setFont(font);

        return cellStyle;
    }

    private CellStyle getBaseStyle(CellStyle cellStyle, Font font) {
        //alignment
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        //border
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());

        //font
        setBaseFont(font);
        cellStyle.setFont(font);

        return cellStyle;
    }

    private Font setBaseFont(Font font) {
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 12);
        font.setColor(IndexedColors.BLACK.getIndex());

        return font;
    }
}
