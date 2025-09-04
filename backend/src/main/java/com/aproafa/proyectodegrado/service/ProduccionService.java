package com.aproafa.proyectodegrado.service;

import com.aproafa.proyectodegrado.model.dto.ProduccionDTO;
import com.aproafa.proyectodegrado.model.dto.UsoInsumoProduccionDTO;
import com.aproafa.proyectodegrado.model.entity.EstadoProduccion;
import com.aproafa.proyectodegrado.model.entity.Finca;
import com.aproafa.proyectodegrado.model.entity.Insumo;
import com.aproafa.proyectodegrado.model.entity.Produccion;
import com.aproafa.proyectodegrado.model.entity.Producto;
import com.aproafa.proyectodegrado.model.entity.UsoInsumoProduccion;
import com.aproafa.proyectodegrado.repository.InsumoRepository;
import com.aproafa.proyectodegrado.repository.ProduccionRepository;
import com.aproafa.proyectodegrado.repository.UsoInsumoProduccionRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProduccionService {

  @Autowired private ProduccionRepository produccionRepo;

  @Autowired private InventarioProductoService inventarioService;

  @Autowired private UsoInsumoProduccionRepository usoInsumoProduccionRepo;

  @Autowired private InsumoRepository insumoRepository;

  @Autowired private InsumoService insumoService;

  @Transactional
  public ProduccionDTO crearProduccion(ProduccionDTO dto) {
    Produccion prod = new Produccion();
    prod.setProducto(new Producto(dto.getIdProducto()));
    prod.setFinca(new Finca(dto.getIdFinca()));
    prod.setFechaSiembra(dto.getFechaSiembra());
    prod.setEstado(dto.getEstado());

    if (dto.getEstado() == EstadoProduccion.COSECHADO) {
      prod.setFechaCosecha(dto.getFechaCosecha());
      prod.setCantidadCosechada(dto.getCantidadCosechada());
      inventarioService.actualizarInventario(dto.getIdProducto(), dto.getCantidadCosechada());
    }

    prod = produccionRepo.save(prod);

    if (dto.getUsosInsumos() != null && !dto.getUsosInsumos().isEmpty()) {
      for (UsoInsumoProduccionDTO usoDto : dto.getUsosInsumos()) {
        Insumo insumo =
            insumoRepository
                .findById(usoDto.getIdInsumo())
                .orElseThrow(
                    () ->
                        new RuntimeException(
                            "Insumo no encontrado con ID: " + usoDto.getIdInsumo()));

        if (insumo.getCantidadDisponible().compareTo(usoDto.getCantidad()) < 0) {
          throw new RuntimeException("Stock insuficiente para el insumo: " + insumo.getNombre());
        }

        insumoService.registrarUsoInsumo(usoDto.getIdInsumo(), usoDto.getCantidad());

        UsoInsumoProduccion uso = new UsoInsumoProduccion();
        uso.setProduccion(prod);
        uso.setInsumo(insumo);
        uso.setCantidad(usoDto.getCantidad());
        uso.setFecha(usoDto.getFechaUso() != null ? usoDto.getFechaUso() : dto.getFechaSiembra());

        usoInsumoProduccionRepo.save(uso);
        prod.getUsosInsumos().add(uso);
      }
    }

    return convertirADTO(prod);
  }

  @Transactional
  public void cosechar(Integer idProduccion, BigDecimal cantidadCosechada, LocalDate fechaCosecha) {
    Produccion prod =
        produccionRepo
            .findById(idProduccion)
            .orElseThrow(() -> new RuntimeException("Producción no encontrada"));

    prod.setCantidadCosechada(cantidadCosechada);
    prod.setFechaCosecha(fechaCosecha);
    prod.setEstado(EstadoProduccion.COSECHADO);
    produccionRepo.save(prod);
    inventarioService.actualizarInventario(prod.getProducto().getIdProducto(), cantidadCosechada);
  }

  public List<ProduccionDTO> listarProducciones() {
    return produccionRepo.findAll().stream().map(this::convertirADTO).collect(Collectors.toList());
  }

  public ProduccionDTO obtenerProduccionPorId(Integer id) {
    return produccionRepo
        .findById(id)
        .map(this::convertirADTO)
        .orElseThrow(() -> new RuntimeException("Producción no encontrada"));
  }

  public List<UsoInsumoProduccionDTO> obtenerInsumosPorProduccion(Integer idProduccion) {
    return usoInsumoProduccionRepo.findByProduccionIdProduccion(idProduccion).stream()
        .map(this::convertirUsoInsumoADTO)
        .collect(Collectors.toList());
  }

  @Transactional
  public void actualizarEstado(Integer idProduccion, EstadoProduccion nuevoEstado) {
    Produccion prod =
        produccionRepo
            .findById(idProduccion)
            .orElseThrow(() -> new RuntimeException("Producción no encontrada"));

    if (prod.getEstado() == EstadoProduccion.COSECHADO) {
      throw new IllegalStateException("No se puede modificar una producción ya cosechada");
    }

    prod.setEstado(nuevoEstado);
    produccionRepo.save(prod);
  }

  public List<ProduccionDTO> listarPorFinca(Integer idFinca) {
    return produccionRepo.findByFinca_Id(idFinca).stream()
        .map(this::convertirADTO)
        .collect(Collectors.toList());
  }

  @Transactional
  public void eliminarProduccion(Integer idProduccion) {
    Produccion prod =
        produccionRepo
            .findById(idProduccion)
            .orElseThrow(() -> new RuntimeException("Producción no encontrada"));

    if (prod.getEstado() == EstadoProduccion.COSECHADO) {
      throw new IllegalStateException("No se puede eliminar una producción cosechada");
    }

    // Devolver insumos usados al stock
    for (UsoInsumoProduccion uso : prod.getUsosInsumos()) {
      Insumo insumo = uso.getInsumo();
      insumo.setCantidadDisponible(insumo.getCantidadDisponible().add(uso.getCantidad()));
      insumoRepository.save(insumo);
    }

    produccionRepo.delete(prod);
  }

  private ProduccionDTO convertirADTO(Produccion produccion) {
    ProduccionDTO dto = new ProduccionDTO();
    dto.setIdProduccion(produccion.getIdProduccion());
    dto.setIdFinca(produccion.getFinca().getId());
    dto.setIdProducto(produccion.getProducto().getIdProducto());
    dto.setFechaSiembra(produccion.getFechaSiembra());
    dto.setFechaCosecha(produccion.getFechaCosecha());
    dto.setCantidadCosechada(produccion.getCantidadCosechada());
    dto.setEstado(produccion.getEstado());

    if (produccion.getUsosInsumos() != null) {
      List<UsoInsumoProduccionDTO> usosDTO =
          produccion.getUsosInsumos().stream()
              .map(this::convertirUsoInsumoADTO)
              .collect(Collectors.toList());
      dto.setUsosInsumos(usosDTO);
    }

    return dto;
  }

  private UsoInsumoProduccionDTO convertirUsoInsumoADTO(UsoInsumoProduccion uso) {
    UsoInsumoProduccionDTO dto = new UsoInsumoProduccionDTO();
    dto.setIdInsumo(uso.getInsumo().getIdInsumo());
    dto.setCantidad(uso.getCantidad());
    dto.setFechaUso(uso.getFecha());
    return dto;
  }

  @Transactional
  public ProduccionDTO actualizarProduccion(Integer idProduccion, ProduccionDTO dto) {
    Produccion prod =
        produccionRepo
            .findById(idProduccion)
            .orElseThrow(() -> new RuntimeException("Producción no encontrada"));

    if (prod.getEstado() == EstadoProduccion.COSECHADO) {
      throw new IllegalStateException("No se puede modificar una producción ya cosechada");
    }

    prod.setProducto(new Producto(dto.getIdProducto()));
    prod.setFinca(new Finca(dto.getIdFinca()));
    prod.setFechaSiembra(dto.getFechaSiembra());
    prod.setEstado(dto.getEstado());

    // Manejo de insumos
    List<UsoInsumoProduccion> usosActuales =
        usoInsumoProduccionRepo.findByProduccionIdProduccion(idProduccion);

    // Eliminar usos que ya no están en el DTO
    for (UsoInsumoProduccion usoExistente : usosActuales) {
      boolean encontrado =
          dto.getUsosInsumos().stream()
              .anyMatch(
                  usoDto -> usoDto.getIdInsumo().equals(usoExistente.getInsumo().getIdInsumo()));

      if (!encontrado) {
        // Devolver insumo al stock
        Insumo insumo = usoExistente.getInsumo();
        insumo.setCantidadDisponible(
            insumo.getCantidadDisponible().add(usoExistente.getCantidad()));
        insumoRepository.save(insumo);
        usoInsumoProduccionRepo.delete(usoExistente);
      }
    }

    // Agregar o actualizar usos
    for (UsoInsumoProduccionDTO usoDto : dto.getUsosInsumos()) {
      // Validar que el DTO tenga los datos necesarios
      if (usoDto.getIdInsumo() == null || usoDto.getCantidad() == null) {
        throw new RuntimeException("Datos incompletos en el uso de insumo");
      }

      // Obtener el insumo con validación
      Insumo insumo =
          insumoRepository
              .findById(usoDto.getIdInsumo())
              .orElseThrow(
                  () ->
                      new RuntimeException("Insumo no encontrado con ID: " + usoDto.getIdInsumo()));

      // Buscar uso existente con manejo de nulos seguro
      UsoInsumoProduccion usoExistente =
          usosActuales.stream()
              .filter(
                  u ->
                      u != null
                          && u.getInsumo() != null
                          && usoDto.getIdInsumo().equals(u.getInsumo().getIdInsumo()))
              .findFirst()
              .orElse(null);

      if (usoExistente != null) {
        // Actualizar uso existente
        BigDecimal cantidadAnterior = usoExistente.getCantidad();
        BigDecimal nuevaCantidad = usoDto.getCantidad();
        BigDecimal diferencia = nuevaCantidad.subtract(cantidadAnterior);

        if (diferencia.compareTo(BigDecimal.ZERO) > 0) {
          // Verificar stock suficiente para la diferencia
          if (insumo.getCantidadDisponible().compareTo(diferencia) < 0) {
            throw new RuntimeException(
                String.format(
                    "Stock insuficiente para el insumo %s. Disponible: %s, Requerido: %s",
                    insumo.getNombre(), insumo.getCantidadDisponible(), diferencia));
          }
          insumo.setCantidadDisponible(insumo.getCantidadDisponible().subtract(diferencia));
        } else if (diferencia.compareTo(BigDecimal.ZERO) < 0) {
          // Devolver diferencia al stock
          insumo.setCantidadDisponible(insumo.getCantidadDisponible().add(diferencia.abs()));
        }

        // Actualizar el uso existente
        usoExistente.setCantidad(nuevaCantidad);
        usoExistente.setFecha(
            usoDto.getFechaUso() != null ? usoDto.getFechaUso() : LocalDate.now());
        insumoRepository.save(insumo);
        usoInsumoProduccionRepo.save(usoExistente);
      } else {
        // Crear nuevo uso con validación de stock
        if (insumo.getCantidadDisponible().compareTo(usoDto.getCantidad()) < 0) {
          throw new RuntimeException(
              String.format(
                  "Stock insuficiente para el insumo %s. Disponible: %s, Requerido: %s",
                  insumo.getNombre(), insumo.getCantidadDisponible(), usoDto.getCantidad()));
        }

        // Crear y guardar el nuevo uso
        UsoInsumoProduccion nuevoUso = new UsoInsumoProduccion();
        nuevoUso.setProduccion(prod);
        nuevoUso.setInsumo(insumo);
        nuevoUso.setCantidad(usoDto.getCantidad());
        nuevoUso.setFecha(usoDto.getFechaUso() != null ? usoDto.getFechaUso() : LocalDate.now());

        // Actualizar stock
        insumo.setCantidadDisponible(insumo.getCantidadDisponible().subtract(usoDto.getCantidad()));
        insumoRepository.save(insumo);
        usoInsumoProduccionRepo.save(nuevoUso);
      }
    }

    produccionRepo.save(prod);
    return convertirADTO(prod);
  }
}
