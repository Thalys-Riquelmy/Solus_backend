package com.solus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.solus.entity.Agendamento;
import com.solus.enums.StatusAgendamento;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
    
    List<Agendamento> findByPacienteId(Long pacienteId);
    
    List<Agendamento> findByProfissionalId(Long profissionalId);
    
    List<Agendamento> findByEmpresaId(Long empresaId);
    
    List<Agendamento> findByStatus(StatusAgendamento status);
    
    @Query("SELECT a FROM Agendamento a WHERE a.profissional.id = :profissionalId " +
           "AND a.dataHora BETWEEN :inicio AND :fim " +
           "AND a.status NOT IN ('CANCELADO')")
    List<Agendamento> findAgendamentosNoPeriodo(
        @Param("profissionalId") Long profissionalId,
        @Param("inicio") LocalDateTime inicio,
        @Param("fim") LocalDateTime fim);
    
    @Query("SELECT COUNT(a) > 0 FROM Agendamento a WHERE a.profissional.id = :profissionalId " +
           "AND a.dataHora = :dataHora " +
           "AND a.status NOT IN ('CANCELADO', 'REALIZADO')")
    boolean existsHorarioOcupado(
        @Param("profissionalId") Long profissionalId,
        @Param("dataHora") LocalDateTime dataHora);
    
    @Query("SELECT a FROM Agendamento a WHERE a.empresa.id = :empresaId " +
           "AND a.dataHora BETWEEN :inicio AND :fim " +
           "ORDER BY a.dataHora")
    List<Agendamento> findAgendamentosPorPeriodo(
        @Param("empresaId") Long empresaId,
        @Param("inicio") LocalDateTime inicio,
        @Param("fim") LocalDateTime fim);
    
    @Query("SELECT a FROM Agendamento a WHERE a.paciente.id = :pacienteId " +
           "ORDER BY a.dataHora DESC")
    List<Agendamento> findHistoricoPaciente(@Param("pacienteId") Long pacienteId);
}