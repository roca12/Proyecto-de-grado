package com.example.proyecto_de_grado.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "proveedor")
public class Proveedor {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_proveedor")
  private int idProveedor;

  @Column(name = "nombre", length = 100, nullable = false)
  private String nombre;

  @Column(name = "contacto", length = 100)
  private String contacto;
}
