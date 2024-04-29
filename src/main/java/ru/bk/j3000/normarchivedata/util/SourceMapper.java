package ru.bk.j3000.normarchivedata.util;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.bk.j3000.normarchivedata.model.Source;
import ru.bk.j3000.normarchivedata.model.dto.SourceAlterDTO;
import ru.bk.j3000.normarchivedata.service.SourceService;

@RequiredArgsConstructor
@Component
@Slf4j
public class SourceMapper {
    private final SourceService sourceService;

    public Source toSource(SourceAlterDTO dto) {
        Source source = sourceService.getSourceById(dto.getSourceId())
                .orElseThrow(() -> new EntityNotFoundException(String
                        .format("Source with id %s not found",
                                dto.getSourceId())));

        log.info("Source created from DTO (name={}, id={})",
                dto.getSourceName(), dto.getSourceId());

        return source;
    }
}
