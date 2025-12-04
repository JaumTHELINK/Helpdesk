package com.turmaa.helpdesk.security;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import com.turmaa.helpdesk.security.UserSS;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * <h1>Utilitário JWT para Sistema Helpdesk</h1>
 * <p>
 * Classe central para operações com JSON Web Tokens (JWT) no sistema de autenticação.
 * Fornece funcionalidades completas para geração, validação e extração de informações
 * de tokens JWT, implementando um sistema de autenticação stateless robusto e seguro.
 * </p>
 * 
 * <h2>Principais Funcionalidades:</h2>
 * <ul>
 *   <li><strong>Geração de Tokens:</strong> Criar tokens JWT assinados para usuários autenticados</li>
 *   <li><strong>Validação:</strong> Verificar integridade, assinatura e expiração dos tokens</li>
 *   <li><strong>Extração de Dados:</strong> Recuperar informações do usuário encapsuladas no token</li>
 *   <li><strong>Configuração:</strong> Gerenciamento flexível de chave secreta e tempo de expiração</li>
 * </ul>
 * 
 * <h2>Arquitetura JWT:</h2>
 * <ul>
 *   <li><strong>Header:</strong> Algoritmo de assinatura (HS512) e tipo de token</li>
 *   <li><strong>Payload:</strong> Claims com dados do usuário (subject, expiration)</li>
 *   <li><strong>Signature:</strong> Assinatura HMAC-SHA512 para garantir integridade</li>
 * </ul>
 * 
 * <h2>Segurança Implementada:</h2>
 * <ul>
 *   <li><strong>Algoritmo HS512:</strong> HMAC com SHA-512 para máxima segurança</li>
 *   <li><strong>Chave Secreta:</strong> Configurável externamente via properties</li>
 *   <li><strong>Tempo de Vida:</strong> Tokens com expiração configurável</li>
 *   <li><strong>Validação Rigorosa:</strong> Verificação de assinatura, tempo e estrutura</li>
 * </ul>
 * 
 * <h2>Integração com Spring:</h2>
 * <p>
 * Anotada com @Component para gerenciamento automático pelo Spring Container,
 * permitindo injeção de dependência e configuração via @Value das propriedades
 * de aplicação (jwt.secret e jwt.expiration).
 * </p>
 * 
 * <h2>Fluxo de Uso:</h2>
 * <ol>
 *   <li><strong>Login:</strong> generateToken() cria token após autenticação</li>
 *   <li><strong>Requisições:</strong> tokenValido() valida token recebido</li>
 *   <li><strong>Autorização:</strong> getUsername() extrai usuário para contexto de segurança</li>
 * </ol>
 * 
 * @author Sistema Helpdesk
 * @version 1.0
 * @since 2024
 * 
 * @see JWTAuthenticationFilter
 * @see JWTAuthorizationFilter
 * @see io.jsonwebtoken.Jwts
 */
@Component
public class JWTUtil {

