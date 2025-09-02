package com.aproafa.proyectodegrado.service;

import com.aproafa.proyectodegrado.model.dto.ActividadDTO;
import com.aproafa.proyectodegrado.model.dto.UsoInsumoDTO;
import com.aproafa.proyectodegrado.model.entity.Actividad;
import com.aproafa.proyectodegrado.model.entity.Insumo;
import com.aproafa.proyectodegrado.model.entity.TipoActividad;
import com.aproafa.proyectodegrado.model.entity.UsoInsumo;
import com.aproafa.proyectodegrado.repository.ActividadRepository;
import com.aproafa.proyectodegrado.repository.InsumoRepository;
import com.aproafa.proyectodegrado.repository.TipoActividadRepository;
import com.aproafa.proyectodegrado.repository.UsoInsumoRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servicio encargado de gestionar las actividades en el sistema.
 *
 * <p>Esta clase contiene los métodos para crear, obtener, actualizar y eliminar actividades en la
 * base de datos. Además, permite registrar insumos utilizados con su respectiva fecha de uso.
 *
 * <p>Autor: Anderson Zuluaga
 */
@Service
public class ActividadService {

  @Autowired private ActividadRepository actividadRepository;
  @Autowired private TipoActividadRepository tipoActividadRepository;
  @Autowired private UsoInsumoRepository usoInsumoRepository;
  @Autowired private InsumoRepository insumoRepository;
  @Autowired private InsumoService insumoService;

  /**
   * Crea una nueva actividad y registra los insumos usados, incluyendo la fecha de uso.
   *
   * @param dto El DTO que contiene la información de la nueva actividad y sus insumos.
   * @return El DTO de la actividad recién creada.
   */
  public ActividadDTO crearActividad(ActividadDTO dto) {
    // Buscar el tipo de actividad
    TipoActividad tipoActividad =
        tipoActividadRepository
            .findById(dto.getIdTipoActividad())
            .orElseThrow(() -> new RuntimeException("Tipo de actividad no encontrado"));

    // Crear y guardar la entidad Actividad
    Actividad actividad = new Actividad();
    actividad.setIdFinca(dto.getIdFinca());
    actividad.setTipoActividad(tipoActividad);
    actividad.setFechaInicio(dto.getFechaInicio());
    actividad.setFechaFin(dto.getFechaFin());
    actividad.setDescripcion(dto.getDescripcion());
    actividadRepository.save(actividad);

    // Procesar insumos usados si existen
    if (dto.getUsosInsumos() != null) {
      for (UsoInsumoDTO usoDto : dto.getUsosInsumos()) {
        // Buscar el insumo
        Insumo insumo =
            insumoRepository
                .findById(usoDto.getIdInsumo())
                .orElseThrow(() -> new RuntimeException("Insumo no encontrado"));

        // Validar si hay suficiente stock disponible
        if (insumo.getCantidadDisponible().compareTo(usoDto.getCantidad()) < 0) {
          throw new RuntimeException("Stock insuficiente para el insumo: " + insumo.getNombre());
        }

        // Registrar el uso del insumo (descontar stock, actualizar historial, etc.)
        insumoService.registrarUsoInsumo(usoDto.getIdInsumo(), usoDto.getCantidad());

        // Crear y guardar el uso del insumo con la fecha correspondiente
        UsoInsumo usoInsumo = new UsoInsumo();
        usoInsumo.setActividad(actividad);
        usoInsumo.setInsumo(insumo);
        usoInsumo.setCantidad(usoDto.getCantidad());

        LocalDate fecha_uso;
        if (usoDto.getFecha_uso() != null) {
          fecha_uso = usoDto.getFecha_uso();
        } else {
          // Si no se especifica fecha, usar la fecha de inicio de la actividad
          fecha_uso = dto.getFechaInicio() != null ? dto.getFechaInicio() : LocalDate.now();
        }
        usoInsumo.setFecha(fecha_uso);

        usoInsumoRepository.save(usoInsumo);
        actividad.getUsosInsumos().add(usoInsumo);
      }
    }

    return toDTO(actividad);
  }

  /**
   * Lista todas las actividades registradas en el sistema.
   *
   * @return Una lista de DTOs que representan todas las actividades.
   */
  public List<ActividadDTO> listarTodas() {
    return actividadRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
  }

  /**
   * Obtiene todas las actividades asociadas a una finca.
   *
   * @param idFinca El ID de la finca.
   * @return Una lista de DTOs que representan las actividades de la finca.
   */
  public List<ActividadDTO> listarPorFinca(Integer idFinca) {
    return actividadRepository.findByIdFinca(idFinca).stream()
        .map(this::toDTO)
        .collect(Collectors.toList());
  }

  /**
   * Obtiene una actividad por su ID.
   *
   * @param idActividad El ID de la actividad.
   * @return Un {@link Optional} que contiene el DTO de la actividad, si existe.
   */
  public Optional<ActividadDTO> obtenerPorId(Integer idActividad) {
    return actividadRepository.findById(idActividad).map(this::toDTO);
  }

