package com.solus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import com.solus.entity.Usuario;
import com.solus.enums.TipoProfissional;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfissionalResponseDTO {
    private Long id;
    private String nome;
    private String email;
    private TipoProfissional tipoProfissional;
    private String registroProfissional;
    private Long especialidadeId;
    private String especialidadeNome;
    private String telefone;
    private Boolean ativo;
    private LocalDateTime dataCadastro;
    
    public static ProfissionalResponseDTO fromEntity(Usuario usuario) {
        return new ProfissionalResponseDTO(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getTipoProfissional(),
            usuario.getRegistroProfissional(),
            usuario.getEspecialidade() != null ? usuario.getEspecialidade().getId() : null,
            usuario.getEspecialidade() != null ? usuario.getEspecialidade().getNome() : null,
            usuario.getTelefone(),
            usuario.getAtivo(),
            usuario.getDataCadastro()
        );
    }
}