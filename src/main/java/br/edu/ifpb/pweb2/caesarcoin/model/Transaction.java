package br.edu.ifpb.pweb2.caesarcoin.model;

import java.sql.Date;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Date date;
    private String description;
    private Double value;
    private String type;
    private Long idCategory;
    @ManyToOne
    @JoinColumn(name = "id_account")
    private Account account;


}
