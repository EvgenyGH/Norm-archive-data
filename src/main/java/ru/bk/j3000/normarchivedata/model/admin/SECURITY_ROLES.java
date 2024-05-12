package ru.bk.j3000.normarchivedata.model.admin;

public enum SECURITY_ROLES {
    ROLE_ADMIN {
        @Override
        public String getRole() {
            return "ADMIN";
        }

        @Override
        public String getRoleName() {
            return "Администратор";
        }
    },
    ROLE_EXPERT {
        @Override
        public String getRole() {
            return "EXPERT";
        }

        @Override
        public String getRoleName() {
            return "Эксперт";
        }
    },
    ROLE_USER {
        @Override
        public String getRole() {
            return "USER";
        }

        @Override
        public String getRoleName() {
            return "Пользователь";
        }
    };

    abstract public String getRole();

    abstract public String getRoleName();
}
