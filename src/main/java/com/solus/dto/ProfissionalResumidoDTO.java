package com.solus.dto;

import com.solus.entity.Usuario;
import com.solus.enums.TipoProfissional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfissionalResumidoDTO {
    private Long id;
    private String nome;
    private TipoProfissional tipoProfissional;
    private String registroProfissional;
    private String especialidadeNome;
    private String telefone;
    private Boolean ativo;
    
    public static ProfissionalResumidoDTO fromEntity(Usuario usuario) {
        return new ProfissionalResumidoDTO(
            usuario.getId(),
            usuario.getNome(),
            usuario.getTipoProfissional(),
            usuario.getRegistroProfissional(),
            usuario.getEspecialidade() != null ? usuario.getEspecialidade().getNome() : null,
            usuario.getTelefone(),
            usuario.getAtivo()
        );
    }
}