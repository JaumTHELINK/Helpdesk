package com.turmaa.helpdesk.security;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.turmaa.helpdesk.domain.enums.Perfil;

/**
 * Implementação personalizada de {@link UserDetails} para integração com o Spring Security.
 *
 * <p>
 * Esta classe representa o usuário autenticado no sistema, armazenando:
 * <ul>
 *   <li>ID do usuário</li>
 *   <li>Email (username)</li>
 *   <li>Senha</li>
 *   <li>Perfis/roles convertidos em {@link GrantedAuthority}</li>
 * </ul>
 * </p>
 *
 * <p>
 * É usada pelo Spring Security durante a autenticação e autorização,
 * permitindo verificar credenciais e permissões.
 * </p>
 */
public class UserSS implements UserDetails {

    /** Serial para controle de versão da classe em operações de serialização. */
    private static final long serialVersionUID = 1L;

    /** Identificador único do usuário no banco de dados. */
    private final Integer id;

    /** E-mail do usuário, utilizado como username para login. */
    private final String email;

    /** Senha criptografada do usuário. */
    private final String senha;

    /**
     * Coleção de autoridades (roles) do usuário.
     * <p>
     * Cada {@link Perfil} é convertido em {@link SimpleGrantedAuthority},
     * que é a representação padrão de permissões no Spring Security.
     * </p>
     */
    private final Collection<? extends GrantedAuthority> authorities;

    /**
     * Construtor que cria uma instância de {@code UserSS}.
     *
     * @param id      identificador único do usuário
     * @param email   e-mail (username) do usuário
     * @param senha   senha criptografada do usuário
     * @param perfis  conjunto de perfis/roles que serão convertidos em authorities
     */
    public UserSS(Integer id, String email, String senha, Set<Perfil> perfis) {
        super();
        this.id = id;
        this.email = email;
        this.senha = senha;
        // Converte cada Perfil em SimpleGrantedAuthority usando a descrição do enum
        this.authorities = perfis.stream()
                                 .map(x -> new SimpleGrantedAuthority(x.getDescricao()))
                                 .collect(Collectors.toSet());
    }

    /**
     * Retorna o identificador único do usuário.
     *
     * @return ID do usuário
     */
    public Integer getId() {
        return id;
    }

    /**
     * Retorna as autoridades (roles) concedidas ao usuário.
     * <p>Necessário para verificação de permissões pelo Spring Security.</p>
     *
     * @return coleção de {@link GrantedAuthority}
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * Retorna a senha do usuário.
     * <p>O Spring Security usa este valor para comparar com a senha informada no login.</p>
     *
     * @return senha criptografada
     */
    @Override
    public String getPassword() {
        return senha;
    }

    /**
     * Retorna o nome de usuário, que aqui é o e-mail.
     *
     * @return e-mail do usuário
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * Indica se a conta está expirada.
     * <p>Aqui sempre retorna {@code true}, significando que nunca expira.</p>
     *
     * @return {@code true} (conta não expirada)
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indica se a conta está bloqueada.
     * <p>Aqui sempre retorna {@code true}, significando que nunca é bloqueada.</p>
     *
     * @return {@code true} (conta não bloqueada)
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indica se as credenciais (senha) estão expiradas.
     * <p>Aqui sempre retorna {@code true}, significando que nunca expiram.</p>
     *
     * @return {@code true} (credenciais não expiradas)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indica se a conta está habilitada.
     * <p>Aqui sempre retorna {@code true}, significando que a conta está sempre ativa.</p>
     *
     * @return {@code true} (conta habilitada)
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}