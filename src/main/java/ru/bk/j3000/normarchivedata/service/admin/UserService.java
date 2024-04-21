package ru.bk.j3000.normarchivedata.service.admin;


import ru.bk.j3000.normarchivedata.model.UserDTO;

import java.util.List;

public interface UserService {
    List<UserDTO> getAllUsers();
}
