package ru.bk.j3000.normarchivedata.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.bk.j3000.normarchivedata.model.Branch;
import ru.bk.j3000.normarchivedata.model.dto.BranchDTO;
import ru.bk.j3000.normarchivedata.repository.BranchRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BranchServiceImpl implements BranchService {
    private final BranchRepository branchRepository;

    @Override
    public List<BranchDTO> getAllBranchesDTO() {
        List<BranchDTO> branches = branchRepository.getAllBranchesDTO();

        log.info("All branches DTO retrieved from database ({} in total)", branches.size());

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
}
