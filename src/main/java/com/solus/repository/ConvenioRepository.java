package com.solus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.solus.entity.Convenio;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConvenioRepository extends JpaRepository<Convenio, Long> {
    List<Convenio> findByEmpresaId(Long empresaId);
    Optional<Convenio> findByNomeAndEmpresaId(String nome, Long empresaId);
    List<Convenio> findByEmpresaIdAndAtivoTrue(Long empresaId);
}