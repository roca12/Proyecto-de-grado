package com.example.proyecto_de_grado.model.entity;

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
  /** Pago en efectivo - dinero físico */
  Efectivo,

  /** Pago con tarjeta de crédito o débito */
  Tarjeta,

  /** Pago mediante transferencia bancaria electrónica */
  Transferencia,

  /** Otros métodos de pago no especificados anteriormente */
  Otro
}
