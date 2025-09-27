package com.turmaa.helpdesk.domain.dtos;

import java.io.Serializable;
import java.time.LocalDate;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.turmaa.helpdesk.domain.Chamado;
import com.turmaa.helpdesk.domain.enums.Prioridade;
import com.turmaa.helpdesk.domain.enums.Status;

/**
 * <h1>Data Transfer Object (DTO) para Chamados</h1>
 * <p>
 * Classe responsável pela transferência segura de dados de chamados técnicos
 * entre as diferentes camadas da aplicação. Esta classe implementa o padrão
 * DTO para garantir o desacoplamento entre a camada de persistência e a
 * camada de apresentação, evitando problemas de serialização e exposição
 * desnecessária de dados sensíveis.
 * </p>
 * 
 * <h2>Principais Características:</h2>
 * <ul>
 *   <li><strong>Serialização:</strong> Implementa Serializable para transferência via rede</li>
 *   <li><strong>Validação:</strong> Utiliza Bean Validation para garantir integridade dos dados</li>
 *   <li><strong>Formatação JSON:</strong> Configuração específica para datas no formato brasileiro</li>
 *   <li><strong>Referências:</strong> Utiliza IDs em vez de objetos completos para relacionamentos</li>
 *   <li><strong>Conversão Automática:</strong> Construtor que converte entidade para DTO</li>
 * </ul>
 * 
 * <h2>Validações Implementadas:</h2>
 * <ul>
 *   <li><strong>Título:</strong> Campo obrigatório que descreve brevemente o problema</li>
 *   <li><strong>Observações:</strong> Detalhamento obrigatório do chamado</li>
 *   <li><strong>Prioridade:</strong> Classificação obrigatória da urgência</li>
 *   <li><strong>Status:</strong> Estado obrigatório do chamado</li>
 * </ul>
 * 
 * <h2>Formato de Datas:</h2>
 * <p>
 * As datas são formatadas no padrão brasileiro (dd/MM/yyyy) tanto para
 * serialização JSON quanto para apresentação nas interfaces.
 * </p>
 * 
 * @author Sistema Helpdesk
 * @version 1.0
 * @since 2024
 * 
 * @see Chamado
 * @see Status
 * @see Prioridade
 */
public class ChamadoDTO implements Serializable {
	/**
	 * <h3>Serial Version UID</h3>
	 * <p>
	 * Identificador único para controle de versão durante a serialização.
	 * Garante compatibilidade entre diferentes versões da classe.
	 * </p>
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * <h3>Identificador Único</h3>
	 * <p>
	 * Chave primária do chamado. Este campo é automaticamente gerado
	 * pelo banco de dados durante a criação de novos chamados.
	 * </p>
	 */
	private Integer id;
	
	/**
	 * <h3>Data de Abertura</h3>
	 * <p>
	 * Data em que o chamado foi criado no sistema. Por padrão, é definida
	 * automaticamente como a data atual. Formato de apresentação: dd/MM/yyyy.
	 * </p>
	 * 
	 * <h4>Comportamento:</h4>
	 * <ul>
	 *   <li>Inicializada automaticamente com LocalDate.now()</li>
	 *   <li>Mantém data de criação original durante atualizações</li>
	 *   <li>Utilizada para relatórios e métricas de tempo</li>
	 * </ul>
	 */
	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate dataAbertura = LocalDate.now();
	
	/**
	 * <h3>Data de Fechamento</h3>
	 * <p>
	 * Data em que o chamado foi finalizado/encerrado. Este campo permanece
	 * null enquanto o chamado estiver em aberto ou em andamento.
	 * Formato de apresentação: dd/MM/yyyy.
	 * </p>
	 * 
	 * <h4>Estados Possíveis:</h4>
	 * <ul>
	 *   <li><strong>null:</strong> Chamado ainda não foi encerrado</li>
	 *   <li><strong>Data válida:</strong> Chamado foi finalizado nesta data</li>
	 * </ul>
	 */
	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate dataFechamento;

	/**
	 * <h3>Título do Chamado</h3>
	 * <p>
	 * Descrição resumida e objetiva do problema ou solicitação reportada
	 * pelo cliente. Este campo é obrigatório e deve fornecer uma visão
	 * geral clara do que está sendo solicitado.
	 * </p>
	 * 
	 * <h4>Boas Práticas:</h4>
	 * <ul>
	 *   <li>Máximo de 100-150 caracteres para boa legibilidade</li>
	 *   <li>Deve ser específico e descritivo</li>
	 *   <li>Evitar abreviações desnecessárias</li>
	 * </ul>
	 */
	@NotNull(message = "O campo TITULO é obrigatório")
	private String titulo;

