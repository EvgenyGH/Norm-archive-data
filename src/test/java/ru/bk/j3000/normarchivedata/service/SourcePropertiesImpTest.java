package ru.bk.j3000.normarchivedata.service;

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
import ru.bk.j3000.normarchivedata.model.*;
import ru.bk.j3000.normarchivedata.model.dto.SourcePropertyDTO;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Transactional
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Sql(scripts = {"/testData.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public class SourcePropertiesImpTest {
    private final SourcePropertyService srcPropService;
    private final SourceService sourceService;
    private final TariffZoneService tariffZoneService;
    private final BranchService branchService;

    @Test
    @DisplayName("Test get all source properties method.")
    public void whenSaveOneSourcePropertyAndGetAllSourcePropertiesThenNumberSourcePropertiesIncreasedByOne() {
        int propertiesNumber = srcPropService.findAllPropDTOByYear(2023).size();
        SourceProperty sourceProperty = createTestSourceProperty(2023, 1,
                1, null);
        srcPropService.addSourceProperty(new SourcePropertyDTO(sourceProperty));

        assertThat(srcPropService.findAllPropDTOByYear(2023).size())
                .isEqualTo(propertiesNumber + 1);
    }

    public SourceProperty createTestSourceProperty(Integer year, Integer zoneId,
                                                   Integer branchId, UUID sourceId) {
        Source source = Objects.isNull(sourceId)
                ? sourceService.getAllSources().get(10)
                : sourceService.getSourceById(sourceId).orElseThrow();
        SrcPropertyId id = new SrcPropertyId(source, year);
        TariffZone tz = tariffZoneService.getTariffZoneById(zoneId);
        Branch br = branchService.getBranchById(branchId);

        return new SourceProperty(id, br, tz);
    }

    @Test
    @DisplayName("Test delete source property method.")
    public void whenAddSrcPropertyAndDeleteSrcPropertyThenNumberOfSrcPropertiesNotChanged() {
        int propertiesNumber = srcPropService.findAllPropDTOByYear(2023).size();
        SourceProperty sourceProperty = createTestSourceProperty(2023, 1,
                1, null);
        srcPropService.addSourceProperty(new SourcePropertyDTO(sourceProperty));
        srcPropService.deleteSrcPropertyById(sourceProperty.getId().getSource().getId(),
                2023);

        assertThat(srcPropService.findAllPropDTOByYear(2023).size())
                .isEqualTo(propertiesNumber);
    }

    @Test
    @DisplayName("Test get source property by id method.")
    public void whenSaveSourcePropertyAndGetSourcePropertyByIdThenSourceIdAndYearAreEqual() {
        SourceProperty sourceProperty = createTestSourceProperty(2023, 1,
                1, null);
        srcPropService.addSourceProperty(new SourcePropertyDTO(sourceProperty));

        assertThat(srcPropService.getSourcePropertyById(sourceProperty.getId().getSource().getId()
                , 2023).getSource().getSourceId()).isEqualTo(sourceProperty
                .getId().getSource().getId());
        assertThat(srcPropService.getSourcePropertyById(sourceProperty
                        .getId().getSource().getId()
                , 2023).getYear()).isEqualTo(sourceProperty.getId().getYear());
    }

    @Test
    @DisplayName("Test update source property method.")
    public void whenUpdateSrcPropertyBranchAndGetSrcPropertyThenSrcPropertyBranchIsUpdated() {
        SourceProperty sourceProperty = createTestSourceProperty(2023, 1,
                1, null);
        srcPropService.addSourceProperty(new SourcePropertyDTO(sourceProperty));
        sourceProperty.setBranch(branchService.getBranchById(19));
        srcPropService.updateSourceProperty(new SourcePropertyDTO(sourceProperty));

        assertThat(srcPropService.getSourcePropertyById(sourceProperty
                        .getId().getSource().getId(), 2023)
                .getBranch().getBranchId())
                .isEqualTo(19);
    }

    @Test
    @DisplayName("Test add source property method.")
    public void whenSaveSrcPropertyAndRetrieveAllSrcPropertiesThenNumberOfSrcPropertiesIncreasedByOne() {
        int propertiesNumber = srcPropService.findAllPropDTOByYear(2023).size();
        SourceProperty sourceProperty = createTestSourceProperty(2023, 1,
                1, null);
        srcPropService.addSourceProperty(new SourcePropertyDTO(sourceProperty));

        assertThat(srcPropService.findAllPropDTOByYear(2023).size())
                .isEqualTo(propertiesNumber + 1);
    }

    @Test
    @DisplayName("Test get source properties template method.")
    public void whenGetTemplateAndUploadIdThenNoExceptionsThrown() throws IOException {
        Resource resource = srcPropService.getSourcePropertyTemplate();
        MockMultipartFile file = new MockMultipartFile("file.xlsm",
                resource.getContentAsByteArray());
        assertThatNoException().isThrownBy(() -> srcPropService.uploadSourceProperties(file, 2024));

    }

    @Test
    @DisplayName("Test upload source properties from file method.")
    public void whenUploadSrcPropertiesFromFileAndGetAllSrcPropertiesThenAllSrcPropertiesFound()
            throws IOException {
        Resource resource = new ClassPathResource("/testdata/sourceProperties.xlsm");
        MockMultipartFile file = new MockMultipartFile("file.xlsm", resource.getContentAsByteArray());
        srcPropService.uploadSourceProperties(file, 2022);
        assertThat(srcPropService.findAllPropDTOByYear(2022)).hasSize(136);
    }
}
