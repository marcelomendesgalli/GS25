package com.greenlight.monitor.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade que representa uma leitura de sensor no sistema.
 * Registra as medições feitas pelos sensores. Cada leitura está ligada a um sensor e possui marcação temporal.
 */
@Entity
@Table(name = "leitura")
public class Leitura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Sensor é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sensor", nullable = false)
    private Sensor sensor;

    @NotNull(message = "Temperatura é obrigatória")
    @DecimalMin(value = "-50.0", message = "Temperatura deve ser maior que -50°C")
    @DecimalMax(value = "70.0", message = "Temperatura deve ser menor que 70°C")
    @Column(name = "temperatura", nullable = false, precision = 5, scale = 2)
    private BigDecimal temperatura;

    @NotNull(message = "Umidade é obrigatória")
    @DecimalMin(value = "0.0", message = "Umidade deve ser maior ou igual a 0%")
    @DecimalMax(value = "100.0", message = "Umidade deve ser menor ou igual a 100%")
    @Column(name = "umidade", nullable = false, precision = 5, scale = 2)
    private BigDecimal umidade;

    @NotNull(message = "Timestamp é obrigatório")
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    // Relacionamento com alertas
    @OneToMany(mappedBy = "leitura", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Alerta> alertas = new ArrayList<>();

    // Construtores
    public Leitura() {
    }

    public Leitura(Sensor sensor, BigDecimal temperatura, BigDecimal umidade) {
        this.sensor = sensor;
        this.temperatura = temperatura;
        this.umidade = umidade;
        this.timestamp = LocalDateTime.now();
    }

    public Leitura(Sensor sensor, BigDecimal temperatura, BigDecimal umidade, LocalDateTime timestamp) {
        this.sensor = sensor;
        this.temperatura = temperatura;
        this.umidade = umidade;
        this.timestamp = timestamp;
    }

    // Métodos de callback JPA
    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
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

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public List<Alerta> getAlertas() {
        return alertas;
    }

    public void setAlertas(List<Alerta> alertas) {
        this.alertas = alertas;
    }

    // Métodos utilitários
    public void addAlerta(Alerta alerta) {
        alertas.add(alerta);
        alerta.setLeitura(this);
    }

    public void removeAlerta(Alerta alerta) {
        alertas.remove(alerta);
        alerta.setLeitura(null);
    }

    /**
     * Verifica se a temperatura está em nível de alerta
     */
    public boolean isTemperaturaAlerta(BigDecimal limiteWarning, BigDecimal limiteCritical) {
        return temperatura.compareTo(limiteWarning) >= 0;
    }

    /**
     * Verifica se a umidade está em nível de alerta
     */
    public boolean isUmidadeAlerta(BigDecimal limiteBaixo, BigDecimal limiteAlto) {
        return umidade.compareTo(limiteBaixo) <= 0 || umidade.compareTo(limiteAlto) >= 0;
    }

    @Override
    public String toString() {
        return "Leitura{" +
                "id=" + id +
                ", temperatura=" + temperatura +
                ", umidade=" + umidade +
                ", timestamp=" + timestamp +
                '}';
    }
}

