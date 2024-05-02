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
import ru.bk.j3000.normarchivedata.model.dto.BranchDTO;
import ru.bk.j3000.normarchivedata.service.BranchService;
import ru.bk.j3000.normarchivedata.service.ModelService;

import java.util.Optional;

@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BranchController {
    private final ModelService modelService;
    private final BranchService branchService;

    @GetMapping("/branch")
    public String getAllBranches(Model model) {
        model.addAllAttributes(modelService.getAllBranchesViewAttributes());

        return "welcome";
    }

    @GetMapping("/branch/alter")
    public String alterBranch(@RequestParam(name = "id", required = false) Optional<Integer> id,
                              Model model) {
        model.addAllAttributes(modelService.getAlterBranchAttributes(id));

        return "welcome";
    }

    @PostMapping("/branch")
    public String addNewBranch(@ModelAttribute(name = "branch") BranchDTO branch) {
        branchService.saveBranch(branch);

        return "redirect:/branch";
    }

    @PutMapping("branch")
    public String updateBranch(@ModelAttribute(name = "branch") BranchDTO branch) {
        branchService.updateBranch(branch);

        return "redirect:/branch";
    }

    @DeleteMapping("/branch/{id}")
    public String deleteBranch(@PathVariable(name = "id") Integer id) {
        branchService.deleteBranch(id);

        return "redirect:/branch";
    }

    @GetMapping("/branch/template")
    public ResponseEntity<Resource> getBranchTemplate() {
        return ResponseEntity
                .ok()
                .contentType(MediaType.valueOf("application/vnd.ms-excel"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=branchesTemplate.xlsm")
                .body(branchService.getTemplate());
    }

    @PostMapping("/branch/template")
    public String uploadBranches(@RequestParam(name = "file") MultipartFile file) {
        branchService.uploadBranches(file);

        return "redirect:/branch";
    }
}
