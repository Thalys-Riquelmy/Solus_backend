package com.solus.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.solus.dto.*;
import com.solus.enums.TipoProfissional;
import com.solus.enums.TipoUsuario;
import com.solus.service.UsuarioService;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;
    
    // ENDPOINTS GERAIS DE USUÁRIOS

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) TipoUsuario tipo,
            @RequestParam(required = false) Boolean ativo) {
        
        List<UsuarioResponseDTO> usuarios = usuarioService.listarTodos(nome, email, tipo, ativo);
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'PROFISSIONAL', 'RECEPCAO') or #id == authentication.principal.id")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        UsuarioResponseDTO usuario = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(usuario);
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<UsuarioResponseDTO> buscarPorEmail(@PathVariable String email) {
        UsuarioResponseDTO usuario = usuarioService.buscarPorEmail(email);
        return ResponseEntity.ok(usuario);
    }

    @GetMapping("/tipo/{tipo}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<List<UsuarioResponseDTO>> listarPorTipo(@PathVariable TipoUsuario tipo) {
        List<UsuarioResponseDTO> usuarios = usuarioService.listarPorTipo(tipo);
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/perfil")
    public ResponseEntity<UsuarioPerfilDTO> getPerfil() {
        UsuarioPerfilDTO perfil = usuarioService.getPerfil();
        return ResponseEntity.ok(perfil);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioSenhaResponseDTO> criar(@Valid @RequestBody UsuarioRequestDTO request) {
        UsuarioSenhaResponseDTO response = usuarioService.criar(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRequestDTO request) {
        
        UsuarioResponseDTO response = usuarioService.atualizar(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/ativar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> ativar(@PathVariable Long id) {
        usuarioService.ativar(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/inativar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        usuarioService.inativar(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/redefinir-senha")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<UsuarioSenhaResponseDTO> redefinirSenha(@PathVariable Long id) {
        UsuarioSenhaResponseDTO response = usuarioService.redefinirSenha(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/alterar-senha")
    public ResponseEntity<Void> alterarPropriaSenha(@Valid @RequestBody AlterarSenhaRequestDTO request) {
        usuarioService.alterarPropriaSenha(request);
        return ResponseEntity.ok().build();
    }

    // ENDPOINTS ESPECÍFICOS PARA PROFISSIONAIS

    @GetMapping("/profissionais")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'RECEPCAO', 'PROFISSIONAL')")
    public ResponseEntity<List<ProfissionalResumidoDTO>> listarProfissionais(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) TipoProfissional tipoProfissional,
            @RequestParam(required = false) Long especialidadeId,
            @RequestParam(required = false) Boolean ativo) {
        
        List<ProfissionalResumidoDTO> profissionais = usuarioService.listarProfissionais(
            nome, tipoProfissional, especialidadeId, ativo);
        return ResponseEntity.ok(profissionais);
    }

    @GetMapping("/profissionais/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'RECEPCAO', 'PROFISSIONAL')")
    public ResponseEntity<ProfissionalResponseDTO> buscarProfissionalPorId(@PathVariable Long id) {
        ProfissionalResponseDTO profissional = usuarioService.buscarProfissionalPorId(id);
        return ResponseEntity.ok(profissional);
    }

    @GetMapping("/profissionais/tipo/{tipo}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'RECEPCAO')")
    public ResponseEntity<List<ProfissionalResumidoDTO>> listarProfissionaisPorTipo(@PathVariable TipoProfissional tipo) {
        List<ProfissionalResumidoDTO> profissionais = usuarioService.listarProfissionaisPorTipo(tipo);
        return ResponseEntity.ok(profissionais);
    }

    @PostMapping("/profissionais")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioSenhaResponseDTO> criarProfissional(@Valid @RequestBody ProfissionalRequestDTO request) {
        UsuarioSenhaResponseDTO response = usuarioService.criarProfissional(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/profissionais/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProfissionalResponseDTO> atualizarProfissional(
            @PathVariable Long id,
            @Valid @RequestBody ProfissionalRequestDTO request) {
        
        ProfissionalResponseDTO response = usuarioService.atualizarProfissional(id, request);
        return ResponseEntity.ok(response);
    }
}