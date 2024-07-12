package ru.bk.j3000.normarchivedata.util.period;

import java.util.List;

public interface PeriodSum {

    String getName();

    double[][] getAvgData();

    void setPeriods(List<PeriodSum> periods);

    boolean hasSubs();
}
