package br.edu.ifpb.pweb2.caesarcoin.model;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.*;
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

    @OneToMany(mappedBy = "accountOwner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Account> accounts;

    @OneToOne
    @JoinColumn(name = "username", referencedColumnName = "username")
    private User user;
}
