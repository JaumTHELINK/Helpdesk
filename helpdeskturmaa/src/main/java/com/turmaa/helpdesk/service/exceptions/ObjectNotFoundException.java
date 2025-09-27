package com.turmaa.helpdesk.service.exceptions;

/**
 * <h2>Exceção para Objetos Não Encontrados</h2>
 * <p>
 * Exceção personalizada de domínio lançada quando operações de busca
 * não conseguem localizar entidades no banco de dados. Representa
 * cenários where recursos solicitados não existem ou não são acessíveis
 * para o contexto atual da operação.
 * </p>
 * 
 * <h3>🎯 Casos de Uso Típicos</h3>
 * <ul>
 *   <li><strong>GET by ID:</strong> Busca de entidade por ID inexistente</li>
 *   <li><strong>Business Operations:</strong> Operações que dependem de recursos específicos</li>
 *   <li><strong>Relationship Queries:</strong> Busca de relacionamentos inexistentes</li>
 *   <li><strong>Authorization Context:</strong> Recursos fora do escopo do usuário</li>
 * </ul>
 * 
 * <h3>🏗️ Design de Exception</h3>
 * <p>
 * Estende {@link RuntimeException} seguindo o padrão de exceções não verificadas
 * (unchecked exceptions) do Spring Framework. Esta abordagem simplifica o código
 * cliente e é adequada para erros que representam falhas de negócio irrecuperáveis
 * em tempo de execução.
 * </p>
 * 
 * <h3>🔄 Fluxo de Tratamento</h3>
 * <ol>
 *   <li><strong>Service Layer:</strong> Detecta objeto não encontrado</li>
 *   <li><strong>Exception Throw:</strong> Lança ObjectNotFoundException</li>
 *   <li><strong>Controller Advice:</strong> {@link ResourceExceptionHandle} intercepta</li>
 *   <li><strong>HTTP Response:</strong> Converte para 404 Not Found</li>
 *   <li><strong>Client Response:</strong> JSON padronizado de erro</li>
 * </ol>
 * 
 * <h3>📋 Exemplos de Uso no Sistema</h3>
 * <pre>
 * // Em TecnicoService
 * public TecnicoDTO findById(Integer id) {
 *     Tecnico tecnico = repository.findById(id)
 *         .orElseThrow(() -> new ObjectNotFoundException("Técnico não encontrado! Id: " + id));
 *     return new TecnicoDTO(tecnico);
 * }
 * 
 * // Em ChamadoService  
 * public Chamado findById(Integer id) {
 *     return repository.findById(id)
 *         .orElseThrow(() -> new ObjectNotFoundException(
 *             "Chamado não encontrado! Id: " + id + ", Tipo: " + Chamado.class.getName()));
 * }
 * </pre>
 * 
 * <h3>🌐 Integração HTTP</h3>
 * <p>
 * Quando capturada por {@link ResourceExceptionHandle}, esta exceção é
 * automaticamente convertida para resposta HTTP 404 (Not Found) com corpo JSON
 * padronizado contendo timestamp, status, error e path da requisição.
 * </p>
 * 
 * <div style="background-color: #d4edda; border: 1px solid #c3e6cb; border-radius: 4px; padding: 8px; margin: 8px 0;">
 * <strong>✅ Vantagens do Design:</strong>
 * <ul>
 *   <li>Semântica clara e específica para domain</li>
 *   <li>Integração automática com tratamento HTTP</li>
 *   <li>Código cliente mais limpo (sem try-catch obrigatório)</li>
 *   <li>Mensagens de erro personalizáveis e detalhadas</li>
 * </ul>
 * </div>
 * 
 * <div style="background-color: #d1ecf1; border: 1px solid #b8daff; border-radius: 4px; padding: 8px; margin: 8px 0;">
 * <strong>💡 Pattern Exception Hierarchy:</strong>
 * Faz parte de hierarquia estruturada de exceções de domínio,
 * permitindo tratamento granular por tipo de erro e facilitando
 * manutenção e evolução do sistema de error handling.
 * </div>
 * 
 * <div style="background-color: #fff3cd; border: 1px solid #ffeaa7; border-radius: 4px; padding: 8px; margin: 8px 0;">
 * <strong>⚠️ Melhores Práticas:</strong>
 * <ul>
 *   <li>Inclua IDs e tipos na mensagem para debug</li>
 *   <li>Não exponha informações sensíveis do sistema</li>
 *   <li>Mantenha mensagens user-friendly quando possível</li>
 *   <li>Use cause chain quando wrapping outras exceptions</li>
 * </ul>
 * </div>
 * 
 * @author Sistema Helpdesk
 * @since 1.0.0
 * @see RuntimeException
 * @see ResourceExceptionHandle
 * @see java.util.Optional#orElseThrow()
 */
public class ObjectNotFoundException extends RuntimeException{

