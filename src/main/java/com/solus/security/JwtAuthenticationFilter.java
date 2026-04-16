package com.solus.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.solus.entity.Usuario;
import com.solus.repository.UsuarioRepository;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String path = request.getRequestURI();
        
        if (path.equals("/api/auth/login")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String token = extrairToken(request);
        
        if (token != null) {
            String email = jwtTokenProvider.validarToken(token);
            
            if (email != null) {
                Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
                
                if (usuarioOpt.isPresent()) {
                    Usuario usuario = usuarioOpt.get();
                    
                    if (usuario.getAtivo()) {
                        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + usuario.getTipo().name());
                        
                        UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(
                                usuario.getEmail(),  
                                null,                
                                Collections.singletonList(authority) 
                            );
                        
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
        }
        
        filterChain.doFilter(request, response);
    }

    private String extrairToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}