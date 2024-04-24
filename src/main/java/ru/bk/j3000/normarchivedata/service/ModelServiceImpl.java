package ru.bk.j3000.normarchivedata.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.bk.j3000.normarchivedata.model.SOURCE_TYPE;
import ru.bk.j3000.normarchivedata.model.Source;
import ru.bk.j3000.normarchivedata.model.UserDTO;
import ru.bk.j3000.normarchivedata.model.admin.SECURITY_ROLES;
import ru.bk.j3000.normarchivedata.service.admin.UserService;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ModelServiceImpl implements ModelService {
    private final static String[] ACTIVE_MENU = {
            "user",
            "source",
            "tariffZone",
            "sourceProperty",
            "ssfc",
            "report"};
    private final SourceService sourceService;
    private final UserService userService;

    @Override
    public Map<String, Object> getAlterSourceAttributes(Optional<UUID> sourceId) {
        HashMap<String, Object> attributes = new HashMap<>();

        attributes.put("shadow", true);
        attributes.put("alterSource", true);
        attributes.put("activeMenu", Collections.emptySet());

        if (sourceId.isEmpty()) {
            attributes.put("source", new Source(null, "", "", SOURCE_TYPE.RTS));
        } else {
            attributes.put("source", sourceService.getSourceById(sourceId.get()).orElseThrow(() ->
                    new EntityNotFoundException("Source not found. Id " + sourceId.get())));
        }

        log.info("Alter source attributes created. Source id {}.",
                sourceId.isEmpty() ? "new" : sourceId.get());

        return attributes;
    }

    @Override
    public Map<String, Object> getAllSourcesViewAttributes() {
        Map<String, Object> attributes = new HashMap<>();

        attributes.put("title", "Источники");
        attributes.put("activeMenu", Set.of("source"));
        attributes.put("sources", sourceService.getAllSources());

        log.info("All sources view attributes created.");

        return attributes;
    }

    @Override
    public Map<String, Object> getAllUsersViewAttributes() {
        Map<String, Object> attributes = new HashMap<>();

        attributes.put("title", "Пользователи");
        attributes.put("activeMenu", Set.of("user"));
        attributes.put("users", userService.getAllUsers());

        log.info("All users view attributes created.");

        return attributes;
    }

    @Override
    public Map<String, Object> getAlterUsersAttributes(Optional<String> name) {
        HashMap<String, Object> attributes = new HashMap<>();

        attributes.put("shadow", true);
        attributes.put("alterUser", true);
        attributes.put("activeMenu", Collections.emptySet());
        attributes.put("secRoles", SECURITY_ROLES.values());

        if (name.isEmpty()) {
            attributes.put("user", new UserDTO("", "", SECURITY_ROLES.ROLE_USER));
        } else {
            attributes.put("user", userService.getUserByName(name.get()));
        }

        log.info("Alter user attributes created. User name is {}.",
                name.orElse("new"));

        return attributes;
    }
}