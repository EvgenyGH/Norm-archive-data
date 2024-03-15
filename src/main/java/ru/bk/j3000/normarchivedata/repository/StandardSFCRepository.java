package ru.bk.j3000.normarchivedata.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bk.j3000.normarchivedata.model.StandardSFC;

import java.util.UUID;

@Repository
public interface StandardSFCRepository extends JpaRepository<StandardSFC, UUID> {
}
