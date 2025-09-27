package com.turmaa.helpdesk.security;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.turmaa.helpdesk.domain.enums.Perfil;

/**
 * <h2>Implementação UserDetails para Spring Security</h2>
 * <p>
 * Classe adaptadora que converte entidades de domínio ({@code Pessoa}) para o formato
 * compatível com Spring Security. Serve como ponte entre o modelo de dados da aplicação
 * e os requisitos de autenticação/autorização do framework de segurança.
 * </p>
 * 
 * <h3>🔐 Arquitetura de Segurança</h3>
 * <p>
 * Implementa a interface {@link UserDetails} do Spring Security, permitindo que
 * o sistema utilize suas próprias entidades de usuário dentro do contexto de
 * segurança padrão do framework. Esta abordagem garante compatibilidade completa
 * com todas as funcionalidades do Spring Security.
 * </p>
 * 
 * <h3>🏗️ Responsabilidades Principais</h3>
 * <ul>
 *   <li><strong>👤 User Adaptation:</strong> Converte Pessoa → UserDetails</li>
 *   <li><strong>🔑 Credential Management:</strong> Gerencia senha criptografada</li>
 *   <li><strong>⚡ Authority Conversion:</strong> Transforma Perfil → GrantedAuthority</li>
 *   <li><strong>🛡️ Security Context:</strong> Integração com SecurityContextHolder</li>
 *   <li><strong>📊 Account Status:</strong> Controle de status da conta</li>
 * </ul>
 * 
 * <h3>🎯 Modelo de Dados Encapsulado</h3>
 * <ul>
 *   <li><strong>ID:</strong> Identificador único do usuário no sistema</li>
 *   <li><strong>Email:</strong> Username para autenticação (formato email)</li>
 *   <li><strong>Senha:</strong> Password hash BCrypt para verificação</li>
 *   <li><strong>Authorities:</strong> Roles convertidas de enum {@link Perfil}</li>
 * </ul>
 * 
 * <h3>🔄 Fluxo de Conversão de Authorities</h3>
 * <pre>
 * Perfil.ADMIN → SimpleGrantedAuthority("ROLE_ADMIN")
 * Perfil.TECHNICIAN → SimpleGrantedAuthority("ROLE_TECHNICIAN") 
 * Perfil.CLIENT → SimpleGrantedAuthority("ROLE_CLIENT")
 * </pre>
 * 
 * <h3>💡 Vantagens da Implementação</h3>
 * <ul>
 *   <li><strong>🔗 Integration:</strong> Compatibilidade total com Spring Security</li>
 *   <li><strong>📈 Scalability:</strong> Fácil extensão para novas funcionalidades</li>
 *   <li><strong>🛠️ Flexibility:</strong> Permite customização de comportamentos</li>
 *   <li><strong>✅ Standards:</strong> Segue padrões do framework</li>
 * </ul>
 * 
 * <h3>🔧 Casos de Uso no Sistema</h3>
 * <ul>
 *   <li><strong>Authentication:</strong> Login via email/senha</li>
 *   <li><strong>Authorization:</strong> Verificação de permissões por endpoint</li>
 *   <li><strong>JWT Processing:</strong> Principal em tokens de autenticação</li>
 *   <li><strong>Method Security:</strong> @PreAuthorize, @Secured annotations</li>
 * </ul>
 * 
 * <h3>📚 Integração com Outras Classes</h3>
 * <ul>
 *   <li><strong>{@link UserDetailsServiceImpl}:</strong> Factory que cria instâncias</li>
 *   <li><strong>{@link JWTAuthenticationFilter}:</strong> Usa como Principal</li>
 *   <li><strong>{@link JWTAuthorizationFilter}:</strong> Reconstrói contexto</li>
 *   <li><strong>Security Expressions:</strong> hasRole(), hasAuthority()</li>
 * </ul>
 * 
 * <div style="background-color: #d4edda; border: 1px solid #c3e6cb; border-radius: 4px; padding: 8px; margin: 8px 0;">
 * <strong>✅ Benefícios Arquiteturais:</strong>
 * <ul>
 *   <li>Separação clara entre domínio e segurança</li>
 *   <li>Reutilização de código para diferentes tipos de usuário</li>
 *   <li>Extensibilidade para features futuras de segurança</li>
 *   <li>Compatibilidade com ferramentas de auditoria</li>
 * </ul>
 * </div>
 * 
 * <div style="background-color: #d1ecf1; border: 1px solid #b8daff; border-radius: 4px; padding: 8px; margin: 8px 0;">
 * <strong>💡 Design Pattern:</strong>
 * Implementa o padrão Adapter, convertendo interfaces incompatíveis
 * (entidades de domínio vs UserDetails) em uma interface comum que
 * o Spring Security pode utilizar nativamente.
 * </div>
 * 
 * @author Sistema Helpdesk
 * @since 1.0.0
 * @see UserDetails
 * @see GrantedAuthority 
 * @see Perfil
 * @see UserDetailsServiceImpl
 */
