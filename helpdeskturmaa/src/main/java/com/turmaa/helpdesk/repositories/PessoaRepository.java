package com.turmaa.helpdesk.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.turmaa.helpdesk.domain.Pessoa;

/**
 * <h1>Repository para Entidade Pessoa</h1>
 * <p>
 * Interface de acesso a dados da entidade Pessoa, que serve como classe base
 * para Técnicos e Clientes. Esta interface estende JpaRepository fornecendo
 * operações CRUD padrão e consultas customizadas específicas para a hierarquia
 * de pessoas no sistema de helpdesk.
 * </p>
 * 
 * <h2>Funcionalidades Principais:</h2>
 * <ul>
 *   <li><strong>CRUD Completo:</strong> Operações básicas herdadas do JpaRepository</li>
 *   <li><strong>Busca por Email:</strong> Método customizado para autenticação</li>
 *   <li><strong>Herança JPA:</strong> Suporte a consultas polimórficas</li>
 *   <li><strong>Transacional:</strong> Operações automáticas com controle transacional</li>
 * </ul>
 * 
 * <h2>Herança e Polimorfismo:</h2>
 * <p>
 * Esta interface trabalha com a estratégia de herança JOINED do JPA, onde
 * cada subclasse (Técnico, Cliente) possui sua própria tabela, mas compartilham
 * os campos básicos da tabela Pessoa. Isso permite consultas eficientes e
 * organizadas por tipo.
 * </p>
 * 
 * <h2>Integração com Spring Security:</h2>
 * <p>
 * O método findByEmail é fundamental para o processo de autenticação,
 * permitindo localizar usuários pelo email (usado como username) durante
 * o login no sistema.
 * </p>
 * 
 * <h2>Operações Automáticas Disponíveis:</h2>
 * <ul>
 *   <li><strong>save(Pessoa):</strong> Inserir/atualizar pessoa</li>
 *   <li><strong>findById(Integer):</strong> Buscar por ID</li>
 *   <li><strong>findAll():</strong> Listar todas as pessoas</li>
 *   <li><strong>deleteById(Integer):</strong> Remover por ID</li>
 *   <li><strong>count():</strong> Contar total de registros</li>
 * </ul>
 * 
 * @author Sistema Helpdesk
 * @version 1.0
 * @since 2024
 * 
 * @see Pessoa
 * @see com.turmaa.helpdesk.domain.Tecnico
 * @see com.turmaa.helpdesk.domain.Cliente
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, Integer> {
    
    /**
     * <h3>Buscar Pessoa por Email</h3>
     * <p>
     * Localiza uma pessoa específica através de seu endereço de email.
     * Este método é fundamental para o sistema de autenticação, onde o
     * email serve como identificador único de login para todos os usuários.
     * </p>
     * 
     * <h4>Características da Busca:</h4>
     * <ul>
     *   <li><strong>Case Sensitive:</strong> Busca exata, considerando maiúsculas/minúsculas</li>
     *   <li><strong>Polimórfica:</strong> Retorna qualquer subtipo (Técnico ou Cliente)</li>
     *   <li><strong>Única:</strong> Email deve ser único no sistema por validação</li>
     *   <li><strong>Otimizada:</strong> JPA gera consulta SQL eficiente automaticamente</li>
     * </ul>
     * 
     * <h4>Uso Principal:</h4>
     * <ul>
     *   <li><strong>Autenticação:</strong> Validar credenciais durante login</li>
     *   <li><strong>Validação:</strong> Verificar unicidade de email em cadastros</li>
     *   <li><strong>Recuperação:</strong> Processos de reset de senha</li>
     * </ul>
     * 
     * <h4>Query SQL Gerada:</h4>
     * <pre>{@code
     * SELECT p.* FROM pessoa p WHERE p.email = ?
     * }</pre>
     * 
     * <h4>Tratamento de Resultado:</h4>
     * <p>
     * O Optional retornado deve sempre ser verificado antes do uso:
     * </p>
     * <pre>{@code
     * Optional<Pessoa> pessoa = repository.findByEmail("user@email.com");
     * if (pessoa.isPresent()) {
     *     // Pessoa encontrada
     * } else {
     *     // Email não existe no sistema
     * }
     * }</pre>
     * 
     * @param email {@link String} Endereço de email a ser localizado (case sensitive)
     * 
     * @return {@link Optional}&lt;{@link Pessoa}&gt; Contém a pessoa encontrada se existir,
     *         ou Optional vazio se não houver pessoa com o email especificado
     * 
     * @see Optional#isPresent()
     * @see Optional#orElse(Object)
     * @see Optional#orElseThrow(Supplier)
     */
    Optional<Pessoa> findByEmail(String email);
}
