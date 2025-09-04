package com.aproafa.proyectodegrado.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

/**
 * DTO (Data Transfer Object) para la gestión de empleados.
 *
 * <p>Esta clase combina información del empleado con datos personales básicos para facilitar la
 * transferencia de datos entre capas de la aplicación, especialmente en operaciones REST.
 *
 * <p>Incluye tanto atributos específicos de empleado como campos heredados de la entidad Persona.
 *
 * @author Anderson Zuluaga
 * @version 1.0
 * @since 2023
 */
@Data
public class EmpleadoDTO {
  /** Identificador único del empleado en el sistema. */
  private Integer idEmpleado;

  /** Cargo del empleado dentro de la organización. */
  private String cargo;

  /** Salario del empleado. */
  private BigDecimal salario;

  /** Fecha de contratación del empleado en el sistema. Formato: YYYY-MM-DD */
  private LocalDate fechaContratacion;

  // Campos heredados de Persona

  /** Identificador único de la persona asociada al empleado. */
  private Integer idPersona;

  /** Primer nombre del empleado. */
  private String nombre;

  /** Apellido(s) del empleado. */
  private String apellido;

  /** Tipo de identificación (1: Cédula, 2: Pasaporte, etc.). */
  private int tipoId;

  /** Número de identificación del empleado. */
  private String numeroIdentificacion;

  /** Correo electrónico de contacto del empleado. */
  private String email;

  /** Número telefónico de contacto del empleado. */
  private String telefono;

  /** Dirección física del empleado. */
  private String direccion;

  /** Identificador de la finca asociada al empleado. Indica la finca donde trabaja el empleado. */
  private Integer idFinca;
}
