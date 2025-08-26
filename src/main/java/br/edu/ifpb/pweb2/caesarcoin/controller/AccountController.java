package br.edu.ifpb.pweb2.caesarcoin.controller;

import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;

import br.edu.ifpb.pweb2.caesarcoin.exception.BusinessException;
import br.edu.ifpb.pweb2.caesarcoin.exception.InvalidDataException;
import br.edu.ifpb.pweb2.caesarcoin.exception.ResourceNotFoundException;
import br.edu.ifpb.pweb2.caesarcoin.model.*;
import br.edu.ifpb.pweb2.caesarcoin.service.CategoryService;
import br.edu.ifpb.pweb2.caesarcoin.service.TransactionService;
import br.edu.ifpb.pweb2.caesarcoin.ui.NavPage;
import br.edu.ifpb.pweb2.caesarcoin.ui.NavePageBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

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
            throw new BusinessException("Erro ao carregar acc do usuário", e);
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
                existing.setType(transaction.getType());
                existing.setCategory(catService.findById(transaction.getCategory().getId()));
                transactionService.save(existing);

                attr.addFlashAttribute("message", "Transação atualizada com sucesso!");
                mav.setViewName("redirect:/accounts/" + existingAccount.getId() + "/transactions");
                return mav;
            }

            if (idAccount != null && transaction.getValue() == null) {
                Account account = accService.findByIdWithTransactions(idAccount);
                if (account != null) {
                    transaction.setCategory(new Category());
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
    public ModelAndView listAll(ModelAndView model, 
    HttpSession session, 
    @RequestParam(defaultValue = "1") int page, 
    @RequestParam(defaultValue = "3") int size
    ){
        Pageable paging = PageRequest.of(page - 1, size);
        AccountOwner accountOwner = (AccountOwner) session.getAttribute("user");
        Page<Account> accPage;
        if (accountOwner != null) {
            accPage = accService.findByAccountOwner(accountOwner, paging);
        } else {
            accPage = accService.findAll(paging);
        }
        NavPage navPage = NavePageBuilder.newNavPage(accPage.getNumber() + 1, accPage.getTotalElements(), accPage.getTotalPages(), size);
        try {
            if (accountOwner != null) {
                model.addObject("accounts", accPage.getContent());
            } else {
                model.addObject("accounts", accPage.getContent());
            }
            model.addObject("menu", "account");
            model.setViewName("accounts/list");
            model.addObject("navPage", navPage);
        } catch (Exception e) {
            throw new BusinessException("Erro ao listar acc", e);
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

            boolean isNew = (account.getId() == null);

            accService.save(account);
            if (!isNew) {
                attr.addFlashAttribute("message", "Conta atualizada com sucesso!");
            } else {
                attr.addFlashAttribute("message", "Conta inserida com sucesso!");
            }
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


    @RequestMapping("/{id}/delete")
    public ModelAndView deleteById(@PathVariable(value = "id") Integer id,
        ModelAndView mav, RedirectAttributes attr) {
        accService.deleteById(id);
        attr.addFlashAttribute("message", "Conta removida com sucesso!");
        mav.setViewName("redirect:/accounts");
        return mav;
    }

    @RequestMapping("/transaction/{id}/delete")
    public ModelAndView deleteTransactionById(@PathVariable(value = "id") Integer id,
        ModelAndView mav, RedirectAttributes attr) {
        Transaction transaction = transactionService.findById(id);
        transactionService.deleteById(id);
        attr.addFlashAttribute("message", "Transação removida com sucesso!");
        String redirect = "redirect:/accounts/ " + transaction.getAccount().getId() + " /transactions";
        mav.setViewName(redirect);
        return mav;
    }

    @GetMapping("/{id}/extract")
    public ModelAndView getAccountExtract(@PathVariable(value = "id") Integer id,
                                        @RequestParam(value = "startDate", required = false) String startDateStr,
                                        @RequestParam(value = "endDate", required = false) String endDateStr,
                                        ModelAndView mav) {
        try {
            Account account = accService.findById(id);
            if (account == null) {
                throw new ResourceNotFoundException("Conta não encontrada");
            }

            ExtractData extractData = transactionService.generateExtractWithDefaultDates(account, startDateStr, endDateStr);

            mav.addObject("account", account);
            mav.addObject("transactions", extractData.getTransactions());
            mav.addObject("startDate", extractData.getStartDate().toString());
            mav.addObject("endDate", extractData.getEndDate().toString());
            mav.addObject("totalEntradas", extractData.getTotalIncomes());
            mav.addObject("totalSaidas", extractData.getTotalOutcomes());
            mav.addObject("totalInvestimentos", extractData.getTotalInvestments());
            mav.addObject("saldoPeriodo", extractData.getPeriodBalance());
            mav.setViewName("accounts/extract");
            
        } catch (Exception e) {
            throw new BusinessException("Erro ao gerar extrato da conta", e);
        }
        return mav;
    }

    @GetMapping("/{id}/annual-budget")
    public ModelAndView getAnnualBudget(@PathVariable("id") Integer id,
                                        @RequestParam(value = "year", required = false) Integer year,
                                        ModelAndView mav) {
        try {
            if (id == null || id <= 0) {
                throw new InvalidDataException("ID da conta inválido");
            }
            Account account = accService.findById(id);
            if (account == null) {
                throw new ResourceNotFoundException("Conta não encontrada");
            }
            int selectedYear = (year == null) ? Year.now().getValue() : year;
            List<AnnualCategoryBudget> rows = transactionService.generateAnnualBudget(account, selectedYear);

            List<AnnualCategoryBudget> incomes = rows.stream().filter(r -> r.getCategory().getKind() == TransactionType.ENTRADA).collect(Collectors.toList());
            List<AnnualCategoryBudget> outcomes = rows.stream().filter(r -> r.getCategory().getKind() == TransactionType.SAIDA).collect(Collectors.toList());
            List<AnnualCategoryBudget> investments = rows.stream().filter(r -> r.getCategory().getKind() == TransactionType.INVESTIMENTO).collect(Collectors.toList());

            // Totais mensais e anuais por natureza para facilitar exibição
            double[] incomeMonths = sumMonths(incomes);
            double[] outcomeMonths = sumMonths(outcomes);
            double[] investmentMonths = sumMonths(investments);
            double incomeAnnual = sumArray(incomeMonths);
            double outcomeAnnual = sumArray(outcomeMonths);
            double investmentAnnual = sumArray(investmentMonths);
            double[] netMonths = new double[12];
            for (int i = 0; i < 12; i++) {
                netMonths[i] = incomeMonths[i] - outcomeMonths[i];
            }
            double netAnnual = incomeAnnual - outcomeAnnual;

            mav.addObject("account", account);
            mav.addObject("year", selectedYear);
            mav.addObject("incomes", incomes);
            mav.addObject("outcomes", outcomes);
            mav.addObject("investments", investments);
            mav.addObject("incomeMonths", incomeMonths);
            mav.addObject("outcomeMonths", outcomeMonths);
            mav.addObject("investmentMonths", investmentMonths);
            mav.addObject("incomeAnnual", incomeAnnual);
            mav.addObject("outcomeAnnual", outcomeAnnual);
            mav.addObject("investmentAnnual", investmentAnnual);
            mav.addObject("netMonths", netMonths);
            mav.addObject("netAnnual", netAnnual);
            mav.addObject("hasData", !rows.isEmpty());
            mav.addObject("menu", "account");
            mav.setViewName("accounts/annualBudget");
        } catch (Exception e) {
            if (e instanceof InvalidDataException || e instanceof ResourceNotFoundException) {
                throw e;
            }
            throw new BusinessException("Erro ao gerar orçamento anual", e);
        }
        return mav;
    }

    private double[] sumMonths(List<AnnualCategoryBudget> list) {
        double[] totals = new double[12];
        for (AnnualCategoryBudget b : list) {
            double[] mts = b.getMonthlyTotals();
            for (int i = 0; i < 12; i++) {
                totals[i] += mts[i];
            }
        }
        return totals;
    }

    private double sumArray(double[] arr) {
        double s = 0d;
        for (double v : arr) s += v;
        return s;
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