package br.edu.ifpb.pweb2.caesarcoin.model;

import java.io.Serializable;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class Account implements Serializable {
    private static final long serialVersionUID = 1L;

    public Integer id;
    public String number;
    public String description;
    public String type;
    
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    public Integer dueDate;

    public AccountOwner accountOwner;

    public Account(AccountOwner accOwner){
        this.accountOwner = accOwner;
    }
    
}
