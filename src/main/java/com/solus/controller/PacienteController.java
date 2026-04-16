package com.solus.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.solus.dto.PacienteRequestDTO;
import com.solus.dto.PacienteResponseDTO;
import com.solus.service.PacienteService;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
@CrossOrigin(origins = "*")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'PROFISSIONAL', 'RECEPCAO')")
    public ResponseEntity<List<PacienteResponseDTO>> listarTodos(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cpf,
            @RequestParam(required = false) Long convenioId,
            @RequestParam(required = false) Boolean ativo) {
        
        var pacientes = pacienteService.listarTodos(nome, cpf, convenioId, ativo);
        return ResponseEntity.ok(pacientes);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'PROFISSIONAL', 'RECEPCAO')")
    public ResponseEntity<PacienteResponseDTO> buscarPorId(@PathVariable Long id) {
        var paciente = pacienteService.buscarPorId(id);
        return ResponseEntity.ok(paciente);
    }

    @GetMapping("/cpf/{cpf}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'PROFISSIONAL', 'RECEPCAO')")
    public ResponseEntity<PacienteResponseDTO> buscarPorCpf(@PathVariable String cpf) {
        var paciente = pacienteService.buscarPorCpf(cpf);
        return ResponseEntity.ok(paciente);
    }

    @GetMapping("/busca")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'PROFISSIONAL', 'RECEPCAO')")
    public ResponseEntity<List<PacienteResponseDTO>> buscarPorNome(@RequestParam String nome) {
        var pacientes = pacienteService.buscarPorNome(nome);
        return ResponseEntity.ok(pacientes);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'RECEPCAO')")
    public ResponseEntity<PacienteResponseDTO> criar(@Valid @RequestBody PacienteRequestDTO request) {
        var response = pacienteService.criar(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'RECEPCAO')")
    public ResponseEntity<PacienteResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody PacienteRequestDTO request) {
        
        var response = pacienteService.atualizar(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/inativar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        pacienteService.inativar(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/ativar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Void> ativar(@PathVariable Long id) {
        pacienteService.ativar(id);
        return ResponseEntity.ok().build();
    }
}