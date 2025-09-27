package com.turmaa.helpdesk.service.exceptions;

/**
 * <h2>Exceção para Violação de Integridade de Dados</h2>
 * <p>
 * Exceção personalizada lançada quando operações de persistência violam
 * constraints de integridade do banco de dados. Representa cenários onde
 * dados enviados conflitam com regras de negócio ou restrições técnicas
 * de unicidade, chaves estrangeiras e validações de domínio.
 * </p>
 * 
 * <h3>🎯 Cenários de Violação de Integridade</h3>
 * <ul>
 *   <li><strong>Unique Constraints:</strong> CPF, email duplicados</li>
 *   <li><strong>Foreign Key Violations:</strong> Referências inexistentes</li>
 *   <li><strong>Check Constraints:</strong> Valores fora de range permitido</li>
 *   <li><strong>Not Null Violations:</strong> Campos obrigatórios em branco</li>
 *   <li><strong>Business Rules:</strong> Regras de negócio específicas</li>
 * </ul>
 * 
 * <h3>🏗️ Design da Exceção</h3>
 * <p>
 * Estende {@link RuntimeException} mantendo consistência com outras exceções
 * de domínio do sistema. Permite encapsular exceções de baixo nível
 * (SQLException, ConstraintViolationException) em contexto de negócio
 * mais claro e user-friendly.
 * </p>
 * 
 * <h3>🔄 Fluxo de Tratamento</h3>
 * <ol>
 *   <li><strong>Database Operation:</strong> Insert/Update que viola constraint</li>
 *   <li><strong>JPA Exception:</strong> Database lança SQLException/ConstraintViolation</li>
 *   <li><strong>Service Layer:</strong> Captura e wraps em DataIntegrityViolation</li>
 *   <li><strong>Exception Handler:</strong> ResourceExceptionHandle intercepta</li>
 *   <li><strong>HTTP Response:</strong> Retorna 400 Bad Request com detalhes</li>
 * </ol>
 * 
 * <h3>📋 Exemplos Típicos no Sistema Helpdesk</h3>
 * <pre>
 * // Em TecnicoService - CPF duplicado
 * try {
 *     tecnicoRepository.save(tecnico);
 * } catch (org.springframework.dao.DataIntegrityViolationException e) {
 *     throw new DataIntegrityViolationException("CPF já cadastrado no sistema!");
 * }
 * 
 * // Em ClienteService - Email duplicado  
 * if (pessoaRepository.findByEmail(email).isPresent()) {
 *     throw new DataIntegrityViolationException("E-mail já cadastrado: " + email);
 * }
 * 
 * // Em ChamadoService - Delete com dependências
 * try {
 *     chamadoRepository.deleteById(id);
 * } catch (org.springframework.dao.DataIntegrityViolationException e) {
 *     throw new DataIntegrityViolationException(
 *         "Chamado possui dependências e não pode ser removido!"
 *     );
 * }
 * </pre>
 * 
 * <h3>🛡️ Constraints Típicas Protegidas</h3>
 * <ul>
 *   <li><strong>CPF:</strong> UNIQUE constraint na tabela pessoa</li>
 *   <li><strong>Email:</strong> UNIQUE constraint na tabela pessoa</li>
 *   <li><strong>Foreign Keys:</strong> Relacionamentos pessoa ↔ chamado</li>
 *   <li><strong>Check Constraints:</strong> Status, prioridades válidas</li>
 * </ul>
 * 
 * <h3>🌐 Integração HTTP</h3>
 * <p>
 * Quando capturada por {@link ResourceExceptionHandle}, esta exceção é
 * automaticamente convertida para resposta HTTP 400 (Bad Request) indicando
 * que os dados enviados não podem ser processados devido a conflitos
 * ou violações de integridade.
 * </p>
 * 
 * <div style="background-color: #f8d7da; border: 1px solid #f5c6cb; border-radius: 4px; padding: 8px; margin: 8px 0;">
 * <strong>⚠️ Cuidados com Exposição de Informações:</strong>
 * <ul>
 *   <li>Não expor detalhes técnicos da constraint SQL</li>
 *   <li>Não revelar estrutura do banco de dados</li>
 *   <li>Manter mensagens user-friendly</li>
 *   <li>Evitar stacktraces em responses de produção</li>
 * </ul>
 * </div>
 * 
 * <div style="background-color: #d4edda; border: 1px solid #c3e6cb; border-radius: 4px; padding: 8px; margin: 8px 0;">
 * <strong>✅ Vantagens do Pattern:</strong>
 * <ul>
 *   <li>Abstração de detalhes técnicos do banco</li>
 *   <li>Mensagens de erro contextualizadas para o negócio</li>
 *   <li>Tratamento centralizado via exception handler</li>
 *   <li>Facilita testes unitários com mocks</li>
 * </ul>
 * </div>
 * 
 * <div style="background-color: #d1ecf1; border: 1px solid #b8daff; border-radius: 4px; padding: 8px; margin: 8px 0;">
 * <strong>💡 Exception Translation:</strong>
 * Implementa o padrão Exception Translation onde exceções de
 * infraestrutura (SQLException) são traduzidas para exceções
 * de domínio com semântica de negócio mais clara.
 * </div>
 * 
 * @author Sistema Helpdesk
 * @since 1.0.0
 * @see RuntimeException
 * @see org.springframework.dao.DataIntegrityViolationException
 * @see ResourceExceptionHandle
 */