	/**
	 * <h3>Observações Detalhadas</h3>
	 * <p>
	 * Campo destinado ao detalhamento completo do problema, incluindo
	 * contexto, passos para reprodução, informações do ambiente e
	 * qualquer observação relevante para resolução do chamado.
	 * </p>
	 * 
	 * <h4>Deve Conter:</h4>
	 * <ul>
	 *   <li>Descrição detalhada do problema</li>
	 *   <li>Passos para reproduzir (se aplicável)</li>
	 *   <li>Informações do ambiente/sistema</li>
	 *   <li>Mensagens de erro (se houver)</li>
	 *   <li>Comportamento esperado vs atual</li>
	 * </ul>
	 */
	@NotNull(message = "O campo OBSERVAÇÕES é obrigatório")
	private String observacoes;

	/**
	 * <h3>Nível de Prioridade</h3>
	 * <p>
	 * Classificação da urgência e impacto do chamado, utilizada para
	 * definir a ordem de atendimento e alocação de recursos. Este campo
	 * é obrigatório e deve refletir a criticidade real da solicitação.
	 * </p>
	 * 
	 * <h4>Níveis Disponíveis:</h4>
	 * <ul>
	 *   <li><strong>BAIXA:</strong> Problemas menores, sem impacto significativo</li>
	 *   <li><strong>MÉDIA:</strong> Problemas que afetam algumas funcionalidades</li>
	 *   <li><strong>ALTA:</strong> Problemas críticos que impedem o trabalho</li>
	 * </ul>
	 * 
	 * @see Prioridade
	 */
	@NotNull(message = "O campo PRIORIDADE é obrigatório")
	private Prioridade prioridade;

	/**
	 * <h3>Status Atual</h3>
	 * <p>
	 * Estado atual do chamado no fluxo de atendimento. Este campo é
	 * obrigatório e controla o ciclo de vida do chamado, desde a
	 * abertura até o encerramento final.
	 * </p>
	 * 
	 * <h4>Estados Possíveis:</h4>
	 * <ul>
	 *   <li><strong>ABERTO:</strong> Chamado criado, aguardando atendimento</li>
	 *   <li><strong>ANDAMENTO:</strong> Sendo trabalhado pelo técnico</li>
	 *   <li><strong>ENCERRADO:</strong> Problema resolvido e chamado finalizado</li>
	 * </ul>
	 * 
	 * @see Status
	 */
	@NotNull(message = "O campo STATUS é obrigatório")
	private Status status;

	/**
	 * <h3>ID do Técnico Responsável</h3>
	 * <p>
	 * Identificador único do técnico que foi designado para resolver
	 * este chamado. Utiliza apenas o ID em vez do objeto completo para
	 * evitar problemas de serialização e melhorar performance.
	 * </p>
	 * 
	 * <h4>Características:</h4>
	 * <ul>
	 *   <li>Referência ao técnico sem carregar dados desnecessários</li>
	 *   <li>Permite reatribuição fácil do chamado</li>
	 *   <li>Usado para validação de existência do técnico</li>
	 * </ul>
	 */
	private Integer tecnico;  // ID do técnico responsável
	
	/**
	 * <h3>ID do Cliente Solicitante</h3>
	 * <p>
	 * Identificador único do cliente que abriu o chamado. Utiliza apenas
	 * o ID em vez do objeto completo para otimização de transferência
	 * de dados e evitar referências circulares na serialização JSON.
	 * </p>
	 * 
	 * <h4>Características:</h4>
	 * <ul>
	 *   <li>Referência ao cliente sem dados pessoais completos</li>
	 *   <li>Permite identificação rápida do solicitante</li>
	 *   <li>Usado para validação de existência do cliente</li>
	 * </ul>
	 */
	private Integer cliente;  // ID do cliente que abriu o chamado

	/**
	 * <h3>Construtor Padrão</h3>
	 * <p>
	 * Construtor vazio necessário para frameworks como Spring Boot, Jackson
	 * e outras bibliotecas que precisam instanciar objetos via reflexão.
	 * </p>
	 * 
	 * <h4>Uso Típico:</h4>
	 * <ul>
	 *   <li>Deserialização JSON automática</li>
	 *   <li>Binding de formulários web</li>
	 *   <li>Frameworks de mapeamento objeto-relacional</li>
	 * </ul>
	 */
	public ChamadoDTO() {
		super();
	}

