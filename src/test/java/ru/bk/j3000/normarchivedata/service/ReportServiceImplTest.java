package ru.bk.j3000.normarchivedata.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.bk.j3000.normarchivedata.model.StandardSFC;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.stream.Collectors.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Transactional
@Sql(scripts = {"/testDataClean.sql", "/testData.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public class ReportServiceImplTest {
    private final EntityManager entityManager;

    @Test
    public void test() {
        List<String> periodsStr = List.of("2024.0", "2024.5", "2025.12", "2025.3", "2023.0", "2023.4");
        //     List<String> periodsStr = List.of( "2024.5", "2025.12", "2025.3", "2023.0", "2023.4");
        List<UUID> ids = List.of(UUID.fromString("a82b2c59-577d-4560-9176-909a3b21645c"));

        Map<Integer, List<Integer>> periods = periodsStr.stream()
                .collect(groupingBy(str -> Integer.parseInt(str.substring(0, 4)),
                        mapping(str -> Integer.parseInt(str.split("\\.")[1]),
                                toList())));

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<StandardSFC> query = criteriaBuilder.createQuery(StandardSFC.class);
        Root<StandardSFC> itemRoot = query.from(StandardSFC.class);

        List<Predicate> predicates = new LinkedList<>();


        for (var period : periods.entrySet()) {
            Predicate yearInRange = criteriaBuilder.equal(itemRoot.get("properties")
                    .get("id").get("year"), period.getKey());

            if (period.getValue().contains(0)) {
                predicates.add(yearInRange);
            } else {
                Predicate monthInRange = itemRoot.get("month")
                        .in(period.getValue().toArray(new Integer[0]));
                Predicate srcInRange = itemRoot.get("properties").get("id")
                        .get("source").get("id").in(ids);
                Predicate complete = criteriaBuilder.and(yearInRange, monthInRange, srcInRange);
                predicates.add(complete);
            }
        }

        Predicate fin = criteriaBuilder.or(predicates.toArray(new Predicate[]{}));

        query.where(fin);

        List<StandardSFC> list = entityManager.createQuery(query).getResultList();

        list.forEach(e -> {
            System.out.println(e.getProperties().getId().getYear());
            System.out.println(e.getMonth());
        });
    }
}
