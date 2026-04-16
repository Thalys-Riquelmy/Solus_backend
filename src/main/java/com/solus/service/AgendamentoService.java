package com.solus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.solus.dto.*;
import com.solus.entity.Agenda;
import com.solus.entity.Agendamento;
import com.solus.enums.StatusAgendamento;
import com.solus.enums.TipoUsuario;
import com.solus.repository.AgendaRepository;
import com.solus.repository.AgendamentoRepository;
import com.solus.repository.PacienteRepository;
import com.solus.repository.UsuarioRepository;
import com.solus.security.UsuarioAutenticado;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AgendamentoService {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AgendaRepository agendaRepository;

    @Autowired
    private UsuarioAutenticado usuarioAutenticado;

    public List<AgendamentoResumidoDTO> listarTodos(
            Long pacienteId, 
            Long profissionalId, 
            StatusAgendamento status,
            LocalDateTime dataInicio,
            LocalDateTime dataFim) {
        
        var usuarioLogado = usuarioAutenticado.get();
        var empresaId = usuarioLogado.getEmpresa().getId();
        
        List<Agendamento> agendamentos;
        
        if (dataInicio != null && dataFim != null) {
            agendamentos = agendamentoRepository.findAgendamentosPorPeriodo(empresaId, dataInicio, dataFim);
        } else if (pacienteId != null) {
            agendamentos = agendamentoRepository.findByPacienteId(pacienteId)
                .stream()
                .filter(a -> a.getEmpresa().getId().equals(empresaId))
                .collect(Collectors.toList());
        } else if (profissionalId != null) {
            agendamentos = agendamentoRepository.findByProfissionalId(profissionalId)
                .stream()
                .filter(a -> a.getEmpresa().getId().equals(empresaId))
                .collect(Collectors.toList());
        } else if (status != null) {
            agendamentos = agendamentoRepository.findByStatus(status)
                .stream()
                .filter(a -> a.getEmpresa().getId().equals(empresaId))
                .collect(Collectors.toList());
        } else {
            agendamentos = agendamentoRepository.findByEmpresaId(empresaId);
        }
        
        return agendamentos.stream()
            .map(AgendamentoResumidoDTO::fromEntity)
            .collect(Collectors.toList());
    }

    public AgendamentoResponseDTO buscarPorId(Long id) {
        var usuarioLogado = usuarioAutenticado.get();
        var agendamento = buscarAgendamentoPorIdEValidarEmpresa(id, usuarioLogado.getEmpresa().getId());
        return AgendamentoResponseDTO.fromEntity(agendamento);
    }

    public List<AgendamentoResumidoDTO> listarPorPaciente(Long pacienteId) {
        var usuarioLogado = usuarioAutenticado.get();
        var empresaId = usuarioLogado.getEmpresa().getId();
        
        var paciente = pacienteRepository.findById(pacienteId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paciente não encontrado"));
        
        if (!paciente.getEmpresa().getId().equals(empresaId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Paciente não pertence à sua empresa");
        }
        
        return agendamentoRepository.findHistoricoPaciente(pacienteId)
            .stream()
            .map(AgendamentoResumidoDTO::fromEntity)
            .collect(Collectors.toList());
    }

    public List<AgendamentoResumidoDTO> listarPorProfissional(Long profissionalId) {
        var usuarioLogado = usuarioAutenticado.get();
        var empresaId = usuarioLogado.getEmpresa().getId();
        
        var profissional = usuarioRepository.findById(profissionalId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profissional não encontrado"));
        
        if (profissional.getTipo() != TipoUsuario.PROFISSIONAL) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário não é um profissional");
        }
        
        if (!profissional.getEmpresa().getId().equals(empresaId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Profissional não pertence à sua empresa");
        }
        
        return agendamentoRepository.findByProfissionalId(profissionalId)
            .stream()
            .map(AgendamentoResumidoDTO::fromEntity)
            .collect(Collectors.toList());
    }

    public HorariosDisponiveisDTO verificarDisponibilidade(
            Long profissionalId, 
            LocalDate data) {
        
        var usuarioLogado = usuarioAutenticado.get();
        var empresaId = usuarioLogado.getEmpresa().getId();
        
        var profissional = usuarioRepository.findById(profissionalId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profissional não encontrado"));
        
        if (!profissional.getEmpresa().getId().equals(empresaId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Profissional não pertence à sua empresa");
        }
        
        DayOfWeek diaSemana = data.getDayOfWeek();
        List<Agenda> agendas = agendaRepository.findByProfissionalIdAndDiaSemana(profissionalId, diaSemana)
            .stream()
            .filter(Agenda::getAtivo)
            .collect(Collectors.toList());
        
        if (agendas.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                "Profissional não possui agenda para este dia da semana");
        }
        
        LocalDateTime inicioDoDia = data.atStartOfDay();
        LocalDateTime fimDoDia = data.plusDays(1).atStartOfDay();
        
        List<Agendamento> agendamentos = agendamentoRepository
            .findAgendamentosNoPeriodo(profissionalId, inicioDoDia, fimDoDia);
        
        List<LocalDateTime> horariosDisponiveis = new ArrayList<>();
        
        for (Agenda agenda : agendas) {
            LocalTime horaAtual = agenda.getHoraInicio();
            LocalTime horaFim = agenda.getHoraFim();
            
            while (horaAtual.isBefore(horaFim)) {
                LocalDateTime horario = data.atTime(horaAtual);
                
                boolean ocupado = agendamentos.stream()
                    .anyMatch(a -> a.getDataHora().equals(horario) && 
                                   a.getStatus() != StatusAgendamento.CANCELADO);
                
                if (!ocupado) {
                    horariosDisponiveis.add(horario);
                }
                
                horaAtual = horaAtual.plusMinutes(agenda.getIntervaloMinutos());
            }
        }
        
        return new HorariosDisponiveisDTO(
            profissionalId,
            profissional.getNome(),
            data.atStartOfDay(),
            horariosDisponiveis
        );
    }

    public AgendamentoResponseDTO criar(AgendamentoRequestDTO request) {
        var usuarioLogado = usuarioAutenticado.get();
        var empresaId = usuarioLogado.getEmpresa().getId();
        
        var paciente = pacienteRepository.findById(request.getPacienteId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paciente não encontrado"));
        
        if (!paciente.getEmpresa().getId().equals(empresaId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Paciente não pertence à sua empresa");
        }
        
        if (!paciente.getAtivo()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Paciente inativo");
        }
        
        var profissional = usuarioRepository.findById(request.getProfissionalId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profissional não encontrado"));
        
        if (profissional.getTipo() != TipoUsuario.PROFISSIONAL) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário não é um profissional");
        }
        
        if (!profissional.getEmpresa().getId().equals(empresaId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Profissional não pertence à sua empresa");
        }
        
        if (!profissional.getAtivo()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Profissional inativo");
        }
        
        if (request.getDataHora().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não é possível agendar no passado");
        }
        
        if (agendamentoRepository.existsHorarioOcupado(request.getProfissionalId(), request.getDataHora())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Horário já ocupado");
        }
        
        DayOfWeek diaSemana = request.getDataHora().getDayOfWeek();
        LocalTime hora = request.getDataHora().toLocalTime();
        
        boolean horarioValido = agendaRepository.findByProfissionalIdAndDiaSemana(request.getProfissionalId(), diaSemana)
            .stream()
            .filter(Agenda::getAtivo)
            .anyMatch(agenda -> 
                !hora.isBefore(agenda.getHoraInicio()) && 
                hora.isBefore(agenda.getHoraFim()) &&
                hora.equals(calcularHorarioDisponivel(agenda.getHoraInicio(), hora, agenda.getIntervaloMinutos()))
            );
        
        if (!horarioValido) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Horário não disponível na agenda do profissional");
        }
        
        var agendamento = new Agendamento();
        agendamento.setPaciente(paciente);
        agendamento.setProfissional(profissional);
        agendamento.setDataHora(request.getDataHora());
        agendamento.setStatus(StatusAgendamento.AGENDADO);
        agendamento.setTipoConsulta(request.getTipoConsulta());
        agendamento.setObservacoes(request.getObservacoes());
        agendamento.setUsuarioCriacao(usuarioLogado);
        agendamento.setDataCriacao(LocalDateTime.now());
        agendamento.setEmpresa(usuarioLogado.getEmpresa());
        
        var agendamentoSalvo = agendamentoRepository.save(agendamento);
        return AgendamentoResponseDTO.fromEntity(agendamentoSalvo);
    }

    public AgendamentoResponseDTO atualizarStatus(Long id, AgendamentoStatusDTO request) {
        var usuarioLogado = usuarioAutenticado.get();
        var agendamento = buscarAgendamentoPorIdEValidarEmpresa(id, usuarioLogado.getEmpresa().getId());
        
        if (request.getStatus() == StatusAgendamento.REALIZADO) {
            if (agendamento.getStatus() == StatusAgendamento.CANCELADO) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Não é possível realizar um agendamento cancelado");
            }
            if (agendamento.getDataHora().isAfter(LocalDateTime.now())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Não é possível realizar um agendamento futuro");
            }
        }
        
        if (request.getStatus() == StatusAgendamento.CANCELADO) {
            if (agendamento.getStatus() == StatusAgendamento.REALIZADO) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Não é possível cancelar um agendamento já realizado");
            }
        }
        
        agendamento.setStatus(request.getStatus());
        if (request.getObservacoes() != null) {
            String observacoesAtuais = agendamento.getObservacoes() != null ? 
                agendamento.getObservacoes() : "";
            agendamento.setObservacoes(observacoesAtuais + " | Status alterado: " + 
                request.getObservacoes());
        }
        
        var agendamentoAtualizado = agendamentoRepository.save(agendamento);
        return AgendamentoResponseDTO.fromEntity(agendamentoAtualizado);
    }

    public void cancelar(Long id) {
        var usuarioLogado = usuarioAutenticado.get();
        var agendamento = buscarAgendamentoPorIdEValidarEmpresa(id, usuarioLogado.getEmpresa().getId());
        
        if (agendamento.getStatus() == StatusAgendamento.REALIZADO) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Não é possível cancelar um agendamento já realizado");
        }
        
        agendamento.setStatus(StatusAgendamento.CANCELADO);
        agendamentoRepository.save(agendamento);
    }

    private LocalTime calcularHorarioDisponivel(LocalTime inicio, LocalTime horario, int intervalo) {
        long minutosDesdeInicio = java.time.Duration.between(inicio, horario).toMinutes();
        return minutosDesdeInicio % intervalo == 0 ? horario : null;
    }

    private Agendamento buscarAgendamentoPorIdEValidarEmpresa(Long id, Long empresaId) {
        var agendamento = agendamentoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Agendamento não encontrado"));
        
        if (!agendamento.getEmpresa().getId().equals(empresaId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Agendamento não pertence à sua empresa");
        }
        
        return agendamento;
    }
}