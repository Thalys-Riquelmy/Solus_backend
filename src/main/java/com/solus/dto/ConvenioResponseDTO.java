package com.solus.dto;

import com.solus.entity.Convenio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConvenioResponseDTO {
    private Long id;
    private String nome;
    private String cnpj;
    private String telefone;
    private String email;
    private Boolean ativo;
    private Long empresaId;
    private String empresaNome;
    
    public static ConvenioResponseDTO fromEntity(Convenio convenio) {
        return new ConvenioResponseDTO(
            convenio.getId(),
            convenio.getNome(),
            convenio.getCnpj(),
            convenio.getTelefone(),
            convenio.getEmail(),
            convenio.getAtivo(),
            convenio.getEmpresa().getId(),
            convenio.getEmpresa().getNomeFantasia()
        );
    }
}