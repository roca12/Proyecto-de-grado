package com.example.proyecto_de_grado.model.dto;

import lombok.Data;

/**
 * Data Transfer Object (DTO) para la entidad Finca.
 *
 * <p>Esta clase es utilizada para transferir los datos de una finca entre capas de la aplicación de
 * manera segura y controlada.
 *
 * <p>Incluye información básica como el identificador de la finca, su nombre, ubicación y el
 * encargado asociado.
 *
 * @author Anderson Zuluaga
 */
@Data
public class FincaDTO {

  /** Identificador único de la finca. */
  private Integer idFinca;

  /** Nombre de la finca. */
  private String nombre;

  /** Ubicación física de la finca. */
  private String ubicacion;

  /** Identificación del usuario encargado de la finca. */
  private String Encargado; // ID del usuario encargado
}