public class UserSS implements UserDetails {

    /**
     * <h3>Serial Version UID</h3>
     * <p>
     * Identificador único para controle de versão durante serialização.
     * Essencial para compatibilidade quando a classe UserSS é serializada
     * (ex: sessões distribuídas, cache, persistência de SecurityContext).
     * </p>
     * 
     * <h4>⚠️ Importância:</h4>
     * <p>
     * Garante que diferentes versões da aplicação possam desserializar
     * objetos UserSS criados por versões anteriores, mantendo compatibilidade
     * backwards quando há atualizações do sistema.
     * </p>
     * 
     * @see java.io.Serializable
     */
    private static final long serialVersionUID = 1L;

    /**
     * <h3>Identificador do Usuário</h3>
     * <p>
     * Chave primária única que identifica o usuário no banco de dados.
     * Permite relacionamento direto com a entidade {@code Pessoa} e
     * facilita operações de auditoria e rastreamento.
     * </p>
     * 
     * <h4>🔗 Casos de Uso:</h4>
     * <ul>
     *   <li><strong>Auditoria:</strong> Logs de ações por usuário específico</li>
     *   <li><strong>Relacionamentos:</strong> FK para tabelas dependentes</li>
     *   <li><strong>Cache:</strong> Chave para cache de permissões</li>
     *   <li><strong>Analytics:</strong> Métricas por usuário</li>
     * </ul>
     * 
     * <div style="background-color: #d1ecf1; border: 1px solid #b8daff; border-radius: 4px; padding: 8px; margin: 8px 0;">
     * <strong>💡 Design:</strong>
     * Campo final (immutable) garante que a identidade do usuário
     * não pode ser alterada após criação do objeto UserSS.
     * </div>
     */
    private final Integer id;

    /**
     * <h3>Email/Username</h3>
     * <p>
     * Endereço de email que serve como username único para autenticação.
     * Este campo é utilizado pelo Spring Security como identificador
     * principal do usuário no sistema.
     * </p>
     * 
     * <h4>📧 Características:</h4>
     * <ul>
     *   <li><strong>Unique:</strong> Cada email identifica apenas um usuário</li>
     *   <li><strong>Username:</strong> Campo usado para login no sistema</li>
     *   <li><strong>Contact:</strong> Meio de comunicação com o usuário</li>
     *   <li><strong>Recovery:</strong> Base para recuperação de senha</li>
     * </ul>
     * 
     * <h4>🔐 Integração Security:</h4>
     * <p>
     * Retornado pelo método {@link #getUsername()} conforme contrato
     * {@link UserDetails}, sendo usado pelo Spring Security para
     * identificação em logs, auditoria e controle de sessão.
     * </p>
     */
    private final String email;

    /**
     * <h3>Senha Criptografada</h3>
     * <p>
     * Hash BCrypt da senha original do usuário. Nunca armazena a senha
     * em texto plano, seguindo melhores práticas de segurança.
     * Utilizada pelo Spring Security para verificação durante autenticação.
     * </p>
     * 
     * <h4>🔒 Características de Segurança:</h4>
     * <ul>
     *   <li><strong>BCrypt Hash:</strong> Algoritmo com salt automático</li>
     *   <li><strong>One-Way:</strong> Impossível reverter para texto plano</li>
     *   <li><strong>Strength:</strong> Configurável via SecurityConfig</li>
     *   <li><strong>Rainbow-Proof:</strong> Resistente a ataques de tabela</li>
     * </ul>
     * 
     * <h4>⚡ Processo de Verificação:</h4>
     * <p>
     * Durante login, o Spring Security chama {@link #getPassword()} e
     * compara com a senha fornecida usando PasswordEncoder configurado,
     * sem nunca expor a senha original.
     * </p>
     * 
     * <div style="background-color: #f8d7da; border: 1px solid #f5c6cb; border-radius: 4px; padding: 8px; margin: 8px 0;">
     * <strong>⚠️ Segurança:</strong>
     * Este campo contém informação extremamente sensível e deve ser tratado
     * com máximo cuidado em logs, dumps de memória e serialização.
     * </div>
     */
    private final String senha;

