package ru.bk.j3000.normarchivedata.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.bk.j3000.normarchivedata.model.Source;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ActiveProfiles("test")
public class SourceRepositoryTest {

    private final SourceRepository repository;

    @Test
    @DisplayName("1. Save Source test")
    public void whenAddSourceToDBThenItsThere() {
        Source s1 = new Source(null, "name", "address",
                1.45F, 8, "Moscow");
        repository.save(s1);

        assertThat(repository.findAll().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("2. Delete Source test")
    public void whenAddSourceToDBAndDeleteItThenDBIsEmpty() {
        Source s1 = new Source(null, "name", "address",
                1.45F, 8, "Moscow");
        repository.save(s1);

        assertThat(repository.findAll().size()).isEqualTo(1);

        repository.deleteById(s1.getId());

        assertThat(repository.findAll().isEmpty()).isTrue();
    }

    @Test
    @DisplayName("3. Find Source test")
    public void whenAddSourceToDbAndFindItThenSourceIsFound() {
        Source s1 = new Source(null, "name", "address",
                1.45F, 8, "Moscow");
        repository.save(s1);

        assertThat(repository.findById(s1.getId()).get()).isEqualTo(s1);
    }
}
