package com.example.proyecto_de_grado.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración de seguridad de la aplicación utilizando Spring Security.
 *
 * <p>Esta clase configura la seguridad de la aplicación, incluyendo la autenticación de usuarios
 * mediante JWT, la autorización basada en roles y la gestión de sesiones sin estado (stateless).
 *
 * <p>Se utiliza un filtro personalizado para verificar el JWT en cada solicitud, y se configuran
 * las reglas de acceso a las diferentes rutas de la API.
 *
 * <p>Autor: Anderson Zuluaga
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  // Filtro para procesar la solicitud y verificar el token JWT
  @Autowired private JwtRequestFilter jwtRequestFilter;

  /**
   * Configura las reglas de seguridad HTTP.
   *
   * @param http La configuración de seguridad HTTP.
   * @return La cadena de configuración de seguridad.
   * @throws Exception si ocurre algún error durante la configuración.
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable()) // Deshabilita la protección CSRF
        .authorizeHttpRequests(
            auth ->
                auth
                    // Permite acceso sin autenticación a estas rutas
                    .requestMatchers(
                        "/api/usuarios/login", "/api/usuarios", "/api/usuarios/register")
                    .permitAll()
                    // Rutas de actividades accesibles solo por usuarios con ciertos roles
                    .requestMatchers("/api/actividades/**")
                    .hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
                    // Rutas de administración solo accesibles por administradores
                    .requestMatchers("/api/usuarios/admin/**")
                    .hasAnyAuthority("ROLE_ADMIN")
                    // El resto de las solicitudes requiere autenticación
                    .anyRequest()
                    .authenticated())
        // Configura la aplicación para usar sesiones sin estado (stateless)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    // Agrega el filtro JWT antes del filtro de autenticación por usuario/contraseña
    http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  /**
   * Configura un codificador de contraseñas personalizado. Este codificador no aplica un hash real,
   * solo compara la contraseña sin cifrar.
   *
   * @return El codificador de contraseñas.
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new PasswordEncoder() {
      @Override
      public String encode(CharSequence rawPassword) {
        return rawPassword.toString(); // No realiza ninguna codificación
      }

      @Override
      public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return rawPassword
            .toString()
            .equals(encodedPassword); // Compara las contraseñas de manera simple
      }
    };
  }

  /**
   * Configura el administrador de autenticación.
   *
   * @param authConfig La configuración de autenticación.
   * @return El administrador de autenticación.
   * @throws Exception si ocurre algún error durante la configuración.
   */
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig)
      throws Exception {
    return authConfig.getAuthenticationManager();
  }
}
