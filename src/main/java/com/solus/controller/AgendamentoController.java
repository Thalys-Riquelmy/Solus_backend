package com.solus.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.solus.dto.*;
import com.solus.enums.StatusAgendamento;
import com.solus.service.AgendamentoService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/agendamentos")
@CrossOrigin(origins = "*")
public class AgendamentoController {

    @Autowired
    private AgendamentoService agendamentoService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'RECEPCAO', 'PROFISSIONAL')")
    public ResponseEntity<List<AgendamentoResumidoDTO>> listarTodos(
            @RequestParam(required = false) Long pacienteId,
            @RequestParam(required = false) Long profissionalId,
            @RequestParam(required = false) StatusAgendamento status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim) {
        
        var agendamentos = agendamentoService.listarTodos(pacienteId, profissionalId, status, dataInicio, dataFim);
        return ResponseEntity.ok(agendamentos);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'RECEPCAO', 'PROFISSIONAL')")
    public ResponseEntity<AgendamentoResponseDTO> buscarPorId(@PathVariable Long id) {
        var agendamento = agendamentoService.buscarPorId(id);
        return ResponseEntity.ok(agendamento);
    }

    @GetMapping("/paciente/{pacienteId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'RECEPCAO', 'PROFISSIONAL')")
    public ResponseEntity<List<AgendamentoResumidoDTO>> listarPorPaciente(@PathVariable Long pacienteId) {
        var agendamentos = agendamentoService.listarPorPaciente(pacienteId);
        return ResponseEntity.ok(agendamentos);
    }

    @GetMapping("/profissional/{profissionalId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'RECEPCAO', 'PROFISSIONAL')")
    public ResponseEntity<List<AgendamentoResumidoDTO>> listarPorProfissional(@PathVariable Long profissionalId) {
        var agendamentos = agendamentoService.listarPorProfissional(profissionalId);
        return ResponseEntity.ok(agendamentos);
    }

    @GetMapping("/disponiveis")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'RECEPCAO')")
    public ResponseEntity<HorariosDisponiveisDTO> verificarDisponibilidade(
            @RequestParam Long profissionalId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        
        var horarios = agendamentoService.verificarDisponibilidade(profissionalId, data);
        return ResponseEntity.ok(horarios);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'RECEPCAO')")
    public ResponseEntity<AgendamentoResponseDTO> criar(@Valid @RequestBody AgendamentoRequestDTO request) {
        var response = agendamentoService.criar(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'RECEPCAO', 'PROFISSIONAL')")
    public ResponseEntity<AgendamentoResponseDTO> atualizarStatus(
            @PathVariable Long id,
            @Valid @RequestBody AgendamentoStatusDTO request) {
        
        var response = agendamentoService.atualizarStatus(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'RECEPCAO')")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        agendamentoService.cancelar(id);
        return ResponseEntity.noContent().build();
    }
}