package ru.bk.j3000.normarchivedata.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.bk.j3000.normarchivedata.service.ModelService;

@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ReportController {
    private final ModelService modelService;

    @GetMapping("/report")
    public String welcome(Model model) {
        model.addAttribute("title", "Отчеты");
        var attribute = modelService.getActiveMenuAttribute("report");
        model.addAttribute("activeMenu", attribute);

        return "welcome";
    }
}