	/**
	 * <h3>Construtor de Conversão</h3>
	 * <p>
	 * Cria uma instância de ChamadoDTO a partir de uma entidade Chamado completa.
	 * Este construtor implementa o padrão de conversão Entity → DTO, extraindo
	 * apenas as informações necessárias para transferência de dados.
	 * </p>
	 * 
	 * <h4>Processo de Conversão:</h4>
	 * <ol>
	 *   <li><strong>Dados Básicos:</strong> ID, título, observações</li>
	 *   <li><strong>Enums:</strong> Prioridade e status (cópia direta)</li>
	 *   <li><strong>Relacionamentos:</strong> Extrai apenas IDs dos objetos relacionados</li>
	 *   <li><strong>Datas:</strong> Copia datas de abertura e fechamento</li>
	 * </ol>
	 * 
	 * <h4>Vantagens desta Conversão:</h4>
	 * <ul>
	 *   <li><strong>Performance:</strong> Evita carregamento lazy desnecessário</li>
	 *   <li><strong>Serialização:</strong> Previne loops infinitos e referências circulares</li>
	 *   <li><strong>Segurança:</strong> Expõe apenas dados necessários</li>
	 *   <li><strong>Flexibilidade:</strong> Permite customização da apresentação</li>
	 * </ul>
	 * 
	 * <h4>Tratamento de Relacionamentos:</h4>
	 * <p>
	 * Os relacionamentos ManyToOne com Tecnico e Cliente são convertidos para
	 * simples referências numéricas (IDs), evitando problemas de:
	 * </p>
	 * <ul>
	 *   <li>Lazy loading exceptions</li>
	 *   <li>Serialização de objetos complexos</li>
	 *   <li>Transferência desnecessária de dados</li>
	 * </ul>
	 * 
	 * @param obj {@link Chamado} Entidade chamado completa com todos os
	 *            relacionamentos carregados para ser convertida em DTO
	 * 
	 * @throws NullPointerException Se o objeto chamado for null ou se
	 *                              os relacionamentos técnico/cliente não estiverem carregados
	 * 
	 * @see Chamado
	 */
	public ChamadoDTO(Chamado obj) {
		this.id = obj.getId();
		this.titulo = obj.getTitulo();
		this.observacoes = obj.getObservacoes();
		this.prioridade = obj.getPrioridade();
		this.status = obj.getStatus();
		this.tecnico = obj.getTecnico().getId();
		this.cliente = obj.getCliente().getId();
		this.dataAbertura = obj.getDataAbertura();
		this.dataFechamento = obj.getDataFechamento();

	}

	// ===========================================
	// GETTERS E SETTERS
	// ===========================================
	
	/**
	 * <h3>Obter ID do Chamado</h3>
	 * <p>Retorna o identificador único do chamado.</p>
	 * 
	 * @return {@link Integer} ID do chamado ou null se ainda não persistido
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * <h3>Definir ID do Chamado</h3>
	 * <p>Define o identificador único do chamado. Geralmente usado durante atualizações.</p>
	 * 
	 * @param id {@link Integer} Novo ID do chamado
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * <h3>Obter Título</h3>
	 * <p>Retorna o título/descrição resumida do chamado.</p>
	 * 
	 * @return {@link String} Título do chamado
	 */
	public String getTitulo() {
		return titulo;
	}

	/**
	 * <h3>Definir Título</h3>
	 * <p>Define o título/descrição resumida do chamado.</p>
	 * 
	 * @param titulo {@link String} Novo título do chamado (obrigatório)
	 */
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	/**
	 * <h3>Obter Observações</h3>
	 * <p>Retorna as observações detalhadas sobre o chamado.</p>
	 * 
	 * @return {@link String} Observações detalhadas
	 */
	public String getObservacoes() {
		return observacoes;
	}

	/**
	 * <h3>Definir Observações</h3>
	 * <p>Define as observações detalhadas sobre o chamado.</p>
	 * 
	 * @param observacoes {@link String} Novas observações (obrigatório)
	 */
	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	/**
	 * <h3>Obter Prioridade</h3>
	 * <p>Retorna o nível de prioridade do chamado.</p>
	 * 
	 * @return {@link Prioridade} Enum representando a prioridade
	 */
	public Prioridade getPrioridade() {
		return prioridade;
	}

	/**
	 * <h3>Definir Prioridade</h3>
	 * <p>Define o nível de prioridade do chamado.</p>
	 * 
	 * @param prioridade {@link Prioridade} Nova prioridade (obrigatório)
	 */
	public void setPrioridade(Prioridade prioridade) {
		this.prioridade = prioridade;
	}

	/**
	 * <h3>Obter Status</h3>
	 * <p>Retorna o status atual do chamado.</p>
	 * 
	 * @return {@link Status} Enum representando o status atual
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * <h3>Definir Status</h3>
	 * <p>Define o status atual do chamado.</p>
	 * 
	 * @param status {@link Status} Novo status (obrigatório)
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * <h3>Obter ID do Técnico</h3>
	 * <p>Retorna o identificador do técnico responsável pelo chamado.</p>
	 * 
	 * @return {@link Integer} ID do técnico responsável
	 */
	public Integer getTecnico() {
		return tecnico;
	}

	/**
	 * <h3>Definir ID do Técnico</h3>
	 * <p>Define o identificador do técnico responsável pelo chamado.</p>
	 * 
	 * @param tecnico {@link Integer} ID do novo técnico responsável
	 */
	public void setTecnico(Integer tecnico) {
		this.tecnico = tecnico;
	}

	/**
	 * <h3>Obter ID do Cliente</h3>
	 * <p>Retorna o identificador do cliente que abriu o chamado.</p>
	 * 
	 * @return {@link Integer} ID do cliente solicitante
	 */
	public Integer getCliente() {
		return cliente;
	}

	/**
	 * <h3>Definir ID do Cliente</h3>
	 * <p>Define o identificador do cliente que abriu o chamado.</p>
	 * 
	 * @param cliente {@link Integer} ID do cliente solicitante
	 */
	public void setCliente(Integer cliente) {
		this.cliente = cliente;
	}
}
