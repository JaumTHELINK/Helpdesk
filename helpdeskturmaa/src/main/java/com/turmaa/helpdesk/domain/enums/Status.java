package com.turmaa.helpdesk.domain.enums;

/**
 * <h1>Enumeração de Status de Chamados</h1>
 * <p>
 * Define os possíveis estados de um chamado técnico durante seu ciclo de vida
 * no sistema de helpdesk. Esta enumeração controla o fluxo de trabalho dos
 * chamados, desde a abertura até o encerramento, fornecendo uma estrutura
 * consistente para gerenciamento de estados.
 * </p>
 * 
 * <h2>Estados do Chamado:</h2>
 * <ul>
 *   <li><strong>ABERTO:</strong> Chamado criado, aguardando atendimento</li>
 *   <li><strong>ANDAMENTO:</strong> Chamado sendo trabalhado pelo técnico</li>
 *   <li><strong>ENCERRADO:</strong> Chamado resolvido e finalizado</li>
 * </ul>
 * 
 * <h2>Funcionalidades:</h2>
 * <ul>
 *   <li><strong>Código Numérico:</strong> Identificador único para persistência</li>
 *   <li><strong>Descrição Role:</strong> String formatada para sistemas de autorização</li>
 *   <li><strong>Conversão:</strong> Método para converter código em enum</li>
 *   <li><strong>Validação:</strong> Tratamento de códigos inválidos</li>
 * </ul>
 * 
 * <h2>Fluxo Típico:</h2>
 * <ol>
 *   <li><strong>ABERTO:</strong> Estado inicial quando cliente cria chamado</li>
 *   <li><strong>ANDAMENTO:</strong> Técnico aceita e inicia trabalho</li>
 *   <li><strong>ENCERRADO:</strong> Problema resolvido, chamado finalizado</li>
 * </ol>
 * 
 * <h2>Persistência:</h2>
 * <p>
 * Os valores são armazenados no banco de dados como códigos numéricos,
 * garantindo eficiência no armazenamento e facilidade nas consultas SQL.
 * </p>
 * 
 * @author Sistema Helpdesk
 * @version 1.0
 * @since 2024
 * 
 * @see com.turmaa.helpdesk.domain.Chamado
 */
public enum Status {
	/**
	 * <h3>Chamado Aberto</h3>
	 * <p>
	 * Estado inicial de um chamado recém-criado. Indica que o problema
	 * foi reportado pelo cliente e está aguardando ser atribuído a um
	 * técnico para análise e resolução.
	 * </p>
	 * 
	 * <h4>Características:</h4>
	 * <ul>
	 *   <li><strong>Estado Inicial:</strong> Todo chamado começa neste status</li>
	 *   <li><strong>Aguarda Atendimento:</strong> Ainda não foi aceito por técnico</li>
	 *   <li><strong>Código:</strong> 0 (zero) para identificação numérica</li>
	 * </ul>
	 */
	ABERTO(0,"ROLE_ABERTO"),
	
	/**
	 * <h3>Chamado em Andamento</h3>
	 * <p>
	 * Indica que o chamado está sendo ativamente trabalhado por um técnico.
	 * O problema foi analisado e está em processo de resolução.
	 * </p>
	 * 
	 * <h4>Características:</h4>
	 * <ul>
	 *   <li><strong>Em Trabalho:</strong> Técnico está ativamente resolvendo</li>
	 *   <li><strong>Progresso Ativo:</strong> Ações estão sendo tomadas</li>
	 *   <li><strong>Código:</strong> 1 (um) para identificação numérica</li>
	 * </ul>
	 */
	ANDAMENTO(1, "ROLE_ANDAMENTO"),
	
	/**
	 * <h3>Chamado Encerrado</h3>
	 * <p>
	 * Estado final indicando que o problema foi resolvido e o chamado
	 * foi finalizado. Nenhuma ação adicional é necessária.
	 * </p>
	 * 
	 * <h4>Características:</h4>
	 * <ul>
	 *   <li><strong>Estado Final:</strong> Problema completamente resolvido</li>
	 *   <li><strong>Data de Fechamento:</strong> Automaticamente definida</li>
	 *   <li><strong>Código:</strong> 2 (dois) para identificação numérica</li>
	 * </ul>
	 */
	ENCERRADO(2, "ROLE_ENCERRADO");
	
	/**
	 * <h3>Construtor do Status</h3>
	 * <p>
	 * Inicializa uma instância do enum Status com seu código numérico
	 * e descrição formatada para sistema de roles/autorização.
	 * </p>
	 * 
	 * @param codigo {@link Integer} Código numérico único para persistência
	 * @param descricao {@link String} Descrição formatada como role (ex: ROLE_ABERTO)
	 */
	Status(int codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}
	
	/**
	 * <h3>Código Numérico</h3>
	 * <p>
	 * Identificador numérico único usado para persistência no banco de dados.
	 * Permite armazenamento eficiente e consultas otimizadas.
	 * </p>
	 */
	private Integer codigo;
	
	/**
	 * <h3>Descrição do Status</h3>
	 * <p>
	 * Descrição textual formatada como role para integração com sistemas
	 * de autorização e controle de acesso.
	 * </p>
	 */
	private String descricao;
	
	/**
	 * <h3>Obter Código Numérico</h3>
	 * <p>
	 * Retorna o código numérico associado a este status.
	 * Usado principalmente para persistência no banco de dados.
	 * </p>
	 * 
	 * @return {@link Integer} Código numérico do status (0, 1 ou 2)
	 */
	public Integer getCodigo() {
		return codigo;
	}
	
	/**
	 * <h3>Obter Descrição</h3>
	 * <p>
	 * Retorna a descrição formatada como role deste status.
	 * Útil para sistemas de autorização e logs.
	 * </p>
	 * 
	 * @return {@link String} Descrição no formato ROLE_XXXXXX
	 */
	public String getDescricao() {
		return descricao;
	}
	
	/**
	 * <h3>Converter Código para Enum</h3>
	 * <p>
	 * Método estático que converte um código numérico em seu respectivo
	 * valor enum Status. Este método é essencial para conversão de dados
	 * vindos do banco de dados ou APIs externas.
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
	 *   <li><strong>0:</strong> Status.ABERTO</li>
	 *   <li><strong>1:</strong> Status.ANDAMENTO</li>
	 *   <li><strong>2:</strong> Status.ENCERRADO</li>
	 * </ul>
	 * 
	 * @param codigo {@link Integer} Código numérico a ser convertido (0, 1 ou 2)
	 * 
	 * @return {@link Status} Enum correspondente ao código fornecido, ou null se código for null
	 * 
	 * @throws IllegalArgumentException Se o código fornecido não corresponder a nenhum status válido
	 * 
	 * @see #getCodigo()
	 */
	public static Status toEnum(Integer codigo) {
		if(codigo == null) {
			return null;
		}
		for(Status x : Status.values()) {
			if(codigo.equals(x.getCodigo())) {
				return x;
			}
		}
		throw new IllegalArgumentException("Status Inválido: " + codigo);
	}
}
