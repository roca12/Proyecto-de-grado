package com.aproafa.proyectodegrado.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class VentaDTO {
  private Integer idVenta;
  private Integer idCliente;
  private Integer idPersona;
  private Integer idFinca;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime fechaVenta;

  private String metodoPago;
  private BigDecimal total;
}
