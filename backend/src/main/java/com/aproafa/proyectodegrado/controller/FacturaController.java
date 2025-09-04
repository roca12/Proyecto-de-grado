package com.aproafa.proyectodegrado.controller;

import com.aproafa.proyectodegrado.model.dto.DetalleVentaDTO;
import com.aproafa.proyectodegrado.model.dto.VentaDTO;
import com.aproafa.proyectodegrado.service.FacturaService;
import com.aproafa.proyectodegrado.service.VentaService;
import com.itextpdf.text.DocumentException;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la gestión y generación de facturas.
 *
 * <p>Esta clase maneja todas las operaciones HTTP relacionadas con la generación de facturas en
 * formato PDF. Se encarga de procesar las peticiones de facturación y coordinar con los servicios
 * correspondientes para generar documentos profesionales de facturación.
 *
 * <p>Proporciona endpoints especializados para la generación de facturas basadas en ventas
 * registradas, permitiendo la descarga directa de documentos PDF con formato profesional.
 *
 * @author Anderson Zuluaga
 * @version 1.0
 * @since 2023
 */
@RestController
@RequestMapping("/api/facturas")
public class FacturaController {

  /** Servicio para la generación de facturas en PDF. */
  private final FacturaService facturaService;

  /** Servicio para la gestión de ventas. */
  private final VentaService ventaService;

  /**
   * Constructor para inyección de dependencias.
   *
   * @param facturaService Servicio de generación de facturas.
   * @param ventaService Servicio de gestión de ventas.
   */
  @Autowired
  public FacturaController(FacturaService facturaService, VentaService ventaService) {
    this.facturaService = facturaService;
    this.ventaService = ventaService;
  }

