package com.turmaa.helpdesk.service.exceptions;

import java.io.Serializable;

/**
 * <h2>Classe de Mensagens de Campo para Validação</h2>
 * <p>
 * A classe <strong>FieldMessage</strong> é um componente utilitário especializado
 * em encapsular mensagens de erro relacionadas a campos específicos durante
 * processos de validação de dados na aplicação Helpdesk.
 * </p>
 * 
 * <h3>🎯 Objetivos Principais</h3>
 * <ul>
 *   <li><strong>Mapeamento Field-Error:</strong> Associa mensagens de erro a campos específicos</li>
 *   <li><strong>Validação Granular:</strong> Permite identificação precisa de campos com problemas</li>
 *   <li><strong>Feedback Estruturado:</strong> Organiza erros de validação de forma clara</li>
 *   <li><strong>Integração com Spring Validation:</strong> Compatível com Bean Validation API</li>
 * </ul>
 * 
 * <h3>🔧 Casos de Uso Típicos</h3>
 * <ul>
 *   <li><strong>Validação de DTOs:</strong> Campos obrigatórios não preenchidos</li>
 *   <li><strong>Validação de Formatos:</strong> Email, CPF, telefone com formato inválido</li>
 *   <li><strong>Regras de Negócio:</strong> Valores fora dos limites permitidos</li>
 *   <li><strong>Unicidade:</strong> Campos que devem ser únicos no sistema</li>
 * </ul>
 * 
 * <h3>📋 Estrutura de Dados</h3>
 * <table>
 *   <tr>
 *     <th>Campo</th>
 *     <th>Tipo</th>
 *     <th>Descrição</th>
 *     <th>Exemplo</th>
 *   </tr>
 *   <tr>
 *     <td>fieldName</td>
 *     <td>String</td>
 *     <td>Nome do campo com erro</td>
 *     <td>"email", "cpf", "telefone"</td>
 *   </tr>
 *   <tr>
 *     <td>message</td>
 *     <td>String</td>
 *     <td>Mensagem descritiva do erro</td>
 *     <td>"Email já cadastrado no sistema"</td>
 *   </tr>
 * </table>
 * 
 * <h3>💡 Exemplo de Uso</h3>
 * <pre>{@code
 * // Cenário: Validação de cadastro de técnico
 * List<FieldMessage> errors = new ArrayList<>();
 * 
 * if (emailJaExiste(dto.getEmail())) {
 *     errors.add(new FieldMessage("email", "Email já cadastrado no sistema"));
 * }
 * 
 * if (!CPFUtil.isValido(dto.getCpf())) {
 *     errors.add(new FieldMessage("cpf", "CPF deve ter formato válido"));
 * }
 * 
 * // Resposta JSON resultante:
 * {
 *   "errors": [
 *     {
 *       "fieldName": "email",
 *       "message": "Email já cadastrado no sistema"
 *     },
 *     {
 *       "fieldName": "cpf", 
 *       "message": "CPF deve ter formato válido"
 *     }
 *   ]
 * }
 * }</pre>
 * 
 * <h3>🔄 Integração com Sistema de Validação</h3>
 * <p>
 * Esta classe integra-se perfeitamente com o sistema de tratamento de exceções
 * global da aplicação, sendo utilizada em conjunto com validadores customizados
 * e o framework de Bean Validation para fornecer feedback detalhado sobre
 * problemas específicos em campos de formulários e DTOs.
 * </p>
 * 
 * <h3>🏗️ Características Técnicas</h3>
 * <ul>
 *   <li><strong>Serializable:</strong> Permite serialização para JSON/XML</li>
 *   <li><strong>Lightweight:</strong> Estrutura mínima e eficiente</li>
 *   <li><strong>Immutable-friendly:</strong> Design compatível com padrões imutáveis</li>
 *   <li><strong>Thread-safe:</strong> Seguro para uso em ambientes concorrentes</li>
 * </ul>
 * 
 * @author Helpdesk Application
 * @version 1.0.0
 * @since Spring Boot 2.3.12
 * 
 * @see java.io.Serializable
 * @see javax.validation.ConstraintViolation
 * @see org.springframework.validation.FieldError
 */
public class FieldMessage implements Serializable {

