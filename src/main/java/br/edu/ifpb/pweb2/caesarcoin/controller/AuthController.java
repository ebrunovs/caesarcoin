package br.edu.ifpb.pweb2.caesarcoin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import br.edu.ifpb.pweb2.caesarcoin.model.AccountOwner;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @GetMapping
    public ModelAndView getForm(ModelAndView model,
                                @RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout) {
        model.setViewName("auth/login");
        model.addObject("user", new AccountOwner());
        
        if (error != null) {
            model.addObject("message", "Credenciais inválidas");
            model.addObject("messageType", "error");
        }
        if (logout != null) {
            model.addObject("message", "Logout realizado com sucesso");
            model.addObject("messageType", "success");
        }
        
        return model;
    }
}