package ru.bk.j3000.normarchivedata.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.bk.j3000.normarchivedata.service.SourceService;

@RestController
@RequiredArgsConstructor
public class SourceController {
    private final SourceService sourceService;

    @GetMapping("/welcome")
    public String welcome() {
        return "welcome";
    }
}
