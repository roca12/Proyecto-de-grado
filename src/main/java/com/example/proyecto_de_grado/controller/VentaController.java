package com.example.proyecto_de_grado.controller;

import com.example.proyecto_de_grado.model.dto.DetalleVentaDTO;
import com.example.proyecto_de_grado.model.dto.VentaDTO;
import com.example.proyecto_de_grado.service.VentaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la gestión de ventas.
 *
 * @author Anderson Zuluaga
 * @version 1.0
 * @since 2025
 */
@RestController
@RequestMapping("/api/ventas")
public class VentaController {

  private final VentaService ventaService;

  @Autowired
  public VentaController(VentaService ventaService) {
    this.ventaService = ventaService;
  }

  /** Endpoint de prueba para verificar conectividad y parsing JSON */
  @PostMapping("/test")
  public ResponseEntity<?> testEndpoint(@RequestBody VentaConDetallesRequest requestBody) {
    try {
      System.out.println("=== TEST ENDPOINT ===");
      System.out.println("Request body: " + requestBody);

      return ResponseEntity.ok()
          .body("{\"message\": \"Test endpoint funcionando correctamente\", \"received\": true}");
    } catch (Exception e) {
      System.err.println("Error en test endpoint: " + e.getMessage());
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("{\"error\": \"Error en test: " + e.getMessage().replace("\"", "'") + "\"}");
    }
  }

  /** Obtiene los métodos de pago válidos */
  @GetMapping("/metodos-pago")
  public ResponseEntity<?> obtenerMetodosPago() {
    try {
      // Try to get valid payment methods from your service or enum
      // This is a guess - you'll need to adjust based on your actual enum
      java.util.List<String> metodos =
          java.util.Arrays.asList(
              "EFECTIVO",
              "TARJETA",
              "TRANSFERENCIA", // Spanish
              "CASH",
              "CARD",
              "TRANSFER", // English
              "CONTADO",
              "CREDITO",
              "DEBITO" // Other possibilities
              );

      return ResponseEntity.ok()
          .body(
              "{\"metodos\": "
                  + metodos
                  + ", \"message\": \"Estos son algunos valores posibles. Revisa tu enum de"
                  + " MetodoPago\"}");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("{\"error\": \"Error obteniendo métodos de pago\"}");
    }
  }

  @GetMapping("/finca/{idFinca}")
  public ResponseEntity<List<VentaDTO>> listarPorFinca(@PathVariable Integer idFinca) {
    List<VentaDTO> ventas = ventaService.obtenerVentasPorFinca(idFinca);
    return new ResponseEntity<>(ventas, HttpStatus.OK);
  }

