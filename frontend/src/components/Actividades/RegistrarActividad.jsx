import "./RegistrarActividad.css";
import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import authService from "../authService";
import logo from "./../assets/APROAFA2.png";
import watermarkImage from "./../assets/LogoBosque.png";
import { FaSignOutAlt, FaPlus, FaTrash } from "react-icons/fa";

const RegistrarActividad = () => {
  const [user, setUser] = useState(null);
  const [showDropdown, setShowDropdown] = useState(false);
  const [idFinca, setIdFinca] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);

  const [formData, setFormData] = useState({
    idTipoActividad: "",
    fechaInicio: "",
    fechaFin: "",
    descripcion: "",
  });

  const [insumosDisponibles, setInsumosDisponibles] = useState([]);
  const [usosInsumos, setUsosInsumos] = useState([]);
  const [loadingInsumos, setLoadingInsumos] = useState(false);

  const navigate = useNavigate();

  const tiposActividad = [
    { id: 1, nombre: "Fertilización" },
    { id: 2, nombre: "Riego" },
    { id: 3, nombre: "Control de plagas" },
    { id: 4, nombre: "Siembra" },
    { id: 5, nombre: "Cosecha" },
  ];

  useEffect(() => {
    const loadInitialData = async () => {
      try {
        const currentUser = authService.getCurrentUser();
        if (!currentUser) {
          setError("Usuario no autenticado");
          navigate("/");
          return;
        }

        setUser(currentUser);

        if (!currentUser.idFinca) {
          setError("El usuario no tiene una finca asignada");
          return;
        }

        setIdFinca(currentUser.idFinca);

        setLoadingInsumos(true);
        const insumosResponse = await fetch(
          `http://localhost:8080/insumos/finca/${currentUser.idFinca}`,
          {
            headers: {
              Authorization: `Bearer ${localStorage.getItem("authToken")}`,
            },
          },
        );

        if (!insumosResponse.ok) throw new Error("Error al obtener insumos");
        const insumosData = await insumosResponse.json();
        setInsumosDisponibles(insumosData);
        setLoadingInsumos(false);
      } catch (error) {
        console.error("Error al cargar datos iniciales:", error);
        setError(error.message);
        setLoadingInsumos(false);
      }
    };

    loadInitialData();
  }, [navigate]);

  const toggleDropdown = () => setShowDropdown(!showDropdown);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });

    if (error) setError(null);
    if (success) setSuccess(false);
  };

  const agregarInsumo = () => {
    setUsosInsumos([
      ...usosInsumos,
      {
        idInsumo: "",
        cantidad: "",
        fecha: formData.fechaInicio || new Date().toISOString().split("T")[0],
      },
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

  const validarFormulario = () => {
    if (!formData.idTipoActividad) {
      setError("Debe seleccionar un tipo de actividad");
      return false;
    }

    if (!formData.fechaInicio) {
      setError("Debe seleccionar una fecha de inicio");
      return false;
    }

    if (formData.fechaFin && formData.fechaFin < formData.fechaInicio) {
      setError(
        "La fecha de finalización no puede ser anterior a la fecha de inicio",
      );
      return false;
    }

    for (let i = 0; i < usosInsumos.length; i++) {
      const insumo = usosInsumos[i];
      if (insumo.idInsumo && (!insumo.cantidad || insumo.cantidad <= 0)) {
        setError(
          `Debe especificar una cantidad válida para el insumo ${i + 1}`,
        );
        return false;
      }
      if (insumo.cantidad && !insumo.idInsumo) {
        setError(
          `Debe seleccionar un insumo para la cantidad especificada en la posición ${i + 1}`,
        );
        return false;
      }
    }

    return true;
  };

  const handleRegister = async () => {
    if (!validarFormulario()) return;

    if (!idFinca) {
      setError("No se pudo determinar la finca asociada");
      return;
    }

    try {
      setLoading(true);
      setError(null);

      const insumosValidos = usosInsumos.filter(
        (insumo) => insumo.idInsumo && insumo.cantidad && insumo.cantidad > 0,
      );

      const actividadData = {
        idFinca: idFinca,
        idTipoActividad: parseInt(formData.idTipoActividad),
        fechaInicio: formData.fechaInicio,
        fechaFin: formData.fechaFin || null,
        descripcion: formData.descripcion || "",
        usosInsumos: insumosValidos,
      };

      const response = await authService.authFetch(
        "http://localhost:8080/actividades",
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(actividadData),
        },
      );

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || "Error al registrar la actividad");
      }

      setSuccess(true);

      setTimeout(() => {
        navigate("/actividades");
      }, 1500);
    } catch (error) {
      console.error("Error al registrar actividad:", error);
      setError(error.message);
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    authService.logout();
    navigate("/");
  };

  const handleCancel = () => navigate("/actividades");

  const obtenerInsumoDisponible = (idInsumo) => {
    return insumosDisponibles.find((i) => i.idInsumo === parseInt(idInsumo));
  };

  return (
    <div className="app-container">
      {/* Barra superior */}
      <div className="topbar">
        <img src={logo} alt="Logo" className="logo-mini" />
        <div className="user-dropdown" onClick={toggleDropdown}>
          <span className="username">{user?.nombre || "Usuario"} ▼</span>
          {showDropdown && (
            <div className="dropdown-menu">
              <button onClick={handleLogout} className="dropdown-btn">
                <FaSignOutAlt style={{ marginRight: "8px" }} /> Cerrar sesión
              </button>
            </div>
          )}
        </div>
      </div>

      {/* Contenido principal */}
      <div className="content-area">
        <div className="registro-actividad-container">
          <div className="registro-header">
            <h2 className="registro-title">Registrar Nueva Actividad</h2>
            <p className="registro-subtitle">
              Complete la información de la actividad y opcionalmente agregue
              insumos utilizados
            </p>
          </div>

          {/* Mensajes de estado */}
          {error && (
            <div className="message error-message">
              <strong>Error:</strong> {error}
            </div>
          )}

          {success && (
            <div className="message success-message">
              <strong>¡Éxito!</strong> Actividad registrada correctamente.
              Redirigiendo...
            </div>
          )}

          {/* Formulario */}
          <form className="registro-form" onSubmit={(e) => e.preventDefault()}>
            {/* Información básica de la actividad */}
            <div className="form-section">
              <h3 className="section-title">Información de la Actividad</h3>

              <div className="form-group">
                <label htmlFor="idTipoActividad">Tipo de Actividad *</label>
                <select
                  id="idTipoActividad"
                  name="idTipoActividad"
                  className="registro-input"
                  value={formData.idTipoActividad}
                  onChange={handleInputChange}
                  required
                  disabled={loading}
                >
                  <option value="">Seleccione un tipo de actividad</option>
                  {tiposActividad.map((tipo) => (
                    <option key={tipo.id} value={tipo.id}>
                      {tipo.nombre}
                    </option>
                  ))}
                </select>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="fechaInicio">Fecha de Inicio *</label>
                  <input
                    id="fechaInicio"
                    type="date"
                    name="fechaInicio"
                    className="registro-input"
                    value={formData.fechaInicio}
                    onChange={handleInputChange}
                    required
                    disabled={loading}
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="fechaFin">Fecha de Finalización</label>
                  <input
                    id="fechaFin"
                    type="date"
                    name="fechaFin"
                    className="registro-input"
                    value={formData.fechaFin}
                    onChange={handleInputChange}
                    disabled={loading}
                    min={formData.fechaInicio}
                  />
                </div>
              </div>

              <div className="form-group">
                <label htmlFor="descripcion">Descripción</label>
                <textarea
                  id="descripcion"
                  name="descripcion"
                  placeholder="Descripción detallada de la actividad (opcional)"
                  className="registro-textarea"
                  value={formData.descripcion}
                  onChange={handleInputChange}
                  disabled={loading}
                  rows="3"
                />
              </div>
            </div>

            {/* Sección de insumos */}
            <div className="form-section">
              <div className="section-header">
                <h3 className="section-title">Insumos Utilizados (Opcional)</h3>
                <button
                  type="button"
                  onClick={agregarInsumo}
                  className="btn-agregar-insumo"
                  disabled={loading || loadingInsumos}
                >
                  <FaPlus /> Agregar Insumo
                </button>
              </div>

              {loadingInsumos && (
                <div className="loading-insumos">
                  Cargando insumos disponibles...
                </div>
              )}

              {usosInsumos.length === 0 ? (
                <div className="no-insumos">
                  <p>No hay insumos agregados.</p>
                  <p>
                    <small>
                      Los insumos son opcionales. Puede agregar los que
                      utilizará en esta actividad.
                    </small>
                  </p>
                </div>
              ) : (
                <div className="insumos-list">
                  {usosInsumos.map((insumo, index) => {
                    const insumoSeleccionado = obtenerInsumoDisponible(
                      insumo.idInsumo,
                    );
                    return (
                      <div key={index} className="insumo-item">
                        <div className="insumo-header">
                          <span className="insumo-numero">
                            Insumo #{index + 1}
                          </span>
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
                              onChange={(e) =>
                                actualizarInsumo(
                                  index,
                                  "idInsumo",
                                  parseInt(e.target.value),
                                )
                              }
                              className="registro-input"
                              disabled={loading}
                            >
                              <option value="">Seleccione un insumo</option>
                              {insumosDisponibles.map((i) => (
                                <option key={i.idInsumo} value={i.idInsumo}>
                                  {i.nombre} (Disponible: {i.cantidadDisponible}{" "}
                                  {i.unidadMedida || ""})
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
                              max={
                                insumoSeleccionado
                                  ? insumoSeleccionado.cantidadDisponible
                                  : undefined
                              }
                              placeholder="Cantidad a utilizar"
                              value={insumo.cantidad}
                              onChange={(e) =>
                                actualizarInsumo(
                                  index,
                                  "cantidad",
                                  parseFloat(e.target.value),
                                )
                              }
                              className="registro-input"
                              disabled={loading}
                            />
                            {insumoSeleccionado && (
                              <small className="stock-info">
                                Stock disponible:{" "}
                                {insumoSeleccionado.cantidadDisponible}{" "}
                                {insumoSeleccionado.unidadMedida || ""}
                              </small>
                            )}
                          </div>

                          <div className="form-group">
                            <label>Fecha de Uso</label>
                            <input
                              type="date"
                              value={insumo.fecha}
                              onChange={(e) =>
                                actualizarInsumo(index, "fecha", e.target.value)
                              }
                              className="registro-input"
                              disabled={loading}
                            />
                          </div>
                        </div>
                      </div>
                    );
                  })}
                </div>
              )}
            </div>

            {/* Botones de acción */}
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
                {loading
                  ? "Registrando..."
                  : success
                    ? "Registrado ✓"
                    : "Registrar Actividad"}
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

export default RegistrarActividad;
