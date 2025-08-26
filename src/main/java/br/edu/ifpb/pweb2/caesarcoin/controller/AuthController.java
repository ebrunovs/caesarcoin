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
        try {

            if (result.hasErrors()) {
                model.addObject("user", accOwner);
                model.addObject(BindingResult.MODEL_KEY_PREFIX + "user", result);
                model.setViewName("auth/login");
                return model;
            }
            
            if (accOwner.getEmail() == null || accOwner.getEmail().trim().isEmpty()) {
                throw new InvalidDataException("Email é obrigatório");
            }
            if (accOwner.getPassword() == null || accOwner.getPassword().trim().isEmpty()) {
                throw new InvalidDataException("Senha é obrigatória");
            }
            
            AccountOwner authenticatedUser = this.isValid(accOwner);
            if (authenticatedUser != null) {
                session.setAttribute("user", authenticatedUser);
                model.setViewName("redirect:/home");
            } else {
                throw new AccountownerNotFoundException("Login e/ou senha inválidos!");
            }
        } catch (Exception e) {
            if (e instanceof InvalidDataException || e instanceof AccountownerNotFoundException) {
                throw e;
            }
            throw new BusinessException("Erro durante o processo de autenticação", e);
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

    private AccountOwner isValid(AccountOwner accOwner) {
        try {
            AccountOwner accOwnerBD = accOwnerRepo.findByEmail(accOwner.getEmail());
            boolean valid = false;
            
            if (accOwnerBD != null) {
                if (!accOwnerBD.isEnabled()) {
                    throw new InvalidDataException("Usuário bloqueado!");
                }
                
                if (PasswordUtil.checkPass(accOwner.getPassword(), accOwnerBD.getPassword())) {
                    valid = true;
                }
            }
            return valid ? accOwnerBD : null;
        } catch (Exception e) {
            throw new BusinessException("Erro na validação de credenciais", e);
        }
        // if (logout != null) {
        //     model.addObject("message", "Logout realizado com sucesso");
        //     model.addObject("messageType", "success");
        // }
        
        // return model;
    }
}