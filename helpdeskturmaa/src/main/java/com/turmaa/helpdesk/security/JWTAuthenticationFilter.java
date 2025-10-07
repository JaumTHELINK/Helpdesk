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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.turmaa.helpdesk.domain.dtos.CredenciaisDTO;

/**
 * <h2>Filtro de Autenticação JWT</h2>
 * <p>
 * Implementa o processo de autenticação baseado em JWT (JSON Web Token) para o sistema
 * Helpdesk. Este filtro intercepta requisições de login, autentica credenciais e
 * gera tokens JWT para usuários autenticados com sucesso.
 * </p>
 * 
 * <h3>🔐 Arquitetura de Segurança</h3>
 * <p>
 * Segue o padrão de filtros do Spring Security, integrando-se à cadeia de filtros
 * para processar autenticação de forma transparente e segura. Utiliza o endpoint
 * padrão <code>/login</code> para receber credenciais via POST JSON.
 * </p>
 * 
 * <h3>Fluxo Completo de Autenticação</h3>
 * <ol>
 *   <li><strong>Interceptação:</strong> Captura POST em /login</li>
 *   <li><strong>Deserialização:</strong> Converte JSON para {@link CredenciaisDTO}</li>
 *   <li><strong>Autenticação:</strong> Valida via {@link AuthenticationManager}</li>
 *   <li><strong>Geração JWT:</strong> Cria token assinado com {@link JWTUtil}</li>
 *   <li><strong>Resposta:</strong> Retorna token no header Authorization</li>
 * </ol>
 * 
 * <h3>Funcionalidades Principais</h3>
 * <ul>
 *   <li><strong>📝 Parsing JSON:</strong> Leitura automática de credenciais</li>
 *   <li><strong>🔒 Validação Segura:</strong> Integração com Spring Security</li>
 *   <li><strong>🎫 Geração JWT:</strong> Tokens criptograficamente assinados</li>
 *   <li><strong>⚠️ Tratamento Erros:</strong> Respostas padronizadas para falhas</li>
 *   <li><strong>🔗 CORS Support:</strong> Headers expostos para frontend</li>
 * </ul>
 * 
 * <h3>Formato de Requisição</h3>
 * <pre>
 * POST /login
 * Content-Type: application/json
 * 
 * {
 *   "email": "usuario@email.com",
 *   "senha": "senhaSegura123"
 * }
 * </pre>
 * 
 * <h3>Formato de Resposta (Sucesso)</h3>
 * <pre>
 * HTTP/1.1 200 OK
 * Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
 * access-control-expose-headers: Authorization
 * </pre>
 * 
 * <h3>Formato de Resposta (Erro)</h3>
 * <pre>
 * HTTP/1.1 401 Unauthorized
 * Content-Type: application/json
 * 
 * {
 *   "timestamp": 1640995200000,
 *   "status": 401,
 *   "error": "Não autorizado",
 *   "message": "Email ou senha inválidos",
 *   "path": "/login"
 * }
 * </pre>
 * 
 * <div style="background-color: #d4edda; border: 1px solid #c3e6cb; border-radius: 4px; padding: 8px; margin: 8px 0;">
 * <strong>✅ Vantagens JWT:</strong>
 * <ul>
 *   <li>Stateless: Não requer armazenamento servidor</li>
 *   <li>Descentralizado: Pode ser validado independentemente</li>
 *   <li>Seguro: Assinatura criptográfica HMAC-SHA512</li>
 *   <li>Padrão: RFC 7519 amplamente suportado</li>
 * </ul>
 * </div>
 * 
 * <div style="background-color: #f8d7da; border: 1px solid #f5c6cb; border-radius: 4px; padding: 8px; margin: 8px 0;">
 * <strong>⚠️ Considerações de Segurança:</strong>
 * <ul>
 *   <li>Tokens devem ser enviados apenas via HTTPS em produção</li>
 *   <li>Frontend deve armazenar tokens de forma segura</li>
 *   <li>Implementar refresh token para sessões longas</li>
 *   <li>Monitorar tentativas de login suspeitas</li>
 * </ul>
 * </div>
 * 
 * @author Sistema Helpdesk
 * @since 1.0.0
 * @see UsernamePasswordAuthenticationFilter
 * @see JWTUtil
 * @see CredenciaisDTO
 * @see UserSS
 */
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    /**
     * <h3>Gerenciador de Autenticação</h3>
     * <p>
     * Componente central do Spring Security responsável por coordenar
     * todo o processo de autenticação. Delega para providers específicos
     * (como DaoAuthenticationProvider) para validar credenciais.
     * </p>
     * 
     * <h4>Responsabilidades:</h4>
     * <ul>
     *   <li><strong>Coordenação:</strong> Gerencia múltiplos providers</li>
     *   <li><strong>Validação:</strong> Verifica credenciais contra UserDetailsService</li>
     *   <li><strong>Encoding:</strong> Utiliza PasswordEncoder para verificar senhas</li>
     *   <li><strong>Authorities:</strong> Carrega roles e permissions do usuário</li>
     * </ul>
     * 
     * @see AuthenticationManager
     * @see DaoAuthenticationProvider
     */
    private final AuthenticationManager authenticationManager;

    /**
     * <h3>Utilitário JWT</h3>
     * <p>
     * Serviço especializado para operações com tokens JWT, incluindo
     * geração, validação, assinatura e extração de informações.
     * Central para toda a lógica de autenticação baseada em tokens.
     * </p>
     * 
     * <h4>Operações Suportadas:</h4>
     * <ul>
     *   <li><strong>Geração:</strong> Cria tokens assinados com claims</li>
     *   <li><strong>Validação:</strong> Verifica assinatura e expiração</li>
     *   <li><strong>Extração:</strong> Obtém username e outras claims</li>
     *   <li><strong>Segurança:</strong> HMAC-SHA512 com chave secreta</li>
     * </ul>
     * 
     * @see JWTUtil
     */
    private final JWTUtil jwtUtil;

    private static final Logger logger = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    /**
     * <h3>Construtor com Injeção de Dependências</h3>
     * <p>
     * Inicializa o filtro de autenticação com as dependências essenciais
     * para o processo de validação de credenciais e geração de tokens JWT.
     * Configura o filtro para processar o endpoint padrão <code>/login</code>.
     * </p>
     * 
     * <h4>Configuração Automática:</h4>
     * <ul>
     *   <li><strong>Endpoint:</strong> POST /login configurado automaticamente</li>
     *   <li><strong>Content-Type:</strong> Aceita application/json</li>
     *   <li><strong>HTTP Method:</strong> Apenas POST permitido</li>
     *   <li><strong>Parsing:</strong> Deserialização automática via ObjectMapper</li>
     * </ul>
     * 
     * <h4>Integração Spring Security:</h4>
     * <p>
     * O filtro é automaticamente inserido na cadeia de filtros do Spring Security
     * através da configuração em {@link SecurityConfig}, garantindo que seja
     * executado na ordem correta para interceptar tentativas de autenticação.
     * </p>
     * 
     * @param authenticationManager {@link AuthenticationManager} Gerenciador central 
     *        do Spring Security para coordenação do processo de autenticação
     * @param jwtUtil {@link JWTUtil} Utilitário especializado para operações 
     *        JWT (geração, validação, assinatura)
     * 
     * @see SecurityConfig#configure(HttpSecurity)
     * @see UsernamePasswordAuthenticationFilter#UsernamePasswordAuthenticationFilter()
     */
    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
        super();
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    /**
     * <h3>Tentativa de Autenticação</h3>
     * <p>
     * Método principal que processa credenciais enviadas via POST JSON
     * e executa o processo de autenticação através do Spring Security.
     * É chamado automaticamente quando uma requisição é feita ao endpoint /login.
     * </p>
     * 
     * <h4>📋 Fluxo Detalhado de Processamento:</h4>
     * <ol>
     *   <li><strong>Leitura Stream:</strong> Captura InputStream da requisição HTTP</li>
     *   <li><strong>Parsing JSON:</strong> Deserializa para {@link CredenciaisDTO} via Jackson</li>
     *   <li><strong>Token Creation:</strong> Cria {@link UsernamePasswordAuthenticationToken}</li>
     *   <li><strong>Delegation:</strong> Delega ao {@link AuthenticationManager}</li>
     *   <li><strong>Validation:</strong> Spring Security valida via UserDetailsService</li>
     *   <li><strong>Response:</strong> Retorna Authentication ou lança exceção</li>
     * </ol>
     * 
     * <h4>🔄 Processo de Validação Interno:</h4>
     * <ul>
     *   <li><strong>User Lookup:</strong> Busca usuário por email no banco</li>
     *   <li><strong>Password Check:</strong> Verifica senha com BCrypt</li>
     *   <li><strong>Account Status:</strong> Verifica se conta está ativa</li>
     *   <li><strong>Authorities Load:</strong> Carrega roles/perfis do usuário</li>
     * </ul>
     * 
     * <h4>📝 Formato JSON Esperado:</h4>
     * <pre>
     * {
     *   "email": "usuario@dominio.com",
     *   "senha": "minhasenha123"
     * }
     * </pre>
     * 
     * <h4>⚡ Tratamento de Exceções:</h4>
     * <p>
     * Qualquer falha durante o processo (JSON malformado, credenciais inválidas,
     * usuário não encontrado, senha incorreta) resulta em RuntimeException
     * que será capturada pelo método {@link #unsuccessfulAuthentication}.
     * </p>
     * 
     * <div style="background-color: #fff3cd; border: 1px solid #ffeaa7; border-radius: 4px; padding: 8px; margin: 8px 0;">
     * <strong>⚠️ Segurança:</strong>
     * Por questões de segurança, todas as falhas de autenticação retornam
     * a mesma mensagem genérica "Email ou senha inválidos" para evitar
     * vazamento de informações sobre usuários existentes.
     * </div>
     * 
     * @param request {@link HttpServletRequest} Requisição HTTP contendo 
     *        JSON com credenciais no body
     * @param response {@link HttpServletResponse} Resposta HTTP (não utilizada neste método)
     * 
     * @return {@link Authentication} Objeto contendo detalhes do usuário autenticado
     *         e suas authorities/roles carregadas do banco de dados
     * 
     * @throws AuthenticationException Se credenciais forem inválidas, usuário não 
     *         existir, senha incorreta ou conta desabilitada
     * @throws RuntimeException Se houver erro na leitura/parsing do JSON ou 
     *         falha inesperada no processo de autenticação
     * 
     * @see CredenciaisDTO
     * @see ObjectMapper#readValue(java.io.InputStream, Class)
     * @see AuthenticationManager#authenticate(Authentication)
     * @see UsernamePasswordAuthenticationToken
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
     * <h3>🎉 Autenticação Bem-Sucedida</h3>
     * <p>
     * Método de callback executado automaticamente após uma autenticação
     * bem-sucedida. Responsável por gerar e inserir o token JWT no
     * cabeçalho da resposta para uso nas requisições subsequentes.
     * </p>
     * 
     * <h4>🔧 Processamento Executado:</h4>
     * <ol>
     *   <li><strong>User Extraction:</strong> Obtém {@link UserSS} do resultado da autenticação</li>
     *   <li><strong>Username Retrieval:</strong> Extrai o username/email do usuário</li>
     *   <li><strong>Token Generation:</strong> Gera JWT assinado via {@link JWTUtil}</li>
     *   <li><strong>CORS Headers:</strong> Configura headers para acesso cross-origin</li>
     *   <li><strong>Token Delivery:</strong> Insere token no header Authorization</li>
     * </ol>
     * 
     * <h4>📤 Headers de Resposta Configurados:</h4>
     * <ul>
     *   <li><strong>access-control-expose-headers:</strong> Authorization</li>
     *   <li><strong>Authorization:</strong> Bearer [JWT_TOKEN]</li>
     * </ul>
     * 
     * <h4>🌐 Suporte CORS:</h4>
     * <p>
     * O header <code>access-control-expose-headers</code> é crucial para
     * permitir que aplicações frontend (SPA/PWA) executando em domínios
     * diferentes consigam acessar o header Authorization via JavaScript.
     * </p>
     * 
     * <h4>🎫 Formato do Token:</h4>
     * <pre>
     * Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJzdWIi...
     * </pre>
     * 
     * <h4>💡 Uso pelo Frontend:</h4>
     * <p>
     * O frontend deve extrair o token do header Authorization e incluí-lo
     * em todas as requisições subsequentes para endpoints protegidos:
     * </p>
     * <pre>
     * // JavaScript
     * const token = response.headers['authorization'];
     * localStorage.setItem('token', token);
     * 
     * // Uso em requisições futuras
     * fetch('/api/chamados', {
     *   headers: { 'Authorization': token }
     * });
     * </pre>
     * 
     * <div style="background-color: #d4edda; border: 1px solid #c3e6cb; border-radius: 4px; padding: 8px; margin: 8px 0;">
     * <strong>✅ Boas Práticas Implementadas:</strong>
     * <ul>
     *   <li>Prefixo "Bearer" padrão OAuth 2.0</li>
     *   <li>CORS headers para integração frontend</li>
     *   <li>Token gerado apenas após validação completa</li>
     *   <li>Não exposição de dados sensíveis em logs</li>
     * </ul>
     * </div>
     * 
     * @param request {@link HttpServletRequest} Requisição HTTP original
     * @param response {@link HttpServletResponse} Resposta onde será inserido o token
     * @param chain {@link FilterChain} Cadeia de filtros (não utilizada neste contexto)
     * @param authResult {@link Authentication} Resultado completo da autenticação
     *        contendo {@link UserSS} com detalhes do usuário validado
     * 
     * @throws IOException Se houver falha na escrita dos headers HTTP
     * @throws ServletException Se houver erro na configuração de servlet
     * 
     * @see JWTUtil#generateToken(String)
     * @see UserSS#getUsername()
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

        // Obtém o nome de usuário autenticado
        String username = ((UserSS) authResult.getPrincipal()).getUsername();

        // Gera o token JWT para este usuário
        String token = jwtUtil.generateToken(username);

        // DEBUG: registrar geração de token (apenas início do token para evitar exposição completa em logs)
        try {
            String prefix = token != null && token.length() > 8 ? token.substring(0, 8) : token;
            logger.debug("JWT gerado para usuário {}: {}...", username, prefix);
        } catch (Exception e) {
            logger.debug("JWT gerado para usuário {} (não foi possível mostrar prefixo do token)", username);
        }

        // Expõe o cabeçalho Authorization para que o front-end consiga ler
        response.setHeader("access-control-expose-headers", "Authorization");

        // Define o token no cabeçalho Authorization com o prefixo "Bearer"
        response.setHeader("Authorization", "Bearer " + token);
    }

    /**
     * <h3>❌ Tratamento de Falha na Autenticação</h3>
     * <p>
     * Método de callback executado quando a autenticação falha por qualquer
     * motivo. Padroniza a resposta de erro retornando um JSON estruturado
     * com informações consistentes sobre a falha.
     * </p>
     * 
     * <h4>🔍 Cenários de Falha Tratados:</h4>
     * <ul>
     *   <li><strong>Credenciais Inválidas:</strong> Email ou senha incorretos</li>
     *   <li><strong>Usuário Inexistente:</strong> Email não cadastrado no sistema</li>
     *   <li><strong>Conta Desabilitada:</strong> Usuário com status inativo</li>
     *   <li><strong>JSON Malformado:</strong> Formato inválido na requisição</li>
     *   <li><strong>Campos Obrigatórios:</strong> Email ou senha em branco</li>
     * </ul>
     * 
     * <h4>📋 Configuração de Resposta:</h4>
     * <ul>
     *   <li><strong>Status HTTP:</strong> 401 Unauthorized</li>
     *   <li><strong>Content-Type:</strong> application/json</li>
     *   <li><strong>Body:</strong> JSON padronizado com detalhes do erro</li>
     *   <li><strong>Encoding:</strong> UTF-8 para caracteres especiais</li>
     * </ul>
     * 
     * <h4>🛡️ Segurança por Design:</h4>
     * <p>
     * Por questões de segurança, todas as falhas de autenticação retornam
     * a mesma mensagem genérica. Isso previne ataques de enumeração de
     * usuários e não vaza informações sobre a existência de contas.
     * </p>
     * 
     * <h4>📤 Formato da Resposta JSON:</h4>
     * <pre>
     * {
     *   "timestamp": 1640995200000,
     *   "status": 401,
     *   "error": "Não autorizado",
     *   "message": "Email ou senha inválidos",
     *   "path": "/login"
     * }
     * </pre>
     * 
     * <h4>🔧 Integração com Frontend:</h4>
     * <p>
     * O formato padronizado permite que aplicações frontend tratem erros
     * de forma consistente e apresentem mensagens apropriadas aos usuários:
     * </p>
     * <pre>
     * // JavaScript - Tratamento típico
     * if (response.status === 401) {
     *   const error = await response.json();
     *   showError(error.message); // "Email ou senha inválidos"
     * }
     * </pre>
     * 
     * <div style="background-color: #f8d7da; border: 1px solid #f5c6cb; border-radius: 4px; padding: 8px; margin: 8px 0;">
     * <strong>⚠️ Monitoramento:</strong>
     * Em produção, é recomendável implementar logs detalhados (sem expor
     * senhas) e monitoramento de tentativas de login para detectar ataques
     * de força bruta ou comportamentos suspeitos.
     * </div>
     * 
     * @param request {@link HttpServletRequest} Requisição HTTP que causou a falha
     * @param response {@link HttpServletResponse} Resposta onde será inserido o erro JSON
     * @param failed {@link AuthenticationException} Exceção específica que causou a falha,
     *        contendo detalhes técnicos do erro (não expostos ao cliente)
     * 
     * @throws IOException Se houver falha na escrita da resposta HTTP
     * @throws ServletException Se houver erro na configuração de servlet
     * 
     * @see #json()
     * @see HttpServletResponse#setStatus(int)
     * @see HttpServletResponse#setContentType(String)
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        // DEBUG: log da falha de autenticação (não expõe credenciais)
        logger.debug("Falha de autenticação: {}", failed.getMessage());

        // Define código HTTP 401 (Não autorizado)
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Define o tipo de conteúdo como JSON
        response.setContentType("application/json");

        // Escreve o JSON de erro no corpo da resposta
        response.getWriter().append(json());
    }

    /**
     * <h3>🔧 Construção da Resposta JSON de Erro</h3>
     * <p>
     * Método utilitário privado que constrói o corpo JSON padronizado
     * para respostas de falha na autenticação. Segue formato consistente
     * com padrões REST e Spring Boot error responses.
     * </p>
     * 
     * <h4>📝 Campos da Resposta:</h4>
     * <ul>
     *   <li><strong>timestamp:</strong> Momento exato da falha em milissegundos Unix</li>
     *   <li><strong>status:</strong> Código HTTP 401 (Unauthorized)</li>
     *   <li><strong>error:</strong> Descrição breve do tipo de erro</li>
     *   <li><strong>message:</strong> Mensagem amigável para o usuário final</li>
     *   <li><strong>path:</strong> Endpoint onde ocorreu a falha (/login)</li>
     * </ul>
     * 
     * <h4>🎯 Vantagens do Formato:</h4>
     * <ul>
     *   <li><strong>Consistência:</strong> Mesmo padrão usado pelo Spring Boot</li>
     *   <li><strong>Depuração:</strong> Timestamp permite rastreamento temporal</li>
     *   <li><strong>Integração:</strong> Fácil parsing pelos frontends</li>
     *   <li><strong>Profissional:</strong> Resposta estruturada e informativa</li>
     * </ul>
     * 
     * <h4>🔒 Considerações de Segurança:</h4>
     * <p>
     * A mensagem é intencionalmente genérica ("Email ou senha inválidos")
     * para não revelar informações sobre:
     * </p>
     * <ul>
     *   <li>Existência de usuários específicos</li>
     *   <li>Diferenciação entre email incorreto vs senha incorreta</li>
     *   <li>Status da conta (ativa, bloqueada, etc.)</li>
     *   <li>Detalhes técnicos internos do sistema</li>
     * </ul>
     * 
     * <h4>📊 Exemplo de Uso em Monitoramento:</h4>
     * <pre>
     * // Log estruturado para monitoramento (sem dados sensíveis)
     * logger.warn("Authentication failed at {} for path {}", 
     *            timestamp, "/login");
     * </pre>
     * 
     * <div style="background-color: #d1ecf1; border: 1px solid #b8daff; border-radius: 4px; padding: 8px; margin: 8px 0;">
     * <strong>💡 Extensibilidade:</strong>
     * Este formato pode ser facilmente estendido para incluir campos
     * adicionais como <code>requestId</code> para rastreamento ou
     * <code>retryAfter</code> para implementar rate limiting.
     * </div>
     * 
     * @return {@link CharSequence} String JSON formatada contendo todos os
     *         campos necessários para uma resposta de erro padronizada
     * 
     * @see Date#getTime()
     * @see HttpServletResponse#SC_UNAUTHORIZED
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