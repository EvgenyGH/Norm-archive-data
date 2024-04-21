package ru.bk.j3000.normarchivedata.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@SpringBootTest(classes = {ModelServiceImpl.class}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ModelServiceImplTest {
    private final ModelServiceImpl modelService;

    @Test
    @DisplayName("Test setting active menu attribute.")
    public void whenGetActiveMenuAttributeSourceThenGetAttributeWithActiveSource() {
        //assertThat(modelService.getActiveMenuAttribute("source")).contains(Map.entry("source", ""));
        //assertThat(modelService.getActiveMenuAttribute("ssfc")).doesNotContain(Map.entry("source", ""));
    }
}
