package ru.bk.j3000.normarchivedata.util.ssfcsum;

import ru.bk.j3000.normarchivedata.model.StandardSFC;

import java.util.ArrayList;
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

    public static List<double[][]> avgDataToList(double[][] avgData) {
        ArrayList<double[][]> dataList;
        int size = 3;

        if (avgData[1][12] == 0 || avgData[2][12] == 0) {
            size = 1;
        }

        dataList = new ArrayList<>(size);

        IntStream.range(0, size).forEach(i -> dataList
                .add(new double[avgData.length / 3][avgData[0].length]));

        IntStream.range(0, avgData.length / 3).forEach(i -> IntStream.range(0, avgData[0].length)
                .forEach(j -> {
                    dataList.get(0)[i][j] = avgData[i * 3][j];
                    if (dataList.size() > 1) {
                        dataList.get(1)[i][j] = avgData[i * 3 + 1][j];
                        dataList.get(2)[i][j] = avgData[i * 3 + 2][j];
                    }
                })
        );

        return dataList;
    }
}