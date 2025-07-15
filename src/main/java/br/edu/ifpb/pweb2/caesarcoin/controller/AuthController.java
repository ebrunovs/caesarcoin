package br.edu.ifpb.pweb2.caesarcoin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifpb.pweb2.caesarcoin.exception.BusinessException;
import br.edu.ifpb.pweb2.caesarcoin.exception.AccountownerNotFoundException;
import br.edu.ifpb.pweb2.caesarcoin.exception.InvalidDataException;
import br.edu.ifpb.pweb2.caesarcoin.model.AccountOwner;
import br.edu.ifpb.pweb2.caesarcoin.repository.AccountOwnerRepository;
import br.edu.ifpb.pweb2.caesarcoin.util.PasswordUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AccountOwnerRepository accOwnerRepo;

    @GetMapping
    public ModelAndView getForm(ModelAndView model){
        model.setViewName("auth/login");
        model.addObject("user", new AccountOwner());
        return model;
    }

    @PostMapping
    public ModelAndView validate(AccountOwner accOwner, HttpSession session, ModelAndView model, RedirectAttributes attr){
        try {
            if (accOwner.getEmail() == null || accOwner.getEmail().trim().isEmpty()) {
                throw new InvalidDataException("Email é obrigatório");
            }
            if (accOwner.getPassword() == null || accOwner.getPassword().trim().isEmpty()) {
                throw new InvalidDataException("Senha é obrigatória");
            }
            
            if ((accOwner = this.isValid(accOwner)) != null){
                session.setAttribute("user", accOwner);
                model.setViewName("redirect:/home");
            } else{
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

    private AccountOwner isValid(AccountOwner accOwner){
        try {
            AccountOwner accOwnerBD = accOwnerRepo.findByEmail(accOwner.getEmail());
            boolean valid = false;
            if(accOwnerBD != null){
                if(PasswordUtil.checkPass(accOwner.getPassword(), accOwnerBD.getPassword())){
                    valid = true;
                }
            }
            return valid ? accOwnerBD : null;
        } catch (Exception e) {
            throw new BusinessException("Erro na validação de credenciais", e);
        }
    }

    // Tratamentos de exceção locais
    @ExceptionHandler(AccountownerNotFoundException.class)
    public ModelAndView handleAccountownerNotFoundException(AccountownerNotFoundException ex, HttpServletRequest req) {
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