  /**
   * Genera y descarga una factura en formato PDF para una venta específica.
   *
   * <p>Este endpoint consulta los datos de una venta y sus detalles asociados, luego genera un
   * documento PDF profesional que puede ser descargado directamente por el cliente. La factura
   * incluye toda la información relevante de la transacción: productos, cantidades, precios y
   * totales.
   *
   * <p>El archivo PDF generado se envía como respuesta HTTP con los headers apropiados para forzar
   * la descarga en el navegador del usuario.
   *
   * @param idVenta Identificador único de la venta para la cual generar la factura.
   * @return ResponseEntity con el archivo PDF como array de bytes y headers de descarga (HTTP 200),
   *     o HTTP 404 si la venta no existe, o HTTP 500 si ocurre un error en la generación.
   */
  @GetMapping("/generar/{idVenta}")
  public ResponseEntity<byte[]> generarFactura(@PathVariable Integer idVenta) {
    try {
      // Obtener los datos de la venta
      VentaDTO venta = ventaService.obtenerVentaPorId(idVenta);
      if (venta == null) {
        return ResponseEntity.notFound().build();
      }

      // Obtener los detalles de la venta
      List<DetalleVentaDTO> detalles = ventaService.obtenerDetallesDeVenta(idVenta);

      // Generar el PDF de la factura
      byte[] pdfBytes = facturaService.generarFacturaPdf(venta, detalles);

      // Configurar headers para la descarga del archivo
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_PDF);
      headers.setContentDispositionFormData("attachment", "factura_" + idVenta + ".pdf");
      headers.setContentLength(pdfBytes.length);

      return ResponseEntity.ok().headers(headers).body(pdfBytes);

    } catch (DocumentException | IOException e) {
      // Log del error para debugging
      System.err.println("Error generando factura PDF: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    } catch (Exception e) {
      // Error general
      System.err.println("Error inesperado generando factura: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Genera una factura en formato PDF para visualización en línea.
   *
   * <p>Similar al endpoint de descarga, pero configura los headers HTTP para que el PDF se muestre
   * directamente en el navegador en lugar de forzar la descarga. Útil para vista previa de
   * facturas.
   *
   * <p>Permite a los usuarios revisar el contenido de la factura antes de decidir si descargarla o
   * imprimirla.
   *
   * @param idVenta Identificador único de la venta.
   * @return ResponseEntity con el archivo PDF para visualización en línea (HTTP 200), o HTTP 404 si
   *     la venta no existe, o HTTP 500 si ocurre un error en la generación.
   */
  @GetMapping("/visualizar/{idVenta}")
  public ResponseEntity<byte[]> visualizarFactura(@PathVariable Integer idVenta) {
    try {
      // Obtener los datos de la venta
      VentaDTO venta = ventaService.obtenerVentaPorId(idVenta);
      if (venta == null) {
        return ResponseEntity.notFound().build();
      }

      // Obtener los detalles de la venta
      List<DetalleVentaDTO> detalles = ventaService.obtenerDetallesDeVenta(idVenta);

      // Generar el PDF de la factura
      byte[] pdfBytes = facturaService.generarFacturaPdf(venta, detalles);

      // Configurar headers para visualización en línea
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_PDF);
      headers.add("Content-Disposition", "inline; filename=factura_" + idVenta + ".pdf");
      headers.setContentLength(pdfBytes.length);

      return ResponseEntity.ok().headers(headers).body(pdfBytes);

    } catch (DocumentException | IOException e) {
      System.err.println("Error generando factura PDF para visualización: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    } catch (Exception e) {
      System.err.println(
          "Error inesperado generando factura para visualización: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Genera una factura personalizada basada en datos proporcionados directamente.
   *
   * <p>Este endpoint permite generar facturas sin necesidad de tener una venta previamente
   * registrada en la base de datos. Útil para generar facturas de prueba o para sistemas que
   * manejan datos de venta externamente.
   *
   * <p>Recibe directamente los datos de venta y detalles en el cuerpo de la petición HTTP y genera
   * el PDF correspondiente.
   *
   * @param requestBody Objeto que contiene los datos de venta y detalles.
   * @return ResponseEntity con el archivo PDF generado (HTTP 200), o HTTP 400 si los datos son
   *     inválidos, o HTTP 500 si ocurre un error en la generación.
   */
  @PostMapping("/generar-personalizada")
  public ResponseEntity<byte[]> generarFacturaPersonalizada(
      @RequestBody FacturaPersonalizadaRequest requestBody) {
    try {
      // Validar que se proporcionen los datos necesarios
      if (requestBody.getVenta() == null) {
        return ResponseEntity.badRequest().build();
      }

      // Inicializar lista de detalles si es null
      List<DetalleVentaDTO> detalles =
          requestBody.getDetalles() != null ? requestBody.getDetalles() : List.of();

      // Generar el PDF de la factura
      byte[] pdfBytes = facturaService.generarFacturaPdf(requestBody.getVenta(), detalles);

      // Configurar headers para la descarga
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_PDF);
      headers.setContentDispositionFormData("attachment", "factura_personalizada.pdf");
      headers.setContentLength(pdfBytes.length);

      return ResponseEntity.ok().headers(headers).body(pdfBytes);

    } catch (DocumentException | IOException e) {
      System.err.println("Error generando factura personalizada: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    } catch (Exception e) {
      System.err.println("Error inesperado generando factura personalizada: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
  }

  /**
   * Verifica si una venta tiene los datos suficientes para generar una factura.
   *
   * <p>Endpoint utilitario que valida si una venta específica contiene toda la información
   * necesaria para generar una factura válida. Útil para validaciones del lado del cliente antes de
   * intentar generar el documento PDF.
   *
   * @param idVenta Identificador único de la venta a validar.
   * @return ResponseEntity con información sobre la validez de los datos (HTTP 200), o HTTP 404 si
   *     la venta no existe.
   */
  @GetMapping("/validar/{idVenta}")
  public ResponseEntity<ValidacionFacturaResponse> validarDatosFactura(
      @PathVariable Integer idVenta) {
    try {
      VentaDTO venta = ventaService.obtenerVentaPorId(idVenta);
      if (venta == null) {
        return ResponseEntity.notFound().build();
      }

      List<DetalleVentaDTO> detalles = ventaService.obtenerDetallesDeVenta(idVenta);

      ValidacionFacturaResponse validacion = new ValidacionFacturaResponse();
      validacion.setVentaValida(true);
      validacion.setTieneDetalles(!detalles.isEmpty());
      validacion.setCantidadDetalles(detalles.size());
      validacion.setMensaje("Datos válidos para generar factura");

      // Validaciones adicionales
      if (venta.getTotal() == null || venta.getTotal().doubleValue() <= 0) {
        validacion.setVentaValida(false);
        validacion.setMensaje("La venta no tiene un total válido");
      }

      return ResponseEntity.ok(validacion);

    } catch (Exception e) {
      ValidacionFacturaResponse validacion = new ValidacionFacturaResponse();
      validacion.setVentaValida(false);
      validacion.setMensaje("Error validando los datos de la venta");
      return ResponseEntity.ok(validacion);
    }
  }

  /**
   * Clase interna para encapsular la petición de factura personalizada.
   *
   * <p>Utilizada para recibir datos de venta y detalles directamente en el cuerpo de peticiones
   * HTTP para generar facturas sin necesidad de consultar la base de datos.
   */
  public static class FacturaPersonalizadaRequest {

    /** Datos de la venta. */
    private VentaDTO venta;

    /** Lista de detalles de la venta. */
    private List<DetalleVentaDTO> detalles;

    /** Constructor por defecto. */
    public FacturaPersonalizadaRequest() {}

    /**
     * Obtiene los datos de la venta.
     *
     * @return VentaDTO con los datos de la venta.
     */
    public VentaDTO getVenta() {
      return venta;
    }

    /**
     * Establece los datos de la venta.
     *
     * @param venta Datos de la venta.
     */
    public void setVenta(VentaDTO venta) {
      this.venta = venta;
    }

    /**
     * Obtiene la lista de detalles.
     *
     * @return Lista de DetalleVentaDTO.
     */
    public List<DetalleVentaDTO> getDetalles() {
      return detalles;
    }

    /**
     * Establece la lista de detalles.
     *
     * @param detalles Lista de detalles de venta.
     */
    public void setDetalles(List<DetalleVentaDTO> detalles) {
      this.detalles = detalles;
    }
  }

  /**
   * Clase interna para la respuesta de validación de facturas.
   *
   * <p>Encapsula la información sobre la validez de los datos de una venta para la generación de
   * facturas.
   */
  public static class ValidacionFacturaResponse {

    /** Indica si la venta es válida para facturación. */
    private boolean ventaValida;

    /** Indica si la venta tiene detalles asociados. */
    private boolean tieneDetalles;

    /** Cantidad de detalles encontrados. */
    private int cantidadDetalles;

    /** Mensaje descriptivo del estado de validación. */
    private String mensaje;

    /** Constructor por defecto. */
    public ValidacionFacturaResponse() {}

    /**
     * Verifica si la venta es válida.
     *
     * @return true si la venta es válida, false en caso contrario.
     */
    public boolean isVentaValida() {
      return ventaValida;
    }

    /**
     * Establece la validez de la venta.
     *
     * @param ventaValida Estado de validez de la venta.
     */
    public void setVentaValida(boolean ventaValida) {
      this.ventaValida = ventaValida;
    }

    /**
     * Verifica si tiene detalles asociados.
     *
     * @return true si tiene detalles, false en caso contrario.
     */
    public boolean isTieneDetalles() {
      return tieneDetalles;
    }

    /**
     * Establece si tiene detalles.
     *
     * @param tieneDetalles Estado de presencia de detalles.
     */
    public void setTieneDetalles(boolean tieneDetalles) {
      this.tieneDetalles = tieneDetalles;
    }

    /**
     * Obtiene la cantidad de detalles.
     *
     * @return Número de detalles encontrados.
     */
    public int getCantidadDetalles() {
      return cantidadDetalles;
    }

    /**
     * Establece la cantidad de detalles.
     *
     * @param cantidadDetalles Número de detalles.
     */
    public void setCantidadDetalles(int cantidadDetalles) {
      this.cantidadDetalles = cantidadDetalles;
    }

    /**
     * Obtiene el mensaje de validación.
     *
     * @return Mensaje descriptivo del estado.
     */
    public String getMensaje() {
      return mensaje;
    }

    /**
     * Establece el mensaje de validación.
     *
     * @param mensaje Mensaje descriptivo.
     */
    public void setMensaje(String mensaje) {
      this.mensaje = mensaje;
    }
  }
}
