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
import ru.bk.j3000.normarchivedata.model.dto.SourcePropertyDTO;
import ru.bk.j3000.normarchivedata.service.ModelService;
import ru.bk.j3000.normarchivedata.service.SourcePropertyService;

import java.util.Optional;
import java.util.UUID;

@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SourcePropertyController {
    private final ModelService modelService;
    private final SourcePropertyService srcPropService;

    // Get all source properties for a particular year
    @GetMapping("/sourceproperty")
    public String getAllSrcProperties(Model model,
                          @RequestParam(name = "selectedYear", required = false) Optional<Integer> year) {
        model.addAllAttributes(modelService.getAllSrcPropertiesViewAttributes(year));

        return "welcome";
    }

    // Request alter source property view
    @GetMapping("/sourceproperty/alter/{reportYear}")
    public String alterSourceProperty(Model model, @PathVariable(name = "reportYear") Integer year,
                                      @RequestParam(name = "srcId", required = false) Optional<UUID> id) {
        model.addAllAttributes(modelService.getAlterSourcePropertyAttributes(year, id));

        return "welcome";
    }

    // Update source property
    @PutMapping("/sourceproperty")
    public String updateSourceProperty(@ModelAttribute(name = "sourceProperty")
                                       SourcePropertyDTO sourcePropertyDTO) {
        srcPropService.updateSourceProperty(sourcePropertyDTO);

        return "redirect:/sourceproperty";
    }

    // Add source property
    @PostMapping("/sourceproperty")
    public String addSourceProperty(@ModelAttribute(name = "sourceProperty")
                                    SourcePropertyDTO sourcePropertyDTO) {
        srcPropService.addSourceProperty(sourcePropertyDTO);

        return "redirect:/sourceproperty";
    }

    // Delete source property
    @DeleteMapping("/sourceproperty")
    public String deleteSourceProperty(@RequestParam(name = "srcId") UUID id,
                                       @RequestParam(name = "propYear") Integer year) {
        srcPropService.deleteSrcPropertyById(id, year);

        return "redirect:/sourceproperty";
    }

    // Upload source properties from file
    @PostMapping("/sourceproperty/template/{reportYear}")
    public String uploadSourceProperties(@PathVariable(name = "reportYear") Integer year,
                                         MultipartFile file) {
        srcPropService.uploadSourceProperties(file, year);

        return "redirect:/sourceproperty?selectedYear=" + year;
    }

    // Get source property template
    @GetMapping("/sourceproperty/template")
    public ResponseEntity<Resource> getSourcePropertyTemplate() {
        Resource resource = srcPropService.getSourcePropertyTemplate();

        return ResponseEntity
                .ok()
                .contentType(MediaType.asMediaType(MimeType.valueOf("application/vnd.ms-excel")))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=sourcePropertyTemplate.xlsm")
                .body(resource);
    }
}
