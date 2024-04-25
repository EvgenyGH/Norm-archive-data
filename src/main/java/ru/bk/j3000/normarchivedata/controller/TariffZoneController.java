package ru.bk.j3000.normarchivedata.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.bk.j3000.normarchivedata.model.TariffZone;
import ru.bk.j3000.normarchivedata.service.ModelService;
import ru.bk.j3000.normarchivedata.service.TariffZoneService;

import java.util.Optional;

@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TariffZoneController {
    private final ModelService modelService;
    private final TariffZoneService tariffZoneService;

    @GetMapping("/tariffzone")
    public String getAllTariffZones(Model model) {
        model.addAllAttributes(modelService.getAllTariffZonesViewAttributes());

        return "welcome";
    }

    @GetMapping("/tariffzone/alter/{id}")
    public String alterTariffZone(@PathVariable(name = "id", required = false) Optional<Integer> id,
                                  Model model) {
        model.addAllAttributes(modelService.getAlterTariffZoneAttributes(id));

        return "welcome";
    }

    @PostMapping("/tariffzone")
    public String addNewTariffZone(@ModelAttribute(name = "tariffZone") TariffZone tariffZone) {
        tariffZoneService.saveTariffZone(tariffZone);

        return "redirect:/tariffzone";
    }

    @PutMapping("tariffzone")
    public String updateTariffZone(@ModelAttribute(name = "tariffzone") TariffZone tariffZone) {
        tariffZoneService.updateTariffZone(tariffZone);

        return "redirect:/tariffzone";
    }

    @DeleteMapping("/tariffzone/{id}")
    public String deleteTariffZone(@PathVariable(name = "id") Integer id) {
        tariffZoneService.deleteTariffZone(id);

        return "redirect:/tariffzone";
    }

    @GetMapping("/tariffzone/template")
    public ResponseEntity<Resource> getTariffZoneTemplate() {
        return ResponseEntity
                .ok()
                .contentType(MediaType.valueOf("application/vnd.ms-excel"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=tariffZonesTemplate.xlsm")
                .body(tariffZoneService.getTemplate());
    }

    @PostMapping("/tariffzone/template")
    public String uploadTariffZones(@RequestParam(name = "file") MultipartFile file) {
        tariffZoneService.uploadTariffZones(file);

        return "redirect:/tariffzone";
    }
}