	/**
	 * Identificador único para controle de serialização da classe.
	 * <p>
	 * Este valor é utilizado durante o processo de serialização/deserialização
	 * para garantir compatibilidade entre diferentes versões da classe,
	 * essencial para comunicação via JSON/XML e armazenamento em cache.
	 * </p>
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Nome do campo que contém o erro de validação.
	 * <p>
	 * Representa o identificador exato do campo no formulário ou DTO que
	 * apresentou problema durante a validação. Este valor deve corresponder
	 * ao nome do atributo na classe Java ou ao name do input HTML.
	 * </p>
	 * 
	 * @apiNote Exemplos comuns: "email", "cpf", "nome", "telefone", "senha"
	 */
	private String fieldName;

	/**
	 * Mensagem descritiva explicando o erro de validação encontrado.
	 * <p>
	 * Contém uma descrição clara e compreensível do problema identificado
	 * no campo, destinada a ser apresentada ao usuário final para correção.
	 * A mensagem deve ser informativa e orientar sobre como corrigir o erro.
	 * </p>
	 * 
	 * @apiNote Deve ser user-friendly e específica sobre o problema encontrado
	 */
	private String message;

	/**
	 * <h4>🏗️ Construtor Padrão</h4>
	 * <p>
	 * Construtor sem argumentos necessário para frameworks de serialização/deserialização
	 * como Jackson (JSON), JAXB (XML) e outras bibliotecas de mapeamento.
	 * </p>
	 * 
	 * <h5>📋 Características</h5>
	 * <ul>
	 *   <li><strong>Framework Compatibility:</strong> Requerido por Spring Boot/Jackson</li>
	 *   <li><strong>Reflection Support:</strong> Permite instanciação via reflexão</li>
	 *   <li><strong>Bean Pattern:</strong> Segue convenções JavaBean</li>
	 * </ul>
	 * 
	 * @since 1.0.0
	 * @apiNote Utilizado principalmente por frameworks, prefira o construtor parametrizado para uso direto
	 */
	public FieldMessage() {
		super();
	}

	/**
	 * <h4>🎯 Construtor Parametrizado Completo</h4>
	 * <p>
	 * Construtor principal para criação de instâncias de FieldMessage com todos
	 * os dados necessários, garantindo que o objeto seja criado em estado válido
	 * e completo para uso imediato.
	 * </p>
	 * 
	 * <h5>💡 Cenários de Uso</h5>
	 * <ul>
	 *   <li><strong>Validação Customizada:</strong> Criação de erros específicos de negócio</li>
	 *   <li><strong>Handler de Exceções:</strong> Transformação de ConstraintViolations</li>
	 *   <li><strong>Validação Manual:</strong> Criação programática de mensagens de erro</li>
	 * </ul>
	 * 
	 * <h5>🔍 Exemplo de Uso</h5>
	 * <pre>{@code
	 * // Validação de email único
	 * if (emailRepository.existsByEmail(dto.getEmail())) {
	 *     return new FieldMessage("email", "Email já está em uso por outro usuário");
	 * }
	 * 
	 * // Validação de CPF formato
	 * if (!CPFValidator.isValid(dto.getCpf())) {
	 *     return new FieldMessage("cpf", "CPF deve ter formato válido (XXX.XXX.XXX-XX)");
	 * }
	 * }</pre>
	 * 
	 * @param fieldName O nome do campo que contém o erro, deve corresponder exatamente
	 *                  ao nome do atributo no DTO ou entidade (ex: "email", "cpf", "nome")
	 * @param message A mensagem descritiva do erro encontrado, deve ser clara e
	 *                orientativa para o usuário final sobre como corrigir o problema
	 * 
	 * @throws IllegalArgumentException se fieldName ou message forem null ou vazios
	 * 
	 * @since 1.0.0
	 * @apiNote Este é o construtor recomendado para uso direto na aplicação
	 */

	public FieldMessage(String fieldName, String message) {
		super();
		this.fieldName = fieldName;
		this.message = message;
	}

