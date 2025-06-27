package com.example.proyecto_de_grado.service;

import com.example.proyecto_de_grado.model.dto.EmpleadoDTO;
import com.example.proyecto_de_grado.model.entity.Empleado;
import com.example.proyecto_de_grado.model.entity.Finca;
import com.example.proyecto_de_grado.model.entity.Persona;
import com.example.proyecto_de_grado.repository.EmpleadoRepository;
import com.example.proyecto_de_grado.repository.FincaRepository;
import com.example.proyecto_de_grado.repository.PersonaRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio encargado de gestionar los empleados en el sistema.
 *
 * <p>Esta clase contiene los métodos para crear, obtener, actualizar y eliminar empleados en la base
 * de datos. Además, convierte entre la entidad {@link Empleado} y el DTO {@link EmpleadoDTO} para la
 * manipulación de datos.
 *
 * <p>Autor: Anderson Zuluaga
 */
@Service
@RequiredArgsConstructor
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final PersonaRepository personaRepository;
    private final FincaRepository fincaRepository;

    /**
     * Obtiene un empleado por el ID de la persona asociada.
     *
     * @param idPersona El ID de la persona asociada al empleado.
     * @return Un {@link Optional} que contiene el empleado si se encuentra, o vacío si no.
     */
    public Optional<Empleado> obtenerEmpleadoPorIdPersona(int idPersona) {
        return empleadoRepository.findByPersona_IdPersona(idPersona);
    }

    /**
     * Obtiene un empleado por el número de identificación de la persona.
     *
     * @param numeroIdentificacion El número de identificación de la persona asociada al empleado.
     * @return Un {@link Optional} que contiene el empleado si se encuentra, o vacío si no.
     */
    public Optional<Empleado> obtenerEmpleadoPorNumeroIdentificacion(String numeroIdentificacion) {
        return empleadoRepository.findByPersona_NumeroIdentificacion(numeroIdentificacion);
    }

    /**
     * Crea un nuevo empleado.
     *
     * @param empleadoDTO El DTO que contiene la información del nuevo empleado.
     * @return El DTO del empleado recién creado.
     */
    @Transactional
    public EmpleadoDTO crearEmpleado(EmpleadoDTO empleadoDTO) {
        // Verificar si la persona ya existe
        Optional<Persona> personaExistente =
                personaRepository.findByNumeroIdentificacion(empleadoDTO.getNumeroIdentificacion());
        Persona persona;

        if (personaExistente.isPresent()) {
            persona = personaExistente.get();
        } else {
            // Crear nueva persona
            persona = new Persona();
            persona.setNombre(empleadoDTO.getNombre());
            persona.setApellido(empleadoDTO.getApellido());
            persona.setTipoId(empleadoDTO.getTipoId());
            persona.setNumeroIdentificacion(empleadoDTO.getNumeroIdentificacion());
            persona.setEmail(empleadoDTO.getEmail());
            persona.setTelefono(empleadoDTO.getTelefono());
            persona.setDireccion(empleadoDTO.getDireccion());
            persona = personaRepository.save(persona);
        }

        // Buscar la finca
        Finca finca =
                fincaRepository
                        .findById(empleadoDTO.getIdFinca())
                        .orElseThrow(() -> new RuntimeException("Finca no encontrada"));

        // Crear el empleado
        Empleado empleado = new Empleado();
        empleado.setPersona(persona);
        empleado.setFinca(finca);
        empleado.setCargo(empleadoDTO.getCargo());
        empleado.setSalario(empleadoDTO.getSalario());
        empleado.setFechaContratacion(empleadoDTO.getFechaContratacion());

        Empleado empleadoGuardado = empleadoRepository.save(empleado);

        return convertirAEmpleadoDTO(empleadoGuardado);
    }

    /**
     * Obtiene todos los empleados.
     *
     * @return Una lista de {@link EmpleadoDTO} que representa todos los empleados.
     */
    public List<EmpleadoDTO> listarEmpleados() {
        List<Empleado> empleados = empleadoRepository.findAll();
        return empleados.stream().map(this::convertirAEmpleadoDTO).collect(Collectors.toList());
    }

    /**
     * Obtiene un empleado por su ID.
     *
     * @param id El ID del empleado a obtener.
     * @return El DTO del empleado.
     */
    public EmpleadoDTO obtenerEmpleadoPorId(Integer id) {
        Empleado empleado =
                empleadoRepository
                        .findById(id)
                        .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
        return convertirAEmpleadoDTO(empleado);
    }

    /**
     * Actualiza la información de un empleado existente.
     *
     * @param id El ID del empleado a actualizar.
     * @param empleadoDTO El DTO con la nueva información del empleado.
     * @return El DTO actualizado del empleado.
     */
    @Transactional
    public EmpleadoDTO actualizarEmpleado(Integer id, EmpleadoDTO empleadoDTO) {
        Empleado empleado =
                empleadoRepository
                        .findById(id)
                        .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        // Actualizar datos de persona
        Persona persona = empleado.getPersona();
        persona.setNombre(empleadoDTO.getNombre());
        persona.setApellido(empleadoDTO.getApellido());
        persona.setTipoId(empleadoDTO.getTipoId());
        persona.setNumeroIdentificacion(empleadoDTO.getNumeroIdentificacion());
        persona.setEmail(empleadoDTO.getEmail());
        persona.setTelefono(empleadoDTO.getTelefono());
        persona.setDireccion(empleadoDTO.getDireccion());
        personaRepository.save(persona);

        // Actualizar datos de empleado
        Finca finca =
                fincaRepository
                        .findById(empleadoDTO.getIdFinca())
                        .orElseThrow(() -> new RuntimeException("Finca no encontrada"));

        empleado.setFinca(finca);
        empleado.setCargo(empleadoDTO.getCargo());
        empleado.setSalario(empleadoDTO.getSalario());
        empleado.setFechaContratacion(empleadoDTO.getFechaContratacion());

        Empleado empleadoActualizado = empleadoRepository.save(empleado);

        return convertirAEmpleadoDTO(empleadoActualizado);
    }

    /**
     * Elimina un empleado por su ID.
     *
     * @param id El ID del empleado a eliminar.
     */
    public void eliminarEmpleado(Integer id) {
        Empleado empleado =
                empleadoRepository
                        .findById(id)
                        .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
        empleadoRepository.delete(empleado);
    }

    /**
     * Convierte una entidad {@link Empleado} en un {@link EmpleadoDTO}.
     *
     * @param empleado La entidad {@link Empleado}.
     * @return El DTO correspondiente de empleado.
     */
    private EmpleadoDTO convertirAEmpleadoDTO(Empleado empleado) {
        EmpleadoDTO dto = new EmpleadoDTO();
        dto.setIdEmpleado(empleado.getIdEmpleado());
        dto.setCargo(empleado.getCargo());
        dto.setSalario(empleado.getSalario());
        dto.setFechaContratacion(empleado.getFechaContratacion());

        // Datos de persona
        Persona persona = empleado.getPersona();
        dto.setIdPersona(persona.getIdPersona());
        dto.setNombre(persona.getNombre());
        dto.setApellido(persona.getApellido());
        dto.setTipoId(persona.getTipoId());
        dto.setNumeroIdentificacion(persona.getNumeroIdentificacion());
        dto.setEmail(persona.getEmail());
        dto.setTelefono(persona.getTelefono());
        dto.setDireccion(persona.getDireccion());

        dto.setIdFinca(empleado.getFinca().getId());

        return dto;
    }
}