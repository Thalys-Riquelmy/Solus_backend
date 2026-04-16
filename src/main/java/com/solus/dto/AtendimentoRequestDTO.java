package com.solus.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AtendimentoRequestDTO {
    
    @NotNull(message = "Agendamento é obrigatório")
    private Long agendamentoId;
    
    private LocalDateTime dataHoraInicio;
    
    private LocalDateTime dataHoraFim;
    
    private String queixaPrincipal;
    
    private String historicoDoenca;
    
    private String exameFisico;
    
    private String diagnostico;
    
    private String prescricao;
    
    private String observacoes;
}