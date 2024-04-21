package ru.bk.j3000.normarchivedata.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.bk.j3000.normarchivedata.model.SOURCE_TYPE;
import ru.bk.j3000.normarchivedata.model.Source;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ModelServiceImpl implements ModelService {
    //    private final static Map<String, String> ACTIVE_MENU = Map.of("source", "inactive",
//            "sourceProperty", "inactive",
//            "tariffZone", "inactive",
//            "user", "inactive",
//            "ssfc", "inactive",
//            "report", "inactive");
    private final SourceService sourceService;

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
                    new EntityNotFoundException("Source id {} not found " + sourceId.get())));
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


}
