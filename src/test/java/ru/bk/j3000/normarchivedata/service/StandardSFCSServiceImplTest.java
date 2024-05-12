package ru.bk.j3000.normarchivedata.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.bk.j3000.normarchivedata.model.FUEL_TYPE;
import ru.bk.j3000.normarchivedata.model.StandardSFC;
import ru.bk.j3000.normarchivedata.model.dto.SsfcShortDTO;
import ru.bk.j3000.normarchivedata.model.dto.SsfcsDTO;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Transactional
@Sql(scripts = {"/testData.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public class StandardSFCSServiceImplTest {
    private final StandardSFCServiceImpl ssfcService;

    @Test
    @DisplayName("Get ssfc by id test.")
    void whenFindSsfcByIdThenSsfcFound() {
        UUID id = UUID.fromString("1181e4f0-7968-4ac7-a1a7-acf45c74ca10");
        assertThat(ssfcService.getSsfcById(id).getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("Get ssfc by id wich not exists test.")
    void whenFindSsfcByIdWhichNotExistsThenEntityNotFoundExceptionThrown() {
        UUID id = UUID.randomUUID();
        assertThatThrownBy(() -> ssfcService.getSsfcById(id))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("Get all ssfcs by year test.")
    void whenFindAllSsfcByYearThenAllSsfcsFound() {
        int year = 2024;

        assertThat(ssfcService.findAllSsfcByYear(year)).hasSize(1620);
    }

    @Test
    @DisplayName("Get all ssfcs by year with no ssfcs test.")
    void FoundZeroSsfcs() {
        int year = 2023;

        assertThat(ssfcService.findAllSsfcByYear(year)).isEmpty();
    }

    @Test
    @DisplayName("Get all ssfcs by year and source id test.")
    void whenFindSsfcsByYearAndSourceIdThenTwelveSsfcsFound() {
        int year = 2024;
        UUID srcId = UUID.fromString("07fac330-6d30-4b07-941e-7e5f168c0957");

        assertThat(ssfcService.findAllSsfcByYearAndSrcId(year, srcId)).hasSize(12);
    }

    @Test
    @DisplayName("Get all ssfcs by year and source id with no ssfcs test.")
    void whenFindSsfcsByYearAndSourceIdThenFoundZeroSsfcs() {
        int year = 2023;

        UUID srcId = UUID.fromString("07fac330-6d30-4b07-941e-7e5f168c0957");

        assertThat(ssfcService.findAllSsfcByYearAndSrcId(year, srcId)).hasSize(0);
    }

    @Test
    @DisplayName("Get all sources with defined ssfcs for particular year test.")
    void whenFindAllSrcsWithDefinedSsfcsByYearThenAllSrcsFound() {
        int year = 2024;

        assertThat(ssfcService.findAllDefinedSourcesByYear(year)).hasSize(135);
    }

    @Test
    @DisplayName("Get all sources with defined ssfcs for particular year with no defined ssfcs test.")
    void whenFindAllSrcsWithDefinedSsfcsWithNoDefinedSfcsByYearThenZeroFound() {
        int year = 2023;
        UUID srcId = UUID.fromString("07fac330-6d30-4b07-941e-7e5f168c0957");

        assertThat(ssfcService.findAllSsfcByYearAndSrcId(year, srcId)).hasSize(0);
    }

    @Test
    @DisplayName("Get all ssfcs by source id, yer and fuel type test.")
    void whenFindAllSrcsSrcIdAndByYearAndByFuelTypeThenAllSrcsFound() {
        int year = 2024;
        UUID srcId = UUID.fromString("07fac330-6d30-4b07-941e-7e5f168c0957");

        assertThat(ssfcService.findAllSsfcByYearAndSrcIdAndFuelType(year, srcId, FUEL_TYPE.GAS))
                .hasSize(12);
    }

    @Test
    @DisplayName("Get all ssfcs by source id, yer and fuel type with no ssfcs exists test.")
    void whenFindAllSrcsSrcIdAndByYearAndByFuelTypeWhichNotExistsThenZeroFound() {
        int year = 2024;
        UUID srcId = UUID.fromString("07fac330-6d30-4b07-941e-7e5f168c0957");

        assertThat(ssfcService.findAllSsfcByYearAndSrcIdAndFuelType(year, srcId, FUEL_TYPE.DIESEL))
                .hasSize(0);
    }

    @Test
    @DisplayName("Update ssfc test (generation).")
    void whenUpdateSsfcGenerationThenSffcIsUpdated() {
        UUID ssfcId = UUID.fromString("1181e4f0-7968-4ac7-a1a7-acf45c74ca10");
        int year = 2024;

        StandardSFC ssfc = ssfcService.getSsfcById(ssfcId);
        ssfc.setGeneration(1000d);
        SsfcsDTO dto = new SsfcsDTO(List.of(ssfc));

        ssfcService.updateSsfc(dto, year, FUEL_TYPE.GAS.getName());

        assertThat(ssfcService.getSsfcById(ssfcId).getGeneration()).isEqualTo(1000d);
    }

    @Test
    @DisplayName("Update ssfc test (update Fuel type) when data for fuel type already exists.")
    void whenUpdateSsfcFuelTypeToOneWithDataExistsThenThrowEntityExistsException() {
        UUID ssfcId = UUID.fromString("1181e4f0-7968-4ac7-a1a7-acf45c74ca10");
        int year = 2024;

        StandardSFC ssfc = ssfcService.getSsfcById(ssfcId);
        ssfc.setFuelType(FUEL_TYPE.GAS);
        SsfcsDTO dto = new SsfcsDTO(List.of(ssfc));


        assertThatThrownBy(() -> ssfcService.updateSsfc(dto, year, FUEL_TYPE.DIESEL.getName()))
                .isInstanceOf(EntityExistsException.class);
    }

    @Test
    @DisplayName("Update ssfc test (Fuel type).")
    void whenUpdateSsfcFuelTypeThenSffcIsUpdated() {
        UUID ssfcId = UUID.fromString("1181e4f0-7968-4ac7-a1a7-acf45c74ca10");
        int year = 2024;

        StandardSFC ssfc = ssfcService.getSsfcById(ssfcId);
        SsfcsDTO dto = new SsfcsDTO(List.of(ssfc));
        dto.setFuelType(FUEL_TYPE.DIESEL.getName());

        ssfcService.updateSsfc(dto, year, FUEL_TYPE.GAS.getName());

        assertThat(ssfcService.getSsfcById(ssfcId).getFuelType()).isEqualTo(FUEL_TYPE.DIESEL);
    }

    @Test
    @DisplayName("Add ssfc test.")
    void whenAddSsfcThenSffcIsAdded() {
        UUID srcId = UUID.fromString("a82b2c59-577d-4560-9176-909a3b21645c");
        int year = 2024;

        SsfcShortDTO shortDto = new SsfcShortDTO(null,
                year,
                12,
                1d,
                1d,
                1d,
                1d,
                1d);

        SsfcsDTO dto = new SsfcsDTO("РТС \"Внуково\"",
                srcId,
                null,
                FUEL_TYPE.DIESEL.getName(),
                null,
                null,
                List.of(shortDto));

        ssfcService.addSsfc(dto, year);

        assertThat(ssfcService.findAllSsfcByYearAndSrcIdAndFuelType(year, srcId, FUEL_TYPE.DIESEL))
                .hasSize(1);
    }

    @Test
    @DisplayName("Add ssfc when data already exists test.")
    void whenAddSsfcWhichExistsThenThrowEntityExistsException() {
        UUID srcId = UUID.fromString("a82b2c59-577d-4560-9176-909a3b21645c");
        int year = 2024;

        SsfcShortDTO shortDto = new SsfcShortDTO(null,
                year,
                12,
                1d,
                1d,
                1d,
                1d,
                1d);

        SsfcsDTO dto = new SsfcsDTO("РТС \"Внуково\"",
                srcId,
                null,
                FUEL_TYPE.GAS.getName(),
                null,
                null,
                List.of(shortDto));

        assertThatThrownBy(() -> ssfcService.addSsfc(dto, year))
                .isInstanceOf(EntityExistsException.class);
    }

    @Test
    @DisplayName("Delete ssfc by id test.")
    void whenDeleteSsfcByIdThenSffcIsDeleted() {
        UUID id = UUID.fromString("1181e4f0-7968-4ac7-a1a7-acf45c74ca10");
        ssfcService.deleteSsfcById(id);

        assertThatThrownBy(() -> ssfcService.getSsfcById(id)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("Delete ssfc by ids test.")
    void whenDeleteSsfcByIdsThenSffcIsDeleted() {
        UUID id = UUID.fromString("1181e4f0-7968-4ac7-a1a7-acf45c74ca10");
        ssfcService.deleteSsfcByIds(List.of(id));

        assertThatThrownBy(() -> ssfcService.getSsfcById(id)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("Delete ssfc by source id, year and fuel type test.")
    void whenDeleteSsfcsBySrcIdAndYearAndFuelTypeThenSffcsDeleted() {
        UUID id = UUID.fromString("1181e4f0-7968-4ac7-a1a7-acf45c74ca10");
        UUID srcId = UUID.fromString("87526201-5129-444d-a385-1588b373c29b");
        int year = 2024;

        ssfcService.deleteSsfcBySrcIdAndYearAndFuelType(srcId, year, FUEL_TYPE.GAS);

        assertThatThrownBy(() -> ssfcService.getSsfcById(id)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("Get ssfcs template test.")
    public void whenGetTemplateAndUploadIdThenNoExceptionsThrown() throws IOException {
        Resource resource = ssfcService.getSsfcTemplate();
        MockMultipartFile file = new MockMultipartFile("file.xlsm",
                resource.getContentAsByteArray());
        assertThatNoException().isThrownBy(() -> ssfcService.uploadSsfc(file, 2024));
    }

    @Test
    @DisplayName("Upload ssfcs from file test.")
    public void whenUploadSsfcsFromFileAndGetAllSsfcsThenAllSsfcsFound() throws IOException {
        Resource resource = new ClassPathResource("/testdata/ssfcs.xlsm");
        MockMultipartFile file = new MockMultipartFile("file.xlsm", resource.getContentAsByteArray());
        ssfcService.uploadSsfc(file, 2024);
        assertThat(ssfcService.findAllSsfcByYear(2024)).hasSize(1620);
    }

    @Test
    @DisplayName("Convert SsfcDTO to ssfc test.")
    public void whenCallToStandardSFCsWithDtoThenReceiveStandardSFCsList() {
        UUID srcId = UUID.fromString("87526201-5129-444d-a385-1588b373c29b");
        int year = 2024;

        List<StandardSFC> list = ssfcService
                .findAllSsfcByYearAndSrcIdAndFuelType(year, srcId, FUEL_TYPE.GAS);

        SsfcsDTO dto = new SsfcsDTO(list);

        assertThat(ssfcService.toStandartSFCs(dto)).isEqualTo(list);
    }
}
