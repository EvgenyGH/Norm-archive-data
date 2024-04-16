package ru.bk.j3000.normarchivedata.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class ModelServiceImpl implements ModelService {
    private final static Map<String, String> ACTIVE_MENU = Map.of("source", "inactive",
            "sourceProperty", "inactive",
            "tariffZone", "inactive",
            "user", "inactive",
            "ssfc", "inactive",
            "report", "inactive");

    @Override
    public Map<String, String> getActiveMenuAttribute(String menu) {
        var attribute = new HashMap<>(ACTIVE_MENU);
        attribute.put(menu, "");

        log.info("Attribute {} activated.", menu);

        return attribute;
    }
}
