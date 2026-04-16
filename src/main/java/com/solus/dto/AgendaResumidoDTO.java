package com.solus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.DayOfWeek;
import java.time.LocalTime;

import com.solus.entity.Agenda;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgendaResumidoDTO {
    private Long id;
    private String profissionalNome;
    private DayOfWeek diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private Integer intervaloMinutos;
    private Boolean ativo;
    
    public static AgendaResumidoDTO fromEntity(Agenda agenda) {
        return new AgendaResumidoDTO(
            agenda.getId(),
            agenda.getProfissional().getNome(),
            agenda.getDiaSemana(),
            agenda.getHoraInicio(),
            agenda.getHoraFim(),
            agenda.getIntervaloMinutos(),
            agenda.getAtivo()
        );
    }
}