package com.example.proyecto_de_grado.controller;

import com.example.proyecto_de_grado.model.entity.HistorialInsumo;
import com.example.proyecto_de_grado.model.entity.Insumo;
import com.example.proyecto_de_grado.service.InsumoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/insumos")
public class InsumoController {
    @Autowired
    private InsumoService insumoService;

    @GetMapping
    public List<Insumo> getAllInsumos() {
        return insumoService.getAllInsumos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Insumo> getInsumoById(@PathVariable int id) {
        Optional<Insumo> insumo = insumoService.getInsumoById(id);
        return insumo.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/bajo-stock/{limite}")
    public List<Insumo> getInsumosBajosStock(@PathVariable BigDecimal limite) {
        return insumoService.getInsumosBajosStock(limite);
    }

    @PostMapping
    public Insumo createInsumo(@RequestBody Insumo insumo) {
        return insumoService.saveInsumo(insumo);
    }

    @PostMapping("/uso/{id}/{cantidad}")
    public ResponseEntity<String> registrarUsoInsumo(@PathVariable int id, @PathVariable BigDecimal  cantidad) {
        insumoService.registrarUsoInsumo(id, cantidad);
        return ResponseEntity.ok("Uso registrado y stock actualizado.");
    }

    @GetMapping("/historial/{id}")
    public List<HistorialInsumo> getHistorialInsumo(@PathVariable int id) {
        return insumoService.getHistorialInsumo(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteInsumo(@PathVariable int id) {
        insumoService.deleteInsumo(id);
        return ResponseEntity.ok("Insumo eliminado correctamente");
    }
}
