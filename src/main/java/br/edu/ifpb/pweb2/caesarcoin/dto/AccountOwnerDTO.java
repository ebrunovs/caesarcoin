package br.edu.ifpb.pweb2.caesarcoin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountOwnerDTO {
    
    private Integer id;
    
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String name;
    
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ter um formato válido")
    @Size(max = 100, message = "Email deve ter no máximo 100 caracteres")
    private String email;
    
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    private String password;
    
    private String confirmPassword;
    
    private Boolean admin = false;
    
    private Boolean enabled = true;
    
    // Campo para indicar se é uma edição (não requer senha)
    private Boolean isEdit = false;
}
