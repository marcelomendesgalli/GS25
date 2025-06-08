# Green Light Monitor

Sistema inteligente e acessível para prevenção de riscos climáticos em escolas públicas da cidade de São Paulo, com foco inicial em altas temperaturas.

## 📋 Descrição do Projeto

O **Green Light Monitor** é uma plataforma integrada de monitoramento ambiental que envia alertas automáticos em situações de calor extremo, auxiliando gestores escolares na tomada de decisões preventivas. Com o aumento de ondas de calor no Brasil, diversas escolas públicas não possuem climatização adequada, e este projeto visa fornecer informações em tempo real para gestores, professores e coordenadores.

### 🎯 Objetivos

- **Monitoramento em tempo real** das condições climáticas nas escolas
- **Alertas automáticos** para situações de risco
- **Dashboard intuitivo** para visualização de dados
- **Tomada de decisão preventiva** (liberação de alunos, hidratação, etc.)
- **Comunicação clara** através de alertas e dashboards

## 🏗️ Arquitetura do Sistema

### Tecnologias Utilizadas

- **Backend**: Spring Boot 3.2.0 + Spring MVC
- **Frontend**: Thymeleaf + Bootstrap 5 + JavaScript
- **Banco de Dados**: H2 (desenvolvimento) / PostgreSQL (produção)
- **Autenticação**: Spring Security + OAuth2 (Google, GitHub)
- **Mensageria**: RabbitMQ para recebimento de dados de sensores
- **IA**: Spring AI + OpenAI para alertas personalizados (opcional)
- **Build**: Maven
- **Java**: 11+

### Componentes Principais

1. **Sistema de Monitoramento**: Recebe dados de sensores via RabbitMQ
2. **Sistema de Alertas**: Processa dados e gera alertas inteligentes
3. **Dashboard Web**: Interface para visualização e gestão
4. **Sistema de Notificações**: Envia alertas via diferentes canais
5. **IA Integrada**: Gera mensagens personalizadas e análises

## 📊 Modelo de Dados

### Entidades Principais

#### Escola
- `id`: Identificador único
- `nome`: Nome completo da escola
- `cidade`: Cidade onde está localizada
- `estado`: UF
- `ativo`: Status da escola no sistema

#### Sensor
- `id`: Identificador único
- `idEscola`: Referência à escola
- `localizacao`: Local de instalação (ex: "Sala 4")
- `ativo`: Status operacional
- `tipo`: Tipo de sensor
- `descricao`: Observações adicionais

#### Leitura
- `id`: Identificador único
- `idSensor`: Referência ao sensor
- `temperatura`: Valor em graus Celsius
- `umidade`: Valor em percentual
- `timestamp`: Data e hora da leitura

#### Alerta
- `id`: Identificador único
- `idLeitura`: Referência à leitura
- `tipo`: Tipo de alerta (ex: "Calor Extremo")
- `mensagem`: Mensagem explicativa
- `nivel`: Classificação ("Alto", "Crítico", etc.)
- `status`: Situação atual
- `timestamp`: Data e hora de criação

## 🚀 Instalação e Configuração

### Pré-requisitos

- Java 11 ou superior
- Maven 3.6+
- RabbitMQ Server
- PostgreSQL (para produção)

### 1. Clone o Repositório

```bash
git clone <repository-url>
cd escola-clima-monitor
```

### 2. Configuração do Banco de Dados

#### Para Desenvolvimento (H2)
O projeto está configurado para usar H2 em memória por padrão. Nenhuma configuração adicional é necessária.

#### Para Produção (PostgreSQL)
Edite o arquivo `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/escola_clima_monitor
    username: seu_usuario
    password: sua_senha
    driver-class-name: org.postgresql.Driver
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
```

### 3. Configuração do RabbitMQ

Instale e configure o RabbitMQ:

```bash
# Ubuntu/Debian
sudo apt-get install rabbitmq-server

# Iniciar o serviço
sudo systemctl start rabbitmq-server
sudo systemctl enable rabbitmq-server

# Habilitar interface web (opcional)
sudo rabbitmq-plugins enable rabbitmq_management
```

Configure as credenciais no `application.yml`:

```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
```

### 4. Configuração OAuth2

