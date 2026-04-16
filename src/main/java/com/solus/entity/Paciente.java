package com.solus.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pacientes")
public class Paciente {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nome;
    
    @Column(unique = true)
    private String cpf;
    
    private String rg;
    
    private LocalDate dataNascimento;
    
    private String sexo;
    
    private String telefone;
    
    private String email;
    
    private String endereco;
    
    private String cidade;
    
    private String estado;
    
    private String cep;
    
    @ManyToOne
    @JoinColumn(name = "convenio_id")
    private Convenio convenio;
    
    private String numeroCarteirinha;
    
    @Column(length = 500)
    private String observacoes;
    
    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;
    
    private Boolean ativo = true;
    
    @Column(nullable = false)
    private LocalDateTime dataCadastro;
}