package ru.bk.j3000.normarchivedata.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import ru.bk.j3000.normarchivedata.model.Branch;
import ru.bk.j3000.normarchivedata.model.dto.BranchDTO;

import java.util.List;

public interface BranchService {
    List<BranchDTO> getAllBranchesDTO();

    Branch getBranchById(Integer branchId);

    void saveBranch(Branch branch);

    void updateBranch(Branch branch);

    void deleteBranch(Integer id);

    Resource getTemplate();

    void uploadBranches(MultipartFile file);

    List<Branch> getAllBranches();
}
