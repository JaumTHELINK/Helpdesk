package com.turmaa.helpdesk.domain.dtos;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.turmaa.helpdesk.domain.Cliente;

/**
 * <h1>Data Transfer Object (DTO) para Clientes</h1>
 * <p>
 * Classe responsável pela transferência segura de dados de clientes entre as
 * diferentes camadas da aplicação. Este DTO implementa uma versão simplificada
 * da entidade Cliente, contendo apenas os campos essenciais para operações
 * de CRUD e exibição nas interfaces do sistema.
 * </p>
 * 
 * <h2>Principais Características:</h2>
 * <ul>
 *   <li><strong>Serialização:</strong> Implementa Serializable para transferência via rede</li>
 *   <li><strong>Validação:</strong> Bean Validation para integridade dos dados obrigatórios</li>
 *   <li><strong>Encapsulamento:</strong> Expõe apenas dados públicos, sem informações sensíveis</li>
 *   <li><strong>Conversão Automática:</strong> Construtor que converte entidade para DTO</li>
 *   <li><strong>Independência:</strong> Desacoplamento total da camada de persistência</li>
 * </ul>
 * 
 * <h2>Campos Expostos:</h2>
 * <ul>
 *   <li><strong>ID:</strong> Identificador único do cliente</li>
 *   <li><strong>Nome:</strong> Nome completo para identificação</li>
 *   <li><strong>CPF:</strong> Documento único para validação</li>
 *   <li><strong>Email:</strong> Contato e login do sistema</li>
 * </ul>
 * 
 * <h2>Campos Não Expostos (Segurança):</h2>
 * <ul>
 *   <li><strong>Senha:</strong> Mantida apenas na entidade (segurança)</li>
 *   <li><strong>Perfis:</strong> Informações de autorização sensíveis</li>
 *   <li><strong>Chamados:</strong> Relacionamentos complexos (performance)</li>
 *   <li><strong>Data de Criação:</strong> Metadados internos</li>
 * </ul>
 * 
 * <h2>Casos de Uso:</h2>
 * <ul>
 *   <li>Listagens de clientes na interface</li>
 *   <li>Formulários de cadastro/edição</li>
 *   <li>Respostas de API REST</li>
 *   <li>Seleção de clientes em chamados</li>
 * </ul>
 * 
 * @author Sistema Helpdesk
 * @version 1.0
 * @since 2024
 * 
 * @see Cliente
 * @see com.turmaa.helpdesk.resources.ClienteResource
 * @see com.turmaa.helpdesk.service.ClienteService
 */
public class ClienteDTO implements Serializable {
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
	 * Chave primária do cliente. Este campo é automaticamente gerado
	 * pelo banco de dados e serve como identificador único em todo o sistema.
	 * </p>
	 */
	private Integer id;

	/**
	 * <h3>Nome Completo do Cliente</h3>
	 * <p>
	 * Nome completo utilizado para identificação do cliente no sistema,
	 * interfaces de usuário e relatórios. Este campo é obrigatório e
	 * deve conter o nome real da pessoa para fins de identificação.
	 * </p>
	 * 
	 * <h4>Validações:</h4>
	 * <ul>
	 *   <li>Campo obrigatório (não pode ser nulo ou vazio)</li>
	 *   <li>Usado em interfaces para identificação visual</li>
	 *   <li>Exibido em listagens e seletores de chamados</li>
	 * </ul>
	 */
	@NotNull(message = "O campo NOME é obrigatório")
	private String nome;

	/**
	 * <h3>Número do CPF</h3>
	 * <p>
	 * Cadastro de Pessoa Física (CPF) do cliente, utilizado como documento
	 * único de identificação. Este campo é obrigatório e deve ser único
	 * no sistema, garantindo que cada cliente tenha apenas um cadastro.
	 * </p>
	 * 
	 * <h4>Características:</h4>
	 * <ul>
	 *   <li><strong>Formato:</strong> String com ou sem formatação (XXX.XXX.XXX-XX)</li>
	 *   <li><strong>Unicidade:</strong> Cada CPF pode ter apenas um cadastro</li>
	 *   <li><strong>Validação:</strong> Algoritmo de validação aplicado no backend</li>
	 *   <li><strong>Obrigatório:</strong> Não pode ser nulo ou vazio</li>
	 * </ul>
	 */
	@NotNull(message = "O campo CPF é obrigatório")
	private String cpf;