	/**
	 * <h3>Serial Version UID</h3>
	 * <p>
	 * Identificador único para controle de versão durante serialização.
	 * Essencial para garantir compatibilidade quando exceções são serializadas
	 * (logs estruturados, sistemas distribuídos, cache de exceptions, etc.).
	 * </p>
	 * 
	 * <h4>⚠️ Importância:</h4>
	 * <p>
	 * Garante que diferentes versões da aplicação possam desserializar
	 * objetos de exceção criados por versões anteriores, evitando
	 * InvalidClassException durante deserialização.
	 * </p>
	 * 
	 * @see java.io.Serializable
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * <h3>🔗 Construtor com Causa Raiz</h3>
	 * <p>
	 * Construtor para cenários onde a ObjectNotFoundException precisa
	 * encapsular uma exceção de nível mais baixo, preservando a stack trace
	 * completa e contexto original do erro para debugging avançado.
	 * </p>
	 * 
	 * <h4>🎯 Casos de Uso:</h4>
	 * <ul>
	 *   <li><strong>Database Exceptions:</strong> SQLException, JPA exceptions</li>
	 *   <li><strong>Network Errors:</strong> Timeouts, connection issues</li>
	 *   <li><strong>External Service:</strong> API calls que falharam</li>
	 *   <li><strong>Parsing Errors:</strong> JSON, XML parsing failures</li>
	 * </ul>
	 * 
	 * <h4>📋 Exemplo de Uso:</h4>
	 * <pre>
	 * try {
	 *     return externalService.findUser(id);
	 * } catch (ServiceUnavailableException e) {
	 *     throw new ObjectNotFoundException(
	 *         "Usuário não pode ser encontrado devido a falha no serviço externo. ID: " + id, 
	 *         e
	 *     );
	 * }
	 * </pre>
	 * 
	 * <h4>🔍 Vantagens do Cause Chain:</h4>
	 * <ul>
	 *   <li><strong>Full Stack Trace:</strong> Preserva origem completa do erro</li>
	 *   <li><strong>Debug Context:</strong> Facilita troubleshooting em produção</li>
	 *   <li><strong>Root Cause Analysis:</strong> Permite análise de causa raiz</li>
	 *   <li><strong>Monitoring:</strong> Logs estruturados com contexto completo</li>
	 * </ul>
	 * 
	 * <div style="background-color: #d1ecf1; border: 1px solid #b8daff; border-radius: 4px; padding: 8px; margin: 8px 0;">
	 * <strong>💡 Exception Chaining:</strong>
	 * Implementa o padrão Exception Chaining recomendado pelo Java,
	 * onde exceptions de alto nível preservam contexto de exceptions
	 * de baixo nível através da cause chain.
	 * </div>
	 * 
	 * @param message {@link String} Mensagem detalhada descrevendo o contexto
	 *        específico onde o objeto não foi encontrado
	 * @param cause {@link Throwable} Exceção original que causou esta falha,
	 *        preservada para debugging e análise de root cause
	 * 
	 * @see RuntimeException#RuntimeException(String, Throwable)
	 * @see Throwable#getCause()
	 */
	public ObjectNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * <h3>💬 Construtor com Mensagem Simples</h3>
	 * <p>
	 * Construtor principal e mais utilizado para cenários onde a ausência
	 * do objeto é detectada diretamente pela lógica de negócio, sem
	 * exceção subjacente. Ideal para casos de Optional.orElseThrow()
	 * e validações diretas de existência.
	 * </p>
	 * 
	 * <h4>🎯 Cenários Típicos:</h4>
	 * <ul>
	 *   <li><strong>Repository Empty:</strong> Optional vazio de findById()</li>
	 *   <li><strong>Business Logic:</strong> Validações de existência</li>
	 *   <li><strong>Authorization:</strong> Recurso não acessível ao usuário</li>
	 *   <li><strong>Filtered Queries:</strong> Sem resultados após filtros</li>
	 * </ul>
	 * 
	 * <h4>📋 Padrões de Mensagem Recomendados:</h4>
	 * <pre>
	 * // Com ID e tipo
	 * "Técnico não encontrado! Id: 123"
	 * 
	 * // Com ID, tipo e contexto
	 * "Chamado não encontrado! Id: 456, Tipo: Chamado"
	 * 
	 * // Com contexto de negócio
	 * "Cliente não encontrado ou não possui permissão de acesso. Id: 789"
	 * 
	 * // Para relacionamentos
	 * "Técnico responsável não encontrado para o chamado Id: 101"
	 * </pre>
	 * 
	 * <h4>💡 Exemplos de Uso Comum:</h4>
	 * <pre>
	 * // Pattern com Optional
	 * Cliente cliente = clienteRepository.findById(id)
	 *     .orElseThrow(() -> new ObjectNotFoundException("Cliente não encontrado! Id: " + id));
	 * 
	 * // Validation pattern
	 * if (!chamadoRepository.existsById(chamadoId)) {
	 *     throw new ObjectNotFoundException("Chamado não encontrado! Id: " + chamadoId);
	 * }
	 * 
	 * // Authorization context
	 * if (tecnico.getCliente().getId() != currentUserId) {
	 *     throw new ObjectNotFoundException("Chamado não encontrado ou sem permissão");
	 * }
	 * </pre>
	 * 
	 * <h4>🌐 Integração com HTTP Response:</h4>
	 * <p>
	 * A mensagem fornecida será incluída na resposta JSON HTTP 404, permitindo
	 * que aplicações cliente apresentem feedback específico e útil aos usuários
	 * finais sobre o recurso que não foi localizado.
	 * </p>
	 * 
	 * <div style="background-color: #d4edda; border: 1px solid #c3e6cb; border-radius: 4px; padding: 8px; margin: 8px 0;">
	 * <strong>✅ Boas Práticas para Mensagens:</strong>
	 * <ul>
	 *   <li>Incluir identificadores únicos (IDs) para debugging</li>
	 *   <li>Especificar o tipo de entidade que não foi encontrada</li>
	 *   <li>Manter linguagem consistente em todo o sistema</li>
	 *   <li>Evitar informações sensíveis ou técnicas demais</li>
	 * </ul>
	 * </div>
	 * 
	 * @param message {@link String} Mensagem descritiva que será apresentada
	 *        ao cliente e incluída em logs, deve ser clara e informativa
	 *        sobre qual objeto não foi encontrado e contexto da busca
	 * 
	 * @see RuntimeException#RuntimeException(String)
	 * @see Optional#orElseThrow(Supplier)
	 */
	public ObjectNotFoundException(String message) {
		super(message);
	}
}