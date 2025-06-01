package br.edu.ifpb.pweb2.caesarcoin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifpb.pweb2.caesarcoin.model.AccountOwner;
import br.edu.ifpb.pweb2.caesarcoin.repository.AccountOwnerRepository;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AccountOwnerRepository accOwnerRepo;

    @GetMapping
    public ModelAndView getForm(ModelAndView model){
        model.setViewName("auth/form");
        model.addObject("user", new  AccountOwner());
        return model;
    }

    @PostMapping
    public ModelAndView validate(AccountOwner accOwner, HttpSession session, ModelAndView model, RedirectAttributes attr){
        if ((accOwner = this.isValid(accOwner)) != null){
            session.setAttribute("user", accOwner);
            model.setViewName("redirect:/home");
        } else{
            attr.addFlashAttribute("mensage", "Login e/ou senha inválidos!");
            model.setViewName("redirect:/auth");
        }
        return model;
    }

    @GetMapping("/logout")
    public ModelAndView logout(ModelAndView model, HttpSession session){
        session.invalidate();
        model.setViewName("redirect:/auth");
        return model;
    }

    private AccountOwner isValid(AccountOwner accOwner){
        AccountOwner accOwnerBD = accOwnerRepo.findByEmail(accOwner.getEmail());
        boolean valid = false;
        if(accOwnerBD != null){
            if(PasswordUtil.checkPass(accOwner.getPassword(), accOwnerBD.getPassword())){
                valid = true;
            }
        }
        return valid ? accOwnerBD : null;
    }
}
