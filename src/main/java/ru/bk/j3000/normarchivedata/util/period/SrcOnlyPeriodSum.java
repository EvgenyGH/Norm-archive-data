package ru.bk.j3000.normarchivedata.util.period;

import ru.bk.j3000.normarchivedata.model.StandardSFC;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SrcOnlyPeriodSum extends PeriodSumBase {
    public SrcOnlyPeriodSum(List<StandardSFC> ssfcs, List<YearMonth> yearMonths) {
        super("base");
        this.periods = formPeriods(ssfcs, yearMonths);
        this.avgData = calcAvgData();
    }

    private List<PeriodSum> formPeriods(List<StandardSFC> ssfcs, List<YearMonth> yearMonths) {

        return ssfcs.stream()
                .collect(Collectors.groupingBy(ssfc -> ssfc.getProperties().getId().getSource().getId(),
                        Collectors.toList()))
                .values().stream()
                .sorted(Comparator.comparing(list -> list.getFirst()
                        .getProperties().getId().getSource().getName()))
                .sorted(Comparator.comparing(list -> list.getFirst()
                        .getProperties().getId().getSource().getSourceType()))
                .map(list -> (PeriodSum) new SrcPeriodSum(list, yearMonths))
                .toList();
    }
}
