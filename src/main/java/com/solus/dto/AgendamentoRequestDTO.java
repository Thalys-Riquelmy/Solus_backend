package com.solus.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import com.solus.enums.TipoConsulta;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgendamentoRequestDTO {
    
    @NotNull(message = "Paciente é obrigatório")
    private Long pacienteId;
    
    @NotNull(message = "Profissional é obrigatório")
    private Long profissionalId;
    
    @NotNull(message = "Data e hora são obrigatórias")
    private LocalDateTime dataHora;
    
    private TipoConsulta tipoConsulta;
    
    private String observacoes;
}