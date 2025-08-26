package com.example.proyecto_de_grado.service;

import com.example.proyecto_de_grado.model.dto.ClienteDTO;
import com.example.proyecto_de_grado.model.entity.Cliente;
import com.example.proyecto_de_grado.model.entity.Finca;
import com.example.proyecto_de_grado.model.entity.Persona;
import com.example.proyecto_de_grado.repository.ClienteRepository;
import com.example.proyecto_de_grado.repository.FincaRepository;
import com.example.proyecto_de_grado.repository.PersonaRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio encargado de gestionar los clientes en el sistema.
 *
 * <p>Esta clase contiene los métodos para crear, obtener, actualizar y eliminar clientes en la base
 * de datos. Además, convierte entre la entidad {@link Cliente} y el DTO {@link ClienteDTO} para la
 * manipulación de datos.
 *
 * <p>Autor: Anderson Zuluaga
 */
@Service
@RequiredArgsConstructor
public class ClienteService {

  private final ClienteRepository clienteRepository;
  private final PersonaRepository personaRepository;
  private final FincaRepository fincaRepository;

  /**
   * Obtiene un cliente por el ID de la persona asociada.
   *
   * @param idPersona El ID de la persona asociada al cliente.
   * @return Un {@link Optional} que contiene el cliente si se encuentra, o vacío si no.
   */
  public Optional<Cliente> obtenerClientePorIdPersona(int idPersona) {
    return clienteRepository.findByPersona_IdPersona(idPersona);
  }

  /**
   * Obtiene un cliente por el número de identificación de la persona.
   *
   * @param numeroIdentificacion El número de identificación de la persona asociada al cliente.
   * @return Un {@link Optional} que contiene el cliente si se encuentra, o vacío si no.
   */
  public Optional<Cliente> obtenerClientePorNumeroIdentificacion(String numeroIdentificacion) {
    return clienteRepository.findByPersona_NumeroIdentificacion(numeroIdentificacion);
  }

  public List<ClienteDTO> listarClientesPorFinca(Integer idFinca) {
    return clienteRepository.findByFinca_Id(idFinca).stream()
        .map(this::convertirAClienteDTO) // O el mapper que uses: clienteMapper::toDto
        .collect(Collectors.toList());
  }

  /**
   * Crea un nuevo cliente.
   *
   * @param clienteDTO El DTO que contiene la información del nuevo cliente.
   * @return El DTO del cliente recién creado.
   */
  @Transactional
  public ClienteDTO crearCliente(ClienteDTO clienteDTO) {
    // Verificar si la persona ya existe
    Optional<Persona> personaExistente =
        personaRepository.findByNumeroIdentificacion(clienteDTO.getNumeroIdentificacion());
    Persona persona;

    if (personaExistente.isPresent()) {
      persona = personaExistente.get();
    } else {
      // Crear nueva persona
      persona = new Persona();
      persona.setNombre(clienteDTO.getNombre());
      persona.setApellido(clienteDTO.getApellido());
      persona.setTipoId(clienteDTO.getTipoId());
      persona.setNumeroIdentificacion(clienteDTO.getNumeroIdentificacion());
      persona.setEmail(clienteDTO.getEmail());
      persona.setTelefono(clienteDTO.getTelefono());
      persona.setDireccion(clienteDTO.getDireccion());
      persona = personaRepository.save(persona);
    }

    // Buscar la finca
    Finca finca =
        fincaRepository
            .findById(clienteDTO.getIdFinca())
            .orElseThrow(() -> new RuntimeException("Finca no encontrada"));

    // Crear el cliente
    Cliente cliente = new Cliente();
    cliente.setPersona(persona);
    cliente.setFinca(finca);
    cliente.setTipoCliente(clienteDTO.getTipoCliente());
    cliente.setFechaRegistro(clienteDTO.getFechaRegistro());

    Cliente clienteGuardado = clienteRepository.save(cliente);

    return convertirAClienteDTO(clienteGuardado);
  }

  /**
   * Obtiene todos los clientes.
   *
   * @return Una lista de {@link ClienteDTO} que representa todos los clientes.
   */
  public List<ClienteDTO> listarClientes() {
    List<Cliente> clientes = clienteRepository.findAll();
    return clientes.stream().map(this::convertirAClienteDTO).collect(Collectors.toList());
  }

  /**
   * Obtiene un cliente por su ID.
   *
   * @param id El ID del cliente a obtener.
   * @return El DTO del cliente.
   */
  public ClienteDTO obtenerClientePorId(Integer id) {
    Cliente cliente =
        clienteRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    return convertirAClienteDTO(cliente);
  }

  /**
   * Actualiza la información de un cliente existente.
   *
   * @param id El ID del cliente a actualizar.
   * @param clienteDTO El DTO con la nueva información del cliente.
   * @return El DTO actualizado del cliente.
   */
  @Transactional
  public ClienteDTO actualizarCliente(Integer id, ClienteDTO clienteDTO) {
    Cliente cliente =
        clienteRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

    // Actualizar datos de persona
    Persona persona = cliente.getPersona();
    persona.setNombre(clienteDTO.getNombre());
    persona.setApellido(clienteDTO.getApellido());
    persona.setTipoId(clienteDTO.getTipoId());
    persona.setNumeroIdentificacion(clienteDTO.getNumeroIdentificacion());
    persona.setEmail(clienteDTO.getEmail());
    persona.setTelefono(clienteDTO.getTelefono());
    persona.setDireccion(clienteDTO.getDireccion());
    personaRepository.save(persona);

    // Actualizar datos de cliente
    Finca finca =
        fincaRepository
            .findById(clienteDTO.getIdFinca())
            .orElseThrow(() -> new RuntimeException("Finca no encontrada"));

    cliente.setFinca(finca);
    cliente.setTipoCliente(clienteDTO.getTipoCliente());
    cliente.setFechaRegistro(clienteDTO.getFechaRegistro());

    Cliente clienteActualizado = clienteRepository.save(cliente);

    return convertirAClienteDTO(clienteActualizado);
  }

  /**
   * Elimina un cliente por su ID.
   *
   * @param id El ID del cliente a eliminar.
   */
  public void eliminarCliente(Integer id) {
    Cliente cliente =
        clienteRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    clienteRepository.delete(cliente);
  }

  /**
   * Convierte una entidad {@link Cliente} en un {@link ClienteDTO}.
   *
   * @param cliente La entidad {@link Cliente}.
   * @return El DTO correspondiente de cliente.
   */
  private ClienteDTO convertirAClienteDTO(Cliente cliente) {
    ClienteDTO dto = new ClienteDTO();
    dto.setIdCliente(cliente.getIdCliente());
    dto.setTipoCliente(cliente.getTipoCliente());
    dto.setFechaRegistro(cliente.getFechaRegistro());

    // Datos de persona
    Persona persona = cliente.getPersona();
    dto.setIdPersona(persona.getIdPersona());
    dto.setNombre(persona.getNombre());
    dto.setApellido(persona.getApellido());
    dto.setTipoId(persona.getTipoId());
    dto.setNumeroIdentificacion(persona.getNumeroIdentificacion());
    dto.setEmail(persona.getEmail());
    dto.setTelefono(persona.getTelefono());
    dto.setDireccion(persona.getDireccion());

    dto.setIdFinca(cliente.getFinca().getId());

    return dto;
  }
}
