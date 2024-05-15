package ru.bk.j3000.normarchivedata.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.io.IOException;
import java.util.Arrays;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Transactional
@Sql(scripts = {"/testData.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public class ReportServiceImplTest {
    private final ReportService reportService;

    @Test
    @DisplayName("Test 1")
    public void test1() {
        reportService.getAllSourcesReport();
    }

    @Test
    void test2() {
        try (Workbook wb = new XSSFWorkbook("./temp.xlsx")) {
            Row row = wb.getSheet("sources").getRow(0);

            XSSFCellStyle cs = (XSSFCellStyle) row.getCell(2).getCellStyle();
            XSSFColor c = cs.getFillForegroundColorColor();


            System.out.println(Arrays.toString(c.getRGB()));
            System.out.println(Arrays.toString(c.getARGB()));
            System.out.println(c.getCTColor());


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
