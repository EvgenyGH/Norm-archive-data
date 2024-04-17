package ru.bk.j3000.normarchivedata.exception;

import lombok.Getter;

public class FileParseException extends StandardException {
    @Getter
    private final int lineNumber;

    public FileParseException(String message, String objectName, int lineNumber) {
        super(message, objectName);
        this.lineNumber = lineNumber;
    }

    @Override
    public String getErrorReport() {
        return String.format("%s. Line: %d.", super.getErrorReport(), lineNumber);
    }
}