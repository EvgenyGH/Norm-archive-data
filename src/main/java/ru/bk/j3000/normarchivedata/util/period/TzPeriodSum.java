package ru.bk.j3000.normarchivedata.util.period;

import ru.bk.j3000.normarchivedata.model.StandardSFC;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TzPeriodSum extends PeriodSumBase {

    public TzPeriodSum(List<StandardSFC> ssfcs, List<YearMonth> yearMonths) {
        super(ssfcs.getFirst().getProperties().getTariffZone().getZoneName());
        this.periods = formPeriods(ssfcs, yearMonths);
        this.avgData = calcAvgData();
    }

    private List<PeriodSum> formPeriods(List<StandardSFC> ssfcs, List<YearMonth> yearMonths) {

        return ssfcs.stream()
                .collect(Collectors.groupingBy(ssfc -> ssfc.getProperties().getBranch().getId(),
                        Collectors.toList()))
                .values().stream()
                .sorted(Comparator.comparing(list -> list.getFirst().getProperties().getBranch().getId()))
                .map(list -> (PeriodSum) new BranchPeriodSum(list, yearMonths))
                .toList();
    }
}
