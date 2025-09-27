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

/**
 * Entidade que representa um chamado no sistema Helpdesk.
 * 
 * <p>
 * Um chamado é a unidade central do sistema, representando uma solicitação
 * de suporte feita por um cliente e atribuída a um técnico para resolução.
 * </p>
 * 
 * <p>
 * Cada chamado possui informações sobre sua prioridade, status atual,
 * descrição do problema e datas de abertura e fechamento.
 * </p>
 * 
 * <h3>Características principais:</h3>
 * <ul>
 *   <li>Relacionamento Many-to-One com Cliente (quem abriu o chamado)</li>
 *   <li>Relacionamento Many-to-One com Técnico (responsável pelo atendimento)</li>
 *   <li>Sistema de prioridades (BAIXA, MÉDIA, ALTA)</li>
 *   <li>Controle de status (ABERTO, ANDAMENTO, ENCERRADO)</li>
 *   <li>Datas automáticas de abertura e fechamento</li>
 *   <li>Validações de campos obrigatórios</li>
 * </ul>
 * 
 * @author Turma A
 * @version 1.0
 * @since 1.0
 * 
 * @see Cliente
 * @see Tecnico
 * @see Prioridade
 * @see Status
 * @see ChamadoDTO
 */
@Entity
public class Chamado implements Serializable {
	/** Número de versão para serialização. */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Identificador único do chamado no sistema.
	 * <p>
	 * Chave primária gerada automaticamente pelo banco de dados.
	 * </p>
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	/**
	 * Data de abertura do chamado.
	 * <p>
	 * Definida automaticamente como a data atual no momento da criação.
	 * Formatada como dd/MM/yyyy para exibição em JSON.
	 * </p>
	 */
	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate dataAbertura = LocalDate.now();
	
	/**
	 * Data de fechamento do chamado.
	 * <p>
	 * Definida quando o chamado é marcado como ENCERRADO.
	 * Permanece null enquanto o chamado estiver ABERTO ou ANDAMENTO.
	 * Formatada como dd/MM/yyyy para exibição em JSON.
	 * </p>
	 */
	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate dataFechamento;
	
	/**
	 * Código numérico da prioridade do chamado.
	 * <p>
	 * Armazenado como Integer no banco, mas convertido para enum Prioridade
	 * nos getters/setters. Campo obrigatório.
	 * </p>
	 * 
	 * @see Prioridade
	 */
	@NotNull(message = "O campo PRIORIDADE é obrigatorio")
	private Integer prioridade;
	
	/**
	 * Código numérico do status do chamado.
	 * <p>
	 * Armazenado como Integer no banco, mas convertido para enum Status
	 * nos getters/setters. Campo obrigatório.
	 * </p>
	 * 
	 * @see Status
	 */
	@NotNull(message = "O campo STATUS é obrigatorio")
	private Integer status;
	
	/**
	 * Título ou assunto do chamado.
	 * <p>
	 * Resumo breve do problema reportado. Campo obrigatório.
	 * </p>
	 */
	@NotNull(message = "O campo TITULO é obrigatorio")
	private String titulo;
	
	/**
	 * Descrição detalhada do chamado.
	 * <p>
	 * Observações e detalhes sobre o problema reportado pelo cliente
	 * e anotações do técnico. Campo obrigatório.
	 * </p>
	 */
	@NotNull(message = "O campo OBSERVAÇÕES é obrigatorio")
	private String observacoes;
	
	/**
	 * Técnico responsável pelo atendimento do chamado.
	 * <p>
	 * Relacionamento Many-to-One - vários chamados podem ser atribuídos
	 * ao mesmo técnico.
	 * </p>
	 * 
	 * @see Tecnico
	 */
	@ManyToOne
	@JoinColumn(name = "tecnico_id")
	private Tecnico tecnico;
	
	/**
	 * Cliente que abriu o chamado.
	 * <p>
	 * Relacionamento Many-to-One - um cliente pode abrir vários chamados,
	 * mas cada chamado pertence a apenas um cliente.
	 * </p>
	 * 
	 * @see Cliente
	 */
	@ManyToOne
	@JoinColumn(name = "cliente_id")
	private Cliente cliente;
	
	/**
	 * Construtor padrão.
	 * 
	 * <p>
	 * Inicializa um novo chamado com valores padrão:
	 * prioridade BAIXA e status ABERTO.
	 * </p>
	 */
	public Chamado() {
		 super();
	     setPrioridade(Prioridade.BAIXA);
	     setStatus(Status.ABERTO);
	}
	
	/**
	 * Construtor com todos os parâmetros para criação completa de um chamado.
	 * 
	 * @param id Identificador único (pode ser null para geração automática)
	 * @param prioridade Prioridade do chamado (BAIXA, MEDIA, ALTA)
	 * @param status Status atual do chamado (ABERTO, ANDAMENTO, ENCERRADO)
	 * @param titulo Título/assunto do chamado
	 * @param observacoes Descrição detalhada do problema
	 * @param tecnico Técnico responsável pelo atendimento
	 * @param cliente Cliente que abriu o chamado
	 */
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
	
