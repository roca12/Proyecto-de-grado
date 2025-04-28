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

/**
 * Controlador REST para la gestión de insumos agrícolas.
 *
 * <p>Proporciona operaciones para el manejo de insumos, incluyendo:
 * <ul>
 *   <li>Registro y consulta de insumos</li>
 *   <li>Gestión de stock y alertas de bajo inventario</li>
 *   <li>Registro de uso de insumos</li>
 *   <li>Consulta de historial de movimientos</li>
 * </ul>
 *
 * @author Anderson Zuluaga
 * @version 1.0
 * @since 2023
 */
@RestController
@RequestMapping("/api/insumos")
public class InsumoController {

    @Autowired
    private InsumoService insumoService;

    /**
     * Obtiene todos los insumos registrados en el sistema.
     *
     * @return Lista completa de insumos con su información actual
     */
    @GetMapping
    public List<Insumo> getAllInsumos() {
        return insumoService.getAllInsumos();
    }

    /**
     * Obtiene un insumo específico por su ID.
     *
     * @param id Identificador único del insumo
     * @return ResponseEntity con el insumo encontrado (200 OK) o 404 si no existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<Insumo> getInsumoById(@PathVariable int id) {
        Optional<Insumo> insumo = insumoService.getInsumoById(id);
        return insumo.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Obtiene los insumos con stock por debajo del límite especificado.
     *
     * @param limite Valor mínimo de stock para considerar como "bajo stock"
     * @return Lista de insumos que requieren reabastecimiento
     */
    @GetMapping("/bajo-stock/{limite}")
    public List<Insumo> getInsumosBajosStock(@PathVariable BigDecimal limite) {
        return insumoService.getInsumosBajosStock(limite);
    }

    /**
     * Registra un nuevo insumo en el sistema.
     *
     * @param insumo Objeto Insumo con los datos a registrar
     * @return El insumo creado con su ID asignado
     */
    @PostMapping
    public Insumo createInsumo(@RequestBody Insumo insumo) {
        return insumoService.saveInsumo(insumo);
    }

    /**
     * Registra el uso de una cantidad específica de un insumo.
     *
     * @param id Identificador del insumo utilizado
     * @param cantidad Cantidad consumida del insumo
     * @return ResponseEntity con mensaje de confirmación (200 OK)
     * @throws IllegalArgumentException si la cantidad es inválida o supera el stock disponible
     */
    @PostMapping("/uso/{id}/{cantidad}")
    public ResponseEntity<String> registrarUsoInsumo(@PathVariable int id, @PathVariable BigDecimal cantidad) {
        insumoService.registrarUsoInsumo(id, cantidad);
        return ResponseEntity.ok("Uso registrado y stock actualizado.");
    }

    /**
     * Obtiene el historial completo de movimientos de un insumo.
     *
     * @param id Identificador del insumo
     * @return Lista de registros históricos del insumo (compras, usos, ajustes)
     */
    @GetMapping("/historial/{id}")
    public List<HistorialInsumo> getHistorialInsumo(@PathVariable int id) {
        return insumoService.getHistorialInsumo(id);
    }

    /**
     * Elimina un insumo del sistema.
     *
     * @param id Identificador del insumo a eliminar
     * @return ResponseEntity con mensaje de confirmación (200 OK)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteInsumo(@PathVariable int id) {
        insumoService.deleteInsumo(id);
        return ResponseEntity.ok("Insumo eliminado correctamente");
    }
}