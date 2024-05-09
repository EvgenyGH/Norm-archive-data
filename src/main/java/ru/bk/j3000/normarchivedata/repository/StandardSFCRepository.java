package ru.bk.j3000.normarchivedata.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;
import ru.bk.j3000.normarchivedata.model.Source;
import ru.bk.j3000.normarchivedata.model.StandardSFC;

import java.util.List;
import java.util.UUID;

@Repository
public interface StandardSFCRepository extends JpaRepository<StandardSFC, UUID> {

    @Query("SELECT ssfc FROM StandardSFC ssfc " +
            "WHERE ssfc.properties.id.year = :year")
    List<StandardSFC> findAllSsfcByYear(@Param("year") Integer year);

    @Modifying
    @Query("DELETE StandardSFC ssfc " +
            "WHERE ssfc.properties.id.year = :year")
    void deleteAllByYear(@Param("year") Integer year);

    @Modifying
    @Query("DELETE StandardSFC ssfc " +
            "WHERE ssfc.properties.id.year = :year " +
            "AND ssfc.properties.id.source.id = :srcId")
    void deleteSsfcsBySrcIdAndYear(@P("srcId") UUID srcId, @Param("year") Integer year);


    @Query("SELECT ssfc FROM StandardSFC ssfc " +
            "WHERE ssfc.properties.id.year = :year " +
            "AND ssfc.properties.id.source.id = :srcId")
    List<StandardSFC> findAllSsfcByYearAndSrcId(@Param("year") Integer year,
                                                @Param("srcId") UUID srcId);

    @Query("SELECT ssfc.properties.id.source " +
            "FROM StandardSFC ssfc " +
            "WHERE ssfc.properties.id.year = :year")
    List<Source> findAllDefinedSourcesByYear(@Param("year") Integer year);
}