package br.edu.ifpb.pweb2.caesarcoin.repository;

import br.edu.ifpb.pweb2.caesarcoin.model.Comment;
import br.edu.ifpb.pweb2.caesarcoin.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByTransaction(Transaction transaction);
    List<Comment> findByTransactionOrderByCreatedAtDesc(Transaction transaction);
}