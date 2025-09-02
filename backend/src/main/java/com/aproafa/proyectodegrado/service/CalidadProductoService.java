package com.aproafa.proyectodegrado.service;

import com.aproafa.proyectodegrado.model.dto.CalidadProductoDTO;
import com.aproafa.proyectodegrado.model.entity.CalidadProducto;
import com.aproafa.proyectodegrado.model.entity.Produccion;
import com.aproafa.proyectodegrado.repository.CalidadProductoRepository;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servicio para gestionar las calidades de productos en la producción. Proporciona métodos para
 * registrar calidades de una producción y obtener todas las calidades registradas.
 *
 * <p>Autor: Anderson Zuluaga
 */
@Service
public class CalidadProductoService {

  @Autowired private CalidadProductoRepository calidadRepo;

  /**
   * Registra la calidad de una producción.
   *
   * <p>Este método recibe un DTO de calidad de producto, crea una nueva entidad de
   * `CalidadProducto`, asigna los valores desde el DTO y la guarda en la base de datos. Luego,
   * retorna el DTO con el ID generado.
   *
   * @param dto Datos de calidad del producto a registrar.
   * @return El DTO de calidad con el ID asignado.
   */
  @Transactional
  public CalidadProductoDTO registrarCalidad(CalidadProductoDTO dto) {
    CalidadProducto cal = new CalidadProducto();
    cal.setProduccion(new Produccion(dto.getIdProduccion())); // Asocia la producción
    cal.setCalidad(dto.getCalidad());
    cal.setObservaciones(dto.getObservaciones());
    cal = calidadRepo.save(cal); // Guarda la entidad en la base de datos
    dto.setIdCalidad(cal.getIdCalidad()); // Asigna el ID generado al DTO
    return dto;
  }

  /**
   * Obtiene todas las calidades registradas.
   *
   * <p>Este método devuelve una lista de todos los registros de calidad de productos en la base de
   * datos, mapeados a objetos DTO.
   *
   * @return Lista de objetos DTO de calidad de productos.
   */
  public List<CalidadProductoDTO> obtenerTodas() {
    return calidadRepo.findAll().stream()
        .map(this::toDTO) // Convierte cada entidad a su correspondiente DTO
        .collect(Collectors.toList());
  }

  /**
   * Método auxiliar para convertir una entidad `CalidadProducto` a su DTO correspondiente.
   *
   * @param cal Entidad de calidad de producto.
   * @return DTO correspondiente con los datos de la entidad.
   */
  private CalidadProductoDTO toDTO(CalidadProducto cal) {
    CalidadProductoDTO dto = new CalidadProductoDTO();
    dto.setIdCalidad(cal.getIdCalidad());
    dto.setIdProduccion(cal.getProduccion().getIdProduccion());
    dto.setCalidad(cal.getCalidad());
    dto.setObservaciones(cal.getObservaciones());
    return dto;
  }
}
