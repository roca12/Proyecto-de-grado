/**
 * @file Menu.jsx
 * @description Componente que muestra el menú principal lateral de navegación,
 * incluyendo barra superior con control de usuario, imagen de fondo y acceso a distintas secciones del sistema.
 */

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
import "./Menu.css";
import logo from "./../assets/APROAFA2.png";
import logoMini from "./../assets/APROAFA.jpg";
import fondo from "./../assets/fondo.jpg";

/**
 * Componente funcional que representa el menú lateral principal del sistema.
 * Incluye navegación a distintas secciones, imagen de fondo, barra superior y control de sesión del usuario.
 *
 * @component
 * @returns {JSX.Element} Menú lateral navegable con barra superior.
 */
const Menu = () => {
    const [isOpen, setIsOpen] = useState(true); // Estado del menú lateral (abierto o colapsado)
    const [showDropdown, setShowDropdown] = useState(false); // Visibilidad del menú desplegable del usuario
    const [user, setUser] = useState(null); // Información del usuario autenticado
    const navigate = useNavigate();

    /**
     * Carga la información del usuario al montar el componente.
     */
    useEffect(() => {
        const currentUser = authService.getCurrentUser();
        setUser(currentUser);
    }, []);

    /** Alterna el estado del menú lateral (abierto/cerrado). */
    const toggleMenu = () => setIsOpen(!isOpen);

    /** Alterna la visibilidad del dropdown de usuario. */
    const toggleDropdown = () => setShowDropdown(!showDropdown);

    /** Cierra la sesión del usuario. */
    const handleLogout = () => authService.logout();

    return (
        <div className="main-container" style={{ backgroundImage: `url(${fondo})` }}>
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
                    <button>
                        <FaCheck /> {isOpen && "Productos"}
                    </button>
                    <button>
                        <FaCreditCard /> {isOpen && "Ventas"}
                    </button>
                    <button>
                        <FaFile /> {isOpen && "Documentos"}
                    </button>
                    <button>
                        <FaChartArea /> {isOpen && "Reportes"}
                    </button>
                    <button>
                        <FaTable /> {isOpen && "Cultivos"}
                    </button>
                </div>

                <img src={logoMini} alt="Logo inferior" className="footer-img" />
            </div>
        </div>
    );
};

export default Menu;
