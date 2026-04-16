package com.solus.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.solus.dto.EspecialidadeRequestDTO;
import com.solus.dto.EspecialidadeResponseDTO;
import com.solus.dto.EspecialidadeResumidoDTO;
import com.solus.service.EspecialidadeService;

import java.util.List;

@RestController
@RequestMapping("/api/especialidades")
@CrossOrigin(origins = "*")
public class EspecialidadeController {

    @Autowired
    private EspecialidadeService especialidadeService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'RECEPCAO', 'PROFISSIONAL')")
    public ResponseEntity<List<EspecialidadeResumidoDTO>> listarTodas(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Boolean ativo) {
        
        var especialidades = especialidadeService.listarTodas(nome, ativo);
        return ResponseEntity.ok(especialidades);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'RECEPCAO', 'PROFISSIONAL')")
    public ResponseEntity<EspecialidadeResponseDTO> buscarPorId(@PathVariable Long id) {
        var especialidade = especialidadeService.buscarPorId(id);
        return ResponseEntity.ok(especialidade);
    }

    @GetMapping("/nome/{nome}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'RECEPCAO', 'PROFISSIONAL')")
    public ResponseEntity<EspecialidadeResponseDTO> buscarPorNome(@PathVariable String nome) {
        var especialidade = especialidadeService.buscarPorNome(nome);
        return ResponseEntity.ok(especialidade);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<EspecialidadeResponseDTO> criar(@Valid @RequestBody EspecialidadeRequestDTO request) {
        var response = especialidadeService.criar(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<EspecialidadeResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody EspecialidadeRequestDTO request) {
        
        var response = especialidadeService.atualizar(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/inativar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        especialidadeService.inativar(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/ativar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Void> ativar(@PathVariable Long id) {
        especialidadeService.ativar(id);
        return ResponseEntity.ok().build();
    }
}