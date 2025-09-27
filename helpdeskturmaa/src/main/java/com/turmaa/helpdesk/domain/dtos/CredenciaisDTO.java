package com.turmaa.helpdesk.domain.dtos;

/**
 * <h1>Data Transfer Object (DTO) para Credenciais de Autenticação</h1>
 * <p>
 * Classe responsável pela transferência segura de credenciais de login entre
 * o cliente e o servidor durante o processo de autenticação. Esta classe
 * encapsula as informações necessárias para validar a identidade do usuário
 * no sistema de helpdesk.
 * </p>
 * 
 * <h2>Principais Características:</h2>
 * <ul>
 *   <li><strong>Simplicidade:</strong> Contém apenas os campos essenciais para login</li>
 *   <li><strong>Segurança:</strong> Projetada especificamente para tráfego de credenciais</li>
 *   <li><strong>Flexibilidade:</strong> Suporta diferentes tipos de usuário (Técnico/Cliente)</li>
 *   <li><strong>Eficiência:</strong> Estrutura mínima para otimização de transferência</li>
 * </ul>
 * 
 * <h2>Uso no Sistema:</h2>
 * <ul>
 *   <li><strong>Endpoint de Login:</strong> Recebe credenciais via POST</li>
 *   <li><strong>Autenticação JWT:</strong> Validação para geração de tokens</li>
 *   <li><strong>Validação de Sessão:</strong> Verificação de identidade</li>
 * </ul>
 * 
 * <h2>Considerações de Segurança:</h2>
 * <p>
 * <strong>IMPORTANTE:</strong> Esta classe trafega senhas em texto plano durante
 * a autenticação inicial. É crucial que:
 * </p>
 * <ul>
 *   <li>Seja utilizada apenas sobre conexões HTTPS</li>
 *   <li>As senhas sejam imediatamente validadas e não armazenadas</li>
 *   <li>Não seja logada ou exposta em stacktraces</li>
 *   <li>Seja descartada após o processo de autenticação</li>
 * </ul>
 * 
 * @author Sistema Helpdesk
 * @version 1.0
 * @since 2024
 * 
 * @see com.turmaa.helpdesk.security.JWTAuthenticationFilter
 */
public class CredenciaisDTO {
    /**
     * <h3>Email do Usuário</h3>
     * <p>
     * Endereço de email que serve como identificador único do usuário no sistema.
     * Este campo é usado como login principal tanto para técnicos quanto para clientes.
     * </p>
     * 
     * <h4>Características:</h4>
     * <ul>
     *   <li><strong>Único:</strong> Cada email só pode estar associado a uma conta</li>
     *   <li><strong>Formato:</strong> Deve seguir padrão RFC de endereços de email</li>
     *   <li><strong>Case-insensitive:</strong> Validação ignora maiúsculas/minúsculas</li>
     *   <li><strong>Obrigatório:</strong> Campo essencial para autenticação</li>
     * </ul>
     * 
     * <h4>Validações Aplicadas:</h4>
     * <ul>
     *   <li>Formato de email válido</li>
     *   <li>Existência na base de dados</li>
     *   <li>Status ativo da conta</li>
     * </ul>
     */
    private String email;
    
    /**
     * <h3>Senha do Usuário</h3>
     * <p>
     * Senha em texto plano fornecida pelo usuário durante o processo de autenticação.
     * Este campo é temporário e deve ser imediatamente processado (hash/validação)
     * e nunca armazenado em sua forma original.
     * </p>
     * 
     * <h4>Características:</h4>
     * <ul>
     *   <li><strong>Temporária:</strong> Existe apenas durante a validação</li>
     *   <li><strong>Sensível:</strong> Contém informação crítica de segurança</li>
     *   <li><strong>Validação:</strong> Comparada com hash BCrypt armazenado</li>
     *   <li><strong>Descartável:</strong> Deve ser limpa após uso</li>
     * </ul>
     * 
     * <h4>Fluxo de Validação:</h4>
     * <ol>
     *   <li>Recebida em texto plano via HTTPS</li>
     *   <li>Comparada com hash BCrypt do banco</li>
     *   <li>Se válida, gera token JWT</li>
     *   <li>Objeto é descartado imediatamente</li>
     * </ol>
     * 
     * <h4>⚠️ CUIDADOS DE SEGURANÇA:</h4>
     * <ul>
     *   <li>Nunca logar este campo</li>
     *   <li>Não incluir em stacktraces</li>
     *   <li>Não serializar para logs ou cache</li>
     *   <li>Processar imediatamente após recebimento</li>
     * </ul>
     */
    private String senha;

    /**
     * <h3>Obter Email</h3>
     * <p>
     * Retorna o endereço de email do usuário utilizado para autenticação.
     * </p>
     * 
     * @return {@link String} Email do usuário ou null se não definido
     */
    public String getEmail() {
        return email;
    }

    /**
     * <h3>Definir Email</h3>
     * <p>
     * Define o endereço de email do usuário para o processo de autenticação.
     * O email será utilizado para localizar o usuário na base de dados.
     * </p>
     * 
     * @param email {@link String} Endereço de email válido do usuário
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * <h3>Obter Senha</h3>
     * <p>
     * Retorna a senha em texto plano fornecida pelo usuário.
     * </p>
     * 
     * <h4>⚠️ ATENÇÃO SEGURANÇA:</h4>
     * <p>
     * Este método retorna informação extremamente sensível. Use apenas para
     * validação imediata durante o processo de autenticação. Nunca armazene,
     * logue ou exponha o valor retornado.
     * </p>
     * 
     * @return {@link String} Senha em texto plano ou null se não definida
     */
    public String getSenha() {
        return senha;
    }

    /**
     * <h3>Definir Senha</h3>
     * <p>
     * Define a senha em texto plano do usuário para validação de autenticação.
     * </p>
     * 
     * <h4>⚠️ ATENÇÃO SEGURANÇA:</h4>
     * <p>
     * Este método recebe informação extremamente sensível. A senha deve ser:
     * </p>
     * <ul>
     *   <li>Validada imediatamente contra o hash armazenado</li>
     *   <li>Nunca armazenada em sua forma original</li>
     *   <li>Descartada após o processo de autenticação</li>
     * </ul>
     * 
     * @param senha {@link String} Senha em texto plano fornecida pelo usuário
     */
    public void setSenha(String senha) {
        this.senha = senha;
    }
}
