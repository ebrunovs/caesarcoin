package br.edu.ifpb.pweb2.caesarcoin.model;

import lombok.Data;

@Data
public class Account {
    public Integer id;
    public String number;
    public String description;
    public String type;
    public Integer dueDate;
    public Integer accountOwner;
}
