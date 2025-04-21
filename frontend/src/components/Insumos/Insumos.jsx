/**
 * @file Insumos.jsx
 * @description Componente que representa la página principal de insumos.
 * Incluye un menú lateral desplegable, una barra superior con usuario,
 * y una tabla para mostrar los insumos registrados. Permite la navegación
 * a la vista para registrar un nuevo insumo.
 */

import "./Insumos.css";
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
 * Componente funcional que muestra la sección de insumos dentro del sistema.
 * Permite visualizar los insumos registrados y acceder al formulario para registrar nuevos insumos.
 *
 * @component
 * @returns {JSX.Element} Página de insumos con menú lateral y tabla de contenido.
 */
const Insumos = () => {
  const [isOpen, setIsOpen] = useState(true); // Estado del menú lateral
  const [showDropdown, setShowDropdown] = useState(false); // Estado del dropdown de usuario
  const [user, setUser] = useState(null); // Usuario autenticado

  const navigate = useNavigate();

  /**
   * Obtiene los datos del usuario autenticado cuando se monta el componente.
   */
  useEffect(() => {
    const currentUser = authService.getCurrentUser();
    setUser(currentUser);
  }, []);

  /**
   * Alterna el menú lateral entre abierto y colapsado.
   */
  const toggleMenu = () => setIsOpen(!isOpen);

  /**
   * Alterna la visibilidad del menú desplegable del usuario.
   */
  const toggleDropdown = () => setShowDropdown(!showDropdown);

  /**
   * Cierra la sesión del usuario.
   */
  const handleLogout = () => authService.logout();

  /**
   * Navega a la vista para registrar un nuevo insumo.
   */
  const irARegistrarInsumo = () => {
    navigate("/registrar-insumo");
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

      {/* Contenedor principal con menú lateral y contenido */}
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
          <div className="insumos-container">
            <div className="insumos-header">
              <h2 className="insumos-title">Insumos</h2>
              <button className="btn-registrar" onClick={irARegistrarInsumo}>
                Registrar Insumo
              </button>
            </div>

            {/* Tabla de insumos */}
            <table className="insumos-table">
              <thead>
                <tr>
                  <th>Nombre</th>
                  <th>Descripción</th>
                  <th>Unidad de medida</th>
                  <th>Proveedor</th>
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

export default Insumos;
