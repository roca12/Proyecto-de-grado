package com.example.proyecto_de_grado.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Filtro para procesar las solicitudes HTTP y verificar la validez de los tokens JWT.
 *
 * <p>Este filtro intercepta las solicitudes HTTP para extraer y validar el token JWT de la cabecera de autorización.
 * Si el token es válido, autentica al usuario en el contexto de seguridad de Spring.</p>
 *
 * <p>Autor: Anderson Zuluaga - Santiago Arias</p>
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger logger = Logger.getLogger(JwtRequestFilter.class.getName());

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Filtra la solicitud HTTP y valida el token JWT presente en la cabecera de autorización.
     *
     * @param request La solicitud HTTP.
     * @param response La respuesta HTTP.
     * @param chain La cadena de filtros a seguir.
     * @throws ServletException Si ocurre un error en el procesamiento del filtro.
     * @throws IOException Si ocurre un error al leer o escribir los flujos de la solicitud o respuesta.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        // Log para debugging
        logger.info("Procesando solicitud: " + request.getRequestURI());
        logger.info("Header de autorización: " + (authorizationHeader != null ? "presente" : "ausente"));

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
                logger.info("Usuario extraído del token: " + username);
            } catch (Exception e) {
                logger.severe("Error al extraer el nombre de usuario del token: " + e.getMessage());
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            logger.info("Nombre de usuario en UserDetails: " + userDetails.getUsername());
            logger.info("Autoridades: " + userDetails.getAuthorities());

            if (jwtUtil.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

                logger.info("Autenticación establecida para usuario: " + username);
            } else {
                logger.warning("Validación de token fallida para usuario: " + username);
            }
        }

        chain.doFilter(request, response);
    }
}
