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
public class AtendimentoResponseDTO {
    private Long id;
    private Long agendamentoId;
    private StatusAgendamento status;
    private LocalDateTime dataAgendamento;
    private Long pacienteId;
    private String pacienteNome;
    private String pacienteCpf;
    private Long profissionalId;
    private String profissionalNome;
    private String profissionalRegistro;
    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraFim;
    private String queixaPrincipal;
    private String historicoDoenca;
    private String exameFisico;
    private String diagnostico;
    private String prescricao;
    private String observacoes;
    private Long empresaId;
    private String empresaNome;
    
    public static AtendimentoResponseDTO fromEntity(Atendimento atendimento) {
        return new AtendimentoResponseDTO(
            atendimento.getId(),
            atendimento.getAgendamento().getId(),
            atendimento.getAgendamento().getStatus(),
            atendimento.getAgendamento().getDataHora(),
            atendimento.getAgendamento().getPaciente().getId(),
            atendimento.getAgendamento().getPaciente().getNome(),
            atendimento.getAgendamento().getPaciente().getCpf(),
            atendimento.getProfissional().getId(),
            atendimento.getProfissional().getNome(),
            atendimento.getProfissional().getRegistroProfissional(),
            atendimento.getDataHoraInicio(),
            atendimento.getDataHoraFim(),
            atendimento.getQueixaPrincipal(),
            atendimento.getHistoricoDoenca(),
            atendimento.getExameFisico(),
            atendimento.getDiagnostico(),
            atendimento.getPrescricao(),
            atendimento.getObservacoes(),
            atendimento.getEmpresa().getId(),
            atendimento.getEmpresa().getNomeFantasia()
        );
    }
}