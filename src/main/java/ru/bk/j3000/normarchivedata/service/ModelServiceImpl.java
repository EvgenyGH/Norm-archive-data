package ru.bk.j3000.normarchivedata.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.bk.j3000.normarchivedata.model.SOURCE_TYPE;
import ru.bk.j3000.normarchivedata.model.Source;
import ru.bk.j3000.normarchivedata.model.TariffZone;
import ru.bk.j3000.normarchivedata.model.admin.SECURITY_ROLES;
import ru.bk.j3000.normarchivedata.model.dto.BranchDTO;
import ru.bk.j3000.normarchivedata.model.dto.SourcePropertyDTO;
import ru.bk.j3000.normarchivedata.model.dto.StandardSfcDTO;
import ru.bk.j3000.normarchivedata.model.dto.UserDTO;
import ru.bk.j3000.normarchivedata.service.admin.UserService;

import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class ModelServiceImpl implements ModelService {
    private final static String[] ACTIVE_MENU = {
            "user",
            "source",
            "tariffZone",
            "branch",
            "sourceProperty",
            "ssfc",
            "report"};
    private final Pattern urlOriginPattern = Pattern.compile("^/[a-zA-z0-9]*");
    private final SourceService sourceService;
    private final UserService userService;
    private final TariffZoneService tariffZoneService;
    private final StandardSFCService ssfcService;
    private final SourcePropertyService srcPropService;
    private final BranchService branchService;

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

    @Override
    public Map<String, Object> getErrorAttributes(Exception e, String requestUri) {
        String originUri;
        Matcher matcher = urlOriginPattern.matcher(requestUri);

        if (matcher.find()) {
            originUri = matcher.group(0);
        } else {
            originUri = "/source";
        }

        var errorAttributes = Map.of("message", e.getMessage(), "className", e.getClass().getName());
        Map<String, Object> attributes = Map.of("error", errorAttributes,
                "originUri", originUri);

        log.info("Error view attributes created.");

        return attributes;
    }

    @Override
    public Map<String, Object> getAllTariffZonesViewAttributes() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("title", "Тарифные зоны");
        attributes.put("activeMenu", Set.of("tariffZone"));
        attributes.put("tariffZones", tariffZoneService.getAllTariffZones());

        log.info("All tariffZones view attributes created.");

        return attributes;
    }

    @Override
    public Map<String, Object> getAlterTariffZoneAttributes(Optional<Integer> id) {
        HashMap<String, Object> attributes = new HashMap<>();

        attributes.put("shadow", true);
        attributes.put("alterTariffZone", true);
        attributes.put("activeMenu", Collections.emptySet());

        if (id.isEmpty()) {
            attributes.put("tariffZone", new TariffZone(null, ""));
        } else {
            attributes.put("tariffZone", tariffZoneService.getTariffZoneById(id.get()));
        }

        log.info("Alter tariff zone attributes created. Tariff zone id {}.",
                id.isEmpty() ? "new" : id.get());

        return attributes;
    }

    @Override
    public Map<String, Object> getAllSrcPropertiesViewAttributes(Optional<Integer> year) {
        Map<String, Object> attributes = new HashMap<>();
        Integer reportYear = year.orElse(LocalDate.now().getYear());

        attributes.put("title", String.format("Свойства источников на %s год", reportYear));
        attributes.put("activeMenu", Set.of("sourceProperty"));
        attributes.put("reportYear", reportYear);
        attributes.put("sourceProperties", srcPropService.findAllPropByYear(reportYear)
                .stream()
                .sorted(Comparator.comparing(sp -> sp.getId().getSource().getName()))
                .sorted(Comparator.comparing(sp -> sp.getId().getSource().getSourceType().ordinal()))
                .sorted(Comparator.comparing(sp -> sp.getBranch().getId()))
                .sorted(Comparator.comparing(sp -> sp.getTariffZone().getId()))
                .map(SourcePropertyDTO::new)
                .toList());

        log.info("All source properties view attributes for year {} created.", reportYear);

        return attributes;
    }

    @Override
    public Map<String, Object> getAlterSourcePropertyAttributes(Integer year, Optional<UUID> id) {
        HashMap<String, Object> attributes = new HashMap<>();

        attributes.put("shadow", true);
        attributes.put("alterSourceProperty", true);
        attributes.put("activeMenu", Collections.emptySet());
        attributes.put("tariffZones", tariffZoneService.getAllTariffZonesDTO());
        attributes.put("branches", branchService.getAllBranchesDTO());

        if (id.isEmpty()) {
            attributes.put("sourceProperty",
                    new SourcePropertyDTO(null, year, null, null));
            attributes.put("sourcesLeft", sourceService.getSourceIdsAndNamesWithNoProp(year));
        } else {
            attributes.put("sourceProperty", srcPropService.getSourcePropertyById(id.get(), year));
        }

        log.info("Alter source property attributes created. Source id {}, year {}.",
                id.isEmpty() ? "new" : id.get(), year);

        return attributes;
    }

    @Override
    public Map<String, Object> getAllBranchesViewAttributes() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("title", "Филиалы");
        attributes.put("activeMenu", Set.of("branch"));
        attributes.put("branches", branchService.getAllBranchesDTO());

        log.info("All branches view attributes created.");

        return attributes;
    }

    @Override
    public Map<String, Object> getAlterBranchAttributes(Optional<Integer> id) {
        HashMap<String, Object> attributes = new HashMap<>();

        attributes.put("shadow", true);
        attributes.put("alterBranch", true);
        attributes.put("activeMenu", Collections.emptySet());

        if (id.isEmpty()) {
            attributes.put("branch", new BranchDTO(null, ""));
        } else {
            attributes.put("branch", new BranchDTO(branchService.getBranchById(id.get())));
        }

        log.info("Alter branch attributes created. Branch id {}.",
                id.isEmpty() ? "new" : id.get());

        return attributes;
    }

    @Override
    public Map<String, Object> getAllSsfcViewAttributes(Optional<Integer> year) {
        //model.addAttribute("title", "Нормативные удельные расходы топлива");
//        model.addAttribute("activeMenu", Set.of("ssfc"));


        Map<String, Object> attributes = new HashMap<>();
        Integer reportYear = year.orElse(LocalDate.now().getYear());

        attributes.put("title", String.format("Нормативные удельные расходы топлива на %s год",
                reportYear));
        attributes.put("activeMenu", Set.of("ssfc"));
        attributes.put("reportYear", reportYear);
        attributes.put("ssfcs", ssfcService.findAllSsfcByYear(reportYear)
                .stream()
                .map(StandardSfcDTO::new)
                .toList());

        log.info("All ssfcs view attributes for year {} created.", reportYear);

        return attributes;
    }

    @Override
    public Map<String, Object> getAlterSsfcAttributes(Integer year, Optional<UUID> id) {
        return Map.of();
    }
}