package ru.bk.j3000.normarchivedata.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.bk.j3000.normarchivedata.model.Source;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SourceServiceImplTest {
    private final SourceServiceImpl service;

    @Test
    @DisplayName("Read sources from file")
    public void whenReadSourcesFromFileThenGetSourcesCollection() {
        List<Source> sources;
        File file = new File("src/test/resources/testdata/sources.xlsm");

        sources = service.readSrcFromFile(file);

        assertThat(sources.size()).isEqualTo(141);
    }
}