package com.solus.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.solus.enums.TipoProfissional;
import com.solus.enums.TipoUsuario;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuarios")
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nome;
    
    @Column(unique = true)
    private String email;
    
    @Column(nullable = false)
    private String senhaHash;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoUsuario tipo;  
    
    private String registroProfissional; 
    
    @ManyToOne
    @JoinColumn(name = "especialidade_id")
    private Especialidade especialidade; 
    
    @Enumerated(EnumType.STRING)
    private TipoProfissional tipoProfissional; 
    
    private String telefone;
    
    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;
    
    private Boolean ativo = true;
    
    @Column(nullable = false)
    private LocalDateTime dataCadastro;
    
    private LocalDateTime ultimoAcesso;
    
    private Boolean precisaTrocarSenha = true;
}