package ru.bk.j3000.normarchivedata.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

import javax.sql.DataSource;

@TestConfiguration
public class SecurityConfig {
    @Bean
    UserDetailsManager userDetails(PasswordEncoder encoder, DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource);
    }
}
