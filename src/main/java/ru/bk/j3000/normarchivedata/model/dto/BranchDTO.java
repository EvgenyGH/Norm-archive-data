package ru.bk.j3000.normarchivedata.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bk.j3000.normarchivedata.model.Branch;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BranchDTO {
    private Integer branchId;
    private String branchName;

    public BranchDTO(Branch branch) {
        this.branchId = branch.getId();
        this.branchName = branch.getBranchName();
    }
}
