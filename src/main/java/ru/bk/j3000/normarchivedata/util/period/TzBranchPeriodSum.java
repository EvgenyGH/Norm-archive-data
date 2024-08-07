package ru.bk.j3000.normarchivedata.util.period;

import ru.bk.j3000.normarchivedata.model.StandardSFC;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TzBranchPeriodSum extends PeriodSumBase {

    public TzBranchPeriodSum(List<StandardSFC> ssfcs, List<YearMonth> yearMonths) {
        super("base");
        this.periods = formPeriods(ssfcs, yearMonths);
        this.avgData = calcAvgData();
    }

    private List<PeriodSum> formPeriods(List<StandardSFC> ssfcs, List<YearMonth> yearMonths) {

        return ssfcs.stream()
                .collect(Collectors.groupingBy(ssfc -> ssfc.getProperties().getTariffZone().getId(),
                        Collectors.toList()))
                .values().stream()
                .sorted(Comparator.comparing(list -> list.getFirst().getProperties().getTariffZone().getId()))
                .map(list -> (PeriodSum) new TzPeriodSum(list, yearMonths))
                .toList();
    }
}
