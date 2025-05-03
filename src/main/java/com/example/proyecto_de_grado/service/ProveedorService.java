package com.example.proyecto_de_grado.service;

import com.example.proyecto_de_grado.model.dto.ProveedorDTO;
import com.example.proyecto_de_grado.model.entity.Finca;
import com.example.proyecto_de_grado.model.entity.Persona;
import com.example.proyecto_de_grado.model.entity.Proveedor;
import com.example.proyecto_de_grado.repository.FincaRepository;
import com.example.proyecto_de_grado.repository.PersonaRepository;
import com.example.proyecto_de_grado.repository.ProveedorRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio encargado de gestionar los proveedores en el sistema.
 *
 * <p>Incluye operaciones CRUD y mapeo entre entidad {@link Proveedor} y DTO {@link ProveedorDTO}.
 *
 * <p>Autor: Anderson Zuluaga
 */
@Service
@RequiredArgsConstructor
public class ProveedorService {

  private final ProveedorRepository proveedorRepository;
  private final PersonaRepository personaRepository;
  private final FincaRepository fincaRepository;

  /**
   * Crea un nuevo proveedor.
   *
   * @param proveedorDTO El DTO con la información del proveedor.
   * @return El DTO del proveedor recién creado.
   */
  @Transactional
  public ProveedorDTO crearProveedor(ProveedorDTO proveedorDTO) {
    // Verificar si la persona ya existe
    Optional<Persona> personaExistente =
        personaRepository.findByNumeroIdentificacion(proveedorDTO.getNumeroIdentificacion());
    Persona persona;

    if (personaExistente.isPresent()) {
      persona = personaExistente.get();
    } else {
      persona = new Persona();
      persona.setNombre(proveedorDTO.getNombre());
      persona.setApellido(proveedorDTO.getApellido());
      persona.setTipoId(proveedorDTO.getTipoId());
      persona.setNumeroIdentificacion(proveedorDTO.getNumeroIdentificacion());
      persona.setEmail(proveedorDTO.getEmail());
      persona.setTelefono(proveedorDTO.getTelefono());
      persona.setDireccion(proveedorDTO.getDireccion());
      persona = personaRepository.save(persona);
    }

    // Buscar finca asociada
    Finca finca =
        fincaRepository
            .findById(proveedorDTO.getIdFinca())
            .orElseThrow(() -> new RuntimeException("Finca no encontrada"));

    Proveedor proveedor = new Proveedor();
    proveedor.setPersona(persona);
    proveedor.setFinca(finca);
    proveedor.setContacto(proveedorDTO.getContacto());
    proveedor.setNombre(proveedorDTO.getNombre());

    Proveedor guardado = proveedorRepository.save(proveedor);
    return convertirAProveedorDTO(guardado);
  }

  /**
   * Obtiene todos los proveedores.
   *
   * @return Lista de proveedores en forma de DTO.
   */
  public List<ProveedorDTO> listarProveedores() {
    return proveedorRepository.findAll().stream()
        .map(this::convertirAProveedorDTO)
        .collect(Collectors.toList());
  }

  /**
   * Obtiene un proveedor por su ID.
   *
   * @param idProveedor El ID del proveedor.
   * @return DTO del proveedor.
   */
  public ProveedorDTO obtenerProveedorPorId(Integer idProveedor) {
    Proveedor proveedor =
        proveedorRepository
            .findById(idProveedor)
            .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
    return convertirAProveedorDTO(proveedor);
  }

  /**
   * Actualiza la información de un proveedor.
   *
   * @param idProveedor ID del proveedor a actualizar.
   * @param proveedorDTO Datos nuevos.
   * @return DTO actualizado.
   */
  @Transactional
  public ProveedorDTO actualizarProveedor(Integer idProveedor, ProveedorDTO proveedorDTO) {
    Proveedor proveedor =
        proveedorRepository
            .findById(idProveedor)
            .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

    // Actualizar datos de persona
    Persona persona = proveedor.getPersona();
    persona.setNombre(proveedorDTO.getNombre());
    persona.setApellido(proveedorDTO.getApellido());
    persona.setTipoId(proveedorDTO.getTipoId());
    persona.setNumeroIdentificacion(proveedorDTO.getNumeroIdentificacion());
    persona.setEmail(proveedorDTO.getEmail());
    persona.setTelefono(proveedorDTO.getTelefono());
    persona.setDireccion(proveedorDTO.getDireccion());
    personaRepository.save(persona);

    // Actualizar finca
    Finca finca =
        fincaRepository
            .findById(proveedorDTO.getIdFinca())
            .orElseThrow(() -> new RuntimeException("Finca no encontrada"));

    proveedor.setFinca(finca);
    proveedor.setContacto(proveedorDTO.getContacto());
    proveedor.setNombre(proveedorDTO.getNombre());

    Proveedor actualizado = proveedorRepository.save(proveedor);
    return convertirAProveedorDTO(actualizado);
  }

  /**
   * Elimina un proveedor por ID.
   *
   * @param idProveedor El ID del proveedor.
   */
  public void eliminarProveedor(Integer idProveedor) {
    Proveedor proveedor =
        proveedorRepository
            .findById(idProveedor)
            .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
    proveedorRepository.delete(proveedor);
  }

  /** Convierte entidad a DTO. */
  private ProveedorDTO convertirAProveedorDTO(Proveedor proveedor) {
    ProveedorDTO dto = new ProveedorDTO();
    dto.setIdProveedor(proveedor.getIdProveedor());
    dto.setContacto(proveedor.getContacto());
    dto.setNombre(proveedor.getNombre());

    Persona persona = proveedor.getPersona();
    dto.setIdPersona(persona.getIdPersona());
    dto.setNombre(persona.getNombre());
    dto.setApellido(persona.getApellido());
    dto.setTipoId(persona.getTipoId());
    dto.setNumeroIdentificacion(persona.getNumeroIdentificacion());
    dto.setEmail(persona.getEmail());
    dto.setTelefono(persona.getTelefono());
    dto.setDireccion(persona.getDireccion());

    dto.setIdFinca(proveedor.getFinca().getId());

    return dto;
  }
}
