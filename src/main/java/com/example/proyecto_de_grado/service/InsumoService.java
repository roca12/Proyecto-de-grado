package com.example.proyecto_de_grado.service;

import com.example.proyecto_de_grado.model.entity.HistorialInsumo;
import com.example.proyecto_de_grado.model.entity.Insumo;
import com.example.proyecto_de_grado.repository.HistorialInsumoRepository;
import com.example.proyecto_de_grado.repository.InsumoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InsumoService {
    @Autowired
    private InsumoRepository insumoRepository;

    @Autowired
    private HistorialInsumoRepository historialInsumoRepository;

    public List<Insumo> getAllInsumos() {
        return insumoRepository.findAll();
    }

    public Optional<Insumo> getInsumoById(int id) {
        return insumoRepository.findById(id);
    }

    public List<Insumo> getInsumosBajosStock(BigDecimal limite) {
        return insumoRepository.findAll()
                .stream()
                .filter(insumo -> insumo.getCantidadDisponible().compareTo(limite) < 0)
                .toList();
    }

    public Insumo saveInsumo(Insumo insumo) {
        return insumoRepository.save(insumo);
    }

    public void deleteInsumo(int id) {
        insumoRepository.deleteById(id);
    }

    public void registrarUsoInsumo(int idInsumo, BigDecimal cantidadUsada) {
        Optional<Insumo> optionalInsumo = insumoRepository.findById(idInsumo);
        if (optionalInsumo.isPresent()) {
            Insumo insumo = optionalInsumo.get();
            BigDecimal nuevaCantidad = insumo.getCantidadDisponible().subtract(cantidadUsada);

            // Validar que no se use más cantidad de la disponible
            if (nuevaCantidad.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("No se puede usar más insumo del disponible.");
            }

            insumo.setCantidadDisponible(nuevaCantidad);
            insumoRepository.save(insumo);

            // Registrar en el historial
            HistorialInsumo historial = new HistorialInsumo();
            historial.setInsumo(insumo);
            historial.setCantidadUtilizada(cantidadUsada);
            historial.setFechaUso(LocalDateTime.now());
            historialInsumoRepository.save(historial);
        }
    }

    public List<HistorialInsumo> getHistorialInsumo(int idInsumo) {
        return historialInsumoRepository.findByInsumoIdInsumo(idInsumo);
    }
}
