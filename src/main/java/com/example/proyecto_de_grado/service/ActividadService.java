package com.example.proyecto_de_grado.service;

import com.example.proyecto_de_grado.model.dto.ActividadDTO;
import com.example.proyecto_de_grado.model.entity.Actividad;
import com.example.proyecto_de_grado.model.entity.TipoActividad;
import com.example.proyecto_de_grado.repository.ActividadRepository;
import com.example.proyecto_de_grado.repository.TipoActividadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio encargado de gestionar las actividades en el sistema.
 *
 * <p>Esta clase contiene los métodos para crear, obtener, actualizar y eliminar actividades en la base de datos.
 * Además, convierte entre la entidad {@link Actividad} y el DTO {@link ActividadDTO} para la manipulación de datos.</p>
 *
 * <p>Autor: Anderson Zuluaga</p>
 */
@Service
public class ActividadService {

    @Autowired
    private ActividadRepository actividadRepository;

    @Autowired
    private TipoActividadRepository tipoActividadRepository;

    /**
     * Crea una nueva actividad.
     *
     * @param dto El DTO que contiene la información de la nueva actividad.
     * @return El DTO de la actividad recién creada.
     */
    public ActividadDTO crearActividad(ActividadDTO dto) {
        TipoActividad tipoActividad = tipoActividadRepository.findById(dto.getIdTipoActividad())
                .orElseThrow(() -> new RuntimeException("Tipo de actividad no encontrado"));

        Actividad actividad = new Actividad();
        actividad.setIdFinca(dto.getIdFinca());
        actividad.setTipoActividad(tipoActividad);
        actividad.setFechaInicio(dto.getFechaInicio());
        actividad.setFechaFin(dto.getFechaFin());
        actividad.setDescripcion(dto.getDescripcion());

        actividadRepository.save(actividad);

        dto.setIdActividad(actividad.getIdActividad()); // actualiza el id
        return dto;
    }

    /**
     * Obtiene todas las actividades asociadas a una finca.
     *
     * @param idFinca El ID de la finca.
     * @return Una lista de DTOs que representan las actividades de la finca.
     */
    public List<ActividadDTO> listarPorFinca(Integer idFinca) {
        return actividadRepository.findByIdFinca(idFinca)
                .stream()
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
        return actividadRepository.findById(idActividad)
                .map(this::toDTO);
    }

    /**
     * Actualiza una actividad existente.
     *
     * @param idActividad El ID de la actividad que se quiere actualizar.
     * @param dto El DTO con la nueva información de la actividad.
     * @return El DTO actualizado de la actividad.
     */
    public ActividadDTO actualizarActividad(Integer idActividad, ActividadDTO dto) {
        Actividad actividad = actividadRepository.findById(idActividad)
                .orElseThrow(() -> new RuntimeException("Actividad no encontrada"));

        TipoActividad tipoActividad = tipoActividadRepository.findById(dto.getIdTipoActividad())
                .orElseThrow(() -> new RuntimeException("Tipo de actividad no encontrado"));

        actividad.setIdFinca(dto.getIdFinca());
        actividad.setTipoActividad(tipoActividad);
        actividad.setFechaInicio(dto.getFechaInicio());
        actividad.setFechaFin(dto.getFechaFin());
        actividad.setDescripcion(dto.getDescripcion());

        actividadRepository.save(actividad);
        return toDTO(actividad);
    }

    /**
     * Elimina una actividad por su ID.
     *
     * @param idActividad El ID de la actividad a eliminar.
     */
    public void eliminarActividad(Integer idActividad) {
        actividadRepository.deleteById(idActividad);
    }

    /**
     * Convierte una entidad {@link Actividad} a su correspondiente DTO {@link ActividadDTO}.
     *
     * @param actividad La entidad {@link Actividad}.
     * @return El DTO correspondiente de la actividad.
     */
    private ActividadDTO toDTO(Actividad actividad) {
        return new ActividadDTO(
                actividad.getIdActividad(),
                actividad.getIdFinca(),
                actividad.getTipoActividad().getIdTipoActividad(),
                actividad.getFechaInicio(),
                actividad.getFechaFin(),
                actividad.getDescripcion()
        );
    }
}