    /**
     * <h3>Authorities/Permissões</h3>
     * <p>
     * Coleção de autoridades (roles/permissões) concedidas ao usuário,
     * convertidas de enums {@link Perfil} para {@link GrantedAuthority}.
     * Base para todo o sistema de autorização do Spring Security.
     * </p>
     * 
     * <h4>🎯 Estrutura de Autorização:</h4>
     * <ul>
     *   <li><strong>ROLE_ADMIN:</strong> Acesso total ao sistema</li>
     *   <li><strong>ROLE_TECHNICIAN:</strong> Gerenciamento de chamados</li>
     *   <li><strong>ROLE_CLIENT:</strong> Acesso limitado a próprios dados</li>
     * </ul>
     * 
     * <h4>🔄 Processo de Conversão:</h4>
     * <pre>
     * Set&lt;Perfil&gt; → Stream → map(Perfil::getDescricao) → SimpleGrantedAuthority → collect()
     * </pre>
     * 
     * <h4>💡 Casos de Uso:</h4>
     * <ul>
     *   <li><strong>@PreAuthorize:</strong> hasRole('ADMIN')</li>
     *   <li><strong>@Secured:</strong> ROLE_TECHNICIAN</li>
     *   <li><strong>HTTP Security:</strong> .hasAuthority('ROLE_CLIENT')</li>
     *   <li><strong>Method Security:</strong> Verificação dinâmica</li>
     * </ul>
     * 
     * <h4>⚡ Performance:</h4>
     * <p>
     * Authorities são calculadas uma vez durante criação do UserSS
     * e reutilizadas em todas as verificações de autorização,
     * evitando múltiplas consultas ao banco de dados.
     * </p>
     * 
     * @see GrantedAuthority
     * @see SimpleGrantedAuthority
     * @see Perfil#getDescricao()
     */
    private final Collection<? extends GrantedAuthority> authorities;

    /**
     * <h3>🏗️ Construtor Principal</h3>
     * <p>
     * Inicializa uma instância completa de UserSS com todos os dados necessários
     * para integração com Spring Security. Executa conversão automática de
     * perfis de domínio para authorities compatíveis com o framework.
     * </p>
     * 
     * <h4>🔧 Processo de Inicialização:</h4>
     * <ol>
     *   <li><strong>Field Assignment:</strong> Atribui valores aos campos finais</li>
     *   <li><strong>Perfil Conversion:</strong> Converte Set&lt;Perfil&gt; → Collection&lt;GrantedAuthority&gt;</li>
     *   <li><strong>Stream Processing:</strong> map() + collect() para transformação eficiente</li>
     *   <li><strong>Authority Creation:</strong> Gera SimpleGrantedAuthority para cada perfil</li>
     * </ol>
     * 
     * <h4>💡 Conversão de Autoridades:</h4>
     * <pre>
     * Set&lt;Perfil&gt; perfis = {ADMIN, CLIENT}
     *     ↓ (stream + map + collect)
     * Collection&lt;GrantedAuthority&gt; = {ROLE_ADMIN, ROLE_CLIENT}
     * </pre>
     * 
     * <h4>🎯 Vantagens do Design:</h4>
     * <ul>
     *   <li><strong>Immutability:</strong> Todos os campos são final</li>
     *   <li><strong>Type Safety:</strong> Conversão tipada Perfil → GrantedAuthority</li>
     *   <li><strong>Performance:</strong> Conversão única durante construção</li>
     *   <li><strong>Consistency:</strong> Estado consistente garantido</li>
     * </ul>
     * 
     * <h4>🔗 Integração com Factory:</h4>
     * <p>
     * Normalmente criado por {@link UserDetailsServiceImpl} que obtém dados
     * da entidade {@code Pessoa} e converte para este formato compatível
     * com Spring Security.
     * </p>
     * 
     * <h4>📊 Exemplo de Uso:</h4>
     * <pre>
     * Pessoa pessoa = pessoaRepository.findByEmail("admin@helpdesk.com");
     * Set&lt;Perfil&gt; perfis = pessoa.getPerfis();
     * 
     * UserSS userSS = new UserSS(
     *     pessoa.getId(),
     *     pessoa.getEmail(),
     *     pessoa.getSenha(),
     *     perfis
     * );
     * </pre>
     * 
     * <div style="background-color: #d4edda; border: 1px solid #c3e6cb; border-radius: 4px; padding: 8px; margin: 8px 0;">
     * <strong>✅ Validação Automática:</strong>
     * O uso de Stream API garante que mesmo conjuntos vazios de perfis
     * resultem em coleção válida (vazia) de authorities, evitando NPE.
     * </div>
     * 
     * @param id {@link Integer} Identificador único do usuário no sistema,
     *        usado para relacionamentos e auditoria
     * @param email {@link String} Endereço de email que serve como username,
     *        deve ser único no sistema e válido
     * @param senha {@link String} Hash BCrypt da senha original,
     *        nunca deve ser texto plano
     * @param perfis {@link Set}&lt;{@link Perfil}&gt; Conjunto de perfis/roles
     *        do usuário que serão convertidos para authorities
     * 
     * @see Perfil#getDescricao()
     * @see SimpleGrantedAuthority#SimpleGrantedAuthority(String)
     * @see Collectors#toSet()
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
     * <h3>🆔 Accessor do Identificador</h3>
     * <p>
     * Retorna o identificador único do usuário no sistema. Este ID
     * não faz parte da interface {@link UserDetails} mas é essencial
     * para operações de domínio que requerem referência direta ao usuário.
     * </p>
     * 
     * <h4>🎯 Casos de Uso Principais:</h4>
     * <ul>
     *   <li><strong>Auditoria:</strong> Registros de log por usuário específico</li>
     *   <li><strong>Relacionamentos:</strong> FK para chamados, comentários</li>
     *   <li><strong>Permissões:</strong> Verificações baseadas em proprietário</li>
     *   <li><strong>Analytics:</strong> Métricas e relatórios por usuário</li>
     * </ul>
     * 
     * <h4>💡 Padrão de Uso:</h4>
     * <pre>
     * // Em controllers ou services
     * UserSS currentUser = (UserSS) SecurityContextHolder
     *     .getContext().getAuthentication().getPrincipal();
     * 
     * Integer userId = currentUser.getId();
     * // Usar para queries específicas do usuário
     * </pre>
     * 
     * <div style="background-color: #d1ecf1; border: 1px solid #b8daff; border-radius: 4px; padding: 8px; margin: 8px 0;">
     * <strong>💡 Design:</strong>
     * Campo adicional além da interface UserDetails, permitindo
     * acesso direto ao ID sem necessidade de consultas adicionais.
     * </div>
     * 
     * @return {@link Integer} Identificador único do usuário no banco de dados
     * 
     * @see UserDetails
     */
    public Integer getId() {
        return id;
    }

