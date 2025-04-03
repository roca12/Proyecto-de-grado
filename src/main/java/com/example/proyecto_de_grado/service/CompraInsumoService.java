package com.example.proyecto_de_grado.service;

import com.example.proyecto_de_grado.model.entity.CompraInsumo;
import com.example.proyecto_de_grado.model.entity.Insumo;
import com.example.proyecto_de_grado.repository.CompraInsumoRepository;
import com.example.proyecto_de_grado.repository.HistorialInsumoRepository;
import com.example.proyecto_de_grado.repository.InsumoRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompraInsumoService {
  @Autowired private CompraInsumoRepository compraInsumoRepository;

  @Autowired private InsumoRepository insumoRepository;

  @Autowired private HistorialInsumoRepository historialInsumoRepository;

  public List<CompraInsumo> getAllCompras() {
    return compraInsumoRepository.findAll();
  }

  public List<CompraInsumo> getComprasByInsumo(int idInsumo) {
    return compraInsumoRepository.findByInsumoIdInsumo(idInsumo);
  }

  public List<CompraInsumo> getComprasByProveedor(int idProveedor) {
    return compraInsumoRepository.findByProveedorIdProveedor(idProveedor);
  }

  public void saveCompra(CompraInsumo compra) {
    Optional<Insumo> optionalInsumo = insumoRepository.findById(compra.getInsumo().getIdInsumo());
    if (optionalInsumo.isPresent()) {
      Insumo insumo = optionalInsumo.get();
      BigDecimal nuevaCantidad = insumo.getCantidadDisponible().add(compra.getCantidad());
      insumo.setCantidadDisponible(nuevaCantidad);
      insumoRepository.save(insumo);

      compraInsumoRepository.save(compra);
    } else {
      throw new IllegalArgumentException("El insumo especificado no existe.");
    }
  }
}
