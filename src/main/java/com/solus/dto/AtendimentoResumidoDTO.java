package com.solus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import com.solus.entity.Atendimento;
import com.solus.enums.StatusAgendamento;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AtendimentoResumidoDTO {
    private Long id;
    private Long agendamentoId;
    private String pacienteNome;
    private String profissionalNome;
    private LocalDateTime dataAtendimento;
    private String diagnostico;
    private StatusAgendamento status;
    
    public static AtendimentoResumidoDTO fromEntity(Atendimento atendimento) {
        return new AtendimentoResumidoDTO(
            atendimento.getId(),
            atendimento.getAgendamento().getId(),
            atendimento.getAgendamento().getPaciente().getNome(),
            atendimento.getProfissional().getNome(),
            atendimento.getDataHoraInicio() != null ? 
                atendimento.getDataHoraInicio() : atendimento.getAgendamento().getDataHora(),
            atendimento.getDiagnostico(),
            atendimento.getAgendamento().getStatus()
        );
    }
}