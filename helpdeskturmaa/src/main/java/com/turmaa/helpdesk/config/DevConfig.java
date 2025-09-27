package com.turmaa.helpdesk.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.turmaa.helpdesk.service.DBService;

/**
 * <h2>Configuração do Ambiente de Desenvolvimento</h2>
 * <p>
 * Classe de configuração específica para o perfil de desenvolvimento ("Dev"),
 * responsável por definir beans e comportamentos exclusivos deste ambiente.
 * Gerencia inicialização automática do banco de dados e configurações
 * otimizadas para desenvolvimento local.
 * </p>
 * 
 * <h3>🔧 Ativação por Profile</h3>
 * <p>
 * Esta configuração é ativada automaticamente quando o profile "Dev" está
 * ativo na aplicação. Pode ser definido via:
 * </p>
 * <ul>
 *   <li><strong>application.properties:</strong> spring.profiles.active=dev</li>
 *   <li><strong>Environment Variable:</strong> SPRING_PROFILES_ACTIVE=dev</li>
 *   <li><strong>JVM Argument:</strong> -Dspring.profiles.active=dev</li>
 *   <li><strong>IDE Configuration:</strong> Run/Debug configuration</li>
 * </ul>
 * 
 * <h3>🎯 Funcionalidades do Ambiente Dev</h3>
 * <ul>
 *   <li><strong>🗃️ Database Initialization:</strong> Criação automática de tabelas</li>
 *   <li><strong>📊 Sample Data:</strong> População com dados de exemplo</li>
 *   <li><strong>🔄 Schema Management:</strong> Controle inteligente DDL</li>
 *   <li><strong>⚡ Fast Startup:</strong> Otimizações para desenvolvimento</li>
 * </ul>
 * 
 * <h3>🏗️ Arquitetura de Configuração</h3>
 * <p>
 * Utiliza o padrão de configuração condicional do Spring Boot, onde beans
 * são criados apenas quando certas condições são atendidas (profile ativo
 * e propriedades específicas configuradas).
 * </p>
 * 
 * <h3>🔄 Fluxo de Inicialização</h3>
 * <ol>
 *   <li><strong>Profile Check:</strong> Spring verifica se profile "Dev" está ativo</li>
 *   <li><strong>Bean Creation:</strong> Instancia DevConfig se profile corresponder</li>
 *   <li><strong>Property Injection:</strong> Injeta spring.jpa.hibernate.ddl-auto</li>
 *   <li><strong>Conditional Execution:</strong> Executa instanciaDB() se ddl-auto=create</li>
 *   <li><strong>Database Setup:</strong> DBService popula banco com dados iniciais</li>
 * </ol>
 * 
 * <h3>⚙️ Configurações Típicas de Desenvolvimento</h3>
 * <pre>
 * # application-dev.properties
 * spring.profiles.active=dev
 * spring.jpa.hibernate.ddl-auto=create
 * spring.jpa.show-sql=true
 * spring.h2.console.enabled=true
 * logging.level.org.hibernate.SQL=debug
 * </pre>
 * 
 * <div style="background-color: #d4edda; border: 1px solid #c3e6cb; border-radius: 4px; padding: 8px; margin: 8px 0;">
 * <strong>✅ Vantagens para Desenvolvimento:</strong>
 * <ul>
 *   <li>Inicialização rápida com dados consistentes</li>
 *   <li>Ambiente isolado e reproduzível</li>
 *   <li>Facilita testes manuais e debugging</li>
 *   <li>Não afeta outros ambientes (test, prod)</li>
 * </ul>
 * </div>
 * 
 * <div style="background-color: #fff3cd; border: 1px solid #ffeaa7; border-radius: 4px; padding: 8px; margin: 8px 0;">
 * <strong>⚠️ Considerações Importantes:</strong>
 * <ul>
 *   <li>Apenas para desenvolvimento - nunca em produção</li>
 *   <li>ddl-auto=create destrói dados existentes</li>
 *   <li>Profile deve ser específico e bem documentado</li>
 *   <li>Cuidado com dados sensíveis em sample data</li>
 * </ul>
 * </div>
 * 
 * <div style="background-color: #d1ecf1; border: 1px solid #b8daff; border-radius: 4px; padding: 8px; margin: 8px 0;">
 * <strong>💡 Padrão de Configuração:</strong>
 * Implementa o padrão "Configuration per Environment" onde cada ambiente
 * (dev, test, prod) possui sua própria classe de configuração com beans
 * e propriedades específicas para as necessidades daquele contexto.
 * </div>
 * 
 * @author Sistema Helpdesk
 * @since 1.0.0
 * @see Configuration
 * @see Profile
 * @see DBService
 */