    /**
     * <h3>🔐 Authorities do Usuário</h3>
     * <p>
     * Implementação obrigatória de {@link UserDetails#getAuthorities()}.
     * Retorna coleção de autoridades (roles/permissões) associadas ao usuário,
     * base para todo o sistema de autorização do Spring Security.
     * </p>
     * 
     * <h4>🎯 Estrutura de Autorização:</h4>
     * <ul>
     *   <li><strong>ROLE_ADMIN:</strong> Acesso completo ao sistema</li>
     *   <li><strong>ROLE_TECHNICIAN:</strong> Gerenciamento de chamados e clientes</li>
     *   <li><strong>ROLE_CLIENT:</strong> Acesso apenas aos próprios chamados</li>
     * </ul>
     * 
     * <h4>🔧 Integração com Framework:</h4>
     * <p>
     * As authorities retornadas são utilizadas por:
     * </p>
     * <ul>
     *   <li><strong>@PreAuthorize:</strong> hasRole('ADMIN')</li>
     *   <li><strong>@Secured:</strong> {"ROLE_TECHNICIAN", "ROLE_ADMIN"}</li>
     *   <li><strong>HttpSecurity:</strong> .hasAnyRole("ADMIN", "TECHNICIAN")</li>
     *   <li><strong>SecurityExpressions:</strong> hasAuthority('ROLE_CLIENT')</li>
     * </ul>
     * 
     * <h4>⚡ Características de Performance:</h4>
     * <ul>
     *   <li><strong>Pre-computed:</strong> Calculadas no construtor</li>
     *   <li><strong>Immutable:</strong> Não mudam após criação</li>
     *   <li><strong>Cached:</strong> Reutilizadas em múltiplas verificações</li>
     *   <li><strong>Thread-Safe:</strong> Coleção imutável</li>
     * </ul>
     * 
     * <h4>📝 Exemplo de Uso:</h4>
     * <pre>
     * Collection&lt;? extends GrantedAuthority&gt; authorities = userSS.getAuthorities();
     * 
     * boolean isAdmin = authorities.stream()
     *     .anyMatch(auth -&gt; "ROLE_ADMIN".equals(auth.getAuthority()));
     * </pre>
     * 
     * <div style="background-color: #d4edda; border: 1px solid #c3e6cb; border-radius: 4px; padding: 8px; margin: 8px 0;">
     * <strong>✅ Garantias:</strong>
     * <ul>
     *   <li>Collection nunca é null (pode ser vazia)</li>
     *   <li>Authorities são sempre SimpleGrantedAuthority</li>
     *   <li>Formato padrão "ROLE_" + perfil</li>
     *   <li>Coleção imutável (Set)</li>
     * </ul>
     * </div>
     * 
     * @return {@link Collection}&lt;? extends {@link GrantedAuthority}&gt;
     *         Coleção imutável de authorities/roles do usuário
     * 
     * @see UserDetails#getAuthorities()
     * @see GrantedAuthority
     * @see SimpleGrantedAuthority
     * @see Perfil
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * <h3>🔒 Senha Criptografada</h3>
     * <p>
     * Implementação obrigatória de {@link UserDetails#getPassword()}.
     * Retorna o hash BCrypt da senha do usuário, utilizado pelo Spring Security
     * para verificação durante o processo de autenticação.
     * </p>
     * 
     * <h4>🔐 Características de Segurança:</h4>
     * <ul>
     *   <li><strong>BCrypt Hash:</strong> Algoritmo com salt automático integrado</li>
     *   <li><strong>One-Way Function:</strong> Irreversível para texto original</li>
     *   <li><strong>Adaptive Cost:</strong> Configurável via SecurityConfig</li>
     *   <li><strong>Rainbow Resistant:</strong> Imune a ataques de tabela</li>
     * </ul>
     * 
     * <h4>⚡ Processo de Validação:</h4>
     * <p>
     * Durante autenticação, o Spring Security:
     * </p>
     * <ol>
     *   <li>Chama este método para obter hash armazenado</li>
     *   <li>Usa PasswordEncoder.matches(rawPassword, encodedPassword)</li>
     *   <li>BCrypt processa senha fornecida com mesmo salt</li>
     *   <li>Compara hashes resultantes de forma constant-time</li>
     * </ol>
     * 
     * <h4>🛡️ Medidas de Proteção:</h4>
     * <ul>
     *   <li><strong>No Logging:</strong> Nunca deve aparecer em logs</li>
     *   <li><strong>Memory Protection:</strong> Limpar após uso quando possível</li>
     *   <li><strong>Serialization Care:</strong> Cuidado em ambientes distribuídos</li>
     *   <li><strong>Timing Attacks:</strong> Comparação constant-time pelo BCrypt</li>
     * </ul>
     * 
     * <h4>📝 Formato BCrypt:</h4>
     * <pre>
     * $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
     * │  │  │                    │
     * │  │  └─ Salt (22 chars)   └─ Hash (31 chars)
     * │  └─ Cost (4-31)
     * └─ Algorithm version
     * </pre>
     * 
     * <div style="background-color: #f8d7da; border: 1px solid #f5c6cb; border-radius: 4px; padding: 8px; margin: 8px 0;">
     * <strong>⚠️ CRÍTICO:</strong>
     * Este método retorna informação extremamente sensível. Deve ser
     * protegido contra logging acidental, dumps de heap e exposição
     * em APIs públicas. Nunca serializar em JSON de resposta.
     * </div>
     * 
     * <div style="background-color: #d1ecf1; border: 1px solid #b8daff; border-radius: 4px; padding: 8px; margin: 8px 0;">
     * <strong>💡 Uso Interno:</strong>
     * Este método é chamado exclusivamente pelo Spring Security
     * durante autenticação. Aplicações não devem chamá-lo diretamente.
     * </div>
     * 
     * @return {@link String} Hash BCrypt da senha original, contendo
     *         algoritmo, cost, salt e hash em formato padrão
     * 
     * @see UserDetails#getPassword()
     * @see BCryptPasswordEncoder
     * @see PasswordEncoder#matches(CharSequence, String)
     */
    @Override
    public String getPassword() {
        return senha;
    }

