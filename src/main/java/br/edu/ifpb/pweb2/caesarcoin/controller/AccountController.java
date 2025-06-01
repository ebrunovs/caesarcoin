package br.edu.ifpb.pweb2.caesarcoin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

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

}
