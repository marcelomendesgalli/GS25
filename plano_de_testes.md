# Plano de Testes Manuais - Green Light Monitor

## 1. Introdução

Este documento descreve o plano de testes manuais de validação no nível de sistema para o projeto "Green Light Monitor". O objetivo é garantir que as principais funcionalidades do sistema estejam operando conforme o esperado e que os requisitos do usuário sejam atendidos.

## 2. Escopo dos Testes

Os testes cobrirão as seguintes áreas funcionais:

-   Autenticação e Autorização de Usuários (OAuth2 e Local)
-   Gerenciamento de Escolas (CRUD)
-   Recebimento de Leituras de Sensores (via simulação RabbitMQ)
-   Geração e Visualização de Alertas (Básicos e com IA, se configurada)
-   Navegação e Usabilidade da Interface Web (Dashboard, Páginas de Gestão)

## 3. Ambiente de Teste

-   **Aplicação**: Green Light Monitor (executando localmente via `./start.sh` ou `mvn spring-boot:run`)
-   **Navegador**: Google Chrome (versão mais recente) ou Mozilla Firefox (versão mais recente)
-   **Banco de Dados**: H2 em memória (configuração padrão de desenvolvimento)
-   **Mensageria**: RabbitMQ (executando localmente, via Docker ou instalação nativa)
-   **IA (Opcional)**: Configuração da API OpenAI no `application-local.yml` se o teste de IA for realizado.

## 4. Dados de Teste Controlados

Antes de iniciar os testes, é recomendado popular o banco de dados com dados de exemplo. O arquivo `dados-exemplo.sql` pode ser usado para isso através do console H2 (`http://localhost:8080/h2-console`).

**Usuários de Teste (do `dados-exemplo.sql` ou cadastrados manualmente):**
-   **Admin Local**: `admin@escolaclima.com` / `password` (Role: ADMIN)
-   **Gestor Local**: `gestor@escola1.edu.br` / `password` (Role: GESTOR)
-   **Usuário Local**: `professor@escola1.edu.br` / `password` (Role: USUARIO)

**Dados de Escolas (do `dados-exemplo.sql`):**
-   Escola 1: "EMEF Prof. João Silva" (ID: 1)
-   Escola 2: "EMEF Maria Santos" (ID: 2)

**Dados de Sensores (do `dados-exemplo.sql`):**
-   Sensor 1 (Escola 1): ID 1, Localização "Sala 1A"
-   Sensor 3 (Escola 1): ID 3, Localização "Pátio"

## 5. Casos de Teste

--- 

**Teste ID:** ECM-TEST-001
**Título:** Login com OAuth2 (Google)
**Prioridade:** Alta
**Status:** Pendente

-   **Descrição:** Verificar se o usuário consegue se autenticar utilizando uma conta Google válida.
-   **Pré-condições:**
    -   Aplicação rodando.
    -   Configuração OAuth2 para Google preenchida no `application-local.yml` com credenciais válidas.
-   **Dados de Entrada:**
    -   Credenciais de uma conta Google válida.
-   **Procedimento:**
    1.  Acessar `http://localhost:8080`.
    2.  Clicar no botão "Login".
    3.  Clicar no botão "Login com Google".
    4.  Ser redirecionado para a página de autenticação do Google.
    5.  Inserir as credenciais da conta Google e autorizar a aplicação.
    6.  Ser redirecionado de volta para a aplicação.
-   **Dados de Saída Esperados:**
    -   Usuário é redirecionado para o Dashboard (`/dashboard`).
    -   O nome do usuário logado (ou email) é exibido no layout.
    -   Um novo registro de usuário (ou atualização) é criado na tabela `USUARIO` com `provider` = "google".
-   **Resultado:** (A ser preenchido após execução: Passou/Falhou)

--- 

**Teste ID:** ECM-TEST-002
**Título:** Login Local com Perfil ADMIN
**Prioridade:** Alta
**Status:** Pendente

-   **Descrição:** Verificar se o usuário ADMIN consegue se autenticar com credenciais locais.
-   **Pré-condições:**
    -   Aplicação rodando.
    -   Usuário `admin@escolaclima.com` com senha `password` e role `ADMIN` existe no banco (via `dados-exemplo.sql`).
-   **Dados de Entrada:**
    -   Email: `admin@escolaclima.com`
    -   Senha: `password`
-   **Procedimento:**
    1.  Acessar `http://localhost:8080`.
    2.  Clicar no botão "Login".
    3.  No formulário de Login Local, inserir o email e senha.
    4.  Clicar no botão "Entrar".
