package com.turmaa.helpdesk.resources.exceptions;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.turmaa.helpdesk.service.exceptions.DataIntegrityViolationException;
import com.turmaa.helpdesk.service.exceptions.ObjectNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import java.util.List;
import java.util.stream.Collectors;
import com.turmaa.helpdesk.service.exceptions.FieldMessage;

/**
 * <h2>Manipulador Global de Exceções</h2>
 * <p>
 * O <strong>ResourceExceptionHandle</strong> é um componente central responsável
 * por interceptar e tratar todas as exceções lançadas pelos controladores REST
 * da aplicação Helpdesk, fornecendo respostas padronizadas e consistentes aos clientes.
 * </p>
 * 
 * <h3>🎯 Objetivos Principais</h3>
 * <ul>
 *   <li><strong>Centralização do Tratamento de Erros:</strong> Concentra todo o tratamento de exceções em um único local</li>
 *   <li><strong>Padronização de Respostas:</strong> Garante que todas as respostas de erro sigam o mesmo formato</li>
 *   <li><strong>Separação de Responsabilidades:</strong> Remove a necessidade de try/catch nos controladores</li>
 *   <li><strong>Transparência para Cliente:</strong> Fornece informações claras sobre os erros ocorridos</li>
 * </ul>
 * 
 * <h3>🔧 Funcionamento da Anotação @ControllerAdvice</h3>
 * <p>
 * A anotação {@code @ControllerAdvice} transforma esta classe em um componente
 * especial do Spring que atua como um "interceptador global" de exceções:
 * </p>
 * <ul>
 *   <li><strong>Escopo Global:</strong> Aplica-se a todos os controladores da aplicação</li>
 *   <li><strong>Detecção Automática:</strong> O Spring detecta automaticamente esta classe</li>
 *   <li><strong>Processamento Transparente:</strong> Intercepta exceções sem afetar o fluxo normal</li>
 * </ul>
 * 
 * <h3>📋 Estratégia de Tratamento</h3>
 * <table>
 *   <tr>
 *     <th>Tipo de Exceção</th>
 *     <th>Status HTTP</th>
 *     <th>Descrição</th>
 *     <th>Cenário Típico</th>
 *   </tr>
 *   <tr>
 *     <td>ObjectNotFoundException</td>
 *     <td>404 NOT FOUND</td>
 *     <td>Recurso não encontrado</td>
 *     <td>Busca por ID inexistente</td>
 *   </tr>
 *   <tr>
 *     <td>DataIntegrityViolationException</td>
 *     <td>400 BAD REQUEST</td>
 *     <td>Violação de integridade</td>
 *     <td>Exclusão com dependências</td>
 *   </tr>
 * </table>
 * 
 * <h3>💡 Exemplo de Uso</h3>
 * <pre>{@code
 * // No Controlador - sem tratamento explícito
 * @GetMapping("/{id}")
 * public ResponseEntity<TecnicoDTO> findById(@PathVariable Integer id) {
 *     Tecnico tecnico = service.findById(id); // Pode lançar ObjectNotFoundException
 *     return ResponseEntity.ok().body(new TecnicoDTO(tecnico));
 * }
 * 
 * // Resposta automática quando exceção ocorre:
 * {
 *   "timestamp": 1640995200000,
 *   "status": 404,
 *   "error": "Object Not Found",
 *   "message": "Objeto não encontrado! Id: 999",
 *   "path": "/tecnicos/999"
 * }
 * }</pre>
 * 
 * <h3>🏗️ Arquitetura e Benefícios</h3>
 * <ul>
 *   <li><strong>Manutenibilidade:</strong> Mudanças no tratamento afetam toda a aplicação</li>
 *   <li><strong>Consistência:</strong> Formato uniforme de respostas de erro</li>
 *   <li><strong>Debugging:</strong> Informações detalhadas facilitam identificação de problemas</li>
 *   <li><strong>User Experience:</strong> Mensagens de erro claras e informativas</li>
 * </ul>
 * 
 * <h3>📊 Integração com StandardError</h3>
 * <p>
 * Todos os métodos utilizam a classe {@link StandardError} para criar respostas
 * estruturadas contendo timestamp, status HTTP, tipo de erro, mensagem detalhada
 * e caminho da requisição original.
 * </p>
 * 
 * @author Helpdesk Application
 * @version 1.0.0
 * @since Spring Boot 2.3.12
 * 
 * @see ObjectNotFoundException
 * @see DataIntegrityViolationException  
 * @see StandardError
 * @see ControllerAdvice
 * @see ExceptionHandler
 */
@ControllerAdvice
public class ResourceExceptionHandle {

