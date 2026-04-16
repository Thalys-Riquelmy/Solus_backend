package com.solus.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.solus.dto.AgendaRequestDTO;
import com.solus.dto.AgendaResponseDTO;
import com.solus.dto.AgendaResumidoDTO;
import com.solus.service.AgendaService;

import java.time.DayOfWeek;
import java.util.List;

@RestController
@RequestMapping("/api/agendas")
@CrossOrigin(origins = "*")
public class AgendaController {

    @Autowired
    private AgendaService agendaService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'RECEPCAO', 'PROFISSIONAL')")
    public ResponseEntity<List<AgendaResumidoDTO>> listarTodas(
            @RequestParam(required = false) Long profissionalId,
            @RequestParam(required = false) DayOfWeek diaSemana,
            @RequestParam(required = false) Boolean ativo) {
        
        var agendas = agendaService.listarTodas(profissionalId, diaSemana, ativo);
        return ResponseEntity.ok(agendas);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'RECEPCAO', 'PROFISSIONAL')")
    public ResponseEntity<AgendaResponseDTO> buscarPorId(@PathVariable Long id) {
        var agenda = agendaService.buscarPorId(id);
        return ResponseEntity.ok(agenda);
    }

    @GetMapping("/profissional/{profissionalId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'RECEPCAO', 'PROFISSIONAL')")
    public ResponseEntity<List<AgendaResumidoDTO>> listarPorProfissional(@PathVariable Long profissionalId) {
        var agendas = agendaService.listarPorProfissional(profissionalId);
        return ResponseEntity.ok(agendas);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<AgendaResponseDTO> criar(@Valid @RequestBody AgendaRequestDTO request) {
        var response = agendaService.criar(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<AgendaResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AgendaRequestDTO request) {
        
        var response = agendaService.atualizar(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/inativar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        agendaService.inativar(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/ativar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Void> ativar(@PathVariable Long id) {
        agendaService.ativar(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        agendaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}