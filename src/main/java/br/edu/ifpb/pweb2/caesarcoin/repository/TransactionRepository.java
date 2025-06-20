package br.edu.ifpb.pweb2.caesarcoin.repository;

import br.edu.ifpb.pweb2.caesarcoin.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
}
