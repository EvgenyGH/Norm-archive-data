package ru.bk.j3000.normarchivedata.util.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.bk.j3000.normarchivedata.model.*;
import ru.bk.j3000.normarchivedata.model.dto.SourcePropertyDTO;

@Component
@RequiredArgsConstructor
@Slf4j
public class SourcePropertiesMapper {
    private final SourceMapper sourceMapper;
    private final BranchMapper branchMapper;
    private final TariffZoneMapper tariffZoneMapper;

    public SourceProperty toSourceProperty(SourcePropertyDTO dto) {
        Source source = sourceMapper.toSource(dto.getSource());
        SrcPropertyId id = new SrcPropertyId(source, dto.getYear());
        Branch branch = branchMapper.toBranch(dto.getBranch());
        TariffZone tariffZone = tariffZoneMapper.toTariffZone(dto.getTariffZone());

        SourceProperty sourceProperty = new SourceProperty(id, branch, tariffZone);

        log.info("Source property created from DTO (id {})", sourceProperty.getId());

        return sourceProperty;
    }
}
