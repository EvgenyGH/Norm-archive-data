package ru.bk.j3000.normarchivedata.exception;

public class FileReadException extends StandardException {
    public FileReadException(String message, String objectName) {
        super(message, objectName);
    }
}
