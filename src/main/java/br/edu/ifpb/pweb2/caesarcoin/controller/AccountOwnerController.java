package br.edu.ifpb.pweb2.caesarcoin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifpb.pweb2.caesarcoin.model.AccountOwner;
import br.edu.ifpb.pweb2.caesarcoin.service.AccountOwnerService;

@Controller
@RequestMapping("/accountowners")
public class AccountOwnerController {
    
    @Autowired
    private AccountOwnerService accOwnerService;

    @GetMapping("/form")
    public ModelAndView getForm(AccountOwner accOwner, ModelAndView model){
        model.addObject("accountowner", accOwner);
        model.setViewName("accountowners/form");
        return model;
    }

    @PostMapping
    public ModelAndView save(AccountOwner accOwner, ModelAndView model, RedirectAttributes attr){
        accOwnerService.save(accOwner);
        attr.addFlashAttribute("message", "Correntista inserido com sucesso!");
        model.setViewName("redirect:accountowners");
        return model;
    }

    @GetMapping
    public ModelAndView listAll(ModelAndView model){
        model.addObject("accountowners", accOwnerService.findAll());
        model.setViewName("accountowners/list");
        return model;
    }

    @GetMapping("/{id}")
    public ModelAndView getAccOwnerById(@PathVariable(value = "id") Integer id, ModelAndView model) {
        model.setViewName("accountowners/form");
        model.addObject("accountowner", accOwnerService.findById(id));
        return model;
    }
}
