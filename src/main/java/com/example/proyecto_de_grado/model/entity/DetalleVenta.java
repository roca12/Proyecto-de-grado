package com.example.proyecto_de_grado.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * Entidad que representa el detalle de una venta en el sistema.
 * Esta clase almacena la información específica de cada producto vendido
 * dentro de una transacción de venta, incluyendo cantidad, precio unitario
 * y subtotal calculado automáticamente por la base de datos.
 *
 * <p>La clase utiliza JPA para el mapeo objeto-relacional y Lombok para
 * la generación automática de getters, setters y constructores.</p>
 *
 * <p>El subtotal se calcula automáticamente en la base de datos mediante
 * un trigger o columna generada, por lo que no debe ser modificado
 * directamente desde la aplicación.</p>
 *
 * @author Anderson Zuluaga
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "detalle_venta")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleVenta {

    /**
     * Identificador único del detalle de venta.
     * Se genera automáticamente utilizando la estrategia de identidad.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Integer idDetalle;

    /**
     * Identificador de la venta a la que pertenece este detalle.
     * Campo obligatorio que establece la relación con la venta principal.
     */
    @Column(name = "id_venta", nullable = false)
    private Integer idVenta;

    /**
     * Identificador del producto vendido en este detalle.
     * Campo obligatorio que establece la relación con el producto.
     */
    @Column(name = "id_producto", nullable = false)
    private Integer idProducto;

    /**
     * Cantidad de unidades del producto vendidas.
     * Campo obligatorio que debe ser mayor a cero.
     */
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    /**
     * Precio unitario del producto al momento de la venta.
     * Campo obligatorio con precisión de 10 dígitos y 2 decimales.
     * Representa el precio por unidad individual del producto.
     */
    @Column(name = "precio_unitario", precision = 10, scale = 2, nullable = false)
    private BigDecimal precioUnitario;

    /**
     * Subtotal calculado automáticamente por la base de datos.
     * Este campo es de solo lectura y se calcula multiplicando
     * la cantidad por el precio unitario.
     *
     * <p>No debe ser modificado directamente desde la aplicación
     * ya que es generado automáticamente por la base de datos.</p>
     */
    @Column(name = "subtotal", precision = 10, scale = 2, nullable = false,
            insertable = false, updatable = false)
    private BigDecimal subtotal;

    /**
     * Relación muchos-a-uno con la entidad Venta.
     * Carga lazy para optimizar el rendimiento.
     * Esta relación es opcional y solo se utiliza cuando se necesita
     * acceder a los datos completos de la venta.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_venta", insertable = false, updatable = false)
    private Venta venta;

    /**
     * Relación muchos-a-uno con la entidad Producto.
     * Carga lazy para optimizar el rendimiento.
     * Esta relación es opcional y solo se utiliza cuando se necesita
     * acceder a los datos completos del producto.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", insertable = false, updatable = false)
    private Producto producto;

    /**
     * Constructor personalizado para crear un nuevo detalle de venta.
     * No incluye el ID (se genera automáticamente) ni el subtotal
     * (se calcula automáticamente por la base de datos).
     *
     * @param idVenta Identificador de la venta padre
     * @param idProducto Identificador del producto vendido
     * @param cantidad Cantidad de unidades vendidas (debe ser mayor a 0)
     * @param precioUnitario Precio por unidad del producto (debe ser mayor a 0)
     *
     * @throws IllegalArgumentException si algún parámetro es nulo o inválido
     */
    public DetalleVenta(Integer idVenta, Integer idProducto, Integer cantidad,
                        BigDecimal precioUnitario) {
        this.idVenta = idVenta;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        // No establecemos subtotal - se calcula automáticamente por la BD
    }

    /**
     * Método auxiliar para calcular el subtotal en memoria.
     * Este método es útil para cálculos temporales o validaciones
     * antes de persistir la entidad en la base de datos.
     *
     * <p>Nota: Este cálculo es solo para uso en memoria. El valor
     * real del subtotal se obtiene de la base de datos donde se
     * calcula automáticamente.</p>
     *
     * @return El subtotal calculado multiplicando cantidad por precio unitario,
     *         o BigDecimal.ZERO si alguno de los valores es nulo
     *
     * @see #getSubtotal() para obtener el subtotal real de la base de datos
     */
    public BigDecimal calcularSubtotal() {
        if (this.cantidad != null && this.precioUnitario != null) {
            return this.precioUnitario.multiply(BigDecimal.valueOf(this.cantidad));
        }
        return BigDecimal.ZERO;
    }
}