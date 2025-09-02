package com.aproafa.proyectodegrado.model.entity;

/**
 * Enumeración que define los métodos de pago disponibles en el sistema. Proporciona las opciones de
 * pago que pueden ser utilizadas en las transacciones de venta, garantizando consistencia en los
 * datos almacenados.
 *
 * @author Anderson Zuluaga
 * @version 1.0
 * @since 1.0
 */
public enum MetodoPago {
  Efectivo,
  Tarjeta,
  Transferencia,
  Otro;

  // Método para convertir desde String
  public static MetodoPago fromString(String value) {
    try {
      return MetodoPago.valueOf(value.toUpperCase());
    } catch (IllegalArgumentException e) {
      // Si falla, intentar con formato capitalizado
      String capitalized = value.substring(0, 1).toUpperCase() + value.substring(1).toLowerCase();
      return MetodoPago.valueOf(capitalized);
    }
  }
}
