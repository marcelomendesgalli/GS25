package com.greenlight.monitor.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Serviço customizado para processar usuários OAuth2.
 * Adiciona roles e processa informações específicas de cada provedor.
 */
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        
        return processOAuth2User(userRequest, oauth2User);
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        
        // Extrair informações do usuário baseado no provedor
        String email = extractEmail(oauth2User, registrationId);
        String name = extractName(oauth2User, registrationId);
        
        // Determinar roles baseado no email ou outras regras de negócio
        Set<GrantedAuthority> authorities = determineAuthorities(email);
        
        // Criar atributos customizados
        var attributes = new java.util.HashMap<>(oauth2User.getAttributes());
        attributes.put("email", email);
        attributes.put("name", name);
        attributes.put("provider", registrationId);
        
        // Determinar o atributo chave baseado no provedor
        String nameAttributeKey = getNameAttributeKey(registrationId);
        
        return new DefaultOAuth2User(authorities, attributes, nameAttributeKey);
    }

    private String extractEmail(OAuth2User oauth2User, String registrationId) {
        switch (registrationId.toLowerCase()) {
            case "google":
                return oauth2User.getAttribute("email");
            case "github":
                // GitHub pode não fornecer email público, tentar diferentes campos
                String email = oauth2User.getAttribute("email");
                if (email == null || email.isEmpty()) {
                    // Fallback para login + @github.local se email não estiver disponível
                    String login = oauth2User.getAttribute("login");
                    return login != null ? login + "@github.local" : "unknown@github.local";
                }
                return email;
            default:
                return oauth2User.getAttribute("email");
        }
    }

    private String extractName(OAuth2User oauth2User, String registrationId) {
        switch (registrationId.toLowerCase()) {
            case "google":
                return oauth2User.getAttribute("name");
            case "github":
                String name = oauth2User.getAttribute("name");
                if (name == null || name.isEmpty()) {
                    return oauth2User.getAttribute("login"); // Fallback para login
                }
                return name;
            default:
                return oauth2User.getAttribute("name");
        }
    }

    private Set<GrantedAuthority> determineAuthorities(String email) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        
        // Adicionar role básico para todos os usuários autenticados
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        
        // Regras de negócio para determinar roles adicionais
        if (email != null) {
            // Administradores (baseado em domínio ou emails específicos)
            if (email.endsWith("@admin.escolaclima.com") || 
                email.equals("admin@example.com")) {
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            }
            
            // Gestores escolares (baseado em domínio específico)
            if (email.endsWith("@sme.prefeitura.sp.gov.br") || 
                email.endsWith("@educacao.sp.gov.br")) {
                authorities.add(new SimpleGrantedAuthority("ROLE_GESTOR"));
            }
            
            // Professores e coordenadores
            if (email.contains("professor") || email.contains("coordenador")) {
                authorities.add(new SimpleGrantedAuthority("ROLE_EDUCADOR"));
            }
        }
        
        return authorities;
    }

    private String getNameAttributeKey(String registrationId) {
        switch (registrationId.toLowerCase()) {
            case "google":
                return "sub"; // Google usa 'sub' como identificador único
            case "github":
                return "id"; // GitHub usa 'id' como identificador único
            default:
                return "id";
        }
    }
}

