package br.edu.ifpb.pweb2.caesarcoin.controller;

import br.edu.ifpb.pweb2.caesarcoin.model.Transaction;
import br.edu.ifpb.pweb2.caesarcoin.model.Comment;
import br.edu.ifpb.pweb2.caesarcoin.service.TransactionService;
import br.edu.ifpb.pweb2.caesarcoin.service.CommentService;
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
        Transaction transaction = transactionService.findById(id);
        model.addObject("transaction", transaction);
        model.addObject("comment", new Comment());
        
        // Se há um comentário sendo editado, adiciona ao modelo
        if (editCommentId != null) {
            model.addObject("editingComment", editCommentId);
        }
        
        model.setViewName("accounts/transactionView");
        return model;
    }

    @PostMapping("/comment")
    public ModelAndView addComment(Comment comment, Integer transactionId, ModelAndView model, RedirectAttributes attr) {
        Transaction transaction = transactionService.findById(transactionId);
        comment.setTransaction(transaction);
        commentService.save(comment);
        
        attr.addFlashAttribute("message", "Comentário adicionado com sucesso!");
        model.setViewName("redirect:/transactions/view/" + transactionId);
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
}