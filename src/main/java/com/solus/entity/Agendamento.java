package com.solus.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.solus.enums.StatusAgendamento;
import com.solus.enums.TipoConsulta;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "agendamentos")
public class Agendamento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;
    
    @ManyToOne
    @JoinColumn(name = "profissional_id", nullable = false)
    private Usuario profissional;
    
    @Column(nullable = false)
    private LocalDateTime dataHora;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAgendamento status;
    
    @Enumerated(EnumType.STRING)
    private TipoConsulta tipoConsulta;
    
    @Column(length = 500)
    private String observacoes;
    
    @ManyToOne
    @JoinColumn(name = "usuario_criacao_id", nullable = false)
    private Usuario usuarioCriacao;
    
    @Column(nullable = false)
    private LocalDateTime dataCriacao;
    
    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;
}