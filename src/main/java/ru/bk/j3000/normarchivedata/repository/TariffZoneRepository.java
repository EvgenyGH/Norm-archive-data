package ru.bk.j3000.normarchivedata.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bk.j3000.normarchivedata.model.TariffZone;

@Repository
public interface TariffZoneRepository extends JpaRepository<TariffZone, Integer> {
}
