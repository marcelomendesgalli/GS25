package com.greenlight.monitor.service;

import com.greenlight.monitor.dto.AlertDTO;
import com.greenlight.monitor.dto.SensorReadingDTO;
import com.greenlight.monitor.entity.Alerta;
import com.greenlight.monitor.entity.Sensor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Serviço para envio de notificações via RabbitMQ e outros canais.
 */
@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.exchange.alerts:alerts.exchange}")
    private String alertsExchange;

    /**
     * Envia alerta via RabbitMQ
     */
    public void sendAlert(Alerta alerta) {
        try {
            AlertDTO alertDTO = convertToDTO(alerta);
            
            // Determinar routing key baseado no nível do alerta
            String routingKey = determineRoutingKey(alerta.getNivel());
            
            // Enviar para exchange de alertas
            rabbitTemplate.convertAndSend(alertsExchange, routingKey, alertDTO);
            
            logger.info("Alerta enviado via RabbitMQ: ID {}, Tipo: {}, Nível: {}", 
                alerta.getId(), alerta.getTipo(), alerta.getNivel());
            
        } catch (Exception e) {
            logger.error("Erro ao enviar alerta via RabbitMQ: {}", alerta.getId(), e);
        }
    }

    /**
     * Envia notificação de bateria baixa
     */
    public void sendLowBatteryAlert(Sensor sensor, Integer batteryLevel) {
        try {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("sensor_id", sensor.getId());
            metadata.put("battery_level", batteryLevel);
            metadata.put("alert_type", "LOW_BATTERY");
            metadata.put("school_name", sensor.getEscola().getNome());
            metadata.put("sensor_location", sensor.getLocalizacao());

            AlertDTO alertDTO = new AlertDTO();
            alertDTO.setSensorId(sensor.getId());
            alertDTO.setEscolaId(sensor.getEscola().getId());
            alertDTO.setTipo("Bateria Baixa");
            alertDTO.setNivel("Médio");
            alertDTO.setStatus("Emitido");
            alertDTO.setTimestamp(LocalDateTime.now());
            alertDTO.setNomeEscola(sensor.getEscola().getNome());
            alertDTO.setLocalizacaoSensor(sensor.getLocalizacao());
            alertDTO.setMetadata(metadata);
            alertDTO.setMensagem(String.format(
                "Bateria baixa detectada no sensor %s da escola %s. Nível atual: %d%%",
                sensor.getLocalizacao(), sensor.getEscola().getNome(), batteryLevel
            ));

            rabbitTemplate.convertAndSend(alertsExchange, "alerts.maintenance", alertDTO);
            
            logger.info("Alerta de bateria baixa enviado para sensor {}", sensor.getId());
            
        } catch (Exception e) {
            logger.error("Erro ao enviar alerta de bateria baixa para sensor {}: {}", sensor.getId(), e.getMessage());
        }
    }

    /**
     * Envia notificação de sinal fraco
     */
    public void sendWeakSignalAlert(Sensor sensor, Integer signalStrength) {
        try {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("sensor_id", sensor.getId());
            metadata.put("signal_strength", signalStrength);
            metadata.put("alert_type", "WEAK_SIGNAL");
            metadata.put("school_name", sensor.getEscola().getNome());
            metadata.put("sensor_location", sensor.getLocalizacao());

            AlertDTO alertDTO = new AlertDTO();
            alertDTO.setSensorId(sensor.getId());
            alertDTO.setEscolaId(sensor.getEscola().getId());
            alertDTO.setTipo("Sinal Fraco");
            alertDTO.setNivel("Baixo");
            alertDTO.setStatus("Emitido");
            alertDTO.setTimestamp(LocalDateTime.now());
            alertDTO.setNomeEscola(sensor.getEscola().getNome());
            alertDTO.setLocalizacaoSensor(sensor.getLocalizacao());
            alertDTO.setMetadata(metadata);
            alertDTO.setMensagem(String.format(
                "Sinal fraco detectado no sensor %s da escola %s. Intensidade: %d dBm",
                sensor.getLocalizacao(), sensor.getEscola().getNome(), signalStrength
            ));

            rabbitTemplate.convertAndSend(alertsExchange, "alerts.maintenance", alertDTO);
            
            logger.info("Alerta de sinal fraco enviado para sensor {}", sensor.getId());
            
        } catch (Exception e) {
            logger.error("Erro ao enviar alerta de sinal fraco para sensor {}: {}", sensor.getId(), e.getMessage());
        }
    }

    /**
     * Envia notificação de possível movimentação do sensor
     */
    public void sendSensorMovementAlert(Sensor sensor, String newLocation) {
        try {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("sensor_id", sensor.getId());
            metadata.put("expected_location", sensor.getLocalizacao());
            metadata.put("detected_location", newLocation);
            metadata.put("alert_type", "SENSOR_MOVEMENT");
            metadata.put("school_name", sensor.getEscola().getNome());

            AlertDTO alertDTO = new AlertDTO();
            alertDTO.setSensorId(sensor.getId());
            alertDTO.setEscolaId(sensor.getEscola().getId());
            alertDTO.setTipo("Movimentação de Sensor");
            alertDTO.setNivel("Alto");
            alertDTO.setStatus("Emitido");
            alertDTO.setTimestamp(LocalDateTime.now());
            alertDTO.setNomeEscola(sensor.getEscola().getNome());
            alertDTO.setLocalizacaoSensor(sensor.getLocalizacao());
            alertDTO.setMetadata(metadata);
            alertDTO.setMensagem(String.format(
                "Possível movimentação detectada no sensor da escola %s. " +
                "Localização esperada: %s, Localização detectada: %s",
                sensor.getEscola().getNome(), sensor.getLocalizacao(), newLocation
            ));

            rabbitTemplate.convertAndSend(alertsExchange, "alerts.security", alertDTO);
            
            logger.warn("Alerta de movimentação de sensor enviado para sensor {}", sensor.getId());
            
        } catch (Exception e) {
            logger.error("Erro ao enviar alerta de movimentação para sensor {}: {}", sensor.getId(), e.getMessage());
        }
    }

    /**
     * Envia notificação de falha no processamento de mensagem
     */
    public void sendFailedMessageAlert(SensorReadingDTO readingDTO) {
        try {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("failed_reading", readingDTO);
            metadata.put("alert_type", "MESSAGE_PROCESSING_FAILED");
            metadata.put("timestamp", LocalDateTime.now());

            AlertDTO alertDTO = new AlertDTO();
            alertDTO.setSensorId(readingDTO.getSensorId());
            alertDTO.setTipo("Falha no Processamento");
            alertDTO.setNivel("Alto");
            alertDTO.setStatus("Emitido");
            alertDTO.setTimestamp(LocalDateTime.now());
            alertDTO.setMetadata(metadata);
            alertDTO.setMensagem(String.format(
                "Falha no processamento de leitura do sensor %d. " +
                "Dados: Temperatura=%.1f°C, Umidade=%.1f%%",
                readingDTO.getSensorId(), 
                readingDTO.getTemperatura().doubleValue(),
                readingDTO.getUmidade().doubleValue()
            ));

            rabbitTemplate.convertAndSend(alertsExchange, "alerts.system", alertDTO);
            
            logger.error("Alerta de falha no processamento enviado para sensor {}", readingDTO.getSensorId());
            
        } catch (Exception e) {
            logger.error("Erro ao enviar alerta de falha no processamento: {}", e.getMessage());
        }
    }

    /**
     * Envia notificação por email
     */
    public void sendEmailNotification(AlertDTO alertDTO) {
        try {
            // Adicionar destinatários baseado no tipo e nível do alerta
            alertDTO.setCanaisNotificacao(Arrays.asList("email"));
            
            rabbitTemplate.convertAndSend(alertsExchange, "alerts.email", alertDTO);
            
            logger.info("Notificação por email enviada para alerta {}", alertDTO.getAlertId());
            
        } catch (Exception e) {
            logger.error("Erro ao enviar notificação por email: {}", e.getMessage());
        }
    }

    /**
     * Envia notificação por SMS
     */
    public void sendSMSNotification(AlertDTO alertDTO) {
        try {
            // Apenas para alertas críticos
            if (alertDTO.isCritico()) {
                alertDTO.setCanaisNotificacao(Arrays.asList("sms"));
                
                rabbitTemplate.convertAndSend(alertsExchange, "alerts.sms", alertDTO);
                
                logger.info("Notificação por SMS enviada para alerta crítico {}", alertDTO.getAlertId());
            }
            
        } catch (Exception e) {
            logger.error("Erro ao enviar notificação por SMS: {}", e.getMessage());
        }
    }

    /**
     * Converte entidade Alerta para DTO
     */
    private AlertDTO convertToDTO(Alerta alerta) {
        AlertDTO dto = new AlertDTO();
        dto.setAlertId(alerta.getId());
        dto.setLeituraId(alerta.getLeitura().getId());
        dto.setSensorId(alerta.getLeitura().getSensor().getId());
        dto.setEscolaId(alerta.getLeitura().getSensor().getEscola().getId());
        dto.setTipo(alerta.getTipo());
        dto.setMensagem(alerta.getMensagem());
        dto.setNivel(alerta.getNivel());
        dto.setStatus(alerta.getStatus());
        dto.setTimestamp(alerta.getTimestamp());
        dto.setNomeEscola(alerta.getLeitura().getSensor().getEscola().getNome());
        dto.setLocalizacaoSensor(alerta.getLeitura().getSensor().getLocalizacao());
        dto.setTemperatura(alerta.getLeitura().getTemperatura().doubleValue());
        dto.setUmidade(alerta.getLeitura().getUmidade().doubleValue());
        
        return dto;
    }

    /**
     * Determina routing key baseado no nível do alerta
     */
    private String determineRoutingKey(String nivel) {
        switch (nivel.toLowerCase()) {
            case "crítico":
                return "alerts.critical";
            case "alto":
                return "alerts.high";
            case "médio":
                return "alerts.medium";
            case "baixo":
                return "alerts.low";
            default:
                return "alerts.general";
        }
    }
}

