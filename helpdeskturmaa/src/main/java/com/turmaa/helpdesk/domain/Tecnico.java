package com.turmaa.helpdesk.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.turmaa.helpdesk.domain.dtos.TecnicoDTO;
import com.turmaa.helpdesk.domain.enums.Perfil;

@Entity
public class Tecnico extends Pessoa{
	@OneToMany(mappedBy = "tecnico")
	private List<Chamado> Chamados = new ArrayList<>();
	
	public Tecnico () {
		super();
		addPerfil(Perfil.TECNICO);
	}
	public Tecnico (Integer id, String nome,String cpf, String email, String senha) {
		super(id, nome, cpf, email, senha);
		addPerfil(Perfil.TECNICO);
	}
	
	public List<Chamado> getChamados() {
		return Chamados;
	}
	public void setChamados(List<Chamado> chamados) {
		Chamados = chamados;
	}
	public Tecnico(TecnicoDTO dto) {
	    this.id = dto.getId();
	    this.nome = dto.getNome();
	    this.cpf = dto.getCpf();
	    this.email = dto.getEmail();

	}
	
}

