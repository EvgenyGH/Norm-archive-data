package ru.bk.j3000.normarchivedata.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import ru.bk.j3000.normarchivedata.model.dto.StandardSfcDTO;

import java.util.UUID;

public interface StandardSFCService {
    void updateSsfc(StandardSfcDTO ssfcDTO);

    void addSsfc(StandardSfcDTO ssfcDTO);

    void deleteSsfcById(UUID id, Integer year);

    void uploadSsfc(MultipartFile file, Integer year);

    Resource getSsfcTemplate();
}
