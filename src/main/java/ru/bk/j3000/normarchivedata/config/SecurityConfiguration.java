package ru.bk.j3000.normarchivedata.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
public class SecurityConfiguration {
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsManager userDetailsManager(PasswordEncoder encoder, DataSource dataSource) {
        var manager = new JdbcUserDetailsManager(dataSource);

        if (!manager.userExists("admin")) {
            manager.createUser(User.withUsername("admin")
                    .password(encoder.encode("12345"))
                    .roles("ADMIN")
                    .build());
        }

        if (!manager.userExists("user")) {
            manager.createUser(User.withUsername("user")
                    .password(encoder.encode("54321"))
                    .roles("USER")
                    .build());
        }

        return manager;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(Customizer.withDefaults());
        http.cors(Customizer.withDefaults());

        http.httpBasic(Customizer.withDefaults());

        http.formLogin(conf -> conf
                .defaultSuccessUrl("/source", true));

        http.authorizeHttpRequests(conf ->
                conf
                        .requestMatchers("/source/**",
                                "/sourceproperty/**",
                                "/tariffzone/**",
                                "/ssfc/**",
                                "/report/**",
                                "/css/*")
                        .hasRole("USER")
                        .requestMatchers("/user/**")
                        .hasRole("ADMIN")
                        .anyRequest()
                        .denyAll()
        );

        return http.build();
    }

    @Bean
    RoleHierarchy roleHierarchy() {
        var hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");
        return hierarchy;
    }
}
