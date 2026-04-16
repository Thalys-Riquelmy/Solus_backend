package com.solus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import com.solus.entity.Agendamento;
import com.solus.enums.StatusAgendamento;
import com.solus.enums.TipoConsulta;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgendamentoResumidoDTO {
    private Long id;
    private String pacienteNome;
    private String profissionalNome;
    private LocalDateTime dataHora;
    private StatusAgendamento status;
    private TipoConsulta tipoConsulta;
    
    public static AgendamentoResumidoDTO fromEntity(Agendamento agendamento) {
        return new AgendamentoResumidoDTO(
            agendamento.getId(),
            agendamento.getPaciente().getNome(),
            agendamento.getProfissional().getNome(),
            agendamento.getDataHora(),
            agendamento.getStatus(),
            agendamento.getTipoConsulta()
        );
    }
}