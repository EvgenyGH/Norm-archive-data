package ru.bk.j3000.normarchivedata.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.test.context.ActiveProfiles;
import ru.bk.j3000.normarchivedata.service.admin.UserServiceImpl;

import javax.sql.DataSource;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        properties = "spring.main.allow-bean-definition-overriding=true")
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Transactional
public class UserServiceImplTest {
    private final UserServiceImpl userService;
    private final UserDetailsManager manager;

    @TestConfiguration
    public static class TestSecurityConfig {
        @Bean
        UserDetailsManager userDetails(PasswordEncoder encoder, DataSource dataSource) {
            return new JdbcUserDetailsManager(dataSource);
        }
    }

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

    @Test
    @DisplayName("Create User test.")
    public void whenCreateUserThenGetUserFromDatabase() {
        userService.createUser((User) User
                .withUsername("user")
                .authorities("auth")
                .password("psw")
                .build());

        assertThat(userService.getAllUsers())
                .hasSize(1)
                .element(0)
                .extracting("name")
                .isEqualTo("user");
    }

    @Test
    @DisplayName("Change User authority test.")
    public void whenChangeUserAuthorityThenGetUpdatedUserAuthorityFromDatabase() {
        var user = User
                .withUsername("user")
                .authorities("auth")
                .password("psw")
                .build();

        userService.createUser((User) user);

        user = User
                .withUsername("user")
                .authorities("auth changed")
                .password("psw")
                .build();

        userService.changeUserAuthorityAndPassword((User) user);

        assertThat(userService.getAllUsers())
                .hasSize(1)
                .element(0)
                .extracting("authority")
                .isEqualTo("auth changed");
    }

    @Test
    @DisplayName("Delete User By Name test.")
    public void whenDeleteUserByNameThenUserIsDeletedFromDatabase() {

    }
}
