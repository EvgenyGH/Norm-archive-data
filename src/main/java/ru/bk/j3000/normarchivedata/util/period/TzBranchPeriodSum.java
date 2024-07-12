package ru.bk.j3000.normarchivedata.util.period;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TzBranchPeriodSum extends PeriodSumBase {

    public TzBranchPeriodSum(List<SrcPeriodSum> periods) {
        super("branches");
        setPeriods(formPeriods(periods));
    }

    private List<PeriodSum> formPeriods(List<SrcPeriodSum> periods) {

        return periods.stream()
                .collect(Collectors.groupingBy(SrcPeriodSum::getZoneId, Collectors.toList()))
                .values().stream()
                .sorted(Comparator.comparing(list -> list.getFirst().getZoneId()))
                .map(list -> (PeriodSum) new BranchPeriodSum(list))
                .toList();
    }
}
