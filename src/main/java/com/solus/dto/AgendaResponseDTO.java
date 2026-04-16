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
public class AgendaResponseDTO {
    private Long id;
    private Long profissionalId;
    private String profissionalNome;
    private DayOfWeek diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private Integer intervaloMinutos;
    private Boolean ativo;
    private Long empresaId;
    private String empresaNome;
    
    public static AgendaResponseDTO fromEntity(Agenda agenda) {
        return new AgendaResponseDTO(
            agenda.getId(),
            agenda.getProfissional().getId(),
            agenda.getProfissional().getNome(),
            agenda.getDiaSemana(),
            agenda.getHoraInicio(),
            agenda.getHoraFim(),
            agenda.getIntervaloMinutos(),
            agenda.getAtivo(),
            agenda.getEmpresa().getId(),
            agenda.getEmpresa().getNomeFantasia()
        );
    }
}