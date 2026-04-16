package com.solus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.solus.entity.Paciente;

import java.util.List;
import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    Optional<Paciente> findByCpf(String cpf);
    List<Paciente> findByEmpresaId(Long empresaId);
    List<Paciente> findByNomeContainingIgnoreCaseAndEmpresaId(String nome, Long empresaId);
}