package br.edu.ifpb.pweb2.caesarcoin.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import br.edu.ifpb.pweb2.caesarcoin.model.AccountOwner;
import br.edu.ifpb.pweb2.caesarcoin.repository.AccountOwnerRepository;
import br.edu.ifpb.pweb2.caesarcoin.util.PasswordUtil;

@Component
public class AccountOwnerInitializer implements ApplicationRunner {
    
    @Autowired
    private AccountOwnerRepository accOwnerRepo;

    @Override
    public void run(ApplicationArguments args){
        if (accOwnerRepo.findByEmail("admin@bitbank.com") == null){
            AccountOwner accOwner = new AccountOwner();
            accOwner.setName("Administrador");
            accOwner.setEmail("admin@caesarcoin.com");
            accOwner.setPassword(PasswordUtil.hashPassword("123"));
            accOwner.setAdmin(true);

            accOwnerRepo.save(accOwner);
        }
    }

}
