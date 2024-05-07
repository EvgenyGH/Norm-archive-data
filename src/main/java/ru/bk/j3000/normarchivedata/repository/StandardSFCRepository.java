package ru.bk.j3000.normarchivedata.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.bk.j3000.normarchivedata.model.StandardSFC;

import java.util.List;
import java.util.UUID;

@Repository
public interface StandardSFCRepository extends JpaRepository<StandardSFC, UUID> {

    @Query("SELECT ssfc FROM StandardSFC ssfc " +
            "WHERE ssfc.properties.id.year = :year")
    List<StandardSFC> findAllSsfcByYear(@Param("year") Integer reportYear);
}
