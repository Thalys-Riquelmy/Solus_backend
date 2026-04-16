package com.solus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.solus.dto.EspecialidadeRequestDTO;
import com.solus.dto.EspecialidadeResponseDTO;
import com.solus.dto.EspecialidadeResumidoDTO;
import com.solus.entity.Especialidade;
import com.solus.repository.EspecialidadeRepository;
import com.solus.security.UsuarioAutenticado;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EspecialidadeService {

    @Autowired
    private EspecialidadeRepository especialidadeRepository;

    @Autowired
    private UsuarioAutenticado usuarioAutenticado;

    public List<EspecialidadeResumidoDTO> listarTodas(String nome, Boolean ativo) {
        var usuarioLogado = usuarioAutenticado.get();
        var empresaId = usuarioLogado.getEmpresa().getId();
        
        List<Especialidade> especialidades = especialidadeRepository.findByEmpresaId(empresaId);
        
        if (nome != null && !nome.isEmpty()) {
            especialidades = especialidades.stream()
                .filter(e -> e.getNome().toLowerCase().contains(nome.toLowerCase()))
                .collect(Collectors.toList());
        }
        
        if (ativo != null) {
            especialidades = especialidades.stream()
                .filter(e -> e.getAtivo().equals(ativo))
                .collect(Collectors.toList());
        }
        
        return especialidades.stream()
            .map(EspecialidadeResumidoDTO::fromEntity)
            .collect(Collectors.toList());
    }

    public EspecialidadeResponseDTO buscarPorId(Long id) {
        var usuarioLogado = usuarioAutenticado.get();
        var especialidade = buscarEspecialidadePorIdEValidarEmpresa(id, usuarioLogado.getEmpresa().getId());
        return EspecialidadeResponseDTO.fromEntity(especialidade);
    }

    public EspecialidadeResponseDTO buscarPorNome(String nome) {
        var usuarioLogado = usuarioAutenticado.get();
        var empresaId = usuarioLogado.getEmpresa().getId();
        
        var especialidade = especialidadeRepository.findByNomeAndEmpresaId(nome, empresaId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Especialidade não encontrada"));
        
        return EspecialidadeResponseDTO.fromEntity(especialidade);
    }

    public EspecialidadeResponseDTO criar(EspecialidadeRequestDTO request) {
        var usuarioLogado = usuarioAutenticado.get();
        var empresaId = usuarioLogado.getEmpresa().getId();
        
        // Verificar se já existe especialidade com mesmo nome na empresa
        especialidadeRepository.findByNomeAndEmpresaId(request.getNome(), empresaId)
            .ifPresent(e -> {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe uma especialidade com este nome");
            });
        
        var especialidade = new Especialidade();
        especialidade.setNome(request.getNome());
        especialidade.setDescricao(request.getDescricao());
        especialidade.setAtivo(request.getAtivo() != null ? request.getAtivo() : true);
        especialidade.setEmpresa(usuarioLogado.getEmpresa());
        
        var especialidadeSalva = especialidadeRepository.save(especialidade);
        return EspecialidadeResponseDTO.fromEntity(especialidadeSalva);
    }

    public EspecialidadeResponseDTO atualizar(Long id, EspecialidadeRequestDTO request) {
        var usuarioLogado = usuarioAutenticado.get();
        var empresaId = usuarioLogado.getEmpresa().getId();
        var especialidade = buscarEspecialidadePorIdEValidarEmpresa(id, empresaId);
        
        // Verificar se já existe outra especialidade com mesmo nome
        especialidadeRepository.findByNomeAndEmpresaId(request.getNome(), empresaId)
            .ifPresent(e -> {
                if (!e.getId().equals(id)) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe uma especialidade com este nome");
                }
            });
        
        especialidade.setNome(request.getNome());
        especialidade.setDescricao(request.getDescricao());
        especialidade.setAtivo(request.getAtivo() != null ? request.getAtivo() : especialidade.getAtivo());
        
        var especialidadeAtualizada = especialidadeRepository.save(especialidade);
        return EspecialidadeResponseDTO.fromEntity(especialidadeAtualizada);
    }

    public void inativar(Long id) {
        var usuarioLogado = usuarioAutenticado.get();
        var especialidade = buscarEspecialidadePorIdEValidarEmpresa(id, usuarioLogado.getEmpresa().getId());
        especialidade.setAtivo(false);
        especialidadeRepository.save(especialidade);
    }

    public void ativar(Long id) {
        var usuarioLogado = usuarioAutenticado.get();
        var especialidade = buscarEspecialidadePorIdEValidarEmpresa(id, usuarioLogado.getEmpresa().getId());
        especialidade.setAtivo(true);
        especialidadeRepository.save(especialidade);
    }

    private Especialidade buscarEspecialidadePorIdEValidarEmpresa(Long id, Long empresaId) {
        var especialidade = especialidadeRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Especialidade não encontrada"));
        
        if (!especialidade.getEmpresa().getId().equals(empresaId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Especialidade não pertence à sua empresa");
        }
        
        return especialidade;
    }
}