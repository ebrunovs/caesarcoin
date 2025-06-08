package br.edu.ifpb.pweb2.caesarcoin.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class AccountOwner implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public Integer id;
    public String name;
    public String password;
    public String email;
    public boolean admin;
}
