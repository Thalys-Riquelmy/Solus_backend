package com.solus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import com.solus.entity.Usuario;
import com.solus.enums.TipoProfissional;
import com.solus.enums.TipoUsuario;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO {
    private Long id;
    private String nome;
    private String email;
    private TipoUsuario tipo;
    private Boolean ativo;
    private LocalDateTime dataCadastro;
    private LocalDateTime ultimoAcesso;
    private Boolean precisaTrocarSenha;
    private TipoProfissional tipo_profissional;
    private EspecialidadeResumidoDTO especialidade;
    private String registroProfissional;
    
    public static UsuarioResponseDTO fromEntity(Usuario usuario) {
        return new UsuarioResponseDTO(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getTipo(),
            usuario.getAtivo(),
            usuario.getDataCadastro(),
            usuario.getUltimoAcesso(),
            usuario.getPrecisaTrocarSenha(),
            usuario.getTipoProfissional(),
            EspecialidadeResumidoDTO.fromEntity(usuario.getEspecialidade()),
            usuario.getRegistroProfissional()
        );
    }
}