package com.solus.dto;

import com.solus.entity.Especialidade;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EspecialidadeResumidoDTO {
    private Long id;
    private String nome;
    private Boolean ativo;
    private String descricao;
    
    public static EspecialidadeResumidoDTO fromEntity(Especialidade especialidade) {
        if (especialidade == null) {
            return null; 
        }
        
        return new EspecialidadeResumidoDTO(
            especialidade.getId(),
            especialidade.getNome(),
            especialidade.getAtivo(),
            especialidade.getDescricao()
        );
    }
}