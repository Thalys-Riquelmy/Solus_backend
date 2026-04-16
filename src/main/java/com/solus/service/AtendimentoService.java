package com.solus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.solus.dto.AtendimentoRequestDTO;
import com.solus.dto.AtendimentoResponseDTO;
import com.solus.dto.AtendimentoResumidoDTO;
import com.solus.entity.Atendimento;
import com.solus.enums.StatusAgendamento;
import com.solus.enums.TipoUsuario;
import com.solus.repository.AgendamentoRepository;
import com.solus.repository.AtendimentoRepository;
import com.solus.security.UsuarioAutenticado;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AtendimentoService {

    @Autowired
    private AtendimentoRepository atendimentoRepository;

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private UsuarioAutenticado usuarioAutenticado;

    public List<AtendimentoResumidoDTO> listarTodos(
            Long pacienteId,
            Long profissionalId,
            LocalDateTime dataInicio,
            LocalDateTime dataFim) {
        
        var usuarioLogado = usuarioAutenticado.get();
        var empresaId = usuarioLogado.getEmpresa().getId();
        
        List<Atendimento> atendimentos;
        
        if (pacienteId != null) {
            atendimentos = atendimentoRepository.findByPacienteId(pacienteId)
                .stream()
                .filter(a -> a.getEmpresa().getId().equals(empresaId))
                .collect(Collectors.toList());
        } else if (profissionalId != null && dataInicio != null && dataFim != null) {
            atendimentos = atendimentoRepository.findByProfissionalAndPeriodo(profissionalId, dataInicio, dataFim)
                .stream()
                .filter(a -> a.getEmpresa().getId().equals(empresaId))
                .collect(Collectors.toList());
        } else if (profissionalId != null) {
            atendimentos = atendimentoRepository.findByProfissionalId(profissionalId)
                .stream()
                .filter(a -> a.getEmpresa().getId().equals(empresaId))
                .collect(Collectors.toList());
        } else {
            atendimentos = atendimentoRepository.findByEmpresaId(empresaId);
        }
        
        return atendimentos.stream()
            .map(AtendimentoResumidoDTO::fromEntity)
            .collect(Collectors.toList());
    }

    public AtendimentoResponseDTO buscarPorId(Long id) {
        var usuarioLogado = usuarioAutenticado.get();
        var atendimento = buscarAtendimentoPorIdEValidarEmpresa(id, usuarioLogado.getEmpresa().getId());
        return AtendimentoResponseDTO.fromEntity(atendimento);
    }

    public AtendimentoResponseDTO buscarPorAgendamentoId(Long agendamentoId) {
        var usuarioLogado = usuarioAutenticado.get();
        var empresaId = usuarioLogado.getEmpresa().getId();
        
        var atendimento = atendimentoRepository.findByAgendamentoId(agendamentoId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                "Atendimento não encontrado para este agendamento"));
        
        if (!atendimento.getEmpresa().getId().equals(empresaId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Atendimento não pertence à sua empresa");
        }
        
        return AtendimentoResponseDTO.fromEntity(atendimento);
    }

    public List<AtendimentoResumidoDTO> listarPorPaciente(Long pacienteId) {
        var usuarioLogado = usuarioAutenticado.get();
        var empresaId = usuarioLogado.getEmpresa().getId();
        
        return atendimentoRepository.findByPacienteId(pacienteId)
            .stream()
            .filter(a -> a.getEmpresa().getId().equals(empresaId))
            .map(AtendimentoResumidoDTO::fromEntity)
            .collect(Collectors.toList());
    }

    public AtendimentoResponseDTO criar(AtendimentoRequestDTO request) {
        var usuarioLogado = usuarioAutenticado.get();
        var empresaId = usuarioLogado.getEmpresa().getId();
        
        var agendamento = agendamentoRepository.findById(request.getAgendamentoId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Agendamento não encontrado"));
        
        if (!agendamento.getEmpresa().getId().equals(empresaId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Agendamento não pertence à sua empresa");
        }
        
        if (usuarioLogado.getTipo() != TipoUsuario.ADMIN && 
            !usuarioLogado.getId().equals(agendamento.getProfissional().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                "Apenas o profissional responsável pode registrar o atendimento");
        }
        
        // Se o agendamento já foi cancelado ou falta, não permite
        if (agendamento.getStatus() == StatusAgendamento.CANCELADO || 
            agendamento.getStatus() == StatusAgendamento.NAO_COMPARECEU) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Não é possível iniciar atendimento para um agendamento Cancelado ou Não Compareceu");
        }
        
        // Atualiza o status do agendamento para EM_ATENDIMENTO se ele ainda não foi realizado 
        if (agendamento.getStatus() != StatusAgendamento.REALIZADO) {
            agendamento.setStatus(StatusAgendamento.EM_ATENDIMENTO);
            agendamentoRepository.save(agendamento);
        }
        
        if (atendimentoRepository.existsByAgendamentoId(request.getAgendamentoId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, 
                "Já existe um atendimento registrado para este agendamento");
        }
        
        var atendimento = new Atendimento();
        atendimento.setAgendamento(agendamento);
        atendimento.setProfissional(agendamento.getProfissional());
        atendimento.setEmpresa(usuarioLogado.getEmpresa());
        
        atendimento.setDataHoraInicio(request.getDataHoraInicio() != null ? 
            request.getDataHoraInicio() : LocalDateTime.now());
        
        atendimento.setDataHoraFim(request.getDataHoraFim());
        atendimento.setQueixaPrincipal(request.getQueixaPrincipal());
        atendimento.setHistoricoDoenca(request.getHistoricoDoenca());
        atendimento.setExameFisico(request.getExameFisico());
        atendimento.setDiagnostico(request.getDiagnostico());
        atendimento.setPrescricao(request.getPrescricao());
        atendimento.setObservacoes(request.getObservacoes());
        
        var atendimentoSalvo = atendimentoRepository.save(atendimento);
        return AtendimentoResponseDTO.fromEntity(atendimentoSalvo);
    }

    public AtendimentoResponseDTO atualizar(Long id, AtendimentoRequestDTO request) {
        var usuarioLogado = usuarioAutenticado.get();
        var atendimento = buscarAtendimentoPorIdEValidarEmpresa(id, usuarioLogado.getEmpresa().getId());
        
        if (usuarioLogado.getTipo() != TipoUsuario.ADMIN && 
            !usuarioLogado.getId().equals(atendimento.getProfissional().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                "Apenas o profissional responsável pode editar o atendimento");
        }
        
        if (request.getDataHoraInicio() != null) {
            atendimento.setDataHoraInicio(request.getDataHoraInicio());
        }
        
        if (request.getDataHoraFim() != null) {
            atendimento.setDataHoraFim(request.getDataHoraFim());
        }
        
        if (request.getQueixaPrincipal() != null) {
            atendimento.setQueixaPrincipal(request.getQueixaPrincipal());
        }
        
        if (request.getHistoricoDoenca() != null) {
            atendimento.setHistoricoDoenca(request.getHistoricoDoenca());
        }
        
        if (request.getExameFisico() != null) {
            atendimento.setExameFisico(request.getExameFisico());
        }
        
        if (request.getDiagnostico() != null) {
            atendimento.setDiagnostico(request.getDiagnostico());
        }
        
        if (request.getPrescricao() != null) {
            atendimento.setPrescricao(request.getPrescricao());
        }
        
        if (request.getObservacoes() != null) {
            atendimento.setObservacoes(request.getObservacoes());
        }
        
        var atendimentoAtualizado = atendimentoRepository.save(atendimento);
        return AtendimentoResponseDTO.fromEntity(atendimentoAtualizado);
    }

    public void finalizarAtendimento(Long id) {
        var usuarioLogado = usuarioAutenticado.get();
        var atendimento = buscarAtendimentoPorIdEValidarEmpresa(id, usuarioLogado.getEmpresa().getId());
        
        if (usuarioLogado.getTipo() != TipoUsuario.ADMIN && 
            !usuarioLogado.getId().equals(atendimento.getProfissional().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                "Apenas o profissional responsável pode finalizar o atendimento");
        }
        
        atendimento.setDataHoraFim(LocalDateTime.now());
        atendimentoRepository.save(atendimento);
        
        var agendamento = atendimento.getAgendamento();
        if (agendamento != null) {
            agendamento.setStatus(StatusAgendamento.REALIZADO);
            agendamentoRepository.save(agendamento);
        }
    }

    public void deletar(Long id) {
        var usuarioLogado = usuarioAutenticado.get();
        var atendimento = buscarAtendimentoPorIdEValidarEmpresa(id, usuarioLogado.getEmpresa().getId());
        
        if (usuarioLogado.getTipo() != TipoUsuario.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                "Apenas administradores podem excluir atendimentos");
        }
        
        atendimentoRepository.delete(atendimento);
    }

    private Atendimento buscarAtendimentoPorIdEValidarEmpresa(Long id, Long empresaId) {
        var atendimento = atendimentoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Atendimento não encontrado"));
        
        if (!atendimento.getEmpresa().getId().equals(empresaId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Atendimento não pertence à sua empresa");
        }
        
        return atendimento;
    }
}