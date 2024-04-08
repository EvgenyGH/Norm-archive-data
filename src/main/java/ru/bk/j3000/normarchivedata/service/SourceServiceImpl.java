package ru.bk.j3000.normarchivedata.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.bk.j3000.normarchivedata.model.Source;
import ru.bk.j3000.normarchivedata.repository.SourceRepository;

import java.io.File;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SourceServiceImpl implements SourceService {
    private final SourceRepository sourceRepository;

    @Override
    public List<Source> readSrcFromFile(File file) {
        return List.of();
    }

    @Override
    public File saveSrcToFile(List<Source> sources) {
        return null;
    }
}
