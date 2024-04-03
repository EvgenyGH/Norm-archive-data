package ru.bk.j3000.normarchivedata.model;

public enum SOURCE_TYPE {
    RTS {
        @Override
        public String getName(){
            return "РТС";
        }
    },
    KTS {
        @Override
        public String getName(){
            return "КТС";
        }
    },
    AIT {
        @Override
        public String getName(){
            return "АИТ";
        }
    },
    MK {
        @Override
        public String getName(){
            return "МК";
        }
    },
    PK {
        @Override
        public String getName(){
            return "ПК";
        }
    };

    abstract public String getName();
}
