package ru.bk.j3000.normarchivedata.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.bk.j3000.normarchivedata.model.TariffZone;
import ru.bk.j3000.normarchivedata.model.dto.TariffZoneDTO;

import java.util.List;

@Repository
public interface TariffZoneRepository extends JpaRepository<TariffZone, Integer> {
    @Query("SELECT new ru.bk.j3000.normarchivedata.model.dto.TariffZoneDTO(tz.id, tz.zoneName) " +
            "FROM TariffZone tz " +
            "ORDER BY tz.id")
    List<TariffZoneDTO> findAllDTO();
}
