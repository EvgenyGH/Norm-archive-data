package ru.bk.j3000.normarchivedata.controller;

import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.bk.j3000.normarchivedata.service.ModelService;
import ru.bk.j3000.normarchivedata.service.admin.UserService;

@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {
    private final ModelService modelService;
    private final UserService userService;

    @GetMapping("/user")
    public String welcome(Model model) {
        model.addAllAttributes(modelService.getAllUsersViewAttributes());

        return "welcome";
    }

    @PostMapping("/user")
    public String createUser(@ModelAttribute User user) {
        userService.createUser(user);

        return "redirect:/user";
    }

    @DeleteMapping("/user/{name}")
    public String deleteUser(@PathVariable(name = "name") String name) {
        userService.deleteUserByName(name);

        return "redirect:/user";
    }

    @PatchMapping("/user")
    public String updateUser(@ModelAttribute User user) {
        userService.updateUser(user);

        return "redirect:/user";
    }
}