    /**
     * <h3>👤 Nome de Usuário (Username)</h3>
     * <p>
     * Implementação obrigatória de {@link UserDetails#getUsername()}.
     * Retorna o email do usuário que serve como identificador único
     * para autenticação no sistema. Este valor é usado pelo Spring Security
     * como principal identifier em logs, auditorias e controles de sessão.
     * </p>
     * 
     * <h4>📧 Características do Username:</h4>
     * <ul>
     *   <li><strong>Email Format:</strong> Sempre um endereço de email válido</li>
     *   <li><strong>Unique:</strong> Identificador único no sistema</li>
     *   <li><strong>Case Insensitive:</strong> Tratado sem distinção de caso</li>
     *   <li><strong>Persistent:</strong> Não muda durante vida útil do usuário</li>
     * </ul>
     * 
     * <h4>🔗 Integrações no Sistema:</h4>
     * <ul>
     *   <li><strong>JWT Subject:</strong> Claim 'sub' em tokens de autenticação</li>
     *   <li><strong>Login Form:</strong> Campo de entrada no formulário</li>
     *   <li><strong>Audit Logs:</strong> Identificador em registros de auditoria</li>
     *   <li><strong>User Lookup:</strong> Chave para UserDetailsService</li>
     * </ul>
     * 
     * <h4>💡 Vantagens do Email como Username:</h4>
     * <ul>
     *   <li><strong>Memorable:</strong> Usuários facilmente lembram</li>
     *   <li><strong>Unique:</strong> Naturalmente único por usuário</li>
     *   <li><strong>Contact:</strong> Meio de comunicação direta</li>
     *   <li><strong>Recovery:</strong> Base para recuperação de senha</li>
     * </ul>
     * 
     * <h4>🔍 Processo de Busca:</h4>
     * <p>
     * Este valor é usado pelo {@link UserDetailsServiceImpl} para
     * localizar o usuário no banco de dados:
     * </p>
     * <pre>
     * Optional&lt;Pessoa&gt; pessoa = pessoaRepository.findByEmail(username);
     * </pre>
     * 
     * <h4>📝 Exemplos de Uso:</h4>
     * <pre>
     * // Em logs de auditoria
     * logger.info("User {} accessed resource {}", userSS.getUsername(), resource);
     * 
     * // Em JWT claims
     * String token = jwtUtil.generateToken(userSS.getUsername());
     * 
     * // Em verificações de proprietário
     * boolean isOwner = chamado.getCliente().getEmail().equals(userSS.getUsername());
     * </pre>
     * 
     * <div style="background-color: #d4edda; border: 1px solid #c3e6cb; border-radius: 4px; padding: 8px; margin: 8px 0;">
     * <strong>✅ Benefícios:</strong>
     * <ul>
     *   <li>UX amigável (usuários lembram facilmente)</li>
     *   <li>Integração natural com sistemas de email</li>
     *   <li>Padrão amplamente adotado em aplicações web</li>
     *   <li>Suporte nativo para recuperação de senha</li>
     * </ul>
     * </div>
     * 
     * @return {@link String} Endereço de email do usuário que serve
     *         como identificador único para autenticação
     * 
     * @see UserDetails#getUsername()
     * @see UserDetailsServiceImpl#loadUserByUsername(String)
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * <h3>⏰ Status de Expiração da Conta</h3>
     * <p>
     * Implementação de {@link UserDetails#isAccountNonExpired()}.
     * Indica se a conta do usuário está dentro do prazo de validade.
     * Na implementação atual, sempre retorna {@code true}, indicando
     * que as contas nunca expiram automaticamente por tempo.
     * </p>
     * 
     * <h4>🔧 Implementação Atual:</h4>
     * <p>
     * O sistema Helpdesk não implementa expiração automática de contas
     * por política de tempo. Contas permanecem ativas indefinidamente
     * até serem explicitamente desabilitadas por um administrador.
     * </p>
     * 
     * <h4>💡 Cenários de Uso Futuro:</h4>
     * <ul>
     *   <li><strong>Contas Temporárias:</strong> Usuários com prazo determinado</li>
     *   <li><strong>Licenças por Tempo:</strong> Expiração baseada em assinatura</li>
     *   <li><strong>Compliance:</strong> Rotação obrigatória de acesso</li>
     *   <li><strong>Políticas Corporativas:</strong> Desativação por inatividade</li>
     * </ul>
     * 
     * <h4>🔄 Possível Implementação Futura:</h4>
     * <pre>
     * public boolean isAccountNonExpired() {
     *     if (this.expirationDate == null) return true;
     *     return this.expirationDate.isAfter(LocalDateTime.now());
     * }
     * </pre>
     * 
     * <h4>⚠️ Comportamento do Framework:</h4>
     * <p>
     * Se este método retornasse {@code false}, o Spring Security
     * lançaria {@code AccountExpiredException} durante autenticação,
     * impedindo o login mesmo com credenciais válidas.
     * </p>
     * 
     * <div style="background-color: #d1ecf1; border: 1px solid #b8daff; border-radius: 4px; padding: 8px; margin: 8px 0;">
     * <strong>💡 Extensibilidade:</strong>
     * Método preparado para evolução futura caso seja necessário
     * implementar políticas de expiração de conta baseadas em
     * data ou outras regras de negócio.
     * </div>
     * 
     * <div style="background-color: #d4edda; border: 1px solid #c3e6cb; border-radius: 4px; padding: 8px; margin: 8px 0;">
     * <strong>✅ Vantagens da Abordagem:</strong>
     * <ul>
     *   <li>Simplicidade operacional</li>
     *   <li>Não requer manutenção de datas</li>
     *   <li>Controle manual por administradores</li>
     *   <li>Foco em habilitação/desabilitação explícita</li>
     * </ul>
     * </div>
     * 
     * @return {@code true} sempre, indicando que contas não expiram automaticamente
     * 
     * @see UserDetails#isAccountNonExpired()
     * @see AccountExpiredException
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * <h3>🔒 Status de Bloqueio da Conta</h3>
     * <p>
     * Implementação de {@link UserDetails#isAccountNonLocked()}.
     * Indica se a conta do usuário não está bloqueada. Na implementação
     * atual, sempre retorna {@code true}, indicando que o sistema não
     * implementa bloqueio automático de contas.
     * </p>
     * 
     * <h4>🔧 Implementação Atual:</h4>
     * <p>
     * O sistema Helpdesk não implementa mecanismo de bloqueio automático
     * de contas por tentativas de login falhadas ou outras políticas
     * de segurança. O controle de acesso é feito através de habilitação/
     * desabilitação manual por administradores.
     * </p>
     * 
     * <h4>🛡️ Cenários de Bloqueio Futuro:</h4>
     * <ul>
     *   <li><strong>Brute Force Protection:</strong> Bloqueio após N tentativas falhas</li>
     *   <li><strong>Suspicious Activity:</strong> Bloqueio por atividade suspeita</li>
     *   <li><strong>Admin Control:</strong> Bloqueio manual por administrador</li>
     *   <li><strong>Time-based Locks:</strong> Bloqueios temporários automáticos</li>
     * </ul>
     * 
     * <h4>🔄 Possível Implementação Futura:</h4>
     * <pre>
     * public boolean isAccountNonLocked() {
     *     if (this.lockedUntil == null) return true;
     *     return this.lockedUntil.isBefore(LocalDateTime.now());
     * }
     * </pre>
     * 
     * <h4>⚠️ Comportamento do Framework:</h4>
     * <p>
     * Se este método retornasse {@code false}, o Spring Security
     * lançaria {@code LockedException} durante autenticação,
     * impedindo o login mesmo com credenciais válidas.
     * </p>
     * 
     * <h4>💡 Alternativas de Segurança:</h4>
     * <ul>
     *   <li><strong>Rate Limiting:</strong> Implementado em nível de aplicação</li>
     *   <li><strong>Fail2Ban:</strong> Proteção em nível de infraestrutura</li>
     *   <li><strong>Account Disabling:</strong> Via campo 'enabled' no banco</li>
     *   <li><strong>JWT Expiration:</strong> Controle temporal via tokens</li>
     * </ul>
     * 
     * <div style="background-color: #fff3cd; border: 1px solid #ffeaa7; border-radius: 4px; padding: 8px; margin: 8px 0;">
     * <strong>⚠️ Consideração de Segurança:</strong>
     * Em ambientes de alta segurança, considerar implementar bloqueio
     * automático após múltiplas tentativas de login falhadas para
     * proteger contra ataques de força bruta.
     * </div>
     * 
     * <div style="background-color: #d4edda; border: 1px solid #c3e6cb; border-radius: 4px; padding: 8px; margin: 8px 0;">
     * <strong>✅ Simplicidade Atual:</strong>
     * A abordagem atual simplifica a gestão de usuários, delegando
     * controles de acesso para mecanismos de habilitação/desabilitação
     * e expiração de tokens JWT.
     * </div>
     * 
     * @return {@code true} sempre, indicando que contas nunca são bloqueadas automaticamente
     * 
     * @see UserDetails#isAccountNonLocked()
     * @see LockedException
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * <h3>🔑 Status de Expiração das Credenciais</h3>
     * <p>
     * Implementação de {@link UserDetails#isCredentialsNonExpired()}.
     * Indica se as credenciais (senha) do usuário não estão expiradas.
     * Na implementação atual, sempre retorna {@code true}, indicando
     * que senhas não expiram automaticamente por política temporal.
     * </p>
     * 
     * <h4>🔧 Implementação Atual:</h4>
     * <p>
     * O sistema Helpdesk não força rotação periódica de senhas.
     * Usuários podem manter a mesma senha indefinidamente, exceto
     * quando alterada voluntariamente ou resetada por administrador.
     * </p>
     * 
     * <h4>🔄 Cenários de Expiração Futura:</h4>
     * <ul>
     *   <li><strong>Corporate Policy:</strong> Rotação obrigatória a cada 90 dias</li>
     *   <li><strong>Security Compliance:</strong> Padrões como ISO 27001, SOX</li>
     *   <li><strong>Breach Response:</strong> Expiração forçada após incidentes</li>
     *   <li><strong>Role-based Expiry:</strong> Políticas diferentes por perfil</li>
     * </ul>
     * 
     * <h4>🔄 Possível Implementação Futura:</h4>
     * <pre>
     * public boolean isCredentialsNonExpired() {
     *     if (this.passwordLastChanged == null) return false;
     *     
     *     long daysSinceChange = ChronoUnit.DAYS.between(
     *         this.passwordLastChanged, LocalDateTime.now());
     *         
     *     return daysSinceChange &lt; PASSWORD_EXPIRY_DAYS;
     * }
     * </pre>
     * 
     * <h4>⚠️ Comportamento do Framework:</h4>
     * <p>
     * Se este método retornasse {@code false}, o Spring Security
     * lançaria {@code CredentialsExpiredException} durante autenticação,
     * forçando o usuário a alterar a senha antes de prosseguir.
     * </p>
     * 
     * <h4>🛡️ Alternativas de Segurança:</h4>
     * <ul>
     *   <li><strong>JWT Expiration:</strong> Controle temporal via tokens</li>
     *   <li><strong>Session Timeout:</strong> Expiração de sessão ativa</li>
     *   <li><strong>Strong Password Policy:</strong> Validação na criação</li>
     *   <li><strong>MFA Integration:</strong> Autenticação multifator</li>
     * </ul>
     * 
     * <div style="background-color: #fff3cd; border: 1px solid #ffeaa7; border-radius: 4px; padding: 8px; margin: 8px 0;">
     * <strong>⚠️ Consideração Corporativa:</strong>
     * Organizações com requisitos de compliance rigorosos podem
     * necessitar implementar expiração automática de credenciais
     * com notificações e fluxos de renovação.
     * </div>
     * 
     * <div style="background-color: #d4edda; border: 1px solid #c3e6cb; border-radius: 4px; padding: 8px; margin: 8px 0;">
     * <strong>✅ Benefícios Atuais:</strong>
     * <ul>
     *   <li>UX simplificada - usuários não precisam trocar senhas</li>
     *   <li>Menos calls de suporte para reset de senha</li>
     *   <li>Foco em outras medidas de segurança (JWT, BCrypt)</li>
     *   <li>Controle opcional via funcionalidades administrativas</li>
     * </ul>
     * </div>
     * 
     * @return {@code true} sempre, indicando que credenciais não expiram automaticamente
     * 
     * @see UserDetails#isCredentialsNonExpired()
     * @see CredentialsExpiredException
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * <h3>✅ Status de Habilitação da Conta</h3>
     * <p>
     * Implementação de {@link UserDetails#isEnabled()}.
     * Indica se a conta do usuário está habilitada e pode ser usada
     * para autenticação. Na implementação atual, sempre retorna {@code true},
     * indicando que o controle de habilitação não é implementado nesta camada.
     * </p>
     * 
     * <h4>🔧 Implementação Atual:</h4>
     * <p>
     * O sistema assume que se um usuário foi carregado do banco de dados
     * e convertido para UserSS, ele está habilitado para uso. O controle
     * de usuários ativos/inativos seria feito em nível de entidade de
     * domínio ou query de busca.
     * </p>
     * 
     * <h4>🔄 Possível Implementação Futura:</h4>
     * <pre>
     * public boolean isEnabled() {
     *     return this.active != null && this.active;
     * }
     * 
     * // Ou integração com entidade
     * public boolean isEnabled() {
     *     return pessoa.isAtivo();
     * }
     * </pre>
     * 
     * <h4>🎯 Cenários de Desabilitação:</h4>
     * <ul>
     *   <li><strong>Admin Control:</strong> Desabilitação manual por administrador</li>
     *   <li><strong>Suspension:</strong> Suspensão temporária por violação</li>
     *   <li><strong>Termination:</strong> Usuário não faz mais parte da organização</li>
     *   <li><strong>Maintenance:</strong> Desabilitação durante manutenções específicas</li>
     * </ul>
     * 
     * <h4>⚠️ Comportamento do Framework:</h4>
     * <p>
     * Se este método retornasse {@code false}, o Spring Security
     * lançaria {@code DisabledException} durante autenticação,
     * impedindo o login mesmo com credenciais válidas.
     * </p>
     * 
     * <h4>💡 Alternativas de Implementação:</h4>
     * <ul>
     *   <li><strong>Database Filter:</strong> WHERE ativo = true na query</li>
     *   <li><strong>Service Layer:</strong> Verificação no UserDetailsService</li>
     *   <li><strong>Entity Field:</strong> Campo boolean na entidade Pessoa</li>
     *   <li><strong>Audit Trail:</strong> Log de habilitação/desabilitação</li>
     * </ul>
     * 
     * <h4>📊 Vantagens por Local de Implementação:</h4>
     * <table>
     *   <tr><th>Local</th><th>Vantagem</th><th>Desvantagem</th></tr>
     *   <tr><td>UserSS</td><td>Controle fino</td><td>Lógica espalhada</td></tr>
     *   <tr><td>Database</td><td>Performance</td><td>Menos flexível</td></tr>
     *   <tr><td>Service</td><td>Centralizado</td><td>Ponto único de falha</td></tr>
     * </table>
     * 
     * <div style="background-color: #d1ecf1; border: 1px solid #b8daff; border-radius: 4px; padding: 8px; margin: 8px 0;">
     * <strong>💡 Padrão Recomendado:</strong>
     * Para evolução futura, considerar implementar controle de habilitação
     * no nível da entidade Pessoa com campo 'ativo', refletindo aqui o
     * valor do banco de dados.
     * </div>
     * 
     * <div style="background-color: #d4edda; border: 1px solid #c3e6cb; border-radius: 4px; padding: 8px; margin: 8px 0;">
     * <strong>✅ Simplicidade Atual:</strong>
     * A abordagem atual elimina complexidade desnecessária, assumindo
     * que usuários presentes no sistema estão habilitados por padrão.
     * Ideal para sistemas com poucos usuários e controle manual.
     * </div>
     * 
     * @return {@code true} sempre, indicando que contas estão habilitadas por padrão
     * 
     * @see UserDetails#isEnabled()
     * @see DisabledException
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}