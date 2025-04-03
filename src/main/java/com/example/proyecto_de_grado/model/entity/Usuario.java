package com.example.proyecto_de_grado.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "usuario")
@PrimaryKeyJoinColumn(name = "id_persona")
public class Usuario extends Persona {
  private String contraseña;
  private String tipoUsuario;
}
