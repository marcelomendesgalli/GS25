package com.greenlight.monitor.repository;

import com.greenlight.monitor.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositório para operações de banco de dados da entidade Usuario.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca usuário por email
     */
    Optional<Usuario> findByEmailIgnoreCase(String email);

    /**
     * Busca usuário por provider e providerId
     */
    Optional<Usuario> findByProviderAndProviderId(String provider, String providerId);

    /**
     * Busca usuários ativos
     */
    List<Usuario> findByAtivoTrue();

    /**
     * Busca usuários por provider
     */
    List<Usuario> findByProviderIgnoreCase(String provider);

    /**
     * Busca usuários ativos por provider
     */
    List<Usuario> findByProviderIgnoreCaseAndAtivoTrue(String provider);

    /**
     * Busca usuários por nome (busca parcial, case insensitive)
     */
    List<Usuario> findByNomeContainingIgnoreCase(String nome);

    /**
     * Busca usuários ativos por nome
     */
    List<Usuario> findByNomeContainingIgnoreCaseAndAtivoTrue(String nome);

    /**
     * Busca usuários que fizeram login recentemente
     */
    @Query("SELECT u FROM Usuario u WHERE u.ultimoLogin >= :limite ORDER BY u.ultimoLogin DESC")
    List<Usuario> findUsuariosComLoginRecente(@Param("limite") LocalDateTime limite);

    /**
     * Busca usuários por role
     */
    @Query("SELECT u FROM Usuario u JOIN u.roles r WHERE r = :role")
    List<Usuario> findByRole(@Param("role") Usuario.Role role);

    /**
     * Busca usuários ativos por role
     */
    @Query("SELECT u FROM Usuario u JOIN u.roles r WHERE r = :role AND u.ativo = true")
    List<Usuario> findByRoleAndAtivoTrue(@Param("role") Usuario.Role role);

    /**
     * Busca paginada de usuários ativos
     */
    Page<Usuario> findByAtivoTrue(Pageable pageable);

    /**
     * Busca paginada de usuários com filtros
     */
    @Query("SELECT u FROM Usuario u WHERE " +
           "(:nome IS NULL OR LOWER(u.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) AND " +
           "(:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
           "(:provider IS NULL OR LOWER(u.provider) = LOWER(:provider)) AND " +
           "(:ativo IS NULL OR u.ativo = :ativo)")
    Page<Usuario> findByFiltros(@Param("nome") String nome,
                                @Param("email") String email,
                                @Param("provider") String provider,
                                @Param("ativo") Boolean ativo,
                                Pageable pageable);

    /**
     * Conta usuários ativos
     */
    long countByAtivoTrue();

    /**
     * Conta usuários por provider
     */
    long countByProviderIgnoreCase(String provider);

    /**
     * Conta usuários por role
     */
    @Query("SELECT COUNT(u) FROM Usuario u JOIN u.roles r WHERE r = :role")
    long countByRole(@Param("role") Usuario.Role role);

    /**
     * Conta usuários ativos por role
     */
    @Query("SELECT COUNT(u) FROM Usuario u JOIN u.roles r WHERE r = :role AND u.ativo = true")
    long countByRoleAndAtivoTrue(@Param("role") Usuario.Role role);

    /**
     * Busca providers únicos
     */
    @Query("SELECT DISTINCT u.provider FROM Usuario u WHERE u.provider IS NOT NULL ORDER BY u.provider")
    List<String> findDistinctProviders();

    /**
     * Verifica se existe usuário com o email informado (excluindo o próprio usuário se for edição)
     */
    @Query("SELECT COUNT(u) > 0 FROM Usuario u WHERE LOWER(u.email) = LOWER(:email) AND (:id IS NULL OR u.id != :id)")
    boolean existsByEmailIgnoreCaseAndIdNot(@Param("email") String email, @Param("id") Long id);

    /**
     * Busca usuários que não fizeram login há muito tempo
     */
    @Query("SELECT u FROM Usuario u WHERE u.ativo = true AND " +
           "(u.ultimoLogin IS NULL OR u.ultimoLogin < :limite) " +
           "ORDER BY u.ultimoLogin ASC NULLS FIRST")
    List<Usuario> findUsuariosSemLoginRecente(@Param("limite") LocalDateTime limite);

    /**
     * Busca administradores ativos
     */
    @Query("SELECT u FROM Usuario u JOIN u.roles r WHERE r = 'ADMIN' AND u.ativo = true")
    List<Usuario> findAdministradoresAtivos();

    /**
     * Busca gestores ativos
     */
    @Query("SELECT u FROM Usuario u JOIN u.roles r WHERE r = 'GESTOR' AND u.ativo = true")
    List<Usuario> findGestoresAtivos();
}

