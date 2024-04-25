package ru.bk.j3000.normarchivedata.service.admin;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import ru.bk.j3000.normarchivedata.model.admin.SECURITY_ROLES;
import ru.bk.j3000.normarchivedata.model.dto.UserDTO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;
    private final String selectAllAuthorities = "SELECT u.username as name, a.authority " +
            "FROM users u " +
            "LEFT JOIN authorities a ON u.username=a.username " +
            "ORDER by name, authority";

    @Override
    public List<UserDTO> getAllUsers() {
        List<UserDTO> usersDTO = jdbcTemplate.query(selectAllAuthorities, this::userDTOrowMap);

        log.info("All usernames and authorities received from database ({} in total).", usersDTO.size());

        return usersDTO;
    }

    private UserDTO userDTOrowMap(ResultSet resultSet, int i) throws SQLException {
        return new UserDTO(resultSet.getString("name"),
                SECURITY_ROLES.valueOf(resultSet.getString("authority")));
    }


    @Override
    public void createUser(UserDTO userDTO) {
        userDetailsManager.createUser(User.withUsername(userDTO.getName())
                .roles(userDTO.getRole().getRole())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .build());

        log.info("User created: name: {}, authority: {}.", userDTO.getName(), userDTO.getRole().getRole());
    }

    @Override
    public void deleteUserByName(String name) {
        userDetailsManager.deleteUser(name);

        log.info("User {} deleted.", name);
    }

    @Override
    public void changeUserAuthorityAndPassword(UserDTO userDTO) {
        var userDetails = userDetailsManager.loadUserByUsername(userDTO.getName());
        UserDetails newUserDetails = User.withUsername(userDTO.getName())
                .password(userDTO.getPassword().isBlank() ? userDetails.getPassword()
                        : passwordEncoder.encode(userDTO.getPassword()))
                .roles(userDTO.getRole().getRole())
                .build();

        userDetailsManager.updateUser(newUserDetails);

        log.info("User role {}assigned. Name: {}, role: {}.",
                userDTO.getPassword().isBlank() ? "" : "and password ",
                userDTO.getName(), userDTO.getRole().getRole());
    }

    @Override
    public UserDTO getUserByName(String name) {
        UserDTO user;

        if (userDetailsManager.userExists(name)) {
            var userDetails = userDetailsManager.loadUserByUsername(name);
            user = new UserDTO(userDetails.getUsername(),
                    SECURITY_ROLES.valueOf(userDetails.getAuthorities()
                            .iterator().next().getAuthority()));
        } else {
            throw new EntityNotFoundException("User not found. Name " + name);
        }

        log.info("User {} found. UserDTO created.", name);

        return user;
    }
}