package com.greenlight.monitor.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Entidade que representa um alerta no sistema.
 * Gera notificações automáticas com base nas leituras recebidas, quando são detectados valores críticos.
 */
@Entity
@Table(name = "alerta")
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Leitura é obrigatória")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_leitura", nullable = false)
    private Leitura leitura;

    @NotBlank(message = "Tipo do alerta é obrigatório")
    @Size(max = 100, message = "Tipo deve ter no máximo 100 caracteres")
    @Column(name = "tipo", nullable = false)
    private String tipo;

    @NotBlank(message = "Mensagem é obrigatória")
    @Size(max = 1000, message = "Mensagem deve ter no máximo 1000 caracteres")
    @Column(name = "mensagem", nullable = false, length = 1000)
    private String mensagem;

    @NotBlank(message = "Nível é obrigatório")
    @Size(max = 50, message = "Nível deve ter no máximo 50 caracteres")
    @Column(name = "nivel", nullable = false)
    private String nivel;

    @NotBlank(message = "Status é obrigatório")
    @Size(max = 50, message = "Status deve ter no máximo 50 caracteres")
    @Column(name = "status", nullable = false)
    private String status;

    @NotNull(message = "Timestamp é obrigatório")
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    // Enums para padronizar valores
    public enum TipoAlerta {
        CALOR_EXTREMO("Calor Extremo"),
        TEMPERATURA_ALTA("Temperatura Alta"),
        UMIDADE_BAIXA("Umidade Baixa"),
        UMIDADE_ALTA("Umidade Alta"),
        SENSOR_OFFLINE("Sensor Offline"),
        FALHA_COMUNICACAO("Falha de Comunicação");

        private final String descricao;

        TipoAlerta(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum NivelAlerta {
        BAIXO("Baixo"),
        MEDIO("Médio"),
        ALTO("Alto"),
        CRITICO("Crítico");

        private final String descricao;

        NivelAlerta(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum StatusAlerta {
        EMITIDO("Emitido"),
        VISUALIZADO("Visualizado"),
        EM_ANDAMENTO("Em Andamento"),
        RESOLVIDO("Resolvido"),
        CANCELADO("Cancelado");

        private final String descricao;

        StatusAlerta(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    // Construtores
    public Alerta() {
    }

    public Alerta(Leitura leitura, String tipo, String mensagem, String nivel) {
        this.leitura = leitura;
        this.tipo = tipo;
        this.mensagem = mensagem;
        this.nivel = nivel;
        this.status = StatusAlerta.EMITIDO.getDescricao();
        this.timestamp = LocalDateTime.now();
    }

    public Alerta(Leitura leitura, TipoAlerta tipo, String mensagem, NivelAlerta nivel) {
        this(leitura, tipo.getDescricao(), mensagem, nivel.getDescricao());
    }

    // Métodos de callback JPA
    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
        if (status == null) {
            status = StatusAlerta.EMITIDO.getDescricao();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Leitura getLeitura() {
        return leitura;
    }

    public void setLeitura(Leitura leitura) {
        this.leitura = leitura;
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

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    // Métodos utilitários
    public boolean isAtivo() {
        return StatusAlerta.EMITIDO.getDescricao().equals(status) || 
               StatusAlerta.EM_ANDAMENTO.getDescricao().equals(status);
    }

    public boolean isCritico() {
        return NivelAlerta.CRITICO.getDescricao().equals(nivel);
    }

    public void marcarComoVisualizado() {
        this.status = StatusAlerta.VISUALIZADO.getDescricao();
    }

    public void marcarComoResolvido() {
        this.status = StatusAlerta.RESOLVIDO.getDescricao();
    }

    @Override
    public String toString() {
        return "Alerta{" +
                "id=" + id +
                ", tipo='" + tipo + '\'' +
                ", nivel='" + nivel + '\'' +
                ", status='" + status + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}

