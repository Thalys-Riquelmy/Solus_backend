package com.solus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.solus.dto.*;
import com.solus.entity.Especialidade;
import com.solus.entity.Usuario;
import com.solus.enums.TipoProfissional;
import com.solus.enums.TipoUsuario;
import com.solus.repository.EspecialidadeRepository;
import com.solus.repository.UsuarioRepository;
import com.solus.security.UsuarioAutenticado;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EspecialidadeRepository especialidadeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioAutenticado usuarioAutenticado;

    private static final String CARACTERES_SENHA = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
    private static final SecureRandom random = new SecureRandom();


    public List<UsuarioResponseDTO> listarTodos(String nome, String email, TipoUsuario tipo, Boolean ativo) {
        Usuario usuarioLogado = usuarioAutenticado.get();
        
        List<Usuario> usuarios = usuarioRepository.findByEmpresaId(usuarioLogado.getEmpresa().getId());
        
        if (nome != null && !nome.isEmpty()) {
            usuarios = usuarios.stream()
                .filter(u -> u.getNome().toLowerCase().contains(nome.toLowerCase()))
                .collect(Collectors.toList());
        }
        
        if (email != null && !email.isEmpty()) {
            usuarios = usuarios.stream()
                .filter(u -> u.getEmail() != null && u.getEmail().toLowerCase().contains(email.toLowerCase()))
                .collect(Collectors.toList());
        }
        
        if (tipo != null) {
            usuarios = usuarios.stream()
                .filter(u -> u.getTipo() == tipo)
                .collect(Collectors.toList());
        }
        
        if (ativo != null) {
            usuarios = usuarios.stream()
                .filter(u -> u.getAtivo().equals(ativo))
                .collect(Collectors.toList());
        }
        
        return usuarios.stream()
            .map(UsuarioResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuarioLogado = usuarioAutenticado.get();
        Usuario usuario = buscarUsuarioPorIdEValidarEmpresa(id, usuarioLogado);
        return UsuarioResponseDTO.fromEntity(usuario);
    }

    public UsuarioResponseDTO buscarPorEmail(String email) {
        Usuario usuarioLogado = usuarioAutenticado.get();
        
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        
        if (!usuario.getEmpresa().getId().equals(usuarioLogado.getEmpresa().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário não pertence à sua empresa");
        }
        
        return UsuarioResponseDTO.fromEntity(usuario);
    }

    public List<UsuarioResponseDTO> listarPorTipo(TipoUsuario tipo) {
        Usuario usuarioLogado = usuarioAutenticado.get();
        
        return usuarioRepository.findByTipo(tipo).stream()
            .filter(u -> u.getEmpresa().getId().equals(usuarioLogado.getEmpresa().getId()))
            .map(UsuarioResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    public UsuarioPerfilDTO getPerfil() {
        Usuario usuarioLogado = usuarioAutenticado.get();
        return UsuarioPerfilDTO.fromEntity(usuarioLogado);
    }

    public UsuarioSenhaResponseDTO criar(UsuarioRequestDTO request) {
        Usuario usuarioLogado = usuarioAutenticado.get();
        
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email já cadastrado");
        }
        
        String senhaTemporaria = gerarSenhaAleatoria(8);
        
        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setSenhaHash(passwordEncoder.encode(senhaTemporaria));
        usuario.setTipo(request.getTipo());
        usuario.setEmpresa(usuarioLogado.getEmpresa());
        usuario.setAtivo(request.getAtivo() != null ? request.getAtivo() : true);
        usuario.setDataCadastro(LocalDateTime.now());
        usuario.setPrecisaTrocarSenha(true);
        
        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        
        return new UsuarioSenhaResponseDTO(
            usuarioSalvo.getId(),
            usuarioSalvo.getNome(),
            usuarioSalvo.getEmail(),
            senhaTemporaria
        );
    }

    public UsuarioSenhaResponseDTO criarProfissional(ProfissionalRequestDTO request) {
        Usuario usuarioLogado = usuarioAutenticado.get();
        
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email já cadastrado");
        }
        
        if (usuarioRepository.findByRegistroProfissional(request.getRegistroProfissional()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Registro profissional já cadastrado");
        }
        
        Especialidade especialidade = null;
        if (request.getEspecialidadeId() != null) {
            especialidade = especialidadeRepository.findById(request.getEspecialidadeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Especialidade não encontrada"));
            
            if (!especialidade.getEmpresa().getId().equals(usuarioLogado.getEmpresa().getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Especialidade não pertence à sua empresa");
            }
        }
        
        String senha = request.getSenha();
        boolean precisaTrocarSenha = false;
        
        if (senha == null || senha.isEmpty()) {
            senha = gerarSenhaAleatoria(8);
            precisaTrocarSenha = true;
        }
        
        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setSenhaHash(passwordEncoder.encode(senha));
        usuario.setTipo(TipoUsuario.PROFISSIONAL);
        usuario.setTipoProfissional(request.getTipoProfissional());
        usuario.setRegistroProfissional(request.getRegistroProfissional());
        usuario.setEspecialidade(especialidade);
        usuario.setTelefone(request.getTelefone());
        usuario.setEmpresa(usuarioLogado.getEmpresa());
        usuario.setAtivo(request.getAtivo() != null ? request.getAtivo() : true);
        usuario.setDataCadastro(LocalDateTime.now());
        usuario.setPrecisaTrocarSenha(precisaTrocarSenha);
        
        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        
        return new UsuarioSenhaResponseDTO(
            usuarioSalvo.getId(),
            usuarioSalvo.getNome(),
            usuarioSalvo.getEmail(),
            senha
        );
    }

    public UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO request) {
        Usuario usuarioLogado = usuarioAutenticado.get();
        Usuario usuario = buscarUsuarioPorIdEValidarEmpresa(id, usuarioLogado);
        
        usuarioRepository.findByEmail(request.getEmail()).ifPresent(u -> {
            if (!u.getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email já cadastrado");
            }
        });
        
        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setTipo(request.getTipo());
        usuario.setAtivo(request.getAtivo() != null ? request.getAtivo() : usuario.getAtivo());
        
        Usuario usuarioAtualizado = usuarioRepository.save(usuario);
        return UsuarioResponseDTO.fromEntity(usuarioAtualizado);
    }

    public ProfissionalResponseDTO atualizarProfissional(Long id, ProfissionalRequestDTO request) {
        Usuario usuarioLogado = usuarioAutenticado.get();
        Usuario usuario = buscarProfissionalPorIdEValidarEmpresa(id, usuarioLogado.getEmpresa().getId());
        
        usuarioRepository.findByEmail(request.getEmail()).ifPresent(u -> {
            if (!u.getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email já cadastrado");
            }
        });
        
        usuarioRepository.findByRegistroProfissional(request.getRegistroProfissional()).ifPresent(u -> {
            if (!u.getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Registro profissional já cadastrado");
            }
        });
        
        Especialidade especialidade = null;
        if (request.getEspecialidadeId() != null) {
            especialidade = especialidadeRepository.findById(request.getEspecialidadeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Especialidade não encontrada"));
            
            if (!especialidade.getEmpresa().getId().equals(usuarioLogado.getEmpresa().getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Especialidade não pertence à sua empresa");
            }
        }
        
        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setTipoProfissional(request.getTipoProfissional());
        usuario.setRegistroProfissional(request.getRegistroProfissional());
        usuario.setEspecialidade(especialidade);
        usuario.setTelefone(request.getTelefone());
        usuario.setAtivo(request.getAtivo() != null ? request.getAtivo() : usuario.getAtivo());
        
        if (request.getSenha() != null && !request.getSenha().isEmpty()) {
            usuario.setSenhaHash(passwordEncoder.encode(request.getSenha()));
            usuario.setPrecisaTrocarSenha(true);
        }
        
        Usuario usuarioAtualizado = usuarioRepository.save(usuario);
        return ProfissionalResponseDTO.fromEntity(usuarioAtualizado);
    }

    public void ativar(Long id) {
        Usuario usuarioLogado = usuarioAutenticado.get();
        Usuario usuario = buscarUsuarioPorIdEValidarEmpresa(id, usuarioLogado);
        usuario.setAtivo(true);
        usuarioRepository.save(usuario);
    }

    public void inativar(Long id) {
        Usuario usuarioLogado = usuarioAutenticado.get();
        
        if (usuarioLogado.getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Você não pode inativar o próprio usuário");
        }
        
        Usuario usuario = buscarUsuarioPorIdEValidarEmpresa(id, usuarioLogado);
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
    }

    public UsuarioSenhaResponseDTO redefinirSenha(Long id) {
        Usuario usuarioLogado = usuarioAutenticado.get();
        Usuario usuario = buscarUsuarioPorIdEValidarEmpresa(id, usuarioLogado);
        
        String novaSenha = gerarSenhaAleatoria(8);
        usuario.setSenhaHash(passwordEncoder.encode(novaSenha));
        usuario.setPrecisaTrocarSenha(true);
        
        usuarioRepository.save(usuario);
        
        return new UsuarioSenhaResponseDTO(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            novaSenha
        );
    }

    public void alterarPropriaSenha(AlterarSenhaRequestDTO request) {
        Usuario usuarioLogado = usuarioAutenticado.get();

        if (!passwordEncoder.matches(request.getSenhaAtual(), usuarioLogado.getSenhaHash())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Senha atual incorreta");
        }
        
        if (!request.getNovaSenha().equals(request.getConfirmacaoSenha())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nova senha e confirmação não conferem");
        }
        
        usuarioLogado.setSenhaHash(passwordEncoder.encode(request.getNovaSenha()));
        usuarioLogado.setPrecisaTrocarSenha(false);
        
        usuarioRepository.save(usuarioLogado);
    }

    // MÉTODOS PARA PROFISSIONAIS

    public List<ProfissionalResumidoDTO> listarProfissionais(
            String nome, 
            TipoProfissional tipoProfissional, 
            Long especialidadeId, 
            Boolean ativo) {
        
        Usuario usuarioLogado = usuarioAutenticado.get();
        Long empresaId = usuarioLogado.getEmpresa().getId();
        
        List<Usuario> profissionais = usuarioRepository.findByEmpresaIdAndTipo(empresaId, TipoUsuario.PROFISSIONAL);
        
        if (nome != null && !nome.isEmpty()) {
            profissionais = profissionais.stream()
                .filter(p -> p.getNome().toLowerCase().contains(nome.toLowerCase()))
                .collect(Collectors.toList());
        }
        
        if (tipoProfissional != null) {
            profissionais = profissionais.stream()
                .filter(p -> p.getTipoProfissional() == tipoProfissional)
                .collect(Collectors.toList());
        }
        
        if (especialidadeId != null) {
            profissionais = profissionais.stream()
                .filter(p -> p.getEspecialidade() != null && 
                             p.getEspecialidade().getId().equals(especialidadeId))
                .collect(Collectors.toList());
        }
        
        if (ativo != null) {
            profissionais = profissionais.stream()
                .filter(p -> p.getAtivo().equals(ativo))
                .collect(Collectors.toList());
        }
        
        return profissionais.stream()
            .map(ProfissionalResumidoDTO::fromEntity)
            .collect(Collectors.toList());
    }

    public ProfissionalResponseDTO buscarProfissionalPorId(Long id) {
        Usuario usuarioLogado = usuarioAutenticado.get();
        Usuario usuario = buscarProfissionalPorIdEValidarEmpresa(id, usuarioLogado.getEmpresa().getId());
        return ProfissionalResponseDTO.fromEntity(usuario);
    }

    public List<ProfissionalResumidoDTO> listarProfissionaisPorTipo(TipoProfissional tipoProfissional) {
        Usuario usuarioLogado = usuarioAutenticado.get();
        Long empresaId = usuarioLogado.getEmpresa().getId();
        
        return usuarioRepository.findByEmpresaIdAndTipo(empresaId, TipoUsuario.PROFISSIONAL)
            .stream()
            .filter(p -> p.getTipoProfissional() == tipoProfissional)
            .map(ProfissionalResumidoDTO::fromEntity)
            .collect(Collectors.toList());
    }

    // MÉTODOS AUXILIARES 

    private Usuario buscarUsuarioPorIdEValidarEmpresa(Long id, Usuario usuarioLogado) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        
        if (!usuario.getEmpresa().getId().equals(usuarioLogado.getEmpresa().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário não pertence à sua empresa");
        }
        
        return usuario;
    }

    private Usuario buscarProfissionalPorIdEValidarEmpresa(Long id, Long empresaId) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profissional não encontrado"));
        
        if (usuario.getTipo() != TipoUsuario.PROFISSIONAL) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário não é um profissional");
        }
        
        if (!usuario.getEmpresa().getId().equals(empresaId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Profissional não pertence à sua empresa");
        }
        
        return usuario;
    }

    private String gerarSenhaAleatoria(int tamanho) {
        StringBuilder sb = new StringBuilder(tamanho);
        for (int i = 0; i < tamanho; i++) {
            sb.append(CARACTERES_SENHA.charAt(random.nextInt(CARACTERES_SENHA.length())));
        }
        return sb.toString();
    }
}