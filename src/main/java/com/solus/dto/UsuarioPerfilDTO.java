package com.solus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import com.solus.entity.Usuario;
import com.solus.enums.TipoUsuario;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioPerfilDTO {
    private Long id;
    private String nome;
    private String email;
    private TipoUsuario tipo;
    private LocalDateTime dataCadastro;
    private Boolean precisaTrocarSenha;
    
    public static UsuarioPerfilDTO fromEntity(Usuario usuario) {
        return new UsuarioPerfilDTO(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getTipo(),
            usuario.getDataCadastro(),
            usuario.getPrecisaTrocarSenha()
        );
    }
}