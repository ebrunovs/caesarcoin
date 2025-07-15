package br.edu.ifpb.pweb2.caesarcoin.controller;

import br.edu.ifpb.pweb2.caesarcoin.exception.BusinessException;
import br.edu.ifpb.pweb2.caesarcoin.exception.InvalidDataException;
import br.edu.ifpb.pweb2.caesarcoin.exception.ResourceNotFoundException;
import br.edu.ifpb.pweb2.caesarcoin.model.Transaction;
import br.edu.ifpb.pweb2.caesarcoin.model.Comment;
import br.edu.ifpb.pweb2.caesarcoin.service.TransactionService;
import br.edu.ifpb.pweb2.caesarcoin.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private CommentService commentService;

    @GetMapping("/view/{id}")
    public ModelAndView viewTransaction(@PathVariable(value = "id") Integer id, 
                                      @RequestParam(value = "editComment", required = false) Integer editCommentId,
                                      ModelAndView model) {
        try {
            if (id == null || id <= 0) {
                throw new InvalidDataException("ID da transação inválido");
            }
            
            Transaction transaction = transactionService.findById(id);
            if (transaction == null) {
                throw new ResourceNotFoundException("Transação não encontrada com ID: " + id);
            }
            
            model.addObject("menu", "transaction");
            model.addObject("transaction", transaction);
            model.addObject("comment", new Comment());
            
            // Se há um comentário sendo editado, adiciona ao modelo
            if (editCommentId != null) {
                model.addObject("editingComment", editCommentId);
            }
            
            model.setViewName("accounts/transactionView");
        } catch (Exception e) {
            if (e instanceof InvalidDataException || e instanceof ResourceNotFoundException) {
                throw e;
            }
            throw new BusinessException("Erro ao visualizar transação", e);
        }
        return model;
    }

    @PostMapping("/comment")
    public ModelAndView addComment(Comment comment, Integer transactionId, ModelAndView model, RedirectAttributes attr) {
        try {
            if (transactionId == null || transactionId <= 0) {
                throw new InvalidDataException("ID da transação inválido");
            }
            if (comment.getText() == null || comment.getText().trim().isEmpty()) {
                throw new InvalidDataException("Texto do comentário é obrigatório");
            }
            
            Transaction transaction = transactionService.findById(transactionId);
            if (transaction == null) {
                throw new ResourceNotFoundException("Transação não encontrada");
            }
            
            comment.setTransaction(transaction);
            commentService.save(comment);
            
            attr.addFlashAttribute("message", "Comentário adicionado com sucesso!");
            model.setViewName("redirect:/transactions/view/" + transactionId);
        } catch (Exception e) {
            if (e instanceof InvalidDataException || e instanceof ResourceNotFoundException) {
                throw e;
            }
            throw new BusinessException("Erro ao adicionar comentário", e);
        }
        return model;
    }

    @PostMapping
    public ModelAndView save(Transaction transaction, ModelAndView model, RedirectAttributes attr){
        try {
            if (transaction.getValue() == null || transaction.getValue().doubleValue() <= 0) {
                throw new InvalidDataException("Valor deve ser maior que zero");
            }
            if (transaction.getDescription() == null || transaction.getDescription().trim().isEmpty()) {
                throw new InvalidDataException("Descrição é obrigatória");
            }
            if (transaction.getDate() == null) {
                throw new InvalidDataException("Data é obrigatória");
            }
            if (transaction.getType() == null) {
                throw new InvalidDataException("Tipo é obrigatório");
            }
            
            transactionService.save(transaction);
            attr.addFlashAttribute("message", "Transação inserida com sucesso!");
            model.setViewName("redirect:transactions");
        } catch (Exception e) {
            if (e instanceof InvalidDataException) {
                throw e;
            }
            throw new BusinessException("Erro ao salvar transação", e);
        }
        return model;
    }

    @GetMapping
    public ModelAndView listAll(ModelAndView model){
        try {
            model.addObject("transactions", transactionService.findAll());
            model.setViewName("transactions/list");
        } catch (Exception e) {
            throw new BusinessException("Erro ao listar transações", e);
        }
        return model;
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