package ru.bk.j3000.normarchivedata.util.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.bk.j3000.normarchivedata.model.Branch;
import ru.bk.j3000.normarchivedata.model.dto.BranchDTO;

@Component
@Slf4j
public class BranchMapper {

    public Branch toBranch(BranchDTO dto) {
        Branch branch = new Branch(dto.getBranchId(), dto.getBranchName());

        log.info("Branch created from DTO (id {}).", dto.getBranchId());

        return branch;
    }
}
