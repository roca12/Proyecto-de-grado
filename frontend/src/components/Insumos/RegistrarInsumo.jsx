/**
 * @file RegistrarInsumo.jsx
 * @description Componente para registrar nuevos insumos agrícolas por finca.
 */

import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import authService from "../authService";
import logo from "./../assets/APROAFA2.png";
import watermarkImage from "./../assets/LogoBosque.png";
import { FaSignOutAlt } from "react-icons/fa";
import "./Insumos.css";
import "./RegistrarInsumo.css";

const RegistrarInsumo = () => {
  const [user, setUser] = useState(null);
  const [showDropdown, setShowDropdown] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);
  const [proveedores, setProveedores] = useState([]);
  const [insumosExistentes, setInsumosExistentes] = useState([]);

  // Estado del formulario
  const [formData, setFormData] = useState({
    nombre: "",
    descripcion: "",
    unidadMedida: "",
    idProveedor: "",
    idFinca: null, // Añadido para el scope por finca
  });

  const unidadesMedida = [
    { value: "Kg", label: "Kilogramos (Kg)" },
    { value: "Litro", label: "Litros (L)" },
    { value: "Bolsa", label: "Bolsas" },
    { value: "Unidad", label: "Unidades" },
  ];

  const navigate = useNavigate();

  useEffect(() => {
    const loadInitialData = async () => {
      try {
        const currentUser = authService.getCurrentUser();
        if (!currentUser) throw new Error("Usuario no autenticado");
        setUser(currentUser);

        // Verificar que el usuario tenga idFinca
        if (!currentUser.idFinca) {
          throw new Error("El usuario no tiene finca asignada.");
        }

        // Actualizar formData con el idFinca del usuario
        setFormData((prevFormData) => ({
          ...prevFormData,
          idFinca: currentUser.idFinca,
        }));

        // Cargar proveedores por finca e insumos existentes
        const [proveedoresRes, insumosRes] = await Promise.all([
          authService.authFetch(`http://localhost:8080/api/proveedores/finca/${currentUser.idFinca}`),
          fetch(`http://localhost:8080/insumos/finca/${currentUser.idFinca}`),
        ]);

        if (!proveedoresRes.ok || !insumosRes.ok)
          throw new Error("Error al obtener datos iniciales");

        setProveedores(await proveedoresRes.json());
        setInsumosExistentes(await insumosRes.json());
      } catch (error) {
        console.error("Error al cargar datos iniciales:", error);
        setError(error.message);
      }
    };

    loadInitialData();
  }, []);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  const handleRegister = async () => {
    if (!formData.nombre || !formData.unidadMedida || !formData.idProveedor) {
      setError("Los campos Nombre, Unidad de Medida y Proveedor son obligatorios");
      return;
    }

    try {
      setLoading(true);
      setError(null);
      setSuccess(null);

      const insumoData = {
        nombre: formData.nombre,
        descripcion: formData.descripcion || null,
        unidadMedida: formData.unidadMedida,
        idProveedor: parseInt(formData.idProveedor),
        cantidadDisponible: 0, // Fijo en 0
        idFinca: formData.idFinca, // Incluir idFinca en la solicitud
      };

      const response = await fetch("http://localhost:8080/insumos", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${localStorage.getItem("authToken")}`,
        },
        body: JSON.stringify(insumoData),
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || "Error al registrar el insumo");
      }

      console.log("Insumo registrado exitosamente");
      setSuccess("Insumo registrado exitosamente");

      // Redirigir después de un breve delay para mostrar el mensaje de éxito
      setTimeout(() => {
        navigate("/insumos");
      }, 1500);

    } catch (error) {
      console.error("Error al registrar insumo:", error);
      if (error.message.includes("Duplicate entry")) {
        setError("Ya existe un insumo con este nombre para este proveedor. Por favor, use un nombre diferente.");
      } else {
        setError(error.message);
      }
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    authService.logout();
    navigate("/");
  };

  const handleCancel = () => {
    navigate("/insumos");
  };

  const toggleDropdown = () => {
    setShowDropdown(!showDropdown);
  };

  return (
      <div className="main-container">
        <div className="topbar">
          <img src={logo} alt="Logo" className="logo-mini" />
          <div className="user-dropdown" onClick={toggleDropdown}>
            <span className="username">{user ? user.username : "Usuario"} ▼</span>
            {showDropdown && (
                <div className="dropdown-menu">
                  <button className="dropdown-btn" onClick={handleLogout}>
                    <FaSignOutAlt style={{ marginRight: "8px" }} /> Cerrar sesión
                  </button>
                </div>
            )}
          </div>
        </div>

        <div
            className="registro-insumo-container"
            style={{ overflowY: "auto", maxHeight: "90vh" }}
        >
          <h2 className="registro-title">Registrar Nuevo Insumo</h2>

          {error && <div className="error-message">{error}</div>}
          {success && <div className="success-message">{success}</div>}

          <form className="registro-form" onSubmit={(e) => e.preventDefault()}>
            <div className="form-group">
              <label htmlFor="nombre">Nombre del Insumo *</label>
              <input
                  id="nombre"
                  type="text"
                  name="nombre"
                  placeholder="Nombre del insumo"
                  className="registro-input"
                  value={formData.nombre}
                  onChange={handleInputChange}
                  list="insumos-list"
                  required
                  disabled={loading}
              />
              <datalist id="insumos-list">
                {insumosExistentes.map((insumo) => (
                    <option key={insumo.idInsumo} value={insumo.nombre} />
                ))}
              </datalist>
              <p style={{ fontSize: "12px", color: "#666", marginTop: "4px" }}>
                Escribe un nuevo nombre o selecciona uno existente de la lista
              </p>
            </div>

            <div className="form-group">
              <label htmlFor="descripcion">Descripción</label>
              <input
                  id="descripcion"
                  type="text"
                  name="descripcion"
                  placeholder="Descripción (opcional)"
                  className="registro-input"
                  value={formData.descripcion}
                  onChange={handleInputChange}
                  disabled={loading}
              />
            </div>

            <div className="form-group">
              <label htmlFor="unidadMedida">Unidad de Medida *</label>
              <select
                  id="unidadMedida"
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
            </div>

            <div className="form-group">
              <label htmlFor="idProveedor">Proveedor *</label>
              <select
                  id="idProveedor"
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
                      {proveedor.nombre} {proveedor.apellido}
                    </option>
                ))}
              </select>
              {proveedores.length === 0 && (
                  <p style={{ fontSize: "12px", color: "#ff6b6b", marginTop: "4px" }}>
                    No hay proveedores registrados en esta finca.
                    <button
                        type="button"
                        onClick={() => navigate("/registrar-persona?tipo=proveedor")}
                        style={{
                          background: "none",
                          border: "none",
                          color: "#007bff",
                          textDecoration: "underline",
                          cursor: "pointer",
                          fontSize: "12px"
                        }}
                    >
                      Registrar proveedor
                    </button>
                  </p>
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

export default RegistrarInsumo;