package com.greenlight.monitor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para dados de leitura de sensores recebidos via RabbitMQ.
 */
public class SensorReadingDTO {

    @NotNull(message = "ID do sensor é obrigatório")
    @JsonProperty("sensor_id")
    private Long sensorId;

    @NotNull(message = "Temperatura é obrigatória")
    @DecimalMin(value = "-50.0", message = "Temperatura deve ser maior que -50°C")
    @DecimalMax(value = "70.0", message = "Temperatura deve ser menor que 70°C")
    @JsonProperty("temperature")
    private BigDecimal temperatura;

    @NotNull(message = "Umidade é obrigatória")
    @DecimalMin(value = "0.0", message = "Umidade deve ser maior ou igual a 0%")
    @DecimalMax(value = "100.0", message = "Umidade deve ser menor ou igual a 100%")
    @JsonProperty("humidity")
    private BigDecimal umidade;

    @JsonProperty("timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    @JsonProperty("device_id")
    private String deviceId;

    @JsonProperty("battery_level")
    private Integer batteryLevel;

    @JsonProperty("signal_strength")
    private Integer signalStrength;

    @JsonProperty("location")
    private String location;

    // Construtores
    public SensorReadingDTO() {
    }

    public SensorReadingDTO(Long sensorId, BigDecimal temperatura, BigDecimal umidade) {
        this.sensorId = sensorId;
        this.temperatura = temperatura;
        this.umidade = umidade;
        this.timestamp = LocalDateTime.now();
    }

    public SensorReadingDTO(Long sensorId, BigDecimal temperatura, BigDecimal umidade, LocalDateTime timestamp) {
        this.sensorId = sensorId;
        this.temperatura = temperatura;
        this.umidade = umidade;
        this.timestamp = timestamp;
    }

    // Getters e Setters
    public Long getSensorId() {
        return sensorId;
    }

    public void setSensorId(Long sensorId) {
        this.sensorId = sensorId;
    }

    public BigDecimal getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(BigDecimal temperatura) {
        this.temperatura = temperatura;
    }

    public BigDecimal getUmidade() {
        return umidade;
    }

    public void setUmidade(BigDecimal umidade) {
        this.umidade = umidade;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Integer getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(Integer batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public Integer getSignalStrength() {
        return signalStrength;
    }

    public void setSignalStrength(Integer signalStrength) {
        this.signalStrength = signalStrength;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    // Métodos utilitários
    public boolean isValidReading() {
        return sensorId != null && 
               temperatura != null && 
               umidade != null && 
               timestamp != null;
    }

    public boolean isBatteryLow() {
        return batteryLevel != null && batteryLevel < 20;
    }

    public boolean isSignalWeak() {
        return signalStrength != null && signalStrength < -80;
    }

    @Override
    public String toString() {
        return "SensorReadingDTO{" +
                "sensorId=" + sensorId +
                ", temperatura=" + temperatura +
                ", umidade=" + umidade +
                ", timestamp=" + timestamp +
                ", deviceId='" + deviceId + '\'' +
                ", batteryLevel=" + batteryLevel +
                ", signalStrength=" + signalStrength +
                ", location='" + location + '\'' +
                '}';
    }
}

