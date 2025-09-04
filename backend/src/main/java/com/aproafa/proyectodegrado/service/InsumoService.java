package com.aproafa.proyectodegrado.service;

import com.aproafa.proyectodegrado.model.entity.HistorialInsumo;
import com.aproafa.proyectodegrado.model.entity.Insumo;
import com.aproafa.proyectodegrado.repository.HistorialInsumoRepository;
import com.aproafa.proyectodegrado.repository.InsumoRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servicio encargado de gestionar las operaciones relacionadas con los insumos.
 *
 * <p>Esta clase permite crear, actualizar, eliminar insumos, así como gestionar su historial y
 * verificar los insumos con stock bajo.
 *
 * <p>Autor: Anderson Zuluaga
 */
@Service
public class InsumoService {

  @Autowired private InsumoRepository insumoRepository;

  @Autowired private HistorialInsumoRepository historialInsumoRepository;

  /**
   * Obtiene todos los insumos registrados en el sistema.
   *
   * @return Una lista de todos los insumos.
   */
  public List<Insumo> getAllInsumos() {
    return insumoRepository.findAll();
  }

  /**
   * Obtiene un insumo específico a partir de su ID.
   *
   * @param id El ID del insumo.
   * @return Un Optional que contiene el insumo si existe.
   */
  public Optional<Insumo> getInsumoById(int id) {
    return insumoRepository.findById(id);
  }

  /**
   * Obtiene los insumos cuyo stock disponible es inferior a un límite especificado.
   *
   * @param limite El límite de stock para filtrar los insumos.
   * @return Una lista de insumos con stock disponible inferior al límite.
   */
  public List<Insumo> getInsumosBajosStock(BigDecimal limite) {
    return insumoRepository.findAll().stream()
        .filter(insumo -> insumo.getCantidadDisponible().compareTo(limite) < 0)
        .toList();
  }

  /**
   * Guarda un nuevo insumo o actualiza uno existente.
   *
   * @param insumo El insumo a guardar.
   * @return El insumo guardado.
   */
  public Insumo saveInsumo(Insumo insumo) {
    return insumoRepository.save(insumo);
  }

  /**
   * Elimina un insumo del sistema a partir de su ID.
   *
   * @param id El ID del insumo a eliminar.
   */
  public void deleteInsumo(int id) {
    insumoRepository.deleteById(id);
  }

  /**
   * Registra el uso de un insumo, actualizando su cantidad disponible y almacenando el evento en el
   * historial.
   *
   * @param idInsumo El ID del insumo utilizado.
   * @param cantidadUsada La cantidad de insumo utilizada.
   * @throws IllegalArgumentException Si la cantidad utilizada excede la cantidad disponible.
   */
  public void registrarUsoInsumo(int idInsumo, BigDecimal cantidadUsada) {
    Optional<Insumo> optionalInsumo = insumoRepository.findById(idInsumo);
    if (optionalInsumo.isPresent()) {
      Insumo insumo = optionalInsumo.get();
      BigDecimal nuevaCantidad = insumo.getCantidadDisponible().subtract(cantidadUsada);

      // Validar que no se use más cantidad de la disponible
      if (nuevaCantidad.compareTo(BigDecimal.ZERO) < 0) {
        throw new IllegalArgumentException("No se puede usar más insumo del disponible.");
      }

      insumo.setCantidadDisponible(nuevaCantidad);
      insumoRepository.save(insumo);

      // Registrar en el historial
      HistorialInsumo historial = new HistorialInsumo();
      historial.setInsumo(insumo);
      historial.setCantidadUtilizada(cantidadUsada);
      historial.setFechaUso(LocalDateTime.now());
      historialInsumoRepository.save(historial);
    }
  }

  /**
   * Obtiene el historial de uso de un insumo específico.
   *
   * @param idInsumo El ID del insumo.
   * @return Una lista de registros en el historial de uso del insumo.
   */
  public List<HistorialInsumo> getHistorialInsumo(int idInsumo) {
    return historialInsumoRepository.findByInsumoIdInsumo(idInsumo);
  }

  public List<Insumo> listarPorFinca(Integer idFinca) {
    return insumoRepository.findByFinca_Id(idFinca);
  }
}
