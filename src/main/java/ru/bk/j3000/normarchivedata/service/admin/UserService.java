package ru.bk.j3000.normarchivedata.service.admin;

import ru.bk.j3000.normarchivedata.model.dto.UserDTO;

import java.util.List;

public interface UserService {
    List<UserDTO> getAllUsers();

    void createUser(UserDTO userDTO);

    void deleteUserByName(String name);

    void changeUserAuthorityAndPassword(UserDTO userDTO);

    UserDTO getUserByName(String name);
}
