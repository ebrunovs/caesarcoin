package br.edu.ifpb.pweb2.caesarcoin.controller;

import br.edu.ifpb.pweb2.caesarcoin.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

    @RequestMapping("/home")
    public ModelAndView home(ModelAndView model) {
        try {
            model.addObject("menu", "home");
            model.setViewName("index");
        } catch (Exception e) {
            throw new BusinessException("Erro ao carregar página inicial", e);
        }
        return model;
    }

    // Tratamento de exceção local
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
}