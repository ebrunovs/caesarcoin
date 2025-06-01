package br.edu.ifpb.pweb2.caesarcoin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifpb.pweb2.caesarcoin.model.Account;
import br.edu.ifpb.pweb2.caesarcoin.model.AccountOwner;
import br.edu.ifpb.pweb2.caesarcoin.service.AccountOwnerService;
import br.edu.ifpb.pweb2.caesarcoin.service.AccountService;

@Controller
@RequestMapping("/accounts")
public class AccountController {
    
    @Autowired
    private AccountService accService;

    @Autowired
    private AccountOwnerService accOwnerService;

    @GetMapping("/form")
    public ModelAndView getForm(ModelAndView model) {
        model.setViewName("accounts/form");
        model.addObject("account", new Account(new AccountOwner()));
        return model;
    }


    @ModelAttribute("accountOwnerItens")
    public List<AccountOwner> getAccountOwner(){
        return accOwnerService.findAll();
    }

    @GetMapping
    public ModelAndView listAll(ModelAndView model){
        model.addObject("accounts", accService.findAll());
        model.setViewName("accounts/list");
        return model;
    }


    @PostMapping
    public ModelAndView save(Account account, ModelAndView model, RedirectAttributes attr){
        accService.save(account);
        attr.addFlashAttribute("mensage","Conta inserida com sucesso");
        model.setViewName("redirect:/accounts");
        return model;
    }

    @GetMapping("/{id}")
    public ModelAndView getAccOwnerById(@PathVariable(value = "id") Integer id, ModelAndView model){
        model.addObject("account", accService.findById(id));
        model.setViewName("accounts/form");
        return model;
    }
}
