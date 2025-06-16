/**
 * @file RegistrarProduccion.jsx
 * @description Componente para registrar nuevas producciones agrícolas.
 * Proporciona un formulario completo para crear nuevas producciones que se enviarán al backend.
 */

import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import authService from "../authService";
import logo from "./../assets/APROAFA2.png";
import watermarkImage from "./../assets/LogoBosque.png";
import { FaSignOutAlt } from "react-icons/fa";
import "./Produccion.css";
import "./RegistrarProduccion.css";

const RegistrarProduccion = () => {
  const [user, setUser] = useState(null);
  const [showDropdown, setShowDropdown] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [productos, setProductos] = useState([]);

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

        // Cargar productos
        const productosResponse = await fetch(
          "http://localhost:8080/api/productos",
        );
        if (!productosResponse.ok)
          throw new Error("Error al obtener productos");
        const productosData = await productosResponse.json();
        setProductos(productosData);
      } catch (error) {
        console.error("Error al cargar datos iniciales:", error);
        setError(error.message);
      }
    };

    loadInitialData();
  }, []);

  const handleInputChange = (e) => {
    const { name, value } = e.target;

    // Si cambia el estado, resetear cantidad cosechada si no es COSECHADO
    if (name === "estado" && value !== "COSECHADO") {
      setFormData({
        ...formData,
        [name]: value,
        cantidadCosechada: "",
      });
    } else {
      setFormData({
        ...formData,
        [name]: value,
      });
    }
  };

  const handleRegister = async () => {
    // Validaciones básicas
    if (!formData.idProducto || !formData.fechaSiembra) {
      setError("Los campos Producto y Fecha de Siembra son obligatorios");
      return;
    }

    // Validaciones específicas para estado COSECHADO
    if (formData.estado === "COSECHADO") {
      if (!formData.fechaCosecha) {
        setError("Para estado COSECHADO, la fecha de cosecha es obligatoria");
        return;
      }
      if (!formData.cantidadCosechada) {
        setError("Para estado COSECHADO, la cantidad cosechada es obligatoria");
        return;
      }
    }

    try {
      setLoading(true);
      setError(null);

      const produccionData = {
        idProducto: parseInt(formData.idProducto),
        idFinca: user.idFinca,
        fechaSiembra: formData.fechaSiembra, // Corregido: estaba como 'fechaSiembra' (con e)
        fechaCosecha:
          formData.estado === "COSECHADO" ? formData.fechaCosecha : null,
        estado: formData.estado,
        cantidadCosechada:
          formData.estado === "COSECHADO"
            ? parseFloat(formData.cantidadCosechada)
            : null,
      };

      console.log("Datos a enviar:", produccionData);

      const response = await fetch("http://localhost:8080/produccion", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${localStorage.getItem("authToken")}`,
        },
        body: JSON.stringify(produccionData),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(
          errorData.message || "Error al registrar la producción",
        );
      }

      navigate("/produccion");
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
        <h2 className="registro-title">Registrar Nueva Producción</h2>

        {error && <div className="error-message">{error}</div>}

        <form className="registro-form">
          <select
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

          <input
            type="date"
            name="fechaSiembra"
            className="registro-input"
            value={formData.fechaSiembra}
            onChange={handleInputChange}
            required
            disabled={loading}
          />

          <input
            type="date"
            name="fechaCosecha"
            className="registro-input"
            value={formData.fechaCosecha}
            onChange={handleInputChange}
            disabled={loading || formData.estado !== "COSECHADO"}
          />

          <select
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

          <input
            type="number"
            name="cantidadCosechada"
            placeholder="Cantidad cosechada (kg)"
            className="registro-input"
            value={formData.cantidadCosechada}
            onChange={handleInputChange}
            min="0"
            step="0.01"
            disabled={loading || formData.estado !== "COSECHADO"}
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

export default RegistrarProduccion;
