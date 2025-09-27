package com.turmaa.helpdesk.domain.enums;

/**
 * <h1>Enumeração de Prioridades de Chamados</h1>
 * <p>
 * Define os níveis de prioridade que podem ser atribuídos aos chamados técnicos
 * no sistema de helpdesk. Esta enumeração é fundamental para o gerenciamento
 * de recursos e definição de SLA (Service Level Agreement), permitindo que
 * chamados mais críticos sejam priorizados no atendimento.
 * </p>
 * 
 * <h2>Níveis de Prioridade:</h2>
 * <ul>
 *   <li><strong>BAIXA:</strong> Problemas menores, sem impacto significativo</li>
 *   <li><strong>MÉDIA:</strong> Problemas que afetam algumas funcionalidades</li>
 *   <li><strong>ALTA:</strong> Problemas críticos que impedem o trabalho</li>
 * </ul>
 * 
 * <h2>Critérios de Classificação:</h2>
 * <ul>
 *   <li><strong>BAIXA:</strong> Dúvidas, melhorias, problemas cosméticos</li>
 *   <li><strong>MÉDIA:</strong> Funcionalidades com workaround disponível</li>
 *   <li><strong>ALTA:</strong> Sistema indisponível, perda de dados, segurança</li>
 * </ul>
 * 
 * <h2>Funcionalidades:</h2>
 * <ul>
 *   <li><strong>Código Numérico:</strong> Identificador único para persistência e ordenação</li>
 *   <li><strong>Descrição Role:</strong> String formatada para sistemas de autorização</li>
 *   <li><strong>Conversão:</strong> Método para converter código em enum</li>
 *   <li><strong>Ordenação Natural:</strong> Crescente por código (0=BAIXA, 2=ALTA)</li>
 * </ul>
 * 
 * <h2>SLA Sugerido por Prioridade:</h2>
 * <ul>
 *   <li><strong>BAIXA:</strong> Até 5 dias úteis para resposta</li>
 *   <li><strong>MÉDIA:</strong> Até 2 dias úteis para resposta</li>
 *   <li><strong>ALTA:</strong> Até 4 horas para resposta</li>
 * </ul>
 * 
 * @author Sistema Helpdesk
 * @version 1.0
 * @since 2024
 * 
 * @see com.turmaa.helpdesk.domain.Chamado
 */
public enum Prioridade {
	/**
	 * <h3>Prioridade Baixa</h3>
	 * <p>
	 * Representa problemas ou solicitações de baixa criticidade que não afetam
	 * significativamente o trabalho do usuário. Podem aguardar mais tempo
	 * para resolução sem impacto no negócio.
	 * </p>
	 * 
	 * <h4>Exemplos de Uso:</h4>
	 * <ul>
	 *   <li>Dúvidas sobre funcionamento do sistema</li>
	 *   <li>Solicitações de melhorias não urgentes</li>
	 *   <li>Problemas cosméticos na interface</li>
	 *   <li>Documentação ou treinamento</li>
	 * </ul>
	 * 
	 * <h4>SLA: Até 5 dias úteis</h4>
	 */
	BAIXA(0,"ROLE_BAIXA"),
	
	/**
	 * <h3>Prioridade Média</h3>
	 * <p>
	 * Representa problemas que afetam algumas funcionalidades mas possuem
	 * alternativas (workarounds) que permitem ao usuário continuar trabalhando,
	 * mesmo com alguma limitação.
	 * </p>
	 * 
	 * <h4>Exemplos de Uso:</h4>
	 * <ul>
	 *   <li>Funcionalidades com comportamento inesperado</li>
	 *   <li>Relatórios com dados incorretos</li>
	 *   <li>Integrações com lentidão ocasional</li>
	 *   <li>Problemas que afetam poucos usuários</li>
	 * </ul>
	 * 
	 * <h4>SLA: Até 2 dias úteis</h4>
	 */
	MEDIA(1, "ROLE_MEDIA"),
	
	/**
	 * <h3>Prioridade Alta</h3>
	 * <p>
	 * Representa problemas críticos que impedem completamente o trabalho
	 * do usuário ou afetam a segurança e integridade dos dados. Requer
	 * atenção imediata e recursos dedicados para resolução.
	 * </p>
	 * 
	 * <h4>Exemplos de Uso:</h4>
	 * <ul>
	 *   <li>Sistema completamente indisponível</li>
	 *   <li>Perda ou corrupção de dados</li>
	 *   <li>Problemas de segurança ou vulnerabilidades</li>
	 *   <li>Falhas que afetam todos os usuários</li>
	 * </ul>
	 * 
	 * <h4>SLA: Até 4 horas</h4>
	 */
	ALTA(2, "ROLE_ALTA");
	
	/**
	 * <h3>Construtor da Prioridade</h3>
	 * <p>
	 * Inicializa uma instância do enum Prioridade com seu código numérico
	 * e descrição formatada para sistema de roles/autorização.
	 * </p>
	 * 
	 * @param codigo {@link Integer} Código numérico único para persistência e ordenação
	 * @param descricao {@link String} Descrição formatada como role (ex: ROLE_BAIXA)
	 */
	Prioridade(int codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}
	
	/**
	 * <h3>Código Numérico</h3>
	 * <p>
	 * Identificador numérico único usado para persistência no banco de dados
	 * e ordenação natural das prioridades. Quanto maior o código, maior a prioridade.
	 * </p>
	 */
	private Integer codigo;
	
	/**
	 * <h3>Descrição da Prioridade</h3>
	 * <p>
	 * Descrição textual formatada como role para integração com sistemas
	 * de autorização e controle de acesso.
	 * </p>
	 */
	private String descricao;
	
	/**
	 * <h3>Obter Código Numérico</h3>
	 * <p>
	 * Retorna o código numérico associado a esta prioridade.
	 * Usado para persistência no banco de dados e ordenação de chamados.
	 * </p>
	 * 
	 * @return {@link Integer} Código numérico da prioridade (0=BAIXA, 1=MÉDIA, 2=ALTA)
	 */
	public Integer getCodigo() {
		return codigo;
	}
	
	/**
	 * <h3>Obter Descrição</h3>
	 * <p>
	 * Retorna a descrição formatada como role desta prioridade.
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
	 * valor enum Prioridade. Este método é essencial para conversão de dados
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
	 *   <li><strong>0:</strong> Prioridade.BAIXA</li>
	 *   <li><strong>1:</strong> Prioridade.MEDIA</li>
	 *   <li><strong>2:</strong> Prioridade.ALTA</li>
	 * </ul>
	 * 
	 * <h4>Uso em Ordenação:</h4>
	 * <p>
	 * O código numérico permite ordenação natural crescente da prioridade,
	 * onde chamados ALTA (2) aparecem antes de BAIXA (0) em ordenação decrescente.
	 * </p>
	 * 
	 * @param codigo {@link Integer} Código numérico a ser convertido (0, 1 ou 2)
	 * 
	 * @return {@link Prioridade} Enum correspondente ao código fornecido, ou null se código for null
	 * 
	 * @throws IllegalArgumentException Se o código fornecido não corresponder a nenhuma prioridade válida
	 * 
	 * @see #getCodigo()
	 */
	public static Prioridade toEnum(Integer codigo) {
		if(codigo == null) {
			return null;
		}
		for(Prioridade x : Prioridade.values()) {
			if(codigo.equals(x.getCodigo())) {
				return x;
			}
		}
		throw new IllegalArgumentException("Prioridade Inválida: " + codigo);
	}
}
