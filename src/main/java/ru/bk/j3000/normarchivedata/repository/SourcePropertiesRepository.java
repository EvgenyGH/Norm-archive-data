package ru.bk.j3000.normarchivedata.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.bk.j3000.normarchivedata.model.SourceProperty;
import ru.bk.j3000.normarchivedata.model.SrcPropertiesId;
import ru.bk.j3000.normarchivedata.model.dto.SourcePropertiesDTO;

import java.util.List;

@Repository
public interface SourcePropertiesRepository extends JpaRepository<SourceProperty, SrcPropertiesId> {
    @Query(value = "SELECT NEW ru.bk.j3000.normarchivedata.model.dto.SourcePropertiesDTO(" +
            "sp.id.source.id, sp.id.source.name, " +
            "sp.id.year, sp.branch, sp.tariffZone.zoneName) " +
            "FROM SourceProperty sp " +
            "WHERE sp.id.year = :year")
    List<SourcePropertiesDTO> findAllPropByYear(@Param("year") Integer year);
}
