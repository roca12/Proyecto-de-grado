/**
 * @file RegistrarInsumo.jsx
 * @description Componente que permite registrar un nuevo insumo dentro del sistema.
 * Incluye un formulario con campos básicos y opciones para cancelar o registrar.
 */

import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import authService from "../authService";
import logo from "./../assets/APROAFA2.png";
import { FaSignOutAlt } from "react-icons/fa";

import "./Insumos.css";
import "./RegistrarInsumo.css";

/**
 * Componente funcional que representa la página para registrar un nuevo insumo.
 * Muestra una barra superior con control de usuario y un formulario básico.
 *
 * @component
 * @returns {JSX.Element} Formulario para registrar insumos.
 */
const RegistrarInsumo = () => {
    const [user, setUser] = useState(null); // Usuario autenticado
    const [showDropdown, setShowDropdown] = useState(false); // Estado del dropdown
    const navigate = useNavigate();

    /**
     * Carga la información del usuario autenticado al montar el componente.
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
     * Cierra la sesión del usuario y redirige al inicio de sesión.
     */
    const handleLogout = () => {
        authService.logout();
        navigate("/");
    };

    /**
     * Redirige al usuario de nuevo a la página de insumos.
     */
    const handleCancel = () => {
        navigate("/insumos");
    };

    /**
     * Acción de registrar el insumo (pendiente de conexión con backend).
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

            {/* Contenedor del formulario */}
            <div className="registro-insumo-container">
                <h2 className="registro-title">Insumos</h2>

                <form className="registro-form">
                    <input type="text" placeholder="Nombre" className="registro-input" />
                    <input type="text" placeholder="Descripcion" className="registro-input" />
                    <input type="text" placeholder="Unidad de medida" className="registro-input" />
                    <input type="text" placeholder="Proveedor" className="registro-input" />

                    <div className="registro-botones">
                        <button type="button" onClick={handleCancel} className="btn-cancelar">Cancelar</button>
                        <button type="button" onClick={handleRegister} className="btn-registrar">Registrar</button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default RegistrarInsumo;
