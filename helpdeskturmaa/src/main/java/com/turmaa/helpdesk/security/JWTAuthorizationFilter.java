package com.turmaa.helpdesk.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

/**
 * Filtro responsável por autorizar requisições com base em um token JWT.
 *
 * <p>Este filtro é executado a cada requisição após o {@link JWTAuthenticationFilter}.
 * Ele verifica se o cabeçalho HTTP <b>Authorization</b> contém um token
 * válido no formato <code>Bearer &lt;token&gt;</code>. Caso seja válido,
 * autentica o usuário no contexto de segurança do Spring.</p>
 *
 * <p>Extende {@link BasicAuthenticationFilter} para se integrar ao fluxo
 * padrão de filtros do Spring Security.</p>
 */
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    /** Utilitário para validar e extrair informações do token JWT. */
    private final JWTUtil jwtUtil;

    /** Serviço que carrega os detalhes de um usuário (nome, senha, perfis). */
    private final UserDetailsService userDetailsService;

    /**
     * Construtor que injeta as dependências necessárias.
     *
     * @param authenticationManager Gerenciador de autenticação do Spring Security.
     * @param jwtUtil Utilitário para manipulação de tokens JWT.
     * @param userDetailsService Serviço para buscar informações do usuário no banco/detalhes.
     */
    public JWTAuthorizationFilter(AuthenticationManager authenticationManager,
                                  JWTUtil jwtUtil,
                                  UserDetailsService userDetailsService) {
        super(authenticationManager);
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Método principal do filtro que intercepta cada requisição HTTP.
     *
     * <p>Fluxo passo a passo:
     * <ol>
     *   <li>Lê o cabeçalho "Authorization" da requisição.</li>
     *   <li>Verifica se o cabeçalho não é nulo e começa com "Bearer ".</li>
     *   <li>Extrai o token (removendo o prefixo "Bearer ") e tenta autenticar.</li>
     *   <li>Se a autenticação for bem-sucedida, registra o usuário no
     *       {@link SecurityContextHolder} para que o Spring conheça o usuário autenticado.</li>
     *   <li>Continua a cadeia de filtros com {@code chain.doFilter()}.</li>
     * </ol>
     * </p>
     *
     * @param request  requisição HTTP recebida
     * @param response resposta HTTP a ser enviada
     * @param chain    cadeia de filtros do Spring Security
     * @throws IOException em caso de erro de leitura/escrita
     * @throws ServletException em caso de erro no processamento do filtro
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws IOException, ServletException {

        // Recupera o valor do cabeçalho Authorization (padrão: "Bearer <token>")
        String header = request.getHeader("Authorization");

        // Verifica se o cabeçalho existe e começa com "Bearer "
        if (header != null && header.startsWith("Bearer ")) {
            // Remove o prefixo "Bearer " e obtém um token de autenticação
            UsernamePasswordAuthenticationToken authToken = getAuthentication(header.substring(7));

            // Se o token for válido, registra a autenticação no contexto de segurança
            if (authToken != null) {
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Segue para o próximo filtro da cadeia
        chain.doFilter(request, response);
    }

    /**
     * Tenta autenticar o usuário a partir do token JWT.
     *
     * <p>Processo:
     * <ul>
     *   <li>Valida se o token é legítimo e não expirou usando {@link JWTUtil}.</li>
     *   <li>Extrai o nome de usuário (subject) do token.</li>
     *   <li>Busca os detalhes do usuário (perfis/roles) via {@link UserDetailsService}.</li>
     *   <li>Retorna um {@link UsernamePasswordAuthenticationToken} com as autoridades
     *       do usuário, permitindo ao Spring identificar permissões em endpoints.</li>
     * </ul>
     * </p>
     *
     * @param token token JWT já sem o prefixo "Bearer "
     * @return objeto de autenticação do Spring ou {@code null} se o token for inválido
     */
    private UsernamePasswordAuthenticationToken getAuthentication(String token) {

        // Verifica se o token é válido (assinatura e data de expiração)
        if (jwtUtil.tokenValido(token)) {
            // Obtém o nome de usuário (subject) do token
            String username = jwtUtil.getUsername(token);

            // Carrega as informações completas do usuário, inclusive authorities
            UserDetails details = userDetailsService.loadUserByUsername(username);

            // Cria o objeto de autenticação com usuário e autoridades
            return new UsernamePasswordAuthenticationToken(
                    details.getUsername(), null, details.getAuthorities());
        }

        // Retorna null se o token não for válido
        return null;
    }
}