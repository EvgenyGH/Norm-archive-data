package ru.bk.j3000.normarchivedata.service.admin;

import ru.bk.j3000.normarchivedata.model.UserDTO;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserDTO> getAllUsers();

    void createUser(UserDTO userDTO);

    void deleteUserByName(String name);

    void changeUserAuthorityAndPassword(UserDTO userDTO);

    Optional<UserDTO> getUserByName(String name);
}