  /**
   * Actualiza una actividad existente, devolviendo stock si se reduce o elimina el uso de insumos.
   *
   * @param idActividad El ID de la actividad que se quiere actualizar.
   * @param dto El DTO con la nueva información de la actividad.
   * @return El DTO actualizado de la actividad.
   */
  public ActividadDTO actualizarActividad(Integer idActividad, ActividadDTO dto) {
    Actividad actividad =
        actividadRepository
            .findById(idActividad)
            .orElseThrow(() -> new RuntimeException("Actividad no encontrada"));

    TipoActividad tipoActividad =
        tipoActividadRepository
            .findById(dto.getIdTipoActividad())
            .orElseThrow(() -> new RuntimeException("Tipo de actividad no encontrado"));

    actividad.setIdFinca(dto.getIdFinca());
    actividad.setTipoActividad(tipoActividad);
    actividad.setFechaInicio(dto.getFechaInicio());
    actividad.setFechaFin(dto.getFechaFin());
    actividad.setDescripcion(dto.getDescripcion());

    // Obtener usos anteriores
    List<UsoInsumo> usosAnteriores = usoInsumoRepository.findByActividadIdActividad(idActividad);

    // Mapear insumos anteriores por ID
    Map<Integer, UsoInsumo> mapaAnterior =
        usosAnteriores.stream().collect(Collectors.toMap(u -> u.getInsumo().getIdInsumo(), u -> u));

    // Limpiar lista actual para ser reemplazada
    actividad.getUsosInsumos().clear();

    if (dto.getUsosInsumos() != null) {
      for (UsoInsumoDTO usoDto : dto.getUsosInsumos()) {
        Insumo insumo =
            insumoRepository
                .findById(usoDto.getIdInsumo())
                .orElseThrow(() -> new RuntimeException("Insumo no encontrado"));

        BigDecimal nuevaCantidad = usoDto.getCantidad();
        BigDecimal cantidadAnterior = BigDecimal.ZERO;

        if (mapaAnterior.containsKey(insumo.getIdInsumo())) {
          cantidadAnterior = mapaAnterior.get(insumo.getIdInsumo()).getCantidad();
          mapaAnterior.remove(insumo.getIdInsumo());
        }

        BigDecimal diferencia = nuevaCantidad.subtract(cantidadAnterior);

        if (diferencia.compareTo(BigDecimal.ZERO) > 0) {
          // Aumentó el uso: verificar stock
          if (insumo.getCantidadDisponible().compareTo(diferencia) < 0) {
            throw new RuntimeException("Stock insuficiente para insumo: " + insumo.getNombre());
          }
          insumo.setCantidadDisponible(insumo.getCantidadDisponible().subtract(diferencia));
        } else if (diferencia.compareTo(BigDecimal.ZERO) < 0) {
          // Disminuyó el uso: devolver al stock
          insumo.setCantidadDisponible(insumo.getCantidadDisponible().add(diferencia.abs()));
        }

        insumoRepository.save(insumo);

        UsoInsumo nuevoUso = new UsoInsumo();
        nuevoUso.setActividad(actividad);
        nuevoUso.setInsumo(insumo);
        nuevoUso.setCantidad(nuevaCantidad);
        nuevoUso.setFecha(
            usoDto.getFecha_uso() != null ? usoDto.getFecha_uso() : dto.getFechaInicio());

        actividad.getUsosInsumos().add(nuevoUso);
      }
    }

    // Devolver stock de insumos eliminados
    for (UsoInsumo eliminado : mapaAnterior.values()) {
      Insumo insumo = eliminado.getInsumo();
      insumo.setCantidadDisponible(insumo.getCantidadDisponible().add(eliminado.getCantidad()));
      insumoRepository.save(insumo);
    }

    actividadRepository.save(actividad);
    return toDTO(actividad);
  }

  /**
   * Elimina una actividad y devuelve al stock los insumos utilizados.
   *
   * @param idActividad El ID de la actividad a eliminar.
   */
  public void eliminarActividad(Integer idActividad) {
    Actividad actividad =
        actividadRepository
            .findById(idActividad)
            .orElseThrow(() -> new RuntimeException("Actividad no encontrada"));

    for (UsoInsumo uso : actividad.getUsosInsumos()) {
      Insumo insumo = uso.getInsumo();
      insumo.setCantidadDisponible(insumo.getCantidadDisponible().add(uso.getCantidad()));
      insumoRepository.save(insumo);
    }

    actividadRepository.delete(actividad);
  }

  /**
   * Convierte una entidad {@link Actividad} a su correspondiente DTO {@link ActividadDTO}.
   *
   * @param actividad La entidad {@link Actividad}.
   * @return El DTO correspondiente de la actividad.
   */
  private ActividadDTO toDTO(Actividad actividad) {
    List<UsoInsumoDTO> usosDTO = new ArrayList<>();
    if (actividad.getUsosInsumos() != null) {
      usosDTO =
          actividad.getUsosInsumos().stream()
              .map(
                  uso ->
                      new UsoInsumoDTO(
                          uso.getInsumo().getIdInsumo(), uso.getCantidad(), uso.getFecha()))
              .collect(Collectors.toList());
    }

    return new ActividadDTO(
        actividad.getIdActividad(),
        actividad.getIdFinca(),
        actividad.getTipoActividad().getIdTipoActividad(),
        actividad.getFechaInicio(),
        actividad.getFechaFin(),
        actividad.getDescripcion(),
        usosDTO);
  }
}
