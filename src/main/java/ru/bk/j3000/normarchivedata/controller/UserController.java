package ru.bk.j3000.normarchivedata.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.bk.j3000.normarchivedata.service.ModelService;

@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {
    private final ModelService modelService;

    @GetMapping("/user")
    public String welcome(Model model) {
        model.addAttribute("title", "Пользователи");
        var attribute = modelService.getActiveMenuAttribute("user");
        model.addAttribute("activeMenu", attribute);

        return "welcome";
    }
}