-   **Dados de Saída Esperados:**
    -   Usuário é redirecionado para o Dashboard (`/dashboard`).
    -   O nome do usuário "Administrador Sistema" é exibido.
    -   Menus/opções específicas para ADMIN (ex: gestão de usuários, se implementado) estão visíveis.
-   **Resultado:** (A ser preenchido após execução: Passou/Falhou)

--- 

**Teste ID:** ECM-TEST-003
**Título:** Acesso Negado a Funcionalidade Restrita (Usuário sem Permissão)
**Prioridade:** Média
**Status:** Pendente

-   **Descrição:** Verificar se um usuário com perfil USUARIO não consegue acessar funcionalidades restritas a ADMIN/GESTOR.
-   **Pré-condições:**
    -   Aplicação rodando.
    -   Usuário `professor@escola1.edu.br` com senha `password` e role `USUARIO` existe no banco.
-   **Dados de Entrada:**
    -   Login com `professor@escola1.edu.br` / `password`.
-   **Procedimento:**
    1.  Realizar login como `professor@escola1.edu.br`.
    2.  Após login, tentar acessar diretamente a URL de gerenciamento de escolas: `http://localhost:8080/escolas/nova` (ou outra URL restrita).
-   **Dados de Saída Esperados:**
    -   Usuário é redirecionado para uma página de acesso negado (erro 403) ou para o dashboard com uma mensagem de erro.
    -   A funcionalidade de adicionar nova escola não é acessível.
-   **Resultado:** (A ser preenchido após execução: Passou/Falhou)

--- 

**Teste ID:** ECM-TEST-004
**Título:** Cadastro de Nova Escola
**Prioridade:** Alta
**Status:** Pendente

-   **Descrição:** Verificar se um usuário ADMIN/GESTOR consegue cadastrar uma nova escola.
-   **Pré-condições:**
    -   Aplicação rodando.
    -   Usuário logado com perfil ADMIN ou GESTOR (ex: `admin@escolaclima.com`).
-   **Dados de Entrada (Formulário):**
    -   Nome: "EMEF Teste Inovador"
    -   Cidade: "São Paulo"
    -   Estado: "SP"
    -   Ativo: Sim
-   **Procedimento:**
    1.  Realizar login como `admin@escolaclima.com`.
    2.  Navegar para a página "Escolas" no menu.
    3.  Clicar no botão "Nova Escola".
    4.  Preencher o formulário com os dados de entrada.
    5.  Clicar no botão "Salvar".
-   **Dados de Saída Esperados:**
    -   Usuário é redirecionado para a lista de escolas (`/escolas`).
    -   A nova escola "EMEF Teste Inovador" aparece na lista.
    -   Um novo registro é criado na tabela `ESCOLA` com os dados inseridos.
-   **Resultado:** (A ser preenchido após execução: Passou/Falhou)

--- 

**Teste ID:** ECM-TEST-005
**Título:** Edição de Escola Existente
**Prioridade:** Média
**Status:** Pendente

-   **Descrição:** Verificar se um usuário ADMIN/GESTOR consegue editar os dados de uma escola existente.
-   **Pré-condições:**
    -   Aplicação rodando.
    -   Usuário logado com perfil ADMIN ou GESTOR.
    -   Escola "EMEF Prof. João Silva" (ID: 1) existe no banco.
-   **Dados de Entrada (Formulário de Edição):**
    -   Nome: "EMEF Prof. João Silva (Renomeada)"
    -   Cidade: "Guarulhos"
    -   Ativo: Não
-   **Procedimento:**
    1.  Realizar login como `admin@escolaclima.com`.
    2.  Navegar para a página "Escolas".
    3.  Encontrar a escola "EMEF Prof. João Silva" na lista e clicar em "Editar".
    4.  Alterar os campos Nome, Cidade e Ativo conforme os dados de entrada.
    5.  Clicar no botão "Salvar".
-   **Dados de Saída Esperados:**
    -   Usuário é redirecionado para a lista de escolas.
    -   A escola aparece na lista com o nome "EMEF Prof. João Silva (Renomeada)", cidade "Guarulhos" e status inativo.
    -   O registro da escola ID 1 na tabela `ESCOLA` é atualizado.
-   **Resultado:** (A ser preenchido após execução: Passou/Falhou)

--- 

**Teste ID:** ECM-TEST-006
**Título:** Recebimento de Leitura de Sensor e Geração de Alerta de Temperatura Elevada
**Prioridade:** Alta
**Status:** Pendente

