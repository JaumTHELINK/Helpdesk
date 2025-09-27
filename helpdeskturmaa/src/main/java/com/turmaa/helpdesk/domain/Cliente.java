package com.turmaa.helpdesk.domain;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.turmaa.helpdesk.domain.dtos.ClienteDTO;
import com.turmaa.helpdesk.domain.enums.Perfil;

/**
 * Entidade que representa um cliente no sistema Helpdesk.
 * 
 * <p>
 * Estende a classe {@link Pessoa} adicionando funcionalidades específicas
 * para clientes, como o relacionamento com chamados abertos por eles.
 * </p>
 * 
 * <p>
 * Clientes são os usuários que abrem chamados no sistema quando precisam
 * de suporte técnico. Possuem perfil CLIENTE por padrão.
 * </p>
 * 
 * <h3>Características:</h3>
 * <ul>
 *   <li>Herda todos os atributos e métodos de Pessoa</li>
 *   <li>Possui lista de chamados abertos</li>
 *   <li>Perfil CLIENTE adicionado automaticamente</li>
 *   <li>Pode ser criado a partir de ClienteDTO</li>
 *   <li>Lista de chamados ignorada na serialização JSON para evitar loops</li>
 * </ul>
 * 
 * @author Turma A
 * @version 1.0
 * @since 1.0
 * 
 * @see Pessoa
 * @see Chamado
 * @see ClienteDTO
 * @see Perfil
 */
@Entity
public class Cliente extends Pessoa {
	/** Número de versão para serialização. */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Lista de chamados abertos pelo cliente.
	 * 
	 * <p>
	 * Relacionamento One-to-Many bidirecional com a entidade Chamado.
	 * Um cliente pode abrir vários chamados, mas cada chamado
	 * pertence a apenas um cliente.
	 * </p>
	 * 
	 * <p>
	 * Anotado com {@code @JsonIgnore} para evitar loops infinitos
	 * na serialização JSON quando um chamado referencia seu cliente.
	 * </p>
	 * 
	 * @see Chamado
	 */
	@JsonIgnore
    @OneToMany(mappedBy = "cliente")
    private List<Chamado> chamados = new ArrayList<>();
	
	/**
	 * Construtor padrão.
	 * 
	 * <p>
	 * Chama o construtor da superclasse e adiciona automaticamente
	 * o perfil CLIENTE ao usuário.
	 * </p>
	 */
	public Cliente() {
        super();
        addPerfil(Perfil.CLIENTE);
    }
	
	/**
	 * Construtor com parâmetros para criação completa de um cliente.
	 * 
	 * @param id Identificador único (pode ser null para geração automática)
	 * @param nome Nome completo do cliente
	 * @param cpf CPF do cliente (deve ser único)
	 * @param email Email do cliente (deve ser único e válido)
	 * @param senha Senha para autenticação (será criptografada)
	 */
	public Cliente(Integer id, String nome, String cpf, String email, String senha) {
        super(id, nome, cpf, email, senha);
        addPerfil(Perfil.CLIENTE);
    }
	
	/**
	 * Construtor que cria um cliente a partir de um ClienteDTO.
	 * 
	 * <p>
	 * Utilizado principalmente na conversão de dados vindos de requisições
	 * HTTP para entidade de domínio.
	 * </p>
	 * 
	 * @param dto Objeto DTO contendo os dados do cliente
	 * @see ClienteDTO
	 */
	public Cliente(ClienteDTO dto) {
        this.id = dto.getId();
        this.nome = dto.getNome();
        this.cpf = dto.getCpf();
        this.email = dto.getEmail();
    }
	
	/**
	 * Obtém a lista de chamados abertos pelo cliente.
	 * 
	 * @return Lista de chamados do cliente
	 */
	public List<Chamado> getChamados() {
        return chamados;
    }
    
    /**
     * Define a lista de chamados abertos pelo cliente.
     * 
     * @param chamados Nova lista de chamados
     */
    public void setChamados(List<Chamado> chamados) {
        this.chamados = chamados;
    }
}