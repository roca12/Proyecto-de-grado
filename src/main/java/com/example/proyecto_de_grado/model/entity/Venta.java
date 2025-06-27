package com.example.proyecto_de_grado.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa una venta realizada en el sistema.
 * Contiene información sobre el cliente, el empleado, la fecha, el método de pago,
 * el total y los detalles asociados a la venta.
 *
 * @author Anderson Zuluaga
 */
@Entity
@Table(name = "venta")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Venta {

    /**
     * Identificador único de la venta.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta")
    private Integer idVenta;

    /**
     * Identificador del cliente que realiza la compra.
     */
    @Column(name = "id_cliente", nullable = false)
    private Integer idCliente;

    /**
     * Identificador del empleado que registra la venta.
     */
    @Column(name = "id_empleado", nullable = false)
    private Integer idEmpleado;

    /**
     * Fecha y hora en que se realiza la venta.
     */
    @Column(name = "fecha_venta", nullable = false)
    private LocalDateTime fechaVenta;

    /**
     * Método de pago utilizado en la venta.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false)
    private MetodoPago metodoPago;

    /**
     * Total de la venta. Se calcula a partir de los detalles o se asigna manualmente.
     */
    @Column(name = "total", precision = 10, scale = 2, nullable = false)
    private BigDecimal total = BigDecimal.ZERO;

    /**
     * Identificador del método de pago (si aplica).
     */
    @Column(name = "id_metodo_pago")
    private Integer idMetodoPago;

    /**
     * Lista de detalles asociados a esta venta.
     * Cada detalle representa un producto o servicio vendido.
     */
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetalleVenta> detalles = new ArrayList<>();

    /**
     * Cliente asociado a la venta (relación de muchos a uno).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", insertable = false, updatable = false)
    private Cliente cliente;

    /**
     * Constructor utilizado para crear una nueva venta sin ID.
     *
     * @param idCliente     ID del cliente
     * @param idEmpleado    ID del empleado
     * @param fechaVenta    Fecha y hora de la venta
     * @param metodoPago    Método de pago utilizado
     * @param total         Total de la venta (opcional)
     * @param idMetodoPago  ID del método de pago (si aplica)
     */
    public Venta(Integer idCliente, Integer idEmpleado, LocalDateTime fechaVenta,
                 MetodoPago metodoPago, BigDecimal total, Integer idMetodoPago) {
        this.idCliente = idCliente;
        this.idEmpleado = idEmpleado;
        this.fechaVenta = fechaVenta;
        this.metodoPago = metodoPago;
        this.total = total != null ? total : BigDecimal.ZERO;
        this.idMetodoPago = idMetodoPago;
    }

    /**
     * Agrega un nuevo detalle a la venta y establece la relación bidireccional.
     *
     * @param detalle Detalle de venta a agregar
     */
    public void agregarDetalle(DetalleVenta detalle) {
        detalles.add(detalle);
        detalle.setVenta(this);
    }

    /**
     * Calcula el total de la venta sumando los subtotales de los detalles.
     */
    public void calcularTotalDesdeDetalles() {
        this.total = detalles.stream()
                .map(DetalleVenta::getSubtotal)
                .filter(subtotal -> subtotal != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
