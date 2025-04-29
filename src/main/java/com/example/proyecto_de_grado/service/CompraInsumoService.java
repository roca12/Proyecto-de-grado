package com.example.proyecto_de_grado.service;

import com.example.proyecto_de_grado.model.entity.CompraInsumo;
import com.example.proyecto_de_grado.model.entity.Insumo;
import com.example.proyecto_de_grado.repository.CompraInsumoRepository;
import com.example.proyecto_de_grado.repository.HistorialInsumoRepository;
import com.example.proyecto_de_grado.repository.InsumoRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servicio encargado de gestionar las compras de insumos en el sistema.
 *
 * <p>Esta clase contiene métodos para registrar compras de insumos, obtener las compras realizadas,
 * y actualizar las cantidades disponibles de los insumos.
 *
 * <p>Autor: Anderson Zuluaga
 */
@Service
public class CompraInsumoService {

  @Autowired private CompraInsumoRepository compraInsumoRepository;

  @Autowired private InsumoRepository insumoRepository;

  @Autowired private HistorialInsumoRepository historialInsumoRepository;

  /**
   * Obtiene todas las compras de insumos registradas.
   *
   * @return Una lista con todas las compras de insumos.
   */
  public List<CompraInsumo> getAllCompras() {
    return compraInsumoRepository.findAll();
  }

  /**
   * Obtiene todas las compras realizadas para un insumo específico.
   *
   * @param idInsumo El ID del insumo.
   * @return Una lista con las compras asociadas al insumo.
   */
  public List<CompraInsumo> getComprasByInsumo(int idInsumo) {
    return compraInsumoRepository.findByInsumoIdInsumo(idInsumo);
  }

  /**
   * Obtiene todas las compras realizadas por un proveedor específico.
   *
   * @param idProveedor El ID del proveedor.
   * @return Una lista con las compras realizadas por el proveedor.
   */
  public List<CompraInsumo> getComprasByProveedor(int idProveedor) {
    return compraInsumoRepository.findByProveedorIdProveedor(idProveedor);
  }

  /**
   * Guarda una nueva compra de insumo y actualiza la cantidad disponible del insumo.
   *
   * <p>Este método primero verifica si el insumo existe. Si es así, se actualiza la cantidad
   * disponible del insumo sumando la cantidad comprada. Luego, se guarda la compra en la base de
   * datos.
   *
   * @param compra La compra de insumo a guardar.
   * @throws IllegalArgumentException Si el insumo no existe en la base de datos.
   */
  public void saveCompra(CompraInsumo compra) {
    Optional<Insumo> optionalInsumo = insumoRepository.findById(compra.getInsumo().getIdInsumo());
    if (optionalInsumo.isPresent()) {
      Insumo insumo = optionalInsumo.get();
      BigDecimal nuevaCantidad = insumo.getCantidadDisponible().add(compra.getCantidad());
      insumo.setCantidadDisponible(nuevaCantidad);
      insumoRepository.save(insumo);

      compraInsumoRepository.save(compra);
    } else {
      throw new IllegalArgumentException("El insumo especificado no existe.");
    }
  }
}
