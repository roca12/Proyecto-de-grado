package com.aproafa.proyectodegrado;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración global de CORS (Cross-Origin Resource Sharing) para la aplicación.
 *
 * <p>Esta clase permite que el frontend (por ejemplo, una aplicación React corriendo en
 * http://localhost:3000) se comunique con el backend Spring Boot, permitiendo el acceso a los
 * endpoints definidos.
 */
@Configuration
public class CorsConfig {

  /**
   * Define y registra una configuración personalizada de CORS.
   *
   * @return una instancia de {@link WebMvcConfigurer} con la configuración de CORS aplicada.
   */
  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      /**
       * Configura los mapeos de CORS permitiendo solicitudes desde el origen http://localhost:3000.
       * Se permiten los métodos HTTP GET, POST, PUT, DELETE y OPTIONS, con cualquier encabezado.
       * También se permite el uso de credenciales (cookies, headers de autenticación, etc.) y se
       * establece un tiempo máximo de cache para la política CORS de 3600 segundos.
       *
       * @param registry el registro de configuraciones de CORS al que se añaden las reglas.
       */
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry
            .addMapping("/**")
            .allowedOriginPatterns("http://localhost:3000")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
      }
    };
  }
}
