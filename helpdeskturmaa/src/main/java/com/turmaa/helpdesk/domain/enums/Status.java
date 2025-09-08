package com.turmaa.helpdesk.domain.enums;

public enum Status {
	ABERTO(0,"ROLE_ABERTO"),
	ANDAMENTO(1, "ROLE_ANDAMENTO"),
	ENCERRADO(2, "ROLE_ENCERRADO");
	
	Status(int codigo, String descricao) {
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
	
	public static Status toEnum(Integer codigo) {
		if(codigo == null) {
			return null;
		}
		for(Status x : Status.values()) {
			if(codigo.equals(x.getCodigo())) {
				return x;
			}
		}
		throw new IllegalArgumentException("Perfil Invalido");
	}
}