	/**
	 * <h3>Endereço de Email</h3>
	 * <p>
	 * Email utilizado tanto para comunicação quanto para autenticação no sistema.
	 * Este campo serve como login único e deve ser válido e acessível pelo cliente.
	 * </p>
	 * 
	 * <h4>Funcionalidades:</h4>
	 * <ul>
	 *   <li><strong>Autenticação:</strong> Usado como username para login</li>
	 *   <li><strong>Comunicação:</strong> Notificações sobre chamados</li>
	 *   <li><strong>Recuperação:</strong> Processos de reset de senha</li>
	 *   <li><strong>Unicidade:</strong> Cada email representa um cliente único</li>
	 * </ul>
	 * 
	 * <h4>Validações:</h4>
	 * <ul>
	 *   <li>Formato de email válido (validação via regex)</li>
	 *   <li>Campo obrigatório</li>
	 *   <li>Deve ser único no sistema</li>
	 * </ul>
	 */
	@NotNull(message = "O campo EMAIL é obrigatório")
	private String email;

	/**
	 * <h3>Construtor Padrão</h3>
	 * <p>
	 * Construtor vazio necessário para frameworks como Spring Boot, Jackson
	 * e outras bibliotecas que precisam instanciar objetos via reflexão
	 * durante processos de deserialização e binding de formulários.
	 * </p>
	 */
	public ClienteDTO() {
		super();
	}

	/**
	 * <h3>Construtor de Conversão Entity → DTO</h3>
	 * <p>
	 * Cria uma instância de ClienteDTO a partir de uma entidade Cliente completa.
	 * Este construtor implementa a conversão segura dos dados essenciais,
	 * filtrando informações sensíveis e expondo apenas campos apropriados
	 * para transferência entre camadas.
	 * </p>
	 * 
	 * <h4>Campos Convertidos:</h4>
	 * <ul>
	 *   <li><strong>ID:</strong> Identificador único copiado diretamente</li>
	 *   <li><strong>Nome:</strong> Nome completo para identificação</li>
	 *   <li><strong>CPF:</strong> Documento único (pode ser formatado)</li>
	 *   <li><strong>Email:</strong> Endereço para comunicação e login</li>
	 * </ul>
	 * 
	 * <h4>Campos Não Convertidos (Segurança):</h4>
	 * <ul>
	 *   <li><strong>Senha:</strong> Nunca exposta em DTOs</li>
	 *   <li><strong>Perfis:</strong> Informações de autorização sensíveis</li>
	 *   <li><strong>Chamados:</strong> Relacionamentos complexos (evita N+1)</li>
	 *   <li><strong>Metadados:</strong> Data de criação e campos internos</li>
	 * </ul>
	 * 
	 * @param obj {@link Cliente} Entidade cliente completa a ser convertida em DTO
	 * 
	 * @see Cliente
	 */
	public ClienteDTO(Cliente obj) {
		this.id = obj.getId();
		this.nome = obj.getNome();
		this.cpf = obj.getCpf();
		this.email = obj.getEmail();
	}

	// ===========================================
	// GETTERS E SETTERS
	// ===========================================
	
	/**
	 * <h3>Obter ID do Cliente</h3>
	 * <p>Retorna o identificador único do cliente.</p>
	 * 
	 * @return {@link Integer} ID do cliente ou null se ainda não persistido
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * <h3>Definir ID do Cliente</h3>
	 * <p>Define o identificador único do cliente. Geralmente usado durante atualizações.</p>
	 * 
	 * @param id {@link Integer} Novo ID do cliente
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * <h3>Obter Nome</h3>
	 * <p>Retorna o nome completo do cliente.</p>
	 * 
	 * @return {@link String} Nome completo do cliente
	 */
	public String getNome() {
		return nome;
	}

	/**
	 * <h3>Definir Nome</h3>
	 * <p>Define o nome completo do cliente.</p>
	 * 
	 * @param nome {@link String} Novo nome completo (obrigatório)
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}

	/**
	 * <h3>Obter CPF</h3>
	 * <p>Retorna o número do CPF do cliente.</p>
	 * 
	 * @return {@link String} CPF do cliente (pode estar formatado)
	 */
	public String getCpf() {
		return cpf;
	}

	/**
	 * <h3>Definir CPF</h3>
	 * <p>Define o número do CPF do cliente. Deve ser único no sistema.</p>
	 * 
	 * @param cpf {@link String} Novo CPF (obrigatório e único)
	 */
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	/**
	 * <h3>Obter Email</h3>
	 * <p>Retorna o endereço de email do cliente.</p>
	 * 
	 * @return {@link String} Email do cliente
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * <h3>Definir Email</h3>
	 * <p>Define o endereço de email do cliente. Usado para login e comunicação.</p>
	 * 
	 * @param email {@link String} Novo email (obrigatório e único)
	 */
	public void setEmail(String email) {
		this.email = email;
	}
}
