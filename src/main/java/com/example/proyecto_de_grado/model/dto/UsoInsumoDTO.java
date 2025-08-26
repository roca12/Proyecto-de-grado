package com.example.proyecto_de_grado.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsoInsumoDTO {
  private Integer idInsumo;
  private BigDecimal cantidad;
  private LocalDate fecha_uso; // Nueva propiedad
}
