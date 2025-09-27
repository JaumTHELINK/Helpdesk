package com.turmaa.helpdesk.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.turmaa.helpdesk.service.DBService;

/**
 * <h2>Configuração do Ambiente de Testes</h2>
 * <p>
 * Classe de configuração específica para o perfil de testes ("test"),
 * responsável por definir beans e comportamentos exclusivos para execução
 * de testes automatizados. Garante ambiente limpo e consistente para
 * cada execução de teste.
 * </p>
 * 
 * <h3>🧪 Ativação por Profile</h3>
 * <p>
 * Esta configuração é ativada automaticamente quando o profile "test" está
 * ativo na aplicação. Geralmente configurado via:
 * </p>
 * <ul>
 *   <li><strong>@ActiveProfiles("test"):</strong> Em classes de teste</li>
 *   <li><strong>application-test.properties:</strong> Arquivo de propriedades específico</li>
 *   <li><strong>Test Containers:</strong> Configuração Docker para testes</li>
 *   <li><strong>IDE Test Runner:</strong> Profile automático em execução de testes</li>
 * </ul>
 * 
 * <h3>🎯 Funcionalidades do Ambiente Test</h3>
 * <ul>
 *   <li><strong>🗃️ Clean Database:</strong> Banco limpo para cada teste</li>
 *   <li><strong>📊 Predictable Data:</strong> Dados conhecidos e consistentes</li>
 *   <li><strong>⚡ Fast Execution:</strong> Otimizado para velocidade</li>
 *   <li><strong>🔒 Isolation:</strong> Isolamento total de outros ambientes</li>
 * </ul>
 * 
 * <h3>🏗️ Arquitetura de Testes</h3>
 * <p>
 * Diferentemente do ambiente de desenvolvimento, o ambiente de testes
 * SEMPRE inicializa o banco com dados padrão, independentemente da
 * configuração DDL. Isso garante consistência e previsibilidade nos testes.
 * </p>
 * 
 * <h3>🔄 Fluxo de Execução em Testes</h3>
 * <ol>
 *   <li><strong>Test Startup:</strong> Spring Boot inicia com profile "test"</li>
 *   <li><strong>Config Loading:</strong> TestConfig é instantiado</li>
 *   <li><strong>Database Init:</strong> instanciaDB() popula banco automaticamente</li>
 *   <li><strong>Test Execution:</strong> Testes executam com dados conhecidos</li>
 *   <li><strong>Teardown:</strong> Banco é limpo para próximo teste</li>
 * </ol>
 * 
 * <h3>⚙️ Configurações Típicas de Test</h3>
 * <pre>
 * # application-test.properties
 * spring.datasource.url=jdbc:h2:mem:testdb
 * spring.jpa.hibernate.ddl-auto=create-drop
 * spring.h2.console.enabled=false
 * spring.jpa.show-sql=false
 * logging.level.org.springframework.web=debug
 * </pre>
 * 
 * <h3>🆚 Diferenças vs DevConfig</h3>
 * <table>
 *   <tr><th>Aspecto</th><th>DevConfig</th><th>TestConfig</th></tr>
 *   <tr><td>Inicialização</td><td>Condicional</td><td>Sempre</td></tr>
 *   <tr><td>Profile</td><td>"Dev"</td><td>"test"</td></tr>
 *   <tr><td>Banco</td><td>Persistente</td><td>In-memory</td></tr>
 *   <tr><td>Performance</td><td>Desenvolvimento</td><td>Velocidade</td></tr>
 * </table>
 * 
 * <div style="background-color: #d4edda; border: 1px solid #c3e6cb; border-radius: 4px; padding: 8px; margin: 8px 0;">
 * <strong>✅ Vantagens para Testes:</strong>
 * <ul>
 *   <li>Dados sempre consistentes e conhecidos</li>
 *   <li>Testes reproduzíveis e determinísticos</li>
 *   <li>Isolamento total entre execuções</li>
 *   <li>Não requer configuração manual</li>
 * </ul>
 * </div>
 * 
 * <div style="background-color: #d1ecf1; border: 1px solid #b8daff; border-radius: 4px; padding: 8px; margin: 8px 0;">
 * <strong>💡 Padrão de Teste:</strong>
 * Implementa o padrão "Fresh Database per Test" onde cada execução
 * de teste inicia com dados limpos e conhecidos, garantindo que testes
 * não interfiram uns com os outros.
 * </div>
 * 
 * <div style="background-color: #fff3cd; border: 1px solid #ffeaa7; border-radius: 4px; padding: 8px; margin: 8px 0;">
 * <strong>⚠️ Performance:</strong>
 * A inicialização automática do banco pode impactar performance de testes.
 * Para testes unitários puros, considere usar mocks ao invés de banco real
 * para máxima velocidade de execução.
 * </div>
 * 
 * @author Sistema Helpdesk
 * @since 1.0.0
 * @see Configuration
 * @see Profile
 * @see DBService
 * @see DevConfig
 */
@Configuration
@Profile("test")
public class TestConfig {
	
