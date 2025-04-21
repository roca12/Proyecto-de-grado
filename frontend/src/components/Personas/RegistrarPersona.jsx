/**
 * @file RegistrarPersona.jsx
 * @description Componente que permite registrar una nueva persona en el sistema.
 * Incluye una barra superior con logout y un formulario con los campos requeridos.
 * Aún no tiene conectividad con el backend, pero ya tiene la estructura lista.
 */

import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import authService from "../authService";
import logo from "./../assets/APROAFA2.png";
import { FaSignOutAlt } from "react-icons/fa";
import "./Personas.css";
import "./RegistrarPersona.css";

/**
 * Componente funcional que representa el formulario de registro de personas.
 * Permite al usuario registrar una nueva persona y navegar entre vistas.
 *
 * @component
 * @returns {JSX.Element} Página para registrar una persona.
 */
const RegistrarPersona = () => {
  const [user, setUser] = useState(null); // Información del usuario actual
  const [showDropdown, setShowDropdown] = useState(false); // Visibilidad del menú desplegable
  const navigate = useNavigate();

  /**
   * Obtiene y almacena al usuario actual al cargar el componente.
   */
  useEffect(() => {
    const currentUser = authService.getCurrentUser();
    setUser(currentUser);
  }, []);

  /** Alterna visibilidad del dropdown de usuario */
  const toggleDropdown = () => {
    setShowDropdown(!showDropdown);
  };

  /** Cierra sesión del usuario y redirige al login */
  const handleLogout = () => {
    authService.logout();
    navigate("/");
  };

  /** Cancela el registro y vuelve a la página de personas */
  const handleCancel = () => {
    navigate("/personas");
  };

  /** Registra la persona (pendiente de integración con Spring Boot) */
  const handleRegister = () => {
    // Aquí se conectará con Spring Boot más adelante
  };

  return (
    <div className="main-container">
      {/* Barra superior */}
      <div className="topbar">
        <img src={logo} alt="Logo" className="logo-mini" />
        <div className="user-dropdown" onClick={toggleDropdown}>
          <span className="username">Usuario ▼</span>
          {showDropdown && (
            <div className="dropdown-menu">
              <button className="dropdown-btn" onClick={handleLogout}>
                <FaSignOutAlt style={{ marginRight: "8px" }} /> Cerrar sesión
              </button>
            </div>
          )}
        </div>
      </div>

      {/* Contenedor del formulario */}
      <div className="registro-persona-container">
        <h2 className="registro-title">Personas</h2>

        <form className="registro-form">
          <select className="registro-input">
            <option>Tipo</option>
            <option>Proveedor</option>
            <option>Cliente</option>
          </select>
          <input type="text" placeholder="Nombre" className="registro-input" />
          <input
            type="text"
            placeholder="Apellidos"
            className="registro-input"
          />
          <input
            type="text"
            placeholder="Documento"
            className="registro-input"
          />
          <input
            type="text"
            placeholder="Teléfono"
            className="registro-input"
          />
          <input
            type="text"
            placeholder="Dirección"
            className="registro-input"
          />
          <input type="email" placeholder="Correo" className="registro-input" />

          {/* Botones */}
          <div className="registro-botones">
            <button
              type="button"
              onClick={handleCancel}
              className="btn-cancelar"
            >
              Cancelar
            </button>
            <button
              type="button"
              onClick={handleRegister}
              className="btn-registrar"
            >
              Registrar
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default RegistrarPersona;
