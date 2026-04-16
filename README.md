# SOLUS One - Sistema de Gestão de Saúde

![SOLUS One Logo](https://img.shields.io/badge/SOLUS-One-blue?style=for-the-badge&logo=heart)
![Version](https://img.shields.io/badge/version-1.0.0-green?style=for-the-badge)

**SOLUS One** é uma plataforma moderna e completa para a gestão de clínicas e consultórios médicos. O sistema integra a recepção, o corpo clínico e o gerenciamento administrativo em um único ecossistema fluido e eficiente.

---

## 📂 Estrutura do Projeto

O repositório está organizado em uma arquitetura de monorepo:

- **[Solus (Backend)](./Solus)**: API REST robusta desenvolvida com Java 21 e Spring Boot 4. Responsável por toda a lógica de negócio, segurança e persistência de dados.
- **[SOLUSfront (Frontend)](./SOLUSfront)**: Aplicação Web moderna desenvolvida com Angular 21. Oferece uma interface intuitiva, responsiva e performática para os usuários finais.

---

## ✨ Principais Funcionalidades

- 🔐 **Autenticação Segura:** Controle de acesso por níveis (Admin, Gerente, Médico, Recepção).
- 📅 **Agenda Inteligente:** Gestão de horários e agendamentos com visualização em calendário.
- 📋 **Prontuário Eletrônico:** Registro completo de atendimentos com histórico clínico.
- 👥 **Gestão de Pacientes:** Cadastro detalhado com integração a convênios.
- 📊 **Dashboard Administrativo:** Indicadores em tempo real para tomada de decisão.

---

## 🛠️ Stack Tecnológica

### Backend
- **Java 21**
- **Spring Boot 4.0.2**
- **PostgreSQL**
- **Spring Security + JWT**

### Frontend
- **Angular 21**
- **Angular Material**
- **FullCalendar**
- **Chart.js**

---

## 🚀 Como Iniciar

### Pré-requisitos
- JDK 21+ instalado.
- Node.js e npm instalados.
- Instância do PostgreSQL em execução.

### Passo 1: Backend
1. Navegue até a pasta `Solus`.
2. Configure o `application.properties` com suas credenciais do banco.
3. Execute `./mvnw spring-boot:run`.

### Passo 2: Frontend
1. Navegue até a pasta `SOLUSfront`.
2. Instale as dependências: `npm install`.
3. Inicie o servidor: `npm start`.
4. Acesse `http://localhost:4200`.

---

## 📄 Documentação Detalhada

Para informações técnicas aprofundadas sobre requisitos funcionais, não funcionais e fluxo do sistema, consulte:
👉 **[DOCUMENTACAO.md](./DOCUMENTACAO.md)**

---

© 2026 SOLUS One. Desenvolvido para transformar a gestão de saúde.
