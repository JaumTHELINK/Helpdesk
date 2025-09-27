package com.turmaa.helpdesk.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.turmaa.helpdesk.domain.enums.Perfil;

/**
 * Classe abstrata que representa uma pessoa no sistema Helpdesk.
 * 
 * <p>
 * Esta é a classe base para todas as pessoas do sistema (Técnicos e Clientes).
 * Contém as informações básicas comuns a todos os usuários como dados pessoais,
 * credenciais de acesso e perfis de autorização.
 * </p>
 * 
 * <p>
 * Utiliza herança com estratégia JOINED do JPA, onde cada subclasse terá sua
 * própria tabela no banco de dados, relacionada através de chave estrangeira.
 * </p>
 * 
 * <h3>Características principais:</h3>
 * <ul>
 *   <li>Classe abstrata - não pode ser instanciada diretamente</li>
 *   <li>Implementa Serializable para persistência e transferência de dados</li>
 *   <li>Validações de entrada nos campos obrigatórios</li>
 *   <li>Sistema de perfis para controle de acesso</li>
 *   <li>Data de criação automática</li>
 * </ul>
 * 
 * @author Turma A
 * @version 1.0
 * @since 1.0
 * 
 * @see Tecnico
 * @see Cliente
 * @see Perfil
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Pessoa implements Serializable{
	/** Número de versão para serialização. */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Identificador único da pessoa no sistema.
	 * <p>
	 * Chave primária gerada automaticamente pelo banco de dados.
	 * </p>
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Integer id;
	
	/**
	 * Nome completo da pessoa.
	 * <p>
	 * Campo obrigatório usado para identificação do usuário no sistema.
	 * </p>
	 */
	@NotNull(message = "O campo NOME é obrigatório")
	protected String nome;
	
	/**
	 * CPF da pessoa.
	 * <p>
	 * Campo obrigatório e único no sistema. Usado para validação de identidade
	 * e garantia de que não haverá duplicação de pessoas.
	 * </p>
	 */
	@NotNull(message = "O campo CPF é obrigatório")
	@Column(unique = true)
	protected String cpf;
	
	/**
	 * Endereço de email da pessoa.
	 * <p>
	 * Campo único no sistema, utilizado como username para login.
	 * Deve ter formato de email válido.
	 * </p>
	 */
	@Email(message = "O campo EMAIL deve ser válido")
	@Column(unique = true)
	protected String email;
	
	/**
	 * Senha para autenticação no sistema.
	 * <p>
	 * Campo obrigatório armazenado de forma criptografada usando BCrypt
	 * para garantir a segurança das credenciais.
	 * </p>
	 */
	@NotNull(message = "O campo SENHA é obrigatório")
    protected String senha;
	
	/**
	 * Conjunto de perfis de acesso da pessoa.
	 * <p>
	 * Define as permissões e níveis de acesso do usuário no sistema.
	 * Armazenado em tabela separada (PERFIS) com relacionamento muitos-para-muitos.
	 * </p>
	 * 
	 * @see Perfil
	 */
	@ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "PERFIS")
    protected Set<Integer> perfis = new HashSet<>();
	
	/**
	 * Data de criação do registro da pessoa no sistema.
	 * <p>
	 * Definida automaticamente como a data atual no momento da criação.
	 * Formatada como dd/MM/yyyy para exibição em JSON.
	 * </p>
	 */
	@JsonFormat(pattern = "dd/MM/yyyy")
	protected LocalDate dataCriacao = LocalDate.now();
	
	/**
	 * Construtor padrão.
	 * <p>
	 * Inicializa uma nova pessoa com perfil CLIENTE como padrão.
	 * Necessário para o JPA e frameworks de serialização.
	 * </p>
	 */
	public Pessoa() {
	       super();
	       addPerfil(Perfil.CLIENTE); // Perfil padrão
	    }
	 
	/**
	 * Construtor com parâmetros para criação completa de uma pessoa.
	 * 
	 * @param id Identificador único (pode ser null para geração automática)
	 * @param nome Nome completo da pessoa
	 * @param cpf CPF da pessoa (deve ser único)
	 * @param email Email da pessoa (deve ser único e válido)
	 * @param senha Senha para autenticação (será criptografada)
	 */
	public Pessoa(Integer id, String nome, String cpf, String email, String senha) {
	        super();
	        this.id = id;
	        this.nome = nome;
	        this.cpf = cpf;
	        this.email = email;
	        this.senha = senha;
	        addPerfil(Perfil.CLIENTE);
	    }
	 
	/**
	 * Obtém o identificador único da pessoa.
	 * 
	 * @return ID da pessoa ou null se ainda não foi persistida
	 */
	public Integer getId() {
	       return id;
	    }
	 
	/**
	 * Define o identificador único da pessoa.
	 * 
	 * @param id Novo ID da pessoa
	 */
	public void setId(Integer id) {
	       this.id = id;
	    }

	/**
	 * Obtém o nome completo da pessoa.
	 * 
	 * @return Nome da pessoa
	 */
	public String getNome() {
	       return nome;
	    }
	
	/**
	 * Define o nome completo da pessoa.
	 * 
	 * @param nome Novo nome da pessoa (não pode ser null)
	 */
	public void setNome(String nome) {
	       this.nome = nome;
	    }

	/**
	 * Obtém o CPF da pessoa.
	 * 
	 * @return CPF da pessoa
	 */
	public String getCpf() {
	       return cpf;
	    }
	
	/**
	 * Define o CPF da pessoa.
	 * 
	 * @param cpf Novo CPF da pessoa (deve ser único no sistema)
	 */
	public void setCpf(String cpf) {
	       this.cpf = cpf;
	    }

	/**
	 * Obtém o email da pessoa.
	 * 
	 * @return Email da pessoa
	 */
	public String getEmail() {
	       return email;
	    }
	
	/**
	 * Define o email da pessoa.
	 * 
	 * @param email Novo email da pessoa (deve ser válido e único)
	 */
	public void setEmail(String email) {
	       this.email = email;
	    }

	/**
	 * Obtém a senha da pessoa.
	 * 
	 * @return Senha criptografada da pessoa
	 */
	public String getSenha() {
	       return senha;
	    }
	
	/**
	 * Define a senha da pessoa.
	 * 
	 * @param senha Nova senha da pessoa (deve ser criptografada)
	 */
	public void setSenha(String senha) {
	       this.senha = senha;
	    }

	/**
	 * Obtém o conjunto de perfis de acesso da pessoa.
	 * 
	 * <p>
	 * Converte os códigos numéricos armazenados no banco em objetos Perfil
	 * para facilitar o uso no código.
	 * </p>
	 * 
	 * @return Conjunto de perfis da pessoa
	 * @see Perfil
	 */
	public Set<Perfil> getPerfis() {
	       return perfis.stream().map(x -> Perfil.toEnum(x)).collect(Collectors.toSet());
	    }

	/**
	 * Adiciona um perfil de acesso à pessoa.
	 * 
	 * <p>
	 * Permite que uma pessoa tenha múltiplos perfis de acesso,
	 * como ADMIN, TECNICO e CLIENTE simultaneamente.
	 * </p>
	 * 
	 * @param perfil Perfil a ser adicionado
	 * @see Perfil
	 */
	public void addPerfil(Perfil perfil) {
	       this.perfis.add(perfil.getCodigo());
	    }

	/**
	 * Obtém a data de criação do registro da pessoa.
	 * 
	 * @return Data quando a pessoa foi criada no sistema
	 */
	public LocalDate getDataCriacao() {
	       return dataCriacao;
	    }
	
	/**
	 * Define a data de criação do registro da pessoa.
	 * 
	 * @param dataCriacao Nova data de criação
	 */
	public void setDataCriacao(LocalDate dataCriacao) {
	       this.dataCriacao = dataCriacao;
	    }
}