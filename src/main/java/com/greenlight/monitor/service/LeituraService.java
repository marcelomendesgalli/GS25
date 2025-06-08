package com.greenlight.monitor.service;

import com.greenlight.monitor.entity.Leitura;
import com.greenlight.monitor.repository.LeituraRepository;
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
 * Serviço para operações CRUD da entidade Leitura.
 */
@Service
@Transactional
public class LeituraService {

    @Autowired
    private LeituraRepository leituraRepository;

    /**
     * Busca todas as leituras
     */
    @Transactional(readOnly = true)
    public List<Leitura> findAll() {
        return leituraRepository.findAll();
    }

    /**
     * Busca leitura por ID
     */
    @Transactional(readOnly = true)
    public Optional<Leitura> findById(Long id) {
        return leituraRepository.findById(id);
    }

    /**
     * Busca leitura por ID (lança exceção se não encontrar)
     */
    @Transactional(readOnly = true)
    public Leitura getById(Long id) {
        return leituraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leitura não encontrada com ID: " + id));
    }

    /**
     * Busca leituras por sensor
     */
    @Transactional(readOnly = true)
    public List<Leitura> findBySensorId(Long sensorId) {
        return leituraRepository.findBySensorIdOrderByTimestampDesc(sensorId);
    }

    /**
     * Busca leituras por escola
     */
    @Transactional(readOnly = true)
    public List<Leitura> findByEscolaId(Long escolaId) {
        return leituraRepository.findByEscolaId(escolaId);
    }

    /**
     * Busca última leitura de um sensor
     */
    @Transactional(readOnly = true)
    public Optional<Leitura> findUltimaLeituraBySensorId(Long sensorId) {
        return leituraRepository.findFirstBySensorIdOrderByTimestampDesc(sensorId);
    }

    /**
     * Busca leituras por período
     */
    @Transactional(readOnly = true)
    public List<Leitura> findByPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return leituraRepository.findByTimestampBetween(inicio, fim);
    }

    /**
     * Busca leituras por sensor e período
     */
    @Transactional(readOnly = true)
    public List<Leitura> findBySensorIdAndPeriodo(Long sensorId, LocalDateTime inicio, LocalDateTime fim) {
        return leituraRepository.findBySensorIdAndTimestampBetween(sensorId, inicio, fim);
    }

    /**
     * Busca leituras paginadas com filtros
     */
    @Transactional(readOnly = true)
    public Page<Leitura> findByFiltros(Long sensorId, Long escolaId, LocalDateTime inicio, 
                                      LocalDateTime fim, BigDecimal tempMin, BigDecimal tempMax, 
                                      Pageable pageable) {
        return leituraRepository.findByFiltros(sensorId, escolaId, inicio, fim, tempMin, tempMax, pageable);
    }

    /**
     * Salva uma leitura
     */
    public Leitura save(Leitura leitura) {
        validateLeitura(leitura);
        return leituraRepository.save(leitura);
    }

    /**
     * Cria uma nova leitura
     */
    public Leitura create(Leitura leitura) {
        leitura.setId(null);
        return save(leitura);
    }

    /**
     * Conta leituras por sensor
     */
    @Transactional(readOnly = true)
    public long countBySensorId(Long sensorId) {
        return leituraRepository.countBySensorId(sensorId);
    }

    /**
     * Conta leituras por escola
     */
    @Transactional(readOnly = true)
    public long countByEscolaId(Long escolaId) {
        return leituraRepository.countByEscolaId(escolaId);
    }

    /**
     * Calcula média de temperatura por sensor em um período
     */
    @Transactional(readOnly = true)
    public Optional<BigDecimal> calcularMediaTemperatura(Long sensorId, LocalDateTime inicio, LocalDateTime fim) {
        return leituraRepository.findMediaTemperaturaBySensorAndPeriodo(sensorId, inicio, fim);
    }

    /**
     * Calcula média de umidade por sensor em um período
     */
    @Transactional(readOnly = true)
    public Optional<BigDecimal> calcularMediaUmidade(Long sensorId, LocalDateTime inicio, LocalDateTime fim) {
        return leituraRepository.findMediaUmidadeBySensorAndPeriodo(sensorId, inicio, fim);
    }

    /**
     * Busca leituras recentes (última hora)
     */
    @Transactional(readOnly = true)
    public List<Leitura> findLeiturasRecentes() {
        LocalDateTime limite = LocalDateTime.now().minusHours(1);
        return leituraRepository.findLeiturasRecentes(limite);
    }

    /**
     * Busca leituras com temperatura crítica
     */
    @Transactional(readOnly = true)
    public List<Leitura> findLeiturasTemperaturaCritica(BigDecimal limite, LocalDateTime inicio, LocalDateTime fim) {
        return leituraRepository.findLeiturasTemperaturaCritica(limite, inicio, fim);
    }

    /**
     * Busca leituras com umidade fora dos limites
     */
    @Transactional(readOnly = true)
    public List<Leitura> findLeiturasUmidadeForaLimites(BigDecimal limiteBaixo, BigDecimal limiteAlto) {
        return leituraRepository.findLeiturasUmidadeForaLimites(limiteBaixo, limiteAlto);
    }

    /**
     * Remove leituras antigas (para limpeza de dados)
     */
    public void limparLeiturasAntigas(int diasParaManter) {
        LocalDateTime limite = LocalDateTime.now().minusDays(diasParaManter);
        leituraRepository.deleteLeiturasAntigas(limite);
    }

    /**
     * Valida os dados da leitura
     */
    private void validateLeitura(Leitura leitura) {
        if (leitura.getSensor() == null) {
            throw new IllegalArgumentException("Sensor é obrigatório");
        }
        
        if (leitura.getTemperatura() == null) {
            throw new IllegalArgumentException("Temperatura é obrigatória");
        }
        
        if (leitura.getUmidade() == null) {
            throw new IllegalArgumentException("Umidade é obrigatória");
        }
        
        if (leitura.getTimestamp() == null) {
            leitura.setTimestamp(LocalDateTime.now());
        }
        
        // Validar ranges
        if (leitura.getTemperatura().compareTo(BigDecimal.valueOf(-50)) < 0 || 
            leitura.getTemperatura().compareTo(BigDecimal.valueOf(70)) > 0) {
            throw new IllegalArgumentException("Temperatura deve estar entre -50°C e 70°C");
        }
        
        if (leitura.getUmidade().compareTo(BigDecimal.ZERO) < 0 || 
            leitura.getUmidade().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Umidade deve estar entre 0% e 100%");
        }
        
        // Validar timestamp (não pode ser muito no futuro)
        if (leitura.getTimestamp().isAfter(LocalDateTime.now().plusMinutes(5))) {
            throw new IllegalArgumentException("Timestamp não pode estar muito no futuro");
        }
    }
}

