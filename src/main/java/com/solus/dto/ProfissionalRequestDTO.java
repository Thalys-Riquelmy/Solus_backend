package com.solus.dto;

import com.solus.enums.TipoProfissional;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfissionalRequestDTO {
    
    @NotBlank(message = "Nome é obrigatório")
    private String nome;
    
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;
    
    private String senha;
    
    @NotNull(message = "Tipo de profissional é obrigatório")
    private TipoProfissional tipoProfissional;
    
    @NotBlank(message = "Registro profissional é obrigatório")
    private String registroProfissional;
    
    private Long especialidadeId; 
    
    private String telefone;
    
    private Boolean ativo = true;
}