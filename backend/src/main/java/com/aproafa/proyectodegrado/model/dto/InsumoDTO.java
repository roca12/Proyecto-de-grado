package com.aproafa.proyectodegrado.model.dto;

import com.aproafa.proyectodegrado.model.entity.UnidadMedida;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Data;

/**
 * DTO para la entidad Insumo. Se usa para recibir datos desde el cliente sin exponer directamente
 * la entidad persistente.
 */
@Data
public class InsumoDTO {

  @NotBlank(message = "El nombre es obligatorio.")
  @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres.")
  private String nombre;

  @Size(max = 1000, message = "La descripción no puede superar los 1000 caracteres.")
  private String descripcion;

  @NotNull(message = "La unidad de medida es obligatoria.")
  private UnidadMedida unidadMedida; // Debe ser 'Kg', 'Litro', 'Bolsa' o 'Unidad'

  @NotNull(message = "El ID del proveedor es obligatorio.")
  private Integer idProveedor;

  @NotNull(message = "La cantidad disponible es obligatoria.")
  @DecimalMin(value = "0.0", inclusive = true, message = "La cantidad no puede ser negativa.")
  private BigDecimal cantidadDisponible;

  private Integer idFinca;
}
