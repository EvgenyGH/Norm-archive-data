package ru.bk.j3000.normarchivedata.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.bk.j3000.normarchivedata.model.Branch;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Sql(scripts = {"/testDataClean.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public class BranchServiceImplTest {
    private final BranchServiceImpl branchService;

    @Test
    @DisplayName("Save branch test.")
    public void whenSaveBranchThenGetBranchFromDatabase() {
        var br = new Branch(1, "First");
        branchService.saveBranch(br);

        assertThat(branchService.getBranchById(br.getId())).isEqualTo(br);
    }

    @Test
    @DisplayName("Save branch when it already exists test.")
    public void whenSaveBranchAndItAlreadyExistsThenThrowEntityExistsException() {
        var br = new Branch(1, "First");
        branchService.saveBranch(br);

        var brNew = new Branch(1, "Second");

        assertThatThrownBy(() -> branchService.saveBranch(brNew))
                .isInstanceOf(EntityExistsException.class);
    }

    @Test
    @DisplayName("Update branch test.")
    public void whenUpdateBranchThenGetUpdatedBranchFromDatabase() {
        var br = new Branch(1, "First");
        branchService.saveBranch(br);
        br.setBranchName("Second");
        branchService.updateBranch(br);

        assertThat(branchService.getBranchById(br.getId()))
                .extracting("branchName")
                .isEqualTo(br.getBranchName());
    }

    @Test
    @DisplayName("Update branch when it does not exists test.")
    public void whenUpdateBranchWhichNotExistsAndThenThrow() {
        var br = new Branch(1, "First");

        assertThatThrownBy(() -> branchService.updateBranch(br))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("Delete branch test.")
    public void whenBranchExistsAndDeletedThenGetNoBranchesFromDatabase() {
        var br = new Branch(1, "First");
        branchService.saveBranch(br);
        branchService.deleteBranch(br.getId());

        assertThat(branchService.getAllBranchesDTO().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Get all branches test.")
    public void whenHaveTwoBranchesAndGetAllBranchesThenGetAllTwoBranchesFromDatabase() {
        var br1 = new Branch(1, "First");
        var br2 = new Branch(2, "Second");
        branchService.saveBranch(br1);
        branchService.saveBranch(br2);

        assertThat(branchService.getAllBranches().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Get branch by id test.")
    public void whenGetBranchByIdThenGetBranchFromDatabase() {
        var br = new Branch(1, "First");
        branchService.saveBranch(br);

        assertThat(branchService.getBranchById(br.getId())).isEqualTo(br);
    }

    @Test
    @DisplayName("Get branch by id which does not exists test.")
    public void whenGetBranchByIdWhichNotExistsThen() {
        var br = new Branch(1, "First");

        assertThatThrownBy(() -> branchService.getBranchById(br.getId()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("Get template test.")
    public void whenUploadSourcePropertyTemplateThenGrtZeroSourcePropertyFromDatabase() throws IOException {
        Resource resource = branchService.getTemplate();
        MockMultipartFile file = new MockMultipartFile("sp.xlsm",
                resource.getContentAsByteArray());
        branchService.uploadBranches(file);

        assertThat(branchService.getAllBranchesDTO()).hasSize(0);
    }

    @Test
    @DisplayName("Upload branches test.")
    public void whenUploadBranchesFromFileThenGetAllBranchesFromDatabase() throws IOException {
        Path path = Path.of("src/test/resources/testdata/branches.xlsm");

        branchService.uploadBranches(
                new MockMultipartFile("file", "file",
                        "text/plain", Files.readAllBytes(path)));
    }

    @Test
    @DisplayName("Get all branches DTO")
    public void whenGetAllBranchesDTOThenGetAllBranchesDTOFromDatabase() {
        var br1 = new Branch(1, "First");
        var br2 = new Branch(2, "Second");
        branchService.saveBranch(br1);
        branchService.saveBranch(br2);

        assertThat(branchService.getAllBranchesDTO().size()).isEqualTo(2);
    }
}