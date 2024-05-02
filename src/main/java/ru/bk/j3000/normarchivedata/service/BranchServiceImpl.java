package ru.bk.j3000.normarchivedata.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.bk.j3000.normarchivedata.exception.FileParseException;
import ru.bk.j3000.normarchivedata.exception.FileReadException;
import ru.bk.j3000.normarchivedata.model.Branch;
import ru.bk.j3000.normarchivedata.model.dto.BranchDTO;
import ru.bk.j3000.normarchivedata.repository.BranchRepository;
import ru.bk.j3000.normarchivedata.util.BranchMapper;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class BranchServiceImpl implements BranchService {
    private final BranchRepository branchRepository;
    private final String[] brTemplateColumns = {"ID", "Название филиала", "Комментарии"};
    private final BranchMapper branchMapper;

    @Override
    public List<BranchDTO> getAllBranchesDTO() {
        List<BranchDTO> branches = branchRepository.getAllBranchesDTO();

        log.info("All branches DTO retrieved from database ({} in total)", branches.size());

        return branches;
    }

    @Override
    public List<Branch> getAllBranches() {
        List<Branch> branches = branchRepository.findAll();

        log.info("All branches retrieved from database ({} in total)", branches.size());

        return branches;
    }

    @Override
    public Branch getBranchById(Integer branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new EntityNotFoundException(String
                        .format("Branch with id %s not found", branchId)));

        log.info("Branch with id {} retrieved from database", branchId);

        return branch;
    }

    @Override
    public void saveBranch(Branch branch) {
        branchRepository.findById(branch.getId())
                .ifPresentOrElse(
                        (br) -> {
                            throw new EntityExistsException(String
                                    .format("Branch id %s already exists.", branch.getId()));
                        },
                        () -> branchRepository.save(branch));

        log.info("Branch saved ({}).", branch);
    }

    @Override
    public void updateBranch(Branch branch) {
        branchRepository.findById(branch.getId())
                .ifPresentOrElse(tz -> branchRepository.save(branch),
                        () -> {
                            throw new EntityNotFoundException(String
                                    .format("Branch id %s not found.", branch.getId()));
                        });

        log.info("Branch id {} updated ({}).", branch.getId(), branch);
    }

    @Override
    public void deleteBranch(Integer id) {
        branchRepository.deleteById(id);

        log.info("Branch id {} deleted from database.", id);
    }

    @Override
    public Resource getTemplate() {
        try {
            var resource = new ClassPathResource("exceltemplates/branchesTemplate.xlsm");

            log.info("Branches template resource created.");

            return resource;
        } catch (Exception e) {
            throw new FileReadException("Error reading template file.", "Branches template.");
        }
    }

    @Override
    public void uploadBranches(MultipartFile file) {
        List<Branch> branches = readBranchesFromFile(file);

        branchRepository.deleteAll();
        branchRepository.saveAll(branches);

        log.info("Branches uploaded to database from file ({} in total).", branches.size());
    }

    private List<Branch> readBranchesFromFile(MultipartFile file) {
        List<Branch> branches;

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheet("branches");

            checkHeaders(sheet.getRow(0));

            branches = IntStream.rangeClosed(1, sheet.getLastRowNum())
                    .mapToObj(sheet::getRow)
                    .filter(row -> row.getCell(0) != null
                            && row.getCell(0).getNumericCellValue() > 0
                            && row.getCell(1) != null
                            && !row.getCell(1).getStringCellValue().isBlank())
                    .map(row -> new Branch(
                            (int) row.getCell(0).getNumericCellValue(),
                            row.getCell(1).getStringCellValue()))
                    .toList();

        } catch (IOException e) {
            throw new FileReadException("Error reading excel file.", "Branches template.");
        }

        log.info("Branches read from file.");

        return branches;
    }

    private void checkHeaders(Row row) {
        IntStream.rangeClosed(0, brTemplateColumns.length - 1)
                .forEach(i -> {
                    if (!row.getCell(i).getStringCellValue().equals(brTemplateColumns[i])) {
                        throw new FileParseException("Измененный шаблон.", "-", row.getRowNum());
                    }
                });

        log.info("Branches template headers are OK.");
    }

    @Override
    public void saveBranch(BranchDTO branch) {
        this.saveBranch(branchMapper.toBranch(branch));
    }

    @Override
    public void updateBranch(BranchDTO branch) {
        this.updateBranch(branchMapper.toBranch(branch));
    }
}