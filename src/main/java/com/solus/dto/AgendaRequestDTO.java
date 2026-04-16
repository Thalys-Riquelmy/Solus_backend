package com.solus.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgendaRequestDTO {
    
    @NotNull(message = "Profissional é obrigatório")
    private Long profissionalId;
    
    @NotNull(message = "Dia da semana é obrigatório")
    private DayOfWeek diaSemana;
    
    @NotNull(message = "Hora de início é obrigatória")
    private LocalTime horaInicio;
    
    @NotNull(message = "Hora de fim é obrigatória")
    private LocalTime horaFim;
    
    @NotNull(message = "Intervalo é obrigatório")
    private Integer intervaloMinutos;
    
    private Boolean ativo = true;
}