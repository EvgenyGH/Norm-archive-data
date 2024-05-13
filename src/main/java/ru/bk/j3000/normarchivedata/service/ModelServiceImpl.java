package ru.bk.j3000.normarchivedata.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.bk.j3000.normarchivedata.model.*;
import ru.bk.j3000.normarchivedata.model.admin.SECURITY_ROLES;
import ru.bk.j3000.normarchivedata.model.dto.*;
import ru.bk.j3000.normarchivedata.service.admin.UserService;

import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    public Map<String, Object> getErrorAttributes(Throwable e, String requestUri, String userInfo) {
        String originUri;
        Matcher matcher = urlOriginPattern.matcher(requestUri);

        if (matcher.find()) {
            originUri = matcher.group(0);
        } else {
            originUri = "/source";
        }

        var errorAttributes = Map.of("message", e.getMessage(),
                "className", e.getClass().getName(),
                "userInfo", userInfo);
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
        Map<String, Object> attributes = new HashMap<>();
        Integer reportYear = year.orElse(LocalDate.now().getYear());
        List<SsfcsDTO> dtos = ssfcService.findAllSsfcByYear(reportYear)
                .stream()
                .collect(Collectors.groupingBy(ssfc ->
                        new SrcIdAndFuelType(ssfc.getProperties().getId().getSource().getId(),
                                ssfc.getFuelType())))
                .values()
                .stream()
                .sorted(Comparator.comparing(e -> e.getFirst()
                        .getFuelType().ordinal()))
                .sorted(Comparator.comparing(e -> e.getFirst()
                        .getProperties().getId().getSource().getName()))
                .sorted(Comparator.comparingInt(e -> e.getFirst()
                        .getProperties().getId().getSource().getSourceType().ordinal()))
                .sorted(Comparator.comparing(e -> e.getFirst().getProperties().getBranch().getId()))
                .sorted(Comparator.comparing(e -> e.getFirst().getProperties().getTariffZone().getId()))
                .map(SsfcsDTO::new)
                .toList();

        attributes.put("title", String.format("Нормативные удельные расходы топлива на %s год",
                reportYear));
        attributes.put("activeMenu", Set.of("ssfc"));
        attributes.put("reportYear", reportYear);
        attributes.put("ssfcs", dtos);

        log.info("All ssfcs view attributes for year {} created.", reportYear);

        return attributes;
    }

    @Override
    public Map<String, Object> getAlterSsfcAttributes(Integer year, Optional<UUID> id, FUEL_TYPE fuelType) {
        HashMap<String, Object> attributes = new HashMap<>();
        String srcName;

        List<SourceDefinedDTO> sources = srcPropService.findAllSourcesByYear(year)
                .stream().map(SourceDefinedDTO::new).toList();

        if (id.isEmpty()) {
            attributes.put("srcSsfcs",
                    new SsfcsDTO(null, null,
                            null, null, null, null,
                            IntStream.rangeClosed(1, 12)
                                    .mapToObj(month -> new SsfcShortDTO(null, year, month,
                                            null, null, null,
                                            null, null))
                                    .toList()));
            attributes.put("newSsfc", true);
            srcName = "new";
        } else {
            Source source = sourceService.getSourceById(id.get())
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format("Source id %s not found.", id.get())));
            srcName = source.getName();

            List<StandardSFC> ssfcs = ssfcService
                    .findAllSsfcByYearAndSrcIdAndFuelType(year, source.getId(), fuelType);

            attributes.put("srcSsfcs", new SsfcsDTO(ssfcs));
            attributes.put("newSsfc", false);
        }

        List<SourceDefinedDTO> sourcesDefined = ssfcService.findAllDefinedSourcesByYear(year)
                .stream()
                .map(SourceDefinedDTO::new)
                .toList();

        sources = sources.stream()
                .map(s -> {
                    if (sourcesDefined.contains(s)) {
                        s.setDefined(true);
                    }
                    return s;
                })
                .sorted(Comparator.comparing(SourceDefinedDTO::getSourceName))
                .sorted((s1, s2) -> Boolean.compare(s1.getDefined(), s2.getDefined()))
                .toList();

        attributes.put("sources", sources);

        attributes.put("shadow", true);
        attributes.put("alterSsfc", true);
        attributes.put("reportYear", year);
        attributes.put("activeMenu", Collections.emptySet());

        log.info("Alter ssfc attributes created. Source {} id {}, year {}.",
                srcName, id.isEmpty() ? "new" : id.get(), year);

        return attributes;
    }

    @Override
    public Map<String, Object> getReportsAttributes() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("title", "Отчеты");
        attributes.put("activeMenu", Set.of("report"));

        return attributes;
    }
}