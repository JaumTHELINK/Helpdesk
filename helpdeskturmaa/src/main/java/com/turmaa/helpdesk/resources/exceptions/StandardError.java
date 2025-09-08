package com.turmaa.helpdesk.resources.exceptions;

import java.io.Serializable;

/**
 * Classe DTO (Data Transfer Object) para representar erros padrão em respostas HTTP.
 * <p>
 * Esta classe é usada para padronizar a estrutura das mensagens de erro
 * retornadas pela API. Isso garante que os clientes (front-end, outros serviços, etc.)
 * sempre recebam respostas de erro em um formato consistente e previsível,
 * facilitando o tratamento de exceções.
 * </p>
 * <p>
 * A implementação da interface {@link java.io.Serializable} permite que
 * as instâncias desta classe possam ser serializadas, o que é um
 * requisito comum para objetos transferidos em rede, como em respostas
 * JSON.
 * </p>
 */
public class StandardError implements Serializable {
	
	/**
	 * Um identificador de versão para a classe serializada. É uma boa prática
	 * definir um valor para garantir a compatibilidade em diferentes versões
	 * da JVM.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * O timestamp (marca de tempo) da ocorrência do erro, em milissegundos.
	 */
	private Long timestamp;
	
	/**
	 * O código de status HTTP do erro (ex: 404 para "Não Encontrado", 400 para "Requisição Inválida").
	 */
	private Integer status;
	
	/**
	 * Uma descrição curta e genérica do tipo de erro (ex: "Not Found").
	 */
	private String error;
	
	/**
	 * Uma mensagem detalhada e amigável sobre o erro, útil para o cliente entender o problema.
	 */
	private String message;
	
	/**
	 * O caminho (URI) da requisição que causou o erro.
	 */
	private String path;
	
	/**
	 * Construtor padrão sem argumentos. Necessário para a desserialização de objetos.
	 */
	public StandardError() {
		super();
	}
	
	/**
	 * Construtor completo para inicializar todos os atributos do erro.
	 *
	 * @param timestamp O timestamp da ocorrência do erro.
	 * @param status    O código de status HTTP.
	 * @param error     A descrição genérica do erro.
	 * @param message   A mensagem detalhada.
	 * @param path      O caminho da requisição.
	 */
	public StandardError(Long timestamp, Integer status, String error, String message, String path) {
		super();
		this.timestamp = timestamp;
		this.status = status;
		this.error = error;
		this.message = message;
		this.path = path;
	}

	// Abaixo estão os métodos Getters e Setters para cada atributo da classe.
	// Eles permitem o acesso e a modificação dos valores dos campos de forma controlada.

	/**
	 * Retorna o timestamp do erro.
	 * @return O timestamp.
	 */
	public Long getTimestamp() {
		return timestamp;
	}
	
	/**
	 * Define o timestamp do erro.
	 * @param timestamp O novo valor do timestamp.
	 */
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	
	/**
	 * Retorna o status HTTP do erro.
	 * @return O status.
	 */
	public Integer getStatus() {
		return status;
	}
	
	/**
	 * Define o status HTTP do erro.
	 * @param status O novo valor do status.
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}
	
	/**
	 * Retorna a descrição genérica do erro.
	 * @return A descrição.
	 */
	public String getError() {
		return error;
	}
	
	/**
	 * Define a descrição genérica do erro.
	 * @param error A nova descrição.
	 */
	public void setError(String error) {
		this.error = error;
	}
	
	/**
	 * Retorna a mensagem detalhada do erro.
	 * @return A mensagem.
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * Define a mensagem detalhada do erro.
	 * @param message A nova mensagem.
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * Retorna o caminho da requisição que gerou o erro.
	 * @return O caminho.
	 */
	public String getPath() {
		return path;
	}
	
	/**
	 * Define o caminho da requisição que gerou o erro.
	 * @param path O novo caminho.
	 */
	public void setPath(String path) {
		this.path = path;
	}
}