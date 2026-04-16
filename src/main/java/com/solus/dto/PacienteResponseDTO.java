package com.solus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.solus.entity.Paciente;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PacienteResponseDTO {
    private Long id;
    private String nome;
    private String cpf;
    private String rg;
    private LocalDate dataNascimento;
    private String sexo;
    private String telefone;
    private String email;
    private String endereco;
    private String cidade;
    private String estado;
    private String cep;
    private Long convenioId;
    private String convenioNome;
    private String numeroCarteirinha;
    private String observacoes;
    private Boolean ativo;
    private LocalDateTime dataCadastro;
    
    public static PacienteResponseDTO fromEntity(Paciente paciente) {
        return new PacienteResponseDTO(
            paciente.getId(),
            paciente.getNome(),
            paciente.getCpf(),
            paciente.getRg(),
            paciente.getDataNascimento(),
            paciente.getSexo(),
            paciente.getTelefone(),
            paciente.getEmail(),
            paciente.getEndereco(),
            paciente.getCidade(),
            paciente.getEstado(),
            paciente.getCep(),
            paciente.getConvenio() != null ? paciente.getConvenio().getId() : null,
            paciente.getConvenio() != null ? paciente.getConvenio().getNome() : null,
            paciente.getNumeroCarteirinha(),
            paciente.getObservacoes(),
            paciente.getAtivo(),
            paciente.getDataCadastro()
        );
    }
}