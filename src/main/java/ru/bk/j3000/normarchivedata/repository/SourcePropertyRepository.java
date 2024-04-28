package ru.bk.j3000.normarchivedata.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.bk.j3000.normarchivedata.model.SourceProperty;
import ru.bk.j3000.normarchivedata.model.SrcPropertyId;
import ru.bk.j3000.normarchivedata.model.dto.SourcePropertyDTO;

import java.util.List;
import java.util.UUID;

@Repository
public interface SourcePropertyRepository extends JpaRepository<SourceProperty, SrcPropertyId> {
    //todo sorting
    @Query("SELECT NEW ru.bk.j3000.normarchivedata.model.dto.SourcePropertyDTO(" +
            "sp.id.source.id, sp.id.source.name, " +
            "sp.id.year, sp.branch, sp.tariffZone.zoneName) " +
            "FROM SourceProperty sp " +
            "WHERE sp.id.year = :year")
    List<SourcePropertyDTO> findAllPropDTOByYear(@Param("year") Integer year);

    @Modifying
    @Query("DELETE SourceProperty sp " +
            "WHERE sp.id.source.id = :srcId " +
            "AND sp.id.year = :propYear")
    void deleteBySrcIdAndYear(@Param("srcId") UUID id, @Param("propYear") Integer year);

    @Query("SELECT NEW ru.bk.j3000.normarchivedata.model.dto.SourcePropertyDTO(" +
            "sp.id.source.id, sp.id.source.name, " +
            "sp.id.year, sp.branch, sp.tariffZone.zoneName) " +
            "FROM SourceProperty sp " +
            "WHERE sp.id.year = :year " +
            "AND sp.id.source.id = :srcId")
    SourcePropertyDTO getSourcePropertyDTOByIdAndYear(@Param("srcId") UUID srcId,
                                                      @Param("year") Integer year);
}