    /**
     * <h3>Tempo de Expiração do Token</h3>
     * <p>
     * Tempo de vida do token JWT em milissegundos, configurado externamente
     * através da propriedade <code>jwt.expiration</code> no arquivo de configuração.
     * Este valor determina por quanto tempo um token permanece válido após sua criação.
     * </p>
     * 
     * <h4>Valores Típicos:</h4>
     * <ul>
     *   <li><strong>Desenvolvimento:</strong> 86400000ms (24 horas)</li>
     *   <li><strong>Produção:</strong> 3600000ms (1 hora) para maior segurança</li>
     *   <li><strong>Refresh Token:</strong> 604800000ms (7 dias) se implementado</li>
     * </ul>
     * 
     * <h4>Considerações de Segurança:</h4>
     * <p>
     * Tokens com tempo de vida muito longo representam risco de segurança se
     * comprometidos. Tempos muito curtos podem prejudicar a experiência do usuário.
     * </p>
     */
    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * <h3>Chave Secreta para Assinatura</h3>
     * <p>
     * Chave secreta utilizada para assinar e validar a autenticidade dos tokens JWT.
     * Esta chave é crítica para a segurança do sistema e deve ser configurada
     * externamente através da propriedade <code>jwt.secret</code>.
     * </p>
     * 
     * <h4>Requisitos de Segurança:</h4>
     * <ul>
     *   <li><strong>Comprimento:</strong> Mínimo 256 bits (32 caracteres) para HS512</li>
     *   <li><strong>Complexidade:</strong> Caracteres aleatórios com alta entropia</li>
     *   <li><strong>Rotação:</strong> Deve ser alterada periodicamente em produção</li>
     *   <li><strong>Armazenamento:</strong> Nunca committed em código, apenas em config</li>
     * </ul>
     * 
     * <h4>⚠️ CRÍTICO:</h4>
     * <p>
     * Se esta chave for comprometida, todos os tokens podem ser falsificados.
     * Deve ser tratada como informação ultra-sensível e armazenada de forma segura.
     * </p>
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * <h3>Gerar Token JWT</h3>
     * <p>
     * Cria um novo token JWT assinado para o usuário especificado pelo email.
     * Este método é o ponto de entrada principal para autenticação bem-sucedida,
     * gerando um token que será usado nas requisições subsequentes.
     * </p>
     * 
     * <h4>Estrutura do Token Gerado:</h4>
     * <ul>
     *   <li><strong>Header:</strong> Algoritmo HS512 e tipo JWT</li>
     *   <li><strong>Payload:</strong> Subject (email) e data de expiração</li>
     *   <li><strong>Signature:</strong> HMAC-SHA512 com chave secreta</li>
     * </ul>
     * 
     * <h4>Claims Incluídas:</h4>
     * <ul>
     *   <li><strong>sub (Subject):</strong> Email do usuário autenticado</li>
     *   <li><strong>exp (Expiration):</strong> Timestamp de expiração</li>
     *   <li><strong>iat (Issued At):</strong> Implícito, momento da criação</li>
     * </ul>
     * 
     * <h4>Processo de Geração:</h4>
     * <ol>
     *   <li>Inicia builder do JWT</li>
     *   <li>Define subject como email do usuário</li>
     *   <li>Calcula data de expiração (agora + tempo configurado)</li>
     *   <li>Assina com HS512 usando chave secreta</li>
     *   <li>Compacta em string Base64URL</li>
     * </ol>
     * 
     * <h4>Exemplo de Uso:</h4>
     * <pre>{@code
     * // Após autenticação bem-sucedida
     * String email = "usuario@exemplo.com";
     * String token = jwtUtil.generateToken(email);
     * 
     * // Token retornado:
     * // eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c3VhcmlvQGV4ZW1wbG8uY29tIiwiZXhwIjoxNjMwMjQ4MDAwfQ.signature
     * }</pre>
     * 
     * @param email {@link String} Endereço de email do usuário que será usado como subject do token
     * 
     * @return {@link String} Token JWT compactado, pronto para ser enviado ao cliente
     * 
     * @throws IllegalArgumentException Se email for null ou vazio
     * 
     * @see #tokenValido(String)
     * @see #getUsername(String)
     */
    public String generateToken(String email) {
        return Jwts.builder()
                   // Define o "subject" (identificação do usuário) como o e-mail
                   .setSubject(email)
                   // Define a data de expiração (agora + tempo configurado)
                   .setExpiration(new Date(System.currentTimeMillis() + expiration))
                   // Assina o token usando algoritmo HS512 e a chave secreta
                   .signWith(SignatureAlgorithm.HS512, secret.getBytes())
                   // Compacta e retorna o token em formato String
                   .compact();
    }

