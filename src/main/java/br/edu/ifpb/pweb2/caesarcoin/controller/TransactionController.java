package br.edu.ifpb.pweb2.caesarcoin.controller;

import br.edu.ifpb.pweb2.caesarcoin.model.Account;
import br.edu.ifpb.pweb2.caesarcoin.model.AccountOwner;
import br.edu.ifpb.pweb2.caesarcoin.model.Transaction;
import br.edu.ifpb.pweb2.caesarcoin.repository.TransactionRepository;
import br.edu.ifpb.pweb2.caesarcoin.service.AccountService;
import br.edu.ifpb.pweb2.caesarcoin.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;


    @GetMapping("/form")
    public ModelAndView getForm(ModelAndView model) {
        model.setViewName("transactions/form");
        model.addObject("transaction", new Transaction());
        return model;
    }

    @PostMapping
    public ModelAndView save(Transaction transaction, ModelAndView model, RedirectAttributes attr){
        transactionService.save(transaction);
        attr.addFlashAttribute("message", "Transação inserida com sucesso!");
        model.setViewName("redirect:transactions");
        return model;
    }


    @GetMapping
    public ModelAndView listAll(ModelAndView model){
        model.addObject("transactions", transactionService.findAll());
        model.setViewName("transactions/list");
        return model;
    }

    @GetMapping("/{id}")
    public ModelAndView getTransactionById(@PathVariable(value = "id") Integer id, ModelAndView model) {
        model.setViewName("transactions/form");
        model.addObject("transaction", transactionService.findById(id));
        return model;
    }

}
