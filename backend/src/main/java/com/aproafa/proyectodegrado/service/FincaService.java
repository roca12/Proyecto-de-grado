package com.aproafa.proyectodegrado.service;

import com.aproafa.proyectodegrado.model.dto.FincaDTO;
import com.aproafa.proyectodegrado.model.entity.Finca;
import com.aproafa.proyectodegrado.repository.FincaRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servicio encargado de gestionar las operaciones relacionadas con las fincas.
 *
 * <p>Esta clase permite crear, actualizar, eliminar y consultar fincas, así como convertir entre la
 * entidad Finca y su DTO.
 *
 * <p>Autor: Anderson Zuluaga
 */
@Service
public class FincaService {

  @Autowired private FincaRepository fincaRepository;

  /**
   * Convierte una entidad Finca en su correspondiente DTO (Data Transfer Object).
   *
   * @param finca La entidad Finca a convertir.
   * @return El DTO correspondiente a la Finca.
   */
  private FincaDTO toDTO(Finca finca) {
    FincaDTO dto = new FincaDTO();
    dto.setIdFinca(finca.getId());
    dto.setNombre(finca.getNombre());
    dto.setUbicacion(finca.getUbicacion());
    dto.setEncargado(finca.getEncargado());
    return dto;
  }

  /**
   * Convierte un DTO de Finca en su correspondiente entidad.
   *
   * @param dto El DTO de Finca a convertir.
   * @return La entidad Finca correspondiente al DTO.
   */
  private Finca toEntity(FincaDTO dto) {
    Finca finca = new Finca();
    finca.setId(dto.getIdFinca());
    finca.setNombre(dto.getNombre());
    finca.setUbicacion(dto.getUbicacion());
    finca.setEncargado(dto.getEncargado());
    return finca;
  }

  /**
   * Obtiene todas las fincas registradas en el sistema.
   *
   * @return Una lista con todos los DTOs de las fincas.
   */
  public List<FincaDTO> obtenerTodas() {
    return fincaRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
  }

  /**
   * Obtiene una finca a partir de su ID.
   *
   * @param id El ID de la finca.
   * @return Un Optional que contiene el DTO de la finca si existe.
   */
  public Optional<FincaDTO> obtenerPorId(Integer id) {
    return fincaRepository.findById(id).map(this::toDTO);
  }

  /**
   * Crea una nueva finca en el sistema.
   *
   * @param dto El DTO que contiene los datos de la nueva finca.
   * @return El DTO de la finca creada.
   */
  public FincaDTO crearFinca(FincaDTO dto) {
    Finca finca = fincaRepository.save(toEntity(dto));
    return toDTO(finca);
  }

  /**
   * Actualiza una finca existente en el sistema.
   *
   * @param id El ID de la finca a actualizar.
   * @param dto El DTO con los nuevos datos de la finca.
   * @return El DTO de la finca actualizada.
   */
  public FincaDTO actualizarFinca(Integer id, FincaDTO dto) {
    Finca finca = fincaRepository.findById(id).orElseThrow();
    finca.setNombre(dto.getNombre());
    finca.setUbicacion(dto.getUbicacion());
    finca.setEncargado(dto.getEncargado());
    return toDTO(fincaRepository.save(finca));
  }

  /**
   * Elimina una finca del sistema.
   *
   * @param id El ID de la finca a eliminar.
   */
  public void eliminarFinca(Integer id) {
    fincaRepository.deleteById(id);
  }
}
