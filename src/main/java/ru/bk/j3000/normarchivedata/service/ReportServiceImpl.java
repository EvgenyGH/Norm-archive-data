package ru.bk.j3000.normarchivedata.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
            Sheet sheet = wb.createSheet("Sources");
            Row row = sheet.createRow(0);

            //set headers
            IntStream.rangeClosed(0, allSrcColumns.length - 1)
                    .forEach(i -> row.createCell(i, CellType.STRING).setCellValue(allSrcColumns[i]));

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
