package ru.bk.j3000.normarchivedata.exceptionhandler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.bk.j3000.normarchivedata.service.ModelService;


@ControllerAdvice()
@RequiredArgsConstructor
@Slf4j
public class AllExceptionHandler {
    private final ModelService modelService;

    @ExceptionHandler
    public String exceptionHandler(Exception e, Model model, HttpServletRequest request) {
        model.addAllAttributes(modelService.getErrorAttributes(e, request.getRequestURI()));

        log.warn(e.getMessage());

        return "errorview";
    }
}
