package com.solus.dto;

import com.solus.entity.Especialidade;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EspecialidadeResponseDTO {
    private Long id;
    private String nome;
    private String descricao;
    private Boolean ativo;
    private Long empresaId;
    private String empresaNome;
    
    public static EspecialidadeResponseDTO fromEntity(Especialidade especialidade) {
        return new EspecialidadeResponseDTO(
            especialidade.getId(),
            especialidade.getNome(),
            especialidade.getDescricao(),
            especialidade.getAtivo(),
            especialidade.getEmpresa().getId(),
            especialidade.getEmpresa().getNomeFantasia()
        );
    }
}