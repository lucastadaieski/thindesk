# Histórico de versões

O formato segue o [Keep a Changelog](https://keepachangelog.com/pt-BR/1.1.0/) e o projeto usa [Versionamento Semântico](https://semver.org/lang/pt-BR/).

## [2.0.0] - 2026-11-25

### Adicionado
- Dashboard com indicadores, gráficos por status e por tipo, últimos chamados e pendências por prioridade.
- Login com usuários no MongoDB, senha com BCrypt e perfis Usuário, Técnico e Administrador.
- Abertura de chamado com título, tipo, prioridade e cliente; tela de detalhes.
- Atendimento: assumir e concluir chamado com solução; cancelamento.
- Telas de Técnicos (cadastro de usuários) e Tipo de Chamado.
- MongoDB em memória para rodar sem configuração; dados de demonstração.
- Testes automatizados, GitHub Actions, Dockerfile e deploy no Render.

### Alterado
- Status do chamado passam a ser controlados pelo sistema (ABERTO, EM_ANDAMENTO, CONCLUIDO, CANCELADO).
- Credenciais do banco movidas para variáveis de ambiente.
- Spring Boot 3.3 e Java 17; empacotamento `.jar`.

### Removido
- Usuário fixo `admin/thindesk`.
- Link "Recursos" da sidebar (não havia tela).

## [1.0.0-alpha]

- Projeto base apresentado em sala (`thindesk.zip`, versão `1.0-alpha` no `pom.xml`): telas de Chamados, Clientes e Horário de Atendimento, login fixo.
