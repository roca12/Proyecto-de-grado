package com.aproafa.proyectodegrado.model.entity;

/**
 * Enumeración que representa los distintos estados posibles en los que puede encontrarse una
 * producción agrícola.
 *
 * <ul>
 *   <li>{@code EN_CRECIMIENTO}: La producción está en etapa de desarrollo o cultivo.
 *   <li>{@code LISTO_PARA_COSECHA}: El producto ha alcanzado su madurez y está listo para ser
 *       cosechado.
 *   <li>{@code COSECHADO}: El producto ya fue recolectado.
 * </ul>
 *
 * Esta enumeración puede utilizarse para controlar el flujo de trabajo en la gestión de cultivos y
 * trazabilidad en el sistema.
 */
public enum EstadoProduccion {
  EN_CRECIMIENTO,
  LISTO_PARA_COSECHA,
  COSECHADO
}
