package ru.bk.j3000.normarchivedata.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import ru.bk.j3000.normarchivedata.model.SOURCE_TYPE;
import ru.bk.j3000.normarchivedata.model.Source;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static java.nio.file.Files.readAllBytes;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Transactional
public class SourceServiceImplTest {
    private final SourceServiceImpl service;
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
}