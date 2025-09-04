package com.aproafa.proyectodegrado.model.dto;

import lombok.Data;

/**
 * DTO (Data Transfer Object) para la gestión de proveedores.
 *
 * <p>Esta clase combina información del proveedor con datos personales básicos para facilitar la
 * transferencia de datos entre capas de la aplicación.
 *
 * <p>Incluye tanto atributos específicos de proveedor como campos heredados de la entidad Persona.
 *
 * @author Anderson Zuluaga
 * @version 1.0
 * @since 2023
 */
@Data
public class ProveedorDTO {

  /** Identificador único del proveedor. */
  private Integer idProveedor;

  /** Nombre del proveedor (o razón social si aplica). */
  private String nombre;

  /** Información adicional de contacto (puede ser nombre del encargado, teléfono alterno, etc.). */
  private String contacto;

  // Campos heredados de Persona

  /** Identificador único de la persona asociada al proveedor. */
  private Integer idPersona;

  /** Primer nombre del proveedor como persona natural (si aplica). */
  private String nombrePersona;

  /** Apellido(s) del proveedor como persona natural. */
  private String apellido;

  /** Tipo de identificación (1: Cédula, 2: Pasaporte, etc.). */
  private int tipoId;

  /** Número de identificación del proveedor. */
  private String numeroIdentificacion;

  /** Correo electrónico del proveedor. */
  private String email;

  /** Teléfono del proveedor. */
  private String telefono;

  /** Dirección del proveedor. */
  private String direccion;

  /** Identificador de la finca asociada (puede ser nulo). */
  private Integer idFinca;
}
