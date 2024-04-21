package ru.bk.j3000.normarchivedata.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserDTO {
    private String name;
    private String password;
    private List<String> authorities;
}
