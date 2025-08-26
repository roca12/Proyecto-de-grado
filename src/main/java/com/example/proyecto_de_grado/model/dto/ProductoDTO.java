package com.example.proyecto_de_grado.model.dto;

import com.example.proyecto_de_grado.model.entity.UnidadMedida;
import lombok.Data;

/**
 * DTO para la entidad Producto. Representa la información básica de un producto. * @author Anderson
 * Zuluaga * @version 1.0 * @since 2023
 */
@Data
public class ProductoDTO {

  /** Identificador único del producto. */
  private Integer idProducto;

  private Integer idFinca;

  /** Nombre descriptivo del producto. */
  private String nombre;

  /** Descripción detallada del producto. */
  private String descripcion;

  /** Unidad de medida del producto (Kg, Unidad, Caja). */
  private UnidadMedida unidadMedida;
}
