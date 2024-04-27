package ru.bk.j3000.normarchivedata.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.bk.j3000.normarchivedata.model.SourceProperty;
import ru.bk.j3000.normarchivedata.model.SrcPropertiesId;
import ru.bk.j3000.normarchivedata.model.dto.SourcePropertiesDTO;

import java.util.List;

@Repository
public interface SourcePropertiesRepository extends JpaRepository<SourceProperty, SrcPropertiesId> {
    @Query(value = "SELECT sp FROM SourceProperty sp ")
    List<SourcePropertiesDTO> findAllPropByYear();
}
