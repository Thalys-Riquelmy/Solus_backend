package com.solus.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.solus.dto.ConvenioRequestDTO;
import com.solus.dto.ConvenioResponseDTO;
import com.solus.dto.ConvenioResumidoDTO;
import com.solus.service.ConvenioService;

import java.util.List;

@RestController
@RequestMapping("/api/convenios")
@CrossOrigin(origins = "*")
public class ConvenioController {

    @Autowired
    private ConvenioService convenioService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'RECEPCAO', 'PROFISSIONAL')")
    public ResponseEntity<List<ConvenioResumidoDTO>> listarTodos(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Boolean ativo) {
        
        var convenios = convenioService.listarTodos(nome, ativo);
        return ResponseEntity.ok(convenios);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'RECEPCAO', 'PROFISSIONAL')")
    public ResponseEntity<ConvenioResponseDTO> buscarPorId(@PathVariable Long id) {
        var convenio = convenioService.buscarPorId(id);
        return ResponseEntity.ok(convenio);
    }

    @GetMapping("/nome/{nome}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'RECEPCAO', 'PROFISSIONAL')")
    public ResponseEntity<ConvenioResponseDTO> buscarPorNome(@PathVariable String nome) {
        var convenio = convenioService.buscarPorNome(nome);
        return ResponseEntity.ok(convenio);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<ConvenioResponseDTO> criar(@Valid @RequestBody ConvenioRequestDTO request) {
        var response = convenioService.criar(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<ConvenioResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ConvenioRequestDTO request) {
        
        var response = convenioService.atualizar(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/inativar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        convenioService.inativar(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/ativar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Void> ativar(@PathVariable Long id) {
        convenioService.ativar(id);
        return ResponseEntity.ok().build();
    }
}