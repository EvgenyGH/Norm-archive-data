package ru.bk.j3000.normarchivedata.service;

import ru.bk.j3000.normarchivedata.model.Source;

import java.io.File;
import java.util.List;

public interface SourceService {
    List<Source> readSrcFromFile(File file);

    File saveSrcToFile(List<Source> sources);
}
