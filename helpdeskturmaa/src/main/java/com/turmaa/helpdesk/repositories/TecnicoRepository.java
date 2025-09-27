package com.turmaa.helpdesk.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.turmaa.helpdesk.domain.Tecnico;

/**
 * <h1>Repository para Entidade Técnico</h1>
 * <p>
 * Interface de acesso a dados especializada para a entidade Técnico.
 * Estende JpaRepository fornecendo operações CRUD padrão e consultas
 * customizadas específicas para técnicos do sistema de helpdesk.
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
 * <h2>Validações de Integridade:</h2>
 * <p>
 * Os métodos customizados (findByCpf e findByEmail) são essenciais para:
 * </p>
 * <ul>
 *   <li><strong>Prevenção de Duplicatas:</strong> Validar unicidade antes de salvar</li>
 *   <li><strong>Autenticação:</strong> Localizar técnico durante login</li>
 *   <li><strong>Validações de Negócio:</strong> Verificar existência em atualizações</li>
 * </ul>
 * 
 * <h2>Herança JPA:</h2>
 * <p>
 * Como Técnico herda de Pessoa com estratégia JOINED, as consultas
 * automaticamente fazem JOIN entre as tabelas 'pessoa' e 'tecnico',
 * garantindo acesso completo a todos os campos da hierarquia.
 * </p>
 * 
 * <h2>Performance:</h2>
 * <ul>
 *   <li><strong>Índices:</strong> CPF e Email devem ser indexados no banco</li>
 *   <li><strong>Cache:</strong> JPA Second Level Cache pode ser aplicado</li>
 *   <li><strong>Lazy Loading:</strong> Relacionamentos carregados conforme necessário</li>
 * </ul>
 * 
 * @author Sistema Helpdesk
 * @version 1.0
 * @since 2024
 * 
 * @see Tecnico
 * @see com.turmaa.helpdesk.domain.Pessoa
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
@Repository
public interface TecnicoRepository extends JpaRepository<Tecnico, Integer> {
    
    /**
     * <h3>Buscar Técnico por CPF</h3>
     * <p>
     * Localiza um técnico específico através de seu número de CPF.
     * Este método é fundamental para validações de unicidade de documento
     * durante processos de cadastro e atualização de técnicos.
     * </p>
     * 
     * <h4>Casos de Uso:</h4>
     * <ul>
     *   <li><strong>Validação de Cadastro:</strong> Verificar se CPF já está em uso</li>
     *   <li><strong>Atualização:</strong> Validar unicidade em edições (exceto próprio registro)</li>
     *   <li><strong>Auditoria:</strong> Localizar técnico por documento oficial</li>
     *   <li><strong>Integração:</strong> Sincronização com sistemas externos por CPF</li>
     * </ul>
     * 
     * <h4>Query SQL Gerada:</h4>
     * <pre>{@code
     * SELECT t.*, p.* FROM tecnico t
     * INNER JOIN pessoa p ON t.id = p.id
     * WHERE p.cpf = ?
     * }</pre>
     * 
     * <h4>Validação de Unicidade:</h4>
     * <pre>{@code
     * // Exemplo de uso em validação
     * Optional<Tecnico> existente = repository.findByCpf(novoCpf);
     * if (existente.isPresent()) {
     *     throw new ValidacaoException("CPF já cadastrado");
     * }
     * }</pre>
     * 
     * @param cpf {@link String} Número do CPF a ser localizado (formato: XXX.XXX.XXX-XX ou XXXXXXXXXXX)
     * 
     * @return {@link Optional}&lt;{@link Tecnico}&gt; Contém o técnico encontrado se existir,
     *         ou Optional vazio se não houver técnico com o CPF especificado
     */
    Optional<Tecnico> findByCpf(String cpf);

    /**
     * <h3>Buscar Técnico por Email</h3>
     * <p>
     * Localiza um técnico específico através de seu endereço de email.
     * Este método é essencial para autenticação e validações de unicidade
     * do email no contexto específico de técnicos.
     * </p>
     * 
     * <h4>Diferença do PessoaRepository:</h4>
     * <p>
     * Enquanto PessoaRepository.findByEmail() retorna qualquer pessoa (Cliente ou Técnico),
     * este método garante retorno específico de técnicos, útil quando se precisa
     * de operações ou validações específicas da classe Técnico.
     * </p>
     * 
     * <h4>Casos de Uso:</h4>
     * <ul>
     *   <li><strong>Autenticação Específica:</strong> Login restrito a técnicos</li>
     *   <li><strong>Validação de Cadastro:</strong> Verificar unicidade de email</li>
     *   <li><strong>Atribuição de Chamados:</strong> Localizar técnico responsável</li>
     *   <li><strong>Notificações:</strong> Envio de alertas específicos para técnicos</li>
     * </ul>
     * 
     * <h4>Query SQL Gerada:</h4>
     * <pre>{@code
     * SELECT t.*, p.* FROM tecnico t
     * INNER JOIN pessoa p ON t.id = p.id
     * WHERE p.email = ?
     * }</pre>
     * 
     * <h4>Segurança:</h4>
     * <p>
     * Este método pode ser usado em endpoints específicos de técnicos
     * onde se precisa garantir que apenas usuários com perfil TECNICO
     * sejam processados.
     * </p>
     * 
     * @param email {@link String} Endereço de email a ser localizado (case sensitive)
     * 
     * @return {@link Optional}&lt;{@link Tecnico}&gt; Contém o técnico encontrado se existir,
     *         ou Optional vazio se não houver técnico com o email especificado
     * 
     * @see PessoaRepository#findByEmail(String)
     */
    Optional<Tecnico> findByEmail(String email);
}
