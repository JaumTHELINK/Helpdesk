package com.turmaa.helpdesk.domain;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.turmaa.helpdesk.domain.dtos.ChamadoDTO;
import com.turmaa.helpdesk.domain.enums.Prioridade;
import com.turmaa.helpdesk.domain.enums.Status;

@Entity
public class Chamado implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate dataAbertura = LocalDate.now();
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate dataFechamento;
	
	@NotNull(message = "O campo PRIORIDADE é obrigatorio")
	private Integer prioridade;
	
	@NotNull(message = "O campo STATUS é obrigatorio")
	private Integer status;
	
	@NotNull(message = "O campo TITULO é obrigatorio")
	private String titulo;
	
	@NotNull(message = "O campo OBSERVAÇÕES é obrigatorio")
	private String observacoes;
	
	@ManyToOne
	@JoinColumn(name = "tecnico_id")
	private Tecnico tecnico;
	
	@ManyToOne
	@JoinColumn(name = "cliente_id")
	private Cliente cliente;
	
	public Chamado() {
		 super();
	     setPrioridade(Prioridade.BAIXA);
	     setStatus(Status.ABERTO);
	}
	
	public Chamado(Integer id, Prioridade prioridade, Status status, String titulo, String observacoes, Tecnico tecnico, Cliente cliente) {
		super();
        this.id = id;
        this.prioridade = (prioridade == null) ? null : prioridade.getCodigo();
        this.status = (status == null) ? null : status.getCodigo();
        this.titulo = titulo;
        this.observacoes = observacoes;
        this.tecnico = tecnico;
        this.cliente = cliente;
	}
	public Chamado(ChamadoDTO dto, Tecnico tecnico, Cliente cliente) {
	    this.id = dto.getId();
	    this.titulo = dto.getTitulo();
	    this.observacoes = dto.getObservacoes();
	    this.prioridade = dto.getPrioridade().getCodigo(); // ou dto.getPrioridade() direto, depende do tipo
	    this.status = dto.getStatus().getCodigo();         // idem acima
	    this.tecnico = tecnico;
	    this.cliente = cliente;
	}

	public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getDataAbertura() {
        return dataAbertura;
    }
    public void setDataAbertura(LocalDate dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public LocalDate getDataFechamento() {
        return dataFechamento;
    }
    public void setDataFechamento(LocalDate dataFechamento) {
        this.dataFechamento = dataFechamento;
    }

    public Prioridade getPrioridade() {
        return Prioridade.toEnum(prioridade);
    }
    public void setPrioridade(Prioridade prioridade) {
        this.prioridade = prioridade.getCodigo();
    }

    public Status getStatus() {
        return Status.toEnum(status);
    }
    public void setStatus(Status status) {
        this.status = status.getCodigo();
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

    public Tecnico getTecnico() {
        return tecnico;
    }
    public void setTecnico(Tecnico tecnico) {
        this.tecnico = tecnico;
    }

    public Cliente getCliente() {
        return cliente;
    }
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
}