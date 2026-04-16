package com.solus.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.solus.entity.Usuario;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    public String gerarToken(Usuario usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer("zeller-pdv")
                    .withSubject(usuario.getEmail())
                    .withClaim("id", usuario.getId())
                    .withClaim("nome", usuario.getNome())
                    .withClaim("tipo", usuario.getTipo().toString())
                    .withClaim("empresaId", usuario.getEmpresa().getId())
                    .withExpiresAt(gerarDataExpiracao())
                    .sign(algorithm);
            
            return token;
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }

    public String validarToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String subject = JWT.require(algorithm)
                    .withIssuer("zeller-pdv")
                    .build()
                    .verify(token)
                    .getSubject();
            
            return subject;
        } catch (JWTVerificationException exception) {
            return null;
        }
    }

    private Instant gerarDataExpiracao() {
        return LocalDateTime.now()
                .plusSeconds(expiration)
                .toInstant(ZoneOffset.of("-03:00"));
    }
}