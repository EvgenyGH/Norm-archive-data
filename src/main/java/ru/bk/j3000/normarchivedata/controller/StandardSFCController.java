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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.bk.j3000.normarchivedata.model.dto.SsfcsDTO;
import ru.bk.j3000.normarchivedata.service.ModelService;
import ru.bk.j3000.normarchivedata.service.StandardSFCService;

import java.util.Optional;
import java.util.UUID;

@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StandardSFCController {
    private final ModelService modelService;
    private final StandardSFCService ssfcService;

    // Get all ssfcs for a particular year
    @GetMapping("/ssfc")
    public String getAllSsfc(Model model,
                             @RequestParam(name = "selectedYear", required = false) Optional<Integer> year) {
        model.addAllAttributes(modelService.getAllSsfcViewAttributes(year));

        return "welcome";
    }

    // Request alter ssfc view
    @GetMapping("/ssfc/alter")
    public String alterSsfc(Model model,
                            @RequestParam(name = "reportYear") Integer year,
                            @RequestParam(name = "srcId", required = false) Optional<UUID> id) {
        model.addAllAttributes(modelService.getAlterSsfcAttributes(year, id));

        return "welcome";
    }

    // Update ssfc
    @PutMapping("/ssfc")
    public String updateSsfc(@ModelAttribute(name = "srcSsfc") SsfcsDTO ssfcsDTO,
                             @RequestParam(name = "reportYear") Integer year) {
        ssfcService.updateSsfc(ssfcsDTO, year);

        return "redirect:/ssfc";
    }

    // Add ssfc
    @PostMapping("/ssfc")
    public String addSsfc(@ModelAttribute(name = "srcSsfc") SsfcsDTO ssfcsDTO,
                          @RequestParam(name = "reportYear") Integer year) {
        ssfcService.addSsfc(ssfcsDTO, year);

        return "redirect:/ssfc";
    }

    // Delete ssfc
    @DeleteMapping("/ssfc")
    public String deleteSsfc(@RequestParam(name = "srcId") UUID srcId,
                             @RequestParam(name = "reportYear") Integer year) {
        ssfcService.deleteSsfcBySrcIdAndYear(srcId, year);

        return "redirect:/ssfc";
    }

    // Upload ssfc from file
    @PostMapping("/ssfc/template/{reportYear}")
    public String uploadSsfc(@PathVariable(name = "reportYear") Integer year,
                             MultipartFile file) {
        ssfcService.uploadSsfc(file, year);

        return "redirect:/ssfc?selectedYear=" + year;
    }

    // Get ssfc template
    @GetMapping("/ssfc/template")
    public ResponseEntity<Resource> getSsfcTemplate() {
        Resource resource = ssfcService.getSsfcTemplate();

        return ResponseEntity
                .ok()
                .contentType(MediaType.asMediaType(MimeType.valueOf("application/vnd.ms-excel")))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=ssfcTemplate.xlsm")
                .body(resource);
    }
}