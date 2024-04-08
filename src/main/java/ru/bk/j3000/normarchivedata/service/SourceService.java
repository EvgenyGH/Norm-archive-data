package ru.bk.j3000.normarchivedata.service;

import jakarta.transaction.Transactional;
import ru.bk.j3000.normarchivedata.model.Source;

import java.io.File;
import java.util.List;

public interface SourceService {

    void saveSource(Source source);

    @Transactional
    void saveSources(List<Source> sources);

    void uploadSources(File file);
}
