package ru.bk.j3000.normarchivedata.service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface ModelService {
    Map<String, Object> getAlterSourceAttributes(Optional<UUID> sourceId);

    Map<String, Object> getAllSourcesViewAttributes();

    Map<String, Object> getAllUsersViewAttributes();

    Map<String, Object> getAlterUsersAttributes(Optional<String> name);
}
