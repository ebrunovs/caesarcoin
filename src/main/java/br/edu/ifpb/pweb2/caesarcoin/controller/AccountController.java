package br.edu.ifpb.pweb2.caesarcoin.controller;

import java.util.List;

import br.edu.ifpb.pweb2.caesarcoin.model.Category;
import br.edu.ifpb.pweb2.caesarcoin.model.Transaction;
import br.edu.ifpb.pweb2.caesarcoin.service.CategoryService;
import br.edu.ifpb.pweb2.caesarcoin.service.TransactionService;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
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

    @Autowired
    private CategoryService catService;

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private AccountOwnerService accountOwnerService;

    @GetMapping("/form")
    public ModelAndView getForm(ModelAndView model, HttpSession session) {
        AccountOwner user = (AccountOwner) session.getAttribute("user");
        Account account = new Account();
        account.setAccountOwner(user);
        
        model.setViewName("accounts/form");
        model.addObject("account", account);
        return model;
    }

    @GetMapping("/nuaccount")
    public ModelAndView getNuAccount(ModelAndView model, HttpSession session) {
        AccountOwner user = (AccountOwner) session.getAttribute("user");
        if (user != null) {
            List<Account> userAccounts = accService.findByAccountOwner(user);
            model.addObject("userAccounts", userAccounts);
        }
        model.setViewName("accounts/transactionForm");
        return model;
    }

    @PostMapping(value = "/transaction")
    public ModelAndView postTransaction(String nuAccount, Transaction transaction, ModelAndView mav, RedirectAttributes attr) {

        if (transaction.getId() != null) {
            Transaction existing = transactionService.findById(transaction.getId());
            Account existingAccount = existing.getAccount();
            existing.setValue(transaction.getValue());
            existing.setDescription(transaction.getDescription());
            existing.setDate(transaction.getDate());
            existing.setCategory(catService.findById(transaction.getCategory().getId()));
            transactionService.save(existing);

            mav.setViewName("redirect:/accounts/" + existingAccount.getId() + "/transactions");
            return mav;
        }

        if (nuAccount != null && transaction.getValue() == null) {
            Account account = accService.findByNumberWithTransactions(nuAccount);
            if (account != null) {
                transaction.setCategory(new Category());
                mav.addObject("account", account);
                mav.addObject("transaction", transaction);
                mav.setViewName("accounts/transactionForm");

            } else {
                mav.addObject("mensagem", "Conta inexistente!");
                mav.setViewName("redirect:/accounts/nuaccount");
            }
        } else {
            Account account = accService.findByNumberWithTransactions(nuAccount);
            Integer categoryId = transaction.getCategory().getId();
            Category category = catService.findById(categoryId);
            account.addTransaction(transaction, category);
            accService.save(account);

            // return addTransactionAccount(account.getId(), mav);

            attr.addFlashAttribute("message", "Transação cadastrada com sucesso!");
            mav.setViewName("redirect:/accounts/" + account.getId() + "/transactions");
        }
        return mav;
    }


    @GetMapping(value = "/{id}/transactions")
    public ModelAndView addTransactionAccount(@PathVariable("id") Integer idAccount, ModelAndView mav) {
        Account account = accService.findByIdWithTransactions(idAccount);
        mav.addObject("account", account);
        mav.setViewName("accounts/transactionList");
        return mav;
    }


    @GetMapping("/edit/{id}")
    public ModelAndView getTransactionById(@PathVariable(value = "id") Integer id, ModelAndView model) {
        Transaction transaction = transactionService.findById(id);
        model.addObject("account", transaction.getAccount());
        model.addObject("transaction", transaction);
        model.setViewName("accounts/transactionForm");
        return model;
    }




    @ModelAttribute("accountOwnerItens")
    public List<AccountOwner> getAccountOwner(){
        return accOwnerService.findAll();
    }

    @ModelAttribute("categories")
    public List<Category> getCateg(){
        return catService.findAll();
    }


    @GetMapping
    public ModelAndView listAll(ModelAndView model, HttpSession session){
        AccountOwner accountOwner = (AccountOwner) session.getAttribute("user");
        if (accountOwner != null && !accountOwner.isAdmin()) {
            List<Account> userAccounts = accService.findByAccountOwner(accountOwner);
            model.addObject("accounts", userAccounts);
        } else {
            model.addObject("accounts", accService.findAll());
        }        
        model.setViewName("accounts/list");
        return model;
    }


    @PostMapping
    public ModelAndView save(Account account, ModelAndView model, RedirectAttributes attr, HttpSession session) {
        AccountOwner user = (AccountOwner) session.getAttribute("user");
        if (user != null) {
            account.setAccountOwner(user);
        }
        accService.save(account);
        attr.addFlashAttribute("message","Conta inserida com sucesso");
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
