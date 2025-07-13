package br.edu.ifpb.pweb2.caesarcoin.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "account")
@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate date;
    private String description;
    private Double value;
    
    @Enumerated(EnumType.STRING)
    private TransactionType type;
    
    @ManyToOne
    @JoinColumn(name = "id_category")
    private Category category;
    @ManyToOne
    @JoinColumn(name = "id_account")
    private Account account;
    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

}
