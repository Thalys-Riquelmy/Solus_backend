package com.solus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.solus.entity.Especialidade;

import java.util.List;
import java.util.Optional;

@Repository
public interface EspecialidadeRepository extends JpaRepository<Especialidade, Long> {
    List<Especialidade> findByEmpresaId(Long empresaId);
    Optional<Especialidade> findByNomeAndEmpresaId(String nome, Long empresaId);
    List<Especialidade> findByEmpresaIdAndAtivoTrue(Long empresaId);
}