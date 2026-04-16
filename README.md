# Solus API - Sistema de Gestão de Saúde

![Solus Logo](https://img.shields.io/badge/Solus-Health%20System-blue?style=for-the-badge)
![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.2-green?style=for-the-badge&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-latest-blue?style=for-the-badge&logo=postgresql)

Solus é uma solução robusta para gestão clínica e hospitalar, oferecendo controle completo sobre agendamentos, prontuários eletrônicos, gestão de profissionais e faturamento por convênios.

---

## 🛠️ Stack Tecnológica

- **Linguagem:** Java 21
- **Framework:** Spring Boot 4.0.2
- **Persistência:** Spring Data JPA + Hibernate
- **Banco de Dados:** PostgreSQL
- **Segurança:** Spring Security + JWT (JSON Web Token)
- **Utilitários:** Lombok, Jakarta Validation
- **Documentação:** README Professional Documentation

---

## 🔐 Autenticação e Segurança

A API utiliza autenticação baseada em **JWT**. Todas as requisições (exceto login) devem incluir o token no cabeçalho:

`Authorization: Bearer <seu_token_jwt>`

### Controle de Acesso (RBAC)
Os acessos são restritos com base nos perfis:
- `ADMIN`: Acesso total ao sistema.
- `GERENTE`: Gestão operacional e visualização de dados.
- `PROFISSIONAL`: Acesso focado em atendimentos e agendas.
- `RECEPCAO`: Foco em pacientes e agendamentos.

---

## 🚀 API Reference

### 1. Autenticação (`/api/auth`)

| Método | Endpoint | Descrição | Acesso |
| :--- | :--- | :--- | :--- |
| `POST` | `/login` | Autentica um usuário e retorna o token | Público |

#### Requisição: `LoginRequestDTO`
```json
{
  "email": "string",
  "senha": "string"
}
```

---

### 2. Usuários (`/api/usuarios`)

| Método | Endpoint | Descrição | Acesso |
| :--- | :--- | :--- | :--- |
| `GET` | `/` | Lista todos os usuários (filtros opcionais) | ADMIN, GERENTE |
| `GET` | `/{id}` | Busca usuário por ID | ADMIN, GERENTE, PRÓPRIO |
| `POST` | `/` | Cria um novo usuário (Gera senha padrão) | ADMIN |
| `PUT` | `/{id}` | Atualiza dados do usuário | ADMIN |
| `PATCH` | `/{id}/ativar` | Reativa um usuário | ADMIN |
| `PATCH` | `/{id}/inativar` | Inativa um usuário | ADMIN |
| `POST` | `/alterar-senha` | Usuário logado altera sua própria senha | Autenticado |

#### Requisição (Criação): `UsuarioRequestDTO`
```json
{
  "nome": "João Silva",
  "email": "joao@solus.com",
  "tipo": "ADMIN", // ADMIN, GERENTE, PROFISSIONAL, RECEPCAO
  "ativo": true
}
```

---

### 3. Profissionais (`/api/usuarios/profissionais`)

| Método | Endpoint | Descrição | Acesso |
| :--- | :--- | :--- | :--- |
| `GET` | `/` | Lista todos os profissionais (médicos, etc) | Todos |
| `POST` | `/` | Cadastra um novo profissional | ADMIN |
| `PUT` | `/{id}` | Atualiza dados do profissional | ADMIN |

#### Requisição: `ProfissionalRequestDTO`
```json
{
  "nome": "Dr. Carlos Oliveira",
  "email": "carlos@clinic.com",
  "tipoProfissional": "MEDICO", // MEDICO, DENTISTA, FISIOTERAPEUTA
  "registroProfissional": "CRM/SP 123456",
  "especialidadeId": 1,
  "telefone": "(11) 98888-7777",
  "ativo": true
}
```

---

### 4. Pacientes (`/api/pacientes`)

| Método | Endpoint | Descrição | Acesso |
| :--- | :--- | :--- | :--- |
| `GET` | `/` | Lista pacientes com filtros | ADMIN, GERENTE, PROF, RECEP |
| `GET` | `/{id}` | Detalhes do paciente | ADMIN, GERENTE, PROF, RECEP |
| `POST` | `/` | Cadastra novo paciente | ADMIN, GERENTE, RECEP |
| `PUT` | `/{id}` | Atualiza paciente | ADMIN, GERENTE, RECEP |

#### Requisição: `PacienteRequestDTO`
```json
{
  "nome": "Maria de Souza",
  "cpf": "123.456.789-00",
  "rg": "12.345.678-9",
  "dataNascimento": "1990-05-15",
  "sexo": "F",
  "telefone": "(11) 97777-6666",
  "email": "maria@email.com",
  "endereco": "Rua das Flores, 123",
  "cidade": "São Paulo",
  "estado": "SP",
  "cep": "01001-000",
  "convenioId": 2,
  "numeroCarteirinha": "CONV-999988",
  "observacoes": "Alérgica a dipirona"
}
```

---

### 5. Agendas e Horários (`/api/agendas`)

| Método | Endpoint | Descrição | Acesso |
| :--- | :--- | :--- | :--- |
| `GET` | `/` | Lista agendas disponíveis | Todos |
| `POST` | `/` | Define nova grade de horários | ADMIN, GERENTE |
| `DELETE` | `/{id}` | Remove uma agenda | ADMIN |

#### Requisição: `AgendaRequestDTO`
```json
{
  "profissionalId": 5,
  "diaSemana": "MONDAY", // MONDAY, TUESDAY, etc
  "horaInicio": "08:00:00",
  "horaFim": "18:00:00",
  "intervaloMinutos": 30,
  "ativo": true
}
```

---

### 6. Agendamentos (`/api/agendamentos`)

| Método | Endpoint | Descrição | Acesso |
| :--- | :--- | :--- | :--- |
| `GET` | `/` | Lista consultas marcadas | Todos |
| `GET` | `/disponiveis` | Verifica horários vagos por data | ADMIN, GERENTE, RECEP |
| `POST` | `/` | Reserva um horário | ADMIN, GERENTE, RECEP |
| `PATCH` | `/{id}/status` | Altera status (AGENDADO, CONFIRMADO, CANCELADO) | Todos |

#### Requisição: `AgendamentoRequestDTO`
```json
{
  "pacienteId": 10,
  "profissionalId": 5,
  "dataHora": "2024-03-20T14:30:00",
  "tipoConsulta": "PRIMEIRA_VEZ", // PRIMEIRA_VEZ, RETORNO, EXAME
  "observacoes": "Trazer exames anteriores"
}
```

---

### 7. Atendimentos (Prontuário) (`/api/atendimentos`)

| Método | Endpoint | Descrição | Acesso |
| :--- | :--- | :--- | :--- |
| `GET` | `/paciente/{id}` | Histórico clínico do paciente | ADMIN, GERENTE, PROF |
| `POST` | `/` | Inicia registro de atendimento | PROFISSIONAL |
| `PATCH` | `/{id}/finalizar` | Conclui o atendimento | PROFISSIONAL |

#### Requisição: `AtendimentoRequestDTO`
```json
{
  "agendamentoId": 15,
  "dataHoraInicio": "2024-03-20T14:35:00",
  "queixaPrincipal": "Dores fortes na lombar",
  "historicoDoenca": "Paciente relata dor há 3 dias...",
  "exameFisico": "Mobilidade reduzida em L4-L5",
  "diagnostico": "Lombalgia aguda",
  "prescricao": "Repouso e medicação X",
  "observacoes": "Retornar em 7 dias se não houver melhora"
}
```

---

## 🏗️ Estrutura de Pastas

```text
src/main/java/com/solus/
├── controller/   # Endpoints da API
├── service/      # Regras de negócio
├── repository/   # Comunicação com Banco de Dados
├── entity/       # Modelos de dados
├── dto/          # Objetos de transferência de dados (Request/Response)
├── security/     # Configurações de JWT e Segurança
├── enums/        # Tipos enumeradores (Status, Perfis)
└── config/       # Configurações gerais (CORS, etc)
```

## ⚙️ Configuração Local

1. Clone o repositório.
2. Configure o banco PostgreSQL no `application.properties`.
3. Certifique-se de ter o **JDK 21** instalado.
4. Execute via Maven:
   ```bash
   mvn spring-boot:run
   ```

---

© 2026 Solus Health System. Todos os direitos reservados.
