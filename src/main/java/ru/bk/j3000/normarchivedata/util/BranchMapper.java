package ru.bk.j3000.normarchivedata.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.bk.j3000.normarchivedata.model.Branch;
import ru.bk.j3000.normarchivedata.model.dto.BranchDTO;
import ru.bk.j3000.normarchivedata.service.BranchService;

@Component
@Slf4j
@RequiredArgsConstructor
public class BranchMapper {
    private final BranchService branchService;

    public Branch toBranch(BranchDTO dto) {
        Branch branch = branchService.getBranchById(dto.getBranchId());

        log.info("Branch created from DTO (id {}).", dto.getBranchId());

        return branch;
    }
}
