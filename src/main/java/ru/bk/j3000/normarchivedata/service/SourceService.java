package ru.bk.j3000.normarchivedata.service;

import jakarta.transaction.Transactional;
import ru.bk.j3000.normarchivedata.model.Source;

import java.io.File;
import java.util.List;
import java.util.UUID;

public interface SourceService {

    void saveSource(Source source);

    @Transactional
    void saveSources(List<Source> sources);

    void uploadSources(File file);

    void deleteAllSources();

    void deleteSourcesById(List<UUID> ids);

    void updateSource(Source source);

    List<Source> getAllSources();
}
