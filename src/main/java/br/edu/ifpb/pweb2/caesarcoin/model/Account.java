package br.edu.ifpb.pweb2.caesarcoin.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Account implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String number;
    private String description;
    private String type;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dueDate;

    @ManyToOne
    @JoinColumn(name = "id_accountOwner")
    private AccountOwner accountOwner;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Transaction> transactions = new HashSet<Transaction>();

    public Account(AccountOwner accOwner){
        this.accountOwner = accOwner;
    }


    public void addTransaction(Transaction transaction ,Category category) {
        this.transactions.add(transaction);
        transaction.setAccount(this);
        transaction.setCategory(category);
    }

}
