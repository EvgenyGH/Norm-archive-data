package ru.bk.j3000.normarchivedata.service;

import org.springframework.core.io.Resource;

public interface ReportService {
    Resource getAllSsfcsReport();

    Resource getAllSrcPropsReport();

    Resource getAllTariffZonesReport();

    Resource getAllBranchesReport();

    Resource getAllSourcesReport();
}
