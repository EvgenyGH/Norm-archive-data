package ru.bk.j3000.normarchivedata.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.bk.j3000.normarchivedata.model.dto.StandardSfcDTO;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class StandardSFCServiceImpl implements StandardSFCService {
    @Override
    public void updateSsfc(StandardSfcDTO ssfcDTO) {

    }

    @Override
    public void addSsfc(StandardSfcDTO ssfcDTO) {

    }

    @Override
    public void deleteSsfcById(UUID id, Integer year) {

    }

    @Override
    public void uploadSsfc(MultipartFile file, Integer year) {

    }

    @Override
    public Resource getSsfcTemplate() {
        return null;
    }
}
