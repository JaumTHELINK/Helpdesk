package com.turmaa.helpdesk.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.turmaa.helpdesk.domain.dtos.CredenciaisDTO;

/**
 * Filtro de autenticação JWT.
 *
 * <p>
 * Responsável por:
 * <ul>
 *   <li>Ler as credenciais (email e senha) enviadas no corpo da requisição.</li>
 *   <li>Autenticar o usuário via {@link AuthenticationManager} do Spring Security.</li>
 *   <li>Gerar e adicionar o token JWT no cabeçalho da resposta quando a autenticação for bem-sucedida.</li>
 *   <li>Retornar um JSON de erro quando a autenticação falhar.</li>
 * </ul>
 * </p>
 *
 * <p>Extende {@link UsernamePasswordAuthenticationFilter}, que já provê o endpoint padrão <code>/login</code>.</p>
 */
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    /** Gerenciador de autenticação do Spring Security. */
    private final AuthenticationManager authenticationManager;

    /** Utilitário responsável por gerar e validar tokens JWT. */
    private final JWTUtil jwtUtil;

    /**
     * Construtor que injeta as dependências principais.
     *
     * @param autheticationManager gerenciador de autenticação usado para validar credenciais
     * @param jwtUtil utilitário para geração de tokens JWT
     */
    public JWTAuthenticationFilter(AuthenticationManager autheticationManager, JWTUtil jwtUtil) {
        super();
        this.authenticationManager = autheticationManager;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Tenta autenticar o usuário a partir dos dados enviados na requisição.
     *
     * <p>
     * Passo a passo:
     * </p>
     * <ol>
     *   <li>Lê o corpo da requisição (JSON) e converte para {@link CredenciaisDTO} usando {@link ObjectMapper}.</li>
     *   <li>Cria um {@link UsernamePasswordAuthenticationToken} com email, senha e lista vazia de authorities.</li>
     *   <li>Chama o {@link AuthenticationManager} para autenticar o token.</li>
     *   <li>Retorna o objeto {@link Authentication} caso as credenciais sejam válidas.</li>
     * </ol>
     *
     * @param request  requisição HTTP
     * @param response resposta HTTP
     * @return objeto de autenticação caso bem-sucedido
     * @throws AuthenticationException se as credenciais forem inválidas
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            // Lê o JSON enviado no corpo da requisição e converte para CredenciaisDTO
            CredenciaisDTO creds = new ObjectMapper().readValue(request.getInputStream(), CredenciaisDTO.class);

            // Cria um token de autenticação com email, senha e sem roles (serão carregadas pelo UserDetailsService)
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(creds.getEmail(), creds.getSenha(), new ArrayList<>());

            // Tenta autenticar usando o AuthenticationManager configurado no Spring Security
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            return authentication;
        } catch (Exception e) {
            // Em caso de falha na leitura ou autenticação, lança uma RuntimeException
            throw new RuntimeException(e);
        }
    }

    /**
     * Chamado quando a autenticação é bem-sucedida.
     *
     * <p>
     * Responsável por gerar o token JWT e adicioná-lo no cabeçalho da resposta.
     * </p>
     *
     * @param request    requisição HTTP
     * @param response   resposta HTTP
     * @param chain      cadeia de filtros
     * @param authResult resultado da autenticação (contém os detalhes do usuário)
     * @throws IOException em caso de erro de I/O
     * @throws ServletException em caso de erro de servlet
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

        // Obtém o nome de usuário autenticado
        String username = ((UserSS) authResult.getPrincipal()).getUsername();

        // Gera o token JWT para este usuário
        String token = jwtUtil.generateToken(username);

        // Expõe o cabeçalho Authorization para que o front-end consiga ler
        response.setHeader("access-control-expose-headers", "Authorization");

        // Define o token no cabeçalho Authorization com o prefixo "Bearer"
        response.setHeader("Authorization", "Bearer " + token);
    }

    /**
     * Chamado quando a autenticação falha.
     *
     * <p>
     * Retorna status 401 e um corpo JSON com informações do erro.
     * </p>
     *
     * @param request  requisição HTTP
     * @param response resposta HTTP
     * @param failed   exceção gerada na falha de autenticação
     * @throws IOException em caso de erro de I/O
     * @throws ServletException em caso de erro de servlet
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {

        // Define código HTTP 401 (Não autorizado)
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Define o tipo de conteúdo como JSON
        response.setContentType("application/json");

        // Escreve o JSON de erro no corpo da resposta
        response.getWriter().append(json());
    }

    /**
     * Monta o corpo JSON de erro para respostas de autenticação não autorizada.
     *
     * @return sequência de caracteres contendo o JSON com informações do erro
     */
    private CharSequence json() {
        long date = new Date().getTime();
        return "{"
                + "\"timestamp\": " + date + ", "
                + "\"status\": 401, "
                + "\"error\": \"Não autorizado\", "
                + "\"message\": \"Email ou senha inválidos\", "
                + "\"path\": \"/login\""
                + "}";
    }
}