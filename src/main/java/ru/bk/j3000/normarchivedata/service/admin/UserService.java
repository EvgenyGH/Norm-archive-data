package ru.bk.j3000.normarchivedata.service.admin;

import org.springframework.security.core.userdetails.User;
import ru.bk.j3000.normarchivedata.model.UserDTO;

import java.util.List;

public interface UserService {
    List<UserDTO> getAllUsers();

    void createUser(User user);

    void deleteUserByName(String name);

    void updateUser(User user);
}
