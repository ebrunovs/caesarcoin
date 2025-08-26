package br.edu.ifpb.pweb2.caesarcoin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import br.edu.ifpb.pweb2.caesarcoin.exception.BusinessException;
import br.edu.ifpb.pweb2.caesarcoin.model.AccountOwner;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @GetMapping
    public ModelAndView getForm(ModelAndView model,
                                @RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout) {
        model.setViewName("auth/login");
        model.addObject("user", new AccountOwner());
        
        // Processar mensagens de erro e logout
        if (error != null) {
            model.addObject("message", "Credenciais inválidas. Verifique seu email e senha.");
        }
        
        if (logout != null) {
            model.addObject("message", "Logout realizado com sucesso!");
            model.addObject("messageType", "success");
        }
        
        return model;
    }

    @GetMapping("/logout")
    public ModelAndView logout(ModelAndView model, HttpSession session){
        try {
            session.invalidate();
            model.setViewName("redirect:/auth");
        } catch (Exception e) {
            throw new BusinessException("Erro durante o logout", e);
        }
        return model;
    }

    @GetMapping("/access-denied")
    public ModelAndView accessDenied(ModelAndView mav) {
        mav.setViewName("auth/accessDenied");
        mav.addObject("message", "Acesso negado");
        return mav;
    }
}
