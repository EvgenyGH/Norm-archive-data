package ru.bk.j3000.normarchivedata.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.bk.j3000.normarchivedata.util.period.TzBranchPeriodSum;
import ru.bk.j3000.normarchivedata.util.period.YearMonth;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.IntStream;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Transactional
@Sql(scripts = {"/testDataClean.sql", "/testData.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public class ReportServiceImplTest {
    private final StandardSFCService service;

    @Test
    public void test() {
        var ssfcs = service.findAllSsfcByYearAndSrcId(2024,
                UUID.fromString("46fafaef-cc8d-4cf1-b490-bd475c71a0e3"));
        var sum = new TzBranchPeriodSum(ssfcs, IntStream.rangeClosed(0, 1)
                .mapToObj(i -> new YearMonth(2024, i)).toList());

        IntStream.range(0, sum.getAvgData().length).forEach(i -> {
            System.out.println(Arrays.toString(sum.getAvgData()[i]));
        });
    }

    @Test
    public void test2() {
        System.out.println(new YearMonth(12, 25));
    }
}
