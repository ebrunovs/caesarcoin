package br.edu.ifpb.pweb2.caesarcoin.repository;

import br.edu.ifpb.pweb2.caesarcoin.model.Account;
import br.edu.ifpb.pweb2.caesarcoin.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    void deleteById(Integer id);
    
    List<Transaction> findByAccountAndDateBetween(Account account, LocalDate startDate, LocalDate endDate);
}
