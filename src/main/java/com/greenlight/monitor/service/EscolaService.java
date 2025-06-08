package com.greenlight.monitor.service;

import com.greenlight.monitor.entity.Escola;
import com.greenlight.monitor.repository.EscolaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Serviço para operações CRUD da entidade Escola.
 */
@Service
@Transactional
public class EscolaService {

    @Autowired
    private EscolaRepository escolaRepository;

    /**
     * Busca todas as escolas
     */
    @Transactional(readOnly = true)
    public List<Escola> findAll() {
        return escolaRepository.findAll();
    }

    /**
     * Busca todas as escolas ativas
     */
    @Transactional(readOnly = true)
    public List<Escola> findAllAtivas() {
        return escolaRepository.findByAtivoTrue();
    }

    /**
     * Busca escola por ID
     */
    @Transactional(readOnly = true)
    public Optional<Escola> findById(Long id) {
        return escolaRepository.findById(id);
    }

    /**
     * Busca escola por ID (lança exceção se não encontrar)
     */
    @Transactional(readOnly = true)
    public Escola getById(Long id) {
        return escolaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Escola não encontrada com ID: " + id));
    }

    /**
     * Busca escolas por cidade
     */
    @Transactional(readOnly = true)
    public List<Escola> findByCidade(String cidade) {
        return escolaRepository.findByCidadeIgnoreCase(cidade);
    }

    /**
     * Busca escolas por estado
     */
    @Transactional(readOnly = true)
    public List<Escola> findByEstado(String estado) {
        return escolaRepository.findByEstadoIgnoreCase(estado);
    }

    /**
     * Busca escolas por nome (busca parcial)
     */
    @Transactional(readOnly = true)
    public List<Escola> findByNome(String nome) {
        return escolaRepository.findByNomeContainingIgnoreCase(nome);
    }

    /**
     * Busca paginada de escolas com filtros
     */
    @Transactional(readOnly = true)
    public Page<Escola> findByFiltros(String nome, String cidade, String estado, Boolean ativo, Pageable pageable) {
        return escolaRepository.findByFiltros(nome, cidade, estado, ativo, pageable);
    }

    /**
     * Busca escolas com sensores ativos
     */
    @Transactional(readOnly = true)
    public List<Escola> findEscolasComSensoresAtivos() {
        return escolaRepository.findEscolasComSensoresAtivos();
    }

    /**
     * Salva uma escola (criar ou atualizar)
     */
    public Escola save(Escola escola) {
        validateEscola(escola);
        return escolaRepository.save(escola);
    }

    /**
     * Cria uma nova escola
     */
    public Escola create(Escola escola) {
        escola.setId(null); // Garantir que é uma nova escola
        return save(escola);
    }

    /**
     * Atualiza uma escola existente
     */
    public Escola update(Long id, Escola escola) {
        Escola escolaExistente = getById(id);
        
        // Atualizar campos
        escolaExistente.setNome(escola.getNome());
        escolaExistente.setCidade(escola.getCidade());
        escolaExistente.setEstado(escola.getEstado());
        escolaExistente.setAtivo(escola.getAtivo());
        
        return save(escolaExistente);
    }

    /**
     * Ativa uma escola
     */
    public Escola ativar(Long id) {
        Escola escola = getById(id);
        escola.setAtivo(true);
        return escolaRepository.save(escola);
    }

    /**
     * Desativa uma escola
     */
    public Escola desativar(Long id) {
        Escola escola = getById(id);
        escola.setAtivo(false);
        return escolaRepository.save(escola);
    }

    /**
     * Remove uma escola (soft delete - apenas desativa)
     */
    public void delete(Long id) {
        desativar(id);
    }

    /**
     * Remove uma escola permanentemente
     */
    public void deletePhysically(Long id) {
        if (!escolaRepository.existsById(id)) {
            throw new RuntimeException("Escola não encontrada com ID: " + id);
        }
        escolaRepository.deleteById(id);
    }

    /**
     * Conta total de escolas
     */
    @Transactional(readOnly = true)
    public long count() {
        return escolaRepository.count();
    }

    /**
     * Conta escolas ativas
     */
    @Transactional(readOnly = true)
    public long countAtivas() {
        return escolaRepository.countByAtivoTrue();
    }

    /**
     * Conta escolas por estado
     */
    @Transactional(readOnly = true)
    public long countByEstado(String estado) {
        return escolaRepository.countByEstadoIgnoreCase(estado);
    }

    /**
     * Verifica se existe escola com o nome informado
     */
    @Transactional(readOnly = true)
    public boolean existsByNome(String nome) {
        return escolaRepository.existsByNomeIgnoreCaseAndIdNot(nome, null);
    }

    /**
     * Verifica se existe escola com o nome informado (excluindo uma escola específica)
     */
    @Transactional(readOnly = true)
    public boolean existsByNome(String nome, Long excludeId) {
        return escolaRepository.existsByNomeIgnoreCaseAndIdNot(nome, excludeId);
    }

    /**
     * Valida os dados da escola
     */
    private void validateEscola(Escola escola) {
        if (escola.getNome() == null || escola.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da escola é obrigatório");
        }
        
        if (escola.getCidade() == null || escola.getCidade().trim().isEmpty()) {
            throw new IllegalArgumentException("Cidade é obrigatória");
        }
        
        if (escola.getEstado() == null || escola.getEstado().trim().isEmpty()) {
            throw new IllegalArgumentException("Estado é obrigatório");
        }
        
        if (escola.getEstado().length() != 2) {
            throw new IllegalArgumentException("Estado deve ter exatamente 2 caracteres (UF)");
        }
        
        // Verificar se já existe escola com o mesmo nome
        if (existsByNome(escola.getNome(), escola.getId())) {
            throw new IllegalArgumentException("Já existe uma escola com o nome: " + escola.getNome());
        }
    }
}

