package com.greenlight.monitor.service;

import com.greenlight.monitor.entity.Alerta;
import com.greenlight.monitor.entity.Escola;
import com.greenlight.monitor.entity.Leitura;
import com.greenlight.monitor.entity.Sensor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Serviço de IA para geração de alertas personalizados e análises inteligentes.
 * Utiliza Spring AI com OpenAI para criar mensagens contextualizadas e recomendações.
 */
@Service
@ConditionalOnProperty(name = "spring.ai.openai.api-key")
public class AIAlertService {

    private static final Logger logger = LoggerFactory.getLogger(AIAlertService.class);

    @Autowired(required = false)
    private ChatClient chatClient;

    @Autowired
    private LeituraService leituraService;

    /**
     * Gera mensagem de alerta personalizada usando IA
     */
    public String generatePersonalizedAlert(Leitura leitura, String tipoAlerta, String nivelAlerta) {
        if (chatClient == null) {
            logger.warn("Spring AI não configurado, usando mensagem padrão");
            return generateDefaultAlert(leitura, tipoAlerta, nivelAlerta);
        }

        try {
            String prompt = buildAlertPrompt(leitura, tipoAlerta, nivelAlerta);
            
            ChatResponse response = chatClient.call(new Prompt(prompt));
            String aiMessage = response.getResult().getOutput().getContent();
            
            logger.info("Mensagem de alerta gerada pela IA para sensor {}", leitura.getSensor().getId());
            return aiMessage;
            
        } catch (Exception e) {
            logger.error("Erro ao gerar alerta com IA, usando mensagem padrão: {}", e.getMessage());
            return generateDefaultAlert(leitura, tipoAlerta, nivelAlerta);
        }
    }

    /**
     * Gera recomendações de ações preventivas usando IA
     */
    public String generatePreventiveRecommendations(Escola escola, List<Leitura> leituras) {
        if (chatClient == null) {
            return generateDefaultRecommendations(escola, leituras);
        }

        try {
            String prompt = buildRecommendationsPrompt(escola, leituras);
            
            ChatResponse response = chatClient.call(new Prompt(prompt));
            String recommendations = response.getResult().getOutput().getContent();
            
            logger.info("Recomendações geradas pela IA para escola {}", escola.getId());
            return recommendations;
            
        } catch (Exception e) {
            logger.error("Erro ao gerar recomendações com IA: {}", e.getMessage());
            return generateDefaultRecommendations(escola, leituras);
        }
    }

    /**
     * Analisa padrões climáticos e gera insights usando IA
     */
    public String analyzeClimatePatterns(Escola escola, LocalDateTime inicio, LocalDateTime fim) {
        if (chatClient == null) {
            return "Análise de IA não disponível. Configure a chave da API OpenAI para habilitar esta funcionalidade.";
        }

        try {
            List<Leitura> leituras = leituraService.findByEscolaIdAndTimestampBetween(escola.getId(), inicio, fim);
            
            if (leituras.isEmpty()) {
                return "Não há dados suficientes para análise no período especificado.";
            }

            String prompt = buildAnalysisPrompt(escola, leituras, inicio, fim);
            
            ChatResponse response = chatClient.call(new Prompt(prompt));
            String analysis = response.getResult().getOutput().getContent();
            
            logger.info("Análise de padrões climáticos gerada para escola {}", escola.getId());
            return analysis;
            
        } catch (Exception e) {
            logger.error("Erro ao analisar padrões climáticos: {}", e.getMessage());
            return "Erro ao gerar análise. Tente novamente mais tarde.";
        }
    }

    /**
     * Gera relatório de risco climático usando IA
     */
    public String generateRiskAssessment(Escola escola) {
        if (chatClient == null) {
            return generateDefaultRiskAssessment(escola);
        }

        try {
            // Buscar dados dos últimos 7 dias
            LocalDateTime fim = LocalDateTime.now();
            LocalDateTime inicio = fim.minusDays(7);
            List<Leitura> leituras = leituraService.findByEscolaIdAndTimestampBetween(escola.getId(), inicio, fim);

            String prompt = buildRiskAssessmentPrompt(escola, leituras);
            
            ChatResponse response = chatClient.call(new Prompt(prompt));
            String riskAssessment = response.getResult().getOutput().getContent();
            
            logger.info("Avaliação de risco gerada para escola {}", escola.getId());
            return riskAssessment;
            
        } catch (Exception e) {
            logger.error("Erro ao gerar avaliação de risco: {}", e.getMessage());
            return generateDefaultRiskAssessment(escola);
        }
    }

