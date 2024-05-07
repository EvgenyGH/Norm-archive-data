package ru.bk.j3000.normarchivedata.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.bk.j3000.normarchivedata.model.StandardSFC;
import ru.bk.j3000.normarchivedata.model.dto.StandardSfcDTO;
import ru.bk.j3000.normarchivedata.repository.StandardSFCRepository;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StandardSFCServiceImpl implements StandardSFCService {
    private final StandardSFCRepository ssfcRepository;

    @Override
    public List<StandardSFC> findAllSsfcByYear(Integer reportYear) {
        List<StandardSFC> ssfcs = ssfcRepository.findAllSsfcByYear(reportYear);

        log.info("Found {} ssfcs for {} year.", ssfcs.size(), reportYear);

        return ssfcs;
    }

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
