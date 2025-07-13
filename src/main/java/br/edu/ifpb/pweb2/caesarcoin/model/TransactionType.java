package br.edu.ifpb.pweb2.caesarcoin.model;

public enum TransactionType {
    ENTRADA("ENTRADA"),
    SAIDA("SAIDA"), 
    INVESTIMENTO("INVESTIMENTO");
    
    private final String description;
    
    TransactionType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
