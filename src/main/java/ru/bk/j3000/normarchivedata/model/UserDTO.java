package ru.bk.j3000.normarchivedata.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bk.j3000.normarchivedata.model.admin.SECURITY_ROLES;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String name;
    private String password;
    private SECURITY_ROLES role;

    public UserDTO(String name, SECURITY_ROLES role) {
        this.role = role;
        this.name = name;
        this.password = null;
    }
}
