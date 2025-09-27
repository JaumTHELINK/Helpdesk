package com.turmaa.helpdesk.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.turmaa.helpdesk.domain.Chamado;

/**
 * <h1>Repository para Entidade Chamado</h1>
 * <p>
 * Interface de acesso a dados para a entidade Chamado, que representa
 * o core business do sistema de helpdesk. Esta interface estende JpaRepository
 * fornecendo todas as operações CRUD necessárias para gerenciamento completo
 * dos chamados técnicos.
 * </p>
 * 
 * <h2>Funcionalidades Principais:</h2>
 * <ul>
 *   <li><strong>CRUD Completo:</strong> Criar, ler, atualizar e deletar chamados</li>
 *   <li><strong>Relacionamentos:</strong> Acesso automático a técnicos e clientes associados</li>
 *   <li><strong>Ordenação:</strong> Suporte a ordenação por data, prioridade, status</li>
 *   <li><strong>Paginação:</strong> Listagem eficiente de grandes volumes</li>
 * </ul>
 * 
 * <h2>Contexto de Negócio:</h2>
 * <p>
 * Chamados são a entidade central do sistema, representando tickets de suporte
 * que conectam clientes (que reportam problemas) com técnicos (que resolvem).
 * Cada chamado possui ciclo de vida controlado por status e é categorizado
 * por prioridade para otimização do atendimento.
 * </p>
 * 
 * <h2>Relacionamentos Carregados:</h2>
 * <ul>
 *   <li><strong>Técnico Responsável:</strong> ManyToOne com eager loading</li>
 *   <li><strong>Cliente Solicitante:</strong> ManyToOne com eager loading</li>
 *   <li><strong>Enums:</strong> Status e Prioridade convertidos automaticamente</li>
 * </ul>
 * 
 * <h2>Operações Automáticas Disponíveis:</h2>
 * <ul>
 *   <li><strong>save(Chamado):</strong> Criar/atualizar chamado completo</li>
 *   <li><strong>findById(Integer):</strong> Buscar por ID com relacionamentos</li>
 *   <li><strong>findAll():</strong> Listar todos os chamados</li>
 *   <li><strong>findAll(Pageable):</strong> Paginação eficiente</li>
 *   <li><strong>count():</strong> Contar total de chamados</li>
 *   <li><strong>deleteById(Integer):</strong> Remover chamado</li>
 * </ul>
 * 
 * <h2>Consultas Customizadas Potenciais:</h2>
 * <p>
 * Esta interface pode ser estendida no futuro com métodos como:
 * </p>
 * <ul>
 *   <li><strong>findByStatus:</strong> Filtrar por estado do chamado</li>
 *   <li><strong>findByPrioridade:</strong> Filtrar por nível de urgência</li>
 *   <li><strong>findByTecnicoId:</strong> Chamados atribuídos a técnico específico</li>
 *   <li><strong>findByClienteId:</strong> Histórico de chamados do cliente</li>
 *   <li><strong>findByDataAberturaAfter:</strong> Chamados recentes</li>
 * </ul>
 * 
 * <h2>Performance e Otimizações:</h2>
 * <ul>
 *   <li><strong>Índices:</strong> Recomendado em status, prioridade, datas</li>
 *   <li><strong>Fetch Strategy:</strong> Relacionamentos otimizados para evitar N+1</li>
 *   <li><strong>Cache:</strong> Consultas frequentes podem ser cacheadas</li>
 *   <li><strong>Projeções:</strong> DTOs podem ser usados para listagens</li>
 * </ul>
 * 
 * <h2>Exemplo de Uso:</h2>
 * <pre>{@code
 * // Buscar chamado completo com relacionamentos
 * Optional<Chamado> chamado = repository.findById(1);
 * 
 * // Listar todos com paginação
 * Pageable paginacao = PageRequest.of(0, 10);
 * Page<Chamado> chamados = repository.findAll(paginacao);
 * 
 * // Criar novo chamado
 * Chamado novo = new Chamado(dto, tecnico, cliente);
 * repository.save(novo);
 * }</pre>
 * 
 * @author Sistema Helpdesk
 * @version 1.0
 * @since 2024
 * 
 * @see Chamado
 * @see com.turmaa.helpdesk.domain.Tecnico
 * @see com.turmaa.helpdesk.domain.Cliente
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
@Repository
public interface ChamadoRepository extends JpaRepository<Chamado, Integer> {
    /**
     * <h3>Repositório Base para Chamados</h3>
     * <p>
     * Interface que herda todos os métodos CRUD do JpaRepository.
     * Atualmente utiliza apenas os métodos padrão, mas pode ser
     * estendida com consultas customizadas conforme necessidade.
     * </p>
     * 
     * <h4>Métodos Herdados Principais:</h4>
     * <ul>
     *   <li><strong>save(Chamado):</strong> Persistir/atualizar chamado</li>
     *   <li><strong>findById(Integer):</strong> Buscar por ID</li>
     *   <li><strong>findAll():</strong> Listar todos</li>
     *   <li><strong>deleteById(Integer):</strong> Remover por ID</li>
     *   <li><strong>count():</strong> Contar registros</li>
     * </ul>
     * 
     * <h4>Expansão Futura:</h4>
     * <p>
     * Esta interface pode ser facilmente expandida com métodos como:
     * </p>
     * <pre>{@code
     * // Exemplos de consultas que podem ser adicionadas:
     * List<Chamado> findByStatus(Status status);
     * List<Chamado> findByPrioridade(Prioridade prioridade);
     * List<Chamado> findByTecnicoId(Integer tecnicoId);
     * List<Chamado> findByClienteId(Integer clienteId);
     * List<Chamado> findByDataAberturaAfter(LocalDate data);
     * Page<Chamado> findByStatusOrderByPrioridadeDesc(Status status, Pageable pageable);
     * }</pre>
     */
    // Aqui geralmente não precisamos de métodos extras,
    // mas você pode criar consultas personalizadas se necessário.
}