-   **Descrição:** Verificar se o sistema recebe uma leitura de sensor via RabbitMQ e gera um alerta de "Temperatura Elevada".
-   **Pré-condições:**
    -   Aplicação rodando.
    -   RabbitMQ rodando e acessível.
    -   Sensor com ID 1 (associado à Escola 1) existe no banco.
    -   Usuário logado (qualquer perfil para visualização no dashboard).
-   **Dados de Entrada (Mensagem JSON para RabbitMQ - fila `sensor.readings`):**
    ```json
    {
      "sensor_id": 1,
      "temperature": 29.5,
      "humidity": 60.0,
      "timestamp": "2025-06-08T10:00:00"
    }
    ```
-   **Procedimento:**
    1.  Realizar login na aplicação (ex: `admin@escolaclima.com`).
    2.  Navegar para o Dashboard.
    3.  Utilizar uma ferramenta ou script para enviar a mensagem JSON para a fila `sensor.readings` no RabbitMQ.
    4.  Aguardar alguns segundos e atualizar a página do Dashboard e/ou a página de Alertas (`/alertas`).
-   **Dados de Saída Esperados:**
    -   Uma nova leitura é registrada na tabela `LEITURA` para o sensor ID 1 com temperatura 29.5°C.
    -   Um novo alerta é registrado na tabela `ALERTA` associado a essa leitura.
    -   Tipo do Alerta: "Temperatura Elevada".
    -   Nível do Alerta: "Médio".
    -   Mensagem do Alerta: (Mensagem padrão ou gerada pela IA, se configurada, contendo a informação da temperatura).
    -   O novo alerta aparece na lista de alertas recentes no Dashboard e na página de Alertas.
-   **Resultado:** (A ser preenchido após execução: Passou/Falhou)

--- 

**Teste ID:** ECM-TEST-007
**Título:** Geração de Alerta de Calor Extremo
**Prioridade:** Alta
**Status:** Pendente

-   **Descrição:** Verificar se o sistema gera um alerta de "Calor Extremo" quando a temperatura é crítica.
-   **Pré-condições:**
    -   Aplicação rodando.
    -   RabbitMQ rodando.
    -   Sensor com ID 3 (associado à Escola 1, Pátio) existe no banco.
-   **Dados de Entrada (Mensagem JSON para RabbitMQ - fila `sensor.readings`):**
    ```json
    {
      "sensor_id": 3,
      "temperature": 36.0,
      "humidity": 55.0,
      "timestamp": "2025-06-08T14:00:00"
    }
    ```
-   **Procedimento:**
    1.  Realizar login na aplicação.
    2.  Enviar a mensagem JSON para a fila `sensor.readings`.
    3.  Atualizar o Dashboard e/ou a página de Alertas.
-   **Dados de Saída Esperados:**
    -   Nova leitura registrada para o sensor ID 3 com temperatura 36.0°C.
    -   Novo alerta registrado.
    -   Tipo do Alerta: "Calor Extremo".
    -   Nível do Alerta: "Crítico".
    -   Mensagem do Alerta: (Mensagem padrão ou IA, indicando risco extremo).
    -   Alerta visível nas interfaces.
-   **Resultado:** (A ser preenchido após execução: Passou/Falhou)

--- 

**Teste ID:** ECM-TEST-008
**Título:** Geração de Alerta de Umidade Baixa
**Prioridade:** Média
**Status:** Pendente

-   **Descrição:** Verificar se o sistema gera um alerta de "Umidade Baixa".
-   **Pré-condições:**
    -   Aplicação rodando.
    -   RabbitMQ rodando.
    -   Sensor com ID 1 existe no banco.
-   **Dados de Entrada (Mensagem JSON para RabbitMQ - fila `sensor.readings`):**
    ```json
    {
      "sensor_id": 1,
      "temperature": 25.0,
      "humidity": 25.0,
      "timestamp": "2025-06-08T11:00:00"
    }
    ```
-   **Procedimento:**
    1.  Realizar login na aplicação.
    2.  Enviar a mensagem JSON para a fila `sensor.readings`.
    3.  Atualizar o Dashboard e/ou a página de Alertas.
-   **Dados de Saída Esperados:**
    -   Nova leitura registrada para o sensor ID 1 com umidade 25.0%.
    -   Novo alerta registrado.
    -   Tipo do Alerta: "Umidade Baixa".
    -   Nível do Alerta: "Médio".
    -   Mensagem do Alerta: (Mensagem padrão ou IA, indicando desconforto respiratório).
    -   Alerta visível nas interfaces.
-   **Resultado:** (A ser preenchido após execução: Passou/Falhou)

