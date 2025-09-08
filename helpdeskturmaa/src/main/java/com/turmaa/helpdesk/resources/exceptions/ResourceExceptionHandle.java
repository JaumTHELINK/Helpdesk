package com.turmaa.helpdesk.resources.exceptions;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.turmaa.helpdesk.service.exceptions.DataIntegrityViolationException;
import com.turmaa.helpdesk.service.exceptions.ObjectNotFoundException;

/**
 * Manipulador de exceções global para todos os controladores REST da aplicação.
 * <p>
 * A anotação {@code @ControllerAdvice} indica que esta classe é um
 * componente especializado que pode centralizar o tratamento de exceções
 * lançadas em toda a aplicação, oferecendo uma resposta padronizada para o cliente.
 * </p>
 */
@ControllerAdvice
public class ResourceExceptionHandle {

	/**
	 * Manipula a exceção {@link ObjectNotFoundException}.
	 * <p>
	 * O método, anotado com {@code @ExceptionHandler}, é invocado sempre que
	 * uma exceção do tipo {@code ObjectNotFoundException} for lançada por
	 * qualquer controlador na aplicação. Ele retorna uma resposta HTTP
	 * consistente, com o status "404 Not Found" e um corpo contendo
	 * detalhes sobre o erro.
	 * </p>
	 *
	 * @param ex      A exceção {@link ObjectNotFoundException} que foi capturada.
	 * @param request O objeto {@link HttpServletRequest} que representa a requisição original.
	 * @return Um {@link ResponseEntity} com um objeto {@link StandardError} no corpo
	 * e o status HTTP 404 (Not Found).
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

}