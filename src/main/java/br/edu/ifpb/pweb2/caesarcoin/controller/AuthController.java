package br.edu.ifpb.pweb2.caesarcoin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifpb.pweb2.caesarcoin.exception.*;

import br.edu.ifpb.pweb2.caesarcoin.model.AccountOwner;
import br.edu.ifpb.pweb2.caesarcoin.repository.AccountOwnerRepository;
import br.edu.ifpb.pweb2.caesarcoin.util.PasswordUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AccountOwnerRepository accOwnerRepo;

    @GetMapping
    public ModelAndView getForm(ModelAndView model,
                                @RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout) {
        model.setViewName("auth/login");
        model.addObject("user", new AccountOwner());
        return model;
    }

    @PostMapping
    public ModelAndView validate(@Valid AccountOwner accOwner, BindingResult result, HttpSession session, ModelAndView model, RedirectAttributes attr) {
        // Este método não é mais necessário com Spring Security
        // O Spring Security gerencia a autenticação automaticamente
        model.setViewName("redirect:/home");
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

    private AccountOwner isValid(AccountOwner accOwner) {
        // Este método não é mais necessário com Spring Security
        // A autenticação é gerenciada pelo CustomUserDetailsService
        return null;
    }

    @GetMapping("/access-denied")
    public ModelAndView accessDenied(ModelAndView mav) {
        mav.setViewName("auth/accessDenied");
        mav.addObject("message", "Acesso negado");
        return mav;
    }

    // Tratamentos de exceção locais
    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest req, jakarta.servlet.http.HttpServletResponse resp) {
        resp.setStatus(jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND);
        ModelAndView model = new ModelAndView("/error");
        model.addObject("message", ex.getMessage());
        model.addObject("exception", ex);
        model.addObject("path", req.getRequestURI());
        model.addObject("status", resp.getStatus());
        return model;
    }

    @ExceptionHandler(InvalidDataException.class)
    public ModelAndView handleInvalidDataException(InvalidDataException ex, HttpServletRequest req,jakarta.servlet.http.HttpServletResponse resp) {       
        resp.setStatus(jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST);
        ModelAndView model = new ModelAndView("/error");
        model.addObject("message", ex.getMessage());
        model.addObject("exception", ex);
        model.addObject("path", req.getRequestURI());
        model.addObject("status", resp.getStatus());
        return model;
    }



    @ExceptionHandler(NoResourceFoundException.class)
    public ModelAndView handleNoResourceFoundException(NoResourceFoundException ex, HttpServletRequest req, jakarta.servlet.http.HttpServletResponse resp) {
        resp.setStatus(jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        ModelAndView model = new ModelAndView("/error");
        model.addObject("message", ex.getMessage());
        model.addObject("exception", ex);
        model.addObject("path", req.getRequestURI());
        model.addObject("status", resp.getStatus());
        return model;
    }
}
