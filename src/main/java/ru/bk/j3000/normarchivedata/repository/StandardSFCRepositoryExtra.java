package ru.bk.j3000.normarchivedata.repository;

import ru.bk.j3000.normarchivedata.model.StandardSFC;
import ru.bk.j3000.normarchivedata.util.period.YearMonth;

import java.util.List;
import java.util.UUID;

public interface StandardSFCRepositoryExtra {
    List<StandardSFC> findAllSsfcByPeriods(List<YearMonth> yearMonths, List<UUID> ids);
}