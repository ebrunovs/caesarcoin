package br.edu.ifpb.pweb2.caesarcoin.controller;

import java.util.List;

import br.edu.ifpb.pweb2.caesarcoin.exception.BusinessException;
import br.edu.ifpb.pweb2.caesarcoin.exception.InvalidDataException;
import br.edu.ifpb.pweb2.caesarcoin.exception.ResourceNotFoundException;
import br.edu.ifpb.pweb2.caesarcoin.model.Category;
import br.edu.ifpb.pweb2.caesarcoin.model.Transaction;
import br.edu.ifpb.pweb2.caesarcoin.service.CategoryService;
import br.edu.ifpb.pweb2.caesarcoin.service.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
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

    @GetMapping("/form")
    public ModelAndView getForm(ModelAndView model, HttpSession session) {
        try {
            AccountOwner user = (AccountOwner) session.getAttribute("user");
            Account account = new Account();
            account.setAccountOwner(user);
            
            model.addObject("menu", "account");
            model.setViewName("accounts/form");
            model.addObject("account", account);
        } catch (Exception e) {
            throw new BusinessException("Erro ao carregar formulário de conta", e);
        }
        return model;
    }

    @GetMapping("/nuaccount")
    public ModelAndView getNuAccount(ModelAndView model, HttpSession session) {
        try {
            AccountOwner user = (AccountOwner) session.getAttribute("user");
            if (user != null) {
                List<Account> userAccounts = accService.findByAccountOwner(user);
                model.addObject("userAccounts", userAccounts);
            }
            model.addObject("menu", "transaction");
            model.setViewName("accounts/transactionForm");
        } catch (Exception e) {
            throw new BusinessException("Erro ao carregar contas do usuário", e);
        }
        return model;
    }

    @PostMapping("/transaction")
    public ModelAndView postTransaction(@RequestParam("idAccount") Integer idAccount, Transaction transaction, ModelAndView mav, RedirectAttributes attr) {
        try {
            if (transaction.getId() != null) {
                Transaction existing = transactionService.findById(transaction.getId());
                if (existing == null) {
                    throw new ResourceNotFoundException("Transação não encontrada");
                }
                
                Account existingAccount = existing.getAccount();
                existing.setValue(transaction.getValue());
                existing.setDescription(transaction.getDescription());
                existing.setDate(transaction.getDate());
                existing.setCategory(catService.findById(transaction.getCategory().getId()));
                transactionService.save(existing);

                mav.setViewName("redirect:/accounts/" + existingAccount.getId() + "/transactions");
                return mav;
            }

            if (idAccount != null && transaction.getValue() == null) {
                Account account = accService.findByIdWithTransactions(idAccount);
                if (account != null) {
                    transaction.setCategory(new Category());
                    mav.addObject("menu", "transaction");
                    mav.addObject("account", account);
                    mav.addObject("transaction", transaction);
                    mav.setViewName("accounts/transactionForm");
                } else {
                    throw new ResourceNotFoundException("Conta inexistente!");
                }
            } else {
                if (idAccount == null || idAccount <= 0) {
                    throw new InvalidDataException("ID da conta é obrigatório");
                }
                if (transaction.getValue() == null || transaction.getValue().doubleValue() <= 0) {
                    throw new InvalidDataException("Valor deve ser maior que zero");
                }
                if (transaction.getDescription() == null || transaction.getDescription().trim().isEmpty()) {
                    throw new InvalidDataException("Descrição é obrigatória");
                }
                if (transaction.getCategory() == null || transaction.getCategory().getId() == null) {
                    throw new InvalidDataException("Categoria é obrigatória");
                }
                
                Account account = accService.findByIdWithTransactions(idAccount);
                if (account == null) {
                    throw new ResourceNotFoundException("Conta não encontrada: " + idAccount);
                }
                
                Integer categoryId = transaction.getCategory().getId();
                Category category = catService.findById(categoryId);
                if (category == null) {
                    throw new ResourceNotFoundException("Categoria não encontrada");
                }
                
                account.addTransaction(transaction, category);
                accService.save(account);

                attr.addFlashAttribute("message", "Transação cadastrada com sucesso!");
                mav.setViewName("redirect:/accounts/" + account.getId() + "/transactions");
            }
        } catch (Exception e) {
            if (e instanceof InvalidDataException || e instanceof ResourceNotFoundException) {
                throw e;
            }
            throw new BusinessException("Erro no processamento da transação", e);
        }
        return mav;
    }



    @GetMapping(value = "/{id}/transactions")
    public ModelAndView addTransactionAccount(@PathVariable("id") Integer idAccount, ModelAndView mav) {
        try {
            if (idAccount == null || idAccount <= 0) {
                throw new InvalidDataException("ID da conta inválido");
            }
            
            Account account = accService.findByIdWithTransactions(idAccount);
            if (account == null) {
                throw new ResourceNotFoundException("Conta não encontrada com ID: " + idAccount);
            }
            
            mav.addObject("menu", "transaction");
            mav.addObject("account", account);
            mav.setViewName("accounts/transactionList");
        } catch (Exception e) {
            if (e instanceof InvalidDataException || e instanceof ResourceNotFoundException) {
                throw e;
            }
            throw new BusinessException("Erro ao carregar transações da conta", e);
        }
        return mav;
    }

    @GetMapping("/edit/{id}")
    public ModelAndView getTransactionById(@PathVariable(value = "id") Integer id, ModelAndView model) {
        try {
            if (id == null || id <= 0) {
                throw new InvalidDataException("ID da transação inválido");
            }
            
            Transaction transaction = transactionService.findById(id);
            if (transaction == null) {
                throw new ResourceNotFoundException("Transação não encontrada com ID: " + id);
            }
            
            model.addObject("menu", "transaction");
            model.addObject("account", transaction.getAccount());
            model.addObject("transaction", transaction);
            model.setViewName("accounts/transactionForm");
        } catch (Exception e) {
            if (e instanceof InvalidDataException || e instanceof ResourceNotFoundException) {
                throw e;
            }
            throw new BusinessException("Erro ao buscar transação", e);
        }
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
        try {
            AccountOwner accountOwner = (AccountOwner) session.getAttribute("user");
            if (accountOwner != null) {
                List<Account> userAccounts = accService.findByAccountOwner(accountOwner);
                model.addObject("accounts", userAccounts);
            } else {
                model.addObject("accounts", accService.findAll());
            }        
            model.addObject("menu", "account");
            model.setViewName("accounts/list");
        } catch (Exception e) {
            throw new BusinessException("Erro ao listar contas", e);
        }
        return model;
    }

    @PostMapping
    public ModelAndView save(Account account, ModelAndView model, RedirectAttributes attr, HttpSession session) {
        try {
            if (account.getNumber() == null || account.getNumber().trim().isEmpty()) {
                throw new InvalidDataException("Número da conta é obrigatório");
            }
            if (account.getDescription() == null || account.getDescription().trim().isEmpty()) {
                throw new InvalidDataException("Descrição é obrigatória");
            }
            if (account.getType() == null) {
                throw new InvalidDataException("Tipo da conta é obrigatório");
            }
            
            AccountOwner user = (AccountOwner) session.getAttribute("user");
            if (user != null) {
                account.setAccountOwner(user);
            }
            
            accService.save(account);
            attr.addFlashAttribute("message","Conta inserida com sucesso");
            model.setViewName("redirect:/accounts");
        } catch (Exception e) {
            if (e instanceof InvalidDataException) {
                throw e;
            }
            throw new BusinessException("Erro ao salvar conta", e);
        }
        return model;
    }

    @GetMapping("/{id}")
    public ModelAndView getAccOwnerById(@PathVariable(value = "id") Integer id, ModelAndView model){
        try {
            if (id == null || id <= 0) {
                throw new InvalidDataException("ID inválido");
            }
            
            Account account = accService.findById(id);
            if (account == null) {
                throw new ResourceNotFoundException("Conta não encontrada com ID: " + id);
            }
            
            model.addObject("account", account);
            model.setViewName("accounts/form");
        } catch (Exception e) {
            if (e instanceof InvalidDataException || e instanceof ResourceNotFoundException) {
                throw e;
            }
            throw new BusinessException("Erro ao buscar conta", e);
        }
        return model;
    }

    @GetMapping("/transaction")
    public ModelAndView showTransactionForm(@RequestParam("idAccount") Integer idAccount, ModelAndView mav) {
        Account account = accService.findById(idAccount);
        if (account == null) {
            throw new ResourceNotFoundException("Conta não encontrada com ID: " + idAccount);
        }
        mav.addObject("account", account);
        mav.addObject("transaction", new Transaction());
        mav.setViewName("accounts/transactionForm");
        return mav;
    }

    // Tratamentos de exceção locais
    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
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