    /**
     * Constrói prompt para geração de alerta personalizado
     */
    private String buildAlertPrompt(Leitura leitura, String tipoAlerta, String nivelAlerta) {
        Sensor sensor = leitura.getSensor();
        Escola escola = sensor.getEscola();
        
        return String.format("""
            Você é um especialista em monitoramento climático escolar. Gere uma mensagem de alerta clara e acionável.
            
            CONTEXTO:
            - Escola: %s
            - Localização: %s, %s
            - Sensor: %s
            - Tipo de Alerta: %s
            - Nível: %s
            
            DADOS ATUAIS:
            - Temperatura: %.1f°C
            - Umidade: %.1f%%
            - Horário: %s
            
            INSTRUÇÕES:
            1. Crie uma mensagem clara e objetiva (máximo 200 palavras)
            2. Inclua o risco específico para os estudantes
            3. Sugira 2-3 ações imediatas que a escola pode tomar
            4. Use linguagem acessível para gestores escolares
            5. Seja específico sobre a urgência baseada no nível do alerta
            
            Mensagem de alerta:
            """,
            escola.getNome(),
            escola.getCidade(), escola.getEstado(),
            sensor.getLocalizacao(),
            tipoAlerta,
            nivelAlerta,
            leitura.getTemperatura(),
            leitura.getUmidade(),
            leitura.getTimestamp().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        );
    }

    /**
     * Constrói prompt para geração de recomendações preventivas
     */
    private String buildRecommendationsPrompt(Escola escola, List<Leitura> leituras) {
        StringBuilder dadosLeituras = new StringBuilder();
        for (Leitura leitura : leituras.subList(0, Math.min(10, leituras.size()))) {
            dadosLeituras.append(String.format("- %s: %.1f°C, %.1f%% (%s)\n",
                leitura.getTimestamp().format(DateTimeFormatter.ofPattern("dd/MM HH:mm")),
                leitura.getTemperatura(),
                leitura.getUmidade(),
                leitura.getSensor().getLocalizacao()
            ));
        }

        return String.format("""
            Você é um consultor especializado em conforto térmico escolar. Analise os dados e forneça recomendações.
            
            ESCOLA: %s (%s, %s)
            
            LEITURAS RECENTES:
            %s
            
            TAREFA:
            Baseado nos dados, forneça recomendações preventivas específicas para:
            1. Melhorar o conforto térmico dos estudantes
            2. Prevenir riscos relacionados ao calor
            3. Otimizar o ambiente de aprendizagem
            4. Medidas de curto e longo prazo
            
            Seja prático e considere as limitações de uma escola pública.
            """,
            escola.getNome(),
            escola.getCidade(), escola.getEstado(),
            dadosLeituras.toString()
        );
    }

