package com.example.proyecto_de_grado.service;

import com.example.proyecto_de_grado.model.dto.ProduccionDTO;
import com.example.proyecto_de_grado.model.entity.EstadoProduccion;
import com.example.proyecto_de_grado.model.entity.Finca;
import com.example.proyecto_de_grado.model.entity.Produccion;
import com.example.proyecto_de_grado.model.entity.Producto;
import com.example.proyecto_de_grado.repository.ProduccionRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio para gestionar las operaciones relacionadas con la producción agrícola. Permite crear,
 * cosechar, listar, actualizar y eliminar producciones, así como gestionar su estado y relación con
 * fincas y productos.
 *
 * @author Anderson Zuluaga
 * @version 1.0
 * @since 2023
 */
@Service
public class ProduccionService {

  @Autowired private ProduccionRepository produccionRepo;

  @Autowired private InventarioProductoService inventarioService;

  /**
   * Crea una nueva producción agrícola con los datos proporcionados.
   *
   * @param dto Objeto DTO con los datos de la producción a crear
   * @return DTO de la producción creada con su ID asignado
   * @throws RuntimeException si ocurre un error durante la creación
   */
  @Transactional
  public ProduccionDTO crearProduccion(ProduccionDTO dto) {
    Produccion prod = new Produccion();
    prod.setProducto(new Producto(dto.getIdProducto()));
    prod.setFinca(new Finca(dto.getIdFinca()));
    prod.setFechaSiembra(dto.getFechaSiembra());
    prod.setEstado(dto.getEstado()); // Usar el estado del DTO

    // Solo establecer fechaCosecha y cantidadCosechada si el estado es COSECHADO
    if (dto.getEstado() == EstadoProduccion.COSECHADO) {
      prod.setFechaCosecha(dto.getFechaCosecha());
      prod.setCantidadCosechada(dto.getCantidadCosechada());

      // Actualizar el inventario si es cosechado
      inventarioService.actualizarInventario(dto.getIdProducto(), dto.getCantidadCosechada());
    }

    prod = produccionRepo.save(prod);
    dto.setIdProduccion(prod.getIdProduccion());
    return dto;
  }

  /**
   * Registra la cosecha de una producción existente. Actualiza la cantidad cosechada, la fecha de
   * cosecha y el estado a COSECHADO. Además, actualiza el inventario de productos con la cantidad
   * cosechada.
   *
   * @param idProduccion ID de la producción a cosechar
   * @param cantidadCosechada Cantidad cosechada del producto
   * @param fechaCosecha Fecha en que se realiza la cosecha
   * @throws RuntimeException si la producción no se encuentra
   */
  @Transactional
  public void cosechar(Integer idProduccion, BigDecimal cantidadCosechada, LocalDate fechaCosecha) {
    Produccion prod =
        produccionRepo
            .findById(idProduccion)
            .orElseThrow(() -> new RuntimeException("Producción no encontrada"));
    prod.setCantidadCosechada(cantidadCosechada);
    prod.setFechaCosecha(fechaCosecha);
    prod.setEstado(EstadoProduccion.COSECHADO);
    produccionRepo.save(prod);
    inventarioService.actualizarInventario(prod.getProducto().getIdProducto(), cantidadCosechada);
  }

  /**
   * Obtiene todas las producciones registradas en el sistema.
   *
   * @return Lista de todas las producciones en formato DTO
   */
  public List<ProduccionDTO> listarProducciones() {
    return produccionRepo.findAll().stream().map(this::convertirADTO).collect(Collectors.toList());
  }

  /**
   * Obtiene una producción específica por su ID.
   *
   * @param id ID de la producción a buscar
   * @return DTO de la producción encontrada
   * @throws RuntimeException si la producción no se encuentra
   */
  public ProduccionDTO obtenerProduccionPorId(Integer id) {
    return produccionRepo
        .findById(id)
        .map(this::convertirADTO)
        .orElseThrow(() -> new RuntimeException("Producción no encontrada"));
  }

  /**
   * Actualiza el estado de una producción existente. No permite actualizar producciones que ya han
   * sido cosechadas.
   *
   * @param idProduccion ID de la producción a actualizar
   * @param nuevoEstado Nuevo estado a asignar a la producción
   * @throws RuntimeException si la producción no se encuentra
   * @throws IllegalStateException si se intenta modificar una producción ya cosechada
   */
  @Transactional
  public void actualizarEstado(Integer idProduccion, EstadoProduccion nuevoEstado) {
    Produccion prod =
        produccionRepo
            .findById(idProduccion)
            .orElseThrow(() -> new RuntimeException("Producción no encontrada"));

    if (prod.getEstado() == EstadoProduccion.COSECHADO) {
      throw new IllegalStateException("No se puede modificar una producción ya cosechada");
    }

    prod.setEstado(nuevoEstado);
    produccionRepo.save(prod);
  }

  /**
   * Obtiene todas las producciones asociadas a una finca específica.
   *
   * @param idFinca ID de la finca para filtrar las producciones
   * @return Lista de producciones de la finca en formato DTO
   */
  public List<ProduccionDTO> listarPorFinca(Integer idFinca) {
    return produccionRepo.findByFinca_Id(idFinca).stream()
        .map(this::convertirADTO)
        .collect(Collectors.toList());
  }

  /**
   * Elimina una producción existente. No permite eliminar producciones que ya han sido cosechadas.
   *
   * @param idProduccion ID de la producción a eliminar
   * @throws RuntimeException si la producción no se encuentra
   * @throws IllegalStateException si se intenta eliminar una producción ya cosechada
   */
  @Transactional
  public void eliminarProduccion(Integer idProduccion) {
    Produccion prod =
        produccionRepo
            .findById(idProduccion)
            .orElseThrow(() -> new RuntimeException("Producción no encontrada"));

    if (prod.getEstado() == EstadoProduccion.COSECHADO) {
      throw new IllegalStateException("No se puede eliminar una producción cosechada");
    }

    produccionRepo.delete(prod);
  }

  /**
   * Convierte una entidad Produccion a su correspondiente DTO.
   *
   * @param produccion Entidad Produccion a convertir
   * @return DTO con los datos de la producción
   */
  private ProduccionDTO convertirADTO(Produccion produccion) {
    ProduccionDTO dto = new ProduccionDTO();
    dto.setIdProduccion(produccion.getIdProduccion());
    dto.setIdFinca(produccion.getFinca().getId());
    dto.setIdProducto(produccion.getProducto().getIdProducto());
    dto.setFechaSiembra(produccion.getFechaSiembra());
    dto.setFechaCosecha(produccion.getFechaCosecha());
    dto.setCantidadCosechada(produccion.getCantidadCosechada());
    dto.setEstado(produccion.getEstado());
    return dto;
  }
}
