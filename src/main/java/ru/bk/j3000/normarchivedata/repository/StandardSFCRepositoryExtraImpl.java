package ru.bk.j3000.normarchivedata.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.bk.j3000.normarchivedata.model.StandardSFC;
import ru.bk.j3000.normarchivedata.util.period.YearMonth;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@Slf4j
public class StandardSFCRepositoryExtraImpl implements StandardSFCRepositoryExtra {
    private final EntityManager entityManager;

    @Override
    public List<StandardSFC> findAllSsfcByPeriods(List<YearMonth> yearMonths, List<UUID> ids) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<StandardSFC> query = criteriaBuilder.createQuery(StandardSFC.class);
        Root<StandardSFC> itemRoot = query.from(StandardSFC.class);
        List<Predicate> predicates = new LinkedList<>();

        Map<Integer, List<Integer>> periods = yearMonths.stream()
                .collect(Collectors.groupingBy(YearMonth::year,
                        Collectors.mapping(YearMonth::month, toList())));

        for (var period : periods.entrySet()) {
            Predicate yearInRange = criteriaBuilder.equal(itemRoot.get("properties")
                    .get("id").get("year"), period.getKey());

            if (period.getValue().contains(0)) {
                if (ids.isEmpty()) {
                    predicates.add(yearInRange);
                } else {
                    Predicate srcInRange = itemRoot.get("properties").get("id")
                            .get("source").get("id").in(ids);
                    predicates.add(criteriaBuilder.and(yearInRange, srcInRange));
                }
            } else {
                Predicate monthInRange = itemRoot.get("month")
                        .in(period.getValue().toArray(new Integer[0]));

                if (ids.isEmpty()) {
                    predicates.add(criteriaBuilder.and(yearInRange, monthInRange));
                } else {
                    Predicate srcInRange = itemRoot.get("properties").get("id")
                            .get("source").get("id").in(ids);
                    predicates.add(criteriaBuilder.and(yearInRange, monthInRange, srcInRange));
                }
            }
        }

        Predicate conditions = criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        query.where(conditions);
        List<StandardSFC> ssfcs = entityManager.createQuery(query).getResultList();

        log.info("Ssfc list formed. {} in total.", ssfcs.size());

        return ssfcs;
    }
}