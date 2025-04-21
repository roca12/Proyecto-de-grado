/**
 * @file RegistrarActividad.jsx
 * @description Componente que representa el formulario para registrar una nueva actividad.
 * Incluye una barra superior con menú de usuario, campos para el ingreso de datos y botones
 * para registrar o cancelar. Este formulario se conectará posteriormente con el backend en Spring Boot.
 */

import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import authService from "../authService";
import logo from "./../assets/APROAFA2.png";
import { FaSignOutAlt } from "react-icons/fa";
import "./Actividades.css";
import "./RegistrarActividad.css";

/**
 * Componente funcional para el registro de actividades.
 * Permite a los usuarios ingresar los datos de una nueva actividad.
 *
 * @component
 * @returns {JSX.Element} Interfaz para registrar actividades dentro del sistema.
 */
const RegistrarActividad = () => {
  const [user, setUser] = useState(null);
  const [showDropdown, setShowDropdown] = useState(false);
  const navigate = useNavigate();

  /**
   * Hook que se ejecuta al montar el componente.
   * Obtiene la información del usuario autenticado.
   */
  useEffect(() => {
    const currentUser = authService.getCurrentUser();
    setUser(currentUser);
  }, []);

  /**
   * Alterna la visibilidad del menú desplegable del usuario.
   */
  const toggleDropdown = () => {
    setShowDropdown(!showDropdown);
  };

  /**
   * Cierra la sesión del usuario y redirige al login.
   */
  const handleLogout = () => {
    authService.logout();
    navigate("/");
  };

  /**
   * Redirige a la vista de actividades sin guardar cambios.
   */
  const handleCancel = () => {
    navigate("/actividades");
  };

  /**
   * Función que se conectará con el backend para registrar la actividad.
   * Actualmente no implementada.
   */
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

      {/* Formulario de registro */}
      <div className="registro-actividad-container">
        <h2 className="registro-title">Actividades</h2>

        <form className="registro-form">
          <input type="text" placeholder="Finca" className="registro-input" />
          <input
            type="text"
            placeholder="Tipo de actividad"
            className="registro-input"
          />
          <input
            type="text"
            placeholder="Fecha de inicio"
            className="registro-input"
          />
          <input
            type="text"
            placeholder="Fecha de finalización"
            className="registro-input"
          />
          <input
            type="text"
            placeholder="Descripción"
            className="registro-input"
          />

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

export default RegistrarActividad;
