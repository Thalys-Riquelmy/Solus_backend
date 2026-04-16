package com.solus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.solus.dto.AgendaRequestDTO;
import com.solus.dto.AgendaResponseDTO;
import com.solus.dto.AgendaResumidoDTO;
import com.solus.entity.Agenda;
import com.solus.entity.Usuario;
import com.solus.enums.TipoUsuario;
import com.solus.repository.AgendaRepository;
import com.solus.repository.UsuarioRepository;
import com.solus.security.UsuarioAutenticado;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AgendaService {

    @Autowired
    private AgendaRepository agendaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioAutenticado usuarioAutenticado;

    public List<AgendaResumidoDTO> listarTodas(Long profissionalId, DayOfWeek diaSemana, Boolean ativo) {
        var usuarioLogado = usuarioAutenticado.get();
        var empresaId = usuarioLogado.getEmpresa().getId();
        
        List<Agenda> agendas;
        
        if (profissionalId != null) {
            var profissional = usuarioRepository.findById(profissionalId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profissional não encontrado"));
            
            if (!profissional.getEmpresa().getId().equals(empresaId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Profissional não pertence à sua empresa");
            }
            
            if (diaSemana != null) {
                agendas = agendaRepository.findByProfissionalIdAndDiaSemana(profissionalId, diaSemana);
            } else {
                agendas = agendaRepository.findByProfissionalId(profissionalId);
            }
        } else {
            agendas = agendaRepository.findByEmpresaId(empresaId);
        }
        
        if (ativo != null) {
            agendas = agendas.stream()
                .filter(a -> a.getAtivo().equals(ativo))
                .collect(Collectors.toList());
        }
        
        return agendas.stream()
            .map(AgendaResumidoDTO::fromEntity)
            .collect(Collectors.toList());
    }

    public AgendaResponseDTO buscarPorId(Long id) {
        var usuarioLogado = usuarioAutenticado.get();
        var agenda = buscarAgendaPorIdEValidarEmpresa(id, usuarioLogado.getEmpresa().getId());
        return AgendaResponseDTO.fromEntity(agenda);
    }

    public List<AgendaResumidoDTO> listarPorProfissional(Long profissionalId) {
        var usuarioLogado = usuarioAutenticado.get();
        var empresaId = usuarioLogado.getEmpresa().getId();
        
        var profissional = usuarioRepository.findById(profissionalId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profissional não encontrado"));
        
        if (!profissional.getEmpresa().getId().equals(empresaId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Profissional não pertence à sua empresa");
        }
        
        return agendaRepository.findByProfissionalId(profissionalId)
            .stream()
            .map(AgendaResumidoDTO::fromEntity)
            .collect(Collectors.toList());
    }

    public AgendaResponseDTO criar(AgendaRequestDTO request) {
        var usuarioLogado = usuarioAutenticado.get();
        var empresaId = usuarioLogado.getEmpresa().getId();
        
        // Validar profissional
        var profissional = usuarioRepository.findById(request.getProfissionalId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profissional não encontrado"));
        
        if (profissional.getTipo() != TipoUsuario.PROFISSIONAL) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário não é um profissional");
        }
        
        if (!profissional.getEmpresa().getId().equals(empresaId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Profissional não pertence à sua empresa");
        }
        
        // Validar horários
        if (request.getHoraFim().isBefore(request.getHoraInicio()) || 
            request.getHoraFim().equals(request.getHoraInicio())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hora de fim deve ser maior que hora de início");
        }
        
        if (request.getIntervaloMinutos() < 15) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Intervalo mínimo é de 15 minutos");
        }
        
        // Verificar se existe agenda com horário conflitante
        if (agendaRepository.existsHorarioConflitante(
                request.getProfissionalId(), 
                request.getDiaSemana(),
                request.getHoraInicio(),
                request.getHoraFim())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, 
                "Já existe uma agenda com horário conflitante para este profissional neste dia");
        }
        
        var agenda = new Agenda();
        agenda.setProfissional(profissional);
        agenda.setDiaSemana(request.getDiaSemana());
        agenda.setHoraInicio(request.getHoraInicio());
        agenda.setHoraFim(request.getHoraFim());
        agenda.setIntervaloMinutos(request.getIntervaloMinutos());
        agenda.setAtivo(request.getAtivo() != null ? request.getAtivo() : true);
        agenda.setEmpresa(usuarioLogado.getEmpresa());
        
        var agendaSalva = agendaRepository.save(agenda);
        return AgendaResponseDTO.fromEntity(agendaSalva);
    }

    public AgendaResponseDTO atualizar(Long id, AgendaRequestDTO request) {
        var usuarioLogado = usuarioAutenticado.get();
        var empresaId = usuarioLogado.getEmpresa().getId();
        var agenda = buscarAgendaPorIdEValidarEmpresa(id, empresaId);
        
        // Validar profissional se foi alterado
        Usuario profissional = agenda.getProfissional();
        if (!agenda.getProfissional().getId().equals(request.getProfissionalId())) {
            profissional = usuarioRepository.findById(request.getProfissionalId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profissional não encontrado"));
            
            if (profissional.getTipo() != TipoUsuario.PROFISSIONAL) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário não é um profissional");
            }
            
            if (!profissional.getEmpresa().getId().equals(empresaId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Profissional não pertence à sua empresa");
            }
        }
        
        // Validar horários
        if (request.getHoraFim().isBefore(request.getHoraInicio()) || 
            request.getHoraFim().equals(request.getHoraInicio())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hora de fim deve ser maior que hora de início");
        }
        
        if (request.getIntervaloMinutos() < 15) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Intervalo mínimo é de 15 minutos");
        }
        
        // Verificar se existe outra agenda com horário conflitante (excluindo a própria)
        List<Agenda> agendasConflitantes = agendaRepository
            .findByProfissionalIdAndDiaSemana(request.getProfissionalId(), request.getDiaSemana())
            .stream()
            .filter(a -> !a.getId().equals(id)) // Exclui a própria agenda
            .filter(a -> a.getAtivo())
            .filter(a -> horariosConflitam(
                a.getHoraInicio(), a.getHoraFim(), 
                request.getHoraInicio(), request.getHoraFim()))
            .collect(Collectors.toList());
        
        if (!agendasConflitantes.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, 
                "Já existe uma agenda com horário conflitante para este profissional neste dia");
        }
        
        agenda.setProfissional(profissional);
        agenda.setDiaSemana(request.getDiaSemana());
        agenda.setHoraInicio(request.getHoraInicio());
        agenda.setHoraFim(request.getHoraFim());
        agenda.setIntervaloMinutos(request.getIntervaloMinutos());
        agenda.setAtivo(request.getAtivo() != null ? request.getAtivo() : agenda.getAtivo());
        
        var agendaAtualizada = agendaRepository.save(agenda);
        return AgendaResponseDTO.fromEntity(agendaAtualizada);
    }

    public void inativar(Long id) {
        var usuarioLogado = usuarioAutenticado.get();
        var agenda = buscarAgendaPorIdEValidarEmpresa(id, usuarioLogado.getEmpresa().getId());
        agenda.setAtivo(false);
        agendaRepository.save(agenda);
    }

    public void ativar(Long id) {
        var usuarioLogado = usuarioAutenticado.get();
        var agenda = buscarAgendaPorIdEValidarEmpresa(id, usuarioLogado.getEmpresa().getId());
        agenda.setAtivo(true);
        agendaRepository.save(agenda);
    }

    public void deletar(Long id) {
        var usuarioLogado = usuarioAutenticado.get();
        var agenda = buscarAgendaPorIdEValidarEmpresa(id, usuarioLogado.getEmpresa().getId());
        agendaRepository.delete(agenda);
    }

    private Agenda buscarAgendaPorIdEValidarEmpresa(Long id, Long empresaId) {
        var agenda = agendaRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Agenda não encontrada"));
        
        if (!agenda.getEmpresa().getId().equals(empresaId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Agenda não pertence à sua empresa");
        }
        
        return agenda;
    }

    private boolean horariosConflitam(LocalTime inicio1, LocalTime fim1, LocalTime inicio2, LocalTime fim2) {
        return inicio1.isBefore(fim2) && fim1.isAfter(inicio2);
    }
}