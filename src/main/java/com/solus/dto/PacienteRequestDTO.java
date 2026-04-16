package com.solus.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PacienteRequestDTO {
    
    @NotBlank(message = "Nome é obrigatório")
    private String nome;
    
    private String cpf;
    
    private String rg;
    
    @Past(message = "Data de nascimento deve ser no passado")
    private LocalDate dataNascimento;
    
    private String sexo;
    
    private String telefone;
    
    private String email;
    
    private String endereco;
    
    private String cidade;
    
    private String estado;
    
    private String cep;
    
    private Long convenioId; 
    
    private String numeroCarteirinha;
    
    private String observacoes;
    
    private Boolean ativo = true;
}