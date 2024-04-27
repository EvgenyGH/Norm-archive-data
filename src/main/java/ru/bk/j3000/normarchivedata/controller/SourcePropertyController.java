package ru.bk.j3000.normarchivedata.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.bk.j3000.normarchivedata.service.ModelService;

import java.util.Optional;

@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SourcePropertyController {
    private static final Logger log = LoggerFactory.getLogger(SourcePropertyController.class);
    private final ModelService modelService;

    @GetMapping("/sourceproperty")
    public String welcome(Model model,
                          @RequestParam(name = "selectedYear", required = false) Optional<Integer> year) {
        model.addAllAttributes(modelService.getAllSrcPropertiesViewAttributes(year));

        return "welcome";
    }
}