    /**
     * Constrói prompt para análise de padrões climáticos
     */
    private String buildAnalysisPrompt(Escola escola, List<Leitura> leituras, LocalDateTime inicio, LocalDateTime fim) {
        // Calcular estatísticas básicas
        double tempMedia = leituras.stream()
            .mapToDouble(l -> l.getTemperatura().doubleValue())
            .average().orElse(0.0);
        
        double umidadeMedia = leituras.stream()
            .mapToDouble(l -> l.getUmidade().doubleValue())
            .average().orElse(0.0);

        double tempMax = leituras.stream()
            .mapToDouble(l -> l.getTemperatura().doubleValue())
            .max().orElse(0.0);

        double tempMin = leituras.stream()
            .mapToDouble(l -> l.getTemperatura().doubleValue())
            .min().orElse(0.0);

        return String.format("""
            Você é um meteorologista especializado em microclima escolar. Analise os padrões climáticos.
            
            ESCOLA: %s (%s, %s)
            PERÍODO: %s a %s
            TOTAL DE LEITURAS: %d
            
            ESTATÍSTICAS:
            - Temperatura média: %.1f°C
            - Temperatura máxima: %.1f°C
            - Temperatura mínima: %.1f°C
            - Umidade média: %.1f%%
            
            ANÁLISE SOLICITADA:
            1. Identifique padrões e tendências nos dados
            2. Avalie riscos para a saúde dos estudantes
            3. Compare com padrões climáticos típicos da região
            4. Identifique horários críticos do dia
            5. Sugira estratégias de monitoramento
            
            Forneça insights acionáveis para gestores escolares.
            """,
            escola.getNome(),
            escola.getCidade(), escola.getEstado(),
            inicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            fim.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            leituras.size(),
            tempMedia, tempMax, tempMin, umidadeMedia
        );
    }

    /**
     * Constrói prompt para avaliação de risco
     */
    private String buildRiskAssessmentPrompt(Escola escola, List<Leitura> leituras) {
        long leiturasAltas = leituras.stream()
            .mapToLong(l -> l.getTemperatura().compareTo(BigDecimal.valueOf(28)) > 0 ? 1 : 0)
            .sum();

        return String.format("""
            Você é um especialista em saúde pública escolar. Avalie os riscos climáticos.
            
            ESCOLA: %s
            PERÍODO: Últimos 7 dias
            LEITURAS ANALISADAS: %d
            LEITURAS COM TEMPERATURA ELEVADA (>28°C): %d
            
            AVALIAÇÃO SOLICITADA:
            1. Nível de risco atual (Baixo/Médio/Alto/Crítico)
            2. Principais preocupações de saúde
            3. Grupos mais vulneráveis (crianças pequenas, etc.)
            4. Recomendações de monitoramento
            5. Plano de ação para diferentes cenários
            
            Seja específico sobre medidas preventivas e protocolos de emergência.
            """,
            escola.getNome(),
            leituras.size(),
            leiturasAltas
        );
    }

    /**
     * Gera mensagem de alerta padrão quando IA não está disponível
     */
    private String generateDefaultAlert(Leitura leitura, String tipoAlerta, String nivelAlerta) {
        Sensor sensor = leitura.getSensor();
        Escola escola = sensor.getEscola();
        
        return String.format(
            "ALERTA %s: %s detectado na escola %s (%s). " +
            "Temperatura: %.1f°C, Umidade: %.1f%%. " +
            "Recomenda-se verificar as condições do ambiente e tomar medidas preventivas conforme necessário.",
            nivelAlerta.toUpperCase(),
            tipoAlerta,
            escola.getNome(),
            sensor.getLocalizacao(),
            leitura.getTemperatura(),
            leitura.getUmidade()
        );
    }

    /**
     * Gera recomendações padrão quando IA não está disponível
     */
    private String generateDefaultRecommendations(Escola escola, List<Leitura> leituras) {
        return String.format("""
            Recomendações para a escola %s:
            
            1. Monitorar regularmente as condições climáticas
            2. Manter hidratação adequada dos estudantes
            3. Ajustar atividades físicas conforme temperatura
            4. Verificar ventilação dos ambientes
            5. Considerar horários alternativos para atividades externas
            
            Para recomendações personalizadas baseadas em IA, configure a integração com OpenAI.
            """, escola.getNome());
    }

    /**
     * Gera avaliação de risco padrão quando IA não está disponível
     */
    private String generateDefaultRiskAssessment(Escola escola) {
        return String.format("""
            Avaliação de Risco - %s:
            
            Baseado nos dados disponíveis, recomenda-se:
            - Monitoramento contínuo das condições climáticas
            - Implementação de protocolos de calor extremo
            - Treinamento da equipe para situações de emergência
            - Manutenção regular dos sistemas de ventilação
            
            Para análise detalhada com IA, configure a chave da API OpenAI.
            """, escola.getNome());
    }
}

