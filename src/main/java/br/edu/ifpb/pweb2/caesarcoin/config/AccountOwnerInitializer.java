package br.edu.ifpb.pweb2.caesarcoin.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import br.edu.ifpb.pweb2.caesarcoin.util.PasswordUtil;
import br.edu.ifpb.pweb2.caesarcoin.model.AccountOwner;
import br.edu.ifpb.pweb2.caesarcoin.repository.AccountOwnerRepository;

@Component
public class AccountOwnerInitializer implements ApplicationRunner {

    String ADMIN_EMAIL = "admin@caesarcoin.tech";

    @Autowired
    private AccountOwnerRepository accOwnerRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (accOwnerRepository.findByEmail(ADMIN_EMAIL) == null) {
            AccountOwner correntista = new AccountOwner();
            correntista.setName("Admin");
            correntista.setEmail(ADMIN_EMAIL);
            correntista.setPassword(PasswordUtil.hashPassword("123"));
            correntista.setAdmin(true);

            accOwnerRepository.save(correntista);
            System.out.println("Correntista admin inserido com sucesso.");
        } else {
            System.out.println("Correntista admin já existe.");
        }
    }
}
