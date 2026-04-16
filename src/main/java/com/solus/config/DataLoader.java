package com.solus.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.solus.entity.Empresa;
import com.solus.entity.Usuario;
import com.solus.enums.TipoUsuario;
import com.solus.repository.EmpresaRepository;
import com.solus.repository.UsuarioRepository;

import java.time.LocalDateTime;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        
        // Criar empresa padrão se não existir
        if (empresaRepository.count() == 0) {
            Empresa empresa = new Empresa();
            empresa.setNomeFantasia("Zeller Demo");
            empresa.setRazaoSocial("Zeller Comércio Ltda");
            empresa.setCnpj("00.000.000/0001-00");
            empresa.setDataCadastro(LocalDateTime.now());
            empresa.setAtivo(true);
            empresa = empresaRepository.save(empresa);
            
            System.out.println("✅ Empresa padrão criada: Zeller Demo");
            
            // Criar usuário DONO padrão
            Usuario dono = new Usuario();
            dono.setNome("Administrador");
            dono.setEmail("admin@zeller.com");
            dono.setSenhaHash(passwordEncoder.encode("123456"));
            dono.setTipo(TipoUsuario.ADMIN);
            dono.setEmpresa(empresa);
            dono.setAtivo(true);
            dono.setDataCadastro(LocalDateTime.now());
            dono.setPrecisaTrocarSenha(false);
            usuarioRepository.save(dono);
            
            System.out.println("✅ Usuário DONO padrão criado: admin@zeller.com / 123456");
        } else {
            System.out.println("ℹ️ Dados já existentes. Nada foi criado.");
        }
    }
}