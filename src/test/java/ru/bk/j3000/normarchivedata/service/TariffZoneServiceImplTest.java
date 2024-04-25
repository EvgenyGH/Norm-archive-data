package ru.bk.j3000.normarchivedata.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import ru.bk.j3000.normarchivedata.model.TariffZone;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TariffZoneServiceImplTest {
    private final TariffZoneServiceImpl tariffZoneService;

    @Test
    @DisplayName("Save tariff zone test.")
    public void whenSaveTariffZoneThenGetTariffZoneFromDatabase() {
        var tz = new TariffZone(1, "First");
        tariffZoneService.saveTariffZone(tz);

        assertThat(tariffZoneService.getTariffZoneById(tz.getId())).isEqualTo(tz);
    }

    @Test
    @DisplayName("Save tariff zone when it already exists test.")
    public void whenSaveTariffZoneAndItAlreadyExistsThenThrowEntityExistsException() {
        var tz = new TariffZone(1, "First");
        tariffZoneService.saveTariffZone(tz);

        var tzNew = new TariffZone(1, "Second");

        assertThatThrownBy(() -> tariffZoneService.saveTariffZone(tzNew))
                .isInstanceOf(EntityExistsException.class);
    }

    @Test
    @DisplayName("Update tariff zone test.")
    public void whenUpdateTariffZoneThenGetUpdatedTariffZoneFromDatabase() {
        var tz = new TariffZone(1, "First");
        tariffZoneService.saveTariffZone(tz);
        tz.setZoneName("Second");
        tariffZoneService.updateTariffZone(tz);

        assertThat(tariffZoneService.getTariffZoneById(tz.getId()))
                .extracting("zoneName")
                .isEqualTo(tz.getZoneName());
    }

    @Test
    @DisplayName("Update tariff zone when it doesn`t exists test.")
    public void whenUpdateTariffZoneWhichNotExistsAndThenThrow() {
        var tz = new TariffZone(1, "First");

        assertThatThrownBy(() -> tariffZoneService.updateTariffZone(tz))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("Delete tariff zone test.")
    public void whenTariffZoneExistsAndDeletedThenGetNoTariffZonesFromDatabase() {
        var tz = new TariffZone(1, "First");
        tariffZoneService.saveTariffZone(tz);
        tariffZoneService.deleteTariffZone(tz.getId());

        assertThat(tariffZoneService.getAllTariffZones().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Get all tariff zones test.")
    public void whenHaveTwoTariffZonesAndGetAllTariffZonesThenGetAllTwoTariffZonesFromDatabase() {
        var tz1 = new TariffZone(1, "First");
        var tz2 = new TariffZone(2, "Second");
        tariffZoneService.saveTariffZone(tz1);
        tariffZoneService.saveTariffZone(tz2);

        assertThat(tariffZoneService.getAllTariffZones().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Get tariff zone by id test.")
    public void whenGetTariffZoneByIdThenGetTariffZoneFromDatabase() {
        var tz = new TariffZone(1, "First");
        tariffZoneService.saveTariffZone(tz);

        assertThat(tariffZoneService.getTariffZoneById(tz.getId())).isEqualTo(tz);
    }

    @Test
    @DisplayName("Get tariff zone by id which doesn`t exists test.")
    public void whenGetTariffZoneByIdWhichNotExistsThen() {
        var tz = new TariffZone(1, "First");

        assertThatThrownBy(() -> tariffZoneService.getTariffZoneById(tz.getId()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("Get template test.")
    //Resource getTemplate();
    public void test5() {
        //todo
    }

    @Test
    @DisplayName("Upload tariff zones test.")
    public void whenUploadTariffZonesFromFileThenGetAllTariffZonesFromDatabase() throws IOException {
        Path path = Path.of("src/test/resources/testdata/tariffZones.xlsm");

        tariffZoneService.uploadTariffZones(
                new MockMultipartFile("file", "file",
                        "text/plain", Files.readAllBytes(path)));
    }
}