	/**
	 * Construtor que cria um chamado a partir de um ChamadoDTO.
	 * 
	 * <p>
	 * Utilizado principalmente na conversão de dados vindos de requisições
	 * HTTP para entidade de domínio. Recebe também as entidades técnico
	 * e cliente já carregadas do banco.
	 * </p>
	 * 
	 * @param dto Objeto DTO contendo os dados do chamado
	 * @param tecnico Entidade técnico carregada do banco
	 * @param cliente Entidade cliente carregada do banco
	 * @see ChamadoDTO
	 */
	public Chamado(ChamadoDTO dto, Tecnico tecnico, Cliente cliente) {
	    this.id = dto.getId();
	    this.titulo = dto.getTitulo();
	    this.observacoes = dto.getObservacoes();
	    this.prioridade = dto.getPrioridade().getCodigo(); // ou dto.getPrioridade() direto, depende do tipo
	    this.status = dto.getStatus().getCodigo();         // idem acima
	    this.tecnico = tecnico;
	    this.cliente = cliente;
	}

	/**
	 * Obtém o identificador único do chamado.
	 * 
	 * @return ID do chamado ou null se ainda não foi persistido
	 */
	public Integer getId() {
        return id;
    }
    
    /**
     * Define o identificador único do chamado.
     * 
     * @param id Novo ID do chamado
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Obtém a data de abertura do chamado.
     * 
     * @return Data quando o chamado foi criado
     */
    public LocalDate getDataAbertura() {
        return dataAbertura;
    }
    
    /**
     * Define a data de abertura do chamado.
     * 
     * @param dataAbertura Nova data de abertura
     */
    public void setDataAbertura(LocalDate dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    /**
     * Obtém a data de fechamento do chamado.
     * 
     * @return Data de fechamento ou null se ainda está aberto/em andamento
     */
    public LocalDate getDataFechamento() {
        return dataFechamento;
    }
    
    /**
     * Define a data de fechamento do chamado.
     * 
     * <p>
     * Normalmente definida automaticamente quando o status muda para ENCERRADO.
     * </p>
     * 
     * @param dataFechamento Nova data de fechamento
     */
    public void setDataFechamento(LocalDate dataFechamento) {
        this.dataFechamento = dataFechamento;
    }

    /**
     * Obtém a prioridade do chamado.
     * 
     * <p>
     * Converte o código numérico armazenado no banco para o enum Prioridade.
     * </p>
     * 
     * @return Prioridade do chamado (BAIXA, MEDIA, ALTA)
     * @see Prioridade
     */
    public Prioridade getPrioridade() {
        return Prioridade.toEnum(prioridade);
    }
    
    /**
     * Define a prioridade do chamado.
     * 
     * <p>
     * Converte o enum Prioridade para código numérico para armazenamento no banco.
     * </p>
     * 
     * @param prioridade Nova prioridade do chamado
     * @see Prioridade
     */
    public void setPrioridade(Prioridade prioridade) {
        this.prioridade = prioridade.getCodigo();
    }

    /**
     * Obtém o status atual do chamado.
     * 
     * <p>
     * Converte o código numérico armazenado no banco para o enum Status.
     * </p>
     * 
     * @return Status do chamado (ABERTO, ANDAMENTO, ENCERRADO)
     * @see Status
     */
    public Status getStatus() {
        return Status.toEnum(status);
    }
    
    /**
     * Define o status do chamado.
     * 
     * <p>
     * Converte o enum Status para código numérico para armazenamento no banco.
     * Quando definido como ENCERRADO, deveria automaticamente definir a data de fechamento.
     * </p>
     * 
     * @param status Novo status do chamado
     * @see Status
     */
    public void setStatus(Status status) {
        this.status = status.getCodigo();
    }

    /**
     * Obtém o título do chamado.
     * 
     * @return Título/assunto do chamado
     */
    public String getTitulo() {
        return titulo;
    }
    
    /**
     * Define o título do chamado.
     * 
     * @param titulo Novo título do chamado (não pode ser null)
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * Obtém as observações do chamado.
     * 
     * @return Descrição detalhada e observações do chamado
     */
    public String getObservacoes() {
        return observacoes;
    }
    
    /**
     * Define as observações do chamado.
     * 
     * @param observacoes Novas observações do chamado (não pode ser null)
     */
    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    /**
     * Obtém o técnico responsável pelo chamado.
     * 
     * @return Técnico atribuído ao chamado
     * @see Tecnico
     */
    public Tecnico getTecnico() {
        return tecnico;
    }
    
    /**
     * Define o técnico responsável pelo chamado.
     * 
     * @param tecnico Novo técnico responsável pelo atendimento
     * @see Tecnico
     */
    public void setTecnico(Tecnico tecnico) {
        this.tecnico = tecnico;
    }

    /**
     * Obtém o cliente que abriu o chamado.
     * 
     * @return Cliente proprietário do chamado
     * @see Cliente
     */
    public Cliente getCliente() {
        return cliente;
    }
    
    /**
     * Define o cliente que abriu o chamado.
     * 
     * @param cliente Cliente que reportou o problema
     * @see Cliente
     */
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
}