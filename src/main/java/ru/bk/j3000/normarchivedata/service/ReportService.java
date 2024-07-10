package ru.bk.j3000.normarchivedata.service;

import org.springframework.core.io.Resource;

import java.util.List;
import java.util.UUID;

public interface ReportService {
    Resource getSourcesReport(String type);

    Resource getBranchesReport(String type);

    Resource getTariffZonesReport(String type);

    Resource getSrcPropertyReport(String type, Integer year);

    Resource getSsfcsReport(String type, String selection, Integer year, List<UUID> srcIds,
                            List<String> sumTypes, List<String> periods);

    Resource getSrcTemplateReport();

    Resource getAllSourcesReport();

    Resource getBranchTemplateReport();

    Resource getAllBranchesReport();

    Resource getTZTemplateReport();

    Resource getAllTZReport();

    Resource getAllSrcPropReport(Integer year);

    Resource getSrcPropTemplateReport(Integer year);

    Resource getSsfcTemplateReport(Integer year, String selection, List<UUID> srcIds);

    Resource getAllSsfcReport(Integer year, String selection, List<UUID> srcIds, List<String> sumTypes);
}