@Configuration
@Profile("Dev")
public class DevConfig {
	
	/**
	 * <h3>Serviço de Inicialização do Banco</h3>
	 * <p>
	 * Serviço especializado responsável por inicializar o banco de dados
	 * com dados de exemplo para desenvolvimento. Cria usuários padrão,
	 * perfis, chamados de teste e outros dados necessários para facilitar
	 * o desenvolvimento e testes manuais.
	 * </p>
	 * 
	 * <h4>📊 Dados Criados pelo DBService:</h4>
	 * <ul>
	 *   <li><strong>Usuários Admin:</strong> Administradores do sistema</li>
	 *   <li><strong>Técnicos:</strong> Usuários com perfil de atendimento</li>
	 *   <li><strong>Clientes:</strong> Usuários finais do sistema</li>
	 *   <li><strong>Chamados:</strong> Tickets de exemplo com diferentes status</li>
	 * </ul>
	 * 
	 * <div style="background-color: #d1ecf1; border: 1px solid #b8daff; border-radius: 4px; padding: 8px; margin: 8px 0;">
	 * <strong>💡 Injeção Automática:</strong>
	 * Utiliza @Autowired para que o Spring injete automaticamente a
	 * instância do DBService, garantindo suas dependências sejam resolvidas.
	 * </div>
	 * 
	 * @see DBService#instanciaDB()
	 * @see Autowired
	 */
	@Autowired
	private DBService dbService;
	
	/**
	 * <h3>Configuração DDL do Hibernate</h3>
	 * <p>
	 * Injeta o valor da propriedade Hibernate DDL auto configuration,
	 * que determina como o Hibernate deve gerenciar o schema do banco.
	 * Usado para decidir condicionalmente se deve popular o banco com dados.
	 * </p>
	 * 
	 * <h4>📋 Valores Possíveis:</h4>
	 * <ul>
	 *   <li><strong>create:</strong> Apaga e recria schema + dados</li>
	 *   <li><strong>create-drop:</strong> Cria na inicialização, apaga no shutdown</li>
	 *   <li><strong>update:</strong> Atualiza schema preservando dados</li>
	 *   <li><strong>validate:</strong> Apenas valida schema existente</li>
	 *   <li><strong>none:</strong> Não faz alterações no schema</li>
	 * </ul>
	 * 
	 * <h4>⚠️ Atenção com Property Expression:</h4>
	 * <p>
	 * O valor atual contém erro de sintaxe: <code>&{...}</code> deveria ser <code>${...}</code>.
	 * A sintaxe correta seria: <code>@Value("${spring.jpa.hibernate.ddl-auto:}")</code>
	 * </p>
	 * 
	 * <div style="background-color: #f8d7da; border: 1px solid #f5c6cb; border-radius: 4px; padding: 8px; margin: 8px 0;">
	 * <strong>🔧 Correção Necessária:</strong>
	 * <pre>@Value("${spring.jpa.hibernate.ddl-auto:}")</pre>
	 * O ":" no final define valor padrão vazio se propriedade não existir.
	 * </div>
	 * 
	 * @see Value
	 */
	@Value("&{spring.jpa.hibernate.ddl-auto=}")
	private String value;
	
