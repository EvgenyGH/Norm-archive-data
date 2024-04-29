package ru.bk.j3000.normarchivedata.service;

import ru.bk.j3000.normarchivedata.model.dto.SourcePropertyDTO;

import java.util.List;
import java.util.UUID;

public interface SourcePropertyService {
    List<SourcePropertyDTO> findAllPropByYear(Integer reportYear);

    void deleteSrcPropertyById(UUID srcId, Integer year);

    SourcePropertyDTO getSourcePropertyById(UUID srcId, Integer year);

    void updateSourceProperty(SourcePropertyDTO sourcePropertyDTO);

    void addSourceProperty(SourcePropertyDTO sourcePropertyDTO);
}
