package com.solus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HorariosDisponiveisDTO {
    private Long profissionalId;
    private String profissionalNome;
    private LocalDateTime data;
    private List<LocalDateTime> horariosDisponiveis;
}