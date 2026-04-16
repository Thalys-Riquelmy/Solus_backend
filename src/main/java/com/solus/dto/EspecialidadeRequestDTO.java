package com.solus.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EspecialidadeRequestDTO {
    
    @NotBlank(message = "Nome da especialidade é obrigatório")
    private String nome;
    
    private String descricao;
    
    private Boolean ativo = true;
}