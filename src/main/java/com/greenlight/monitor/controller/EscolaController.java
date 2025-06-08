package com.greenlight.monitor.controller;

import com.greenlight.monitor.entity.Escola;
import com.greenlight.monitor.service.EscolaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.Optional;

/**
 * Controller para gerenciar operações CRUD de escolas.
 */
@Controller
@RequestMapping("/escolas")
public class EscolaController {

    @Autowired
    private EscolaService escolaService;

    /**
     * Lista todas as escolas com paginação e filtros
     */
    @GetMapping
    public String listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nome") String sort,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cidade,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Boolean ativo,
            Model model) {

        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? 
            Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        Page<Escola> escolas = escolaService.findByFiltros(nome, cidade, estado, ativo, pageable);

        model.addAttribute("escolas", escolas);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", escolas.getTotalPages());
        model.addAttribute("totalElements", escolas.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("sort", sort);
        model.addAttribute("direction", direction);
        
        // Manter filtros na view
        model.addAttribute("filtroNome", nome);
        model.addAttribute("filtroCidade", cidade);
        model.addAttribute("filtroEstado", estado);
        model.addAttribute("filtroAtivo", ativo);

        return "escolas/lista";
    }

    /**
     * Exibe detalhes de uma escola
     */
    @GetMapping("/{id}")
    public String detalhar(@PathVariable Long id, Model model) {
        Optional<Escola> escola = escolaService.findById(id);
        if (escola.isEmpty()) {
            return "redirect:/escolas?error=Escola não encontrada";
        }

        model.addAttribute("escola", escola.get());
        return "escolas/detalhes";
    }

    /**
     * Exibe formulário para criar nova escola
     */
    @GetMapping("/nova")
    public String novaEscola(Model model) {
        model.addAttribute("escola", new Escola());
        model.addAttribute("acao", "Criar");
        return "escolas/formulario";
    }

    /**
     * Processa criação de nova escola
     */
    @PostMapping("/nova")
    public String criarEscola(@Valid @ModelAttribute Escola escola, 
                             BindingResult result, 
                             RedirectAttributes redirectAttributes,
                             Model model) {
        
        if (result.hasErrors()) {
            model.addAttribute("acao", "Criar");
            return "escolas/formulario";
        }

        try {
            Escola escolaSalva = escolaService.create(escola);
            redirectAttributes.addFlashAttribute("sucesso", 
                "Escola '" + escolaSalva.getNome() + "' criada com sucesso!");
            return "redirect:/escolas/" + escolaSalva.getId();
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao criar escola: " + e.getMessage());
            model.addAttribute("acao", "Criar");
            return "escolas/formulario";
        }
    }

    /**
     * Exibe formulário para editar escola
     */
    @GetMapping("/{id}/editar")
    public String editarEscola(@PathVariable Long id, Model model) {
        Optional<Escola> escola = escolaService.findById(id);
        if (escola.isEmpty()) {
            return "redirect:/escolas?error=Escola não encontrada";
        }

        model.addAttribute("escola", escola.get());
        model.addAttribute("acao", "Editar");
        return "escolas/formulario";
    }

    /**
     * Processa edição de escola
     */
    @PostMapping("/{id}/editar")
    public String atualizarEscola(@PathVariable Long id,
                                 @Valid @ModelAttribute Escola escola,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        
        if (result.hasErrors()) {
            escola.setId(id);
            model.addAttribute("acao", "Editar");
            return "escolas/formulario";
        }

        try {
            Escola escolaAtualizada = escolaService.update(id, escola);
            redirectAttributes.addFlashAttribute("sucesso", 
                "Escola '" + escolaAtualizada.getNome() + "' atualizada com sucesso!");
            return "redirect:/escolas/" + escolaAtualizada.getId();
        } catch (Exception e) {
            escola.setId(id);
            model.addAttribute("erro", "Erro ao atualizar escola: " + e.getMessage());
            model.addAttribute("acao", "Editar");
            return "escolas/formulario";
        }
    }

    /**
     * Ativa uma escola
     */
    @PostMapping("/{id}/ativar")
    public String ativarEscola(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Escola escola = escolaService.ativar(id);
            redirectAttributes.addFlashAttribute("sucesso", 
                "Escola '" + escola.getNome() + "' ativada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao ativar escola: " + e.getMessage());
        }
        return "redirect:/escolas";
    }

    /**
     * Desativa uma escola
     */
    @PostMapping("/{id}/desativar")
    public String desativarEscola(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Escola escola = escolaService.desativar(id);
            redirectAttributes.addFlashAttribute("sucesso", 
                "Escola '" + escola.getNome() + "' desativada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao desativar escola: " + e.getMessage());
        }
        return "redirect:/escolas";
    }

    /**
     * Remove uma escola (soft delete)
     */
    @PostMapping("/{id}/remover")
    public String removerEscola(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<Escola> escola = escolaService.findById(id);
            if (escola.isPresent()) {
                escolaService.delete(id);
                redirectAttributes.addFlashAttribute("sucesso", 
                    "Escola '" + escola.get().getNome() + "' removida com sucesso!");
            } else {
                redirectAttributes.addFlashAttribute("erro", "Escola não encontrada!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao remover escola: " + e.getMessage());
        }
        return "redirect:/escolas";
    }

    /**
     * API endpoint para buscar escolas (para uso em AJAX)
     */
    @GetMapping("/api")
    @ResponseBody
    public Page<Escola> listarApi(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nome") String sort,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cidade,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Boolean ativo) {

        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? 
            Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        return escolaService.findByFiltros(nome, cidade, estado, ativo, pageable);
    }
}

