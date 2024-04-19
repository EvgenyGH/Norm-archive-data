package ru.bk.j3000.normarchivedata.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.bk.j3000.normarchivedata.model.Source;
import ru.bk.j3000.normarchivedata.service.ModelService;
import ru.bk.j3000.normarchivedata.service.SourceService;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class SourceController {
    private final SourceService sourceService;
    private final ModelService modelService;

    @GetMapping("/source")
    public String welcome(Model model) {
        model.addAllAttributes(modelService.getAllSourcesViewAttributes());

        return "welcome";
    }

    @GetMapping("/source/template")
    public ResponseEntity<Resource> getTemplate() throws MalformedURLException {
        Path path = Path.of("src/main/resources/exceltemplates/sources.xlsm");
        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity
                .ok()
                .contentType(MediaType.asMediaType(MimeType.valueOf("application/vnd.ms-excel")))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=sourceTemplate.xlsm")
                .body(resource);
    }

    @PostMapping("/source/template")
    public String uploadSources(@RequestParam("file") MultipartFile file, Model model) {
        sourceService.uploadSources(file);

        return "redirect:/source";
    }

    @DeleteMapping("/source/delete/{sourceId}")
    public String deleteSources(Model model, @PathVariable("sourceId") UUID sourceId) {
        sourceService.deleteSourcesById(List.of(sourceId));

        return "redirect:/source";
    }

    @GetMapping("/source/alter")
    public String alterSources(Model model, @RequestParam(name = "id", required = false) Optional<UUID> sourceId) {
        model.addAllAttributes(modelService.getAlterSourceAttributes(sourceId));

        return "welcome";
    }

    @PutMapping("/source")
    public String putSource(Model model, @ModelAttribute("source") Source source) {
        sourceService.saveSource(source);

        return "redirect:/source";
    }
}

