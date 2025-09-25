package com.turmaa.helpdesk.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Utilitário para operações com JWT (JSON Web Token).
 *
 * <p>
 * Esta classe centraliza a criação, validação e extração de informações de tokens JWT
 * usados na autenticação da aplicação.
 * </p>
 *
 * <p>
 * É anotada com {@link Component}, permitindo que o Spring gerencie a sua instância e
 * possibilite a injeção em outras classes.
 * </p>
 */
@Component
public class JWTUtil {

    /**
     * Tempo de expiração do token em milissegundos.
     * <p>
     * Valor é lido da propriedade <code>jwt.expiration</code> do arquivo de configuração.
     * </p>
     */
    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * Chave secreta usada para assinar e validar tokens.
     * <p>
     * Valor é lido da propriedade <code>jwt.secret</code> do arquivo de configuração.
     * </p>
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * Gera um token JWT para o e-mail (username) informado.
     *
     * <p>
     * O token conterá:
     * <ul>
     *   <li>O e-mail do usuário como "subject".</li>
     *   <li>Data de expiração baseada no tempo atual + valor configurado em <code>expiration</code>.</li>
     *   <li>Assinatura com algoritmo HS512 e a chave secreta.</li>
     * </ul>
     * </p>
     *
     * @param email e-mail do usuário (subject do token)
     * @return token JWT assinado
     */
    public String generateToken(String email) {
        return Jwts.builder()
                   // Define o "subject" (identificação do usuário) como o e-mail
                   .setSubject(email)
                   // Define a data de expiração (agora + tempo configurado)
                   .setExpiration(new Date(System.currentTimeMillis() + expiration))
                   // Assina o token usando algoritmo HS512 e a chave secreta
                   .signWith(SignatureAlgorithm.HS512, secret.getBytes())
                   // Compacta e retorna o token em formato String
                   .compact();
    }

    /**
     * Verifica se um token é válido.
     *
     * <p>
     * Regras de validação:
     * <ul>
     *   <li>Token deve conter um subject (username).</li>
     *   <li>Token não pode estar expirado.</li>
     *   <li>Assinatura deve ser válida.</li>
     * </ul>
     * </p>
     *
     * @param token token JWT a ser validado
     * @return {@code true} se o token for válido, caso contrário {@code false}
     */
    public boolean tokenValido(String token) {
        // Obtém as claims (informações) contidas no token
        Claims claims = getClaims(token);
        if (claims != null) {
            // Subject (nome de usuário) do token
            String username = claims.getSubject();
            // Data de expiração
            Date expirationDate = claims.getExpiration();
            // Data/hora atual
            Date now = new Date(System.currentTimeMillis());

            // Token é válido se possuir subject, data de expiração
            // e a data atual for anterior à expiração
            if (username != null && expirationDate != null && now.before(expirationDate)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Obtém as {@link Claims} (informações) do token.
     *
     * <p>
     * As claims incluem dados como subject, data de emissão, data de expiração e quaisquer
     * informações adicionais que possam ter sido inseridas no token.
     * </p>
     *
     * @param token token JWT
     * @return objeto {@link Claims} com os dados do token ou {@code null} se o token for inválido
     */
    private Claims getClaims(String token) {
        try {
            // Faz o parsing do token usando a chave secreta para validar a assinatura
            return Jwts.parser()
                       .setSigningKey(secret.getBytes())
                       .parseClaimsJws(token)
                       .getBody();
        } catch (Exception e) {
            // Em caso de erro (token inválido, assinatura incorreta, expirado etc.) retorna null
            return null;
        }
    }

    /**
     * Obtém o nome de usuário (subject) a partir do token.
     *
     * <p>
     * Caso o token seja inválido ou as claims não possam ser lidas, retorna {@code null}.
     * </p>
     *
     * @param token token JWT
     * @return e-mail/username contido no subject ou {@code null} se não for possível extrair
     */
    public String getUsername(String token) {
        Claims claims = getClaims(token);
        if (claims != null) {
            return claims.getSubject();
        }
        return null;
    }
}