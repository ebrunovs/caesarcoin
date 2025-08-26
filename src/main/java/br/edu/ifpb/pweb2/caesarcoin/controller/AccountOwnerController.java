package br.edu.ifpb.pweb2.caesarcoin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.edu.ifpb.pweb2.caesarcoin.exception.BusinessException;
import br.edu.ifpb.pweb2.caesarcoin.exception.InvalidDataException;
import br.edu.ifpb.pweb2.caesarcoin.exception.ResourceNotFoundException;
import br.edu.ifpb.pweb2.caesarcoin.model.AccountOwner;
import br.edu.ifpb.pweb2.caesarcoin.model.User;
import br.edu.ifpb.pweb2.caesarcoin.service.AccountOwnerService;
import br.edu.ifpb.pweb2.caesarcoin.ui.NavPage;
import br.edu.ifpb.pweb2.caesarcoin.ui.NavePageBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/accountowners")
public class AccountOwnerController {
    
    @Autowired
    private AccountOwnerService accOwnerService;

    @GetMapping("/form")
    public ModelAndView getForm(AccountOwner accOwner, ModelAndView model){
        model.addObject("accountowner", accOwner);
        model.addObject("menu", "accountowner");
        model.setViewName("accountowners/form");
        return model;
    }

    @PostMapping
    public ModelAndView save(@Valid AccountOwner accOwner,BindingResult result, ModelAndView model, RedirectAttributes attr) {
        try {

            if (result.hasErrors()) {         
                model.addObject("accountowner", accOwner);    
                model.addObject(BindingResult.MODEL_KEY_PREFIX + "accountowner", result);   
                model.setViewName("accountowners/form");
                return model;
            }

            boolean isNew = (accOwner.getId() == null);
            accOwnerService.save(accOwner);
            if (!isNew) {
                attr.addFlashAttribute("message", "Correntista atualizado com sucesso!");
            } else {
                attr.addFlashAttribute("message", "Correntista inserido com sucesso!");
            }
            if (!isNew) {
                attr.addFlashAttribute("message", "Correntista atualizado com sucesso!");
            } else {
                attr.addFlashAttribute("message", "Correntista inserido com sucesso!");
            }
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
    public ModelAndView listAll(ModelAndView model, 
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "5") int size
    ){
        Pageable paging = PageRequest.of(page - 1, size);
        Page<AccountOwner> accOwners = accOwnerService.findAll(paging);
        NavPage navPage = NavePageBuilder.newNavPage(accOwners.getNumber() + 1, accOwners.getTotalElements(),
                accOwners.getTotalPages(), size);
        try {
            model.addObject("menu", "accountowner");
            model.addObject("accountowners", accOwners.getContent());
            model.setViewName("accountowners/list");
            model.addObject("navPage", navPage);
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

    @GetMapping("/{id}/block")
    public ModelAndView blockById(@PathVariable(value = "id") Integer id, ModelAndView mav, RedirectAttributes attr) {
        try {
            AccountOwner accOwnerBlock = accOwnerService.findById(id);
            if (accOwnerBlock == null) {
                throw new ResourceNotFoundException("Correntista não encontrado");
            }
            accOwnerBlock.setEnabled(false);
            accOwnerService.save(accOwnerBlock); 
            
            attr.addFlashAttribute("message", "Correntista bloqueado com sucesso!");
            mav.setViewName("redirect:/accountowners");
        } catch (Exception e) {
            throw new BusinessException("Erro ao bloquear correntista", e);
        }
        return mav;
    }

    @GetMapping("/{id}/unlock")
    public ModelAndView unlockById(@PathVariable(value = "id") Integer id, ModelAndView mav, RedirectAttributes attr) {
        try {
            AccountOwner accOwnerBlock = accOwnerService.findById(id);
            if (accOwnerBlock == null) {
                throw new ResourceNotFoundException("Correntista não encontrado");
            }
            accOwnerBlock.setEnabled(true);
            accOwnerService.save(accOwnerBlock); 
            
            attr.addFlashAttribute("message", "Correntista desbloqueado com sucesso!");
            mav.setViewName("redirect:/accountowners");
        } catch (Exception e) {
            throw new BusinessException("Erro ao desbloquear correntista", e);
        }
        return mav;
    }

    @GetMapping("/{id}/delete")
    public ModelAndView deleteById(@PathVariable(value = "id") Integer id,
        ModelAndView mav, RedirectAttributes attr) {
        accOwnerService.deleteById(id);
        attr.addFlashAttribute("message", "Correntista removido com sucesso!");
        mav.setViewName("redirect:/accountowners");
        return mav;
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

    @ModelAttribute("users")
    public List<User> getUserOptions(){
        return accOwnerService.findEnabledUsers();
    }
    


}