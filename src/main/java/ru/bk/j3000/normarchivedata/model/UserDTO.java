package ru.bk.j3000.normarchivedata.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String name;
    private String password;
    private String authority;

    public UserDTO(String name, String authority) {
        this.authority = authority;
        this.name = name;
        this.password = null;
    }
}
