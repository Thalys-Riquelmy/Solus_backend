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
@Table(name = "atendimentos")
public class Atendimento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "agendamento_id", nullable = false)
    private Agendamento agendamento;
    
    private LocalDateTime dataHoraInicio;
    
    private LocalDateTime dataHoraFim;
    
    @Column(length = 1000)
    private String queixaPrincipal;
    
    @Column(length = 2000)
    private String historicoDoenca;
    
    @Column(length = 1000)
    private String exameFisico;
    
    @Column(length = 1000)
    private String diagnostico;
    
    @Column(length = 1000)
    private String prescricao;
    
    @Column(length = 2000)
    private String observacoes;
    
    @ManyToOne
    @JoinColumn(name = "profissional_id", nullable = false)
    private Usuario profissional;
    
    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;
}