	/**
	 * <h3>🔍 Tratamento de Exceções de Objeto Não Encontrado</h3>
	 * <p>
	 * Este método é automaticamente invocado sempre que uma exceção do tipo
	 * {@link ObjectNotFoundException} for lançada por qualquer controlador
	 * da aplicação, garantindo um tratamento consistente para cenários onde
	 * recursos solicitados não existem no sistema.
	 * </p>
	 * 
	 * <h4>🎯 Cenários de Uso</h4>
	 * <ul>
	 *   <li><strong>Busca por ID:</strong> Quando cliente busca técnico/cliente/chamado com ID inexistente</li>
	 *   <li><strong>Operações de Atualização:</strong> Tentativa de atualizar registro não existente</li>
	 *   <li><strong>Operações de Exclusão:</strong> Tentativa de excluir registro já removido</li>
	 *   <li><strong>Consultas Específicas:</strong> Filtros que não retornam resultados</li>
	 * </ul>
	 * 
	 * <h4>🔄 Fluxo de Processamento</h4>
	 * <ol>
	 *   <li><strong>Captura:</strong> Spring intercepta a exceção automaticamente</li>
	 *   <li><strong>Criação do Erro:</strong> Constrói objeto StandardError com dados contextuais</li>
	 *   <li><strong>Resposta HTTP:</strong> Retorna status 404 com corpo estruturado</li>
	 *   <li><strong>Log do Sistema:</strong> Registra ocorrência para debugging</li>
	 * </ol>
	 * 
	 * <h4>📤 Formato da Resposta</h4>
	 * <pre>{@code
	 * // Exemplo de resposta para GET /tecnicos/999 (ID inexistente)
	 * HTTP 404 Not Found
	 * {
	 *   "timestamp": 1640995200000,
	 *   "status": 404,
	 *   "error": "Object Not Found",
	 *   "message": "Objeto não encontrado! Id: 999",
	 *   "path": "/tecnicos/999"
	 * }
	 * }</pre>
	 * 
	 * <h4>💡 Informações Incluídas</h4>
	 * <ul>
	 *   <li><strong>Timestamp:</strong> Momento exato da ocorrência (milliseconds)</li>
	 *   <li><strong>Status:</strong> Código HTTP 404 (Not Found)</li>
	 *   <li><strong>Error Type:</strong> Classificação do tipo de erro</li>
	 *   <li><strong>Message:</strong> Descrição detalhada da exceção original</li>
	 *   <li><strong>Path:</strong> URI da requisição que causou o erro</li>
	 * </ul>
	 * 
	 * @param ex A exceção {@link ObjectNotFoundException} que foi interceptada
	 *           contendo detalhes sobre o objeto não encontrado e contexto do erro
	 * @param request O objeto {@link HttpServletRequest} representando a requisição
	 *                HTTP original que resultou na exceção, usado para extrair
	 *                informações contextuais como URI e parâmetros
	 * 
	 * @return {@link ResponseEntity} contendo:
	 *         <ul>
	 *           <li><strong>Status:</strong> HTTP 404 (Not Found)</li>
	 *           <li><strong>Body:</strong> Objeto {@link StandardError} com detalhes completos</li>
	 *           <li><strong>Headers:</strong> Headers padrão de resposta JSON</li>
	 *         </ul>
	 * 
	 * @see ObjectNotFoundException Para detalhes sobre quando esta exceção é lançada
	 * @see StandardError Para estrutura completa da resposta de erro
	 * @see HttpStatus#NOT_FOUND Para especificação do código de status HTTP
	 * 
	 * @since 1.0.0
	 * @apiNote Este método é invocado automaticamente pelo Spring Framework
	 *          através do mecanismo de {@code @ExceptionHandler}
	 */
	@ExceptionHandler(ObjectNotFoundException.class)
	public ResponseEntity<StandardError> objectNotFoundException(ObjectNotFoundException ex,
			HttpServletRequest request) {

		// Cria um objeto StandardError para padronizar a resposta de erro.
		// Os parâmetros são: timestamp, status, tipo de erro, mensagem e caminho da requisição.
		StandardError error = new StandardError(
				System.currentTimeMillis(),
				HttpStatus.NOT_FOUND.value(),
				"Object Not Found",
				ex.getMessage(),
				request.getRequestURI());

		// Retorna um ResponseEntity com o status 404 Not Found e o corpo de erro.
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<StandardError> dataIntegrityViolationException(DataIntegrityViolationException ex,
	    HttpServletRequest request) {

	StandardError error = new StandardError(
		System.currentTimeMillis(),
		HttpStatus.BAD_REQUEST.value(),
		"Data Integrity Violation",
		ex.getMessage(),
		request.getRequestURI());

	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> methodArgumentNotValidException(MethodArgumentNotValidException ex,
	    HttpServletRequest request) {

	StandardError error = new StandardError(
		System.currentTimeMillis(),
		HttpStatus.BAD_REQUEST.value(),
		"Validation Error",
		"Erro de validação nos campos",
		request.getRequestURI());

	// Collect field errors into FieldMessage list

	List<FieldMessage> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
		.map(err -> new FieldMessage(err.getField(), err.getDefaultMessage()))
		.collect(Collectors.toList());

	// Attach field errors to message by concatenation (simple approach)
	String detailed = fieldErrors.stream()
		.map(f -> f.getFieldName() + ": " + f.getMessage())
		.collect(Collectors.joining("; "));

	// set more detailed message
	error.setMessage(detailed.isEmpty() ? error.getMessage() : detailed);

	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
	
	/**
	 * <h3>⚠️ Tratamento de Exceções de Violação de Integridade de Dados</h3>
	 * <p>
	 * Este método especializado é automaticamente invocado quando uma exceção do tipo
	 * {@link DataIntegrityViolationException} é lançada, indicando que uma operação
	 * violou regras de integridade referencial ou restrições de dados do sistema.
	 * </p>
	 * 
	 * <h4>🎯 Cenários Típicos de Violação</h4>
	 * <ul>
	 *   <li><strong>Exclusão com Dependências:</strong> Tentativa de excluir técnico com chamados ativos</li>
	 *   <li><strong>Exclusão com Dependências:</strong> Tentativa de excluir cliente com histórico de chamados</li>
	 *   <li><strong>Restrições de Unicidade:</strong> Criação de registros com valores únicos duplicados</li>
	 *   <li><strong>Violação de FK:</strong> Referência a entidades inexistentes em relacionamentos</li>
	 *   <li><strong>Restrições de Domínio:</strong> Valores que violam regras de negócio definidas</li>
	 * </ul>
	 * 
	 * <h4>🔄 Fluxo de Processamento</h4>
	 * <ol>
	 *   <li><strong>Detecção:</strong> Sistema detecta violação de integridade na camada de serviço</li>
	 *   <li><strong>Interceptação:</strong> Spring captura a exceção automaticamente</li>
	 *   <li><strong>Análise:</strong> Extrai informações contextuais da exceção</li>
	 *   <li><strong>Resposta:</strong> Gera resposta HTTP 400 com detalhes do problema</li>
	 * </ol>
	 * 
	 * <h4>📤 Formato da Resposta</h4>
	 * <pre>{@code
	 * // Exemplo: DELETE /tecnicos/1 (técnico com chamados)
	 * HTTP 400 Bad Request
	 * {
	 *   "timestamp": 1640995200000,
	 *   "status": 400,
	 *   "error": "Data Integrity Violation",
	 *   "message": "Técnico possui chamados e não pode ser deletado",
	 *   "path": "/tecnicos/1"
	 * }
	 * }</pre>
	 * 
	 * <h4>🛡️ Benefícios do Tratamento</h4>
	 * <ul>
	 *   <li><strong>Proteção de Dados:</strong> Evita corrupção da base de dados</li>
	 *   <li><strong>Feedback Claro:</strong> Informa exatamente qual restrição foi violada</li>
	 *   <li><strong>Rollback Automático:</strong> Transação é automaticamente desfeita</li>
	 *   <li><strong>Consistência:</strong> Mantém relacionamentos íntegros</li>
	 * </ul>
	 * 
	 * <h4>💡 Status HTTP 400 vs 409</h4>
	 * <p>
	 * Utiliza-se <strong>400 Bad Request</strong> porque a violação geralmente indica
	 * que o cliente enviou dados incorretos ou tentou uma operação inválida no
	 * contexto atual dos dados.
	 * </p>
	 * 
	 * @param ex A exceção {@link DataIntegrityViolationException} interceptada,
	 *           contendo informações detalhadas sobre a violação de integridade
	 *           específica que ocorreu na operação de banco de dados
	 * @param request O objeto {@link HttpServletRequest} da requisição original
	 *                que resultou na violação, utilizado para contexto e logging
	 *                incluindo URI, método HTTP e parâmetros
	 * 
	 * @return {@link ResponseEntity} estruturado contendo:
	 *         <ul>
	 *           <li><strong>Status:</strong> HTTP 400 (Bad Request)</li>
	 *           <li><strong>Body:</strong> {@link StandardError} com detalhes da violação</li>
	 *           <li><strong>Content-Type:</strong> application/json para parsing automático</li>
	 *         </ul>
	 * 
	 * @see DataIntegrityViolationException Para detalhes sobre cenários de violação
	 * @see StandardError Para formato completo da resposta de erro
	 * @see HttpStatus#BAD_REQUEST Para especificação do código de status
	 * 
	 * @since 1.0.0
	 * @apiNote Invocado automaticamente pelo framework Spring através do
	 *          mecanismo declarativo {@code @ExceptionHandler}
	 */

}