package ru.bk.j3000.normarchivedata.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.bk.j3000.normarchivedata.model.Branch;
import ru.bk.j3000.normarchivedata.model.dto.BranchDTO;

import java.util.List;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Integer> {
    @Query("SELECT new ru.bk.j3000.normarchivedata.model.dto.BranchDTO(b.id, b.branchName) " +
            "FROM Branch b " +
            "ORDER BY b.id")
    List<BranchDTO> getAllBranchesDTO();
}
