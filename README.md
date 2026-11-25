# ThinDesk: controle de chamados técnicos

![Versão](https://img.shields.io/badge/vers%C3%A3o-2.0.0-blue)
![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-brightgreen)

Sistema web para **abrir, acompanhar e resolver chamados técnicos**, desenvolvido na disciplina a partir do projeto ThinDesk apresentado em sala (`thindesk.zip`). Mantém a estrutura e o visual do projeto original (sidebar "Thindesk Menu", Bootstrap, telas de Chamados, Clientes e Horário de Atendimento) e acrescenta login com perfis, um **dashboard** e as regras de negócio do atendimento.

## Acesso à aplicação (deploy)

**Link:** https://SEU-SERVICO.onrender.com

| Perfil | E-mail | Senha |
|--------|--------|-------|
| Técnico | `tecnico@thindesk.com` | `tecnico123` |
| Usuário | `usuario@thindesk.com` | `usuario123` |

> O deploy está no plano gratuito do Render: se a aplicação estiver parada, o primeiro acesso pode levar até 1 minuto para carregar.

## Versionamento

**Versão atual: `2.0.0`**

O projeto usa **Versionamento Semântico** ([SemVer 2.0.0](https://semver.org/lang/pt-BR/)), no formato:

```
MAJOR.MINOR.PATCH[-PRÉ-LANÇAMENTO]
  2  .  0  .  0
```

| Parte | Quando aumenta | Exemplo a partir de 2.0.0 |
|-------|----------------|---------------------------|
| **MAJOR** | Mudança incompatível com a versão anterior (estrutura dos dados, regras ou telas que deixam de funcionar como antes) | `3.0.0` |
| **MINOR** | Nova funcionalidade que não quebra o que já existe | `2.1.0` |
| **PATCH** | Correção de erro, sem funcionalidade nova | `2.0.1` |
| **-PRÉ-LANÇAMENTO** | Versão ainda em teste, antes da versão final: `alpha` (incompleta), `beta` (completa, em testes), `rc` (candidata a final) | `2.1.0-beta.1` |

Ao aumentar uma parte, as da direita voltam a zero (`2.1.3` → `2.2.0`). Uma versão com sufixo vem antes da versão final: `2.1.0-alpha` < `2.1.0-beta.1` < `2.1.0`.

**Por que esta versão é a 2.0.0:** o projeto recebido em sala estava na versão `1.0-alpha` (ver `pom.xml` do zip), ou seja, `1.0.0-alpha` em SemVer. Esta entrega muda a estrutura dos dados do chamado, troca o usuário fixo `admin/thindesk` por usuários com perfis e altera as telas. Como não é compatível com a versão anterior, o MAJOR passou de 1 para 2. E, por ser a versão de entrega, já estável, não leva sufixo.

**Onde a versão aparece:**
- `pom.xml` (`<version>2.0.0</version>`), a fonte oficial;
- rodapé da sidebar em todas as telas (lido do `pom.xml` durante o build);
- tag do Git `v2.0.0`;
- histórico de mudanças em [`CHANGELOG.md`](CHANGELOG.md).

## Dashboard

![Dashboard do ThinDesk](docs/dashboard.png)

O dashboard é a tela inicial depois do login e segue o layout do ThinDesk original (mesma sidebar, Bootstrap e logo). Ele mostra:
- indicadores de chamados **abertos**, **em andamento** e **concluídos**, mais a **taxa de conclusão** (clicar num indicador abre a lista filtrada);
- gráficos de chamados **por status** e **por tipo**;
- últimos chamados e pendências por prioridade.

Técnicos e administradores veem todos os chamados; o usuário comum vê só os seus.

## Funcionalidades

| Tela | O que faz | Quem pode |
|------|-----------|-----------|
| Login | Entrada com e-mail e senha | todos |
| Dashboard | Indicadores e gráficos | todos |
| Chamados | Listar, filtrar por status, abrir chamado, ver detalhes | todos (usuário vê só os seus) |
| Atendimento | Assumir e concluir chamado (com solução) | técnico e admin |
| Cancelar chamado | Cancelar enquanto não foi assumido | quem abriu, técnico e admin |
| Clientes | Listar e cadastrar | cadastro: técnico e admin |
| Técnicos | Equipe e cadastro de usuários | lista: técnico e admin; cadastro: admin |
| Tipo de Chamado | Tipos usados na abertura | lista: técnico e admin; cadastro: admin |
| Horário de Atendimento | Horários por setor | todos veem; cadastro: admin |

### Regras de negócio

- Ao abrir um chamado, o sistema registra **quem abriu**, a **data de abertura** e o status **ABERTO**.
- Título, descrição e tipo são obrigatórios.
- Ciclo do chamado: **ABERTO → EM_ANDAMENTO → CONCLUIDO** (os mesmos status do projeto original), ou **CANCELADO**.
- Quando um técnico assume, ele vira o responsável e o status passa para EM_ANDAMENTO.
- Para concluir é obrigatório descrever a **solução**; a data de conclusão é gravada.
- Chamado **concluído ou cancelado não pode mais ser alterado**.
- O usuário comum só vê os próprios chamados e só cancela enquanto ninguém assumiu.

## Tecnologias

- Java 17 e Spring Boot 3.3 (Web, Data MongoDB, Security, Validation, Actuator)
- Thymeleaf + Thymeleaf Extras Spring Security, Bootstrap 5, Bootstrap Icons e Chart.js
- MongoDB Atlas (produção) ou MongoDB em memória (execução local sem configuração)
- Docker e Render (deploy), GitHub Actions (build e testes a cada push)

## Como executar localmente

Pré-requisito: Java 17.

```bash
./mvnw spring-boot:run
```

Acesse http://localhost:8080 e entre com uma das credenciais acima (o administrador local é `admin@thindesk.com` / `admin123`).

Sem configuração nenhuma, o sistema usa um **MongoDB em memória** com dados de exemplo (os dados somem ao reiniciar). Para usar o **MongoDB Atlas**, copie `.env.example` para `.env` e preencha `MONGODB_URI`.

### Variáveis de ambiente

| Variável | Para que serve | Padrão |
|----------|----------------|--------|
| `MONGODB_URI` | Conexão do MongoDB Atlas | vazio = MongoDB em memória |
| `MONGODB_DATABASE` | Nome do banco | `thindesk` |
| `SENHA_ADMIN` | Senha do `admin@thindesk.com` | `admin123` |
| `SENHA_TECNICO` / `SENHA_USUARIO` | Senhas dos usuários de demonstração | `tecnico123` / `usuario123` |
| `DADOS_EXEMPLO` | Cria clientes, horários e chamados de exemplo se o banco estiver vazio | `true` |
| `MOSTRAR_CREDENCIAIS` | Mostra as credenciais de demonstração na tela de login | `true` |
| `PORT` | Porta HTTP (o Render define sozinho) | `8080` |

Os usuários de demonstração são criados só se ainda não existirem; mudar a senha na variável depois não altera um usuário já gravado no banco.

## Testes

```bash
./mvnw test
```

- `ChamadoServiceTest`: regras de negócio, sem banco.
- `ThindeskIntegracaoTest`: sobe o sistema com o MongoDB em memória e testa login, permissões, todas as telas e o fluxo completo de um chamado.

O GitHub Actions roda os testes a cada push.

## Deploy (Render + MongoDB Atlas)

1. **MongoDB Atlas:** em *Network Access*, libere o acesso de qualquer IP (`0.0.0.0/0`), porque o Render não tem IP fixo no plano gratuito. Copie a *connection string* do cluster.
2. **Render:** *New → Blueprint*, escolha este repositório (o arquivo `render.yaml` já descreve o serviço) e preencha `MONGODB_URI` e `SENHA_ADMIN`.
3. O Render constrói a imagem pelo `Dockerfile` e publica a aplicação. O health check usa `/actuator/health`.

## Estrutura

```
src/main/java/com/pfc
├── ThindeskApplication.java
└── thindesk
    ├── config       SecurityConfig, UsuarioLogado, DadosIniciais, MongoEmMemoriaConfig
    ├── controller   Home (login e dashboard), Chamado, Cliente, Tecnico, TipoChamado, HorarioAtendimento
    ├── dto          ChamadoForm, UsuarioForm, DashboardDados
    ├── entity       Chamado, Usuario, Cliente, HorarioAtendimento, TipoChamado, enums
    ├── repository   repositórios MongoDB
    └── service      ChamadoService (regras), UsuarioService, UsuarioDetailsService
src/main/resources
├── templates        login, home (dashboard), chamados, clientes, tecnicos, tipo-chamado, ajustes-horarios
└── static           css/style.css, js/script.js e images/logo.png do projeto original
```

## O que mudou em relação ao ThinDesk original

- **Dashboard** novo como tela inicial (antes era só a logo).
- O usuário fixo `admin/thindesk` foi trocado por **usuários no MongoDB**, com senha criptografada (BCrypt) e três perfis: Usuário, Técnico e Administrador. A proteção CSRF, desligada no original, foi religada.
- A senha do banco saiu do `application.properties` e foi para variável de ambiente.
- O chamado ganhou título, prioridade, cliente, solução e datas, mantendo os campos originais (descrição, status, tipo, técnico e usuário).
- As telas de Técnicos e Tipo de Chamado, que tinham link na sidebar mas não existiam, foram implementadas. O link Recursos, sem tela no original, foi removido.
- Projeto atualizado para Spring Boot 3.3 e Java 17, empacotado como `.jar` executável.
