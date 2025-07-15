package br.edu.ifpb.pweb2.caesarcoin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import br.edu.ifpb.pweb2.caesarcoin.exception.BusinessException;
import br.edu.ifpb.pweb2.caesarcoin.exception.InvalidDataException;
import br.edu.ifpb.pweb2.caesarcoin.exception.ResourceNotFoundException;
import br.edu.ifpb.pweb2.caesarcoin.model.AccountOwner;
import br.edu.ifpb.pweb2.caesarcoin.service.AccountOwnerService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/accountowners")
public class AccountOwnerController {
    
    @Autowired
    private AccountOwnerService accOwnerService;

    @GetMapping("/form")
    public ModelAndView getForm(AccountOwner accOwner, ModelAndView model){
        model.addObject("menu", "accountowner");
        model.addObject("accountowner", accOwner);
        model.setViewName("accountowners/form");
        return model;
    }

    @PostMapping
    public ModelAndView save(AccountOwner accOwner, ModelAndView model, RedirectAttributes attr){
        try {
            if (accOwner.getName() == null || accOwner.getName().trim().isEmpty()) {
                throw new InvalidDataException("Nome é obrigatório");
            }
            if (accOwner.getEmail() == null || accOwner.getEmail().trim().isEmpty()) {
                throw new InvalidDataException("Email é obrigatório");
            }
            if (accOwner.getPassword() == null || accOwner.getPassword().trim().isEmpty()) {
                throw new InvalidDataException("Senha é obrigatória");
            }
            
            accOwnerService.save(accOwner);
            attr.addFlashAttribute("message", "Correntista inserido com sucesso!");
            model.setViewName("redirect:accountowners");
        } catch (Exception e) {
            if (e instanceof InvalidDataException) {
                throw e;
            }
            throw new BusinessException("Erro ao salvar correntista", e);
        }
        return model;
    }

    @GetMapping
    public ModelAndView listAll(ModelAndView model){
        try {
            model.addObject("menu", "accountowner");
            model.addObject("accountowners", accOwnerService.findAll());
            model.setViewName("accountowners/list");
        } catch (Exception e) {
            throw new BusinessException("Erro ao listar correntistas", e);
        }
        return model;
    }

    @GetMapping("/{id}")
    public ModelAndView getAccOwnerById(@PathVariable(value = "id") Integer id, ModelAndView model) {
        try {
            if (id == null || id <= 0) {
                throw new InvalidDataException("ID inválido");
            }
            
            AccountOwner accOwner = accOwnerService.findById(id);
            if (accOwner == null) {
                throw new ResourceNotFoundException("Correntista não encontrado com ID: " + id);
            }
            
            model.addObject("menu", "accountowner");
            model.setViewName("accountowners/form");
            model.addObject("accountowner", accOwner);
        } catch (Exception e) {
            if (e instanceof InvalidDataException || e instanceof ResourceNotFoundException) {
                throw e;
            }
            throw new BusinessException("Erro ao buscar correntista", e);
        }
        return model;
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