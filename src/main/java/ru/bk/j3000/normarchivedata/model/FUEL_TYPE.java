package ru.bk.j3000.normarchivedata.model;

public enum FUEL_TYPE {
    GAS {
        @Override
        public String getName() {
            return "Газ";
        }
    },
    DIESEL {
        @Override
        public String getName() {
            return "Дизельное топливо";
        }
    };

    abstract public String getName();

    public static FUEL_TYPE getByName(String name) {
        return switch (name) {
            case "Газ" -> FUEL_TYPE.GAS;
            case "Дизельное топливо" -> FUEL_TYPE.DIESEL;
            default -> throw new TypeNotPresentException(String.format("Неверное имя(%s) для FUEL_TYPE.", name),
                    new Throwable());
        };
    }
}