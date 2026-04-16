package com.solus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.solus.dto.PacienteRequestDTO;
import com.solus.dto.PacienteResponseDTO;
import com.solus.entity.Convenio;
import com.solus.entity.Paciente;
import com.solus.repository.ConvenioRepository;
import com.solus.repository.PacienteRepository;
import com.solus.security.UsuarioAutenticado;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private ConvenioRepository convenioRepository;

    @Autowired
    private UsuarioAutenticado usuarioAutenticado;

    public List<PacienteResponseDTO> listarTodos(String nome, String cpf, Long convenioId, Boolean ativo) {
        var usuarioLogado = usuarioAutenticado.get();
        var empresaId = usuarioLogado.getEmpresa().getId();
        
        List<Paciente> pacientes = pacienteRepository.findByEmpresaId(empresaId);
        
        if (nome != null && !nome.isEmpty()) {
            pacientes = pacientes.stream()
                .filter(p -> p.getNome().toLowerCase().contains(nome.toLowerCase()))
                .collect(Collectors.toList());
        }
        
        if (cpf != null && !cpf.isEmpty()) {
            pacientes = pacientes.stream()
                .filter(p -> p.getCpf() != null && p.getCpf().contains(cpf))
                .collect(Collectors.toList());
        }
        
        if (convenioId != null) {
            pacientes = pacientes.stream()
                .filter(p -> p.getConvenio() != null && 
                             p.getConvenio().getId().equals(convenioId))
                .collect(Collectors.toList());
        }
        
        if (ativo != null) {
            pacientes = pacientes.stream()
                .filter(p -> p.getAtivo().equals(ativo))
                .collect(Collectors.toList());
        }
        
        return pacientes.stream()
            .map(PacienteResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    public PacienteResponseDTO buscarPorId(Long id) {
        var usuarioLogado = usuarioAutenticado.get();
        var paciente = buscarPacientePorIdEValidarEmpresa(id, usuarioLogado.getEmpresa().getId());
        return PacienteResponseDTO.fromEntity(paciente);
    }

    public PacienteResponseDTO buscarPorCpf(String cpf) {
        var usuarioLogado = usuarioAutenticado.get();
        
        var paciente = pacienteRepository.findByCpf(cpf)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paciente não encontrado"));
        
        if (!paciente.getEmpresa().getId().equals(usuarioLogado.getEmpresa().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Paciente não pertence à sua empresa");
        }
        
        return PacienteResponseDTO.fromEntity(paciente);
    }

    public List<PacienteResponseDTO> buscarPorNome(String nome) {
        var usuarioLogado = usuarioAutenticado.get();
        
        return pacienteRepository.findByNomeContainingIgnoreCaseAndEmpresaId(nome, usuarioLogado.getEmpresa().getId())
            .stream()
            .map(PacienteResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    public PacienteResponseDTO criar(PacienteRequestDTO request) {
        var usuarioLogado = usuarioAutenticado.get();
        
        if (request.getCpf() != null && !request.getCpf().isEmpty()) {
            pacienteRepository.findByCpf(request.getCpf()).ifPresent(p -> {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado");
            });
        }
        
        Convenio convenio = null;
        if (request.getConvenioId() != null) {
            convenio = convenioRepository.findById(request.getConvenioId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Convênio não encontrado"));
            
            if (!convenio.getEmpresa().getId().equals(usuarioLogado.getEmpresa().getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Convênio não pertence à sua empresa");
            }
        }
        
        var paciente = new Paciente();
        paciente.setNome(request.getNome());
        paciente.setCpf(request.getCpf());
        paciente.setRg(request.getRg());
        paciente.setDataNascimento(request.getDataNascimento());
        paciente.setSexo(request.getSexo());
        paciente.setTelefone(request.getTelefone());
        paciente.setEmail(request.getEmail());
        paciente.setEndereco(request.getEndereco());
        paciente.setCidade(request.getCidade());
        paciente.setEstado(request.getEstado());
        paciente.setCep(request.getCep());
        paciente.setConvenio(convenio);
        paciente.setNumeroCarteirinha(request.getNumeroCarteirinha());
        paciente.setObservacoes(request.getObservacoes());
        paciente.setEmpresa(usuarioLogado.getEmpresa());
        paciente.setAtivo(request.getAtivo() != null ? request.getAtivo() : true);
        paciente.setDataCadastro(LocalDateTime.now());
        
        var pacienteSalvo = pacienteRepository.save(paciente);
        return PacienteResponseDTO.fromEntity(pacienteSalvo);
    }

    public PacienteResponseDTO atualizar(Long id, PacienteRequestDTO request) {
        var usuarioLogado = usuarioAutenticado.get();
        var paciente = buscarPacientePorIdEValidarEmpresa(id, usuarioLogado.getEmpresa().getId());
        
        // Verificar se CPF já existe em outro paciente
        if (request.getCpf() != null && !request.getCpf().isEmpty()) {
            pacienteRepository.findByCpf(request.getCpf()).ifPresent(p -> {
                if (!p.getId().equals(id)) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado");
                }
            });
        }
        
        Convenio convenio = null;
        if (request.getConvenioId() != null) {
            convenio = convenioRepository.findById(request.getConvenioId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Convênio não encontrado"));
            
            if (!convenio.getEmpresa().getId().equals(usuarioLogado.getEmpresa().getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Convênio não pertence à sua empresa");
            }
        }
        
        paciente.setNome(request.getNome());
        paciente.setCpf(request.getCpf());
        paciente.setRg(request.getRg());
        paciente.setDataNascimento(request.getDataNascimento());
        paciente.setSexo(request.getSexo());
        paciente.setTelefone(request.getTelefone());
        paciente.setEmail(request.getEmail());
        paciente.setEndereco(request.getEndereco());
        paciente.setCidade(request.getCidade());
        paciente.setEstado(request.getEstado());
        paciente.setCep(request.getCep());
        paciente.setConvenio(convenio);
        paciente.setNumeroCarteirinha(request.getNumeroCarteirinha());
        paciente.setObservacoes(request.getObservacoes());
        paciente.setAtivo(request.getAtivo() != null ? request.getAtivo() : paciente.getAtivo());
        
        var pacienteAtualizado = pacienteRepository.save(paciente);
        return PacienteResponseDTO.fromEntity(pacienteAtualizado);
    }

    public void inativar(Long id) {
        var usuarioLogado = usuarioAutenticado.get();
        var paciente = buscarPacientePorIdEValidarEmpresa(id, usuarioLogado.getEmpresa().getId());
        paciente.setAtivo(false);
        pacienteRepository.save(paciente);
    }

    public void ativar(Long id) {
        var usuarioLogado = usuarioAutenticado.get();
        var paciente = buscarPacientePorIdEValidarEmpresa(id, usuarioLogado.getEmpresa().getId());
        paciente.setAtivo(true);
        pacienteRepository.save(paciente);
    }

    private Paciente buscarPacientePorIdEValidarEmpresa(Long id, Long empresaId) {
        var paciente = pacienteRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paciente não encontrado"));
        
        if (!paciente.getEmpresa().getId().equals(empresaId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Paciente não pertence à sua empresa");
        }
        
        return paciente;
    }
}