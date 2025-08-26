package br.edu.ifpb.pweb2.caesarcoin.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import br.edu.ifpb.pweb2.caesarcoin.model.AccountOwner;
import br.edu.ifpb.pweb2.caesarcoin.repository.AccountOwnerRepository;

@Configuration
@EnableWebSecurity
public class CaesarcoinSecurityConfig {
    
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private AccountOwnerRepository accountOwnerRepository;

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
        JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
        
        // Cria usuários padrão se não existirem
        createDefaultUsersIfNotExists(users);
        
        return users;
    }

    private void createDefaultUsersIfNotExists(JdbcUserDetailsManager users) {
        // Usuários padrão do sistema CaesarCoin
        UserDetails admin = User.withUsername("admin@caesarcoin.com")
            .password(passwordEncoder().encode("admin123"))
            .roles("USER", "ADMIN")
            .build();
            
        UserDetails demo = User.withUsername("demo@caesarcoin.com")
            .password(passwordEncoder().encode("demo123"))
            .roles("USER")
            .build();
            
        UserDetails caesar = User.withUsername("caesar@rome.com")
            .password(passwordEncoder().encode("veni123"))
            .roles("USER")
            .build();

        // Evita duplicação dos usuários no banco
        if (!users.userExists(admin.getUsername())) {
            users.createUser(admin);
        }
        if (!users.userExists(demo.getUsername())) {
            users.createUser(demo);
        }
        if (!users.userExists(caesar.getUsername())) {
            users.createUser(caesar);
        }
        
        // Cria AccountOwners correspondentes se não existirem (nomes válidos)
        createAccountOwnerIfNotExists("admin@caesarcoin.com", "Admin Sistema", true);
        createAccountOwnerIfNotExists("demo@caesarcoin.com", "Usuario Demo", false);
        createAccountOwnerIfNotExists("caesar@rome.com", "Julius Caesar", false);
    }
    
    private void createAccountOwnerIfNotExists(String email, String name, boolean isAdmin) {
        try {
            if (accountOwnerRepository.findByEmail(email) == null) {
                AccountOwner owner = new AccountOwner();
                owner.setEmail(email);
                owner.setName(name); // Nomes sem caracteres especiais
                owner.setPassword(passwordEncoder().encode(email.split("@")[0] + "123"));
                owner.setAdmin(isAdmin);
                accountOwnerRepository.save(owner);
            }
        } catch (Exception e) {
            // Log do erro mas não interrompe a inicialização
            System.err.println("Erro ao criar AccountOwner para " + email + ": " + e.getMessage());
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