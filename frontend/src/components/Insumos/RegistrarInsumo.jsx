/**
 * @file RegistrarInsumo.jsx
 * @description Componente para registrar nuevos insumos agrícolas.
 * Proporciona un formulario completo para crear nuevos insumos que se enviarán al backend.
 */

import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import authService from "../authService";
import logo from "./../assets/APROAFA2.png";
import { FaSignOutAlt } from "react-icons/fa";
import "./Insumos.css";
import "./RegistrarInsumo.css";

const RegistrarInsumo = () => {
  // Estados del componente
  const [user, setUser] = useState(null);
  const [showDropdown, setShowDropdown] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [proveedores, setProveedores] = useState([]);

  // Estado para los datos del formulario
  const [formData, setFormData] = useState({
    nombre: "",
    descripcion: "",
    unidadMedida: "",
    idProveedor: "",
    cantidadDisponible: ""
  });

  // Opciones para unidades de medida
  const unidadesMedida = [
    { value: "Kg", label: "Kilogramos (Kg)" },
    { value: "Litro", label: "Litros (L)" },
    { value: "Bolsa", label: "Bolsas" },
    { value: "Unidad", label: "Unidades" },
    { value: "Caja", label: "Cajas" }
  ];

  const navigate = useNavigate();

  /**
   * Efecto para cargar los datos iniciales del componente
   * - Obtiene el usuario autenticado
   * - Obtiene la lista de proveedores
   */
  useEffect(() => {
    const loadInitialData = async () => {
      try {
        const currentUser = authService.getCurrentUser();
        if (!currentUser) {
          throw new Error("Usuario no autenticado");
        }
        setUser(currentUser);

        // Obtener lista de proveedores
        const proveedoresResponse = await fetch("http://localhost:8080/api/proveedores");
        if (!proveedoresResponse.ok) {
          throw new Error("Error al obtener proveedores");
        }
        const proveedoresData = await proveedoresResponse.json();
        setProveedores(proveedoresData);
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
      [name]: value
    });
  };

  /**
   * Maneja el envío del formulario para registrar un nuevo insumo
   */
  const handleRegister = async () => {
    // Validación de campos obligatorios
    if (!formData.nombre || !formData.unidadMedida || !formData.idProveedor || !formData.cantidadDisponible) {
      setError("Los campos Nombre, Unidad de Medida, Proveedor y Cantidad son obligatorios");
      return;
    }

    try {
      setLoading(true);
      setError(null);

      // Preparar los datos para enviar al backend
      const insumoData = {
        nombre: formData.nombre,
        descripcion: formData.descripcion || null,
        unidadMedida: formData.unidadMedida,
        idProveedor: parseInt(formData.idProveedor),
        cantidadDisponible: parseFloat(formData.cantidadDisponible)
      };

      // Realizar la petición al backend
      const response = await fetch("http://localhost:8080/insumos", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${localStorage.getItem("authToken")}`
        },
        body: JSON.stringify(insumoData)
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || "Error al registrar el insumo");
      }

      // Si todo sale bien, redirigir a la lista de insumos
      navigate("/insumos");
    } catch (error) {
      console.error("Error al registrar insumo:", error);
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
    navigate("/insumos");
  };

  return (
      <div className="main-container">
        {/* Barra superior con logo y menú de usuario */}
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

        {/* Contenedor principal del formulario */}
        <div className="registro-insumo-container">
          <h2 className="registro-title">Registrar Nuevo Insumo</h2>

          {/* Mensaje de error */}
          {error && (
              <div className="error-message">
                {error}
              </div>
          )}

          {/* Formulario de registro */}
          <form className="registro-form">
            {/* Campo para nombre */}
            <input
                type="text"
                name="nombre"
                placeholder="Nombre del insumo"
                className="registro-input"
                value={formData.nombre}
                onChange={handleInputChange}
                required
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

            {/* Selector de unidad de medida */}
            <select
                name="unidadMedida"
                className="registro-input"
                value={formData.unidadMedida}
                onChange={handleInputChange}
                required
                disabled={loading}
            >
              <option value="">Seleccione unidad de medida</option>
              {unidadesMedida.map((unidad) => (
                  <option key={unidad.value} value={unidad.value}>
                    {unidad.label}
                  </option>
              ))}
            </select>

            {/* Selector de proveedor */}
            <select
                name="idProveedor"
                className="registro-input"
                value={formData.idProveedor}
                onChange={handleInputChange}
                required
                disabled={loading}
            >
              <option value="">Seleccione un proveedor</option>
              {proveedores.map((proveedor) => (
                  <option key={proveedor.idProveedor} value={proveedor.idProveedor}>
                    {proveedor.nombre}
                  </option>
              ))}
            </select>

            {/* Campo para cantidad disponible */}
            <input
                type="number"
                name="cantidadDisponible"
                placeholder="Cantidad disponible"
                className="registro-input"
                value={formData.cantidadDisponible}
                onChange={handleInputChange}
                min="0"
                step="0.01"
                required
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
      </div>
  );
};

export default RegistrarInsumo;