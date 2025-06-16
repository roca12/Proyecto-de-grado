/**
 * @file RegistrarActividad.jsx
 * @description Componente para registrar nuevas actividades agrícolas.
 * Obtiene automáticamente la finca asociada al usuario y proporciona un formulario
 * para crear nuevas actividades que se enviarán al backend.
 */

import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import authService from "../authService";
import logo from "./../assets/APROAFA2.png";
import watermarkImage from "./../assets/LogoBosque.png";
import { FaSignOutAlt } from "react-icons/fa";
import "./Actividades.css";
import "./RegistrarActividad.css";

const RegistrarActividad = () => {
  // Estados del componente
  const [user, setUser] = useState(null);
  const [showDropdown, setShowDropdown] = useState(false);
  const [idFinca, setIdFinca] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // Estado para los datos del formulario
  const [formData, setFormData] = useState({
    idTipoActividad: "",
    fechaInicio: "",
    fechaFin: "",
    descripcion: "",
  });

  const navigate = useNavigate();

  // Opciones para el dropdown de tipos de actividad
  const tiposActividad = [
    { id: 1, nombre: "Fertilización" },
    { id: 2, nombre: "Riego" },
    { id: 3, nombre: "Control de plagas" },
    { id: 4, nombre: "Siembra" },
    { id: 5, nombre: "Cosecha" },
  ];

  /**
   * Efecto para cargar los datos iniciales del componente
   * - Obtiene el usuario autenticado
   * - Obtiene la finca asociada al usuario
   */
  useEffect(() => {
    const loadInitialData = async () => {
      try {
        const currentUser = authService.getCurrentUser();
        if (!currentUser) {
          throw new Error("Usuario no autenticado");
        }

        setUser(currentUser);

        // Obtener el idFinca del usuario
        if (currentUser.idFinca) {
          setIdFinca(currentUser.idFinca);
        } else {
          throw new Error("El usuario no tiene una finca asignada");
        }
      } catch (error) {
        console.error("Error al cargar datos iniciales:", error);
        setError(error.message);
      }
    };

    loadInitialData();
  }, []);

  /**
   * Maneja el cambio en los campos del formulario
   * @param {Object} e - Evento del input
   */
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  /**
   * Maneja el envío del formulario para registrar una nueva actividad
   */
  const handleRegister = async () => {
    // Validación de campos obligatorios
    if (!formData.idTipoActividad || !formData.fechaInicio) {
      setError(
        "Los campos Tipo de Actividad y Fecha de Inicio son obligatorios",
      );
      return;
    }

    if (!idFinca) {
      setError("No se pudo determinar la finca asociada");
      return;
    }

    try {
      setLoading(true);
      setError(null);

      // Preparar los datos para enviar al backend
      const actividadData = {
        idFinca: idFinca,
        idTipoActividad: parseInt(formData.idTipoActividad),
        fechaInicio: formData.fechaInicio,
        fechaFin: formData.fechaFin || null,
        descripcion: formData.descripcion || "",
      };

      // Realizar la petición al backend
      const response = await authService.authFetch(
        "http://localhost:8080/actividades",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(actividadData),
        },
      );

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || "Error al registrar la actividad");
      }

      // Si todo sale bien, redirigir a la lista de actividades
      navigate("/actividades");
    } catch (error) {
      console.error("Error al registrar actividad:", error);
      setError(error.message);
    } finally {
      setLoading(false);
    }
  };

  /**
   * Maneja el cierre de sesión del usuario
   */
  const handleLogout = () => {
    authService.logout();
    navigate("/");
  };

  /**
   * Maneja la cancelación del formulario
   */
  const handleCancel = () => {
    navigate("/actividades");
  };

  return (
    <div className="main-container">
      {/* Barra superior con logo y menú de usuario */}
      <div className="topbar">
        <img src={logo} alt="Logo" className="logo-mini" />
        <div
          className="user-dropdown"
          onClick={() => setShowDropdown(!showDropdown)}
        >
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

      {/* Contenedor principal del formulario */}
      <div className="registro-actividad-container">
        <h2 className="registro-title">Registrar Nueva Actividad</h2>

        {/* Mensaje de error */}
        {error && <div className="error-message">{error}</div>}

        {/* Formulario de registro */}
        <form className="registro-form">
          {/* Selector de tipo de actividad */}
          <select
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

          {/* Campo para fecha de inicio */}
          <input
            type="date"
            name="fechaInicio"
            className="registro-input"
            value={formData.fechaInicio}
            onChange={handleInputChange}
            required
            disabled={loading}
          />

          {/* Campo para fecha de fin (opcional) */}
          <input
            type="date"
            name="fechaFin"
            className="registro-input"
            value={formData.fechaFin}
            onChange={handleInputChange}
            disabled={loading}
          />

          {/* Campo para descripción (opcional) */}
          <input
            type="text"
            name="descripcion"
            placeholder="Descripción (opcional)"
            className="registro-input"
            value={formData.descripcion}
            onChange={handleInputChange}
            disabled={loading}
          />

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
              disabled={loading}
            >
              {loading ? "Registrando..." : "Registrar"}
            </button>
          </div>
        </form>
      </div>
      <div className="watermark">
        <img src={watermarkImage} alt="Marca de agua" />
      </div>
    </div>
  );
};

export default RegistrarActividad;
