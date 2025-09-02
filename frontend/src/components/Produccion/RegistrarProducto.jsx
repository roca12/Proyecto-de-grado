/**
 * @file RegistrarProducto.jsx
 * @description Componente para registrar nuevos productos agrícolas.
 * Proporciona un formulario completo para crear nuevos productos que se enviarán al backend.
 */

import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import authService from "../authService";
import logo from "./../assets/APROAFA2.png";
import watermarkImage from "./../assets/LogoBosque.png";
import { FaSignOutAlt } from "react-icons/fa";
import "./RegistrarProducto.css";

const RegistrarProducto = () => {
  const [user, setUser] = useState(null);
  const [showDropdown, setShowDropdown] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const [formData, setFormData] = useState({
    nombre: "",
    descripcion: "",
    unidadMedida: "UNIDAD",
    idFinca: null, // se asigna en el useEffect
  });

  const navigate = useNavigate();

  useEffect(() => {
    const currentUser = authService.getCurrentUser();
    if (!currentUser) {
      navigate("/login");
      return;
    }
    setUser(currentUser);

    // asignamos la finca del usuario al formData
    if (currentUser.idFinca) {
      setFormData((prev) => ({
        ...prev,
        idFinca: currentUser.idFinca,
      }));
    }
  }, [navigate]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  const handleRegister = async () => {
    if (!formData.nombre) {
      setError("El campo Nombre es obligatorio");
      return;
    }
    if (!formData.idFinca) {
      setError("No se pudo determinar la finca asociada al usuario");
      return;
    }

    try {
      setLoading(true);
      setError(null);

      const response = await fetch("http://localhost:8080/api/productos", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${localStorage.getItem("authToken")}`,
        },
        body: JSON.stringify(formData),
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || "Error al registrar el producto");
      }

      navigate("/produccion");
    } catch (error) {
      console.error("Error al registrar producto:", error);
      setError(error.message || "Ocurrió un error al registrar el producto");
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
    <div className="main-container">
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

      <div className="registro-produccion-container">
        <h2 className="registro-title">Registrar Nuevo Producto</h2>

        {error && <div className="error-message">{error}</div>}

        <form className="registro-form">
          <input
            type="text"
            name="nombre"
            placeholder="Nombre del producto"
            className="registro-input"
            value={formData.nombre}
            onChange={handleInputChange}
            required
            disabled={loading}
          />

          <textarea
            name="descripcion"
            placeholder="Descripción"
            className="registro-input"
            value={formData.descripcion}
            onChange={handleInputChange}
            disabled={loading}
            rows="3"
          />

          <input
            type="text"
            className="registro-input"
            value="UNIDAD"
            disabled
          />

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

export default RegistrarProducto;
