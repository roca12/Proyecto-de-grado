package com.example.proyecto_de_grado.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la respuesta del proceso de autenticación.
 *
 * <p>Contiene la información que se devuelve al cliente luego de una autenticación exitosa,
 * incluyendo el token de acceso y los datos básicos del usuario autenticado.
 *
 * <p>Autor: Anderson Zuluaga
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

  /** Token de acceso generado tras la autenticación exitosa. */
  private String token;

  /** Identificador de la persona autenticada. */
  private int idPersona;

  /** Nombre de la persona autenticada. */
  private String nombre;

  /** Apellido de la persona autenticada. */
  private String apellido;

  /** Tipo de usuario ("ADMIN" o "USER"). */
  private String tipoUsuario;
  private int idFinca;
}
