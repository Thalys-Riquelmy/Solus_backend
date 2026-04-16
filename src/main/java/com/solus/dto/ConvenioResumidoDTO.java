package com.solus.dto;

import com.solus.entity.Convenio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConvenioResumidoDTO {
    private Long id;
    private String nome;
    private Boolean ativo;
    
    public static ConvenioResumidoDTO fromEntity(Convenio convenio) {
        return new ConvenioResumidoDTO(
            convenio.getId(),
            convenio.getNome(),
            convenio.getAtivo()
        );
    }
}