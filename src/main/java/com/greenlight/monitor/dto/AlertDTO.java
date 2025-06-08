package com.greenlight.monitor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO para alertas enviados via RabbitMQ.
 */
public class AlertDTO {

    @NotNull(message = "ID do alerta é obrigatório")
    @JsonProperty("alert_id")
    private Long alertId;

    @NotNull(message = "ID da leitura é obrigatório")
    @JsonProperty("reading_id")
    private Long leituraId;

    @NotNull(message = "ID do sensor é obrigatório")
    @JsonProperty("sensor_id")
    private Long sensorId;

    @NotNull(message = "ID da escola é obrigatório")
    @JsonProperty("school_id")
    private Long escolaId;

    @NotBlank(message = "Tipo do alerta é obrigatório")
    @JsonProperty("alert_type")
    private String tipo;

    @NotBlank(message = "Mensagem é obrigatória")
    @JsonProperty("message")
    private String mensagem;

    @NotBlank(message = "Nível é obrigatório")
    @JsonProperty("level")
    private String nivel;

    @NotBlank(message = "Status é obrigatório")
    @JsonProperty("status")
    private String status;

    @JsonProperty("timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    @JsonProperty("school_name")
    private String nomeEscola;

    @JsonProperty("sensor_location")
    private String localizacaoSensor;

    @JsonProperty("temperature")
    private Double temperatura;

    @JsonProperty("humidity")
    private Double umidade;

    @JsonProperty("recipients")
    private List<String> destinatarios;

    @JsonProperty("notification_channels")
    private List<String> canaisNotificacao;

    @JsonProperty("metadata")
    private Map<String, Object> metadata;

    @JsonProperty("priority")
    private Integer prioridade;

    @JsonProperty("expires_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime expiraEm;

    // Construtores
    public AlertDTO() {
    }

    public AlertDTO(Long alertId, Long leituraId, Long sensorId, Long escolaId, 
                   String tipo, String mensagem, String nivel, String status) {
        this.alertId = alertId;
        this.leituraId = leituraId;
        this.sensorId = sensorId;
        this.escolaId = escolaId;
        this.tipo = tipo;
        this.mensagem = mensagem;
        this.nivel = nivel;
        this.status = status;
        this.timestamp = LocalDateTime.now();
        this.prioridade = calculatePriority(nivel);
    }

    // Getters e Setters
    public Long getAlertId() {
        return alertId;
    }

    public void setAlertId(Long alertId) {
        this.alertId = alertId;
    }

    public Long getLeituraId() {
        return leituraId;
    }

    public void setLeituraId(Long leituraId) {
        this.leituraId = leituraId;
    }

    public Long getSensorId() {
        return sensorId;
    }

    public void setSensorId(Long sensorId) {
        this.sensorId = sensorId;
    }

    public Long getEscolaId() {
        return escolaId;
    }

    public void setEscolaId(Long escolaId) {
        this.escolaId = escolaId;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
        this.prioridade = calculatePriority(nivel);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getNomeEscola() {
        return nomeEscola;
    }

    public void setNomeEscola(String nomeEscola) {
        this.nomeEscola = nomeEscola;
    }

    public String getLocalizacaoSensor() {
        return localizacaoSensor;
    }

    public void setLocalizacaoSensor(String localizacaoSensor) {
        this.localizacaoSensor = localizacaoSensor;
    }

    public Double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(Double temperatura) {
        this.temperatura = temperatura;
    }

    public Double getUmidade() {
        return umidade;
    }

    public void setUmidade(Double umidade) {
        this.umidade = umidade;
    }

    public List<String> getDestinatarios() {
        return destinatarios;
    }

    public void setDestinatarios(List<String> destinatarios) {
        this.destinatarios = destinatarios;
    }

    public List<String> getCanaisNotificacao() {
        return canaisNotificacao;
    }

    public void setCanaisNotificacao(List<String> canaisNotificacao) {
        this.canaisNotificacao = canaisNotificacao;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public Integer getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(Integer prioridade) {
        this.prioridade = prioridade;
    }

    public LocalDateTime getExpiraEm() {
        return expiraEm;
    }

    public void setExpiraEm(LocalDateTime expiraEm) {
        this.expiraEm = expiraEm;
    }

    // Métodos utilitários
    private Integer calculatePriority(String nivel) {
        if (nivel == null) return 3;
        
        switch (nivel.toLowerCase()) {
            case "crítico":
                return 1; // Máxima prioridade
            case "alto":
                return 2;
            case "médio":
                return 3;
            case "baixo":
                return 4;
            default:
                return 3;
        }
    }

    public boolean isCritico() {
        return "Crítico".equalsIgnoreCase(nivel);
    }

    public boolean isExpired() {
        return expiraEm != null && LocalDateTime.now().isAfter(expiraEm);
    }

    public boolean isActive() {
        return "Emitido".equalsIgnoreCase(status) || "Em Andamento".equalsIgnoreCase(status);
    }

    public String getFormattedMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("🚨 ALERTA ").append(nivel.toUpperCase()).append(" 🚨\n\n");
        sb.append("Escola: ").append(nomeEscola).append("\n");
        sb.append("Local: ").append(localizacaoSensor).append("\n");
        sb.append("Tipo: ").append(tipo).append("\n\n");
        sb.append("Detalhes:\n").append(mensagem).append("\n\n");
        
        if (temperatura != null) {
            sb.append("Temperatura: ").append(String.format("%.1f°C", temperatura)).append("\n");
        }
        if (umidade != null) {
            sb.append("Umidade: ").append(String.format("%.1f%%", umidade)).append("\n");
        }
        
        sb.append("\nHorário: ").append(timestamp.toString());
        
        return sb.toString();
    }

    @Override
    public String toString() {
        return "AlertDTO{" +
                "alertId=" + alertId +
                ", tipo='" + tipo + '\'' +
                ", nivel='" + nivel + '\'' +
                ", status='" + status + '\'' +
                ", nomeEscola='" + nomeEscola + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}

