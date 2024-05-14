package ru.bk.j3000.normarchivedata.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.apache.poi.ss.usermodel.*;
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
            CellStyle headerPrimary = wb.createCellStyle();
            //alignment
            headerPrimary.setAlignment(HorizontalAlignment.CENTER);
            headerPrimary.setVerticalAlignment(VerticalAlignment.CENTER);
            //border
            headerPrimary.setBorderBottom(BorderStyle.THIN);
            headerPrimary.setBorderLeft(BorderStyle.THIN);
            headerPrimary.setBorderRight(BorderStyle.THIN);
            headerPrimary.setBorderTop(BorderStyle.THIN);
            headerPrimary.setLeftBorderColor(IndexedColors.BLACK.getIndex());
            headerPrimary.setRightBorderColor(IndexedColors.BLACK.getIndex());
            headerPrimary.setTopBorderColor(IndexedColors.BLACK.getIndex());
            headerPrimary.setBottomBorderColor(IndexedColors.BLACK.getIndex());
            //fill
            //headerPrimary.setFillForegroundColor(new XSSFColor(new java.awt.Color(255, 204, 153), new DefaultIndexedColorMap()));
            headerPrimary.setFillForegroundColor(IndexedColors.TAN.getIndex());
            headerPrimary.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            //font
            Font font = wb.createFont();
            font.setFontHeightInPoints((short) 12);
            font.setFontName("Times New Roman");
            font.setBold(true);
            font.setColor(IndexedColors.BLACK.getIndex());
            headerPrimary.setFont(font);
            //


            // DataFormatter df = new DataFormatter();
            // CreationHelper helper = wb.getCreationHelper();
            // RegionUtil
            // CellUtil
            // CellRangeUtil
            // CellRefUtil
            // sheet.addMergedRegion(new CellRangeAddress(1, //first row (0-based)1, //last row  (0-based)1, //first column (0-based)2  //last column  (0-based)));

            CellStyle headerSecondary = wb.createCellStyle();
            CellStyle cellText = wb.createCellStyle();
            CellStyle cellNumber = wb.createCellStyle();
            CellStyle cellDecimal = wb.createCellStyle();


            Sheet sheet = wb.createSheet("Sources");
            Row row = sheet.createRow(0);
            row.createCell(5).setCellValue("UUID7");
            row.setRowStyle(headerPrimary);


            //set headers
            IntStream.rangeClosed(0, allSrcColumns.length - 1)
                    .forEach(i -> row.createCell(i, CellType.STRING).setCellValue(allSrcColumns[i]));
            // set header style


            FileOutputStream outputStream = new FileOutputStream(fileLocation);
            wb.write(outputStream);

            resource = new ClassPathResource(fileLocation);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        log.info("All sources report created.");

        return resource;
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
