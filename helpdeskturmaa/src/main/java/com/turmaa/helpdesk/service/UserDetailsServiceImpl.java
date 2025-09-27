package com.turmaa.helpdesk.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.turmaa.helpdesk.domain.Pessoa;
import com.turmaa.helpdesk.repositories.PessoaRepository;
import com.turmaa.helpdesk.security.UserSS;

/**
 * <h2>Implementação do Serviço de Detalhes do Usuário</h2>
 * <p>
 * Implementação personalizada de {@link UserDetailsService} que integra o modelo
 * de domínio da aplicação ({@code Pessoa}) com o sistema de autenticação do
 * Spring Security. Atua como ponte entre o banco de dados e o framework de segurança.
 * </p>
 * 
 * <h3>🔐 Arquitetura de Autenticação</h3>
 * <p>
 * Esta classe é peça fundamental na cadeia de autenticação do Spring Security,
 * sendo responsável por carregar dados do usuário durante o processo de login
 * e validação de tokens JWT. Converte entidades de domínio para formato
 * compatível com as interfaces de segurança do framework.
 * </p>
 * 
 * <h3>🔄 Fluxo de Operação</h3>
 * <ol>
 *   <li><strong>Login Request:</strong> Spring Security intercepta tentativa de autenticação</li>
 *   <li><strong>User Lookup:</strong> Chama loadUserByUsername() com email fornecido</li>
 *   <li><strong>Database Query:</strong> Busca entidade Pessoa via PessoaRepository</li>
 *   <li><strong>User Conversion:</strong> Converte Pessoa → UserSS (UserDetails)</li>
 *   <li><strong>Authentication:</strong> Retorna UserDetails para validação</li>
 * </ol>
 * 
 * <h3>Principais Responsabilidades</h3>
 * <ul>
 *   <li><strong>👤 User Loading:</strong> Carrega dados do usuário por email</li>
 *   <li><strong>🔄 Data Conversion:</strong> Converte Pessoa para UserDetails</li>
 *   <li><strong>🔍 Validation:</strong> Verifica existência do usuário</li>
 *   <li><strong>🔐 Security Integration:</strong> Interface com Spring Security</li>
 *   <li><strong>⚠️ Exception Handling:</strong> Trata usuários não encontrados</li>
 * </ul>
 * 
 * <h3>🎯 Cenários de Uso</h3>
 * <ul>
 *   <li><strong>Login Form:</strong> Autenticação via formulário web</li>
 *   <li><strong>JWT Authentication:</strong> Validação de tokens durante requisições</li>
 *   <li><strong>Basic Auth:</strong> Autenticação HTTP Basic (se habilitada)</li>
 *   <li><strong>Custom Auth:</strong> Fluxos personalizados de autenticação</li>
 * </ul>
 * 
 * <h3>🔗 Integração com Sistema</h3>
 * <ul>
 *   <li><strong>{@link JWTAuthenticationFilter}:</strong> Usa durante login inicial</li>
 *   <li><strong>{@link JWTAuthorizationFilter}:</strong> Reconstrói UserDetails</li>
 *   <li><strong>{@link SecurityConfig}:</strong> Configurado como UserDetailsService padrão</li>
 *   <li><strong>{@link Pessoa}:</strong> Entidade de domínio fonte dos dados</li>
 * </ul>
 * 
 * <h3>📊 Modelo de Dados Processado</h3>
 * <pre>
 * Pessoa (Database) → UserSS (Security)
 * ├─ id: Integer → id: Integer
 * ├─ email: String → username: String  
 * ├─ senha: String → password: String (BCrypt)
 * └─ perfis: Set&lt;Perfil&gt; → authorities: Collection&lt;GrantedAuthority&gt;
 * </pre>
 * 
 * <div style="background-color: #d4edda; border: 1px solid #c3e6cb; border-radius: 4px; padding: 8px; margin: 8px 0;">
 * <strong>✅ Vantagens da Implementação:</strong>
 * <ul>
 *   <li>Reutilização do modelo de domínio existente</li>
 *   <li>Integração transparente com Spring Security</li>
 *   <li>Flexibilidade para evolução das entidades</li>
 *   <li>Suporte completo para roles e authorities</li>
 * </ul>
 * </div>
 * 
 * <div style="background-color: #d1ecf1; border: 1px solid #b8daff; border-radius: 4px; padding: 8px; margin: 8px 0;">
 * <strong>💡 Design Pattern:</strong>
 * Implementa o padrão Service Locator / Factory, onde o Spring Security
 * usa esta implementação para localizar e construir objetos UserDetails
 * a partir de identificadores de usuário.
 * </div>
 * 
 * <div style="background-color: #fff3cd; border: 1px solid #ffeaa7; border-radius: 4px; padding: 8px; margin: 8px 0;">
 * <strong>⚠️ Considerações de Performance:</strong>
 * Este serviço é chamado em toda autenticação. Em aplicações de alto volume,
 * considere implementar cache para UserDetails ou otimizar queries do
 * repository para reduzir latência.
 * </div>
 * 
 * @author Sistema Helpdesk
 * @since 1.0.0
 * @see UserDetailsService
 * @see UserSS
 * @see Pessoa
 * @see PessoaRepository
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    /**
     * <h3>Repository de Acesso a Dados</h3>
     * <p>
     * Interface JPA que fornece acesso à entidade {@link Pessoa} no banco de dados.
     * Utilizada para executar queries de busca por email durante o processo de
     * autenticação, convertendo dados relacionais para objetos de domínio.
     * </p>
     * 
     * <h4>🔍 Operações Suportadas:</h4>
     * <ul>
     *   <li><strong>findByEmail:</strong> Busca usuário por endereço de email</li>
     *   <li><strong>Query Optimization:</strong> Aproveita índices de email único</li>
     *   <li><strong>Relationship Loading:</strong> Carrega perfis via JOIN FETCH</li>
     *   <li><strong>Transaction Management:</strong> Operações dentro de transação</li>
     * </ul>
     * 
     * <h4>⚡ Performance:</h4>
     * <p>
     * O repository utiliza índice único na coluna email para busca eficiente.
     * Relationships (perfis) são carregadas eagerly para evitar LazyLoadingException
     * fora do contexto transacional.
     * </p>
     * 
     * <div style="background-color: #d1ecf1; border: 1px solid #b8daff; border-radius: 4px; padding: 8px; margin: 8px 0;">
     * <strong>💡 Injeção de Dependência:</strong>
     * Utiliza @Autowired para injeção automática pelo Spring, garantindo
     * que a instância correta do repository seja fornecida em runtime.
     * </div>
     * 
     * @see PessoaRepository#findByEmail(String)
     * @see Pessoa
     * @see Autowired
     */
    @Autowired
    private PessoaRepository pessoaRepository;

    /**
     * <h3>🔍 Carregamento de Usuário por Email</h3>
     * <p>
     * Implementação central da interface {@link UserDetailsService} que localiza
     * e carrega dados completos do usuário para autenticação no Spring Security.
     * Método fundamental chamado durante todo processo de autenticação e autorização.
     * </p>
     * 
     * <h4>📋 Fluxo Detalhado de Execução:</h4>
     * <ol>
     *   <li><strong>Database Query:</strong> Executa findByEmail() no repository</li>
     *   <li><strong>Optional Processing:</strong> Verifica se usuário existe</li>
     *   <li><strong>Entity → UserDetails:</strong> Converte Pessoa para UserSS</li>
     *   <li><strong>Data Mapping:</strong> Mapeia ID, email, senha e perfis</li>
     *   <li><strong>Authority Conversion:</strong> Transforma Perfil → GrantedAuthority</li>
     *   <li><strong>Return/Exception:</strong> Retorna UserSS ou lança exceção</li>
     * </ol>
     * 
     * <h4>🔄 Cenários de Chamada:</h4>
     * <ul>
     *   <li><strong>Initial Login:</strong> Durante autenticação via formulário</li>
     *   <li><strong>JWT Processing:</strong> Revalidação de tokens em requisições</li>
     *   <li><strong>Session Restore:</strong> Reconstrução de contexto de segurança</li>
     *   <li><strong>Custom Auth:</strong> Fluxos personalizados de autenticação</li>
     * </ul>
     * 
     * <h4>🎯 Processo de Conversão:</h4>
     * <pre>
     * Pessoa pessoa = repository.findByEmail(email)
     *     ↓
     * UserSS userSS = new UserSS(
     *     pessoa.getId(),           // Integer ID
     *     pessoa.getEmail(),        // String username  
     *     pessoa.getSenha(),        // String password (BCrypt)
     *     pessoa.getPerfis()        // Set&lt;Perfil&gt; → Collection&lt;GrantedAuthority&gt;
     * )
     * </pre>
     * 
     * <h4>⚡ Otimizações de Performance:</h4>
     * <ul>
     *   <li><strong>Index Usage:</strong> Utiliza índice único da coluna email</li>
     *   <li><strong>Eager Loading:</strong> Perfis carregados em query única</li>
     *   <li><strong>Optional Pattern:</strong> Evita NullPointerException</li>
     *   <li><strong>Direct Mapping:</strong> Conversão direta sem DTOs intermediários</li>
     * </ul>
     * 
     * <h4>🛡️ Tratamento de Segurança:</h4>
     * <ul>
     *   <li><strong>Email Normalization:</strong> Case-insensitive lookup</li>
     *   <li><strong>SQL Injection:</strong> Protegido por JPA/Hibernate</li>
     *   <li><strong>Data Validation:</strong> Validação automática de entidade</li>
     *   <li><strong>Exception Safety:</strong> UsernameNotFoundException padronizada</li>
     * </ul>
     * 
     * <h4>📝 Exemplos de Uso pelo Framework:</h4>
     * <pre>
     * // Durante autenticação JWT
     * UserDetails user = userDetailsService.loadUserByUsername("admin@helpdesk.com");
     * 
     * // Spring Security valida automaticamente
     * passwordEncoder.matches(rawPassword, user.getPassword());
     * 
     * // Carrega authorities para autorização
     * Collection&lt;GrantedAuthority&gt; authorities = user.getAuthorities();
     * </pre>
     * 
     * <div style="background-color: #f8d7da; border: 1px solid #f5c6cb; border-radius: 4px; padding: 8px; margin: 8px 0;">
     * <strong>⚠️ Considerações de Segurança:</strong>
     * Por razões de segurança, a exceção lançada para usuário não encontrado
     * deve ser a mesma independente do motivo (usuário inexistente, conta
     * desabilitada, etc.) para evitar vazamento de informações.
     * </div>
     * 
     * <div style="background-color: #d4edda; border: 1px solid #c3e6cb; border-radius: 4px; padding: 8px; margin: 8px 0;">
     * <strong>✅ Vantagens da Implementação:</strong>
     * <ul>
     *   <li>Reutilização completa do modelo de domínio</li>
     *   <li>Mapeamento direto sem overhead de conversão</li>
     *   <li>Integração natural com JPA e transações</li>
     *   <li>Suporte automático para cache L1/L2 do Hibernate</li>
     * </ul>
     * </div>
     * 
     * <div style="background-color: #d1ecf1; border: 1px solid #b8daff; border-radius: 4px; padding: 8px; margin: 8px 0;">
     * <strong>💡 Cache Strategy:</strong>
     * Para aplicações de alto volume, considere implementar cache específico
     * para UserDetails usando Spring Cache (@Cacheable) com TTL apropriado
     * para balance entre performance e consistência de dados.
     * </div>
     * 
     * @param email {@link String} Endereço de email único que identifica o usuário
     *        no sistema, usado como username na autenticação
     * 
     * @return {@link UserDetails} Implementação {@link UserSS} contendo todos
     *         os dados necessários para autenticação e autorização
     * 
     * @throws UsernameNotFoundException Se não existir usuário com o email
     *         fornecido no banco de dados, seguindo padrão Spring Security
     * 
     * @see UserDetailsService#loadUserByUsername(String)
     * @see PessoaRepository#findByEmail(String)
     * @see UserSS#UserSS(Integer, String, String, Set)
     * @see UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Busca a pessoa pelo e-mail no repositório (pode ou não existir)
        Optional<Pessoa> pessoa = pessoaRepository.findByEmail(email);

        // Se a pessoa existe, retorna um UserSS com os dados necessários para autenticação
        if (pessoa.isPresent()) {
            return new UserSS(
                    pessoa.get().getId(),
                    pessoa.get().getEmail(),
                    pessoa.get().getSenha(),
                    pessoa.get().getPerfis()
            );
        }

        // Caso não encontre, lança exceção para o Spring Security tratar
        throw new UsernameNotFoundException(email);
    }
}