package com.solus.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.solus.entity.Usuario;
import com.solus.repository.UsuarioRepository;

@Component
public class UsuarioAutenticado {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario get() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com email: " + email));
    }
    
    public Long getId() {
        return get().getId();
    }
    
    public String getEmail() {
        return get().getEmail();
    }
    
    public String getNome() {
        return get().getNome();
    }
    
    public Long getEmpresaId() {
        return get().getEmpresa().getId();
    }
}