package com.greenlight.monitor.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidade que representa um usuário do sistema.
 * Armazena informações de usuários autenticados via OAuth2 ou login tradicional.
 */
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 255, message = "Nome deve ter no máximo 255 caracteres")
    @Column(name = "nome", nullable = false)
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ter formato válido")
    @Size(max = 255, message = "Email deve ter no máximo 255 caracteres")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "provider")
    private String provider; // google, github, local

    @Column(name = "provider_id")
    private String providerId; // ID do usuário no provedor OAuth2

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "usuario_roles", joinColumns = @JoinColumn(name = "usuario_id"))
    @Column(name = "role")
    private Set<Role> roles = new HashSet<>();

    @Column(name = "ultimo_login")
    private LocalDateTime ultimoLogin;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    // Enum para roles
    public enum Role {
        USER("ROLE_USER", "Usuário"),
        ADMIN("ROLE_ADMIN", "Administrador"),
        GESTOR("ROLE_GESTOR", "Gestor Escolar"),
        EDUCADOR("ROLE_EDUCADOR", "Educador");

        private final String authority;
        private final String descricao;

        Role(String authority, String descricao) {
            this.authority = authority;
            this.descricao = descricao;
        }

        public String getAuthority() {
            return authority;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    // Construtores
    public Usuario() {
        this.roles.add(Role.USER); // Role padrão
    }

    public Usuario(String nome, String email) {
        this();
        this.nome = nome;
        this.email = email;
    }

    public Usuario(String nome, String email, String provider, String providerId) {
        this(nome, email);
        this.provider = provider;
        this.providerId = providerId;
    }

    // Métodos de callback JPA
    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public LocalDateTime getUltimoLogin() {
        return ultimoLogin;
    }

    public void setUltimoLogin(LocalDateTime ultimoLogin) {
        this.ultimoLogin = ultimoLogin;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    // Métodos utilitários
    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    public boolean hasRole(Role role) {
        return this.roles.contains(role);
    }

    public boolean isAdmin() {
        return hasRole(Role.ADMIN);
    }

    public boolean isGestor() {
        return hasRole(Role.GESTOR);
    }

    public boolean isEducador() {
        return hasRole(Role.EDUCADOR);
    }

    public void registrarLogin() {
        this.ultimoLogin = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", provider='" + provider + '\'' +
                ", ativo=" + ativo +
                ", roles=" + roles +
                '}';
    }
}

