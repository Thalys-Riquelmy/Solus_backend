package com.solus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.solus.dto.ConvenioRequestDTO;
import com.solus.dto.ConvenioResponseDTO;
import com.solus.dto.ConvenioResumidoDTO;
import com.solus.entity.Convenio;
import com.solus.repository.ConvenioRepository;
import com.solus.security.UsuarioAutenticado;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConvenioService {

    @Autowired
    private ConvenioRepository convenioRepository;

    @Autowired
    private UsuarioAutenticado usuarioAutenticado;

    public List<ConvenioResumidoDTO> listarTodos(String nome, Boolean ativo) {
        var usuarioLogado = usuarioAutenticado.get();
        var empresaId = usuarioLogado.getEmpresa().getId();
        
        List<Convenio> convenios = convenioRepository.findByEmpresaId(empresaId);
        
        if (nome != null && !nome.isEmpty()) {
            convenios = convenios.stream()
                .filter(c -> c.getNome().toLowerCase().contains(nome.toLowerCase()))
                .collect(Collectors.toList());
        }
        
        if (ativo != null) {
            convenios = convenios.stream()
                .filter(c -> c.getAtivo().equals(ativo))
                .collect(Collectors.toList());
        }
        
        return convenios.stream()
            .map(ConvenioResumidoDTO::fromEntity)
            .collect(Collectors.toList());
    }

    public ConvenioResponseDTO buscarPorId(Long id) {
        var usuarioLogado = usuarioAutenticado.get();
        var convenio = buscarConvenioPorIdEValidarEmpresa(id, usuarioLogado.getEmpresa().getId());
        return ConvenioResponseDTO.fromEntity(convenio);
    }

    public ConvenioResponseDTO buscarPorNome(String nome) {
        var usuarioLogado = usuarioAutenticado.get();
        var empresaId = usuarioLogado.getEmpresa().getId();
        
        var convenio = convenioRepository.findByNomeAndEmpresaId(nome, empresaId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Convênio não encontrado"));
        
        return ConvenioResponseDTO.fromEntity(convenio);
    }

    public ConvenioResponseDTO criar(ConvenioRequestDTO request) {
        var usuarioLogado = usuarioAutenticado.get();
        var empresaId = usuarioLogado.getEmpresa().getId();
        
        convenioRepository.findByNomeAndEmpresaId(request.getNome(), empresaId)
            .ifPresent(c -> {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um convênio com este nome");
            });
        
        var convenio = new Convenio();
        convenio.setNome(request.getNome());
        convenio.setCnpj(request.getCnpj());
        convenio.setTelefone(request.getTelefone());
        convenio.setEmail(request.getEmail());
        convenio.setAtivo(request.getAtivo() != null ? request.getAtivo() : true);
        convenio.setEmpresa(usuarioLogado.getEmpresa());
        
        var convenioSalvo = convenioRepository.save(convenio);
        return ConvenioResponseDTO.fromEntity(convenioSalvo);
    }

    public ConvenioResponseDTO atualizar(Long id, ConvenioRequestDTO request) {
        var usuarioLogado = usuarioAutenticado.get();
        var empresaId = usuarioLogado.getEmpresa().getId();
        var convenio = buscarConvenioPorIdEValidarEmpresa(id, empresaId);
        
        convenioRepository.findByNomeAndEmpresaId(request.getNome(), empresaId)
            .ifPresent(c -> {
                if (!c.getId().equals(id)) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um convênio com este nome");
                }
            });
        
        convenio.setNome(request.getNome());
        convenio.setCnpj(request.getCnpj());
        convenio.setTelefone(request.getTelefone());
        convenio.setEmail(request.getEmail());
        convenio.setAtivo(request.getAtivo() != null ? request.getAtivo() : convenio.getAtivo());
        
        var convenioAtualizado = convenioRepository.save(convenio);
        return ConvenioResponseDTO.fromEntity(convenioAtualizado);
    }

    public void inativar(Long id) {
        var usuarioLogado = usuarioAutenticado.get();
        var convenio = buscarConvenioPorIdEValidarEmpresa(id, usuarioLogado.getEmpresa().getId());
        convenio.setAtivo(false);
        convenioRepository.save(convenio);
    }

    public void ativar(Long id) {
        var usuarioLogado = usuarioAutenticado.get();
        var convenio = buscarConvenioPorIdEValidarEmpresa(id, usuarioLogado.getEmpresa().getId());
        convenio.setAtivo(true);
        convenioRepository.save(convenio);
    }

    private Convenio buscarConvenioPorIdEValidarEmpresa(Long id, Long empresaId) {
        var convenio = convenioRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Convênio não encontrado"));
        
        if (!convenio.getEmpresa().getId().equals(empresaId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Convênio não pertence à sua empresa");
        }
        
        return convenio;
    }
}