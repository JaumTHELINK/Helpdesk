package com.turmaa.helpdesk.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.turmaa.helpdesk.domain.dtos.TecnicoDTO;
import com.turmaa.helpdesk.domain.enums.Perfil;

/**
 * Entidade que representa um técnico no sistema Helpdesk.
 * 
 * <p>
 * Estende a classe {@link Pessoa} adicionando funcionalidades específicas
 * para técnicos, como o relacionamento com chamados atribuídos.
 * </p>
 * 
 * <p>
 * Técnicos são responsáveis por atender e resolver os chamados abertos
 * pelos clientes no sistema. Possuem perfil TECNICO por padrão, mas podem
 * ter perfis adicionais como ADMIN.
 * </p>
 * 
 * <h3>Características:</h3>
 * <ul>
 *   <li>Herda todos os atributos e métodos de Pessoa</li>
 *   <li>Possui lista de chamados atribuídos</li>
 *   <li>Perfil TECNICO adicionado automaticamente</li>
 *   <li>Pode ser criado a partir de TecnicoDTO</li>
 * </ul>
 * 
 * @author Turma A
 * @version 1.0
 * @since 1.0
 * 
 * @see Pessoa
 * @see Chamado
 * @see TecnicoDTO
 * @see Perfil
 */
@Entity
public class Tecnico extends Pessoa{
	/**
	 * Lista de chamados atribuídos ao técnico.
	 * 
	 * <p>
	 * Relacionamento One-to-Many bidirecional com a entidade Chamado.
	 * Um técnico pode ter vários chamados atribuídos, mas cada chamado
	 * pertence a apenas um técnico.
	 * </p>
	 * 
	 * @see Chamado
	 */
	@OneToMany(mappedBy = "tecnico")
	private List<Chamado> Chamados = new ArrayList<>();
	
	/**
	 * Construtor padrão.
	 * 
	 * <p>
	 * Chama o construtor da superclasse e adiciona automaticamente
	 * o perfil TECNICO ao usuário.
	 * </p>
	 */
	public Tecnico () {
		super();
		addPerfil(Perfil.TECNICO);
	}
	
	/**
	 * Construtor com parâmetros para criação completa de um técnico.
	 * 
	 * @param id Identificador único (pode ser null para geração automática)
	 * @param nome Nome completo do técnico
	 * @param cpf CPF do técnico (deve ser único)
	 * @param email Email do técnico (deve ser único e válido)
	 * @param senha Senha para autenticação (será criptografada)
	 */
	public Tecnico (Integer id, String nome,String cpf, String email, String senha) {
		super(id, nome, cpf, email, senha);
		addPerfil(Perfil.TECNICO);
	}
	
	/**
	 * Construtor que cria um técnico a partir de um TecnicoDTO.
	 * 
	 * <p>
	 * Utilizado principalmente na conversão de dados vindos de requisições
	 * HTTP para entidade de domínio.
	 * </p>
	 * 
	 * @param dto Objeto DTO contendo os dados do técnico
	 * @see TecnicoDTO
	 */
	public Tecnico(TecnicoDTO dto) {
		this.id = dto.getId();
		this.nome = dto.getNome();
		this.cpf = dto.getCpf();
		this.email = dto.getEmail();
		this.senha = dto.getSenha();
		addPerfil(Perfil.TECNICO);
	}
	
	/**
	 * Obtém a lista de chamados atribuídos ao técnico.
	 * 
	 * @return Lista de chamados do técnico
	 */
	public List<Chamado> getChamados() {
		return Chamados;
	}
	
	/**
	 * Define a lista de chamados atribuídos ao técnico.
	 * 
	 * @param chamados Nova lista de chamados
	 */
	public void setChamados(List<Chamado> chamados) {
		Chamados = chamados;
	}
	
}

