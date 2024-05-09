package ru.bk.j3000.normarchivedata.model;

public enum FUEL_TYPE {
    GAS {
        @Override
        public String getName(){
            return "Газ";
      }
    },
    DIESEL {
        @Override
        public String getName(){
            return "Дизельное топливо";
        }
    };

    abstract public String getName();
}
