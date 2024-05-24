package ru.bk.j3000.normarchivedata.controller.admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {
    @GetMapping("/login")
    public String login() {

        return "login";
    }

    @GetMapping("/logout")
    public String performLogout() {

        return "logout";
    }

    @PostMapping("/logout")
    public String performLogout(Authentication authentication,
                                HttpServletRequest request,
                                HttpServletResponse response,
                                SecurityContextLogoutHandler logoutHandler) {

        logoutHandler.logout(request, response, authentication);

        return "redirect:/login?logout";
    }
}