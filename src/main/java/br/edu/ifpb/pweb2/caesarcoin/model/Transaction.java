package br.edu.ifpb.pweb2.caesarcoin.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "Data é obrigatória")
    private LocalDate date;
    @NotBlank(message = "Descrição é obrigatória")
    private String description;
    @NotBlank(message = "Valor é obrigatório")
    @Min(value = 1, message = "Valor não pode ser negativo ou zero")
    private Double value;
    
    @Enumerated(EnumType.STRING)
    @NotBlank(message = "Tipo de transação é obrigatório")
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
