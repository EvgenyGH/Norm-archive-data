package ru.bk.j3000.normarchivedata.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.bk.j3000.normarchivedata.model.dto.SourcePropertyDTO;
import ru.bk.j3000.normarchivedata.repository.SourcePropertyRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SourcePropertyServiceImpl implements SourcePropertyService {
    private final SourcePropertyRepository srcPropRepository;

    @Override
    public List<SourcePropertyDTO> findAllPropByYear(Integer reportYear) {
        List<SourcePropertyDTO> properties = srcPropRepository.findAllPropDTOByYear(reportYear);

        log.info("Found {} source properties for {} year.", properties.size(), reportYear);

        return properties;
    }

    @Override
    @Transactional
    public void deleteSrcPropertyById(UUID srcId, Integer year) {
        srcPropRepository.deleteBySrcIdAndYear(srcId, year);

        log.info("Deleted source property. Source id {}, year {}.", srcId, year);
    }

    @Override
    public SourcePropertyDTO getSourcePropertyById(UUID srcId, Integer year) {
        SourcePropertyDTO property = srcPropRepository.getSourcePropertyDTOByIdAndYear(srcId, year);

        log.info("Found source property. Source name {}, source id {}, year {}.",
                property.getSourceName(),
                property.getSourceId(),
                year);

        return property;
    }
}
