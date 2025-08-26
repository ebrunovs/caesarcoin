package br.edu.ifpb.pweb2.caesarcoin.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.edu.ifpb.pweb2.caesarcoin.model.Account;
import br.edu.ifpb.pweb2.caesarcoin.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    Page<Transaction> findByAccount(Account account, Pageable pageable);

    void deleteById(Integer id);
    
    List<Transaction> findByAccountAndDateBetween(Account account, LocalDate startDate, LocalDate endDate);
}
