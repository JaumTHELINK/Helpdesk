package com.turmaa.helpdesk.domain.enums;

/**
 * <h1>Enumeração de Perfis de Usuário</h1>
 * <p>
 * Define os diferentes níveis de acesso e autorização disponíveis no sistema
 * de helpdesk. Esta enumeração é fundamental para o controle de segurança,
 * determinando quais funcionalidades cada tipo de usuário pode acessar e
 * quais operações podem executar.
 * </p>
 * 
 * <h2>Perfis Disponíveis:</h2>
 * <ul>
 *   <li><strong>ADMIN:</strong> Administrador com acesso total ao sistema</li>
 *   <li><strong>CLIENTE:</strong> Cliente que pode abrir e acompanhar chamados</li>
 *   <li><strong>TECNICO:</strong> Técnico que resolve e gerencia chamados</li>
 * </ul>
 * 
 * <h2>Hierarquia de Permissões:</h2>
 * <ul>
 *   <li><strong>ADMIN:</strong> Todas as permissões + gerenciamento de usuários</li>
 *   <li><strong>TECNICO:</strong> Gerenciar chamados + visualizar clientes</li>
 *   <li><strong>CLIENTE:</strong> Criar e visualizar próprios chamados</li>
 * </ul>
 * 
 * <h2>Integração com Spring Security:</h2>
 * <p>
 * Os perfis são formatados como roles do Spring Security (ROLE_XXXX),
 * permitindo integração direta com anotações como @PreAuthorize e
 * configurações de segurança baseadas em authorities.
 * </p>
 * 
 * <h2>Funcionalidades por Perfil:</h2>
 * <ul>
 *   <li><strong>ADMIN:</strong> CRUD completo de usuários, chamados e configurações</li>
 *   <li><strong>TECNICO:</strong> Resolver chamados, visualizar clientes, atualizar status</li>
 *   <li><strong>CLIENTE:</strong> Criar chamados, visualizar histórico próprio</li>
 * </ul>
 * 
 * @author Sistema Helpdesk
 * @version 1.0
 * @since 2024
 * 
 * @see com.turmaa.helpdesk.domain.Pessoa
 * @see org.springframework.security.core.authority.SimpleGrantedAuthority
 */
public enum Perfil {
	/**
	 * <h3>Perfil Administrador</h3>
	 * <p>
	 * Maior nível de privilégio no sistema. Administradores têm acesso
	 * completo a todas as funcionalidades, incluindo gerenciamento de
	 * usuários, configurações do sistema e relatórios avançados.
	 * </p>
	 * 
	 * <h4>Permissões Exclusivas:</h4>
	 * <ul>
	 *   <li>Criar, editar e remover técnicos e clientes</li>
	 *   <li>Acessar configurações do sistema</li>
	 *   <li>Visualizar relatórios gerenciais</li>
	 *   <li>Gerenciar perfis de usuário</li>
	 *   <li>Backup e manutenção do sistema</li>
	 * </ul>
	 * 
	 * <h4>Acesso Total a Chamados</h4>
	 */
	ADMIN(0,"ROLE_ADMIN"),
	
	/**
	 * <h3>Perfil Cliente</h3>
	 * <p>
	 * Usuários finais que utilizam o sistema para reportar problemas
	 * e solicitar suporte técnico. Têm acesso limitado apenas às suas
	 * próprias informações e chamados.
	 * </p>
	 * 
	 * <h4>Permissões:</h4>
	 * <ul>
	 *   <li>Criar novos chamados de suporte</li>
	 *   <li>Visualizar histórico dos próprios chamados</li>
	 *   <li>Atualizar informações do próprio perfil</li>
	 *   <li>Adicionar comentários aos próprios chamados</li>
	 *   <li>Alterar senha de acesso</li>
	 * </ul>
	 * 
	 * <h4>Restrições de Segurança</h4>
	 * <p>Clientes só podem acessar chamados que eles próprios criaram.</p>
	 */
	CLIENTE(1, "ROLE_CLIENTE"),
	
