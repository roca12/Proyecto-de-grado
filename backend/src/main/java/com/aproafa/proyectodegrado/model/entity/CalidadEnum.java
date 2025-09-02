package com.aproafa.proyectodegrado.model.entity;

/**
 * Enumeración que representa los niveles de calidad posibles para un producto agrícola, lote o
 * insumo dentro del sistema.
 *
 * <ul>
 *   <li>{@code EXCELENTE}: Calidad óptima, sin defectos ni deterioros visibles.
 *   <li>{@code BUENA}: Buen estado general con mínimas imperfecciones.
 *   <li>{@code REGULAR}: Condiciones aceptables pero con varias observaciones o defectos menores.
 *   <li>{@code MALA}: Producto en mal estado, con defectos graves o no apto para el uso esperado.
 * </ul>
 *
 * Este enum permite clasificar y evaluar la calidad durante procesos como la recepción de insumos,
 * cosecha, almacenamiento o despacho.
 */
public enum CalidadEnum {
  EXCELENTE,
  BUENA,
  REGULAR,
  MALA
}
