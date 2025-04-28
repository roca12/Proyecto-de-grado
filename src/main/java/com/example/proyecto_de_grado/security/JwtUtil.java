package com.example.proyecto_de_grado.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Utilidad para la creación, validación y extracción de información de tokens JWT.
 *
 * <p>Esta clase proporciona métodos para generar, validar y extraer información de los tokens JWT
 * utilizados en el sistema. Se encarga de firmar el token, verificar su expiración y extraer datos
 * del token, como el nombre de usuario.
 *
 * <p>Autor: Anderson Zuluaga - Santiago Arias
 */
@Component
public class JwtUtil {

  private static final Logger logger = Logger.getLogger(JwtUtil.class.getName());

  // Clave secreta para firmar el JWT utilizando el algoritmo HS256
  private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

  // Tiempo de expiración del token, configurable desde properties (1 hora por defecto)
  @Value("${jwt.expiration:3600000}") // 1 hora por defecto
  private long jwtExpiration;

  /**
   * Genera un token JWT con la información del usuario.
   *
   * @param username El nombre de usuario para el cual se generará el token.
   * @param tipoUsuario El tipo de usuario (por ejemplo, ADMIN o USER).
   * @param idUsuario El identificador del usuario.
   * @return El token JWT generado.
   */
  public String generateToken(String username, String tipoUsuario, Integer idUsuario) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("tipo", tipoUsuario);
    claims.put("id", idUsuario);

    logger.info("Generando token para usuario: " + username);
    return createToken(claims, username);
  }

  /**
   * Crea el token JWT con los reclamos y el asunto especificado.
   *
   * @param claims Los reclamos del token (información adicional).
   * @param subject El asunto (usuario) del token.
   * @return El token JWT creado.
   */
  private String createToken(Map<String, Object> claims, String subject) {
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
        .signWith(key)
        .compact();
  }

  /**
   * Valida si el token JWT es válido.
   *
   * @param token El token JWT.
   * @param userDetails Los detalles del usuario.
   * @return true si el token es válido, false de lo contrario.
   */
  public Boolean validateToken(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    boolean isValid = (username.equals(userDetails.getUsername()) && !isTokenExpired(token));

    logger.info("Validando token para: " + username);
    logger.info("Token válido: " + isValid);

    return isValid;
  }

  /**
   * Extrae el nombre de usuario del token JWT.
   *
   * @param token El token JWT.
   * @return El nombre de usuario extraído del token.
   */
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  /**
   * Extrae la fecha de expiración del token JWT.
   *
   * @param token El token JWT.
   * @return La fecha de expiración del token.
   */
  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  /**
   * Extrae una reclamación específica del token JWT.
   *
   * @param token El token JWT.
   * @param claimsResolver La función para resolver la reclamación.
   * @param <T> El tipo de la reclamación.
   * @return El valor de la reclamación extraída.
   */
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  /**
   * Extrae todas las reclamaciones del token JWT.
   *
   * @param token El token JWT.
   * @return Las reclamaciones extraídas del token.
   */
  private Claims extractAllClaims(String token) {
    return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
  }

  /**
   * Verifica si el token JWT ha expirado.
   *
   * @param token El token JWT.
   * @return true si el token ha expirado, false de lo contrario.
   */
  private Boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }
}
