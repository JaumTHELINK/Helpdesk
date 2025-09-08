package com.turmaa.helpdesk.domain.dtos;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.turmaa.helpdesk.domain.Chamado;
import com.turmaa.helpdesk.domain.enums.Prioridade;
import com.turmaa.helpdesk.domain.enums.Status;

public class ChamadoDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;

	@NotNull(message = "O campo TITULO é obrigatório")
	private String titulo;

	@NotNull(message = "O campo OBSERVAÇÕES é obrigatório")
	private String observacoes;

	@NotNull(message = "O campo PRIORIDADE é obrigatório")
	private Prioridade prioridade;

	@NotNull(message = "O campo STATUS é obrigatório")
	private Status status;

	private Integer tecnico;  // ID do técnico responsável
	private Integer cliente;  // ID do cliente que abriu o chamado

	public ChamadoDTO() {
		super();
	}

	public ChamadoDTO(Chamado obj) {
		this.id = obj.getId();
		this.titulo = obj.getTitulo();
		this.observacoes = obj.getObservacoes();
		this.prioridade = obj.getPrioridade();
		this.status = obj.getStatus();
		this.tecnico = obj.getTecnico().getId();
		this.cliente = obj.getCliente().getId();
	}

	// Getters e Setters
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	public Prioridade getPrioridade() {
		return prioridade;
	}

	public void setPrioridade(Prioridade prioridade) {
		this.prioridade = prioridade;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Integer getTecnico() {
		return tecnico;
	}

	public void setTecnico(Integer tecnico) {
		this.tecnico = tecnico;
	}

	public Integer getCliente() {
		return cliente;
	}

	public void setCliente(Integer cliente) {
		this.cliente = cliente;
	}
}
