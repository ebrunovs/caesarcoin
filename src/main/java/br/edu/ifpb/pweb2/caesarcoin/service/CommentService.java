package br.edu.ifpb.pweb2.caesarcoin.service;

import br.edu.ifpb.pweb2.caesarcoin.model.Comment;
import br.edu.ifpb.pweb2.caesarcoin.model.Transaction;
import br.edu.ifpb.pweb2.caesarcoin.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommentService implements Service<Comment, Integer> {

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public List<Comment> findAll() {
        return commentRepository.findAll();
    }

    @Override
    public Comment findById(Integer id) {
        return commentRepository.findById(id).orElse(null);
    }

    @Override
    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    public List<Comment> findByTransaction(Transaction transaction) {
        return commentRepository.findByTransaction(transaction);
    }

    public List<Comment> findByTransactionOrderByCreatedAtDesc(Transaction transaction) {
        return commentRepository.findByTransactionOrderByCreatedAtDesc(transaction);
    }

    public void deleteById(Integer id) {
        commentRepository.deleteById(id);
    }
}