package com.solus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.solus.entity.Agenda;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AgendaRepository extends JpaRepository<Agenda, Long> {
    
    List<Agenda> findByProfissionalId(Long profissionalId);
    
    List<Agenda> findByEmpresaId(Long empresaId);
    
    List<Agenda> findByProfissionalIdAndDiaSemana(Long profissionalId, DayOfWeek diaSemana);
    
    @Query("SELECT a FROM Agenda a WHERE a.profissional.id = :profissionalId AND a.diaSemana = :diaSemana AND a.ativo = true")
    Optional<Agenda> findAgendaAtivaPorProfissionalEDia(
        @Param("profissionalId") Long profissionalId, 
        @Param("diaSemana") DayOfWeek diaSemana);
    
    @Query("SELECT COUNT(a) > 0 FROM Agenda a WHERE a.profissional.id = :profissionalId " +
    	       "AND a.diaSemana = :diaSemana AND a.ativo = true " +
    	       "AND ((a.horaInicio < :horaFim AND a.horaFim > :horaInicio))")
    	boolean existsHorarioConflitante(
    	    @Param("profissionalId") Long profissionalId,
    	    @Param("diaSemana") DayOfWeek diaSemana,
    	    @Param("horaInicio") LocalTime horaInicio,
    	    @Param("horaFim") LocalTime horaFim);
}