package ru.bk.j3000.normarchivedata.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import ru.bk.j3000.normarchivedata.model.StandardSFC;
import ru.bk.j3000.normarchivedata.model.dto.SsfcDTO;

import java.util.List;
import java.util.UUID;

public interface StandardSFCService {
    void updateSsfc(SsfcDTO ssfcDTO);

    void addSsfc(SsfcDTO ssfcDTO);

    void deleteSsfcById(UUID id, Integer year);

    void uploadSsfc(MultipartFile file, Integer year);

    Resource getSsfcTemplate();

    List<StandardSFC> findAllSsfcByYear(Integer reportYear);
}
