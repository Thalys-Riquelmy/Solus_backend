package com.solus.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.solus.dto.AtendimentoRequestDTO;
import com.solus.dto.AtendimentoResponseDTO;
import com.solus.dto.AtendimentoResumidoDTO;
import com.solus.service.AtendimentoService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/atendimentos")
@CrossOrigin(origins = "*")
public class AtendimentoController {

    @Autowired
    private AtendimentoService atendimentoService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'RECEPCAO', 'PROFISSIONAL')")
    public ResponseEntity<List<AtendimentoResumidoDTO>> listarTodos(
            @RequestParam(required = false) Long pacienteId,
            @RequestParam(required = false) Long profissionalId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim) {
        
        var atendimentos = atendimentoService.listarTodos(pacienteId, profissionalId, dataInicio, dataFim);
        return ResponseEntity.ok(atendimentos);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'RECEPCAO', 'PROFISSIONAL')")
    public ResponseEntity<AtendimentoResponseDTO> buscarPorId(@PathVariable Long id) {
        var atendimento = atendimentoService.buscarPorId(id);
        return ResponseEntity.ok(atendimento);
    }

    @GetMapping("/agendamento/{agendamentoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'RECEPCAO', 'PROFISSIONAL')")
    public ResponseEntity<AtendimentoResponseDTO> buscarPorAgendamentoId(@PathVariable Long agendamentoId) {
        var atendimento = atendimentoService.buscarPorAgendamentoId(agendamentoId);
        return ResponseEntity.ok(atendimento);
    }

    @GetMapping("/paciente/{pacienteId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'RECEPCAO', 'PROFISSIONAL')")
    public ResponseEntity<List<AtendimentoResumidoDTO>> listarPorPaciente(@PathVariable Long pacienteId) {
        var atendimentos = atendimentoService.listarPorPaciente(pacienteId);
        return ResponseEntity.ok(atendimentos);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'RECEPCAO', 'PROFISSIONAL')")
    public ResponseEntity<AtendimentoResponseDTO> criar(@Valid @RequestBody AtendimentoRequestDTO request) {
        var response = atendimentoService.criar(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'RECEPCAO', 'PROFISSIONAL')")
    public ResponseEntity<AtendimentoResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AtendimentoRequestDTO request) {
        
        var response = atendimentoService.atualizar(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/finalizar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'PROFISSIONAL')")
    public ResponseEntity<Void> finalizarAtendimento(@PathVariable Long id) {
        atendimentoService.finalizarAtendimento(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        atendimentoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}