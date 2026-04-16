package com.solus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.solus.dto.LoginRequestDTO;
import com.solus.dto.LoginResponseDTO;
import com.solus.entity.Usuario;
import com.solus.repository.UsuarioRepository;
import com.solus.security.JwtTokenProvider;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        
        Usuario usuario = usuarioRepository.findByEmail(loginRequest.getEmail())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "Email ou senha inválidos"
            ));
        
        if (!passwordEncoder.matches(loginRequest.getSenha(), usuario.getSenhaHash())) {
            throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "Email ou senha inválidos"
            );
        }
                
        if (!usuario.getAtivo()) {
            throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "Usuário inativo. Contate o administrador."
            );
        }
        
        String token = jwtTokenProvider.gerarToken(usuario);
        
        usuario.setUltimoAcesso(java.time.LocalDateTime.now());
        usuarioRepository.save(usuario);
        
        return new LoginResponseDTO(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getTipo(),
            token,
            usuario.getEmpresa().getId()
        );
    }
}