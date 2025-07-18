package br.edu.ifpb.pweb2.caesarcoin.repository;

import java.util.List;
import br.edu.ifpb.pweb2.caesarcoin.model.AccountOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.edu.ifpb.pweb2.caesarcoin.model.Account;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account,Integer> {

    List<Account> findByAccountOwner(AccountOwner accountOwner);
    
    void deleteById(Integer id);
 
    @Query("from Account c left join fetch c.transactions t where c.number = :number")
     Account findByNumberWithTransactions(@Param("number") String number);

    @Query("select distinct c from Account c left join fetch c.transactions t where c.id = :id")
     Account findByIdWithTransactions(@Param("id") Integer id);
}