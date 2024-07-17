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
import ru.bk.j3000.normarchivedata.util.Counter;
import ru.bk.j3000.normarchivedata.util.period.*;
import ru.bk.j3000.normarchivedata.util.ssfcsum.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.groupingBy;
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
            "Декабрь", "Год", "Комментарии"};

    private final String[] allSsfcsColumns = {"№ п/п",
            "Наименование источника тепловой энергии",
            "Вид топлива", "Наименование показателя",
            "Единицы измерения", "Год",
            "Январь", "Февраль", "Март", "Апрель",
            "Май", "Июнь", "Июль", "Август", "Сентябрь",
            "Октябрь", "Ноябрь", "Декабрь"};

    private final List<String> allSsfcsPeriodBaseColumns = new ArrayList<>(List.of("№ п/п",
            "Наименование источника тепловой энергии", "Вид топлива", "Наименование показателя",
            "Единицы измерения", "Итого"));

    private final String[] ssfcRows = {"Выработка тепловой энергии",
            "Тепловая энергия на собственные нужды",
            "Тепловая энергия на собственные нужды",
            "Отпуск тепловой энергии с коллекторов источников",
            "УРУТ на выработку тепловой энергии",
            "УРУТ на отпуск тепловой энергии"};

    private final String[] ssfcUnits = {"тыс. Гкал", "тыс. Гкал", "%", "тыс. Гкал",
            "кг у.т./Гкал", "кг у.т./Гкал"};


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
    public Resource getSsfcsReport(String type, String selection, Integer year, List<UUID> srcIds,
                                   List<String> sumTypes, List<String> periods) {
        Resource resource = switch (type) {
            case "template" -> getSsfcTemplateReport(year, selection, srcIds);
            case "standard" -> getAllSsfcReport(year, selection, srcIds,
                    Objects.isNull(sumTypes) ? List.of() : sumTypes);
            case "period" -> getSsfcPeriodReport(selection, srcIds,
                    Objects.isNull(sumTypes) ? List.of() : sumTypes, periods);
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
            Sheet sheet = wb.createSheet("sources");
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
            Sheet sheet = wb.createSheet("branches");
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
            Sheet sheet = wb.createSheet("tariffZones");
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
            Sheet sheet = wb.createSheet("sourceProperties");
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
                .collect(groupingBy(ssfc ->
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
            Sheet sheet = wb.createSheet("ssfcs");

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
    public Resource getAllSsfcReport(Integer year, String selection, List<UUID> srcIds, List<String> sumTypes) {
        Resource resource;

        List<StandardSFC> ssfcs = switch (selection) {
            case "all" -> ssfcService.findAllSsfcByYear(year);
            case "particular" -> ssfcService.findAllSsfcByYearAndSrcIds(year, srcIds);
            default -> throw new InvalidParameterException(String
                    .format("Invalid ssfc report type <%s>", selection));
        };

        SsfcSummary summary;

        if (sumTypes.contains("branch") && !sumTypes.contains("tz")) {
            summary = new SsfcSumBranch(ssfcs);
        } else {
            summary = new SsfcSumTZBranch(ssfcs);
        }

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Ssfcs");

            Map<String, Map<String, CellStyle>> styles = getStyles(wb);

            // set headers
            Row headerRow = sheet.createRow(1);
            IntStream.rangeClosed(0, allSsfcsColumns.length - 1)
                    .forEach(i -> createCell(headerRow, i, allSsfcsColumns[i],
                            styles.get("base").get("header primary")));

            log.debug("Standard report headers set.");

            // set data and merge cells
            Counter counter = new Counter(2);
            Counter number = new Counter(1);

            summary.getSubSsfcs().forEach(sum -> this.formStandardSsfcBlock(sheet, sum, counter,
                    number, styles, sumTypes)
            );

            log.debug("Standard report data set.");

            // set totals
            if (sumTypes.contains("totals")) {
                formGroupBlock("Всего по компании", sheet, summary.avgData(), counter, null,
                        styles.get("totals").get("integer"),
                        styles.get("totals").get("string"),
                        styles.get("totals").get("threeDigits"),
                        styles.get("totals").get("twoDigits"));
            }

            log.debug("Standard report totals set.");

            // autosize columns
            IntStream.rangeClosed(0, allSsfcsColumns.length - 1).forEach(sheet::autoSizeColumn);

            log.debug("Standard report columns autosized.");

            // set title (first cell is too wide if before)
            Row titleRow = sheet.createRow(0);
            createCell(titleRow, 0, String.format("Нормативные удельные расходы топлива на единицу " +
                            "отпущенной тепловой энергии источниками ПАО \"МОЭК\" на %s год", year),
                    styles.get("base").get("title"));

            log.debug("Standard report title set.");


            // freeze rows and columns
            sheet.createFreezePane(0, 2);

            log.debug("Standard report columns and rows frozen.");

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            wb.write(out);
            resource = new ByteArrayResource(out.toByteArray());

        } catch (
                IOException e) {
            throw new ReportIOException("Ошибка формирования отчета",
                    "НУР в стандартном формате");
        }

        return resource;
    }

    // set top and bottom borders to double
    private void styleTopBottomDoubleBorder(Sheet sheet, int firstRow, int lastRow,
                                            int firstColumn, int lastColumn) {
        var range = new CellRangeAddress(firstRow, lastRow, firstColumn, lastColumn);

        RegionUtil.setBorderBottom(BorderStyle.DOUBLE, range, sheet);
        RegionUtil.setBorderTop(BorderStyle.DOUBLE, range, sheet);
        RegionUtil.setBorderLeft(BorderStyle.THIN, range, sheet);
        RegionUtil.setBorderRight(BorderStyle.THIN, range, sheet);
    }

    private void formStandardSsfcBlock(Sheet sheet, SsfcSummary summary, Counter counter, Counter number,
                                       Map<String, Map<String, CellStyle>> styles,
                                       List<String> sumTypes) {
        boolean srcsIncluded = !sumTypes.contains("sumsOnly");

        log.debug("Standard report ssfcBlock started for {}. Row {}",
                summary.getName(), counter.getCounter());

        // select group
        String group = selectGroup(summary.getClass().getSimpleName(), summary.getName());

        // set group header
        if (sumTypes.contains(group) && srcsIncluded) {
            Row row = sheet.createRow(counter.incrementAndGet());
            IntStream.range(0, allSsfcsColumns.length).forEach(i -> row.createCell(i)
                    .setCellStyle(styles.get(group + " no borders").get("string")));
            styleTopBottomDoubleBorder(sheet, row.getRowNum(), row.getRowNum(),
                    0, allSsfcsColumns.length - 1);
            row.getCell(1).setCellValue(summary.getName());
        }

        // set data
        if (summary.hasSub()) {
            summary.getSubSsfcs().forEach(sum -> this.formStandardSsfcBlock(sheet, sum, counter,
                    number, styles, sumTypes));
        } else if (srcsIncluded) {
            summary.getAllSsfcs()
                    .stream()
                    .collect(groupingBy(ssfc -> ssfc.getProperties().getId()
                            .getSource().getId()))
                    .values().stream()
                    .sorted(Comparator.comparing(ssfcs -> ssfcs.getFirst().getProperties().getId().getYear()))
                    .sorted(Comparator.comparing(ssfcs -> ssfcs.getFirst().getProperties()
                            .getId().getSource().getName()))
                    .sorted(Comparator.comparing(ssfcs -> ssfcs.getFirst().getProperties()
                            .getId().getSource().getSourceType()))
                    .sorted(Comparator.comparing(ssfcs -> ssfcs.getFirst().getFuelType()))
                    .map(SsfcSumSrc::new)
                    .forEach(sum -> formGroupBlock(sum.getAllSsfcs().getFirst().getProperties()
                                    .getId().getSource().getName(), sheet, sum.avgData(),
                            counter, number,
                            styles.get("base").get("integer"),
                            styles.get("base").get("string"),
                            styles.get("base").get("threeDigits"),
                            styles.get("base").get("twoDigits"))
                    );
        }

        log.debug("Standard report sum footer started for {}. Row {}",
                summary.getName(), counter.getCounter());

        // set footer sums
        if (sumTypes.contains(group)) {
            formGroupBlock(summary.getName(), sheet, summary.avgData(), counter, null,
                    styles.get(group).get("integer"),
                    styles.get(group).get("string"),
                    styles.get(group).get("threeDigits"),
                    styles.get(group).get("twoDigits"));
        }
    }

    private String selectGroup(String className, String summaryName) {
        String group;

        if (className.equals(SsfcSumBranch.class.getSimpleName())) {
            group = "tz";
        } else if (summaryName.equals("source")) {
            group = "base";
        } else {
            group = "branch";
        }

        return group;
    }

    private void formGroupBlock(String name, Sheet sheet, double[][] avgData,
                                Counter counter, Counter number,
                                CellStyle integerStyle, CellStyle stringStyle,
                                CellStyle threeDigitsStyle, CellStyle twoDigitsStyle) {

        List<double[][]> listData = AvgDataHandler.avgDataToList(avgData);

        if (listData.size() == 1) {
            formGroupData(name, sheet, listData.getFirst(),
                    avgData[1][12] == 0 ? FUEL_TYPE.DIESEL.getName() : FUEL_TYPE.GAS.getName(),
                    counter, number, integerStyle, stringStyle, threeDigitsStyle, twoDigitsStyle);
        } else {
            formGroupData(String.format("%s (газ + дизель)", name), sheet, listData.get(0),
                    "газ + дизель", counter, number, integerStyle,
                    stringStyle, threeDigitsStyle, twoDigitsStyle);
            formGroupData(String.format("%s (газ)", name), sheet, listData.get(1), FUEL_TYPE.GAS.getName(),
                    counter, number, integerStyle, stringStyle, threeDigitsStyle, twoDigitsStyle);
            formGroupData(String.format("%s (дизель)", name), sheet, listData.get(2), FUEL_TYPE.DIESEL.getName(),
                    counter, number, integerStyle, stringStyle, threeDigitsStyle, twoDigitsStyle);
        }
    }

    private void formGroupData(String name, Sheet sheet, double[][] data, String fuelType,
                               Counter counter, Counter number,
                               CellStyle integerStyle, CellStyle stringStyle,
                               CellStyle threeDigitsStyle, CellStyle twoDigitsStyle) {

        log.debug("Standard report sum group data started for {}. Row {}",
                name, counter.getCounter());

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
        if (Objects.isNull(number)) {
            createCell(currentRow, 0, "Итого", stringStyle);
        } else {
            cell.setCellValue(number.incrementAndGet());
            cell.setCellStyle(integerStyle);
        }

        createCell(currentRow, 1, name, stringStyle);
        createCell(currentRow, 2, fuelType, stringStyle);

        // set ssfc data row names and units
        IntStream.range(0, ssfcRows.length).forEach(i -> {
            createCell(sheet.getRow(counter.getCounter() + i), 3,
                    ssfcRows[i], stringStyle);
            createCell(sheet.getRow(counter.getCounter() + i), 4,
                    ssfcUnits[i], stringStyle);
        });

        // set ssfc data
        IntStream.range(0, 12).forEach(i ->
                setSsfcMonthDataCellsStd(sheet,
                        counter.getCounter(),
                        6 + i, // data set in 6 - 17 cells
                        data[0][i],
                        data[1][i],
                        data[2][i],
                        data[3][i],
                        data[4][i],
                        data[5][i],
                        threeDigitsStyle,
                        twoDigitsStyle));

        // set source summary data
        setSsfcMonthDataCellsStd(sheet,
                counter.getCounter(),
                5,
                data[0][12],
                data[1][12],
                data[2][12],
                data[3][12],
                data[4][12],
                data[5][12],
                threeDigitsStyle,
                twoDigitsStyle);

        // set top and bottom double borders
        styleTopBottomDoubleBorder(sheet, counter.getCounter(),
                counter.getCounter() + ssfcRows.length - 1,
                0, allSsfcsColumns.length - 1);

        counter.increment(ssfcRows.length);

        log.debug("Standard report sum group data set for {}. Row {}",
                name, counter.getCounter());
    }

    private void setSsfcMonthDataCells(Sheet sheet, int elementNumber, int columnNumber,
                                       Double generation, Double ownNeeds, Double percentOwnNeeds,
                                       Double production, Double ssfcg, Double ssfc,
                                       CellStyle threeDigitsStyle, CellStyle twoDigitsStyle) {

        setSsfcMonthDataCellsStd(sheet, ssfcRows.length * elementNumber + 2,
                columnNumber, generation, ownNeeds, percentOwnNeeds, production,
                ssfcg, ssfc, threeDigitsStyle, twoDigitsStyle);
    }

    private void setSsfcMonthDataCellsStd(Sheet sheet, int rowNumber, int columnNumber,
                                          Double generation, Double ownNeeds, Double percentOwnNeeds,
                                          Double production, Double ssfcg, Double ssfc,
                                          CellStyle threeDigitsStyle, CellStyle twoDigitsStyle) {

        Row row = sheet.getRow(rowNumber);
        Cell cellSsfc = row.createCell(columnNumber);
        cellSsfc.setCellValue(generation);
        cellSsfc.setCellStyle(threeDigitsStyle);

        row = sheet.getRow(rowNumber + 1);
        cellSsfc = row.createCell(columnNumber);
        cellSsfc.setCellValue(ownNeeds);
        cellSsfc.setCellStyle(threeDigitsStyle);

        row = sheet.getRow(rowNumber + 2);
        cellSsfc = row.createCell(columnNumber);
        cellSsfc.setCellValue(percentOwnNeeds);
        cellSsfc.setCellStyle(twoDigitsStyle);

        row = sheet.getRow(rowNumber + 3);
        cellSsfc = row.createCell(columnNumber);
        cellSsfc.setCellValue(production);
        cellSsfc.setCellStyle(threeDigitsStyle);

        row = sheet.getRow(rowNumber + 4);
        cellSsfc = row.createCell(columnNumber);
        cellSsfc.setCellValue(ssfcg);
        cellSsfc.setCellStyle(twoDigitsStyle);

        row = sheet.getRow(rowNumber + 5);
        cellSsfc = row.createCell(columnNumber);
        cellSsfc.setCellValue(ssfc);
        cellSsfc.setCellStyle(twoDigitsStyle);

        log.debug("Ssfc one month data for one source set to report file");
    }

    private Map<String, Map<String, CellStyle>> getStyles(Workbook wb) {
        Map<String, Map<String, CellStyle>> styles = new HashMap<>();

        styles.put("base", new HashMap<>());
        styles.put("tz", new HashMap<>());
        styles.put("tz no borders", new HashMap<>());
        styles.put("branch", new HashMap<>());
        styles.put("branch no borders", new HashMap<>());
        styles.put("totals", new HashMap<>());

        Font fontHeader = wb.createFont();
        Font fontData = wb.createFont();
        Font fontTitle = wb.createFont();

        Map<String, CellStyle> styleGroup = styles.get("base");
        styleGroup.put("title", getTitleStyle(wb.createCellStyle(), fontTitle));
        styleGroup.put("header primary", getPrimaryHeaderStyle(wb.createCellStyle(), fontHeader));
        styleGroup.put("string", getStringStyle(wb.createCellStyle(), fontData));
        styleGroup.put("integer", getIntegerStyle(wb.createCellStyle(), wb.createDataFormat(), fontData));
        styleGroup.put("threeDigits", getDecimalStyle(wb.createCellStyle(), wb.createDataFormat(),
                fontData, 3));
        styleGroup.put("twoDigits", getDecimalStyle(wb.createCellStyle(), wb.createDataFormat(),
                fontData, 2));

        styleGroup = styles.get("tz");
        styleGroup.put("title", getTitleStyleTZ(wb.createCellStyle(), fontTitle, true));
        styleGroup.put("header primary", getPrimaryHeaderStyleTZ(wb.createCellStyle(), fontHeader,
                true));
        styleGroup.put("string", getStringStyleTZ(wb.createCellStyle(), fontData, true));
        styleGroup.put("integer", getIntegerStyleTZ(wb.createCellStyle(), wb.createDataFormat(),
                fontData, true));
        styleGroup.put("threeDigits", getDecimalStyleTZ(wb.createCellStyle(), wb.createDataFormat(),
                fontData, 3, true));
        styleGroup.put("twoDigits", getDecimalStyleTZ(wb.createCellStyle(), wb.createDataFormat(),
                fontData, 2, true));

        styleGroup = styles.get("tz no borders");
        styleGroup.put("title", getTitleStyleTZ(wb.createCellStyle(), fontTitle, false));
        styleGroup.put("header primary", getPrimaryHeaderStyleTZ(wb.createCellStyle(), fontHeader,
                false));
        styleGroup.put("string", getStringStyleTZ(wb.createCellStyle(), fontData, false));
        styleGroup.put("integer", getIntegerStyleTZ(wb.createCellStyle(), wb.createDataFormat(),
                fontData, false));
        styleGroup.put("threeDigits", getDecimalStyleTZ(wb.createCellStyle(), wb.createDataFormat(),
                fontData, 3, false));
        styleGroup.put("twoDigits", getDecimalStyleTZ(wb.createCellStyle(), wb.createDataFormat(),
                fontData, 2, false));

        styleGroup = styles.get("branch");
        styleGroup.put("title", getTitleStyleBranch(wb.createCellStyle(), fontTitle, true));
        styleGroup.put("header primary", getPrimaryHeaderStyleBranch(wb.createCellStyle(), fontHeader,
                true));
        styleGroup.put("string", getStringStyleBranch(wb.createCellStyle(), fontData, true));
        styleGroup.put("integer", getIntegerStyleBranch(wb.createCellStyle(), wb.createDataFormat(),
                fontData, true));
        styleGroup.put("threeDigits", getDecimalStyleBranch(wb.createCellStyle(), wb.createDataFormat(),
                fontData, 3, true));
        styleGroup.put("twoDigits", getDecimalStyleBranch(wb.createCellStyle(), wb.createDataFormat(),
                fontData, 2, true));

        styleGroup = styles.get("branch no borders");
        styleGroup.put("title", getTitleStyleBranch(wb.createCellStyle(), fontTitle, false));
        styleGroup.put("header primary", getPrimaryHeaderStyleBranch(wb.createCellStyle(), fontHeader,
                false));
        styleGroup.put("string", getStringStyleBranch(wb.createCellStyle(), fontData, false));
        styleGroup.put("integer", getIntegerStyleBranch(wb.createCellStyle(), wb.createDataFormat(),
                fontData, false));
        styleGroup.put("threeDigits", getDecimalStyleBranch(wb.createCellStyle(), wb.createDataFormat(),
                fontData, 3, false));
        styleGroup.put("twoDigits", getDecimalStyleBranch(wb.createCellStyle(), wb.createDataFormat(),
                fontData, 2, false));

        styleGroup = styles.get("totals");
        styleGroup.put("title", getTitleStyleTotals(wb.createCellStyle(), fontTitle, true));
        styleGroup.put("header primary", getPrimaryHeaderStyleTotals(wb.createCellStyle(), fontHeader,
                true));
        styleGroup.put("string", getStringStyleTotals(wb.createCellStyle(), fontData, true));
        styleGroup.put("integer", getIntegerStyleTotals(wb.createCellStyle(), wb.createDataFormat(),
                fontData, true));
        styleGroup.put("threeDigits", getDecimalStyleTotals(wb.createCellStyle(), wb.createDataFormat(),
                fontData, 3, true));
        styleGroup.put("twoDigits", getDecimalStyleTotals(wb.createCellStyle(), wb.createDataFormat(),
                fontData, 2, true));

        return styles;
    }

    private CellStyle getDecimalStyleTZ(CellStyle style, DataFormat dataFormat, Font font,
                                        int digits, boolean borders) {
        getDecimalStyle(style, dataFormat, font, digits);
        styleBackgroundAndBorders(style, IndexedColors.LIGHT_GREEN, borders);

        return style;
    }

    private CellStyle getIntegerStyleTZ(CellStyle style, DataFormat dataFormat,
                                        Font font, boolean borders) {
        getIntegerStyle(style, dataFormat, font);
        styleBackgroundAndBorders(style, IndexedColors.LIGHT_GREEN, borders);

        return style;
    }

    private CellStyle getStringStyleTZ(CellStyle style, Font font, boolean borders) {
        getStringStyle(style, font);
        styleBackgroundAndBorders(style, IndexedColors.LIGHT_GREEN, borders);

        return style;
    }

    private CellStyle getPrimaryHeaderStyleTZ(CellStyle style, Font font, boolean borders) {
        getPrimaryHeaderStyle(style, font);
        styleBackgroundAndBorders(style, IndexedColors.LIGHT_GREEN, borders);

        return style;
    }

    private CellStyle getTitleStyleTZ(CellStyle style, Font font, boolean borders) {
        getTitleStyle(style, font);
        styleBackgroundAndBorders(style, IndexedColors.LIGHT_GREEN, borders);

        return style;
    }

    private CellStyle getDecimalStyleBranch(CellStyle style, DataFormat dataFormat, Font font,
                                            int digits, boolean borders) {
        getDecimalStyle(style, dataFormat, font, digits);
        styleBackgroundAndBorders(style, IndexedColors.LEMON_CHIFFON, borders);

        return style;
    }

    private CellStyle getIntegerStyleBranch(CellStyle style, DataFormat dataFormat, Font font, boolean borders) {
        getIntegerStyle(style, dataFormat, font);
        styleBackgroundAndBorders(style, IndexedColors.LEMON_CHIFFON, borders);

        return style;
    }

    private CellStyle getStringStyleBranch(CellStyle style, Font font, boolean borders) {
        getStringStyle(style, font);
        styleBackgroundAndBorders(style, IndexedColors.LEMON_CHIFFON, borders);

        return style;
    }

    private CellStyle getPrimaryHeaderStyleBranch(CellStyle style, Font font, boolean borders) {
        getPrimaryHeaderStyle(style, font);
        styleBackgroundAndBorders(style, IndexedColors.LEMON_CHIFFON, borders);

        return style;
    }

    private CellStyle getTitleStyleBranch(CellStyle style, Font font, boolean borders) {
        getTitleStyle(style, font);
        styleBackgroundAndBorders(style, IndexedColors.LEMON_CHIFFON, borders);

        return style;
    }

    private CellStyle getDecimalStyleTotals(CellStyle style, DataFormat dataFormat, Font font,
                                            int digits, boolean borders) {
        getDecimalStyle(style, dataFormat, font, digits);
        styleBackgroundAndBorders(style, IndexedColors.LIGHT_YELLOW, borders);

        return style;
    }

    private CellStyle getIntegerStyleTotals(CellStyle style, DataFormat dataFormat, Font font, boolean borders) {
        getIntegerStyle(style, dataFormat, font);
        styleBackgroundAndBorders(style, IndexedColors.LIGHT_YELLOW, borders);

        return style;
    }

    private CellStyle getStringStyleTotals(CellStyle style, Font font, boolean borders) {
        getStringStyle(style, font);
        styleBackgroundAndBorders(style, IndexedColors.LIGHT_YELLOW, borders);

        return style;
    }

    private CellStyle getPrimaryHeaderStyleTotals(CellStyle style, Font font, boolean borders) {
        getPrimaryHeaderStyle(style, font);
        styleBackgroundAndBorders(style, IndexedColors.LIGHT_YELLOW, borders);

        return style;
    }

    private CellStyle getTitleStyleTotals(CellStyle style, Font font, boolean borders) {
        getTitleStyle(style, font);
        styleBackgroundAndBorders(style, IndexedColors.LIGHT_YELLOW, borders);

        return style;
    }

    private CellStyle styleBackgroundAndBorders(CellStyle cellStyle, IndexedColors color, boolean borders) {
        cellStyle.setFillForegroundColor(color.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        if (!borders) {
            cellStyle.setBorderBottom(BorderStyle.NONE);
            cellStyle.setBorderTop(BorderStyle.NONE);
            cellStyle.setBorderLeft(BorderStyle.NONE);
            cellStyle.setBorderRight(BorderStyle.NONE);
        }

        return cellStyle;
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

    private Resource getSsfcPeriodReport(String selection, List<UUID> srcIds,
                                         List<String> sumTypes, List<String> periodsStr) {

        List<YearMonth> yearMonths = periodsStr.stream().map(str -> {
                    String[] period = str.split("\\.");
                    return new YearMonth(Integer.parseInt(period[0]), Integer.parseInt(period[1]));
                })
                .toList();

        String[] columnNames = getColumnNames(yearMonths);

        Resource resource;

        List<StandardSFC> ssfcs = switch (selection) {
            case "all" -> ssfcService.findAllSsfcByPeriods(yearMonths);
            case "particular" -> ssfcService.findAllSsfcByPeriodsAndSrcIds(yearMonths, srcIds);
            default -> throw new InvalidParameterException(String
                    .format("Invalid ssfc report (period) type <%s>", selection));
        };

        // summary;
        PeriodSum periodSum;

        if (sumTypes.contains("branch") && !sumTypes.contains("tz")) {
            periodSum = new TzPeriodSum(ssfcs, yearMonths);
        } else {
            periodSum = new TzBranchPeriodSum(ssfcs, yearMonths);
        }

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Ssfcs");

            Map<String, Map<String, CellStyle>> styles = getStyles(wb);

            // set headers
            Row headerRow = sheet.createRow(1);
            IntStream.range(0, columnNames.length)
                    .forEach(i -> createCell(headerRow, i, columnNames[i],
                            styles.get("base").get("header primary")));

            log.debug("Standard report (period) headers set.");

            // set data and merge cells
            Counter counter = new Counter(2);
            Counter number = new Counter(1);

            periodSum.getPeriods().forEach(sum -> this.formStandardSsfcBlockPeriod(sheet, sum, counter,
                    number, styles, sumTypes, columnNames)
            );

            log.debug("Standard report (period) data set.");

            // set totals
            if (sumTypes.contains("totals")) {
                formGroupBlockPeriod("Всего по компании", sheet, periodSum.getAvgData(),
                        counter, null,
                        styles.get("totals").get("integer"),
                        styles.get("totals").get("string"),
                        styles.get("totals").get("threeDigits"),
                        styles.get("totals").get("twoDigits"));
            }

            log.debug("Standard report (period) totals set.");

            // autosize columns
            IntStream.rangeClosed(0, columnNames.length - 1).forEach(sheet::autoSizeColumn);

            log.debug("Standard report (period) columns autosized.");

            // set title (first cell is too wide if before)
            Row titleRow = sheet.createRow(0);
            createCell(titleRow, 0, "Нормативные удельные расходы топлива на единицу " +
                            "отпущенной тепловой энергии источниками ПАО \"МОЭК\"",
                    styles.get("base").get("title"));

            log.debug("Standard report (period) title set.");

            // freeze rows and columns
            sheet.createFreezePane(0, 2);

            log.debug("Standard report (period) columns and rows frozen.");

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            wb.write(out);
            resource = new ByteArrayResource(out.toByteArray());

        } catch (
                IOException e) {
            throw new ReportIOException("Ошибка формирования отчета",
                    "НУР в стандартном формате (за период)");
        }

        return resource;
    }

    private void formStandardSsfcBlockPeriod(Sheet sheet, PeriodSum periodSum, Counter counter, Counter number,
                                             Map<String, Map<String, CellStyle>> styles,
                                             List<String> sumTypes, String[] columnNames) {
        boolean srcsIncluded = !sumTypes.contains("sumsOnly");

        log.debug("Standard report (period) ssfcBlock started for {}. Row {}",
                periodSum.getName(), counter.getCounter());

        // select group
        String group = selectGroupPeriod(periodSum.getClass().getSimpleName());

        // set group header
        if (sumTypes.contains(group) && srcsIncluded) {
            Row row = sheet.createRow(counter.incrementAndGet());
            IntStream.range(0, columnNames.length).forEach(i -> row.createCell(i)
                    .setCellStyle(styles.get(group + " no borders").get("string")));
            styleTopBottomDoubleBorder(sheet, row.getRowNum(), row.getRowNum(),
                    0, columnNames.length - 1);
            row.getCell(1).setCellValue(periodSum.getName());
        }

        // set data
        if (periodSum.hasSubs()) {
            periodSum.getPeriods().forEach(period -> this.formStandardSsfcBlockPeriod(sheet, period, counter,
                    number, styles, sumTypes, columnNames));
        } else if (srcsIncluded) {
            formGroupBlockPeriod(((SrcPeriodSum) periodSum).getSrcName(), sheet, periodSum.getAvgData(),
                    counter, number,
                    styles.get("base").get("integer"),
                    styles.get("base").get("string"),
                    styles.get("base").get("threeDigits"),
                    styles.get("base").get("twoDigits"));
        }

        log.debug("Standard report (period) sum footer started for {}. Row {}",
                periodSum.getName(), counter.getCounter());

        // set footer sums
        if (sumTypes.contains(group)) {
            formGroupBlockPeriod(periodSum.getName(), sheet, periodSum.getAvgData(), counter, null,
                    styles.get(group).get("integer"),
                    styles.get(group).get("string"),
                    styles.get(group).get("threeDigits"),
                    styles.get(group).get("twoDigits"));
        }
    }

    private String selectGroupPeriod(String className) {
        String group;

        if (className.equals(TzPeriodSum.class.getSimpleName())) {
            group = "tz";
        } else if (className.equals(SrcPeriodSum.class.getSimpleName())) {
            group = "base";
        } else {
            group = "branch";
        }

        return group;
    }


    private String[] getColumnNames(List<YearMonth> yearMonths) {
        ArrayList<String> columns = new ArrayList<String>(allSsfcsPeriodBaseColumns);

        String[] monthNames = {"Год", "Январь", "Февраль", "Март", "Апрель",
                "Май", "Июнь", "Июль", "Август", "Сентябрь",
                "Октябрь", "Ноябрь", "Декабрь"};

        yearMonths.forEach(ym -> {
            columns.add(String.format("%s %s", monthNames[ym.month()], ym.year()));
        });

        log.info("Column names for ssfc report (periods) formed. {} in total", columns.size());

        return columns.toArray(new String[0]);
    }

    private void formGroupBlockPeriod(String name, Sheet sheet, double[][] avgData,
                                      Counter counter, Counter number,
                                      CellStyle integerStyle, CellStyle stringStyle,
                                      CellStyle threeDigitsStyle, CellStyle twoDigitsStyle) {

        if (avgData[1][avgData[0].length - 1] == 0) {
            formGroupDataPeriod(name, sheet, avgData, FUEL_TYPE.DIESEL.getName(),
                    counter, number, integerStyle, stringStyle, threeDigitsStyle,
                    twoDigitsStyle, 2);
        } else if (avgData[2][avgData[0].length - 1] == 0) {
            formGroupDataPeriod(name, sheet, avgData, FUEL_TYPE.GAS.getName(),
                    counter, number, integerStyle, stringStyle, threeDigitsStyle,
                    twoDigitsStyle, 1);
        } else {
            formGroupDataPeriod(String.format("%s (газ + дизель)", name), sheet, avgData,
                    "газ + дизель", counter, number, integerStyle, stringStyle,
                    threeDigitsStyle, twoDigitsStyle, 0);
            formGroupDataPeriod(String.format("%s (газ)", name), sheet, avgData,
                    FUEL_TYPE.GAS.getName(), counter, number, integerStyle, stringStyle,
                    threeDigitsStyle, twoDigitsStyle, 1);
            formGroupDataPeriod(String.format("%s (дизель)", name), sheet, avgData,
                    FUEL_TYPE.DIESEL.getName(), counter, number, integerStyle, stringStyle,
                    threeDigitsStyle, twoDigitsStyle, 2);
        }
    }

    private void formGroupDataPeriod(String name, Sheet sheet, double[][] avgData, String fuelType,
                                     Counter counter, Counter number,
                                     CellStyle integerStyle, CellStyle stringStyle,
                                     CellStyle threeDigitsStyle, CellStyle twoDigitsStyle,
                                     Integer indexAddition) {

        log.debug("Standard report (period) sum group data started for {}. Row {}",
                name, counter.getCounter());

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
        if (Objects.isNull(number)) {
            createCell(currentRow, 0, "Итого", stringStyle);
        } else {
            cell.setCellValue(number.incrementAndGet());
            cell.setCellStyle(integerStyle);
        }

        createCell(currentRow, 1, name, stringStyle);
        createCell(currentRow, 2, fuelType, stringStyle);

        // set ssfc data row names and units
        IntStream.range(0, ssfcRows.length).forEach(i -> {
            createCell(sheet.getRow(counter.getCounter() + i), 3,
                    ssfcRows[i], stringStyle);
            createCell(sheet.getRow(counter.getCounter() + i), 4,
                    ssfcUnits[i], stringStyle);
        });

        // set ssfc data
        IntStream.range(0, avgData[0].length - 1).forEach(i ->
                setSsfcMonthDataCellsStd(sheet,
                        counter.getCounter(),
                        6 + i, // data set in 6 - 17 cells
                        avgData[0 + indexAddition][i],
                        avgData[3 + indexAddition][i],
                        avgData[6 + indexAddition][i],
                        avgData[9 + indexAddition][i],
                        avgData[12 + indexAddition][i],
                        avgData[15 + indexAddition][i],
                        threeDigitsStyle,
                        twoDigitsStyle));

        // set source summary data
        setSsfcMonthDataCellsStd(sheet,
                counter.getCounter(),
                5,
                avgData[0 + indexAddition][avgData[0].length - 1],
                avgData[3 + indexAddition][avgData[0].length - 1],
                avgData[6 + indexAddition][avgData[0].length - 1],
                avgData[9 + indexAddition][avgData[0].length - 1],
                avgData[12 + indexAddition][avgData[0].length - 1],
                avgData[15 + indexAddition][avgData[0].length - 1],
                threeDigitsStyle,
                twoDigitsStyle);

        // set top and bottom double borders
        // Базовые колонки allSsfcsPeriodBaseColumns + периоды (без суммарных данных) avgData[0].length - 1 и
        // (-1) для индекса
        styleTopBottomDoubleBorder(sheet, counter.getCounter(),
                counter.getCounter() + ssfcRows.length - 1,
                0, avgData[0].length + allSsfcsPeriodBaseColumns.size() - 2);

        counter.increment(ssfcRows.length);

        log.debug("Standard report (period) sum group data set for {}. Row {}",
                name, counter.getCounter());
    }
}