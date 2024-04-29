package ru.bk.j3000.normarchivedata.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.bk.j3000.normarchivedata.model.SourceProperty;
import ru.bk.j3000.normarchivedata.model.dto.SourcePropertyDTO;
import ru.bk.j3000.normarchivedata.repository.SourcePropertyRepository;
import ru.bk.j3000.normarchivedata.util.SourcePropertiesMapper;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SourcePropertyServiceImpl implements SourcePropertyService {
    private final SourcePropertyRepository srcPropRepository;
    private final SourcePropertiesMapper mapper;

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
                property.getSource().getSourceName(),
                property.getSource().getSourceId(),
                year);

        return property;
    }

    @Override
    public void updateSourceProperty(SourcePropertyDTO sourcePropertyDTO) {
        SourceProperty property = mapper.toSourceProperty(sourcePropertyDTO);

        srcPropRepository.findById(property.getId())
                .ifPresentOrElse(p -> srcPropRepository.save(property),
                        () -> {
                            throw new EntityNotFoundException(String
                                    .format("Source property not found. Id %s", property.getId()));
                        });

        log.info("Source property for source {} id {} year {} updated.",
                property.getId().getSource().getName(),
                property.getId().getSource().getId(),
                property.getId().getYear());
    }

    @Override
    public void addSourceProperty(SourcePropertyDTO sourcePropertyDTO) {
        SourceProperty property = mapper.toSourceProperty(sourcePropertyDTO);

        srcPropRepository.findById(property.getId())
                .ifPresentOrElse(p -> {
                            throw new EntityExistsException(String
                                    .format("Source property already exists. Id %s", p.getId()));
                        },
                        () -> srcPropRepository.save(property));

        log.info("Source property already exists. ({}) Id {}.",
                property.getId().getSource().getName(),
                property.getId());
    }
}
