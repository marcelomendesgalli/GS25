package com.greenlight.monitor.repository;

import com.greenlight.monitor.entity.Alerta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositório para operações de banco de dados da entidade Alerta.
 */
@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    /**
     * Busca alertas por tipo
     */
    List<Alerta> findByTipoIgnoreCase(String tipo);

    /**
     * Busca alertas por nível
     */
    List<Alerta> findByNivelIgnoreCase(String nivel);

    /**
     * Busca alertas por status
     */
    List<Alerta> findByStatusIgnoreCase(String status);

    /**
     * Busca alertas ativos (emitidos ou em andamento)
     */
    @Query("SELECT a FROM Alerta a WHERE a.status IN ('Emitido', 'Em Andamento') ORDER BY a.timestamp DESC")
    List<Alerta> findAlertasAtivos();

    /**
     * Busca alertas críticos ativos
     */
    @Query("SELECT a FROM Alerta a WHERE a.nivel = 'Crítico' AND a.status IN ('Emitido', 'Em Andamento') " +
           "ORDER BY a.timestamp DESC")
    List<Alerta> findAlertasCriticosAtivos();

    /**
     * Busca alertas por sensor
     */
    @Query("SELECT a FROM Alerta a WHERE a.leitura.sensor.id = :sensorId ORDER BY a.timestamp DESC")
    List<Alerta> findBySensorId(@Param("sensorId") Long sensorId);

    /**
     * Busca alertas por escola
     */
    @Query("SELECT a FROM Alerta a WHERE a.leitura.sensor.escola.id = :escolaId ORDER BY a.timestamp DESC")
    List<Alerta> findByEscolaId(@Param("escolaId") Long escolaId);

    /**
     * Busca alertas ativos por escola
     */
    @Query("SELECT a FROM Alerta a WHERE a.leitura.sensor.escola.id = :escolaId AND " +
           "a.status IN ('Emitido', 'Em Andamento') ORDER BY a.timestamp DESC")
    List<Alerta> findAlertasAtivosByEscolaId(@Param("escolaId") Long escolaId);

    /**
     * Busca alertas por período
     */
    List<Alerta> findByTimestampBetween(LocalDateTime inicio, LocalDateTime fim);

    /**
     * Busca alertas por escola e período
     */
    @Query("SELECT a FROM Alerta a WHERE a.leitura.sensor.escola.id = :escolaId AND " +
           "a.timestamp BETWEEN :inicio AND :fim ORDER BY a.timestamp DESC")
    List<Alerta> findByEscolaIdAndTimestampBetween(@Param("escolaId") Long escolaId,
                                                   @Param("inicio") LocalDateTime inicio,
                                                   @Param("fim") LocalDateTime fim);

    /**
     * Busca alertas por tipo e período
     */
    List<Alerta> findByTipoIgnoreCaseAndTimestampBetween(String tipo, LocalDateTime inicio, LocalDateTime fim);

    /**
     * Busca alertas por nível e período
     */
    List<Alerta> findByNivelIgnoreCaseAndTimestampBetween(String nivel, LocalDateTime inicio, LocalDateTime fim);

    /**
     * Busca paginada de alertas ordenados por timestamp
     */
    Page<Alerta> findAllByOrderByTimestampDesc(Pageable pageable);

    /**
     * Busca paginada de alertas ativos
     */
    @Query("SELECT a FROM Alerta a WHERE a.status IN ('Emitido', 'Em Andamento') ORDER BY a.timestamp DESC")
    Page<Alerta> findAlertasAtivos(Pageable pageable);

    /**
     * Busca paginada de alertas por escola
     */
    @Query("SELECT a FROM Alerta a WHERE a.leitura.sensor.escola.id = :escolaId ORDER BY a.timestamp DESC")
    Page<Alerta> findByEscolaIdOrderByTimestampDesc(@Param("escolaId") Long escolaId, Pageable pageable);

    /**
     * Busca paginada de alertas com filtros
     */
    @Query("SELECT a FROM Alerta a WHERE " +
           "(:escolaId IS NULL OR a.leitura.sensor.escola.id = :escolaId) AND " +
           "(:sensorId IS NULL OR a.leitura.sensor.id = :sensorId) AND " +
           "(:tipo IS NULL OR LOWER(a.tipo) LIKE LOWER(CONCAT('%', :tipo, '%'))) AND " +
           "(:nivel IS NULL OR LOWER(a.nivel) = LOWER(:nivel)) AND " +
           "(:status IS NULL OR LOWER(a.status) = LOWER(:status)) AND " +
           "(:inicio IS NULL OR a.timestamp >= :inicio) AND " +
           "(:fim IS NULL OR a.timestamp <= :fim) " +
           "ORDER BY a.timestamp DESC")
    Page<Alerta> findByFiltros(@Param("escolaId") Long escolaId,
                               @Param("sensorId") Long sensorId,
                               @Param("tipo") String tipo,
                               @Param("nivel") String nivel,
                               @Param("status") String status,
                               @Param("inicio") LocalDateTime inicio,
                               @Param("fim") LocalDateTime fim,
                               Pageable pageable);

    /**
     * Conta alertas ativos
     */
    @Query("SELECT COUNT(a) FROM Alerta a WHERE a.status IN ('Emitido', 'Em Andamento')")
    long countAlertasAtivos();

    /**
     * Conta alertas críticos ativos
     */
    @Query("SELECT COUNT(a) FROM Alerta a WHERE a.nivel = 'Crítico' AND a.status IN ('Emitido', 'Em Andamento')")
    long countAlertasCriticosAtivos();

    /**
     * Conta alertas por escola
     */
    @Query("SELECT COUNT(a) FROM Alerta a WHERE a.leitura.sensor.escola.id = :escolaId")
    long countByEscolaId(@Param("escolaId") Long escolaId);

    /**
     * Conta alertas ativos por escola
     */
    @Query("SELECT COUNT(a) FROM Alerta a WHERE a.leitura.sensor.escola.id = :escolaId AND " +
           "a.status IN ('Emitido', 'Em Andamento')")
    long countAlertasAtivosByEscolaId(@Param("escolaId") Long escolaId);

    /**
     * Conta alertas por tipo
     */
    long countByTipoIgnoreCase(String tipo);

    /**
     * Conta alertas por nível
     */
    long countByNivelIgnoreCase(String nivel);

    /**
     * Conta alertas por status
     */
    long countByStatusIgnoreCase(String status);

    /**
     * Busca tipos de alertas únicos
     */
    @Query("SELECT DISTINCT a.tipo FROM Alerta a ORDER BY a.tipo")
    List<String> findDistinctTipos();

    /**
     * Busca níveis de alertas únicos
     */
    @Query("SELECT DISTINCT a.nivel FROM Alerta a ORDER BY a.nivel")
    List<String> findDistinctNiveis();

    /**
     * Busca status de alertas únicos
     */
    @Query("SELECT DISTINCT a.status FROM Alerta a ORDER BY a.status")
    List<String> findDistinctStatus();

    /**
     * Busca alertas recentes (última hora)
     */
    @Query("SELECT a FROM Alerta a WHERE a.timestamp >= :limite ORDER BY a.timestamp DESC")
    List<Alerta> findAlertasRecentes(@Param("limite") LocalDateTime limite);

    /**
     * Busca alertas não visualizados
     */
    @Query("SELECT a FROM Alerta a WHERE a.status = 'Emitido' ORDER BY a.timestamp DESC")
    List<Alerta> findAlertasNaoVisualizados();

    /**
     * Remove alertas antigos resolvidos (para limpeza de dados)
     */
    @Query("DELETE FROM Alerta a WHERE a.status = 'Resolvido' AND a.timestamp < :limite")
    void deleteAlertasAntigosResolvidos(@Param("limite") LocalDateTime limite);
}

