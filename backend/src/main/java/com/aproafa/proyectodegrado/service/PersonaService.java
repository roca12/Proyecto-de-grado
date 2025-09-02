package com.aproafa.proyectodegrado.service;

import com.aproafa.proyectodegrado.model.entity.Persona;
import com.aproafa.proyectodegrado.repository.PersonaRepository;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servicio encargado de gestionar las operaciones relacionadas con las personas.
 *
 * <p>Esta clase permite guardar una persona y verificar si existe una persona con un número de
 * identificación específico.
 *
 * <p>Autor: Anderson Zuluaga
 */
@Service
public class PersonaService {

  @Autowired private PersonaRepository personaRepository;

  /**
   * Verifica si existe una persona con el número de identificación proporcionado.
   *
   * @param numero El número de identificación a verificar.
   * @return true si existe una persona con ese número de identificación, false en caso contrario.
   */
  public boolean findByNumeroIdentificacion(String numero) {
    return personaRepository.existsByNumeroIdentificacion(numero);
  }

  // ... lo que ya tienes arriba

  /**
   * Obtiene una lista de todas las personas registradas.
   *
   * @return Lista de personas.
   */
  public List<Persona> getAllPersonas() {
    return personaRepository.findAll();
  }

  /**
   * Busca una persona por su ID.
   *
   * @param id El ID de la persona.
   * @return La persona si existe, o null si no se encuentra.
   */
  public Persona getPersonaById(Integer id) {
    return personaRepository.findById(id).orElse(null);
  }

  /**
   * Guarda una persona en el sistema.
   *
   * @param persona La persona a guardar.
   * @return La persona guardada.
   */
  public Persona save(Persona persona) {
    return personaRepository.save(persona);
  }
}
