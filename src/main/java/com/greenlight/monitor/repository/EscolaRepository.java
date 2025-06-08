package com.greenlight.monitor.repository;

import com.greenlight.monitor.entity.Escola;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório para operações de banco de dados da entidade Escola.
 */
@Repository
public interface EscolaRepository extends JpaRepository<Escola, Long> {

    /**
     * Busca escolas ativas
     */
    List<Escola> findByAtivoTrue();

    /**
     * Busca escolas por cidade
     */
    List<Escola> findByCidadeIgnoreCase(String cidade);

    /**
     * Busca escolas por estado
     */
    List<Escola> findByEstadoIgnoreCase(String estado);

    /**
     * Busca escolas por cidade e estado
     */
    List<Escola> findByCidadeIgnoreCaseAndEstadoIgnoreCase(String cidade, String estado);

    /**
     * Busca escolas ativas por cidade
     */
    List<Escola> findByCidadeIgnoreCaseAndAtivoTrue(String cidade);

    /**
     * Busca escolas ativas por estado
     */
    List<Escola> findByEstadoIgnoreCaseAndAtivoTrue(String estado);

    /**
     * Busca escolas por nome (busca parcial, case insensitive)
     */
    List<Escola> findByNomeContainingIgnoreCase(String nome);

    /**
     * Busca escolas ativas por nome (busca parcial, case insensitive)
     */
    List<Escola> findByNomeContainingIgnoreCaseAndAtivoTrue(String nome);

    /**
     * Busca escola por nome exato
     */
    Optional<Escola> findByNomeIgnoreCase(String nome);

    /**
     * Busca paginada de escolas ativas
     */
    Page<Escola> findByAtivoTrue(Pageable pageable);

    /**
     * Busca paginada de escolas por filtros
     */
    @Query("SELECT e FROM Escola e WHERE " +
           "(:nome IS NULL OR LOWER(e.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) AND " +
           "(:cidade IS NULL OR LOWER(e.cidade) LIKE LOWER(CONCAT('%', :cidade, '%'))) AND " +
           "(:estado IS NULL OR LOWER(e.estado) = LOWER(:estado)) AND " +
           "(:ativo IS NULL OR e.ativo = :ativo)")
    Page<Escola> findByFiltros(@Param("nome") String nome,
                               @Param("cidade") String cidade,
                               @Param("estado") String estado,
                               @Param("ativo") Boolean ativo,
                               Pageable pageable);

    /**
     * Conta escolas ativas
     */
    long countByAtivoTrue();

    /**
     * Conta escolas por estado
     */
    long countByEstadoIgnoreCase(String estado);

    /**
     * Busca escolas com sensores ativos
     */
    @Query("SELECT DISTINCT e FROM Escola e JOIN e.sensores s WHERE s.ativo = true AND e.ativo = true")
    List<Escola> findEscolasComSensoresAtivos();

    /**
     * Verifica se existe escola com o nome informado (excluindo a própria escola se for edição)
     */
    @Query("SELECT COUNT(e) > 0 FROM Escola e WHERE LOWER(e.nome) = LOWER(:nome) AND (:id IS NULL OR e.id != :id)")
    boolean existsByNomeIgnoreCaseAndIdNot(@Param("nome") String nome, @Param("id") Long id);
}

