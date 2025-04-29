package com.example.proyecto_de_grado.service;

import com.example.proyecto_de_grado.model.entity.Persona;
import com.example.proyecto_de_grado.repository.PersonaRepository;
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