  /** Obtiene todas las ventas registradas en el sistema. */
  @GetMapping
  public ResponseEntity<List<VentaDTO>> obtenerTodasLasVentas() {
    try {
      List<VentaDTO> ventas = ventaService.obtenerTodasLasVentas();
      return ResponseEntity.ok(ventas);
    } catch (Exception e) {
      System.err.println("Error obteniendo ventas: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /** Obtiene una venta específica por su identificador único. */
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
      System.err.println("Error obteniendo venta por ID: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /** Obtiene todos los detalles de productos asociados a una venta específica. */
  @GetMapping("/{idVenta}/detalles")
  public ResponseEntity<List<DetalleVentaDTO>> obtenerDetallesDeVenta(
      @PathVariable Integer idVenta) {
    try {
      List<DetalleVentaDTO> detalles = ventaService.obtenerDetallesDeVenta(idVenta);
      return ResponseEntity.ok(detalles);
    } catch (Exception e) {
      System.err.println("Error obteniendo detalles de venta: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /** Crea una nueva venta sin detalles. */
  @PostMapping
  public ResponseEntity<VentaDTO> crearVenta(@Valid @RequestBody VentaDTO ventaDTO) {
    try {
      VentaDTO ventaGuardada = ventaService.guardarVenta(ventaDTO);
      return ResponseEntity.status(HttpStatus.CREATED).body(ventaGuardada);
    } catch (IllegalArgumentException e) {
      System.err.println("Error de validación al crear venta: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    } catch (Exception e) {
      System.err.println("Error interno al crear venta: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /** Crea una nueva venta completa con sus detalles de productos. */
  @PostMapping("/con-detalles")
  public ResponseEntity<?> crearVentaConDetalles(@RequestBody VentaConDetallesRequest requestBody) {
    try {
      System.out.println("=== INICIO DEBUG CREAR VENTA CON DETALLES ===");
      System.out.println("Request body recibido: " + requestBody);

      // Validar que la request tenga datos válidos
      if (requestBody == null) {
        System.err.println("ERROR: Request body es null");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("{\"error\": \"Request body es null\"}");
      }

      System.out.println("Venta en request: " + requestBody.getVenta());
      System.out.println("Detalles en request: " + requestBody.getDetalles());

      if (requestBody.getVenta() == null) {
        System.err.println("ERROR: Venta en request body es null");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("{\"error\": \"Venta no puede ser null\"}");
      }

      if (requestBody.getDetalles() == null || requestBody.getDetalles().isEmpty()) {
        System.err.println("ERROR: Detalles en request body son null o vacíos");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("{\"error\": \"Detalles no pueden ser null o vacíos\"}");
      }

      // Log individual fields for debugging
      VentaDTO venta = requestBody.getVenta();
      System.out.println("Venta details:");
      System.out.println("  - idCliente: " + venta.getIdCliente());
      System.out.println("  - idPersona: " + venta.getIdPersona());
      System.out.println("  - metodoPago: " + venta.getMetodoPago());
      System.out.println("  - total: " + venta.getTotal());

      System.out.println("Detalles count: " + requestBody.getDetalles().size());
      for (int i = 0; i < requestBody.getDetalles().size(); i++) {
        DetalleVentaDTO detalle = requestBody.getDetalles().get(i);
        System.out.println("  Detalle " + i + ":");
        System.out.println("    - idProduccion: " + detalle.getIdProduccion());
        System.out.println("    - cantidad: " + detalle.getCantidad());
        System.out.println("    - precioUnitario: " + detalle.getPrecioUnitario());
      }

      System.out.println("Validación pasada, llamando al servicio");
      VentaDTO ventaGuardada =
          ventaService.guardarVentaConDetalles(requestBody.getVenta(), requestBody.getDetalles());

      System.out.println("Venta guardada exitosamente: " + ventaGuardada);
      System.out.println("=== FIN DEBUG CREAR VENTA CON DETALLES ===");
      return ResponseEntity.status(HttpStatus.CREATED).body(ventaGuardada);
    } catch (IllegalArgumentException e) {
      System.err.println("ERROR de validación al crear venta con detalles: " + e.getMessage());
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("{\"error\": \"Error de validación: " + e.getMessage().replace("\"", "'") + "\"}");
    } catch (Exception e) {
      System.err.println("ERROR interno al crear venta con detalles: " + e.getMessage());
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("{\"error\": \"Error interno: " + e.getMessage().replace("\"", "'") + "\"}");
    }
  }

  /** Actualiza una venta existente. */
  @PutMapping("/{idVenta}")
  public ResponseEntity<VentaDTO> actualizarVenta(
      @PathVariable Integer idVenta, @Valid @RequestBody VentaDTO ventaDTO) {
    try {
      VentaDTO ventaActualizada = ventaService.actualizarVenta(idVenta, ventaDTO);
      if (ventaActualizada != null) {
        return ResponseEntity.ok(ventaActualizada);
      } else {
        return ResponseEntity.notFound().build();
      }
    } catch (IllegalArgumentException e) {
      System.err.println("Error de validación al actualizar venta: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    } catch (Exception e) {
      System.err.println("Error interno al actualizar venta: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /** Elimina una venta y todos sus detalles asociados. */
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
      System.err.println("Error al eliminar venta: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /** Clase interna para encapsular la petición de venta con detalles. */
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

    @Override
    public String toString() {
      return "VentaConDetallesRequest{" + "venta=" + venta + ", detalles=" + detalles + '}';
    }
  }
}
