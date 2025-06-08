package com.greenlight.monitor.service;

import com.greenlight.monitor.entity.Alerta;
import com.greenlight.monitor.entity.Leitura;
import com.greenlight.monitor.repository.AlertaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Serviço para operações CRUD da entidade Alerta e processamento inteligente de alertas.
 */
@Service
@Transactional
public class AlertaService {

    private static final Logger logger = LoggerFactory.getLogger(AlertaService.class);

    @Autowired
    private AlertaRepository alertaRepository;

    @Autowired(required = false)
    private AIAlertService aiAlertService;

    @Autowired
    private NotificationService notificationService;

    // Limites para alertas
    private static final BigDecimal TEMP_CRITICA = BigDecimal.valueOf(35.0);
    private static final BigDecimal TEMP_ALTA = BigDecimal.valueOf(30.0);
    private static final BigDecimal TEMP_ELEVADA = BigDecimal.valueOf(28.0);
    private static final BigDecimal UMIDADE_BAIXA = BigDecimal.valueOf(30.0);
    private static final BigDecimal UMIDADE_ALTA = BigDecimal.valueOf(80.0);

    /**
     * Processa uma leitura e gera alertas conforme necessário
     */
    public void processarLeitura(Leitura leitura) {
        try {
            logger.debug("Processando leitura para alertas: Sensor {}, Temp: {}°C, Umidade: {}%",
                leitura.getSensor().getId(), leitura.getTemperatura(), leitura.getUmidade());

            // Verificar alertas de temperatura
            processarAlertasTemperatura(leitura);

            // Verificar alertas de umidade
            processarAlertasUmidade(leitura);

            // Verificar alertas combinados (temperatura + umidade)
            processarAlertasCombinados(leitura);

        } catch (Exception e) {
            logger.error("Erro ao processar alertas para leitura {}: {}", leitura.getId(), e.getMessage());
        }
    }

    /**
     * Processa alertas relacionados à temperatura
     */
    private void processarAlertasTemperatura(Leitura leitura) {
        BigDecimal temperatura = leitura.getTemperatura();

        if (temperatura.compareTo(TEMP_CRITICA) >= 0) {
            criarAlerta(leitura, "Calor Extremo", "Crítico", 
                "Temperatura crítica detectada. Risco extremo para a saúde dos estudantes.");
        } else if (temperatura.compareTo(TEMP_ALTA) >= 0) {
            criarAlerta(leitura, "Calor Intenso", "Alto", 
                "Temperatura muito alta detectada. Medidas preventivas necessárias.");
        } else if (temperatura.compareTo(TEMP_ELEVADA) >= 0) {
            criarAlerta(leitura, "Temperatura Elevada", "Médio", 
                "Temperatura acima do confortável. Monitoramento recomendado.");
        }
    }

    /**
     * Processa alertas relacionados à umidade
     */
    private void processarAlertasUmidade(Leitura leitura) {
        BigDecimal umidade = leitura.getUmidade();

        if (umidade.compareTo(UMIDADE_BAIXA) <= 0) {
            criarAlerta(leitura, "Umidade Baixa", "Médio", 
                "Umidade muito baixa detectada. Pode causar desconforto respiratório.");
        } else if (umidade.compareTo(UMIDADE_ALTA) >= 0) {
            criarAlerta(leitura, "Umidade Alta", "Médio", 
                "Umidade muito alta detectada. Ambiente pode ficar abafado.");
        }
    }

    /**
     * Processa alertas combinados (temperatura + umidade)
     */
    private void processarAlertasCombinados(Leitura leitura) {
        BigDecimal temperatura = leitura.getTemperatura();
        BigDecimal umidade = leitura.getUmidade();

        // Índice de calor simplificado
        if (temperatura.compareTo(TEMP_ELEVADA) >= 0 && umidade.compareTo(BigDecimal.valueOf(70)) >= 0) {
            criarAlerta(leitura, "Índice de Calor Elevado", "Alto", 
                "Combinação de temperatura e umidade alta. Sensação térmica muito desconfortável.");
        }
    }

