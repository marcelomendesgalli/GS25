package com.greenlight.monitor.repository;

import com.greenlight.monitor.entity.Sensor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositório para operações de banco de dados da entidade Sensor.
 */
@Repository
public interface SensorRepository extends JpaRepository<Sensor, Long> {

    /**
     * Busca sensores ativos
     */
    List<Sensor> findByAtivoTrue();

    /**
     * Busca sensores por escola
     */
    List<Sensor> findByEscolaId(Long escolaId);

    /**
     * Busca sensores ativos por escola
     */
    List<Sensor> findByEscolaIdAndAtivoTrue(Long escolaId);

    /**
     * Busca sensores por tipo
     */
    List<Sensor> findByTipoIgnoreCase(String tipo);

    /**
     * Busca sensores ativos por tipo
     */
    List<Sensor> findByTipoIgnoreCaseAndAtivoTrue(String tipo);

    /**
     * Busca sensores por localização (busca parcial)
     */
    List<Sensor> findByLocalizacaoContainingIgnoreCase(String localizacao);

    /**
     * Busca sensores ativos por localização
     */
    List<Sensor> findByLocalizacaoContainingIgnoreCaseAndAtivoTrue(String localizacao);

    /**
     * Busca sensores por escola e tipo
     */
    List<Sensor> findByEscolaIdAndTipoIgnoreCase(Long escolaId, String tipo);

    /**
     * Busca sensores ativos por escola e tipo
     */
    List<Sensor> findByEscolaIdAndTipoIgnoreCaseAndAtivoTrue(Long escolaId, String tipo);

    /**
     * Busca paginada de sensores ativos
     */
    Page<Sensor> findByAtivoTrue(Pageable pageable);

    /**
     * Busca paginada de sensores por escola
     */
    Page<Sensor> findByEscolaId(Long escolaId, Pageable pageable);

    /**
     * Busca paginada de sensores com filtros
     */
    @Query("SELECT s FROM Sensor s WHERE " +
           "(:escolaId IS NULL OR s.escola.id = :escolaId) AND " +
           "(:tipo IS NULL OR LOWER(s.tipo) LIKE LOWER(CONCAT('%', :tipo, '%'))) AND " +
           "(:localizacao IS NULL OR LOWER(s.localizacao) LIKE LOWER(CONCAT('%', :localizacao, '%'))) AND " +
           "(:ativo IS NULL OR s.ativo = :ativo)")
    Page<Sensor> findByFiltros(@Param("escolaId") Long escolaId,
                               @Param("tipo") String tipo,
                               @Param("localizacao") String localizacao,
                               @Param("ativo") Boolean ativo,
                               Pageable pageable);

    /**
     * Conta sensores ativos
     */
    long countByAtivoTrue();

    /**
     * Conta sensores por escola
     */
    long countByEscolaId(Long escolaId);

    /**
     * Conta sensores ativos por escola
     */
    long countByEscolaIdAndAtivoTrue(Long escolaId);

    /**
     * Conta sensores por tipo
     */
    long countByTipoIgnoreCase(String tipo);

    /**
     * Busca tipos de sensores únicos
     */
    @Query("SELECT DISTINCT s.tipo FROM Sensor s ORDER BY s.tipo")
    List<String> findDistinctTipos();

    /**
     * Busca sensores com leituras recentes
     */
    @Query("SELECT DISTINCT s FROM Sensor s JOIN s.leituras l WHERE " +
           "l.timestamp >= CURRENT_TIMESTAMP - INTERVAL '1' HOUR AND s.ativo = true")
    List<Sensor> findSensoresComLeiturasRecentes();

    /**
     * Busca sensores sem leituras recentes (possível problema)
     */
    @Query("SELECT s FROM Sensor s WHERE s.ativo = true AND " +
           "s.id NOT IN (SELECT DISTINCT l.sensor.id FROM Leitura l WHERE " +
           "l.timestamp >= CURRENT_TIMESTAMP - INTERVAL '2' HOUR)")
    List<Sensor> findSensoresSemLeiturasRecentes();

    /**
     * Verifica se existe sensor na mesma localização da escola
     */
    @Query("SELECT COUNT(s) > 0 FROM Sensor s WHERE s.escola.id = :escolaId AND " +
           "LOWER(s.localizacao) = LOWER(:localizacao) AND (:id IS NULL OR s.id != :id)")
    boolean existsByEscolaIdAndLocalizacaoIgnoreCaseAndIdNot(@Param("escolaId") Long escolaId,
                                                             @Param("localizacao") String localizacao,
                                                             @Param("id") Long id);
}

