package br.edu.ifpb.pweb2.caesarcoin.model;

import lombok.Data;

@Data
public class Comment {
    public Long id;
    public String text;
    public Long idTransaction;
}
