package ru.bk.j3000.normarchivedata.util.ssfcsum;

import ru.bk.j3000.normarchivedata.model.StandardSFC;

import java.util.List;
import java.util.stream.IntStream;

public class AvgDataHandler {
    public static double[][] avgDataOf(List<StandardSFC> allSsfcs) {
        return new SsfcSumSrc(allSsfcs).avgData();
    }

    public static double[] avgSingleSrcDataOf(List<StandardSFC> allSsfcs) {
        double[][] avgData = avgDataOf(allSsfcs);

        return IntStream.of(0, 3, 6, 9, 12, 15)
                .mapToDouble(i -> avgData[i][12])
                .toArray();
    }
}