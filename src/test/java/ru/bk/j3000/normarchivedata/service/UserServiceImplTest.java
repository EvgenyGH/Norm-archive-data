package ru.bk.j3000.normarchivedata.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.bk.j3000.normarchivedata.model.UserDTO;
import ru.bk.j3000.normarchivedata.model.admin.SECURITY_ROLES;
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
        UserDetailsManager userDetailsManager(DataSource dataSource) {
            return new JdbcUserDetailsManager(dataSource);
        }
    }

    @Test
    @DisplayName("Get all users test.")
    public void whenSaveThreeUsersThenGetThreeUsersFromDatabase() {
        IntStream.rangeClosed(1, 3).forEach(i -> userService
                .createUser(new UserDTO("user" + i, "psw" + i, SECURITY_ROLES.ROLE_USER)));

        assertThat(userService.getAllUsers()).hasSize(3);
    }

    @Test
    @DisplayName("Create User test.")
    public void whenCreateUserThenGetUserFromDatabase() {
        userService.createUser(new UserDTO("user", "psw", SECURITY_ROLES.ROLE_USER));

        assertThat(userService.getAllUsers())
                .hasSize(1)
                .element(0)
                .extracting("name")
                .isEqualTo("user");
    }

    @Test
    @DisplayName("Change User authority test.")
    public void whenChangeUserAuthorityThenGetUpdatedUserAuthorityFromDatabase() {
        var user = new UserDTO("user", "psw", SECURITY_ROLES.ROLE_USER);

        userService.createUser(user);

        user = new UserDTO("user", "psw", SECURITY_ROLES.ROLE_ADMIN);

        userService.changeUserAuthorityAndPassword(user);

        assertThat(userService.getAllUsers())
                .hasSize(1)
                .element(0)
                .extracting("role")
                .isEqualTo(SECURITY_ROLES.ROLE_ADMIN);
    }

    @Test
    @DisplayName("Delete User By Name test.")
    public void whenDeleteUserByNameThenUserIsDeletedFromDatabase() {
        userService.createUser(new UserDTO("user", "psw", SECURITY_ROLES.ROLE_USER));
        userService.deleteUserByName("user");

        assertThat(userService.getAllUsers()).hasSize(0);
    }
}
