package ru.bk.j3000.normarchivedata.util.ssfcsum;

import ru.bk.j3000.normarchivedata.model.StandardSFC;

import java.util.List;

public class AvgDataHandler {
    public static double[][] avgDataOf(List<StandardSFC> allSsfcs) {
        return new SsfcSumSrc(allSsfcs).avgData();
    }
}
