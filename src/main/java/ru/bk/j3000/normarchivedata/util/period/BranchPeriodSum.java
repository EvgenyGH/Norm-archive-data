package ru.bk.j3000.normarchivedata.util.period;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BranchPeriodSum extends PeriodSumBase {
    public BranchPeriodSum(List<SrcPeriodSum> periods) {
        super("sources");
        setPeriods(formPeriods(periods));

    }

    private List<PeriodSum> formPeriods(List<SrcPeriodSum> periods) {

        return periods.stream()
                .collect(Collectors.groupingBy(SrcPeriodSum::getSrcId, Collectors.toList()))
                .values().stream()
                .sorted(Comparator.comparing(list -> list.getFirst().getSrcName()))
                .sorted(Comparator.comparing(list -> list.getFirst().getType()))
                .map(list -> (PeriodSum) list)
                .toList();
    }
}
