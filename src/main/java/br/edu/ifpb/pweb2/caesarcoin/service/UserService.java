package br.edu.ifpb.pweb2.caesarcoin.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.edu.ifpb.pweb2.caesarcoin.model.AccountOwner;
import br.edu.ifpb.pweb2.caesarcoin.model.User;
import br.edu.ifpb.pweb2.caesarcoin.model.Authority;
import br.edu.ifpb.pweb2.caesarcoin.repository.AccountOwnerRepository;
import br.edu.ifpb.pweb2.caesarcoin.repository.UserRepository;
import br.edu.ifpb.pweb2.caesarcoin.repository.AuthorityRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AccountOwnerRepository accountOwnerRepository;
    
    @Autowired
    private AuthorityRepository authorityRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUserWithAccountOwner(String username, String email, String name, String password, boolean isAdmin) {
        // Criar User
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setName(name);
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(true);
        user = userRepository.save(user);
        
        // Criar authorities
        String[] roles = isAdmin ? new String[]{"ROLE_USER", "ROLE_ADMIN"} : new String[]{"ROLE_USER"};
        for (String role : roles) {
            Authority authority = new Authority();
            Authority.AuthorityId authorityId = new Authority.AuthorityId();
            authorityId.setUsername(username);
            authorityId.setAuthority(role);
            authority.setId(authorityId);
            authority.setUsername(user);
            authority.setAuthority(role);
            authorityRepository.save(authority);
        }
        
        // Criar AccountOwner correspondente
        AccountOwner owner = new AccountOwner();
        owner.setUser(user);
        accountOwnerRepository.save(owner);
        
        return user;
    }
    
    public User updateUser(User user) {
        return userRepository.save(user);
    }
    
    public boolean isAdmin(String username) {
        List<Authority> authorities = authorityRepository.findByUsernameUsername(username);
        return authorities.stream().anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));
    }
    
    public User findByUsername(String username) {
        return userRepository.findById(username).orElse(null);
    }
}
