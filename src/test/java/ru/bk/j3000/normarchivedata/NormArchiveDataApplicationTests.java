package ru.bk.j3000.normarchivedata;

import org.junit.jupiter.api.Test;
import ru.bk.j3000.normarchivedata.model.admin.SECURITY_ROLES;


class NormArchiveDataApplicationTests {

    @Test
    void contextLoads() {
        System.out.println(SECURITY_ROLES.valueOf(SECURITY_ROLES.ROLE_ADMIN.name()));
    }

}
