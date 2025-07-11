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

    @PostMapping("/update")
    public ModelAndView update(Comment comment, ModelAndView model, RedirectAttributes attr) {
        Comment existingComment = commentService.findById(comment.getId());
        if (existingComment == null) {
            attr.addFlashAttribute("message", "Comentário não encontrado!");
            model.setViewName("redirect:/transactions");
            return model;
        }
        
        // Preservar dados originais
        comment.setCreatedAt(existingComment.getCreatedAt());
        comment.setTransaction(existingComment.getTransaction());
        
        commentService.save(comment);
        
        attr.addFlashAttribute("message", "Comentário atualizado com sucesso!");
        model.setViewName("redirect:/transactions/view/" + comment.getTransaction().getId());
        return model;
    }

    @PostMapping("/delete/{id}")
    public ModelAndView delete(@PathVariable(value = "id") Integer id, RedirectAttributes attr) {
        Comment comment = commentService.findById(id);
        if (comment == null) {
            attr.addFlashAttribute("message", "Comentário não encontrado!");
            return new ModelAndView("redirect:/transactions");
        }
        
        Integer transactionId = comment.getTransaction().getId();
        commentService.deleteById(id);
        
        attr.addFlashAttribute("message", "Comentário excluído com sucesso!");
        return new ModelAndView("redirect:/transactions/view/" + transactionId);
    }

    @GetMapping
    public ModelAndView listAll(ModelAndView model) {
        model.addObject("comments", commentService.findAll());
        model.setViewName("comments/list");
        return model;
    }
}