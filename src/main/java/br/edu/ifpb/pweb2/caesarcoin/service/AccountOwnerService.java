package br.edu.ifpb.pweb2.caesarcoin.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.edu.ifpb.pweb2.caesarcoin.model.AccountOwner;
import br.edu.ifpb.pweb2.caesarcoin.model.Authority;
import br.edu.ifpb.pweb2.caesarcoin.model.User;
import br.edu.ifpb.pweb2.caesarcoin.dto.AccountOwnerDTO;
import br.edu.ifpb.pweb2.caesarcoin.repository.AccountOwnerRepository;
import br.edu.ifpb.pweb2.caesarcoin.repository.UserRepository;
import br.edu.ifpb.pweb2.caesarcoin.repository.AuthorityRepository;
// import br.edu.ifpb.pweb2.caesarcoin.util.PasswordUtil;

@Component
public class AccountOwnerService implements Service<AccountOwner, Integer>{

    @Autowired
    private AccountOwnerRepository accOwnerRepo;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AuthorityRepository authorityRepository;
    
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
            // Apenas salvar o AccountOwner já que não tem mais campos próprios para validar
            // O User deve ser salvo separadamente
            return accOwnerRepo.save(accOwner);
        } catch (Exception e) {
            System.out.println("Erro detalhado: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro ao salvar correntista: " + e.getMessage(), e);
        }
    }

    public AccountOwner findByUserUsername(String username) {
        return accOwnerRepo.findByUserUsername(username);
    }
    
    public AccountOwner saveFromDTO(AccountOwnerDTO dto) {
        try {
            User user;
            AccountOwner accountOwner;
            
            if (dto.getId() != null) {
                // Edição - buscar o AccountOwner existente
                accountOwner = findById(dto.getId());
                if (accountOwner == null) {
                    throw new RuntimeException("Correntista não encontrado");
                }
                user = accountOwner.getUser();
                if (user == null) {
                    throw new RuntimeException("Usuário associado não encontrado");
                }
            } else {
                // Criação - criar novo User e AccountOwner
                user = new User();
                accountOwner = new AccountOwner();
            }
            
            // Atualizar dados do User
            user.setName(dto.getName());
            user.setEmail(dto.getEmail());
            user.setUsername(dto.getEmail()); // usar email como username
            user.setEnabled(dto.getEnabled());
            
            // Atualizar senha apenas se fornecida
            if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
                user.setPassword(passwordEncoder.encode(dto.getPassword()));
            }
            
            // Salvar User
            user = userRepository.save(user);
            
            // Configurar relacionamento
            accountOwner.setUser(user);
            
            // Salvar AccountOwner
            accountOwner = accOwnerRepo.save(accountOwner);
            
            // Gerenciar authorities apenas para novos usuários ou quando mudança de admin
            if (dto.getId() == null || shouldUpdateAuthorities(user.getUsername(), dto.getAdmin())) {
                updateAuthorities(user.getUsername(), dto.getAdmin());
            }
            
            return accountOwner;
        } catch (Exception e) {
            System.out.println("Erro detalhado: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro ao salvar correntista: " + e.getMessage(), e);
        }
    }
    
    private boolean shouldUpdateAuthorities(String username, Boolean isAdmin) {
        List<Authority> authorities = authorityRepository.findByUsernameUsername(username);
        boolean currentlyAdmin = authorities.stream()
            .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));
        return currentlyAdmin != (isAdmin != null && isAdmin);
    }
    
    private void updateAuthorities(String username, Boolean isAdmin) {
        User user = userRepository.findById(username).orElse(null);
        if (user == null) return;
        
        // Remover authorities existentes
        List<Authority> existingAuthorities = authorityRepository.findByUsernameUsername(username);
        authorityRepository.deleteAll(existingAuthorities);
        
        // Adicionar ROLE_USER sempre
        Authority userAuth = new Authority();
        Authority.AuthorityId userId = new Authority.AuthorityId();
        userId.setUsername(username);
        userId.setAuthority("ROLE_USER");
        userAuth.setId(userId);
        userAuth.setUsername(user);
        userAuth.setAuthority("ROLE_USER");
        authorityRepository.save(userAuth);
        
        // Adicionar ROLE_ADMIN se necessário
        if (isAdmin != null && isAdmin) {
            Authority adminAuth = new Authority();
            Authority.AuthorityId adminId = new Authority.AuthorityId();
            adminId.setUsername(username);
            adminId.setAuthority("ROLE_ADMIN");
            adminAuth.setId(adminId);
            adminAuth.setUsername(user);
            adminAuth.setAuthority("ROLE_ADMIN");
            authorityRepository.save(adminAuth);
        }
    }

}
