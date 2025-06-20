package br.edu.ifpb.pweb2.caesarcoin.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.edu.ifpb.pweb2.caesarcoin.model.AccountOwner;
import br.edu.ifpb.pweb2.caesarcoin.repository.AccountOwnerRepository;
import br.edu.ifpb.pweb2.caesarcoin.util.PasswordUtil;

@Component
public class AccountOwnerService implements Service<AccountOwner, Integer>{

    @Autowired
    private AccountOwnerRepository accOwnerRepo;

    @Override
    public List<AccountOwner> findAll(){
        return accOwnerRepo.findAll();
    }

    @Override
    public AccountOwner findById(Integer id) {
        return accOwnerRepo.findById(id).orElse(null);
    }

    @Override
    public AccountOwner save(AccountOwner accOwner) {
        accOwner.setPassword(PasswordUtil.hashPassword(accOwner.getPassword()));
        return accOwnerRepo.save(accOwner);
    }

}
