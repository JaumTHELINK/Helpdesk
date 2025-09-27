package com.turmaa.helpdesk.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.turmaa.helpdesk.domain.Cliente;

/**
 * <h1>Repository para Entidade Cliente</h1>
 * <p>
 * Interface de acesso a dados especializada para a entidade Cliente.
 * Estende JpaRepository fornecendo operações CRUD padrão e consultas
 * customizadas específicas para clientes do sistema de helpdesk.
 * </p>
 * 
 * <h2>Funcionalidades Específicas:</h2>
 * <ul>
 *   <li><strong>Busca por CPF:</strong> Validação de unicidade de documento</li>
 *   <li><strong>Busca por Email:</strong> Localização para autenticação</li>
 *   <li><strong>CRUD Completo:</strong> Operações herdadas do JpaRepository</li>
 *   <li><strong>Validações:</strong> Suporte a verificação de duplicatas</li>
 * </ul>
 * 
 * <h2>Contexto de Negócio:</h2>
 * <p>
 * Clientes são os usuários finais que criam chamados de suporte no sistema.
 * Diferentemente dos técnicos, clientes têm acesso restrito apenas aos seus
 * próprios chamados e informações pessoais.
 * </p>
 * 
 * <h2>Validações de Integridade:</h2>
 * <ul>
 *   <li><strong>CPF Único:</strong> Cada cliente deve ter documento único</li>
 *   <li><strong>Email Único:</strong> Usado como identificador de login</li>
 *   <li><strong>Relacionamentos:</strong> Clientes podem ter múltiplos chamados</li>
 * </ul>
 * 
 * <h2>Herança JPA:</h2>
 * <p>
 * Como Cliente herda de Pessoa com estratégia JOINED, as consultas
 * automaticamente fazem JOIN entre as tabelas 'pessoa' e 'cliente',
 * garantindo acesso completo a todos os campos da hierarquia.
 * </p>
 * 
 * <h2>Segurança:</h2>
 * <p>
 * Os métodos desta interface devem ser usados em contextos que requerem
 * operações específicas de clientes, especialmente em validações de
 * autorização onde se precisa garantir que apenas clientes acessem
 * determinadas funcionalidades.
 * </p>
 * 
 * @author Sistema Helpdesk
 * @version 1.0
 * @since 2024
 * 
 * @see Cliente
 * @see com.turmaa.helpdesk.domain.Pessoa
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    /**
     * <h3>Buscar Cliente por CPF</h3>
     * <p>
     * Localiza um cliente específico através de seu número de CPF.
     * Este método é fundamental para validações de unicidade de documento
     * durante processos de cadastro e atualização de clientes.
     * </p>
     * 
     * <h4>Casos de Uso:</h4>
     * <ul>
     *   <li><strong>Validação de Cadastro:</strong> Verificar se CPF já está em uso</li>
     *   <li><strong>Auto-cadastro:</strong> Evitar duplicação quando cliente se registra</li>
     *   <li><strong>Atualização:</strong> Validar unicidade em edições (exceto próprio registro)</li>
     *   <li><strong>Auditoria:</strong> Localizar cliente por documento oficial</li>
     *   <li><strong>Suporte:</strong> Técnicos localizarem cliente para atendimento</li>
     * </ul>
     * 
     * <h4>Query SQL Gerada:</h4>
     * <pre>{@code
     * SELECT c.*, p.* FROM cliente c
     * INNER JOIN pessoa p ON c.id = p.id
     * WHERE p.cpf = ?
     * }</pre>
     * 
     * <h4>Validação de Unicidade:</h4>
     * <pre>{@code
     * // Exemplo de uso em validação
     * Optional<Cliente> existente = repository.findByCpf(novoCpf);
     * if (existente.isPresent()) {
     *     throw new DataIntegrityViolationException("CPF já cadastrado");
     * }
     * }</pre>
     * 
     * <h4>Considerações de Privacidade:</h4>
     * <p>
     * CPF é informação sensível. Logs devem ser cuidadosamente configurados
     * para não expor números de documento em arquivos de auditoria.
     * </p>
     * 
     * @param cpf {@link String} Número do CPF a ser localizado (formato: XXX.XXX.XXX-XX ou XXXXXXXXXXX)
     * 
     * @return {@link Optional}&lt;{@link Cliente}&gt; Contém o cliente encontrado se existir,
     *         ou Optional vazio se não houver cliente com o CPF especificado
     * 
     * @see TecnicoRepository#findByCpf(String)
     */
    Optional<Cliente> findByCpf(String cpf);

    /**
     * <h3>Buscar Cliente por Email</h3>
     * <p>
     * Localiza um cliente específico através de seu endereço de email.
     * Este método é essencial para autenticação e validações de unicidade
     * do email no contexto específico de clientes.
     * </p>
     * 
     * <h4>Diferença do PessoaRepository:</h4>
     * <p>
     * Enquanto PessoaRepository.findByEmail() retorna qualquer pessoa (Cliente ou Técnico),
     * este método garante retorno específico de clientes, útil quando se precisa
     * de operações ou validações específicas da classe Cliente.
     * </p>
     * 
     * <h4>Casos de Uso:</h4>
     * <ul>
     *   <li><strong>Autenticação de Cliente:</strong> Login específico para área do cliente</li>
     *   <li><strong>Validação de Cadastro:</strong> Verificar unicidade de email</li>
     *   <li><strong>Criação de Chamados:</strong> Associar chamado ao cliente correto</li>
     *   <li><strong>Notificações:</strong> Envio de updates sobre chamados</li>
     *   <li><strong>Auto-cadastro:</strong> Verificar se email já tem conta</li>
     * </ul>
     * 
     * <h4>Query SQL Gerada:</h4>
     * <pre>{@code
     * SELECT c.*, p.* FROM cliente c
     * INNER JOIN pessoa p ON c.id = p.id
     * WHERE p.email = ?
     * }</pre>
     * 
     * <h4>Segurança de Acesso:</h4>
     * <p>
     * Este método é frequentemente usado em contextos onde se precisa
     * garantir que apenas usuários com perfil CLIENTE tenham acesso
     * a determinadas funcionalidades (como criar chamados).
     * </p>
     * 
     * <h4>Integração com Autenticação:</h4>
     * <pre>{@code
     * // Exemplo de uso em autenticação
     * Optional<Cliente> cliente = repository.findByEmail(email);
     * if (cliente.isPresent() && senhaValida) {
     *     // Gerar JWT para cliente
     * }
     * }</pre>
     * 
     * @param email {@link String} Endereço de email a ser localizado (case sensitive)
     * 
     * @return {@link Optional}&lt;{@link Cliente}&gt; Contém o cliente encontrado se existir,
     *         ou Optional vazio se não houver cliente com o email especificado
     * 
     * @see PessoaRepository#findByEmail(String)
     * @see TecnicoRepository#findByEmail(String)
     */
    Optional<Cliente> findByEmail(String email);
}
