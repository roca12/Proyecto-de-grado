package com.example.proyecto_de_grado.model.dto;

import lombok.Data;

/**
 * DTO para la solicitud de registro de un nuevo usuario en el sistema.
 *
 * <p>Contiene tanto los datos personales como la información necesaria
 * para la creación de la cuenta de usuario.</p>
 *
 * <p>Se utiliza principalmente en el proceso de registro inicial.</p>
 *
 * <p>Autor: Anderson Zuluaga</p>
 */
@Data
public class RegistroRequest {

    // Datos de persona

    /** Nombre de la persona. */
    private String nombre;

    /** Apellido de la persona. */
    private String apellido;

    /** Tipo de identificación (por ejemplo, 1 para cédula, 2 para pasaporte, etc.). */
    private int tipoId;

    /** Número de identificación de la persona. */
    private String numeroIdentificacion;

    /** Correo electrónico de contacto. */
    private String email;

    /** Número de teléfono de contacto. */
    private String telefono;

    /** Dirección de residencia. */
    private String direccion;

    // Datos de usuario

    /** Contraseña para la cuenta de usuario. */
    private String contraseña;

    /** Tipo de usuario: puede ser "ADMIN" o "USER". */
    private String tipoUsuario;

    /** ID de la finca asociada al usuario. */
    private int fincaId;
}
