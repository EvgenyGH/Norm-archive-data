package ru.bk.j3000.normarchivedata.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.stream.IntStream;


@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ReportServiceImpl implements ReportService {
    private final String[] allSrcColumns = {"UUID", "Источник", "Адрес источника",
            "Тип источника"};
    private final SourceService sourceService;


    @Override
    public Resource getAllSourcesReport() {
        Resource resource;
        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        String fileLocation = path.substring(0, path.length() - 1) + "temp.xlsx";


        try (Workbook wb = new XSSFWorkbook()) {
            Font font = wb.createFont();

            CellStyle headerStylePrimary = getPrimaryHeaderStyle(wb.createCellStyle(), font);
            CellStyle headerStyleSecondary = getSecondaryHeaderStyle(wb.createCellStyle(), font);
            CellStyle stringStyle = getStringStyle(wb.createCellStyle(), font);
            CellStyle integerStyle = getIntegerStyle(wb.createCellStyle(), font);
            CellStyle decimalStyle = getDecimalStyle(wb.createCellStyle(), font, 3);

            // DataFormatter df = new DataFormatter();
            // CreationHelper helper = wb.getCreationHelper();
            // RegionUtil
            // CellUtil
            // CellRangeUtil
            // CellRefUtil
            // sheet.addMergedRegion(new CellRangeAddress(1, //first row (0-based)1, //last row  (0-based)1, //first column (0-based)2  //last column  (0-based)));

            Sheet sheet = wb.createSheet("Sources");
            Row row = sheet.createRow(0);

            //setup headers
            IntStream.rangeClosed(0, allSrcColumns.length - 1)
                    .forEach(i -> CellUtil.createCell(row, i, allSrcColumns[i], headerStylePrimary));

            // autosize columns
            IntStream.rangeClosed(0, allSrcColumns.length - 1)
                    .forEach(sheet::autoSizeColumn);

            sheet.autoSizeColumn(2);

            FileOutputStream outputStream = new FileOutputStream(fileLocation);
            wb.write(outputStream);

            resource = new ClassPathResource(fileLocation);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        log.info("All sources report created.");

        return resource;
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
        cellStyle.setFillForegroundColor(IndexedColors.TAN.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        return cellStyle;
    }

    private CellStyle getPrimaryHeaderStyle(CellStyle cellStyle, Font font) {
        font.setBold(true);
        getStringStyle(cellStyle, font);
        //fill
        //headerPrimary.setFillForegroundColor(new XSSFColor(new java.awt.Color(255, 204, 153), new DefaultIndexedColorMap()));
        cellStyle.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

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
        font.setFontHeightInPoints((short) 12);
        font.setFontName("Times New Roman");
        font.setColor(IndexedColors.BLACK.getIndex());
        cellStyle.setFont(font);

        return cellStyle;
    }

    @Override
    public Resource getAllBranchesReport() {
        throw new NotImplementedException("Not implemented yet.");
    }

    @Override
    public Resource getAllTariffZonesReport() {
        throw new NotImplementedException("Not implemented yet.");
    }

    @Override
    public Resource getAllSrcPropsReport() {
        throw new NotImplementedException("Not implemented yet.");
    }

    @Override
    public Resource getAllSsfcsReport() {

        throw new NotImplementedException("Not implemented yet.");
    }
}
