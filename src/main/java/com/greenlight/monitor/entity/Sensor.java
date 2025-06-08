package com.greenlight.monitor.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade que representa um sensor no sistema.
 * Representa os sensores instalados nas escolas, identificando o local e o tipo de medição realizada.
 */
@Entity
@Table(name = "sensor")
public class Sensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Escola é obrigatória")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_escola", nullable = false)
    private Escola escola;

    @NotBlank(message = "Localização é obrigatória")
    @Size(max = 255, message = "Localização deve ter no máximo 255 caracteres")
    @Column(name = "localizacao", nullable = false)
    private String localizacao;

    @NotNull(message = "Status ativo é obrigatório")
    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    @NotBlank(message = "Tipo do sensor é obrigatório")
    @Size(max = 50, message = "Tipo deve ter no máximo 50 caracteres")
    @Column(name = "tipo", nullable = false)
    private String tipo;

    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    @Column(name = "descricao")
    private String descricao;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    // Relacionamento com leituras
    @OneToMany(mappedBy = "sensor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Leitura> leituras = new ArrayList<>();

    // Construtores
    public Sensor() {
    }

    public Sensor(Escola escola, String localizacao, String tipo) {
        this.escola = escola;
        this.localizacao = localizacao;
        this.tipo = tipo;
        this.ativo = true;
    }

    public Sensor(Escola escola, String localizacao, String tipo, String descricao) {
        this(escola, localizacao, tipo);
        this.descricao = descricao;
    }

    // Métodos de callback JPA
    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
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

    public Escola getEscola() {
        return escola;
    }

    public void setEscola(Escola escola) {
        this.escola = escola;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
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

    public List<Leitura> getLeituras() {
        return leituras;
    }

    public void setLeituras(List<Leitura> leituras) {
        this.leituras = leituras;
    }

    // Métodos utilitários
    public void addLeitura(Leitura leitura) {
        leituras.add(leitura);
        leitura.setSensor(this);
    }

    public void removeLeitura(Leitura leitura) {
        leituras.remove(leitura);
        leitura.setSensor(null);
    }

    @Override
    public String toString() {
        return "Sensor{" +
                "id=" + id +
                ", localizacao='" + localizacao + '\'' +
                ", ativo=" + ativo +
                ", tipo='" + tipo + '\'' +
                ", descricao='" + descricao + '\'' +
                '}';
    }
}

