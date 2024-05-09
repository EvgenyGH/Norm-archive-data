package ru.bk.j3000.normarchivedata.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import ru.bk.j3000.normarchivedata.model.Source;
import ru.bk.j3000.normarchivedata.model.SourceProperty;
import ru.bk.j3000.normarchivedata.model.SrcPropertyId;
import ru.bk.j3000.normarchivedata.model.dto.SourcePropertyDTO;

import java.util.List;
import java.util.UUID;

public interface SourcePropertyService {
    List<SourcePropertyDTO> findAllPropDTOByYear(Integer reportYear);

    void deleteSrcPropertyById(UUID srcId, Integer year);

    SourcePropertyDTO getSourcePropertyById(UUID srcId, Integer year);

    void updateSourceProperty(SourcePropertyDTO sourcePropertyDTO);

    void addSourceProperty(SourcePropertyDTO sourcePropertyDTO);

    Resource getSourcePropertyTemplate();

    void uploadSourceProperties(MultipartFile file, Integer year);

    List<SourceProperty> findAllPropByYear(Integer reportYear);

    SourceProperty getSourcePropertyById(SrcPropertyId srcPropId);

    List<Source> findAllSourcesByYear(Integer year);
}
