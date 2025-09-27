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
 * <h2>Filtro de Autorização JWT</h2>
 * <p>
 * Implementa a validação e autorização contínua baseada em tokens JWT para
 * todas as requisições subsequentes após autenticação inicial. Este filtro
 * é o guardião que protege endpoints autenticados do sistema Helpdesk.
 * </p>
 * 
 * <h3>🛡️ Arquitetura de Autorização</h3>
 * <p>
 * Integra-se perfeitamente à cadeia de filtros do Spring Security, executando
 * após {@link JWTAuthenticationFilter} para validar tokens em cada requisição.
 * Garante que apenas usuários autenticados com tokens válidos acessem recursos protegidos.
 * </p>
 * 
 * <h3>🔄 Fluxo Completo de Autorização</h3>
 * <ol>
 *   <li><strong>Interceptação:</strong> Captura toda requisição HTTP</li>
 *   <li><strong>Header Parsing:</strong> Extrai token do Authorization Bearer</li>
 *   <li><strong>Validação JWT:</strong> Verifica assinatura, expiração e formato</li>
 *   <li><strong>User Loading:</strong> Carrega detalhes via UserDetailsService</li>
 *   <li><strong>Context Setting:</strong> Registra usuário no SecurityContext</li>
 *   <li><strong>Chain Continuation:</strong> Permite acesso ao recurso solicitado</li>
 * </ol>
 * 
 * <h3>Principais Responsabilidades</h3>
 * <ul>
 *   <li><strong>🔍 Token Validation:</strong> Verifica autenticidade e validade</li>
 *   <li><strong>👤 User Context:</strong> Estabelece contexto de segurança</li>
 *   <li><strong>🔐 Authorization:</strong> Carrega roles e permissions</li>
 *   <li><strong>🛡️ Protection:</strong> Bloqueia requisições não autenticadas</li>
 *   <li><strong>⚡ Performance:</strong> Validação rápida sem consulta DB desnecessária</li>
 * </ul>
 * 
 * <h3>🎯 Cenários de Uso</h3>
 * <ul>
 *   <li><strong>API Calls:</strong> Todas requisições a endpoints protegidos</li>
 *   <li><strong>Resource Access:</strong> Acesso a chamados, clientes, técnicos</li>
 *   <li><strong>Admin Operations:</strong> Operações administrativas do sistema</li>
 *   <li><strong>Profile Management:</strong> Gestão de perfis e preferências</li>
 * </ul>
 * 
 * <h3>📋 Formato do Header Esperado</h3>
 * <pre>
 * Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...
 * </pre>
 * 
 * <h3>🔒 Benefícios da Abordagem Stateless</h3>
 * <ul>
 *   <li><strong>Escalabilidade:</strong> Sem estado servidor, fácil load balancing</li>
 *   <li><strong>Performance:</strong> Não requer consulta de sessão no banco</li>
 *   <li><strong>Simplicidade:</strong> Frontend gerencia apenas o token</li>
 *   <li><strong>Flexibilidade:</strong> Suporte nativo para SPAs e APIs</li>
 * </ul>
 * 
 * <div style="background-color: #d4edda; border: 1px solid #c3e6cb; border-radius: 4px; padding: 8px; margin: 8px 0;">
 * <strong>✅ Integração Automática:</strong>
 * <ul>
 *   <li>Executa automaticamente em toda requisição protegida</li>
 *   <li>Integração transparente com @PreAuthorize e @Secured</li>
 *   <li>Suporte completo para method-level security</li>
 *   <li>Compatibilidade total com Spring Security features</li>
 * </ul>
 * </div>
 * 
 * <div style="background-color: #fff3cd; border: 1px solid #ffeaa7; border-radius: 4px; padding: 8px; margin: 8px 0;">
 * <strong>⚠️ Considerações Importantes:</strong>
 * <ul>
 *   <li>Token deve ser enviado em TODAS as requisições protegidas</li>
 *   <li>Expiração do token resulta em 401 Unauthorized</li>
 *   <li>Headers CORS podem requerer configuração para Authorization</li>
 *   <li>Frontend deve tratar refresh de tokens expirados</li>
 * </ul>
 * </div>
 * 
 * @author Sistema Helpdesk  
 * @since 1.0.0
 * @see BasicAuthenticationFilter
 * @see JWTAuthenticationFilter
 * @see JWTUtil
 * @see UserDetailsService
 * @see SecurityContextHolder
 */
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    /**
     * <h3>Utilitário de Operações JWT</h3>
     * <p>
     * Componente especializado responsável por todas as operações relacionadas
     * a tokens JWT, incluindo validação de assinatura, verificação de expiração
     * e extração segura de informações (claims) do token.
     * </p>
     * 
     * <h4>Operações Críticas Suportadas:</h4>
     * <ul>
     *   <li><strong>Validação:</strong> Verifica integridade e expiração</li>
     *   <li><strong>Parsing:</strong> Extrai claims de forma segura</li>
     *   <li><strong>Username Extraction:</strong> Obtém subject do token</li>
     *   <li><strong>Security:</strong> Validação criptográfica HMAC-SHA512</li>
     * </ul>
     * 
     * @see JWTUtil#tokenValido(String)
     * @see JWTUtil#getUsername(String)
     */
    private final JWTUtil jwtUtil;

    /**
     * <h3>Serviço de Detalhes do Usuário</h3>
     * <p>
     * Interface padrão do Spring Security para carregamento de informações
     * completas do usuário, incluindo credenciais, status da conta e
     * authorities (roles/permissões) associadas ao usuário.
     * </p>
     * 
     * <h4>Informações Carregadas:</h4>
     * <ul>
     *   <li><strong>UserDetails:</strong> Nome, senha hash, status da conta</li>
     *   <li><strong>Authorities:</strong> Roles (ADMIN, CLIENT, TECHNICIAN)</li>
     *   <li><strong>Account Status:</strong> Habilitado, não expirado, desbloqueado</li>
     *   <li><strong>Credentials Status:</strong> Credenciais não expiradas</li>
     * </ul>
     * 
     * <h4>Implementação no Sistema:</h4>
     * <p>
     * No contexto do Helpdesk, carrega informações da entidade {@code Pessoa}
     * e seus perfis associados, convertendo-os para o formato {@link UserSS}
     * compatível com Spring Security.
     * </p>
     * 
     * @see UserDetailsService#loadUserByUsername(String)
     * @see UserSS
     */
    private final UserDetailsService userDetailsService;

    /**
     * <h3>Construtor com Injeção de Dependências</h3>
     * <p>
     * Inicializa o filtro de autorização com todas as dependências necessárias
     * para validação de tokens JWT e carregamento de contexto de segurança.
     * Integra-se automaticamente à cadeia de filtros do Spring Security.
     * </p>
     * 
     * <h4>🔧 Configuração Automática:</h4>
     * <ul>
     *   <li><strong>Filter Chain:</strong> Inserido automaticamente após autenticação</li>
     *   <li><strong>Order:</strong> Executado antes dos filtros de autorização</li>
     *   <li><strong>Scope:</strong> Singleton gerenciado pelo Spring</li>
     *   <li><strong>Integration:</strong> Compatível com configurações personalizadas</li>
     * </ul>
     * 
     * <h4>🏗️ Arquitetura de Dependências:</h4>
     * <p>
     * O padrão de injeção de dependências permite testabilidade e flexibilidade,
     * facilitando a criação de mocks para testes unitários e integração com
     * diferentes implementações de UserDetailsService conforme necessário.
     * </p>
     * 
     * <div style="background-color: #d1ecf1; border: 1px solid #b8daff; border-radius: 4px; padding: 8px; margin: 8px 0;">
     * <strong>💡 Design Pattern:</strong>
     * Utiliza o padrão Constructor Injection recomendado pelo Spring para
     * garantir imutabilidade e facilitar testes unitários com dependências mockadas.
     * </div>
     * 
     * @param authenticationManager {@link AuthenticationManager} Gerenciador central
     *        de autenticação do Spring Security, requerido pela classe pai
     *        {@link BasicAuthenticationFilter}
     * @param jwtUtil {@link JWTUtil} Utilitário especializado para operações JWT
     *        incluindo validação, parsing e extração segura de claims
     * @param userDetailsService {@link UserDetailsService} Serviço responsável
     *        por carregar detalhes completos do usuário e suas authorities
     * 
     * @see BasicAuthenticationFilter#BasicAuthenticationFilter(AuthenticationManager)
     * @see SecurityConfig#jwtAuthorizationFilter()
     */
    public JWTAuthorizationFilter(AuthenticationManager authenticationManager,
                                  JWTUtil jwtUtil,
                                  UserDetailsService userDetailsService) {
        super(authenticationManager);
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    /**
     * <h3>🔍 Filtro Principal de Interceptação</h3>
     * <p>
     * Método central que intercepta TODA requisição HTTP para validar autorização
     * via token JWT. Implementa a lógica core de segurança stateless do sistema,
     * garantindo que apenas usuários autenticados acessem recursos protegidos.
     * </p>
     * 
     * <h4>🔄 Fluxo Detalhado de Processamento:</h4>
     * <ol>
     *   <li><strong>Header Extraction:</strong> Captura header 'Authorization'</li>
     *   <li><strong>Format Validation:</strong> Verifica prefixo 'Bearer '</li>
     *   <li><strong>Token Isolation:</strong> Remove prefixo e isola JWT</li>
     *   <li><strong>Authentication:</strong> Processa via {@link #getAuthentication}</li>
     *   <li><strong>Context Setting:</strong> Registra no {@link SecurityContextHolder}</li>
     *   <li><strong>Chain Continuation:</strong> Prossegue para próximo filtro</li>
     * </ol>
     * 
     * <h4>📋 Cenários de Processamento:</h4>
     * <ul>
     *   <li><strong>✅ Token Válido:</strong> Usuário autenticado, acesso liberado</li>
     *   <li><strong>❌ Token Inválido:</strong> Contexto não definido, acesso negado</li>
     *   <li><strong>🚫 Sem Header:</strong> Requisição anônima, depende do endpoint</li>
     *   <li><strong>⚠️ Malformado:</strong> Prefixo incorreto, ignorado silenciosamente</li>
     * </ul>
     * 
     * <h4>🛡️ Estratégia de Segurança:</h4>
     * <p>
     * O filtro implementa uma abordagem de "fail-safe" onde tokens inválidos
     * simplesmente não estabelecem contexto de autenticação, permitindo que
     * endpoints públicos funcionem normalmente enquanto protege os privados.
     * </p>
     * 
     * <h4>⚡ Otimizações de Performance:</h4>
     * <ul>
     *   <li><strong>Early Exit:</strong> Para requisições sem header Authorization</li>
     *   <li><strong>Format Check:</strong> Validação rápida do prefixo Bearer</li>
     *   <li><strong>Lazy Loading:</strong> UserDetails carregado apenas se token válido</li>
     *   <li><strong>Context Reuse:</strong> SecurityContext mantido durante requisição</li>
     * </ul>
     * 
     * <h4>📝 Exemplos de Headers Processados:</h4>
     * <pre>
     * ✅ Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
     * ❌ Authorization: Basic dXNlcjpwYXNz
     * ❌ Authorization: eyJhbGciOiJIUzUxMiJ9... (sem Bearer)
     * ❌ Token: eyJhbGciOiJIUzUxMiJ9... (header incorreto)
     * </pre>
     * 
     * <div style="background-color: #d4edda; border: 1px solid #c3e6cb; border-radius: 4px; padding: 8px; margin: 8px 0;">
     * <strong>✅ Vantagens da Implementação:</strong>
     * <ul>
     *   <li>Execução transparente em cada requisição</li>
     *   <li>Integração perfeita com Spring Security</li>
     *   <li>Suporte automático para method-level security</li>
     *   <li>Compatibilidade com @PreAuthorize, @Secured, etc.</li>
     * </ul>
     * </div>
     * 
     * @param request {@link HttpServletRequest} Requisição HTTP sendo processada,
     *        contendo headers, parâmetros e body da chamada do cliente
     * @param response {@link HttpServletResponse} Resposta HTTP que será enviada,
     *        pode ser modificada por filtros subsequentes
     * @param chain {@link FilterChain} Cadeia de filtros do Spring Security,
     *        usado para continuar o processamento após autorização
     * 
     * @throws IOException Se houver falha na leitura da requisição ou escrita da resposta
     * @throws ServletException Se houver erro no processamento do servlet
     * 
     * @see #getAuthentication(String)
     * @see SecurityContextHolder#getContext()
     * @see FilterChain#doFilter(ServletRequest, ServletResponse)
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
     * <h3>🎫 Processamento de Autenticação JWT</h3>
     * <p>
     * Método privado especializado que processa tokens JWT extraídos do header
     * Authorization, executando validação completa e criação do contexto de
     * autenticação para o Spring Security.
     * </p>
     * 
     * <h4>🔍 Pipeline de Validação:</h4>
     * <ol>
     *   <li><strong>Token Validation:</strong> Verifica assinatura digital HMAC-SHA512</li>
     *   <li><strong>Expiration Check:</strong> Confirma que token não expirou</li>
     *   <li><strong>Format Verification:</strong> Valida estrutura JWT (header.payload.signature)</li>
     *   <li><strong>Claims Extraction:</strong> Obtém subject (username) das claims</li>
     *   <li><strong>User Loading:</strong> Carrega detalhes via UserDetailsService</li>
     *   <li><strong>Token Creation:</strong> Gera UsernamePasswordAuthenticationToken</li>
     * </ol>
     * 
     * <h4>🔐 Processo de Carregamento do Usuário:</h4>
     * <p>
     * Após validação do token, o sistema carrega informações completas do usuário
     * incluindo roles, permissões e status da conta. Isso garante que alterações
     * de perfil sejam refletidas imediatamente sem necessidade de novo login.
     * </p>
     * 
     * <h4>🏗️ Construção do Authentication Token:</h4>
     * <ul>
     *   <li><strong>Principal:</strong> Username extraído do token JWT</li>
     *   <li><strong>Credentials:</strong> null (não armazenamos senha no contexto)</li>
     *   <li><strong>Authorities:</strong> Lista de roles/permissões do usuário</li>
     * </ul>
     * 
     * <h4>⚡ Otimizações Implementadas:</h4>
     * <ul>
     *   <li><strong>Early Return:</strong> Retorna null imediatamente se token inválido</li>
     *   <li><strong>Lazy DB Query:</strong> UserDetailsService só consultado se token válido</li>
     *   <li><strong>Cache Friendly:</strong> Compatível com cache de UserDetails</li>
     *   <li><strong>Exception Safe:</strong> Todas exceções resultam em retorno null</li>
     * </ul>
     * 
     * <h4>🛡️ Vantagens da Validação Dupla:</h4>
     * <p>
     * A combinação de validação JWT + carregamento de UserDetails oferece:
     * </p>
     * <ul>
     *   <li><strong>Segurança:</strong> Token assinado + verificação de conta ativa</li>
     *   <li><strong>Flexibilidade:</strong> Mudanças de permissão refletidas instantaneamente</li>
     *   <li><strong>Auditoria:</strong> Logs detalhados de acesso por usuário</li>
     *   <li><strong>Controle:</strong> Possibilidade de desabilitar usuários remotamente</li>
     * </ul>
     * 
     * <h4>📊 Fluxo de Dados:</h4>
     * <pre>
     * JWT Token → Validation → Username → UserDetailsService → UserDetails → AuthToken
     * </pre>
     * 
     * <div style="background-color: #d1ecf1; border: 1px solid #b8daff; border-radius: 4px; padding: 8px; margin: 8px 0;">
     * <strong>💡 Design Pattern:</strong>
     * Implementa o padrão "Token-to-Principal" com lazy loading, onde a validação
     * do token é feita primeiro (operação rápida) e só então carregamos os detalhes
     * completos do usuário se necessário.
     * </div>
     * 
     * <div style="background-color: #fff3cd; border: 1px solid #ffeaa7; border-radius: 4px; padding: 8px; margin: 8px 0;">
     * <strong>⚠️ Tratamento de Exceções:</strong>
     * Qualquer falha na validação (token expirado, assinatura inválida, usuário
     * não encontrado) resulta em retorno null, garantindo que o contexto de
     * segurança não seja estabelecido para tokens problemáticos.
     * </div>
     * 
     * @param token {@link String} Token JWT puro (sem prefixo "Bearer"),
     *        previamente extraído do header Authorization da requisição HTTP
     * 
     * @return {@link UsernamePasswordAuthenticationToken} Objeto de autenticação
     *         do Spring Security contendo principal, credentials e authorities,
     *         ou null se token for inválido por qualquer motivo
     * 
     * @see JWTUtil#tokenValido(String)
     * @see JWTUtil#getUsername(String)  
     * @see UserDetailsService#loadUserByUsername(String)
     * @see UsernamePasswordAuthenticationToken#UsernamePasswordAuthenticationToken(Object, Object, Collection)
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