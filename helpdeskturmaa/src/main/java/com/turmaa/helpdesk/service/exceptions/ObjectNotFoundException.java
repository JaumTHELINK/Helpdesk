package com.turmaa.helpdesk.service.exceptions;

/**
 * Exceção personalizada lançada quando um objeto não é encontrado no banco de dados.
 * <p>
 * Esta classe estende {@link java.lang.RuntimeException}, o que significa que
 * é uma exceção não verificada (unchecked exception). Exceções não verificadas
 * não precisam ser declaradas na cláusula {@code throws} dos métodos. Isso
 * simplifica o código e é uma prática comum para erros que não podem ser
 * recuperados em tempo de execução.
 * </p>
 */
public class ObjectNotFoundException extends RuntimeException{

	/**
	 * Um identificador de versão para a classe serializada. É uma boa prática
	 * definir um valor para garantir a compatibilidade em diferentes versões
	 * da JVM.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Construtor da exceção com uma mensagem e uma causa original.
	 * <p>
	 * É útil quando se precisa encapsular uma exceção de nível mais baixo,
	 * como uma exceção de banco de dados, e lançar uma exceção de negócio
	 * mais específica.
	 * </p>
	 *
	 * @param message A mensagem detalhada da exceção.
	 * @param cause   A causa original da exceção (outra {@link java.lang.Throwable}).
	 */
	public ObjectNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Construtor da exceção que aceita apenas uma mensagem.
	 * <p>
	 * Este é o construtor mais comum para indicar que a exceção ocorreu
	 * devido a um motivo específico, como a ausência de um objeto com um
	 * determinado ID.
	 * </p>
	 *
	 * @param message A mensagem detalhada da exceção.
	 */
	public ObjectNotFoundException(String message) {
		super(message);
	}
}