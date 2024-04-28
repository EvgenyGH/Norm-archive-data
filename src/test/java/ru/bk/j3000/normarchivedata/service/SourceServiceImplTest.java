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
import org.springframework.test.context.jdbc.Sql;
import ru.bk.j3000.normarchivedata.model.SOURCE_TYPE;
import ru.bk.j3000.normarchivedata.model.Source;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static java.nio.file.Files.readAllBytes;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Transactional
public class SourceServiceImplTest {
    private final SourceServiceImpl service;
    private final SourcePropertyService srcPropService;
    private final File file = new File("src/test/resources/testdata/sources.xlsm");

    @Test
    @DisplayName("Upload sources from file")
    public void whenReadSourcesFromFileAndGetSourcesFromDataBaseThenSourcesNumberIsEqual() throws IOException {
        service.uploadSources(new MockMultipartFile(file.getName(), file.getName(), "text/plain",
                readAllBytes(file.toPath())));

        assertThat(service.getAllSources().size()).isEqualTo(141);
    }

    @Test
    @DisplayName("Save sources")
    public void whenSaveSourcesToDatabaseAndGetSourcesFromDataBaseThenSourcesNumberIsEqual() {
        List<Source> sources = List.of(new Source(null, "name1", "address1", SOURCE_TYPE.KTS),
                new Source(null, "name1", "address1", SOURCE_TYPE.RTS));
        service.saveSources(sources);

        assertThat(service.getAllSources().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Save source")
    public void whenSaveSourceToDatabaseAndGetSourceFromDataBaseThenSourceIsOneAndNameIsEqual() {
        Source source = new Source(null, "name1", "address1", SOURCE_TYPE.KTS);
        service.saveSource(source);

        assertThat(service.getAllSources().size()).isEqualTo(1);
        assertThat(service.getAllSources().getFirst().getName()).isEqualTo(source.getName());
    }

    @Test
    @DisplayName("Save source when it exists")
    public void whenSaveSourceToDatabaseAndItAlreadyExistsThenThrowEntityExistsException() {
        Source source = new Source(null, "name1", "address1", SOURCE_TYPE.KTS);
        service.saveSource(source);

        assertThatThrownBy(() -> service.saveSource(source)).isInstanceOf(EntityExistsException.class);
    }

    @Test
    @DisplayName("Delete all sources")
    public void whenAddTwoSourcesToDatabaseAndDeleteAllSourcesFromDataBaseThenSourcesNumberNull() {
        List<Source> sources = List.of(new Source(null, "name1", "address1", SOURCE_TYPE.KTS),
                new Source(null, "name1", "address1", SOURCE_TYPE.RTS));
        service.saveSources(sources);
        service.deleteAllSources();

        assertThat(service.getAllSources()).isEmpty();
    }

    @Test
    @DisplayName("Delete sources by ids")
    public void whenSaveSourceToDatabaseAndDeleteSourceFromDataBaseByIdThenSourceNumberIsNull() {
        Source source = new Source(null, "name1", "address1", SOURCE_TYPE.KTS);
        service.saveSource(source);
        service.deleteSourcesById(Collections.singletonList(source.getId()));

        assertThat(service.getAllSources()).isEmpty();
    }

    @Test
    @DisplayName("Update source")
    public void whenSaveSourceToDatabaseAndUpdateSourceNameThenSourceNameIsUpdated() {
        Source source = new Source(null, "name1", "address1", SOURCE_TYPE.KTS);
        service.saveSource(source);
        source.setName("updatedName");
        service.updateSource(source);

        assertThat(service.getAllSources().getFirst().getName()).isEqualTo("updatedName");
    }

    @Test
    @DisplayName("Update source when it does not exists")
    public void whenUpdateSourceAndItNotExistsThenThrowEntityNotFoundException() {
        Source source = new Source(UUID.randomUUID(), "name1", "address1", SOURCE_TYPE.KTS);

        assertThatThrownBy(() -> service.updateSource(source)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("Get source Ids and Names DTO list with no properties assigned")
    @Sql(scripts = {"/testData.sql"})
    public void whenGetSourceIdsAndNamesDTOListThenGetSourceDTOListWithNoPropertiesAssigned() {
        assertThat(service.getAllSources()).hasSize(141);
        assertThat(srcPropService.findAllPropByYear(2023)).hasSize(3);
        assertThat(service.getSourceIdsAndNamesWithNoProp(2023)).hasSize(138);
    }
}