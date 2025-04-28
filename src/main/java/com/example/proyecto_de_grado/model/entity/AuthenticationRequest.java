package com.example.proyecto_de_grado.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la solicitud de autenticación de usuarios.
 *
 * <p>Contiene los datos necesarios para validar las credenciales de acceso
 * en el sistema: identificador de la persona y contraseña.</p>
 *
 * <p>Autor: Anderson Zuluaga</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {

    /** Identificador de la persona que desea autenticarse. */
    private int idPersona;

    /** Contraseña asociada a la cuenta de la persona. */
    private String contraseña;
}
