package ru.bk.j3000.normarchivedata.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import ru.bk.j3000.normarchivedata.model.Branch;
import ru.bk.j3000.normarchivedata.model.Source;
import ru.bk.j3000.normarchivedata.model.SourceProperty;
import ru.bk.j3000.normarchivedata.model.TariffZone;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.apache.poi.ss.util.CellUtil.createCell;


@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ReportServiceImpl implements ReportService {
    private final String[] allSrcColumns = {"№ п/п", "Тип источника", "Источник", "Адрес источника"};
    private final String[] srcTemplateColumns = {"UUID", "Источник", "Адрес источника",
            "Тип источника", "Комментарии"};

    private final String[] allBranchesColumns = {"№ п./п.", "Название филиала"};
    private final String[] brTemplateColumns = {"ID", "Название филиала", "Комментарии"};

    private final String[] allTariffZonesColumns = {"№ п./п.", "Название тарифной зоны"};
    private final String[] tzTemplateColumns = {"ID", "Название тарифной зоны", "Комментарии"};

    private final String[] allSrcPropColumns = {"№ п./п.", "Источник", "Тарифная зона", "Филиал"};
    private final String[] srcPropTemplateColumns = {"ID источника", "ID Филиала",
            "ID тарифной зоны", "Комментарии"};

    private final String[] ssfcsTemplateColumns = {"№ п/п",
            "Наименование источника тепловой энергии",
            "Вид топлива", "Наименование показателя",
            "Единицы измерения", "Id источника",
            "Id вида топлива", "Январь", "Февраль",
            "Март", "Апрель", "Май", "Июнь", "Июль",
            "Август", "Сентябрь", "Октябрь", "Ноябрь",
            "Декабрь", "2024 год", "Комментарии"};

    private final String[] ssfcRows = {"Выработка тепловой энергии, Гкал",
            "Тепловая энергия на собственные нужды, Гкал",
            "Тепловая энергия на собственные нужды, %",
            "Отпуск тепловой энергии с коллекторов источников, Гкал",
            "УРУТ на выработу тепловой энергии, кг у.т./Гкал",
            "УРУТ на отпуск тепловой энергии, кг у.т./Гкал"};

    private final String[] units = {"Гкал", "Гкал", "%", "тыс. Гкал", "кг у.т./Гкал", "кг у.т./Гкал"};

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
            case "template" -> getSsfcTemplateReport(year);
            case "standard" -> getAllSsfcReport(year);
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
            throw new RuntimeException(e);
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
            CellStyle integerStyle = getIntegerStyle(wb.createCellStyle(), fontData);

            // set headers
            Row headerRow = sheet.createRow(0);
            IntStream.rangeClosed(0, allSrcColumns.length - 1)
                    .forEach(i -> createCell(headerRow, i, allSrcColumns[i], headerStylePrimary));

            // set data
            for (int i = 0; i < sources.size(); i++) {
                Row row = sheet.createRow(i + 1);
                Source source = sources.get(i);

                Cell cell = row.createCell(0, CellType.NUMERIC);
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
            throw new RuntimeException(e);
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
            CellStyle integerStyle = getIntegerStyle(wb.createCellStyle(), fontData);

            // set headers
            Row headerRow = sheet.createRow(0);
            IntStream.rangeClosed(0, brTemplateColumns.length - 1)
                    .forEach(i -> createCell(headerRow, i, brTemplateColumns[i],
                            i == 2 ? headerStyleSecondary : headerStylePrimary));

            // set data
            for (int i = 0; i < branches.size(); i++) {
                Row row = sheet.createRow(i + 1);
                Branch branch = branches.get(i);

                Cell cell = row.createCell(0, CellType.NUMERIC);
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
            throw new RuntimeException(e);
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
            CellStyle integerStyle = getIntegerStyle(wb.createCellStyle(), fontData);

            // set headers
            Row headerRow = sheet.createRow(0);
            IntStream.rangeClosed(0, allBranchesColumns.length - 1)
                    .forEach(i -> createCell(headerRow, i, allBranchesColumns[i], headerStylePrimary));

            // set data
            for (int i = 0; i < branches.size(); i++) {
                Row row = sheet.createRow(i + 1);
                Branch branch = branches.get(i);

                Cell cell = row.createCell(0, CellType.NUMERIC);
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
            throw new RuntimeException(e);
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
            CellStyle integerStyle = getIntegerStyle(wb.createCellStyle(), fontData);

            // set headers
            Row headerRow = sheet.createRow(0);
            IntStream.rangeClosed(0, tzTemplateColumns.length - 1)
                    .forEach(i -> createCell(headerRow, i, tzTemplateColumns[i],
                            i == 2 ? headerStyleSecondary : headerStylePrimary));

            // set data
            for (int i = 0; i < tzs.size(); i++) {
                Row row = sheet.createRow(i + 1);
                TariffZone tz = tzs.get(i);

                Cell cell = row.createCell(0, CellType.NUMERIC);
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
            throw new RuntimeException(e);
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
            CellStyle integerStyle = getIntegerStyle(wb.createCellStyle(), fontData);

            // set headers
            Row headerRow = sheet.createRow(0);
            IntStream.rangeClosed(0, allTariffZonesColumns.length - 1)
                    .forEach(i -> createCell(headerRow, i, allTariffZonesColumns[i], headerStylePrimary));

            // set data
            for (int i = 0; i < tzs.size(); i++) {
                Row row = sheet.createRow(i + 1);
                TariffZone tz = tzs.get(i);

                Cell cell = row.createCell(0, CellType.NUMERIC);
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
            throw new RuntimeException(e);
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
            CellStyle integerStyle = getIntegerStyle(wb.createCellStyle(), fontData);
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
                Cell cell = row.createCell(1, CellType.NUMERIC);
                cell.setCellValue(srcProp.getBranch().getId());
                cell.setCellStyle(integerStyle);

                cell = row.createCell(2, CellType.NUMERIC);
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
            throw new RuntimeException(e);
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
            CellStyle integerStyle = getIntegerStyle(wb.createCellStyle(), fontData);
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

                Cell cell = row.createCell(0, CellType.NUMERIC);
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
            throw new RuntimeException(e);
        }

        return resource;
    }

    @Override
    public Resource getSsfcTemplateReport(Integer year) {
        //        CellStyle headerStylePrimary = getPrimaryHeaderStyle(wb.createCellStyle(), fontHeader);
//        CellStyle headerStyleSecondary = getSecondaryHeaderStyle(wb.createCellStyle(), fontHeader);
//        CellStyle stringStyle = getStringStyle(wb.createCellStyle(), fontData);
//        CellStyle integerStyle = getIntegerStyle(wb.createCellStyle(), fontData);
//        CellStyle decimalStyle = getDecimalStyle(wb.createCellStyle(), fontData, 3);
        throw new NotImplementedException("Not implemented yet.");
    }

    @Override
    public Resource getAllSsfcReport(Integer year) {
        throw new NotImplementedException("Not implemented yet.");
    }

    private CellStyle getIntegerStyle(CellStyle cellStyle, Font font) {
        getBaseStyle(cellStyle, font)
                .setDataFormat((short) BuiltinFormats.getBuiltinFormat("# ##0"));

        return cellStyle;
    }

    private CellStyle getDecimalStyle(CellStyle cellStyle, Font font, Integer digits) {
        getBaseStyle(cellStyle, font)
                .setDataFormat((short) BuiltinFormats
                        .getBuiltinFormat(String.format("# ##0.%s", Strings.repeat("0", digits)))
                );

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
        font.setFontHeightInPoints((short) 16);
        font.setColor(IndexedColors.BLACK.getIndex());

        return font;
    }
}