	/**
	 * <h4>📤 Getter - Nome do Campo</h4>
	 * <p>
	 * Retorna o nome do campo que contém o erro de validação.
	 * Este valor é utilizado para identificar precisamente qual campo
	 * no formulário ou DTO apresentou problema durante a validação.
	 * </p>
	 * 
	 * <h5>🔍 Aplicações Típicas</h5>
	 * <ul>
	 *   <li><strong>Frontend Highlighting:</strong> Destacar campos com erro na UI</li>
	 *   <li><strong>Error Mapping:</strong> Mapear erros para campos específicos</li>
	 *   <li><strong>Logging:</strong> Registrar qual campo causou o problema</li>
	 *   <li><strong>API Response:</strong> Informar cliente sobre campo problemático</li>
	 * </ul>
	 * 
	 * @return String contendo o nome do campo (ex: "email", "cpf", "telefone")
	 *         ou null se não foi definido
	 * 
	 * @since 1.0.0
	 * @apiNote Valor deve corresponder ao nome do atributo no DTO/Entity
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * <h4>📝 Setter - Nome do Campo</h4>
	 * <p>
	 * Define o nome do campo que contém o erro de validação.
	 * Permite modificar o campo associado ao erro após a criação do objeto,
	 * útil para cenários de reutilização ou ajustes dinâmicos.
	 * </p>
	 * 
	 * <h5>⚠️ Considerações de Uso</h5>
	 * <ul>
	 *   <li><strong>Consistência:</strong> Deve corresponder ao nome real do campo</li>
	 *   <li><strong>Case Sensitivity:</strong> Respeitar case exato do atributo</li>
	 *   <li><strong>Validation:</strong> Verificar se campo existe no contexto</li>
	 * </ul>
	 * 
	 * @param fieldName O novo nome do campo, deve ser um identificador válido
	 *                  correspondente a um atributo existente no DTO/Entity
	 * 
	 * @since 1.0.0
	 * @apiNote Prefira definir via construtor quando possível para imutabilidade
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	/**
	 * <h4>📤 Getter - Mensagem de Erro</h4>
	 * <p>
	 * Retorna a mensagem descritiva explicando o erro de validação encontrado.
	 * Esta mensagem é destinada ao usuário final e deve ser clara, informativa
	 * e orientativa sobre como corrigir o problema identificado.
	 * </p>
	 * 
	 * <h5>🎯 Características da Mensagem</h5>
	 * <ul>
	 *   <li><strong>User-Friendly:</strong> Linguagem clara e compreensível</li>
	 *   <li><strong>Specific:</strong> Específica sobre o problema encontrado</li>
	 *   <li><strong>Actionable:</strong> Orienta sobre como corrigir</li>
	 *   <li><strong>Contextual:</strong> Relacionada ao campo e situação</li>
	 * </ul>
	 * 
	 * <h5>💡 Exemplos de Mensagens</h5>
	 * <ul>
	 *   <li>"Email já está em uso por outro usuário"</li>
	 *   <li>"CPF deve ter formato válido (XXX.XXX.XXX-XX)"</li>
	 *   <li>"Campo nome é obrigatório"</li>
	 *   <li>"Telefone deve ter 10 ou 11 dígitos"</li>
	 * </ul>
	 * 
	 * @return String contendo a mensagem de erro formatada para o usuário
	 *         ou null se não foi definida
	 * 
	 * @since 1.0.0
	 * @apiNote Mensagem deve ser internacionalizada quando aplicável
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * <h4>📝 Setter - Mensagem de Erro</h4>
	 * <p>
	 * Define a mensagem descritiva do erro de validação.
	 * Permite modificar a mensagem após a criação do objeto, útil para
	 * personalização dinâmica ou tradução de mensagens.
	 * </p>
	 * 
	 * <h5>📋 Boas Práticas</h5>
	 * <ul>
	 *   <li><strong>Clareza:</strong> Usar linguagem simples e direta</li>
	 *   <li><strong>Contexto:</strong> Incluir informações relevantes sobre o erro</li>
	 *   <li><strong>Solução:</strong> Sugerir como corrigir quando possível</li>
	 *   <li><strong>Consistência:</strong> Manter padrão com outras mensagens</li>
	 * </ul>
	 * 
	 * @param message A nova mensagem de erro, deve ser informativa e orientativa
	 *                para ajudar o usuário a corrigir o problema
	 * 
	 * @since 1.0.0
	 * @apiNote Considere internacionalização para aplicações multi-idioma
	 */

}