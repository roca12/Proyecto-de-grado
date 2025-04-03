package com.example.proyecto_de_grado.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
  private String token;
  private int idPersona;
  private String nombre;
  private String apellido;
  private String tipoUsuario;
}
