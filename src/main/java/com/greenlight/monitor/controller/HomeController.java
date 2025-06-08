package com.greenlight.monitor.controller;

import com.greenlight.monitor.service.EscolaService;
import com.greenlight.monitor.service.AlertaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

/**
 * Controller principal da aplicação.
 * Gerencia páginas principais como home, dashboard, login, etc.
 */
@Controller
public class HomeController {

    @Autowired
    private EscolaService escolaService;

    @Autowired
    private AlertaService alertaService;

    /**
     * Página inicial pública
     */
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("totalEscolas", escolaService.countAtivas());
        return "home";
    }

    /**
     * Página de login
     */
    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    /**
     * Dashboard principal (requer autenticação)
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        // Estatísticas gerais
        model.addAttribute("totalEscolas", escolaService.countAtivas());
        model.addAttribute("totalEscolasComSensores", escolaService.findEscolasComSensoresAtivos().size());
        model.addAttribute("alertasAtivos", alertaService.countAlertasAtivos());
        model.addAttribute("alertasCriticos", alertaService.countAlertasCriticosAtivos());

        // Informações do usuário
        if (authentication != null) {
            String nomeUsuario = getUserName(authentication);
            model.addAttribute("nomeUsuario", nomeUsuario);
            model.addAttribute("isAdmin", hasRole(authentication, "ADMIN"));
            model.addAttribute("isGestor", hasRole(authentication, "GESTOR"));
        }

        // Escolas com sensores ativos para o mapa/lista
        model.addAttribute("escolasComSensores", escolaService.findEscolasComSensoresAtivos());

        // Alertas recentes
        model.addAttribute("alertasRecentes", alertaService.findAlertasRecentes(10));

        return "dashboard";
    }

    /**
     * Página sobre o sistema
     */
    @GetMapping("/sobre")
    public String sobre() {
        return "sobre";
    }

    /**
     * Página de ajuda
     */
    @GetMapping("/ajuda")
    public String ajuda() {
        return "ajuda";
    }

    /**
     * Página de contato
     */
    @GetMapping("/contato")
    public String contato() {
        return "contato";
    }

    /**
     * Página de perfil do usuário
     */
    @GetMapping("/perfil")
    public String perfil(Model model, Authentication authentication) {
        if (authentication != null) {
            model.addAttribute("nomeUsuario", getUserName(authentication));
            model.addAttribute("emailUsuario", getUserEmail(authentication));
            model.addAttribute("provider", getProvider(authentication));
            model.addAttribute("roles", getUserRoles(authentication));
        }
        return "auth/perfil";
    }

    /**
     * Página de erro 403 (acesso negado)
     */
    @GetMapping("/403")
    public String accessDenied() {
        return "error/403";
    }

    /**
     * Página de erro 404 (não encontrado)
     */
    @GetMapping("/404")
    public String notFound() {
        return "error/404";
    }

    /**
     * Página de erro 500 (erro interno)
     */
    @GetMapping("/500")
    public String internalError() {
        return "error/500";
    }

    // Métodos utilitários para extrair informações do usuário autenticado

    private String getUserName(Authentication authentication) {
        if (authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            String name = oauth2User.getAttribute("name");
            return name != null ? name : oauth2User.getAttribute("login");
        }
        return authentication.getName();
    }

    private String getUserEmail(Authentication authentication) {
        if (authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            return oauth2User.getAttribute("email");
        }
        return null;
    }

    private String getProvider(Authentication authentication) {
        if (authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            return oauth2User.getAttribute("provider");
        }
        return "local";
    }

    private String getUserRoles(Authentication authentication) {
        return authentication.getAuthorities().toString();
    }

    private boolean hasRole(Authentication authentication, String role) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role));
    }
}

