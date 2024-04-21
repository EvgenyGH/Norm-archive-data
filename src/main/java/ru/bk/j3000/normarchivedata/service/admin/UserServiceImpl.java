package ru.bk.j3000.normarchivedata.service.admin;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.User;
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
    private final JdbcTemplate jdbcTemplate;
    private final String selectAllAuthorities = "SELECT * FROM authorities ORDER by authority";

    @Override
    public List<UserDTO> getAllUsers() {
        List<UserDTO> usersDTO = jdbcTemplate.query(selectAllAuthorities, this::userDTOrowMap);

        log.info("User authorities received from database ({} in total).", usersDTO.size());

        return usersDTO;
    }

    private UserDTO userDTOrowMap(ResultSet resultSet, int i) throws SQLException {
        return new UserDTO(resultSet.getString("username"),
                resultSet.getString("authority"));
    }


    @Override
    public void createUser(User user) {
        userDetailsManager.createUser(user);

        log.info("User created: name: {}, authority: {}.", user.getUsername(), user.getAuthorities());
    }

    @Override
    public void deleteUserByName(String name) {
        userDetailsManager.deleteUser(name);

        log.info("User {} deleted.", name);
    }

    @Override
    public void updateUser(User user) {
        userDetailsManager.updateUser(user);

        log.info("User updated. Name: {}, authority: {}.", user.getUsername(), user.getAuthorities());
    }
}
