package com.solus.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConvenioRequestDTO {
    
    @NotBlank(message = "Nome do convênio é obrigatório")
    private String nome;
    
    private String cnpj;
    
    private String telefone;
    
    private String email;
    
    private Boolean ativo = true;
}