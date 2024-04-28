package ru.bk.j3000.normarchivedata.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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
    public String welcome(Model model,
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

    // Update new source property
    @PutMapping("/sourceproperty/{id}/{year}")
    public String updateSourceProperty(Model model) {
        return "welcome";
    }

    //new
    @PostMapping("/sourceproperty/{id}/{year}")
    public String addSourceProperty(Model model) {
        return "redirect:welcome";
    }

    ;


    // Delete source property
    @DeleteMapping("/sourceproperty")
    public String deleteSourceProperty(@RequestParam(name = "srcId") UUID id,
                                       @RequestParam(name = "propYear") Integer year) {
        srcPropService.deleteSrcPropertyById(id, year);

        return "redirect:/sourceproperty";
    }

    //post template
    @PostMapping("/sourceproperty/template")
    public String uploadSourceProperties(Model model, MultipartFile file) {

        return "redirect:/sourceproperty";
    }

    @GetMapping("/sourceproperty/template")
    public String getSourcePropertyTemplate(Model model) {
        return "welcome";
    }


}
