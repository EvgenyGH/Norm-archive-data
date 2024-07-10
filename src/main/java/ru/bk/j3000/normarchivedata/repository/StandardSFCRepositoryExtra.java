package ru.bk.j3000.normarchivedata.repository;

import ru.bk.j3000.normarchivedata.model.StandardSFC;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface StandardSFCRepositoryExtra {
    List<StandardSFC> findAllSsfcByPeriods(Map<Integer, List<Integer>> periods, List<UUID> ids);
}