package com.example.proyecto_de_grado.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * DTO (Data Transfer Object) para la gestión de ventas.
 *
 * <p>Esta clase representa la información básica de una venta del sistema, incluyendo datos
 * esenciales de la transacción como cliente, empleado, fecha, método de pago y total.
 * Facilita la transferencia de datos entre capas de la aplicación, especialmente en operaciones REST.
 *
 * <p>Se basa en la estructura de la tabla 'venta' y contiene únicamente los campos principales
 * sin relaciones complejas para optimizar el rendimiento.
 *
 * @author Anderson Zuluaga
 * @version 1.0
 * @since 2023
 */
@Data
public class VentaDTO {

    /** Identificador único de la venta en el sistema. */
    private Integer idVenta;

    /** Identificador del cliente que realizó la compra. */
    private Integer idCliente;

    /** Identificador del empleado que procesó la venta. */
    private Integer idEmpleado;

    /**
     * Fecha y hora cuando se realizó la venta.
     * Formato: YYYY-MM-DD HH:MM:SS
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaVenta;

    /**
     * Método de pago utilizado en la venta.
     * Valores posibles: "Efectivo", "Tarjeta", "Transferencia", "Otro"
     */
    private String metodoPago;

    /** Monto total de la venta con precisión decimal (10,2). */
    private BigDecimal total;

    /** Identificador específico del método de pago utilizado. */
    private Integer idMetodoPago;
}