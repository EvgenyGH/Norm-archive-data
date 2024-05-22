package ru.bk.j3000.normarchivedata.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.bk.j3000.normarchivedata.model.StandardSFC;
import ru.bk.j3000.normarchivedata.model.dto.ssfcsum.SsfcSumSrc;
import ru.bk.j3000.normarchivedata.model.dto.ssfcsum.SsfcSummary;

import java.util.Arrays;
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

        //SsfcSummary tzBrSrcSum = new SsfcSumTZBranch(allSsfcs, "base", "base");

        //log.info("Generation: {} th. Gcal", tzBrSrcSum.avgData());

        SsfcSummary srcSumm = new SsfcSumSrc(allSsfcs);

        System.out.println(Arrays.toString(srcSumm.avgData()[0]));
        System.out.println(Arrays.toString(srcSumm.avgData()[1]));
        System.out.println(Arrays.toString(srcSumm.avgData()[2]));
        System.out.println(Arrays.toString(srcSumm.avgData()[3]));
        System.out.println(Arrays.toString(srcSumm.avgData()[4]));
        System.out.println(Arrays.toString(srcSumm.avgData()[5]));
    }
}
