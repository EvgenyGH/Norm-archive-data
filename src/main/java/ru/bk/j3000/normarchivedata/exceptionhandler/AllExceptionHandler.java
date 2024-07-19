package ru.bk.j3000.normarchivedata.exceptionhandler;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.EmptyFileException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.bk.j3000.normarchivedata.exception.EmptyPeriodException;
import ru.bk.j3000.normarchivedata.exception.ReportIOException;
import ru.bk.j3000.normarchivedata.exception.SsfcDataNotValidException;
import ru.bk.j3000.normarchivedata.exception.StandardException;
import ru.bk.j3000.normarchivedata.service.ModelService;

import java.io.IOException;
import java.security.InvalidParameterException;


@ControllerAdvice()
@RequiredArgsConstructor
@Slf4j
public class AllExceptionHandler {
    private final ModelService modelService;

    @ExceptionHandler({EntityNotFoundException.class})
    public String entityNotFoundExceptionHandler(Exception e, Model model, HttpServletRequest request) {
        model.addAllAttributes(modelService.getErrorAttributes(e,
                request.getRequestURI(),
                "Объект не найден (не существует).")
        );

        log.warn(e.getMessage());

        return "errorview";
    }

    @ExceptionHandler({EntityExistsException.class})
    public String entityExistsExceptionHandler(Exception e, Model model, HttpServletRequest request) {
        model.addAllAttributes(modelService.getErrorAttributes(e,
                request.getRequestURI(),
                "Объект уже существует."));

        log.warn(e.getMessage());

        return "errorview";
    }

    @ExceptionHandler({DataIntegrityViolationException.class})
    public String dataIntegrityViolationExceptionHandler(Exception e, Model model,
                                                         HttpServletRequest request) {
        model.addAllAttributes(modelService.getErrorAttributes(e,
                request.getRequestURI(),
                "Недопустимый формат данных."));

        log.warn(e.getMessage());

        return "errorview";
    }

    @ExceptionHandler({PersistenceException.class})
    public String persistenceExceptionHandler(Exception e, Model model,
                                              HttpServletRequest request) {
        model.addAllAttributes(modelService.getErrorAttributes(e,
                request.getRequestURI(),
                "Ошибка обработки данных (persistence)."));
        model.addAttribute("userInfo",
                "Неизвестная ошибка! Без паники. Все под контролем.");

        log.warn(e.getMessage());

        return "errorview";
    }

    @ExceptionHandler({ReportIOException.class})
    public String reportIOExceptionHandler(Exception e, Model model,
                                           HttpServletRequest request) {
        model.addAllAttributes(modelService.getErrorAttributes(e,
                request.getRequestURI(),
                "Ошибка формирования отчета."));

        log.warn(e.getMessage());

        return "errorview";
    }

    @ExceptionHandler({SsfcDataNotValidException.class})
    public String ssfcDataNotValidExceptionHandler(SsfcDataNotValidException e, Model model,
                                                   HttpServletRequest request) {

        model.addAllAttributes(modelService.getErrorAttributes(e,
                request.getRequestURI(),
                e.getErrors()));

        log.warn(e.getMessage());

        return "errorview";
    }

    @ExceptionHandler({StandardException.class})
    public String standardExceptionHandler(Exception e, Model model,
                                           HttpServletRequest request) {
        model.addAllAttributes(modelService.getErrorAttributes(e,
                request.getRequestURI(),
                "Ошибка при чтении файла."));

        log.warn(e.getMessage());

        return "errorview";
    }

    @ExceptionHandler({IOException.class})
    public String iOExceptionHandler(Exception e, Model model,
                                     HttpServletRequest request) {
        model.addAllAttributes(modelService.getErrorAttributes(e,
                request.getRequestURI(),
                "Ошибка ввода/вывода."));

        log.warn(e.getMessage());

        return "errorview";
    }

    @ExceptionHandler({InvalidParameterException.class})
    public String invalidParameterExceptionHandler(Exception e, Model model,
                                                   HttpServletRequest request) {
        model.addAllAttributes(modelService.getErrorAttributes(e,
                request.getRequestURI(),
                "Недопустимый параметр."));

        log.warn(e.getMessage());

        return "errorview";
    }

    @ExceptionHandler({EmptyFileException.class})
    public String emptyFileExceptionHandler(Exception e, Model model,
                                                   HttpServletRequest request) {
        model.addAllAttributes(modelService.getErrorAttributes(e,
                request.getRequestURI(),
                "Загружен пустой файл."));

        log.warn(e.getMessage());

        return "errorview";
    }

    @ExceptionHandler({EmptyPeriodException.class})
    public String EmptyPeriodExceptionHandler(Exception e, Model model,
                                              HttpServletRequest request) {
        model.addAllAttributes(modelService.getErrorAttributes(e,
                request.getRequestURI(),
                "Период отчёта не задан. Установите период отчёта и повторите запрос."));

        log.warn(e.getMessage());

        return "errorview";
    }

    @ExceptionHandler({Throwable.class})
    public String exceptionHandler(Throwable e, Model model, HttpServletRequest request) {
        model.addAllAttributes(modelService.getErrorAttributes(e,
                request.getRequestURI(),
                "Неизвестная ошибка! Без паники. Все под контролем."));

        log.warn(e.getMessage());

        return "errorview";
    }
}