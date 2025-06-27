package com.example.proyecto_de_grado.controller;

import com.example.proyecto_de_grado.model.dto.DetalleVentaDTO;
import com.example.proyecto_de_grado.model.dto.VentaDTO;
import com.example.proyecto_de_grado.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Controlador REST para la gestión de ventas.
 *
 * @author Anderson Zuluaga
 * @version 1.0
 * @since 2023
 */
@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final VentaService ventaService;

    @Autowired
    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    /**
     * Obtiene todas las ventas registradas en el sistema.
     */
    @GetMapping
    public ResponseEntity<List<VentaDTO>> obtenerTodasLasVentas() {
        try {
            List<VentaDTO> ventas = ventaService.obtenerTodasLasVentas();
            return ResponseEntity.ok(ventas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtiene una venta específica por su identificador único.
     */
    @GetMapping("/{idVenta}")
    public ResponseEntity<VentaDTO> obtenerVentaPorId(@PathVariable Integer idVenta) {
        try {
            VentaDTO venta = ventaService.obtenerVentaPorId(idVenta);
            if (venta != null) {
                return ResponseEntity.ok(venta);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtiene todos los detalles de productos asociados a una venta específica.
     */
    @GetMapping("/{idVenta}/detalles")
    public ResponseEntity<List<DetalleVentaDTO>> obtenerDetallesDeVenta(@PathVariable Integer idVenta) {
        try {
            List<DetalleVentaDTO> detalles = ventaService.obtenerDetallesDeVenta(idVenta);
            return ResponseEntity.ok(detalles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Crea una nueva venta sin detalles.
     */
    @PostMapping
    public ResponseEntity<VentaDTO> crearVenta(@Valid @RequestBody VentaDTO ventaDTO) {
        try {
            VentaDTO ventaGuardada = ventaService.guardarVenta(ventaDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(ventaGuardada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Crea una nueva venta completa con sus detalles de productos.
     */
    @PostMapping("/con-detalles")
    public ResponseEntity<VentaDTO> crearVentaConDetalles(@Valid @RequestBody VentaConDetallesRequest requestBody) {
        try {
            // Validar que la request tenga datos válidos
            if (requestBody.getVenta() == null || requestBody.getDetalles() == null || requestBody.getDetalles().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            VentaDTO ventaGuardada = ventaService.guardarVentaConDetalles(
                    requestBody.getVenta(),
                    requestBody.getDetalles()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(ventaGuardada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Actualiza una venta existente.
     */
    @PutMapping("/{idVenta}")
    public ResponseEntity<VentaDTO> actualizarVenta(@PathVariable Integer idVenta,
                                                    @Valid @RequestBody VentaDTO ventaDTO) {
        try {
            VentaDTO ventaActualizada = ventaService.actualizarVenta(idVenta, ventaDTO);
            if (ventaActualizada != null) {
                return ResponseEntity.ok(ventaActualizada);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Elimina una venta y todos sus detalles asociados.
     */
    @DeleteMapping("/{idVenta}")
    public ResponseEntity<Void> eliminarVenta(@PathVariable Integer idVenta) {
        try {
            boolean eliminada = ventaService.eliminarVenta(idVenta);
            if (eliminada) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Clase interna para encapsular la petición de venta con detalles.
     */
    public static class VentaConDetallesRequest {
        private VentaDTO venta;
        private List<DetalleVentaDTO> detalles;

        public VentaConDetallesRequest() {}

        public VentaConDetallesRequest(VentaDTO venta, List<DetalleVentaDTO> detalles) {
            this.venta = venta;
            this.detalles = detalles;
        }

        public VentaDTO getVenta() {
            return venta;
        }

        public void setVenta(VentaDTO venta) {
            this.venta = venta;
        }

        public List<DetalleVentaDTO> getDetalles() {
            return detalles;
        }

        public void setDetalles(List<DetalleVentaDTO> detalles) {
            this.detalles = detalles;
        }
    }
}