	/**
	 * <h3>🗃️ Bean de Inicialização do Banco de Dados</h3>
	 * <p>
	 * Bean de configuração que controla condicionalmente a inicialização
	 * do banco de dados com dados de exemplo. Executado automaticamente
	 * pelo Spring durante a inicialização da aplicação quando o profile
	 * "Dev" está ativo.
	 * </p>
	 * 
	 * <h4>🔍 Lógica Condicional:</h4>
	 * <p>
	 * A inicialização só ocorre quando <code>spring.jpa.hibernate.ddl-auto</code>
	 * está configurado como "create", garantindo que dados de exemplo sejam
	 * inseridos apenas quando o schema está sendo recriado do zero.
	 * </p>
	 * 
	 * <h4>📋 Cenários de Execução:</h4>
	 * <ul>
	 *   <li><strong>ddl-auto=create:</strong> ✅ Executa instanciaDB()</li>
	 *   <li><strong>ddl-auto=update:</strong> ❌ Não executa (preserva dados)</li>
	 *   <li><strong>ddl-auto=validate:</strong> ❌ Não executa (apenas valida)</li>
	 *   <li><strong>ddl-auto=none:</strong> ❌ Não executa (nenhuma alteração)</li>
	 * </ul>
	 * 
	 * <h4>⚡ Fluxo de Execução:</h4>
	 * <ol>
	 *   <li><strong>Bean Creation:</strong> Spring instancia o bean</li>
	 *   <li><strong>Condition Check:</strong> Verifica se value equals "create"</li>
	 *   <li><strong>Database Init:</strong> Chama dbService.instanciaDB() se true</li>
	 *   <li><strong>Return Value:</strong> Retorna false (não usado pelo Spring)</li>
	 * </ol>
	 * 
	 * <h4>🎯 Dados Inicializados:</h4>
	 * <ul>
	 *   <li><strong>Admin User:</strong> admin@mail.com / 123</li>
	 *   <li><strong>Técnicos:</strong> Usuários de atendimento</li>
	 *   <li><strong>Clientes:</strong> Usuários finais</li>
	 *   <li><strong>Chamados:</strong> Tickets em diferentes status</li>
	 * </ul>
	 * 
	 * <h4>💡 Vantagens da Abordagem:</h4>
	 * <ul>
	 *   <li><strong>Conditional:</strong> Só executa quando necessário</li>
	 *   <li><strong>Consistent:</strong> Dados sempre iguais para desenvolvimento</li>
	 *   <li><strong>Safe:</strong> Não sobrescreve dados em update/validate</li>
	 *   <li><strong>Automatic:</strong> Configuração zero pelo desenvolvedor</li>
	 * </ul>
	 * 
	 * <div style="background-color: #d4edda; border: 1px solid #c3e6cb; border-radius: 4px; padding: 8px; margin: 8px 0;">
	 * <strong>✅ Benefícios para Desenvolvimento:</strong>
	 * <ul>
	 *   <li>Ambiente sempre pronto para uso</li>
	 *   <li>Dados consistentes entre reinicializações</li>
	 *   <li>Facilita testes manuais e demonstrações</li>
	 *   <li>Reduz tempo de setup do ambiente</li>
	 * </ul>
	 * </div>
	 * 
	 * <div style="background-color: #fff3cd; border: 1px solid #ffeaa7; border-radius: 4px; padding: 8px; margin: 8px 0;">
	 * <strong>⚠️ Consideração sobre Return Type:</strong>
	 * O método retorna boolean mas o valor não é utilizado pelo Spring.
	 * Poderia retornar void ou outro tipo mais semântico. O importante
	 * é a anotação @Bean que registra a execução do método.
	 * </div>
	 * 
	 * <div style="background-color: #d1ecf1; border: 1px solid #b8daff; border-radius: 4px; padding: 8px; margin: 8px 0;">
	 * <strong>💡 Alternativa de Design:</strong>
	 * Uma abordagem alternativa seria usar @EventListener com
	 * ApplicationReadyEvent para separar configuração de inicialização
	 * de dados, melhorando separação de responsabilidades.
	 * </div>
	 * 
	 * @return {@code false} sempre - valor não utilizado pelo Spring,
	 *         método executado pelo efeito colateral da inicialização
	 * 
	 * @see Bean
	 * @see DBService#instanciaDB()
	 * @see Profile
	 */
	@Bean
	public boolean instanciaDB() {
		if(value.equals("create")) {
			this.dbService.instanciaDB();
		}
		return false;
	}
}
