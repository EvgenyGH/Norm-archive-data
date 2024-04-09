package ru.bk.j3000.normarchivedata.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.bk.j3000.normarchivedata.service.SourceService;

@Controller
@RequiredArgsConstructor
public class SourceController {
    private final SourceService sourceService;

    @GetMapping("/welcome")
    public String welcome(Model model) {
        model.addAttribute("name", "World!");
        return "welcome";
    }
}
