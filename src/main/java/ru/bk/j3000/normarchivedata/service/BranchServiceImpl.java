package ru.bk.j3000.normarchivedata.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
}
