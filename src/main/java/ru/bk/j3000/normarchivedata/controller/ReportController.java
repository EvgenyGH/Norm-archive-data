package ru.bk.j3000.normarchivedata.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.bk.j3000.normarchivedata.service.ModelService;

import java.util.Set;

@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ReportController {
    private final ModelService modelService;

    @GetMapping("/report")
    public String welcome(Model model) {
        model.addAttribute("title", "Отчеты");
        model.addAttribute("activeMenu", Set.of("report"));

        return "welcome";
    }
}
