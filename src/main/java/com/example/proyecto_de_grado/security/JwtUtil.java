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

@Component
public class JwtUtil {

  private static final Logger logger = Logger.getLogger(JwtUtil.class.getName());

  private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

  @Value("${jwt.expiration:3600000}") // 1 hora por defecto
  private long jwtExpiration;

  public String generateToken(String username, String tipoUsuario, Integer idUsuario) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("tipo", tipoUsuario);
    claims.put("id", idUsuario);

    logger.info("Generando token para usuario: " + username);
    return createToken(claims, username);
  }

  private String createToken(Map<String, Object> claims, String subject) {
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
        .signWith(key)
        .compact();
  }

  public Boolean validateToken(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    boolean isValid = (username.equals(userDetails.getUsername()) && !isTokenExpired(token));

    logger.info("Validando token para: " + username);
    logger.info("Token válido: " + isValid);

    return isValid;
  }

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
  }

  private Boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }
}
