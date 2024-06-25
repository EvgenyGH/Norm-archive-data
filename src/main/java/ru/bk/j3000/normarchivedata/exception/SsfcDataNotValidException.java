package ru.bk.j3000.normarchivedata.exception;

import lombok.Getter;

import java.util.List;


@Getter
public class SsfcDataNotValidException extends StandardException {
    private final List<String> errors;

    public SsfcDataNotValidException(String message, String objectName,
                                     List<String> errors) {
        super(message, objectName);
        this.errors = errors;
    }
}