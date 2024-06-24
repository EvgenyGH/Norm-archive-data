package ru.bk.j3000.normarchivedata.service;

import ru.bk.j3000.normarchivedata.model.FUEL_TYPE;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface ModelService {
    Map<String, Object> getAlterSourceAttributes(Optional<UUID> sourceId);

    Map<String, Object> getAllSourcesViewAttributes();

    Map<String, Object> getAllUsersViewAttributes();

    Map<String, Object> getAlterUsersAttributes(Optional<String> name);

    Map<String, Object> getErrorAttributes(Throwable e, String requestUri, String userInfo);

    Map<String, Object> getAllTariffZonesViewAttributes();

    Map<String, Object> getAlterTariffZoneAttributes(Optional<Integer> id);

    Map<String, Object> getAllSrcPropertiesViewAttributes(Optional<Integer> year);

    Map<String, Object> getAlterSourcePropertyAttributes(Integer year, Optional<UUID> id);

    Map<String, Object> getAllBranchesViewAttributes();

    Map<String, Object> getAlterBranchAttributes(Optional<Integer> id);

    Map<String, Object> getAllSsfcViewAttributes(Optional<Integer> year, Object warns);

    Map<String, Object> getAlterSsfcAttributes(Integer year, Optional<UUID> id, FUEL_TYPE fuelType);

    Map<String, Object> getReportsAttributes(Integer year);
}
