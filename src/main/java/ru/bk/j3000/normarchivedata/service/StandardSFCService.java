package ru.bk.j3000.normarchivedata.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import ru.bk.j3000.normarchivedata.model.FUEL_TYPE;
import ru.bk.j3000.normarchivedata.model.Source;
import ru.bk.j3000.normarchivedata.model.StandardSFC;
import ru.bk.j3000.normarchivedata.model.dto.SsfcsDTO;

import java.util.List;
import java.util.UUID;

public interface StandardSFCService {
    StandardSFC getSsfcById(UUID id);

    List<StandardSFC> findAllSsfcByYear(Integer year);

    List<StandardSFC> findAllSsfcByYearAndSrcId(Integer year, UUID srcId);

    void updateSsfc(SsfcsDTO ssfcsDTO, Integer year, String originalFuelType);

    List<StandardSFC> toStandartSFCs(SsfcsDTO ssfcsDTO);

    void addSsfc(SsfcsDTO ssfcDTO, Integer year);

    void deleteSsfcById(UUID id);

    void deleteSsfcByIds(List<UUID> ids);

    void deleteSsfcBySrcIdAndYearAndFuelType(UUID srcId, Integer year, FUEL_TYPE fuelType);

    void uploadSsfc(MultipartFile file, Integer year);

    Resource getSsfcTemplate();

    List<Source> findAllDefinedSourcesByYear(Integer year);

    List<StandardSFC> findAllSsfcByYearAndSrcIdAndFuelType(Integer year, UUID srcId, FUEL_TYPE fuelType);
}
