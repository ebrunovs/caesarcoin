package br.edu.ifpb.pweb2.caesarcoin.controller;

import br.edu.ifpb.pweb2.caesarcoin.model.Comment;
import br.edu.ifpb.pweb2.caesarcoin.model.Transaction;
import br.edu.ifpb.pweb2.caesarcoin.service.CommentService;
import br.edu.ifpb.pweb2.caesarcoin.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private TransactionService transactionService;

    @PostMapping
    public ModelAndView save(Comment comment, Integer transactionId, ModelAndView model, RedirectAttributes attr) {
        Transaction transaction = transactionService.findById(transactionId);
        comment.setTransaction(transaction);
        commentService.save(comment);
        
        attr.addFlashAttribute("message", "Comentário adicionado com sucesso!");
        model.setViewName("redirect:/accounts/view/" + transactionId);
        return model;
    }

    @GetMapping
    public ModelAndView listAll(ModelAndView model) {
        model.addObject("comments", commentService.findAll());
        model.setViewName("comments/list");
        return model;
    }

    @GetMapping("/{id}")
    public ModelAndView getById(@PathVariable(value = "id") Integer id, ModelAndView model) {
        Comment comment = commentService.findById(id);
        model.addObject("comment", comment);
        model.setViewName("comments/form");
        return model;
    }

    @GetMapping("/transaction/{transactionId}")
    public ModelAndView getByTransaction(@PathVariable(value = "transactionId") Integer transactionId, ModelAndView model) {
        Transaction transaction = transactionService.findById(transactionId);
        List<Comment> comments = commentService.findByTransactionOrderByCreatedAtDesc(transaction);
        
        model.addObject("transaction", transaction);
        model.addObject("comments", comments);
        model.setViewName("comments/list");
        return model;
    }

    @PostMapping("/delete/{id}")
    public ModelAndView delete(@PathVariable(value = "id") Integer id, RedirectAttributes attr) {
        Comment comment = commentService.findById(id);
        Integer transactionId = comment.getTransaction().getId();
        
        commentService.deleteById(id);
        
        attr.addFlashAttribute("message", "Comentário removido com sucesso!");
        return new ModelAndView("redirect:/accounts/view/" + transactionId);
    }
}