#### Google OAuth2
1. Acesse o [Google Cloud Console](https://console.cloud.google.com/)
2. Crie um novo projeto ou selecione um existente
3. Habilite a Google+ API
4. Crie credenciais OAuth2
5. Configure as URLs de redirecionamento:
   - `http://localhost:8080/login/oauth2/code/google`

#### GitHub OAuth2
1. Acesse [GitHub Developer Settings](https://github.com/settings/developers)
2. Crie uma nova OAuth App
3. Configure a Authorization callback URL:
   - `http://localhost:8080/login/oauth2/code/github`

Adicione as credenciais no `application.yml`:

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: seu_google_client_id
            client-secret: seu_google_client_secret
          github:
            client-id: seu_github_client_id
            client-secret: seu_github_client_secret
```

### 5. Configuração Spring AI (Opcional)

Para habilitar alertas personalizados com IA:

```yaml
spring:
  ai:
    openai:
      api-key: sua_openai_api_key
```

### 6. Compilação e Execução

```bash
# Compilar o projeto
mvn clean compile

# Executar a aplicação
mvn spring-boot:run
```

A aplicação estará disponível em: `http://localhost:8080`

## 🔧 Configurações Avançadas

### Variáveis de Ambiente

Você pode configurar a aplicação usando variáveis de ambiente:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/escola_clima_monitor
export SPRING_DATASOURCE_USERNAME=usuario
export SPRING_DATASOURCE_PASSWORD=senha
export SPRING_RABBITMQ_HOST=localhost
export SPRING_RABBITMQ_USERNAME=guest
export SPRING_RABBITMQ_PASSWORD=guest
export SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_ID=google_client_id
export SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_SECRET=google_client_secret
export SPRING_AI_OPENAI_API_KEY=openai_api_key
```

### Perfis de Execução

#### Desenvolvimento
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

#### Produção
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

## 📡 API e Integração

### Envio de Dados de Sensores

Os sensores devem enviar dados para a fila RabbitMQ `sensor.readings` no formato:

```json
{
  "sensor_id": 1,
  "temperature": 28.5,
  "humidity": 65.2,
  "timestamp": "2024-01-15T14:30:00",
  "device_id": "SENSOR_001",
  "battery_level": 85,
  "signal_strength": -45,
  "location": "Sala 4"
}
```

### Endpoints REST

#### Escolas
- `GET /api/escolas` - Lista escolas
- `POST /api/escolas` - Cria escola
- `GET /api/escolas/{id}` - Busca escola por ID
- `PUT /api/escolas/{id}` - Atualiza escola
- `DELETE /api/escolas/{id}` - Remove escola

#### Sensores
- `GET /api/sensores` - Lista sensores
- `POST /api/sensores` - Cria sensor
- `GET /api/sensores/{id}` - Busca sensor por ID

#### Leituras
- `GET /api/leituras` - Lista leituras
- `GET /api/leituras/sensor/{id}` - Leituras por sensor

#### Alertas
- `GET /api/alertas` - Lista alertas
- `GET /api/alertas/ativos` - Alertas ativos
- `PUT /api/alertas/{id}/status` - Atualiza status

## 🎨 Interface Web

### Páginas Principais

1. **Home** (`/`) - Página inicial pública
2. **Login** (`/login`) - Autenticação OAuth2
3. **Dashboard** (`/dashboard`) - Painel principal
4. **Escolas** (`/escolas`) - Gestão de escolas
5. **Sensores** (`/sensores`) - Gestão de sensores
6. **Alertas** (`/alertas`) - Visualização de alertas
7. **Relatórios** (`/relatorios`) - Análises e relatórios

### Funcionalidades

- **Dashboard em tempo real** com estatísticas
- **CRUD completo** para escolas e sensores
- **Visualização de alertas** com filtros
- **Gráficos e métricas** de monitoramento
- **Interface responsiva** para mobile
- **Notificações em tempo real**

## 🤖 Inteligência Artificial

### Funcionalidades de IA

1. **Alertas Personalizados**: Mensagens contextualizadas baseadas em dados históricos
2. **Análise de Padrões**: Identificação de tendências climáticas
3. **Recomendações Preventivas**: Sugestões de ações baseadas em IA
4. **Avaliação de Riscos**: Análise inteligente de condições críticas

### Configuração

A IA é opcional e requer uma chave da API OpenAI. Sem a configuração, o sistema usa mensagens padrão.

## 🔒 Segurança

### Autenticação e Autorização

- **OAuth2** com Google e GitHub
- **Roles baseadas em perfis**: ADMIN, GESTOR, USUARIO
- **Proteção CSRF** habilitada
- **Sessões seguras** com Spring Security

### Níveis de Acesso

- **ADMIN**: Acesso completo ao sistema
- **GESTOR**: Gestão de escolas e visualização de dados
- **USUARIO**: Visualização de dados apenas

## 📊 Monitoramento e Logs

### Logs da Aplicação

Os logs são configurados com diferentes níveis:

```yaml
logging:
  level:
    com.escolaclima.monitor: DEBUG
    org.springframework.security: INFO
    org.springframework.amqp: INFO
```

### Métricas

- **Leituras processadas por minuto**
- **Alertas gerados por dia**
- **Tempo de resposta da aplicação**
- **Status dos sensores**

## 🧪 Testes

### Executar Testes

```bash
# Todos os testes
mvn test

# Testes específicos
mvn test -Dtest=EscolaServiceTest
```

### Cobertura de Testes

```bash
mvn jacoco:report
```

## 🚀 Deploy

### Docker

```dockerfile
FROM openjdk:11-jre-slim
COPY target/escola-clima-monitor-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Docker Compose

```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - postgres
      - rabbitmq
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/escola_clima_monitor
      - SPRING_RABBITMQ_HOST=rabbitmq

  postgres:
    image: postgres:13
    environment:
      POSTGRES_DB: escola_clima_monitor
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: password

  rabbitmq:
    image: rabbitmq:3-management
    ports:
      - "5672:5672"
      - "15672:15672"
```

## 📚 Documentação Adicional

### Estrutura do Projeto

```
escola-clima-monitor/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/escolaclima/monitor/
│   │   │       ├── config/          # Configurações
│   │   │       ├── controller/      # Controllers MVC
│   │   │       ├── dto/             # Data Transfer Objects
│   │   │       ├── entity/          # Entidades JPA
│   │   │       ├── repository/      # Repositórios
│   │   │       └── service/         # Serviços de negócio
│   │   └── resources/
│   │       ├── static/              # CSS, JS, imagens
│   │       ├── templates/           # Templates Thymeleaf
│   │       └── application.yml      # Configurações
│   └── test/                        # Testes unitários
├── pom.xml                          # Dependências Maven
└── README.md                        # Este arquivo
```

## 🙏 Integrantes

- Lucas Bastos RM553771
- Erick Lopes RM553927
- Marcelo Galli RM553654


