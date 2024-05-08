package ru.bk.j3000.normarchivedata.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.bk.j3000.normarchivedata.exception.FileReadException;
import ru.bk.j3000.normarchivedata.model.StandardSFC;
import ru.bk.j3000.normarchivedata.model.dto.SsfcDTO;
import ru.bk.j3000.normarchivedata.repository.StandardSFCRepository;

import java.net.MalformedURLException;
import java.nio.file.Path;
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
    public void updateSsfc(SsfcDTO ssfcDTO) {

    }

    @Override
    public void addSsfc(SsfcDTO ssfcDTO) {

    }

    @Override
    public void deleteSsfcById(UUID id, Integer year) {

    }

    @Override
    public void uploadSsfc(MultipartFile file, Integer year) {

    }

    @Override
    public Resource getSsfcTemplate() {
        Path path = Path.of("src/main/resources/exceltemplates/ssfcsTemplate.xlsm");
        try {
            Resource resource = new UrlResource(path.toUri());

            log.info("Ssfcs template resource created.");

            return resource;

        } catch (MalformedURLException e) {
            throw new FileReadException("Error reading template file.", "Ssfcs template.");
        }
    }


}
