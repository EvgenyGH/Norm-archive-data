package ru.bk.j3000.normarchivedata.service;

import ru.bk.j3000.normarchivedata.model.dto.BranchDTO;

import java.util.List;

public interface BranchService {
    List<BranchDTO> getAllBranchesDTO();
}
