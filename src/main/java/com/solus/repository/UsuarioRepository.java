package com.solus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.solus.entity.Usuario;
import com.solus.enums.TipoProfissional;
import com.solus.enums.TipoUsuario;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findByEmpresaId(Long empresaId);
    List<Usuario> findByTipo(TipoUsuario tipo);
    Optional<Usuario> findByRegistroProfissional(String registroProfissional);
    List<Usuario> findByEmpresaIdAndTipo(Long empresaId, TipoUsuario tipo);
    List<Usuario> findByTipoProfissional(TipoProfissional tipoProfissional);
}