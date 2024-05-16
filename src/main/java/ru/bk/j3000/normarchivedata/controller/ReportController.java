package ru.bk.j3000.normarchivedata.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.bk.j3000.normarchivedata.service.ModelService;
import ru.bk.j3000.normarchivedata.service.ReportService;

@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ReportController {
    private final ModelService modelService;
    private final ReportService reportService;

    @GetMapping("/report")
    public String reportsStartPage(Model model) {
        model.addAllAttributes(modelService.getReportsAttributes());

        return "welcome";
    }

    @GetMapping("/report/source")
    public ResponseEntity<Resource> getSourceReport(@RequestParam(name = "type") String type) {

        return ResponseEntity.ok()
                .contentType(MediaType.asMediaType(MimeType.valueOf("application/vnd.ms-excel")))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=sourceReport.xlsx")
                .body(reportService.getSourcesReport(type)
                );
    }

    @GetMapping("/report/branch")
    public ResponseEntity<Resource> getBranchReport(@RequestParam(name = "type") String type) {

        return ResponseEntity.ok()
                .contentType(MediaType.asMediaType(MimeType.valueOf("application/vnd.ms-excel")))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=branchReport.xlsx")
                .body(reportService.getBranchesReport(type));
    }

    @GetMapping("/report/tariffzone")
    public ResponseEntity<Resource> getTariffZoneReport(@RequestParam(name = "type") String type) {

        return ResponseEntity.ok()
                .contentType(MediaType.asMediaType(MimeType.valueOf("application/vnd.ms-excel")))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=tzReport.xlsx")
                .body(reportService.getTariffZonesReport(type));
    }

    @GetMapping("/report/sourceproperty")
    public ResponseEntity<Resource> getSourcePropertyReport(Model model) {

        return ResponseEntity.ok()
                .contentType(MediaType.asMediaType(MimeType.valueOf("application/vnd.ms-excel")))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=allSrcPropsReport.xlsm")
                .body(reportService.getAllSrcPropsReport());
    }

    @GetMapping("/report/ssfc")
    public ResponseEntity<Resource> getSsfcReport(Model model) {

        return ResponseEntity.ok()
                .contentType(MediaType.asMediaType(MimeType.valueOf("application/vnd.ms-excel")))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=allSsfcsReport.xlsm")
                .body(reportService.getAllSsfcsReport());
    }
}
