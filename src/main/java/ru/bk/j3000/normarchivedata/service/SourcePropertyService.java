package ru.bk.j3000.normarchivedata.service;

import ru.bk.j3000.normarchivedata.model.dto.SourcePropertiesDTO;

import java.util.List;

public interface SourcePropertyService {
    List<SourcePropertiesDTO> findAllPropByYear(Integer reportYear);
}
