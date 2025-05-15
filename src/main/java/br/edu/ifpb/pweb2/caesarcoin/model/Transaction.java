package br.edu.ifpb.pweb2.caesarcoin.model;

import java.sql.Date;

import lombok.Data;

@Data
public class Transaction {
    public Long id;
    public Date date;
    public String description;
    public Double value;
    public String type;
    public Long idCategory;
}
