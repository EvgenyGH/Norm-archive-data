package ru.bk.j3000.normarchivedata.util.period;

import java.util.List;

public interface PeriodSum {

    String getName();

    double[][] getAvgData();

    List<PeriodSum> getPeriods();

    boolean hasSubs();
}