public class DataIntegrityViolationException extends RuntimeException {
	
	/**
	 * <h3>Serial Version UID</h3>
	 * <p>
	 * Identificador único para controle de versão durante serialização.
	 * Garante compatibilidade entre diferentes versões da aplicação quando
	 * exceções são serializadas para logs, cache ou sistemas distribuídos.
	 * </p>
	 * 
	 * @see java.io.Serializable
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * <h3>🔗 Construtor com Exception Wrapping</h3>
	 * <p>
	 * Construtor usado para encapsular exceções de baixo nível (principalmente
	 * de JPA/Hibernate) mantendo a stack trace original para debugging
	 * detalhado. Fundamental para análise de root cause em produção.
	 * </p>
	 * 
	 * <h4>🎯 Casos de Uso Típicos:</h4>
	 * <ul>
	 *   <li><strong>SQLException:</strong> Constraint violations do JDBC</li>
	 *   <li><strong>ConstraintViolationException:</strong> Bean Validation violations</li>
	 *   <li><strong>Spring DataIntegrityViolation:</strong> Exceções do Spring Data</li>
	 *   <li><strong>Hibernate Exceptions:</strong> Persistence layer errors</li>
	 * </ul>
	 * 
	 * <h4>📋 Exemplo de Wrapping:</h4>
	 * <pre>
	 * try {
	 *     pessoaRepository.save(pessoa);
	 * } catch (org.springframework.dao.DataIntegrityViolationException e) {
	 *     // Analisa a causa raiz para personalizar mensagem
	 *     if (e.getCause().getMessage().contains("cpf")) {
	 *         throw new DataIntegrityViolationException("CPF já cadastrado!", e);
	 *     } else if (e.getCause().getMessage().contains("email")) {
	 *         throw new DataIntegrityViolationException("E-mail já cadastrado!", e);  
	 *     }
	 *     throw new DataIntegrityViolationException("Erro de integridade dos dados!", e);
	 * }
	 * </pre>
	 * 
	 * <div style="background-color: #d1ecf1; border: 1px solid #b8daff; border-radius: 4px; padding: 8px; margin: 8px 0;">
	 * <strong>💡 Exception Chain Benefits:</strong>
	 * Preserva contexto completo do erro incluindo stack trace original,
	 * facilitando debugging em produção sem expor detalhes técnicos
	 * nas respostas HTTP para clientes.
	 * </div>
	 * 
	 * @param message {@link String} Mensagem user-friendly explicando
	 *        a violação de integridade em termos de negócio
	 * @param cause {@link Throwable} Exceção original (SQLException, etc.)
	 *        que causou a violação de integridade
	 * 
	 * @see RuntimeException#RuntimeException(String, Throwable)
	 */
	public DataIntegrityViolationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * <h3>💬 Construtor com Mensagem Direta</h3>
	 * <p>
	 * Construtor para cenários onde a violação de integridade é detectada
	 * pela lógica de negócio antes da tentativa de persistência, permitindo
	 * mensagens de erro mais específicas e user-friendly.
	 * </p>
	 * 
	 * <h4>🎯 Cenários de Uso:</h4>
	 * <ul>
	 *   <li><strong>Validation Preview:</strong> Verificação antes de save()</li>
	 *   <li><strong>Business Rules:</strong> Regras específicas do domínio</li>
	 *   <li><strong>Custom Constraints:</strong> Validações complexas</li>
	 *   <li><strong>User Feedback:</strong> Mensagens específicas e claras</li>
	 * </ul>
	 * 
	 * <h4>📋 Exemplos de Mensagens Recomendadas:</h4>
	 * <pre>
	 * // Para CPF duplicado
	 * "CPF já cadastrado no sistema: 123.456.789-00"
	 * 
	 * // Para email duplicado
	 * "E-mail já cadastrado: usuario@example.com"
	 * 
	 * // Para relacionamentos
	 * "Não é possível remover técnico com chamados ativos"
	 * 
	 * // Para regras de negócio
	 * "Cliente não pode ter mais de 10 chamados abertos simultaneamente"
	 * </pre>
	 * 
	 * <h4>🌐 HTTP Response Integration:</h4>
	 * <p>
	 * A mensagem será incluída na resposta HTTP 400 Bad Request, fornecendo
	 * feedback direto para aplicações cliente sobre qual constraint
	 * foi violada e como corrigir o problema.
	 * </p>
	 * 
	 * <div style="background-color: #d4edda; border: 1px solid #c3e6cb; border-radius: 4px; padding: 8px; margin: 8px 0;">
	 * <strong>✅ Boas Práticas para Mensagens:</strong>
	 * <ul>
	 *   <li>Especificar qual campo/valor causou o conflito</li>
	 *   <li>Sugerir ação corretiva quando possível</li>
	 *   <li>Manter linguagem consistente com UX da aplicação</li>
	 *   <li>Evitar jargão técnico de banco de dados</li>
	 * </ul>
	 * </div>
	 * 
	 * @param message {@link String} Mensagem clara e específica sobre
	 *        a violação de integridade, adequada para apresentação
	 *        ao usuário final através da interface da aplicação
	 * 
	 * @see RuntimeException#RuntimeException(String)
	 */
	public DataIntegrityViolationException(String message) {
		super(message);
	}
}
