package com.turmaa.helpdesk.domain.enums;

public enum Perfil {
	ADMIN(0,"ROLE_ADMIN"),
	CLIENTE(1, "ROLE_CLIENTE"),
	TECNICO(2, "ROLE_TECNICO");
	
	Perfil(int codigo, String descricao) {
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
	
	public static Perfil toEnum(Integer codigo) {
		if(codigo == null) {
			return null;
		}
		for(Perfil x : Perfil.values()) {
			if(codigo.equals(x.getCodigo())) {
				return x;
			}
		}
		throw new IllegalArgumentException("Perfil Invalido");
	}
}
