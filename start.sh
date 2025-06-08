#!/bin/bash

# Script de Inicialização - Green Light Monitor
# Este script configura o ambiente e inicia a aplicação

set -e

echo "🌡️ Green Light Monitor - Script de Inicialização"
echo "=================================================="

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Função para imprimir mensagens coloridas
print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

# Verificar se Java está instalado
check_java() {
    print_info "Verificando instalação do Java..."
    if command -v java &> /dev/null; then
        JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
        print_success "Java encontrado: $JAVA_VERSION"
        
        # Verificar se é Java 11+
        MAJOR_VERSION=$(echo $JAVA_VERSION | cut -d. -f1)
        if [ "$MAJOR_VERSION" -ge 11 ]; then
            print_success "Versão do Java compatível (11+)"
        else
            print_error "Java 11 ou superior é necessário. Versão atual: $JAVA_VERSION"
            exit 1
        fi
    else
        print_error "Java não encontrado. Por favor, instale Java 11 ou superior."
        exit 1
    fi
}

# Verificar se Maven está instalado
check_maven() {
    print_info "Verificando instalação do Maven..."
    if command -v mvn &> /dev/null; then
        MVN_VERSION=$(mvn -version | head -n 1)
        print_success "Maven encontrado: $MVN_VERSION"
    else
        print_error "Maven não encontrado. Por favor, instale Maven 3.6 ou superior."
        exit 1
    fi
}

# Verificar se RabbitMQ está rodando
check_rabbitmq() {
    print_info "Verificando RabbitMQ..."
    if command -v rabbitmqctl &> /dev/null; then
        if rabbitmqctl status &> /dev/null; then
            print_success "RabbitMQ está rodando"
        else
            print_warning "RabbitMQ está instalado mas não está rodando"
            print_info "Tentando iniciar RabbitMQ..."
            if sudo systemctl start rabbitmq-server 2>/dev/null; then
                print_success "RabbitMQ iniciado com sucesso"
            else
                print_warning "Não foi possível iniciar RabbitMQ automaticamente"
                print_info "Execute manualmente: sudo systemctl start rabbitmq-server"
            fi
        fi
    else
        print_warning "RabbitMQ não encontrado. A aplicação funcionará sem mensageria."
        print_info "Para instalar RabbitMQ: sudo apt-get install rabbitmq-server"
    fi
}

# Configurar arquivo de propriedades
setup_config() {
    print_info "Configurando arquivo de propriedades..."
    
    if [ ! -f "src/main/resources/application-local.yml" ]; then
        print_info "Criando arquivo de configuração local..."
        cp src/main/resources/application-example.yml src/main/resources/application-local.yml
        print_success "Arquivo application-local.yml criado"
        print_warning "Edite o arquivo src/main/resources/application-local.yml com suas configurações"
    else
        print_success "Arquivo de configuração local já existe"
    fi
}

# Compilar o projeto
compile_project() {
    print_info "Compilando o projeto..."
    if mvn clean compile -q; then
        print_success "Projeto compilado com sucesso"
    else
        print_error "Erro na compilação do projeto"
        exit 1
    fi
}

# Executar testes
run_tests() {
    if [ "$1" = "--skip-tests" ]; then
        print_warning "Testes ignorados conforme solicitado"
        return
    fi
    
    print_info "Executando testes..."
    if mvn test -q; then
        print_success "Todos os testes passaram"
    else
        print_warning "Alguns testes falharam, mas continuando..."
    fi
}

# Iniciar a aplicação
start_application() {
    print_info "Iniciando a aplicação..."
    print_info "A aplicação estará disponível em: http://localhost:8080"
    print_info "Console H2 (se habilitado): http://localhost:8080/h2-console"
    print_info "Para parar a aplicação, pressione Ctrl+C"
    echo ""
    
    # Verificar se deve usar perfil específico
    PROFILE=""
    if [ -f "src/main/resources/application-local.yml" ]; then
        PROFILE="-Dspring.profiles.active=local"
    fi
    
    mvn spring-boot:run $PROFILE
}

# Função de ajuda
show_help() {
    echo "Uso: $0 [opções]"
    echo ""
    echo "Opções:"
    echo "  --help          Mostra esta ajuda"
    echo "  --skip-tests    Pula a execução dos testes"
    echo "  --check-only    Apenas verifica dependências, não inicia a aplicação"
    echo "  --setup-only    Apenas configura o ambiente, não inicia a aplicação"
    echo ""
    echo "Exemplos:"
    echo "  $0                    # Execução completa"
    echo "  $0 --skip-tests       # Execução sem testes"
    echo "  $0 --check-only       # Apenas verificação"
    echo ""
}

# Função principal
main() {
    # Verificar argumentos
    SKIP_TESTS=false
    CHECK_ONLY=false
    SETUP_ONLY=false
    
    for arg in "$@"; do
        case $arg in
            --help)
                show_help
                exit 0
                ;;
            --skip-tests)
                SKIP_TESTS=true
                ;;
            --check-only)
                CHECK_ONLY=true
                ;;
            --setup-only)
                SETUP_ONLY=true
                ;;
            *)
                print_error "Argumento desconhecido: $arg"
                show_help
                exit 1
                ;;
        esac
    done
    
    # Verificar dependências
    check_java
    check_maven
    check_rabbitmq
    
    if [ "$CHECK_ONLY" = true ]; then
        print_success "Verificação de dependências concluída"
        exit 0
    fi
    
    # Configurar ambiente
    setup_config
    compile_project
    
    if [ "$SETUP_ONLY" = true ]; then
        print_success "Configuração do ambiente concluída"
        exit 0
    fi
    
    # Executar testes
    if [ "$SKIP_TESTS" = true ]; then
        run_tests --skip-tests
    else
        run_tests
    fi
    
    # Iniciar aplicação
    start_application
}

# Verificar se o script está sendo executado do diretório correto
if [ ! -f "pom.xml" ]; then
    print_error "Este script deve ser executado do diretório raiz do projeto (onde está o pom.xml)"
    exit 1
fi

# Executar função principal
main "$@"

