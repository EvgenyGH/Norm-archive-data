package ru.bk.j3000.normarchivedata.exception;

import lombok.Getter;

@Getter
public class standardException extends RuntimeException {
    private final String objectName;

    public standardException(String message, String objectName) {
        super(message);
        this.objectName = objectName;
    }

    public String getErrorReport() {
        return String.format("%s. Объект: %s.", getMessage(), objectName);
    }
}
