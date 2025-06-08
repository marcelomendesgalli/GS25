package com.greenlight.monitor.repository;

import com.greenlight.monitor.entity.Leitura;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositório para operações de banco de dados da entidade Leitura.
 */
@Repository
public interface LeituraRepository extends JpaRepository<Leitura, Long> {

    /**
     * Busca leituras por sensor
     */
    List<Leitura> findBySensorId(Long sensorId);

    /**
     * Busca leituras por sensor ordenadas por timestamp (mais recentes primeiro)
     */
    List<Leitura> findBySensorIdOrderByTimestampDesc(Long sensorId);

    /**
     * Busca leituras por escola
     */
    @Query("SELECT l FROM Leitura l WHERE l.sensor.escola.id = :escolaId ORDER BY l.timestamp DESC")
    List<Leitura> findByEscolaId(@Param("escolaId") Long escolaId);

    /**
     * Busca leituras por período
     */
    List<Leitura> findByTimestampBetween(LocalDateTime inicio, LocalDateTime fim);

    /**
     * Busca leituras por sensor e período
     */
    List<Leitura> findBySensorIdAndTimestampBetween(Long sensorId, LocalDateTime inicio, LocalDateTime fim);

    /**
     * Busca leituras por escola e período
     */
    @Query("SELECT l FROM Leitura l WHERE l.sensor.escola.id = :escolaId AND " +
           "l.timestamp BETWEEN :inicio AND :fim ORDER BY l.timestamp DESC")
    List<Leitura> findByEscolaIdAndTimestampBetween(@Param("escolaId") Long escolaId,
                                                    @Param("inicio") LocalDateTime inicio,
                                                    @Param("fim") LocalDateTime fim);

    /**
     * Busca última leitura de um sensor
     */
    Optional<Leitura> findFirstBySensorIdOrderByTimestampDesc(Long sensorId);

    /**
     * Busca últimas leituras de uma escola
     */
    @Query("SELECT l FROM Leitura l WHERE l.sensor.escola.id = :escolaId AND " +
           "l.timestamp = (SELECT MAX(l2.timestamp) FROM Leitura l2 WHERE l2.sensor.id = l.sensor.id)")
    List<Leitura> findUltimasLeiturasByEscolaId(@Param("escolaId") Long escolaId);

    /**
     * Busca leituras com temperatura acima do limite
     */
    List<Leitura> findByTemperaturaGreaterThanEqual(BigDecimal temperatura);

    /**
     * Busca leituras com temperatura crítica por período
     */
    @Query("SELECT l FROM Leitura l WHERE l.temperatura >= :limite AND " +
           "l.timestamp BETWEEN :inicio AND :fim ORDER BY l.timestamp DESC")
    List<Leitura> findLeiturasTemperaturaCritica(@Param("limite") BigDecimal limite,
                                                 @Param("inicio") LocalDateTime inicio,
                                                 @Param("fim") LocalDateTime fim);

    /**
     * Busca leituras com umidade fora dos limites
     */
    @Query("SELECT l FROM Leitura l WHERE (l.umidade <= :limiteBaixo OR l.umidade >= :limiteAlto) " +
           "ORDER BY l.timestamp DESC")
    List<Leitura> findLeiturasUmidadeForaLimites(@Param("limiteBaixo") BigDecimal limiteBaixo,
                                                 @Param("limiteAlto") BigDecimal limiteAlto);

    /**
     * Busca paginada de leituras por sensor
     */
    Page<Leitura> findBySensorIdOrderByTimestampDesc(Long sensorId, Pageable pageable);

    /**
     * Busca paginada de leituras por escola
     */
    @Query("SELECT l FROM Leitura l WHERE l.sensor.escola.id = :escolaId ORDER BY l.timestamp DESC")
    Page<Leitura> findByEscolaIdOrderByTimestampDesc(@Param("escolaId") Long escolaId, Pageable pageable);

    /**
     * Busca paginada de leituras com filtros
     */
    @Query("SELECT l FROM Leitura l WHERE " +
           "(:sensorId IS NULL OR l.sensor.id = :sensorId) AND " +
           "(:escolaId IS NULL OR l.sensor.escola.id = :escolaId) AND " +
           "(:inicio IS NULL OR l.timestamp >= :inicio) AND " +
           "(:fim IS NULL OR l.timestamp <= :fim) AND " +
           "(:tempMin IS NULL OR l.temperatura >= :tempMin) AND " +
           "(:tempMax IS NULL OR l.temperatura <= :tempMax) " +
           "ORDER BY l.timestamp DESC")
    Page<Leitura> findByFiltros(@Param("sensorId") Long sensorId,
                                @Param("escolaId") Long escolaId,
                                @Param("inicio") LocalDateTime inicio,
                                @Param("fim") LocalDateTime fim,
                                @Param("tempMin") BigDecimal tempMin,
                                @Param("tempMax") BigDecimal tempMax,
                                Pageable pageable);

    /**
     * Conta leituras por sensor
     */
    long countBySensorId(Long sensorId);

    /**
     * Conta leituras por escola
     */
    @Query("SELECT COUNT(l) FROM Leitura l WHERE l.sensor.escola.id = :escolaId")
    long countByEscolaId(@Param("escolaId") Long escolaId);

    /**
     * Conta leituras por período
     */
    long countByTimestampBetween(LocalDateTime inicio, LocalDateTime fim);

    /**
     * Calcula média de temperatura por sensor em um período
     */
    @Query("SELECT AVG(l.temperatura) FROM Leitura l WHERE l.sensor.id = :sensorId AND " +
           "l.timestamp BETWEEN :inicio AND :fim")
    Optional<BigDecimal> findMediaTemperaturaBySensorAndPeriodo(@Param("sensorId") Long sensorId,
                                                                @Param("inicio") LocalDateTime inicio,
                                                                @Param("fim") LocalDateTime fim);

    /**
     * Calcula média de umidade por sensor em um período
     */
    @Query("SELECT AVG(l.umidade) FROM Leitura l WHERE l.sensor.id = :sensorId AND " +
           "l.timestamp BETWEEN :inicio AND :fim")
    Optional<BigDecimal> findMediaUmidadeBySensorAndPeriodo(@Param("sensorId") Long sensorId,
                                                            @Param("inicio") LocalDateTime inicio,
                                                            @Param("fim") LocalDateTime fim);

    /**
     * Busca leituras recentes (última hora)
     */
    @Query("SELECT l FROM Leitura l WHERE l.timestamp >= :limite ORDER BY l.timestamp DESC")
    List<Leitura> findLeiturasRecentes(@Param("limite") LocalDateTime limite);

    /**
     * Remove leituras antigas (para limpeza de dados)
     */
    @Query("DELETE FROM Leitura l WHERE l.timestamp < :limite")
    void deleteLeiturasAntigas(@Param("limite") LocalDateTime limite);
}

