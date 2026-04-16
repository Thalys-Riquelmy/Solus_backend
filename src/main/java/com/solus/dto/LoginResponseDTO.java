package com.solus.dto;

import com.solus.enums.TipoUsuario;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private Long id;
    private String nome;
    private String email;
    private TipoUsuario tipo;
    private String token;
    private Long empresaId;
}