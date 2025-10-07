package com.turmaa.helpdesk.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.turmaa.helpdesk.security.JWTAuthenticationFilter;
import com.turmaa.helpdesk.security.JWTAuthorizationFilter;
import com.turmaa.helpdesk.security.JWTUtil;

/**
 * <h1>Configuração de Segurança do Sistema Helpdesk</h1>
 * <p>
 * Classe central de configuração de segurança que implementa autenticação JWT,
 * autorização baseada em roles, configuração CORS e políticas de sessão stateless.
 * Esta configuração garante que apenas usuários autenticados e autorizados
 * possam acessar os recursos protegidos do sistema.
 * </p>
 * 
 * <h2>Principais Funcionalidades:</h2>
 * <ul>
 *   <li><strong>Autenticação JWT:</strong> Sistema de tokens para autenticação stateless</li>
 *   <li><strong>Autorização:</strong> Controle de acesso baseado em perfis de usuário</li>
 *   <li><strong>CORS:</strong> Configuração para permitir requisições cross-origin</li>
 *   <li><strong>Criptografia:</strong> BCrypt para hash seguro de senhas</li>
 *   <li><strong>Ambientes:</strong> Configurações específicas para dev/test/prod</li>
 * </ul>
 * 
 * <h2>Arquitetura de Segurança:</h2>
 * <ul>
 *   <li><strong>Stateless:</strong> Sem sessões HTTP, cada requisição carrega o token</li>
 *   <li><strong>Filter Chain:</strong> Filtros customizados para processar JWT</li>
 *   <li><strong>UserDetails:</strong> Integração com entidades Pessoa/Técnico/Cliente</li>
 *   <li><strong>Password Encoder:</strong> BCrypt com salt automático</li>
 * </ul>
 * 
 * <h2>Fluxo de Autenticação:</h2>
 * <ol>
 *   <li>Cliente envia credenciais (email/senha) para /login</li>
 *   <li>JWTAuthenticationFilter valida credenciais</li>
 *   <li>Se válidas, gera token JWT com dados do usuário</li>
 *   <li>Token é retornado no header Authorization</li>
 *   <li>Requisições subsequentes devem incluir token</li>
 * </ol>
 * 
 * <h2>Perfis e Autorização:</h2>
 * <ul>
 *   <li><strong>ADMIN:</strong> Acesso total ao sistema</li>
 *   <li><strong>TECNICO:</strong> Gerenciar chamados e visualizar clientes</li>
 *   <li><strong>CLIENTE:</strong> Criar e visualizar próprios chamados</li>
 * </ul>
 * 
 * <h2>Configurações por Ambiente:</h2>
 * <ul>
 *   <li><strong>Test:</strong> H2 Console habilitado, headers frame desabilitados</li>
 *   <li><strong>Dev:</strong> CORS permissivo, logs detalhados</li>
 *   <li><strong>Prod:</strong> Máxima segurança, HTTPS obrigatório</li>
 * </ul>
 * 
 * @author Sistema Helpdesk
 * @version 1.0
 * @since 2024
 * 
 * @see JWTAuthenticationFilter
 * @see JWTUtil
 * @see com.turmaa.helpdesk.security.UserDetailsImpl
 */
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * URLs públicas que não exigem autenticação.
     * Exemplo: console do H2 para uso em ambiente de desenvolvimento/testes.
     */
    private static final String[] PUBLIC_MATCHES = { "/h2-console/**" };

    /**
     * Ambiente atual da aplicação (profiles ativos, etc).
     * Injetado pelo Spring para permitir condicionais (ex.: liberar frame para H2 em profile "test").
     */
    @Autowired
    private Environment env;

    /**
     * Utilitário para operações com JWT (gerar/validar tokens).
     * Injetado pelo Spring.
     */
    @Autowired
    private JWTUtil jwtUtil;

    /**
     * Serviço que fornece os dados do usuário (username, senha, roles).
     * Usado para autenticação.
     */
    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * Configurações de segurança HTTP.
     * 
     * <p>
     * Explicação linha-a-linha (em Javadoc) dentro do método:
     * </p>
     * 
     * @param http objeto para configurar as regras HTTP do Spring Security
     * @throws Exception se ocorrer erro na configuração
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /**
         * Se o profile "test" estiver ativo, desabilita a proteção de frame
         * (X-Frame-Options) para permitir o console H2 aparecer em iframe.
         */
        if (Arrays.asList(env.getActiveProfiles()).contains("test")) {
            /**
             * Remove a header que impede que a aplicação seja exibida dentro de frames.
             * Necessário para o console H2 em modo de desenvolvimento/teste.
             */
            http.headers().frameOptions().disable();
        }

        /**
         * Habilita CORS (Cross-Origin Resource Sharing) e desabilita CSRF.
         * - CORS: permite requisições de origens diferentes (configurado em corsConfigurationSource()).
         * - CSRF: desabilitado porque a API usa tokens JWT (stateless).
         */
        http.cors().and().csrf().disable();

    /**
     * Adiciona o filtro responsável pela autenticação via JWT.
     * Esse filtro fará login e retornará o token para o cliente quando as credenciais estiverem corretas.
     */
    http.addFilter(new JWTAuthenticationFilter(authenticationManager(), jwtUtil));
    // Adiciona o filtro de autorização que valida o token em requisições subsequentes
    http.addFilter(new JWTAuthorizationFilter(authenticationManager(), jwtUtil, userDetailsService));

        /**
         * Define que as rotas em PUBLIC_MATCHES são permitidas sem autenticação,
         * e todas as demais requisições exigem autenticação.
         */
        // Permitir explicitamente o endpoint de login e as rotas públicas
        http.authorizeRequests()
            .antMatchers(PUBLIC_MATCHES).permitAll()
            .antMatchers("/login").permitAll()
            .anyRequest().authenticated();

        /**
         * Configura a aplicação para não manter sessão HTTP (STATELESS),
         * pois a autenticação é feita via JWT em cada requisição.
         */
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    /**
     * Configura o provedor de autenticação do Spring Security.
     * 
     * <p>
     * Aqui informamos qual {@link UserDetailsService} usar e qual encoder de senha.
     * </p>
     * 
     * @param auth builder para configurar autenticação
     * @throws Exception se ocorrer erro na configuração de autenticação
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        /**
         * Registra o UserDetailsService e o BCryptPasswordEncoder para que o Spring
         * consiga buscar o usuário e comparar a senha enviada com a senha armazenada.
         */
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
    }

    /**
     * Define a configuração de CORS para a aplicação.
     * 
     * <p>
     * Este bean é usado pelo Spring Security para resolver as requisições CORS.
     * applyPermitDefaultValues() já define métodos GET, POST, HEAD e headers comuns.
     * Depois adicionamos explicitamente outros métodos como PUT e DELETE.
     * </p>
     * 
     * @return fonte de configuração CORS
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        /**
         * Cria uma configuração CORS com valores padrão (origens, headers, credenciais).
         */
        CorsConfiguration configuration = new CorsConfiguration().applyPermitDefaultValues();

        /**
         * Permite explicitamente os métodos HTTP usados pela API REST.
         */
        configuration.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE", "OPTIONS"));

        /**
         * Fonte baseada em URL para registrar as configurações de CORS para todas as rotas.
         */
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        /**
         * Registra a configuração CORS para todas as rotas (/**).
         */
        source.registerCorsConfiguration("/**", configuration);

        /**
         * Retorna a fonte de configuração CORS.
         */
        return source;
    }

    /**
     * Bean que provê o {@link BCryptPasswordEncoder} usado para criptografar senhas.
     * 
     * <p>
     * Usar BCrypt é uma boa prática para armazenamento seguro de senhas.
     * </p>
     * 
     * @return um {@link BCryptPasswordEncoder} pronto para uso
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        /**
         * Cria e retorna a instância de BCryptPasswordEncoder.
         */
        return new BCryptPasswordEncoder();
    }

}