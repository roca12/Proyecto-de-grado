package com.example.proyecto_de_grado.model.dto;

import lombok.Data;
import java.time.LocalDate;

/**
 * DTO (Data Transfer Object) para la gestión de clientes.
 *
 * <p>Esta clase combina información del cliente con datos personales básicos
 * para facilitar la transferencia de datos entre capas de la aplicación,
 * especialmente en operaciones REST.</p>
 *
 * <p>Incluye tanto atributos específicos de cliente como campos heredados
 * de la entidad Persona.</p>
 *
 * @author Anderson Zuluaga
 * @version 1.0
 * @since 2023
 */
@Data
public class ClienteDTO {
    /**
     * Identificador único del cliente en el sistema.
     */
    private Integer idCliente;

    /**
     * Tipo de cliente (Ej: "Minorista", "Mayorista", "Corporativo").
     */
    private String tipoCliente;

    /**
     * Fecha de registro del cliente en el sistema.
     * Formato: YYYY-MM-DD
     */
    private LocalDate fechaRegistro;

    // Campos heredados de Persona

    /**
     * Identificador único de la persona asociada al cliente.
     */
    private Integer idPersona;

    /**
     * Primer nombre del cliente.
     */
    private String nombre;

    /**
     * Apellido(s) del cliente.
     */
    private String apellido;

    /**
     * Tipo de identificación (1: Cédula, 2: Pasaporte, etc.).
     */
    private int tipoId;

    /**
     * Número de identificación del cliente.
     */
    private String numeroIdentificacion;

    /**
     * Correo electrónico de contacto del cliente.
     */
    private String email;

    /**
     * Número telefónico de contacto del cliente.
     */
    private String telefono;

    /**
     * Dirección física del cliente.
     */
    private String direccion;

    /**
     * Identificador de la finca asociada al cliente (opcional).
     * Puede ser nulo si el cliente no tiene finca asignada.
     */
    private Integer idFinca;
}