package ru.bk.j3000.normarchivedata.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.bk.j3000.normarchivedata.model.Source;
import ru.bk.j3000.normarchivedata.model.dto.SourceAlterDTO;

import java.util.List;
import java.util.UUID;

@Repository
public interface SourceRepository extends JpaRepository<Source, UUID> {

    @Query("SELECT new ru.bk.j3000.normarchivedata.model.dto.SourceAlterDTO(s.id, s.name) " +
            "FROM Source s " +
            "WHERE s.id NOT IN " +
            "(SELECT sp.id.source.id " +
            "FROM SourceProperty sp " +
            "WHERE sp.id.year = :year)")
    List<SourceAlterDTO> getSourceIdsAndNamesWithNoProp(@Param("year") Integer year);
}
