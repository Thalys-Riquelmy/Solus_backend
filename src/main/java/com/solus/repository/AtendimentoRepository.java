package com.solus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.solus.entity.Atendimento;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AtendimentoRepository extends JpaRepository<Atendimento, Long> {
    
    Optional<Atendimento> findByAgendamentoId(Long agendamentoId);
    
    List<Atendimento> findByProfissionalId(Long profissionalId);
    
    List<Atendimento> findByEmpresaId(Long empresaId);
    
    @Query("SELECT a FROM Atendimento a WHERE a.agendamento.paciente.id = :pacienteId ORDER BY a.dataHoraInicio DESC")
    List<Atendimento> findByPacienteId(@Param("pacienteId") Long pacienteId);
    
    @Query("SELECT a FROM Atendimento a WHERE a.profissional.id = :profissionalId AND a.dataHoraInicio BETWEEN :inicio AND :fim")
    List<Atendimento> findByProfissionalAndPeriodo(
        @Param("profissionalId") Long profissionalId,
        @Param("inicio") LocalDateTime inicio,
        @Param("fim") LocalDateTime fim);
    
    boolean existsByAgendamentoId(Long agendamentoId);
}