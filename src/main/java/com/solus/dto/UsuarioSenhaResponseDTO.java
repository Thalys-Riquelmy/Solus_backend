package com.solus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioSenhaResponseDTO {
    private Long id;
    private String nome;
    private String email;
    private String senhaTemporaria;
    private String mensagem = "Senha temporária gerada. Usuário deverá trocar no primeiro acesso.";
    
    public UsuarioSenhaResponseDTO(Long id, String nome, String email, String senhaTemporaria) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senhaTemporaria = senhaTemporaria;
    }
}