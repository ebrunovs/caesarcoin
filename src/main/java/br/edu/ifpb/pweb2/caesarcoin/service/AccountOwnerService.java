package br.edu.ifpb.pweb2.caesarcoin.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.edu.ifpb.pweb2.caesarcoin.model.AccountOwner;
import br.edu.ifpb.pweb2.caesarcoin.model.User;
import br.edu.ifpb.pweb2.caesarcoin.model.Authority;
import br.edu.ifpb.pweb2.caesarcoin.repository.AccountOwnerRepository;
import br.edu.ifpb.pweb2.caesarcoin.repository.UserRepository;
import br.edu.ifpb.pweb2.caesarcoin.repository.AuthorityRepository;
// import br.edu.ifpb.pweb2.caesarcoin.util.PasswordUtil;

@Component
public class AccountOwnerService implements Service<AccountOwner, Integer>{

    @Autowired
    private AccountOwnerRepository accOwnerRepo;

    @Autowired
    private UserRepository userRepo;
    
    @Autowired
    private AuthorityRepository authorityRepo;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

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
        try {
            // Validação de confirmação de senha apenas para novos registros ou quando a senha foi alterada
            if (accOwner.getNewPassword() != null && !accOwner.getNewPassword().isEmpty()) {
                if (accOwner.getConfirmPassword() == null || !accOwner.getNewPassword().equals(accOwner.getConfirmPassword())) {
                    throw new RuntimeException("As senhas não coincidem");
                }
                
                // Verificar se já existe um usuário com este email
                User existingUser = userRepo.findById(accOwner.getEmail()).orElse(null);
                
                User user;
                if (existingUser != null) {
                    // Atualizar usuário existente
                    user = existingUser;
                    user.setPassword(passwordEncoder.encode(accOwner.getNewPassword()));
                    user.setEnabled(accOwner.isEnabled());
                } else {
                    // Criar novo usuário
                    user = new User();
                    user.setUsername(accOwner.getEmail());
                    user.setEmail(accOwner.getEmail());
                    user.setPassword(passwordEncoder.encode(accOwner.getNewPassword()));
                    user.setEnabled(accOwner.isEnabled());
                }
                
                user = userRepo.save(user);
                
                // Criar authorities apenas para novos usuários
                if (existingUser == null) {
                    String[] roles = accOwner.isAdmin() ? new String[]{"ROLE_USER", "ROLE_ADMIN"} : new String[]{"ROLE_USER"};
                    
                    for (String role : roles) {
                        Authority authority = new Authority();
                        Authority.AuthorityId authorityId = new Authority.AuthorityId();
                        authorityId.setUsername(user.getUsername());
                        authorityId.setAuthority(role);
                        authority.setId(authorityId);
                        authority.setUsername(user);
                        authority.setAuthority(role);
                        authorityRepo.save(authority);
                    }
                }
                
                // Sincronizar dados
                accOwner.setUser(user);
                
                // Limpar campos temporários
                accOwner.setNewPassword(null);
                accOwner.setConfirmPassword(null);
            } else if (accOwner.getUser() != null && accOwner.getUser().getUsername() != null) {
                // Caso de edição sem mudança de senha - manter lógica existente
                User user = userRepo.findById(accOwner.getUser().getUsername()).orElse(null);
                if (user != null) {
                    accOwner.setUser(user);
                }
            }
            
            return accOwnerRepo.save(accOwner);
        } catch (Exception e) {
            System.out.println("Erro detalhado: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro ao salvar correntista: " + e.getMessage(), e);
        }
    }

    public AccountOwner findByEmail(String email) {
        return accOwnerRepo.findByEmail(email);
    }

}