	/**
	 * <h3>Perfil Técnico</h3>
	 * <p>
	 * Profissionais responsáveis pela resolução dos chamados técnicos.
	 * Têm permissões intermediárias que permitem gerenciar chamados
	 * e visualizar informações de clientes conforme necessário.
	 * </p>
	 * 
	 * <h4>Permissões:</h4>
	 * <ul>
	 *   <li>Visualizar e gerenciar todos os chamados</li>
	 *   <li>Atualizar status e prioridade de chamados</li>
	 *   <li>Visualizar informações básicas de clientes</li>
	 *   <li>Adicionar soluções e comentários técnicos</li>
	 *   <li>Aceitar atribuição de chamados</li>
	 *   <li>Encerrar chamados resolvidos</li>
	 * </ul>
	 * 
	 * <h4>Responsabilidades de Atendimento</h4>
	 */
	TECNICO(2, "ROLE_TECNICO");
	
	/**
	 * <h3>Construtor do Perfil</h3>
	 * <p>
	 * Inicializa uma instância do enum Perfil com seu código numérico
	 * e descrição formatada como role do Spring Security.
	 * </p>
	 * 
	 * @param codigo {@link Integer} Código numérico único para persistência
	 * @param descricao {@link String} Descrição formatada como role Spring Security
	 */
	Perfil(int codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}
	
	/**
	 * <h3>Código Numérico</h3>
	 * <p>
	 * Identificador numérico único usado para persistência no banco de dados.
	 * Permite armazenamento eficiente em relacionamentos many-to-many com usuários.
	 * </p>
	 */
	private Integer codigo;
	
	/**
	 * <h3>Descrição do Perfil</h3>
	 * <p>
	 * Descrição textual formatada como role do Spring Security (ROLE_XXXX).
	 * Utilizada diretamente pelo framework de segurança para autorização.
	 * </p>
	 */
	private String descricao;
	
	/**
	 * <h3>Obter Código Numérico</h3>
	 * <p>
	 * Retorna o código numérico associado a este perfil.
	 * Usado para persistência em relacionamentos many-to-many.
	 * </p>
	 * 
	 * @return {@link Integer} Código numérico do perfil (0=ADMIN, 1=CLIENTE, 2=TECNICO)
	 */
	public Integer getCodigo() {
		return codigo;
	}
	
	/**
	 * <h3>Obter Descrição (Role)</h3>
	 * <p>
	 * Retorna a descrição formatada como role do Spring Security.
	 * Esta string é utilizada diretamente pelo framework para
	 * controle de autorização em endpoints e métodos.
	 * </p>
	 * 
	 * @return {@link String} Role no formato Spring Security (ROLE_XXXX)
	 */
	public String getDescricao() {
		return descricao;
	}
	
	/**
	 * <h3>Converter Código para Enum</h3>
	 * <p>
	 * Método estático que converte um código numérico em seu respectivo
	 * valor enum Perfil. Este método é essencial para conversão de dados
	 * vindos do banco de dados durante a autenticação e autorização.
	 * </p>
	 * 
	 * <h4>Processo de Conversão:</h4>
	 * <ol>
	 *   <li><strong>Validação Null:</strong> Retorna null se código for null</li>
	 *   <li><strong>Busca Iterativa:</strong> Percorre todos os valores do enum</li>
	 *   <li><strong>Comparação:</strong> Compara código fornecido com códigos do enum</li>
	 *   <li><strong>Retorno:</strong> Retorna enum correspondente ou lança exceção</li>
	 * </ol>
	 * 
	 * <h4>Códigos Válidos:</h4>
	 * <ul>
	 *   <li><strong>0:</strong> Perfil.ADMIN</li>
	 *   <li><strong>1:</strong> Perfil.CLIENTE</li>
	 *   <li><strong>2:</strong> Perfil.TECNICO</li>
	 * </ul>
	 * 
	 * <h4>Uso em Autenticação:</h4>
	 * <p>
	 * Este método é comumente utilizado durante o processo de login
	 * para converter perfis armazenados no banco em authorities
	 * do Spring Security.
	 * </p>
	 * 
	 * @param codigo {@link Integer} Código numérico a ser convertido (0, 1 ou 2)
	 * 
	 * @return {@link Perfil} Enum correspondente ao código fornecido, ou null se código for null
	 * 
	 * @throws IllegalArgumentException Se o código fornecido não corresponder a nenhum perfil válido
	 * 
	 * @see #getCodigo()
	 * @see #getDescricao()
	 */
	public static Perfil toEnum(Integer codigo) {
		if(codigo == null) {
			return null;
		}
		for(Perfil x : Perfil.values()) {
			if(codigo.equals(x.getCodigo())) {
				return x;
			}
		}
		throw new IllegalArgumentException("Perfil Inválido: " + codigo);
	}
}
