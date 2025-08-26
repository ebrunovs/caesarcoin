package br.edu.ifpb.pweb2.caesarcoin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import br.edu.ifpb.pweb2.caesarcoin.model.AccountOwner;
import br.edu.ifpb.pweb2.caesarcoin.model.Authority;
import br.edu.ifpb.pweb2.caesarcoin.repository.AccountOwnerRepository;
import br.edu.ifpb.pweb2.caesarcoin.repository.UserRepository;
import br.edu.ifpb.pweb2.caesarcoin.repository.AuthorityRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AccountOwnerRepository accountOwnerRepository;
    
    @Autowired
    private AuthorityRepository authorityRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Buscar o usuário
        br.edu.ifpb.pweb2.caesarcoin.model.User user = userRepository.findById(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

        // Verificar se o usuário está habilitado
        if (!user.getEnabled()) {
            throw new UsernameNotFoundException("Usuário desabilitado: " + username);
        }

        // Buscar o correntista associado
        AccountOwner accountOwner = accountOwnerRepository.findByEmail(username);
        if (accountOwner != null && !accountOwner.isEnabled()) {
            throw new UsernameNotFoundException("Correntista bloqueado: " + username);
        }

        // Buscar as authorities usando query direta para evitar lazy loading
        List<Authority> authorities = authorityRepository.findByUsernameUsername(username);
        List<SimpleGrantedAuthority> grantedAuthorities = authorities.stream()
            .map(auth -> new SimpleGrantedAuthority(auth.getAuthority()))
            .collect(Collectors.toList());

        // Retornar UserDetails
        return User.builder()
            .username(user.getUsername())
            .password(user.getPassword())
            .authorities(grantedAuthorities)
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(!user.getEnabled())
            .build();
    }
}
