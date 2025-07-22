package br.edu.ifpb.pweb2.caesarcoin.model;

import jakarta.persistence.*;
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
    private String name;
    private Boolean isActive;
    
    @Enumerated(EnumType.STRING)
    private TransactionType kind;
    
    private Integer ord;
}
