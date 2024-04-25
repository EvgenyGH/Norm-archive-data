package ru.bk.j3000.normarchivedata.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.bk.j3000.normarchivedata.repository.TariffZoneRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class TariffZoneServiceImpl implements TariffZoneService {
    private final TariffZoneRepository tariffZoneRepository;
}
