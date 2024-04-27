package ru.bk.j3000.normarchivedata.service;

import jakarta.transaction.Transactional;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import ru.bk.j3000.normarchivedata.model.Source;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SourceService {

    void saveSource(Source source);

    @Transactional
    void saveSources(List<Source> sources);

    void uploadSources(MultipartFile file);

    void deleteAllSources();

    void deleteSourcesById(List<UUID> ids);

    void updateSource(Source source);

    List<Source> getAllSources();

    Optional<Source> getSourceById(UUID id);

    Resource getTemplate();

    void alterSource(Source source);
}
