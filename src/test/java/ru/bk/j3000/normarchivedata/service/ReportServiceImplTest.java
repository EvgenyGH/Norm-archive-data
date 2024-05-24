package ru.bk.j3000.normarchivedata.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.bk.j3000.normarchivedata.model.StandardSFC;
import ru.bk.j3000.normarchivedata.util.ssfcsum.SsfcSumTZ;
import ru.bk.j3000.normarchivedata.util.ssfcsum.SsfcSummary;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Transactional
@Sql(scripts = {"/testData.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Slf4j
public class ReportServiceImplTest {
    private final ReportService reportService;
    private final StandardSFCService ssfcService;

    @Test
    @DisplayName("Test 1")
    public void test1() {
        List<StandardSFC> allSsfcs = ssfcService.findAllSsfcByYear(2024);

        SsfcSummary tzBrSrcSum = new SsfcSumTZ(allSsfcs, "base", "base");

        double[][] data = tzBrSrcSum.avgData();
//
//        for (int i = 0; i < data.length; i++) {
//            for (int j = 0; j < data[i].length; j++) {
//                System.out.print(data[i][j] + ", ");
//            }
//            System.out.println();
//        }

//        SsfcSummary srcSumm = new SsfcSumSrc(allSsfcs);
//
//        System.out.println(Arrays.toString(srcSumm.avgData()[0]));
//        System.out.println(Arrays.toString(srcSumm.avgData()[1]));
//        System.out.println(Arrays.toString(srcSumm.avgData()[2]));
//        System.out.println(Arrays.toString(srcSumm.avgData()[3]));
//        System.out.println(Arrays.toString(srcSumm.avgData()[4]));
//        System.out.println(Arrays.toString(srcSumm.avgData()[5]));
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Sheet1");

            for (int i = 0; i < data.length; i++) {
                Row row = sheet.createRow(i);
                for (int j = 0; j < data[i].length; j++) {
                    row.createCell(j).setCellValue(data[i][j]);
                }
            }

            wb.write(new FileOutputStream("./test.xlsx"));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void test2() {
        double[][] arr = new double[2][5];
        System.out.println("0 * 0" + 0d * 0d);
    }
}
