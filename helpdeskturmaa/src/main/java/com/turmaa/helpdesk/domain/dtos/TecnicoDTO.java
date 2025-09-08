package com.turmaa.helpdesk.domain.dtos;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.turmaa.helpdesk.domain.Tecnico;

/**
 * DTO usado para transferir dados da entidade Tecnico
 * sem expor diretamente o objeto de domínio.
 */
public class TecnicoDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;

	@NotNull(message = "O campo NOME é obrigatório")
	private String nome;

	@NotNull(message = "O campo CPF é obrigatório")
	private String cpf;

	@NotNull(message = "O campo EMAIL é obrigatório")
	private String email;

	public TecnicoDTO() {
		super();
	}

	// Construtor que recebe a entidade Tecnico e converte para DTO
	public TecnicoDTO(Tecnico obj) {
		this.id = obj.getId();
		this.nome = obj.getNome();
		this.cpf = obj.getCpf();
		this.email = obj.getEmail();
	}

	// Getters e Setters
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
