package ru.bk.j3000.normarchivedata.service.admin;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import ru.bk.j3000.normarchivedata.model.UserDTO;

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
                resultSet.getString("authority"));
    }


    @Override
    public void createUser(UserDTO userDTO) {
        userDetailsManager.createUser(User.withUsername(userDTO.getName())
                .authorities(userDTO.getAuthority())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .build());

        log.info("User created: name: {}, authority: {}.", userDTO.getName(), userDTO.getAuthority());
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
                .password(userDTO.getPassword().isBlank() ?
                        userDetails.getPassword() : passwordEncoder.encode(userDTO.getPassword()))
                .authorities(userDTO.getAuthority().isBlank() ?
                        userDetails.getAuthorities() :
                        List.of(new SimpleGrantedAuthority(userDTO.getAuthority())))
                .build();

        userDetailsManager.updateUser(newUserDetails);

        log.info("User authority {}changed. Name: {}, authority: {}.",
                userDTO.getPassword().isBlank() ? "" : "and password ",
                newUserDetails.getUsername(), newUserDetails.getAuthorities());
    }
}
