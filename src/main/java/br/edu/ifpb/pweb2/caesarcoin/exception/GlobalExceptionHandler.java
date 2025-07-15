package br.edu.ifpb.pweb2.caesarcoin.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountownerNotFoundException.class)
    public ModelAndView handleCorrentistanNotfoundException(AccountownerNotFoundException ex, HttpServletRequest req) {
        System.out.println("Registrando o erro no log");
        ModelAndView model = new ModelAndView("/error");
        model.addObject("message", ex.getMessage());
        model.addObject("exception", ex);
        model.addObject("path", req.getRequestURI());
        model.addObject("trace", ex.getStackTrace());
        return model;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
        System.out.println("Registrando o erro no log");
        ModelAndView model = new ModelAndView("/error");
        model.addObject("message", ex.getMessage());
        model.addObject("exception", ex);
        model.addObject("path", req.getRequestURI());
        model.addObject("trace", ex.getStackTrace());
        return model;
    }
@ExceptionHandler(BusinessException.class)
    public ModelAndView handleBusinessException(BusinessException ex, HttpServletRequest req) {
        System.out.println("Registrando o erro no log");
        ModelAndView model = new ModelAndView("/error");
        model.addObject("message", ex.getMessage());
        model.addObject("exception", ex);
        model.addObject("path", req.getRequestURI());
        model.addObject("trace", ex.getStackTrace());
        return model;
    }

    @ExceptionHandler(InvalidDataException.class)
    public ModelAndView handleInvalidDataException(InvalidDataException ex, HttpServletRequest req) {
        System.out.println("Registrando o erro no log");
        ModelAndView model = new ModelAndView("/error");
        model.addObject("message", ex.getMessage());
        model.addObject("exception", ex);
        model.addObject("path", req.getRequestURI());
        model.addObject("trace", ex.getStackTrace());
        return model;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGenericException(Exception ex, HttpServletRequest req) {
        System.out.println("Registrando o erro no log");
        ModelAndView model = new ModelAndView("/error");
        model.addObject("message", "Erro interno do servidor");
        model.addObject("exception", ex);
        model.addObject("path", req.getRequestURI());
        model.addObject("trace", ex.getStackTrace());
        model.addObject("status", 500);
        return model;
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ModelAndView handleNoResourceFoundException(NoResourceFoundException ex, HttpServletRequest req) {
        ModelAndView model = new ModelAndView("/error");
        model.addObject("message", ex.getMessage());
        model.addObject("exception", ex);
        model.addObject("path", req.getRequestURI());
        model.addObject("trace", ex.getStackTrace());
        return model;
    }
}