--- 

**Teste ID:** ECM-TEST-009
**Título:** Visualização e Filtragem de Alertas
**Prioridade:** Média
**Status:** Pendente

-   **Descrição:** Verificar se a página de alertas exibe os alertas corretamente e se os filtros funcionam.
-   **Pré-condições:**
    -   Aplicação rodando.
    -   Vários alertas de diferentes tipos, níveis e status existem no banco (gerados pelos testes anteriores ou via `dados-exemplo.sql`).
    -   Usuário logado.
-   **Dados de Entrada (Filtros):**
    -   Filtro por Nível: "Crítico"
    -   Filtro por Tipo: "Calor Extremo"
    -   Filtro por Status: "Emitido"
-   **Procedimento:**
    1.  Realizar login.
    2.  Navegar para a página "Alertas".
    3.  Verificar se todos os alertas são listados inicialmente.
    4.  Aplicar o filtro de Nível = "Crítico". Verificar se apenas alertas críticos são exibidos.
    5.  Limpar o filtro. Aplicar o filtro de Tipo = "Calor Extremo". Verificar se apenas alertas desse tipo são exibidos.
    6.  Limpar o filtro. Aplicar o filtro de Status = "Emitido". Verificar se apenas alertas com esse status são exibidos.
-   **Dados de Saída Esperados:**
    -   A lista de alertas é exibida corretamente.
    -   Os filtros aplicados restringem a lista de alertas conforme o esperado.
-   **Resultado:** (A ser preenchido após execução: Passou/Falhou)

--- 

**Teste ID:** ECM-TEST-010
**Título:** Geração de Mensagem de Alerta com IA (Opcional)
**Prioridade:** Média
**Status:** Pendente

-   **Descrição:** Verificar se, com a IA configurada, a mensagem do alerta é gerada pelo serviço de IA.
-   **Pré-condições:**
    -   Aplicação rodando.
    -   RabbitMQ rodando.
    -   Sensor com ID 1 existe no banco.
    -   `spring.ai.openai.api-key` configurado com uma chave válida no `application-local.yml`.
    -   `escola-clima.ai.habilitado` = `true` (se houver tal configuração, ou garantir que `AIAlertService` seja injetado).
-   **Dados de Entrada (Mensagem JSON para RabbitMQ - fila `sensor.readings`):**
    ```json
    {
      "sensor_id": 1,
      "temperature": 33.0,
      "humidity": 75.0,
      "timestamp": "2025-06-08T15:00:00"
    }
    ```
-   **Procedimento:**
    1.  Realizar login na aplicação.
    2.  Enviar a mensagem JSON para a fila `sensor.readings`.
    3.  Aguardar e verificar o novo alerta gerado (no Dashboard ou página de Alertas).
-   **Dados de Saída Esperados:**
    -   Um novo alerta é gerado (provavelmente "Índice de Calor Elevado" ou similar).
    -   A mensagem do alerta é mais detalhada e contextualizada do que a mensagem padrão, contendo sugestões de ações (característica da IA).
    -   Verificar nos logs da aplicação se houve uma chamada bem-sucedida ao `AIAlertService` ou à API da OpenAI.
-   **Resultado:** (A ser preenchido após execução: Passou/Falhou)

--- 

## 6. Status dos Testes

| Teste ID   | Título                                                              | Status    | Resultado (se realizado) |
| :--------- | :------------------------------------------------------------------ | :-------- | :----------------------- |
| ECM-TEST-001 | Login com OAuth2 (Google)                                           | Pendente  |                          |
| ECM-TEST-002 | Login Local com Perfil ADMIN                                        | Pendente  |                          |
| ECM-TEST-003 | Acesso Negado a Funcionalidade Restrita                             | Pendente  |                          |
| ECM-TEST-004 | Cadastro de Nova Escola                                             | Pendente  |                          |
| ECM-TEST-005 | Edição de Escola Existente                                          | Pendente  |                          |
| ECM-TEST-006 | Recebimento de Leitura e Alerta de Temperatura Elevada              | Pendente  |                          |
| ECM-TEST-007 | Geração de Alerta de Calor Extremo                                  | Pendente  |                          |
| ECM-TEST-008 | Geração de Alerta de Umidade Baixa                                  | Pendente  |                          |
| ECM-TEST-009 | Visualização e Filtragem de Alertas                                 | Pendente  |                          |
| ECM-TEST-010 | Geração de Mensagem de Alerta com IA (Opcional)                     | Pendente  |                          |

*(Esta tabela deve ser atualizada conforme os testes são executados)*

