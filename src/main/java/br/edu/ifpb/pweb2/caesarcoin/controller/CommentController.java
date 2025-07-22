package br.edu.ifpb.pweb2.caesarcoin.controller;

import br.edu.ifpb.pweb2.caesarcoin.exception.BusinessException;
import br.edu.ifpb.pweb2.caesarcoin.exception.InvalidDataException;
import br.edu.ifpb.pweb2.caesarcoin.exception.ResourceNotFoundException;
import br.edu.ifpb.pweb2.caesarcoin.model.Comment;
import br.edu.ifpb.pweb2.caesarcoin.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;


@Controller
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/update")
    public ModelAndView update(Comment comment, ModelAndView model, RedirectAttributes attr) {
        try {
            if (comment.getId() == null || comment.getId() <= 0) {
                throw new InvalidDataException("ID do comentário inválido");
            }
            if (comment.getText() == null || comment.getText().trim().isEmpty()) {
                throw new InvalidDataException("Texto do comentário é obrigatório");
            }
            
            Comment existingComment = commentService.findById(comment.getId());
            if (existingComment == null) {
                throw new ResourceNotFoundException("Comentário não encontrado!");
            }
            
            // Preservar dados originais
            comment.setCreatedAt(existingComment.getCreatedAt());
            comment.setTransaction(existingComment.getTransaction());
            
            commentService.save(comment);
            
            attr.addFlashAttribute("message", "Comentário atualizado com sucesso!");
            model.setViewName("redirect:/transactions/view/" + comment.getTransaction().getId());
        } catch (Exception e) {
            if (e instanceof InvalidDataException || e instanceof ResourceNotFoundException) {
                throw e;
            }
            throw new BusinessException("Erro ao atualizar comentário", e);
        }
        return model;
    }

    @RequestMapping("/delete/{id}")
    public ModelAndView delete(@PathVariable(value = "id") Integer id, RedirectAttributes attr) {
        try {
            if (id == null || id <= 0) {
                throw new InvalidDataException("ID do comentário inválido");
            }
            
            Comment comment = commentService.findById(id);
            if (comment == null) {
                throw new ResourceNotFoundException("Comentário não encontrado!");
            }
            
            Integer transactionId = comment.getTransaction().getId();
            commentService.deleteById(id);
            
            attr.addFlashAttribute("message", "Comentário excluído com sucesso!");
            return new ModelAndView("redirect:/transactions/view/" + transactionId);
        } catch (Exception e) {
            if (e instanceof InvalidDataException || e instanceof ResourceNotFoundException) {
                throw e;
            }
            throw new BusinessException("Erro ao excluir comentário", e);
        }
    }

    @GetMapping
    public ModelAndView listAll(ModelAndView model) {
        try {
            model.addObject("comments", commentService.findAll());
            model.setViewName("comments/list");
        } catch (Exception e) {
            throw new BusinessException("Erro ao listar comentários", e);
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