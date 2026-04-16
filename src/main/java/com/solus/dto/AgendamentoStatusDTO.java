package com.solus.dto;

import com.solus.enums.StatusAgendamento;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgendamentoStatusDTO {
    
    @NotNull(message = "Status é obrigatório")
    private StatusAgendamento status;
    
    private String observacoes;
}