import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import authService from "../authService";
import logo from "./../assets/APROAFA2.png";
import watermarkImage from "./../assets/LogoBosque.png";
import {
  FaSignOutAlt,
  FaPlus,
  FaTrash
} from "react-icons/fa";
import "./RegistrarProduccion.css";

const RegistrarProduccion = () => {
  const [user, setUser] = useState(null);
  const [showDropdown, setShowDropdown] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);
  const [productos, setProductos] = useState([]);

  // Estados para insumos
  const [insumosDisponibles, setInsumosDisponibles] = useState([]);
  const [usosInsumos, setUsosInsumos] = useState([]);
  const [loadingInsumos, setLoadingInsumos] = useState(false);
  const [nombresInsumos, setNombresInsumos] = useState({});

  const [formData, setFormData] = useState({
    idProducto: "",
    fechaSiembra: "",
    fechaCosecha: "",
    estado: "EN_CRECIMIENTO",
    cantidadCosechada: "",
  });

  const estadosProduccion = [
    { value: "EN_CRECIMIENTO", label: "En Crecimiento" },
    { value: "LISTO_PARA_COSECHA", label: "Listo para Cosecha" },
    { value: "COSECHADO", label: "Cosechado" },
  ];

  const navigate = useNavigate();

  useEffect(() => {
    const loadInitialData = async () => {
      try {
        const currentUser = authService.getCurrentUser();
        if (!currentUser) {
          throw new Error("Usuario no autenticado");
        }
        setUser(currentUser);

        // Cargar productos filtrados por finca
        const productosResponse = await fetch(
            `http://localhost:8080/api/productos/finca/${currentUser.idFinca}`,
            {
              headers: {
                Authorization: `Bearer ${localStorage.getItem("authToken")}`,
              },
            }
        );
        if (!productosResponse.ok)
          throw new Error("Error al obtener productos");
        const productosData = await productosResponse.json();
        setProductos(productosData);

        // Cargar insumos
        const insumosResponse = await fetch(
            `http://localhost:8080/insumos/finca/${currentUser.idFinca}`,
            {
              headers: {
                Authorization: `Bearer ${localStorage.getItem("authToken")}`,
              },
            }
        );
        if (!insumosResponse.ok) throw new Error("Error al obtener insumos");
        const insumosData = await insumosResponse.json();

        // Crear mapeo de ID a nombre
        const nombresMap = {};
        insumosData.forEach(insumo => {
          nombresMap[insumo.idInsumo] = insumo.nombre;
        });
        setNombresInsumos(nombresMap);
        setInsumosDisponibles(insumosData);

      } catch (error) {
        console.error("Error al cargar datos iniciales:", error);
        setError(error.message);
      }
    };

    loadInitialData();
  }, [navigate]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;

    // Si cambia el estado, resetear cantidad cosechada si no es COSECHADO
    if (name === "estado" && value !== "COSECHADO") {
      setFormData({
        ...formData,
        [name]: value,
        cantidadCosechada: "",
        fechaCosecha: ""
      });
    } else {
      setFormData({
        ...formData,
        [name]: value,
      });
    }
  };

  // Funciones para manejar insumos
  const agregarInsumo = () => {
    setUsosInsumos([
      ...usosInsumos,
      {
        idInsumo: "",
        cantidad: "",
        fecha: formData.fechaSiembra || new Date().toISOString().split('T')[0]
      }
    ]);
  };

  const actualizarInsumo = (index, campo, valor) => {
    const nuevosUsos = [...usosInsumos];
    nuevosUsos[index][campo] = valor;
    setUsosInsumos(nuevosUsos);
  };

  const eliminarInsumo = (index) => {
    const nuevosUsos = [...usosInsumos];
    nuevosUsos.splice(index, 1);
    setUsosInsumos(nuevosUsos);
  };

  const obtenerNombreInsumo = (idInsumo) => {
    return nombresInsumos[idInsumo] || `Insumo ${idInsumo}`;
  };

  const validarFormulario = () => {
    if (!formData.idProducto) {
      setError("Debe seleccionar un producto");
      return false;
    }

    if (!formData.fechaSiembra) {
      setError("Debe seleccionar una fecha de siembra");
      return false;
    }

    if (formData.estado === "COSECHADO") {
      if (!formData.fechaCosecha) {
        setError("Para estado COSECHADO, la fecha de cosecha es obligatoria");
        return false;
      }
      if (!formData.cantidadCosechada) {
        setError("Para estado COSECHADO, la cantidad cosechada es obligatoria");
        return false;
      }
      if (formData.fechaCosecha < formData.fechaSiembra) {
        setError("La fecha de cosecha no puede ser anterior a la fecha de siembra");
        return false;
      }
    }

    // Validar insumos si los hay
    for (let i = 0; i < usosInsumos.length; i++) {
      const insumo = usosInsumos[i];
      if (insumo.idInsumo && (!insumo.cantidad || insumo.cantidad <= 0)) {
        setError(`Debe especificar una cantidad válida para el insumo ${i + 1}`);
        return false;
      }
      if (insumo.cantidad && !insumo.idInsumo) {
        setError(`Debe seleccionar un insumo para la cantidad especificada en la posición ${i + 1}`);
        return false;
      }
    }

    return true;
  };

  const handleRegister = async () => {
    if (!validarFormulario()) return;

    if (!user?.idFinca) {
      setError("No se pudo determinar la finca asociada");
      return;
    }

    try {
      setLoading(true);
      setError(null);

      // Filtrar solo insumos válidos
      const insumosValidos = usosInsumos.filter(
          insumo => insumo.idInsumo && insumo.cantidad && insumo.cantidad > 0
      );

      const produccionData = {
        idProducto: parseInt(formData.idProducto),
        idFinca: user.idFinca,
        fechaSiembra: formData.fechaSiembra,
        fechaCosecha: formData.estado === "COSECHADO" ? formData.fechaCosecha : null,
        estado: formData.estado,
        cantidadCosechada: formData.estado === "COSECHADO"
            ? parseFloat(formData.cantidadCosechada)
            : null,
        usosInsumos: insumosValidos
      };

      const response = await fetch("http://localhost:8080/produccion", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${localStorage.getItem("authToken")}`,
        },
        body: JSON.stringify(produccionData),
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || "Error al registrar la producción");
      }

      setSuccess(true);

      // Redirigir después de un breve delay para mostrar el mensaje de éxito
      setTimeout(() => {
        navigate("/produccion");
      }, 1500);

    } catch (error) {
      console.error("Error al registrar producción:", error);
      setError(error.message || "Ocurrió un error al registrar la producción");
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    authService.logout();
    navigate("/");
  };

  const handleCancel = () => {
    navigate("/produccion");
  };

  return (
      <div className="app-container">
        <div className="topbar">
          <img src={logo} alt="Logo" className="logo-mini" />
          <div className="user-dropdown" onClick={() => setShowDropdown(!showDropdown)}>
            <span className="username">{user?.nombre || "Usuario"} ▼</span>
            {showDropdown && (
                <div className="dropdown-menu">
                  <button className="dropdown-btn" onClick={handleLogout}>
                    <FaSignOutAlt style={{ marginRight: "8px" }} /> Cerrar sesión
                  </button>
                </div>
            )}
          </div>
        </div>

        <div className="content-area">
          <div className="registro-actividad-container">
            <div className="registro-header">
              <h2 className="registro-title">Registrar Nueva Producción</h2>
              <p className="registro-subtitle">Complete la información de la producción y opcionalmente agregue insumos utilizados</p>
            </div>

            {error && (
                <div className="message error-message">
                  <strong>Error:</strong> {error}
                </div>
            )}

            {success && (
                <div className="message success-message">
                  <strong>¡Éxito!</strong> Producción registrada correctamente. Redirigiendo...
                </div>
            )}

            <form className="registro-form" onSubmit={(e) => e.preventDefault()}>
              {/* Información básica de la producción */}
              <div className="form-section">
                <h3 className="section-title">Información de la Producción</h3>

                <div className="form-group">
                  <label htmlFor="idProducto">Producto *</label>
                  <select
                      id="idProducto"
                      name="idProducto"
                      className="registro-input"
                      value={formData.idProducto}
                      onChange={handleInputChange}
                      required
                      disabled={loading}
                  >
                    <option value="">Seleccione un producto</option>
                    {productos.map((producto) => (
                        <option key={producto.idProducto} value={producto.idProducto}>
                          {producto.nombre}
                        </option>
                    ))}
                  </select>
                </div>

                <div className="form-row">
                  <div className="form-group">
                    <label htmlFor="fechaSiembra">Fecha de Siembra *</label>
                    <input
                        id="fechaSiembra"
                        type="date"
                        name="fechaSiembra"
                        className="registro-input"
                        value={formData.fechaSiembra}
                        onChange={handleInputChange}
                        required
                        disabled={loading}
                    />
                  </div>

                  <div className="form-group">
                    <label htmlFor="estado">Estado *</label>
                    <select
                        id="estado"
                        name="estado"
                        className="registro-input"
                        value={formData.estado}
                        onChange={handleInputChange}
                        required
                        disabled={loading}
                    >
                      {estadosProduccion.map((estado) => (
                          <option key={estado.value} value={estado.value}>
                            {estado.label}
                          </option>
                      ))}
                    </select>
                  </div>
                </div>

                {formData.estado === "COSECHADO" && (
                    <div className="form-row">
                      <div className="form-group">
                        <label htmlFor="fechaCosecha">Fecha de Cosecha *</label>
                        <input
                            id="fechaCosecha"
                            type="date"
                            name="fechaCosecha"
                            className="registro-input"
                            value={formData.fechaCosecha}
                            onChange={handleInputChange}
                            required
                            disabled={loading}
                            min={formData.fechaSiembra}
                        />
                      </div>

                      <div className="form-group">
                        <label htmlFor="cantidadCosechada">Cantidad Cosechada (kg) *</label>
                        <input
                            id="cantidadCosechada"
                            type="number"
                            name="cantidadCosechada"
                            className="registro-input"
                            value={formData.cantidadCosechada}
                            onChange={handleInputChange}
                            min="0"
                            step="0.01"
                            required
                            disabled={loading}
                        />
                      </div>
                    </div>
                )}
              </div>

              {/* Sección de insumos */}
              <div className="form-section">
                <div className="section-header">
                  <h3 className="section-title">Insumos Utilizados (Opcional)</h3>
                  <button
                      type="button"
                      onClick={agregarInsumo}
                      className="btn-agregar-insumo"
                      disabled={loading}
                  >
                    <FaPlus /> Agregar Insumo
                  </button>
                </div>

                {usosInsumos.length === 0 ? (
                    <div className="no-insumos">
                      <p>No hay insumos agregados.</p>
                      <p><small>Los insumos son opcionales. Puede agregar los que utilizará en esta producción.</small></p>
                    </div>
                ) : (
                    <div className="insumos-list">
                      {usosInsumos.map((insumo, index) => {
                        const insumoSeleccionado = insumosDisponibles.find(i => i.idInsumo === parseInt(insumo.idInsumo));
                        return (
                            <div key={index} className="insumo-item">
                              <div className="insumo-header">
                                <span className="insumo-numero">Insumo #{index + 1}</span>
                                <button
                                    type="button"
                                    onClick={() => eliminarInsumo(index)}
                                    className="btn-eliminar-insumo"
                                    disabled={loading}
                                >
                                  <FaTrash />
                                </button>
                              </div>

                              <div className="insumo-fields">
                                <div className="form-group">
                                  <label>Seleccionar Insumo</label>
                                  <select
                                      value={insumo.idInsumo}
                                      onChange={(e) => actualizarInsumo(index, 'idInsumo', parseInt(e.target.value))}
                                      className="registro-input"
                                      disabled={loading}
                                  >
                                    <option value="">Seleccione un insumo</option>
                                    {insumosDisponibles.map((i) => (
                                        <option key={i.idInsumo} value={i.idInsumo}>
                                          {i.nombre} (Disponible: {i.cantidadDisponible} {i.unidadMedida || ''})
                                        </option>
                                    ))}
                                  </select>
                                </div>

                                <div className="form-group">
                                  <label>Cantidad</label>
                                  <input
                                      type="number"
                                      step="0.01"
                                      min="0.01"
                                      max={insumoSeleccionado ? insumoSeleccionado.cantidadDisponible : undefined}
                                      placeholder="Cantidad a utilizar"
                                      value={insumo.cantidad}
                                      onChange={(e) => actualizarInsumo(index, 'cantidad', parseFloat(e.target.value))}
                                      className="registro-input"
                                      disabled={loading}
                                  />
                                  {insumoSeleccionado && (
                                      <small className="stock-info">
                                        Stock disponible: {insumoSeleccionado.cantidadDisponible} {insumoSeleccionado.unidadMedida || ''}
                                      </small>
                                  )}
                                </div>

                                <div className="form-group">
                                  <label>Fecha de Uso</label>
                                  <input
                                      type="date"
                                      value={insumo.fecha}
                                      onChange={(e) => actualizarInsumo(index, 'fecha', e.target.value)}
                                      className="registro-input"
                                      disabled={loading}
                                      max={formData.fechaSiembra}
                                  />
                                </div>
                              </div>
                            </div>
                        );
                      })}
                    </div>
                )}
              </div>

              <div className="registro-botones">
                <button
                    type="button"
                    onClick={handleCancel}
                    className="btn-cancelar"
                    disabled={loading}
                >
                  Cancelar
                </button>
                <button
                    type="button"
                    onClick={handleRegister}
                    className="btn-registrar"
                    disabled={loading || success}
                >
                  {loading ? "Registrando..." : success ? "Registrado ✓" : "Registrar Producción"}
                </button>
              </div>
            </form>
          </div>
        </div>

        <div className="watermark">
          <img src={watermarkImage} alt="Marca de agua" />
        </div>
      </div>
  );
};

export default RegistrarProduccion;