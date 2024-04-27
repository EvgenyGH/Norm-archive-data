package ru.bk.j3000.normarchivedata.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.bk.j3000.normarchivedata.model.dto.SourcePropertiesDTO;
import ru.bk.j3000.normarchivedata.repository.SourcePropertiesRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SourcePropertyServiceImpl implements SourcePropertyService {
    private final SourcePropertiesRepository srcPropRepository;

    @Override
    public List<SourcePropertiesDTO> findAllPropByYear(Integer reportYear) {
        List<SourcePropertiesDTO> properties = srcPropRepository.findAllPropByYear(reportYear);

        log.info("Found {} source properties for {} year.", properties.size(), reportYear);

        return properties;
    }
}
