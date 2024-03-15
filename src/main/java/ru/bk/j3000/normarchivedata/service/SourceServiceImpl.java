package ru.bk.j3000.normarchivedata.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.bk.j3000.normarchivedata.repository.SourceRepository;

@Service
@RequiredArgsConstructor
public class SourceServiceImpl implements SourceService {
    private final SourceRepository sourceRepository;

}
