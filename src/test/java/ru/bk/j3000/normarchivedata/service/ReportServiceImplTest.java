package ru.bk.j3000.normarchivedata.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.bk.j3000.normarchivedata.model.StandardSFC;
import ru.bk.j3000.normarchivedata.model.dto.ssfcsum.SsfcSumBranch;
import ru.bk.j3000.normarchivedata.model.dto.ssfcsum.SsfcSumTZ;
import ru.bk.j3000.normarchivedata.model.dto.ssfcsum.SsfcSumTZBranch;
import ru.bk.j3000.normarchivedata.model.dto.ssfcsum.SsfcSummary;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Transactional
@Sql(scripts = {"/testData.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public class ReportServiceImplTest {
    private final ReportService reportService;
    private final StandardSFCService ssfcService;

    @Test
    @DisplayName("Test 1")
    public void test1() {
        List<StandardSFC> allSsfcs = ssfcService.findAllSsfcByYear(2024);

        SsfcSummary tzBrSrcSum = new SsfcSumTZBranch(allSsfcs, "base", "base");
        SsfcSummary tzSum = new SsfcSumTZ(allSsfcs, "base", "base");
        SsfcSummary brSum = new SsfcSumBranch(allSsfcs, "base", "base");

        assertThat(tzBrSrcSum.hasSub()).isTrue();
    }
}
