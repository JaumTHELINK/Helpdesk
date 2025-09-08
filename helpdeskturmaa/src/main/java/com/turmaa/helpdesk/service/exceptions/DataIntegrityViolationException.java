package com.turmaa.helpdesk.service.exceptions;

/**
 * Exceção lançada quando há violação de integridade de dados,
 * como tentativa de cadastrar CPF ou e-mail já existente.
 */
public class DataIntegrityViolationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public DataIntegrityViolationException(String message, Throwable cause) {
		super(message, cause);
	}

	public DataIntegrityViolationException(String message) {
		super(message);
	}
}
