package com.turmaa.helpdesk.domain.enums;

public enum Prioridade {
	BAIXA(0,"ROLE_BAIXA"),
	MEDIA(1, "ROLE_MEDIA"),
	ALTA(2, "ROLE_ALTA");
	
	Prioridade(int codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}
	private Integer codigo;
	private String descricao;
	
	public Integer getCodigo() {
		return codigo;
	}
	public String getDescricao() {
		return descricao;
	}
	
	public static Prioridade toEnum(Integer codigo) {
		if(codigo == null) {
			return null;
		}
		for(Prioridade x : Prioridade.values()) {
			if(codigo.equals(x.getCodigo())) {
				return x;
			}
		}
		throw new IllegalArgumentException("Perfil Invalido");
	}
}
