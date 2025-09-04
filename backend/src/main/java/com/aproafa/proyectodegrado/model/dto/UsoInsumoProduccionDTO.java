package com.aproafa.proyectodegrado.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
public class UsoInsumoProduccionDTO {
  private Integer idInsumo;
  private BigDecimal cantidad;
  private LocalDate fechaUso;
}
