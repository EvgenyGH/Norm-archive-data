package ru.bk.j3000.normarchivedata.service;

import org.springframework.core.io.Resource;

public interface ReportService {
    Resource getSourcesReport(String type);

    Resource getBranchesReport(String type);

    Resource getTariffZonesReport(String type);

    Resource getSrcTemplateReport();

    Resource getAllSourcesReport();

    Resource getBranchTemplateReport();

    Resource getAllBranchesReport();

    Resource getTZTemplateReport();

    Resource getAllTZReport();

    Resource getAllSsfcsReport();

    Resource getAllSrcPropsReport();
}
