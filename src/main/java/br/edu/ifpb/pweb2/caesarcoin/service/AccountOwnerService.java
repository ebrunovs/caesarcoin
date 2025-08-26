package br.edu.ifpb.pweb2.caesarcoin.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.edu.ifpb.pweb2.caesarcoin.model.AccountOwner;
import br.edu.ifpb.pweb2.caesarcoin.model.User;
import br.edu.ifpb.pweb2.caesarcoin.repository.AccountOwnerRepository;
import br.edu.ifpb.pweb2.caesarcoin.repository.UserRepository;
// import br.edu.ifpb.pweb2.caesarcoin.util.PasswordUtil;

@Component
public class AccountOwnerService implements Service<AccountOwner, Integer>{

    @Autowired
    private AccountOwnerRepository accOwnerRepo;

    @Autowired
    private UserRepository userRepo;

    @Override
    public List<AccountOwner> findAll(){
        return accOwnerRepo.findAll();
    }

    @Override
    public Page<AccountOwner> findAll(Pageable p) {
        return accOwnerRepo.findAll(p);
    }


    @Override
    public AccountOwner findById(Integer id) {
        return accOwnerRepo.findById(id).orElse(null);
    }
    public void deleteById(Integer id) {
        accOwnerRepo.deleteById(id);
    }

    @Override
    public AccountOwner save(AccountOwner accOwner) {
        return accOwnerRepo.save(accOwner);
    }

    public List<User> findEnabledUsers() {
        return userRepo.findByEnabledTrue();
    }

    public AccountOwner findByEmail(String email) {
        return accOwnerRepo.findByEmail(email);
    }

}