    /**
     * Cria um novo alerta
     */
    private void criarAlerta(Leitura leitura, String tipo, String nivel, String mensagemPadrao) {
        try {
            // Verificar se já existe alerta similar recente para evitar spam
            if (existeAlertaRecente(leitura.getSensor().getId(), tipo)) {
                logger.debug("Alerta similar já existe para sensor {}, tipo {}", 
                    leitura.getSensor().getId(), tipo);
                return;
            }

            Alerta alerta = new Alerta();
            alerta.setLeitura(leitura);
            alerta.setTipo(tipo);
            alerta.setNivel(nivel);
            alerta.setStatus("Emitido");
            alerta.setTimestamp(LocalDateTime.now());

            // Gerar mensagem personalizada com IA (se disponível)
            String mensagem = mensagemPadrao;
            if (aiAlertService != null) {
                try {
                    mensagem = aiAlertService.generatePersonalizedAlert(leitura, tipo, nivel);
                } catch (Exception e) {
                    logger.warn("Erro ao gerar mensagem com IA, usando mensagem padrão: {}", e.getMessage());
                }
            }
            alerta.setMensagem(mensagem);

            // Salvar alerta
            alerta = save(alerta);
            logger.info("Alerta criado: ID {}, Tipo: {}, Nível: {}", alerta.getId(), tipo, nivel);

            // Enviar notificação
            notificationService.sendAlert(alerta);

        } catch (Exception e) {
            logger.error("Erro ao criar alerta: {}", e.getMessage());
        }
    }

    /**
     * Verifica se existe alerta recente similar
     */
    private boolean existeAlertaRecente(Long sensorId, String tipo) {
        LocalDateTime limite = LocalDateTime.now().minusMinutes(30); // 30 minutos
        return alertaRepository.existsAlertaRecente(sensorId, tipo, limite);
    }

    /**
     * Busca todos os alertas
     */
    @Transactional(readOnly = true)
    public List<Alerta> findAll() {
        return alertaRepository.findAll();
    }

    /**
     * Busca alerta por ID
     */
    @Transactional(readOnly = true)
    public Optional<Alerta> findById(Long id) {
        return alertaRepository.findById(id);
    }

    /**
     * Busca alerta por ID (lança exceção se não encontrar)
     */
    @Transactional(readOnly = true)
    public Alerta getById(Long id) {
        return alertaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alerta não encontrado com ID: " + id));
    }

    /**
     * Busca alertas ativos
     */
    @Transactional(readOnly = true)
    public List<Alerta> findAlertasAtivos() {
        return alertaRepository.findByStatusIn(List.of("Emitido", "Em Andamento"));
    }

    /**
     * Busca alertas por nível
     */
    @Transactional(readOnly = true)
    public List<Alerta> findByNivel(String nivel) {
        return alertaRepository.findByNivel(nivel);
    }

    /**
     * Busca alertas críticos ativos
     */
    @Transactional(readOnly = true)
    public List<Alerta> findAlertasCriticosAtivos() {
        return alertaRepository.findAlertasCriticosAtivos();
    }

    /**
     * Busca alertas recentes (últimas 24 horas)
     */
    @Transactional(readOnly = true)
    public List<Alerta> findAlertasRecentes() {
        LocalDateTime limite = LocalDateTime.now().minusHours(24);
        return alertaRepository.findAlertasRecentes(limite);
    }

    /**
     * Busca alertas por escola
     */
    @Transactional(readOnly = true)
    public List<Alerta> findByEscolaId(Long escolaId) {
        return alertaRepository.findByEscolaId(escolaId);
    }

    /**
     * Busca alertas paginados com filtros
     */
    @Transactional(readOnly = true)
    public Page<Alerta> findByFiltros(String tipo, String nivel, String status, 
                                     LocalDateTime inicio, LocalDateTime fim, 
                                     Pageable pageable) {
        return alertaRepository.findByFiltros(tipo, nivel, status, inicio, fim, pageable);
    }

    /**
     * Salva um alerta
     */
    public Alerta save(Alerta alerta) {
        return alertaRepository.save(alerta);
    }

    /**
     * Cria um novo alerta
     */
    public Alerta create(Alerta alerta) {
        alerta.setId(null);
        alerta.setTimestamp(LocalDateTime.now());
        return save(alerta);
    }

    /**
     * Atualiza status de um alerta
     */
    public Alerta updateStatus(Long id, String novoStatus) {
        Alerta alerta = getById(id);
        alerta.setStatus(novoStatus);
        return save(alerta);
    }

    /**
     * Conta alertas ativos
     */
    @Transactional(readOnly = true)
    public long countAlertasAtivos() {
        return alertaRepository.countAlertasAtivos();
    }

    /**
     * Conta alertas críticos ativos
     */
    @Transactional(readOnly = true)
    public long countAlertasCriticosAtivos() {
        return alertaRepository.countAlertasCriticosAtivos();
    }

    /**
     * Remove alertas antigos
     */
    public void limparAlertasAntigos(int diasParaManter) {
        LocalDateTime limite = LocalDateTime.now().minusDays(diasParaManter);
        alertaRepository.deleteAlertasAntigos(limite);
    }
}

