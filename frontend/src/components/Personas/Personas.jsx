/**
 * @file Personas.jsx
 * @description Componente que representa la página de gestión de personas.
 * Incluye navegación lateral, barra superior y una tabla para visualizar personas registradas,
 * junto con un botón para acceder al formulario de registro de nuevas personas.
 */

import "./Personas.css";
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
  FaTable,
} from "react-icons/fa";
import authService from "../authService";
import logo from "./../assets/APROAFA2.png";
import logoMini from "./../assets/APROAFA.jpg";

/**
 * Componente funcional que muestra la sección de Personas, con acceso al registro de nuevas personas
 * y una tabla donde se desplegarán los datos registrados. Incluye navegación lateral y barra superior.
 *
 * @component
 * @returns {JSX.Element} Página de gestión de personas.
 */
const Personas = () => {
  const [isOpen, setIsOpen] = useState(true); // Estado del menú lateral (expandido/colapsado)
  const [showDropdown, setShowDropdown] = useState(false); // Estado del dropdown del usuario
  const [user, setUser] = useState(null); // Información del usuario actual
  const navigate = useNavigate();

  /**
   * Carga el usuario actual al iniciar el componente.
   */
  useEffect(() => {
    const currentUser = authService.getCurrentUser();
    setUser(currentUser);
  }, []);

  /** Alterna visibilidad del menú lateral */
  const toggleMenu = () => setIsOpen(!isOpen);

  /** Alterna visibilidad del dropdown del usuario */
  const toggleDropdown = () => setShowDropdown(!showDropdown);

  /** Cierra sesión del usuario actual */
  const handleLogout = () => authService.logout();

  /** Redirige a la página de registro de persona */
  const irARegistrarPersona = () => {
    navigate("/registrar-persona");
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

        {/* Contenido principal */}
        <div className="main-content">
          <div className="personas-container">
            <div className="personas-header">
              <h2 className="personas-title">Personas</h2>
              <button className="btn-registrar" onClick={irARegistrarPersona}>
                Registrar persona
              </button>
            </div>

            <table className="personas-table">
              <thead>
                <tr>
                  <th>Tipo</th>
                  <th>Nombre</th>
                  <th>Apellidos</th>
                  <th>Documento</th>
                  <th>Teléfono</th>
                  <th>Dirección</th>
                  <th>Correo</th>
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

export default Personas;
