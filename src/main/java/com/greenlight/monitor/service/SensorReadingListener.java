package com.greenlight.monitor.service;

import com.greenlight.monitor.dto.SensorReadingDTO;
import com.greenlight.monitor.entity.Leitura;
import com.greenlight.monitor.entity.Sensor;
import com.greenlight.monitor.repository.SensorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Serviço para processar mensagens de leituras de sensores recebidas via RabbitMQ.
 */
@Service
public class SensorReadingListener {

    private static final Logger logger = LoggerFactory.getLogger(SensorReadingListener.class);

    @Autowired
    private SensorRepository sensorRepository;

    @Autowired
    private LeituraService leituraService;

    @Autowired
    private AlertaService alertaService;

    @Autowired
    private NotificationService notificationService;

    /**
     * Processa leituras de sensores recebidas via RabbitMQ
     */
    @RabbitListener(queues = "${app.rabbitmq.queue.sensor-readings:sensor.readings}")
    @Transactional
    public void processSensorReading(SensorReadingDTO readingDTO) {
        logger.info("Recebida leitura do sensor: {}", readingDTO);

        try {
            // Validar dados recebidos
            if (!readingDTO.isValidReading()) {
                logger.error("Leitura inválida recebida: {}", readingDTO);
                return;
            }

            // Buscar sensor
            Optional<Sensor> sensorOpt = sensorRepository.findById(readingDTO.getSensorId());
            if (sensorOpt.isEmpty()) {
                logger.error("Sensor não encontrado com ID: {}", readingDTO.getSensorId());
                return;
            }

            Sensor sensor = sensorOpt.get();
            
            // Verificar se sensor está ativo
            if (!sensor.getAtivo()) {
                logger.warn("Leitura recebida de sensor inativo: {}", sensor.getId());
                return;
            }

            // Criar leitura
            Leitura leitura = new Leitura();
            leitura.setSensor(sensor);
            leitura.setTemperatura(readingDTO.getTemperatura());
            leitura.setUmidade(readingDTO.getUmidade());
            leitura.setTimestamp(readingDTO.getTimestamp() != null ? 
                readingDTO.getTimestamp() : LocalDateTime.now());

            // Salvar leitura
            leitura = leituraService.save(leitura);
            logger.info("Leitura salva com sucesso: ID {}", leitura.getId());

            // Processar alertas baseados na leitura
            alertaService.processarLeitura(leitura);

            // Verificar condições especiais do sensor
            checkSensorConditions(readingDTO, sensor);

            // Atualizar estatísticas em tempo real (se necessário)
            updateRealTimeStats(sensor, leitura);

            logger.info("Processamento da leitura concluído com sucesso para sensor {}", sensor.getId());

        } catch (Exception e) {
            logger.error("Erro ao processar leitura do sensor: {}", readingDTO, e);
            // Em caso de erro, a mensagem será enviada para a Dead Letter Queue
            throw e;
        }
    }

    /**
     * Verifica condições especiais do sensor (bateria baixa, sinal fraco, etc.)
     */
    private void checkSensorConditions(SensorReadingDTO readingDTO, Sensor sensor) {
        try {
            // Verificar bateria baixa
            if (readingDTO.isBatteryLow()) {
                logger.warn("Bateria baixa detectada no sensor {}: {}%", 
                    sensor.getId(), readingDTO.getBatteryLevel());
                
                notificationService.sendLowBatteryAlert(sensor, readingDTO.getBatteryLevel());
            }

            // Verificar sinal fraco
            if (readingDTO.isSignalWeak()) {
                logger.warn("Sinal fraco detectado no sensor {}: {} dBm", 
                    sensor.getId(), readingDTO.getSignalStrength());
                
                notificationService.sendWeakSignalAlert(sensor, readingDTO.getSignalStrength());
            }

            // Verificar se a localização mudou (possível movimentação do sensor)
            if (readingDTO.getLocation() != null && 
                !readingDTO.getLocation().equals(sensor.getLocalizacao())) {
                logger.warn("Possível movimentação do sensor {} detectada. " +
                    "Localização esperada: {}, Localização atual: {}", 
                    sensor.getId(), sensor.getLocalizacao(), readingDTO.getLocation());
                
                notificationService.sendSensorMovementAlert(sensor, readingDTO.getLocation());
            }

        } catch (Exception e) {
            logger.error("Erro ao verificar condições do sensor {}: {}", sensor.getId(), e.getMessage());
        }
    }

    /**
     * Atualiza estatísticas em tempo real
     */
    private void updateRealTimeStats(Sensor sensor, Leitura leitura) {
        try {
            // Aqui você pode implementar lógica para:
            // - Atualizar cache de estatísticas
            // - Enviar dados para dashboard em tempo real via WebSocket
            // - Atualizar métricas de monitoramento
            
            logger.debug("Estatísticas atualizadas para sensor {}", sensor.getId());
            
        } catch (Exception e) {
            logger.error("Erro ao atualizar estatísticas para sensor {}: {}", sensor.getId(), e.getMessage());
        }
    }

    /**
     * Processa dados históricos (fila separada para processamento em lote)
     */
    @RabbitListener(queues = "historical.data")
    @Transactional
    public void processHistoricalData(SensorReadingDTO readingDTO) {
        logger.info("Processando dados históricos do sensor: {}", readingDTO.getSensorId());

        try {
            // Lógica específica para dados históricos
            // Pode incluir validações diferentes, processamento em lote, etc.
            
            processSensorReading(readingDTO);
            
        } catch (Exception e) {
            logger.error("Erro ao processar dados históricos: {}", readingDTO, e);
            throw e;
        }
    }

    /**
     * Processa mensagens da Dead Letter Queue para análise de falhas
     */
    @RabbitListener(queues = "${app.rabbitmq.queue.sensor-readings:sensor.readings}.dlq")
    public void processFailedMessages(SensorReadingDTO readingDTO) {
        logger.error("Processando mensagem que falhou: {}", readingDTO);

        try {
            // Lógica para lidar com mensagens que falharam
            // - Registrar em log de auditoria
            // - Enviar notificação para administradores
            // - Tentar reprocessamento com lógica diferente
            
            notificationService.sendFailedMessageAlert(readingDTO);
            
        } catch (Exception e) {
            logger.error("Erro ao processar mensagem que falhou: {}", readingDTO, e);
        }
    }
}