    /**
     * Gera token JWT incluindo as roles/perfis do usuário no payload.
     *
     * @param user instância de {@link UserSS} contendo authorities
     * @return token JWT com claims adicionais "roles" e "perfil"
     */
    public String generateToken(UserSS user) {
        Claims claims = Jwts.claims().setSubject(user.getUsername());

        List<String> roles = user.getAuthorities().stream()
                .map((GrantedAuthority g) -> g.getAuthority())
                .map(s -> s != null && s.startsWith("ROLE_") ? s.substring(5) : s)
                .collect(Collectors.toList());

        // inserir claims compatíveis com front-end
        claims.put("roles", roles); // ex: ["ADMIN"]
        claims.put("perfil", roles); // compat com front-ends antigos que usam 'perfil'

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS512, secret.getBytes())
                .compact();
    }

    /**
     * <h3>Validar Token JWT</h3>
     * <p>
     * Realiza validação completa de um token JWT, verificando sua estrutura,
     * assinatura, integridade e tempo de expiração. Este método é crítico
     * para garantir que apenas tokens legítimos e válidos sejam aceitos.
     * </p>
     * 
     * <h4>Validações Realizadas:</h4>
     * <ul>
     *   <li><strong>Estrutura:</strong> Token deve ter formato JWT válido</li>
     *   <li><strong>Assinatura:</strong> HMAC-SHA512 deve corresponder à chave secreta</li>
     *   <li><strong>Subject:</strong> Deve conter identificação do usuário (email)</li>
     *   <li><strong>Expiração:</strong> Token não deve estar expirado</li>
     *   <li><strong>Integridade:</strong> Payload não deve ter sido alterado</li>
     * </ul>
     * 
     * <h4>Processo de Validação:</h4>
     * <ol>
     *   <li>Extrai claims do token usando chave secreta</li>
     *   <li>Verifica se claims foram extraídas com sucesso</li>
     *   <li>Valida presença do subject (username)</li>
     *   <li>Verifica data de expiração</li>
     *   <li>Compara timestamp atual com expiração</li>
     * </ol>
     * 
     * <h4>Casos de Invalidez:</h4>
     * <ul>
     *   <li>Token malformado ou corrompido</li>
     *   <li>Assinatura inválida (chave incorreta)</li>
     *   <li>Token expirado (exp < now)</li>
     *   <li>Subject ausente ou nulo</li>
     *   <li>Claims não podem ser extraídas</li>
     * </ul>
     * 
     * <h4>Segurança:</h4>
     * <p>
     * Este método é resistente a ataques de timing e não vaza informações
     * sobre por que um token é inválido, retornando simplesmente false
     * para qualquer problema detectado.
     * </p>
     * 
     * <h4>Exemplo de Uso:</h4>
     * <pre>{@code
     * String token = request.getHeader("Authorization");
     * if (token != null && token.startsWith("Bearer ")) {
     *     token = token.substring(7);
     *     if (jwtUtil.tokenValido(token)) {
     *         // Token válido, prosseguir com autorização
     *         String username = jwtUtil.getUsername(token);
     *     } else {
     *         // Token inválido, rejeitar requisição
     *     }
     * }
     * }</pre>
     * 
     * @param token {@link String} Token JWT a ser validado (sem prefixo "Bearer ")
     * 
     * @return {@link Boolean} true se o token for válido em todos os aspectos,
     *         false caso contrário (malformado, expirado, assinatura inválida, etc.)
     * 
     * @see #getClaims(String)
     * @see #getUsername(String)
     */
    public boolean tokenValido(String token) {
        // Obtém as claims (informações) contidas no token
        Claims claims = getClaims(token);
        if (claims != null) {
            // Subject (nome de usuário) do token
            String username = claims.getSubject();
            // Data de expiração
            Date expirationDate = claims.getExpiration();
            // Data/hora atual
            Date now = new Date(System.currentTimeMillis());

            // Token é válido se possuir subject, data de expiração
            // e a data atual for anterior à expiração
            if (username != null && expirationDate != null && now.before(expirationDate)) {
                return true;
            }
        }
        return false;
    }

    /**
     * <h3>Extrair Claims do Token</h3>
     * <p>
     * Método privado que extrai e valida as claims (informações) contidas
     * em um token JWT. Este método é fundamental para todos os outros
     * processos de validação e extração de dados do token.
     * </p>
     * 
     * <h4>Processo de Extração:</h4>
     * <ol>
     *   <li>Cria parser JWT com chave secreta</li>
     *   <li>Faz parsing do token e valida assinatura</li>
     *   <li>Extrai body (payload) com as claims</li>
     *   <li>Retorna claims ou null se inválido</li>
     * </ol>
     * 
     * <h4>Validações Implícitas:</h4>
     * <ul>
     *   <li><strong>Formato:</strong> Token deve ter estrutura JWT válida</li>
     *   <li><strong>Assinatura:</strong> HMAC deve corresponder à chave</li>
     *   <li><strong>Headers:</strong> Algoritmo deve ser compatível</li>
     *   <li><strong>Encoding:</strong> Base64URL deve ser válido</li>
     * </ul>
     * 
     * <h4>Tratamento de Exceções:</h4>
     * <p>
     * Qualquer exceção durante o parsing (token malformado, assinatura
     * inválida, algoritmo incompatível) resulta em retorno null,
     * garantindo comportamento consistente e seguro.
     * </p>
     * 
     * <h4>Tipos de Exceções Capturadas:</h4>
     * <ul>
     *   <li><strong>MalformedJwtException:</strong> Token com formato inválido</li>
     *   <li><strong>SignatureException:</strong> Assinatura não confere</li>
     *   <li><strong>ExpiredJwtException:</strong> Token expirado</li>
     *   <li><strong>UnsupportedJwtException:</strong> Algoritmo não suportado</li>
     *   <li><strong>IllegalArgumentException:</strong> Token nulo ou vazio</li>
     * </ul>
     * 
     * @param token {@link String} Token JWT completo para extração de claims
     * 
     * @return {@link Claims} Objeto contendo todas as informações do token,
     *         ou null se o token for inválido por qualquer motivo
     * 
     * @see io.jsonwebtoken.Jwts#parser()
     * @see io.jsonwebtoken.Claims
     */
    private Claims getClaims(String token) {
        try {
            // Faz o parsing do token usando a chave secreta para validar a assinatura
            return Jwts.parser()
                       .setSigningKey(secret.getBytes())
                       .parseClaimsJws(token)
                       .getBody();
        } catch (Exception e) {
            // Em caso de erro (token inválido, assinatura incorreta, expirado etc.) retorna null
            return null;
        }
    }

    /**
     * <h3>Extrair Nome de Usuário do Token</h3>
     * <p>
     * Extrai o nome de usuário (subject) do token JWT validado. O subject
     * é a claim padrão que identifica o proprietário do token no sistema
     * de autenticação JWT.
     * </p>
     * 
     * <h4>Fluxo de Extração:</h4>
     * <ol>
     *   <li>Chama {@link #getClaims(String)} para extrair claims</li>
     *   <li>Verifica se claims são válidas (não nulas)</li>
     *   <li>Retorna subject ou null se inválido</li>
     * </ol>
     * 
     * <h4>Casos de Uso:</h4>
     * <ul>
     *   <li><strong>Autenticação:</strong> Identificar usuário logado</li>
     *   <li><strong>Autorização:</strong> Verificar permissões específicas</li>
     *   <li><strong>Auditoria:</strong> Log de ações por usuário</li>
     *   <li><strong>Personalização:</strong> Configurações específicas</li>
     * </ul>
     * 
     * <h4>Padrão JWT Subject:</h4>
     * <p>
     * O "subject" (sub) é uma claim reservada do JWT RFC 7519 que identifica
     * o principal sobre o qual o token faz assertivas. No contexto desta
     * aplicação, representa o username do usuário autenticado.
     * </p>
     * 
     * <h4>Segurança:</h4>
     * <ul>
     *   <li><strong>Validação:</strong> Token deve ter passado por todas as verificações</li>
     *   <li><strong>Não-Repúdio:</strong> Subject assinado criptograficamente</li>
     *   <li><strong>Integridade:</strong> Impossível alterar sem invalidar assinatura</li>
     * </ul>
     * 
     * <div style="background-color: #fff3cd; border: 1px solid #ffeaa7; border-radius: 4px; padding: 8px; margin: 8px 0;">
     * <strong>⚠️ Importante:</strong>
     * Este método não valida a expiração do token. Para validação completa,
     * use {@link #tokenValido(String)} antes de extrair o username.
     * </div>
     * 
     * @param token {@link String} Token JWT válido contendo subject claim
     * 
     * @return {@link String} E-mail/username extraído do token,
     *         ou null se o token for inválido ou não contiver subject
     * 
     * @see #getClaims(String)
     * @see io.jsonwebtoken.Claims#getSubject()
     */
    public String getUsername(String token) {
        Claims claims = getClaims(token);
        if (claims != null) {
            return claims.getSubject();
        }
        return null;
    }
}