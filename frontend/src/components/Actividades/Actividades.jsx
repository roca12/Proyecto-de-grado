/**
 * @file Actividades.jsx
 * @description Componente que representa la vista de actividades dentro del sistema.
 * Incluye un menú lateral colapsable, una barra superior con menú de usuario, y una tabla
 * para mostrar las actividades registradas. Permite navegar entre secciones y registrar nuevas actividades.
 */

import "./Actividades.css";
import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import {
    FaBars,
    FaTimes,
    FaUser,
    FaSignOutAlt,
    FaAddressBook,
    FaTruck,
    FaCheck,
    FaFile,
    FaCreditCard,
    FaChartArea,
    FaTable
} from "react-icons/fa";
import authService from "../authService";
import logo from "./../assets/APROAFA2.png";
import logoMini from "./../assets/APROAFA.jpg";

/**
 * Componente funcional para la gestión de actividades.
 * Contiene navegación lateral, barra superior y una tabla de actividades.
 *
 * @component
 * @returns {JSX.Element} Interfaz de usuario para visualizar y registrar actividades.
 */
const Actividades = () => {
    const [isOpen, setIsOpen] = useState(true);
    const [showDropdown, setShowDropdown] = useState(false);
    const [user, setUser] = useState(null);
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
     * Alterna la visibilidad del menú lateral.
     */
    const toggleMenu = () => setIsOpen(!isOpen);

    /**
     * Alterna la visibilidad del menú desplegable del usuario.
     */
    const toggleDropdown = () => setShowDropdown(!showDropdown);

    /**
     * Cierra la sesión del usuario actual.
     */
    const handleLogout = () => authService.logout();

    /**
     * Redirige al formulario para registrar una nueva actividad.
     */
    const irARegistrarActividad = () => {
        navigate("/registrar-actividad");
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

            {/* Contenido principal */}
            <div className="content-wrapper">
                {/* Menú lateral */}
                <div className={`sidebar ${isOpen ? "open" : "collapsed"}`}>
                    <button className="toggle-button" onClick={toggleMenu}>
                        {isOpen ? <FaTimes /> : <FaBars />}
                        <span>{isOpen ? "Ocultar menú" : ""}</span>
                    </button>

                    <div className="menu-items">
                        <button onClick={() => navigate("/actividades")}>
                            <FaAddressBook /> {isOpen && "Actividades"}
                        </button>
                        <button onClick={() => navigate("/personas")}>
                            <FaUser /> {isOpen && "Personas"}
                        </button>
                        <button onClick={() => navigate("/insumos")}>
                            <FaTruck /> {isOpen && "Insumos"}
                        </button>
                        <button><FaCheck /> {isOpen && "Productos"}</button>
                        <button><FaCreditCard /> {isOpen && "Ventas"}</button>
                        <button><FaFile /> {isOpen && "Documentos"}</button>
                        <button><FaChartArea /> {isOpen && "Reportes"}</button>
                        <button><FaTable /> {isOpen && "Cultivos"}</button>
                    </div>

                    <img src={logoMini} alt="Logo inferior" className="footer-img" />
                </div>

                {/* Vista de actividades */}
                <div className="main-content">
                    <div className="actividades-container">
                        <div className="actividades-header">
                            <h2 className="actividades-title">Actividades</h2>
                            <button className="btn-registrar" onClick={irARegistrarActividad}>
                                Registrar actividad
                            </button>
                        </div>

                        <table className="actividades-table">
                            <thead>
                            <tr>
                                <th>Finca</th>
                                <th>Tipo de actividad</th>
                                <th>Fecha de inicio</th>
                                <th>Fecha de finalización</th>
                                <th>Descripción</th>
                            </tr>
                            </thead>
                            <tbody>
                            {/* Aquí irán los datos dinámicamente o de prueba */}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Actividades;
