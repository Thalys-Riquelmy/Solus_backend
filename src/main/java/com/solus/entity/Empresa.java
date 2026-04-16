package com.solus.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "empresas")
public class Empresa {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nomeFantasia;
    
    private String razaoSocial;
    
    @Column(unique = true)
    private String cnpj;
    
    private String ie;
    
    private String endereco;
    
    private String cidade;
    
    private String estado;
    
    private String cep;
    
    private String telefone;
    
    private String email;
    
    @Column(nullable = false)
    private LocalDateTime dataCadastro;
    
    private Boolean ativo = true;
}