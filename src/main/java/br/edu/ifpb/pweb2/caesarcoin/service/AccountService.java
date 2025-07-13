package br.edu.ifpb.pweb2.caesarcoin.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.edu.ifpb.pweb2.caesarcoin.model.Account;
import br.edu.ifpb.pweb2.caesarcoin.model.AccountOwner;
import br.edu.ifpb.pweb2.caesarcoin.repository.AccountRepository;

@Component
public class AccountService implements Service<Account, Integer> {
    
    @Autowired
    private AccountRepository accRepo;

    @Autowired
    private AccountOwnerService accOwnerService;

    @Override
    public List<Account> findAll(){
        return accRepo.findAll();
    }

    @Override
    public Account findById(Integer id) {
        return accRepo.findById(id).orElse(null);
    }

    @Override
    public Account save(Account conta) {
        AccountOwner correntista = accOwnerService.findById(conta.getAccountOwner().getId());
        conta.setAccountOwner(correntista);
        return accRepo.save(conta);

    }

    public Account findByNumberWithTransactions(String nuAccount){
        return accRepo.findByNumberWithTransactions(nuAccount);

    }

    public Account findByIdWithTransactions(Integer idAccount) {
        return accRepo.findByIdWithTransactions(idAccount);
    }

    public List<Account> findByAccountOwner(AccountOwner accountOwner) {
        return accRepo.findByAccountOwner(accountOwner);
    }


}
