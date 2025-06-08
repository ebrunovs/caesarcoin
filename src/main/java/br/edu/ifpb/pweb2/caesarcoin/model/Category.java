package br.edu.ifpb.pweb2.caesarcoin.model;

import lombok.Data;

@Data
public class Category {
    public Integer id;
    public String name;
    public Boolean isActive;
    public String kind;
    public Integer order;
}
