package ru.bk.j3000.normarchivedata.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.ActiveProfiles;
import ru.bk.j3000.normarchivedata.service.admin.UserServiceImpl;

import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        properties = "spring.main.allow-bean-definition-overriding=true")
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Transactional
public class UserServiceImplTest {
    private final UserServiceImpl userService;

    @Test
    @DisplayName("Get all users test.")
    public void whenSaveThreeUsersThenGetThreeUsersFromDatabase() {
        IntStream.rangeClosed(1, 3).forEach(i -> userService.createUser((User) User
                .withUsername("user" + i)
                .authorities("auth" + i)
                .password("psw" + i)
                .build()));

        assertThat(userService.getAllUsers()).hasSize(3);
    }
}
