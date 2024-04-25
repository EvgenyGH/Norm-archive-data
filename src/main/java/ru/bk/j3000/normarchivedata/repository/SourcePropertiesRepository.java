package ru.bk.j3000.normarchivedata.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bk.j3000.normarchivedata.model.SourceProperties;
import ru.bk.j3000.normarchivedata.model.SrcPropertiesId;

@Repository
public interface SourcePropertiesRepository extends JpaRepository<SourceProperties, SrcPropertiesId> {
}
