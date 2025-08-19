package br.edu.ifpb.pweb2.caesarcoin.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotBlank(message = "Nome da categoria é obrigatório")
    private String name;
    private Boolean isActive;
    
    @Enumerated(EnumType.STRING)
    private TransactionType kind;
    @Min(value = 1, message = "Ordem deve ser maior que 0")
    @Max(value = 20, message = "Ordem deve ser menor ou igual a 20")
    private Integer ord;
}
