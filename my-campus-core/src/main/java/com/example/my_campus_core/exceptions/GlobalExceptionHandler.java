package com.example.my_campus_core.exceptions;

import java.util.logging.Logger;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public String handleNotFoundException(NotFoundException exception,
            RedirectAttributes redirectAttributes) {

        ExceptionResponse exceptionResponse = new ExceptionResponse("RECORD NOT FOUND", exception.getMessage(), 404);
        redirectAttributes.addFlashAttribute("exception", exceptionResponse);

        return "redirect:/exception";
    }

    @ExceptionHandler(InputMissingException.class)
    public String handleInputMissingException(InputMissingException exception, RedirectAttributes redirectAttributes) {

        ExceptionResponse exceptionResponse = new ExceptionResponse("BAD REQUEST", exception.getMessage(), 400);
        redirectAttributes.addFlashAttribute("exception", exceptionResponse);

        return "redirect:/exception";
    }

    @ExceptionHandler(UnsupportedEntityException.class)
    public String handleUnsuppertedEntityException(UnsupportedEntityException exception,
            RedirectAttributes redirectAttributes) {

        ExceptionResponse exceptionResponse = new ExceptionResponse("UNSUPPORTED ENTITY", exception.getMessage(), 422);
        redirectAttributes.addFlashAttribute("exception", exceptionResponse);

        return "redirect:/exception";

    }

    @ExceptionHandler(InternalErrorException.class)
    public String handleInternalErrorException(InternalErrorException exception,
            RedirectAttributes redirectAttributes) {
        ExceptionResponse exceptionResponse = new ExceptionResponse("INTERNAL ERROR", exception.getMessage(), 500);
        redirectAttributes.addFlashAttribute("exception", exceptionResponse);

        return "redirect:/exception";
    }
}
