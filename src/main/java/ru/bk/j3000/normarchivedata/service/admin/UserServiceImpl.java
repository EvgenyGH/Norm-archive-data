package ru.bk.j3000.normarchivedata.service.admin;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import ru.bk.j3000.normarchivedata.model.UserDTO;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDetailsManager userDetailsManager;


    @Override
    public List<UserDTO> getAllUsers() {


        return List.of();
    }
}
