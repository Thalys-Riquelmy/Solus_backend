package com.solus.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "agendas")
public class Agenda {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "profissional_id", nullable = false)
    private Usuario profissional;
    
    @Enumerated(EnumType.STRING)
    private DayOfWeek diaSemana;
    
    private LocalTime horaInicio;
    
    private LocalTime horaFim;
    
    private Integer intervaloMinutos; // 30, 60, etc
    
    private Boolean ativo = true;
    
    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;
}