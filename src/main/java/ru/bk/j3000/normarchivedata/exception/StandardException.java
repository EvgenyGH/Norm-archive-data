package ru.bk.j3000.normarchivedata.exception;

import lombok.Getter;

@Getter
public class StandardException extends RuntimeException {
    private final String objectName;

    public StandardException(String message, String objectName) {
        super(message);
        this.objectName = objectName;
    }

    public String getErrorReport() {
        return String.format("Объект: %s. %s.", getMessage(), objectName);
    }
}
