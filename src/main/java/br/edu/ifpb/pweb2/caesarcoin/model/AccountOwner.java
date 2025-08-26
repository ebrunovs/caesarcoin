package br.edu.ifpb.pweb2.caesarcoin.model;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AccountOwner implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Nome deve conter apenas letras e espaços")
    private String name;

    // Campo para capturar a senha do formulário (não persistido)
    @Transient
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    private String newPassword;

    // Campo para confirmação da senha (não persistido)
    @Transient
    private String confirmPassword;

    // Senha criptografada que será sincronizada com User
    private String password;

    @Email(message = "Email deve ser válido")
    private String email;

    private boolean admin;
    
    @Column(columnDefinition = "boolean default true")
    private boolean enabled = true;

    @OneToMany(mappedBy = "accountOwner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Account> accounts;

    @OneToOne
    @JoinColumn(name = "username", referencedColumnName = "username")
    private User user;
}
