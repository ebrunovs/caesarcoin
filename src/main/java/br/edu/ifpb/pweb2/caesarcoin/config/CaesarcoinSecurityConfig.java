package br.edu.ifpb.pweb2.caesarcoin.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import br.edu.ifpb.pweb2.caesarcoin.model.AccountOwner;
import br.edu.ifpb.pweb2.caesarcoin.model.Authority;
import br.edu.ifpb.pweb2.caesarcoin.model.User;
import br.edu.ifpb.pweb2.caesarcoin.repository.AccountOwnerRepository;
import br.edu.ifpb.pweb2.caesarcoin.repository.AuthorityRepository;
import br.edu.ifpb.pweb2.caesarcoin.repository.UserRepository;
import br.edu.ifpb.pweb2.caesarcoin.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class CaesarcoinSecurityConfig {
    
    @Autowired
    private AccountOwnerRepository accountOwnerRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AuthorityRepository authorityRepository;
    
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    
    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/images/**", "/auth/**").permitAll()
                .requestMatchers("/accountowners/**").hasRole("ADMIN")
                .requestMatchers("/categories/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .formLogin(form -> form
                .loginPage("/auth")
                .loginProcessingUrl("/auth")
                .defaultSuccessUrl("/home", true)
                .failureUrl("/auth?error")
                .permitAll())
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/auth?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID"))
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/auth/access-denied"));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return customUserDetailsService;
    }

    @Bean
    public CommandLineRunner dataLoader() {
        return args -> {
            try {
                // Create default users after application context is fully loaded
                createDefaultUsersIfNotExists();
            } catch (Exception e) {
                System.err.println("Erro ao criar usuários padrão: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }

    private void createDefaultUsersIfNotExists() {
        try {
            // Criar usuários diretamente usando repository pattern para garantir consistência
            createUserAndOwnerIfNotExists("admin@caesarcoin.com", "admin123", "Admin Sistema", true, new String[]{"ROLE_USER", "ROLE_ADMIN"});
            createUserAndOwnerIfNotExists("demo@caesarcoin.com", "demo123", "Usuario Demo", false, new String[]{"ROLE_USER"});
            createUserAndOwnerIfNotExists("caesar@rome.com", "veni123", "Julius Caesar", false, new String[]{"ROLE_USER"});
        } catch (Exception e) {
            System.err.println("Erro ao criar usuários padrão: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void createUserAndOwnerIfNotExists(String email, String password, String name, boolean isAdmin, String[] roles) {
        try {
            // Verificar se o usuário já existe
            if (userRepository.findById(email).isEmpty()) {
                // Criar User
                User user = new User();
                user.setUsername(email);
                user.setEmail(email);
                user.setName(name);
                user.setPassword(passwordEncoder().encode(password));
                user.setEnabled(true);
                user = userRepository.save(user);
                
                // Criar authorities
                for (String role : roles) {
                    Authority authority = new Authority();
                    Authority.AuthorityId authorityId = new Authority.AuthorityId();
                    authorityId.setUsername(email);
                    authorityId.setAuthority(role);
                    authority.setId(authorityId);
                    authority.setUsername(user);
                    authority.setAuthority(role);
                    authorityRepository.save(authority);
                }
                
                // Criar AccountOwner correspondente
                createAccountOwnerIfNotExists(email, name, isAdmin, user);
            }
        } catch (Exception e) {
            System.err.println("Erro ao criar usuário " + email + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void createAccountOwnerIfNotExists(String email, String name, boolean isAdmin, User user) {
        try {
            // Verificar se já existe um AccountOwner com este User
            if (accountOwnerRepository.findByUser(user) == null) {
                AccountOwner owner = new AccountOwner();
                owner.setUser(user);
                accountOwnerRepository.save(owner);
            }
        } catch (Exception e) {
            System.err.println("Erro ao criar AccountOwner para " + email + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}