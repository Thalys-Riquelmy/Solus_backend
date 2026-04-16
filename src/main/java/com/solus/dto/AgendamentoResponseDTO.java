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
public class AgendamentoResponseDTO {
    private Long id;
    private Long pacienteId;
    private String pacienteNome;
    private String pacienteCpf;
    private Long profissionalId;
    private String profissionalNome;
    private String profissionalRegistro;
    private LocalDateTime dataHora;
    private StatusAgendamento status;
    private TipoConsulta tipoConsulta;
    private String observacoes;
    private Long usuarioCriacaoId;
    private String usuarioCriacaoNome;
    private LocalDateTime dataCriacao;
    private Long empresaId;
    private String empresaNome;
    
    public static AgendamentoResponseDTO fromEntity(Agendamento agendamento) {
        return new AgendamentoResponseDTO(
            agendamento.getId(),
            agendamento.getPaciente().getId(),
            agendamento.getPaciente().getNome(),
            agendamento.getPaciente().getCpf(),
            agendamento.getProfissional().getId(),
            agendamento.getProfissional().getNome(),
            agendamento.getProfissional().getRegistroProfissional(),
            agendamento.getDataHora(),
            agendamento.getStatus(),
            agendamento.getTipoConsulta(),
            agendamento.getObservacoes(),
            agendamento.getUsuarioCriacao().getId(),
            agendamento.getUsuarioCriacao().getNome(),
            agendamento.getDataCriacao(),
            agendamento.getEmpresa().getId(),
            agendamento.getEmpresa().getNomeFantasia()
        );
    }
}