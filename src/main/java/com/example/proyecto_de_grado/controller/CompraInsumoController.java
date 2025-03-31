package com.example.proyecto_de_grado.controller;

import com.example.proyecto_de_grado.model.entity.CompraInsumo;
import com.example.proyecto_de_grado.service.CompraInsumoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/compra-insumos")
public class CompraInsumoController {
    @Autowired
    private CompraInsumoService compraInsumoService;

    @GetMapping
    public List<CompraInsumo> getAllCompras() {
        return compraInsumoService.getAllCompras();
    }

    @GetMapping("/insumo/{id}")
    public List<CompraInsumo> getComprasByInsumo(@PathVariable int id) {
        return compraInsumoService.getComprasByInsumo(id);
    }

    @GetMapping("/proveedor/{id}")
    public List<CompraInsumo> getComprasByProveedor(@PathVariable int id) {
        return compraInsumoService.getComprasByProveedor(id);
    }

    @PostMapping
    public ResponseEntity<?> createCompra(@Valid @RequestBody CompraInsumo compra) {
        try {
            compraInsumoService.saveCompra(compra);
            return ResponseEntity.ok("Compra registrada y stock actualizado.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    }