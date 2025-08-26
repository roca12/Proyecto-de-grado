package com.example.proyecto_de_grado.service;

import com.example.proyecto_de_grado.model.dto.DetalleVentaDTO;
import com.example.proyecto_de_grado.model.dto.VentaDTO;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;

/**
 * Servicio encargado de generar facturas en formato PDF a partir de los datos de una venta. Utiliza
 * la librería iText para construir y formatear el documento PDF.
 *
 * @author Anderson Zuluaga
 */
@Service
public class FacturaService {

  /**
   * Genera un documento PDF que representa la factura de una venta.
   *
   * @param venta La venta que contiene los datos generales de la transacción.
   * @param detalles La lista de producciones vendidas (detalles de la venta).
   * @return Un arreglo de bytes que representa el archivo PDF generado.
   * @throws DocumentException Si ocurre un error al generar el documento PDF.
   * @throws IOException Si ocurre un error de entrada/salida.
   */
  public byte[] generarFacturaPdf(VentaDTO venta, List<DetalleVentaDTO> detalles)
          throws DocumentException, IOException {

    Document document = new Document();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    try {
      PdfWriter.getInstance(document, baos);
      document.open();

      agregarEncabezado(document);
      agregarInformacionVenta(document, venta);
      agregarTablaProducciones(document, detalles);
      agregarTotal(document, venta);
      agregarPie(document);

    } finally {
      if (document.isOpen()) {
        document.close();
      }
    }

    return baos.toByteArray();
  }

  /**
   * Agrega el encabezado principal del documento PDF.
   *
   * @param document Documento al cual se le agregará el encabezado.
   * @throws DocumentException Si ocurre un error al agregar el encabezado.
   */
  private void agregarEncabezado(Document document) throws DocumentException {
    Font tituloFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
    Paragraph titulo = new Paragraph("FACTURA DE VENTA", tituloFont);
    titulo.setAlignment(Element.ALIGN_CENTER);
    document.add(titulo);
    document.add(Chunk.NEWLINE);
  }

  /**
   * Agrega la información general de la venta al documento.
   *
   * @param document Documento PDF.
   * @param venta Objeto con los datos generales de la venta.
   * @throws DocumentException Si ocurre un error al agregar los datos.
   */
  private void agregarInformacionVenta(Document document, VentaDTO venta) throws DocumentException {
    Font infoFont = new Font(Font.FontFamily.HELVETICA, 12);
    document.add(new Paragraph("Número de Factura: " + venta.getIdVenta(), infoFont));
    document.add(new Paragraph("ID Cliente: " + venta.getIdCliente(), infoFont));
    document.add(new Paragraph("ID Empleado: " + venta.getIdPersona(), infoFont));

    if (venta.getFechaVenta() != null) {
      document.add(
              new Paragraph(
                      "Fecha: "
                              + venta.getFechaVenta().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                      infoFont));
    }

    if (venta.getMetodoPago() != null) {
      document.add(new Paragraph("Método de pago: " + venta.getMetodoPago(), infoFont));
    }
    document.add(Chunk.NEWLINE);
  }

  /**
   * Agrega una tabla con las producciones vendidas (detalles de la venta).
   *
   * @param document Documento PDF.
   * @param detalles Lista de producciones vendidas.
   * @throws DocumentException Si ocurre un error al construir la tabla.
   */
  private void agregarTablaProducciones(Document document, List<DetalleVentaDTO> detalles)
          throws DocumentException {

    PdfPTable table = new PdfPTable(5);
    table.setWidthPercentage(100);
    table.setSpacingBefore(10f);
    table.setSpacingAfter(10f);
    table.setWidths(new float[] {2f, 1.5f, 2f, 2f, 1.5f});

    // Encabezados actualizados
    Stream.of("ID Producción", "Cantidad", "Precio Unitario", "Subtotal", "ID Detalle")
            .forEach(
                    header -> {
                      PdfPCell cell = new PdfPCell(new Phrase(header));
                      cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                      cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                      cell.setPadding(8);
                      table.addCell(cell);
                    });

    for (DetalleVentaDTO detalle : detalles) {
      agregarFilaProduccion(table, detalle);
    }

    document.add(table);
  }

  /**
   * Agrega una fila en la tabla con los datos de una producción vendida.
   *
   * @param table Tabla PDF a la que se agregará la fila.
   * @param detalle Detalle individual de la venta.
   */
  private void agregarFilaProduccion(PdfPTable table, DetalleVentaDTO detalle) {
    table.addCell(crearCeldaCentrada(String.valueOf(detalle.getIdProduccion()))); // ✅ Actualizado
    table.addCell(crearCeldaCentrada(String.valueOf(detalle.getCantidad())));

    String precioStr =
            detalle.getPrecioUnitario() != null ? "$" + detalle.getPrecioUnitario() : "$0.00";
    table.addCell(crearCeldaDerecha(precioStr));

    String subtotalStr = detalle.getSubtotal() != null ? "$" + detalle.getSubtotal() : "$0.00";
    table.addCell(crearCeldaDerecha(subtotalStr));

    table.addCell(crearCeldaCentrada(String.valueOf(detalle.getIdDetalle())));
  }

  /**
   * Agrega el total de la venta en el documento.
   *
   * @param document Documento PDF.
   * @param venta Venta con el valor total.
   * @throws DocumentException Si ocurre un error al agregar el total.
   */
  private void agregarTotal(Document document, VentaDTO venta) throws DocumentException {
    Font totalFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
    String totalStr = venta.getTotal() != null ? "$" + venta.getTotal() : "$0.00";
    Paragraph total = new Paragraph("TOTAL: " + totalStr, totalFont);
    total.setAlignment(Element.ALIGN_RIGHT);
    total.setSpacingBefore(10f);
    document.add(total);
  }

  /**
   * Agrega un mensaje de agradecimiento al final del documento.
   *
   * @param document Documento PDF.
   * @throws DocumentException Si ocurre un error al agregar el pie de página.
   */
  private void agregarPie(Document document) throws DocumentException {
    document.add(Chunk.NEWLINE);
    Font pieFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
    Paragraph pie = new Paragraph("Gracias por su compra", pieFont);
    pie.setAlignment(Element.ALIGN_CENTER);
    document.add(pie);
  }

  /**
   * Crea una celda centrada para la tabla.
   *
   * @param texto Contenido de la celda.
   * @return Celda con alineación centrada.
   */
  private PdfPCell crearCeldaCentrada(String texto) {
    PdfPCell cell = new PdfPCell(new Phrase(texto));
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    cell.setPadding(5);
    return cell;
  }

  /**
   * Crea una celda alineada a la derecha para la tabla.
   *
   * @param texto Contenido de la celda.
   * @return Celda con alineación derecha.
   */
  private PdfPCell crearCeldaDerecha(String texto) {
    PdfPCell cell = new PdfPCell(new Phrase(texto));
    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
    cell.setPadding(5);
    return cell;
  }
}