	/**
	 * <h3>Serviço de Inicialização para Testes</h3>
	 * <p>
	 * Serviço especializado responsável por popular o banco de dados
	 * com conjunto padrão de dados para execução de testes. Garante
	 * que todos os testes tenham acesso aos mesmos dados base,
	 * proporcionando execuções consistentes e previsíveis.
	 * </p>
	 * 
	 * <h4>📊 Dados para Testes:</h4>
	 * <ul>
	 *   <li><strong>Admin Padrão:</strong> Para testes de funcionalidades administrativas</li>
	 *   <li><strong>Técnico Teste:</strong> Para testes de atendimento e chamados</li>
	 *   <li><strong>Cliente Teste:</strong> Para testes de abertura de chamados</li>
	 *   <li><strong>Chamados Variados:</strong> Diferentes status e prioridades</li>
	 * </ul>
	 * 
	 * <h4>🧪 Características dos Dados de Teste:</h4>
	 * <ul>
	 *   <li><strong>Determinísticos:</strong> Sempre os mesmos IDs e valores</li>
	 *   <li><strong>Completos:</strong> Cobrem todos os cenários principais</li>
	 *   <li><strong>Válidos:</strong> Passam todas as validações de domínio</li>
	 *   <li><strong>Isolados:</strong> Não dependem de dados externos</li>
	 * </ul>
	 * 
	 * <div style="background-color: #d1ecf1; border: 1px solid #b8daff; border-radius: 4px; padding: 8px; margin: 8px 0;">
	 * <strong>💡 Injeção Automática:</strong>
	 * O Spring resolve automaticamente a dependência do DBService,
	 * garantindo que todas as suas dependências (repositories, etc.)
	 * estejam disponíveis durante a inicialização.
	 * </div>
	 * 
	 * @see DBService#instanciaDB()
	 * @see Autowired
	 */
	@Autowired
	private DBService dbService;
	
	/**
	 * <h3>🧪 Bean de Inicialização para Testes</h3>
	 * <p>
	 * Bean de configuração que executa SEMPRE a inicialização do banco
	 * de dados quando o profile "test" está ativo. Diferentemente da
	 * configuração de desenvolvimento, não há condições - a inicialização
	 * é mandatória para garantir ambiente de teste consistente.
	 * </p>
	 * 
	 * <h4>🔄 Execução Garantida:</h4>
	 * <p>
	 * Este método é executado automaticamente pelo Spring durante a
	 * inicialização do contexto de aplicação em testes, garantindo
	 * que o banco sempre esteja populado com dados conhecidos.
	 * </p>
	 * 
	 * <h4>📋 Vantagens da Abordagem:</h4>
	 * <ul>
	 *   <li><strong>Simplicity:</strong> Sem condições complexas</li>
	 *   <li><strong>Consistency:</strong> Sempre executa, sempre igual</li>
	 *   <li><strong>Reliability:</strong> Testes nunca falham por falta de dados</li>
	 *   <li><strong>Isolation:</strong> Cada execução tem dados limpos</li>
	 * </ul>
	 * 
	 * <h4>🎯 Cenários de Teste Suportados:</h4>
	 * <ul>
	 *   <li><strong>@SpringBootTest:</strong> Testes de integração completos</li>
	 *   <li><strong>@DataJpaTest:</strong> Testes focados na camada de dados</li>
	 *   <li><strong>@WebMvcTest:</strong> Testes de controllers (com mocks)</li>
	 *   <li><strong>Custom Test Slices:</strong> Testes específicos de camadas</li>
	 * </ul>
	 * 
	 * <h4>⚡ Fluxo de Dados em Testes:</h4>
	 * <ol>
	 *   <li><strong>Test Context Init:</strong> Spring carrega contexto de teste</li>
	 *   <li><strong>Profile Activation:</strong> Profile "test" ativo</li>
	 *   <li><strong>Bean Creation:</strong> TestConfig instanciado</li>
	 *   <li><strong>Database Population:</strong> instanciaDB() executado</li>
	 *   <li><strong>Test Ready:</strong> Ambiente pronto para testes</li>
	 * </ol>
	 * 
	 * <div style="background-color: #d4edda; border: 1px solid #c3e6cb; border-radius: 4px; padding: 8px; margin: 8px 0;">
	 * <strong>✅ Benefícios para Qualidade:</strong>
	 * <ul>
	 *   <li>Testes determinísticos e reproduzíveis</li>
	 *   <li>Cobertura completa de cenários de dados</li>
	 *   <li>Facilita debug de falhas de teste</li>
	 *   <li>Reduz complexidade de setup em testes</li>
	 * </ul>
	 * </div>
	 * 
	 * <div style="background-color: #d1ecf1; border: 1px solid #b8daff; border-radius: 4px; padding: 8px; margin: 8px 0;">
	 * <strong>💡 Padrão Clean State:</strong>
	 * Cada execução de teste inicia com estado conhecido e limpo,
	 * implementando o padrão "Fresh Fixture" que garante isolamento
	 * e previsibilidade entre diferentes execuções de teste.
	 * </div>
	 * 
	 * <div style="background-color: #fff3cd; border: 1px solid #ffeaa7; border-radius: 4px; padding: 8px; margin: 8px 0;">
	 * <strong>⚠️ Consideração de Performance:</strong>
	 * A inicialização do banco em cada teste pode impactar performance.
	 * Para testes que não precisam de dados completos, considere usar
	 * @DirtiesContext ou dados específicos via @Sql.
	 * </div>
	 * 
	 * @see Bean
	 * @see DBService#instanciaDB()
	 * @see Profile
	 * @see SpringBootTest
	 */
	@Bean
	public void instanciaDB () {
		this.dbService.instanciaDB();
	}
}
