package com.example.proyecto_de_grado.model.entity;

import jakarta.persistence.*;
import java.util.List;
import lombok.Data;

/**
 * Entidad que representa un producto agrícola gestionado en la finca.
 *
 * <p>Contiene información como el nombre, descripción, unidad de medida y sus relaciones con otras
 * entidades como producción, inventario y precios.
 *
 * <p>Relaciones:
 *
 * <ul>
 *   <li>Uno a Muchos con {@link Produccion}: Un producto puede tener varias producciones.
 *   <li>Uno a Uno con {@link InventarioProducto}: Cada producto tiene un registro de inventario.
 *   <li>Uno a Muchos con {@link PrecioProducto}: Un producto puede tener varios precios históricos.
 * </ul>
 *
 * <p>Atributos:
 *
 * <ul>
 *   <li>{@code idProducto}: Identificador único del producto.
 *   <li>{@code nombre}: Nombre del producto (obligatorio, máximo 100 caracteres).
 *   <li>{@code descripcion}: Descripción detallada del producto.
 *   <li>{@code unidadMedida}: Unidad de medida usada (enum: KG, UNIDAD, CAJA).
 *   <li>{@code producciones}: Lista de producciones asociadas al producto.
 *   <li>{@code inventario}: Información de inventario para el producto.
 *   <li>{@code precios}: Lista de precios históricos del producto.
 * </ul>
 */
@Data
@Entity
@Table(name = "producto")
public class Producto {

  /** Identificador único del producto. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_producto")
  private Integer idProducto;

  /** Nombre del producto (obligatorio, hasta 100 caracteres). */
  @Column(length = 100, nullable = false)
  private String nombre;

  /** Descripción textual del producto. */
  @Column(columnDefinition = "TEXT")
  private String descripcion;

  /** Unidad de medida en la que se gestiona el producto (KG, UNIDAD, CAJA). */
  @Enumerated(EnumType.STRING)
  @Column(name = "unidad_medida", nullable = false)
  private UnidadMedida unidadMedida;

  /** Lista de producciones asociadas a este producto. */
  @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<Produccion> producciones;

  /** Registro único de inventario asociado a este producto. */
  @OneToOne(mappedBy = "producto", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private InventarioProducto inventario;

  /** Lista de precios históricos del producto. */
  @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<PrecioProducto> precios;

  /** Constructor sin-args que JPA requiere para instanciación. */
  public Producto() {}

  /**
   * Constructor de conveniencia para referenciar un producto por su ID.
   *
   * @param idProducto Identificador del producto.
   */
  public Producto(Integer idProducto) {
    this.idProducto = idProducto;
  }
}
