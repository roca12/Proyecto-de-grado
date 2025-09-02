import "./Ventas.css";
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
  FaPlus,
} from "react-icons/fa";
import authService from "../authService";
import logo from "./../assets/APROAFA2.png";
import logoMini from "./../assets/APROAFA.jpg";
import watermarkImage from "./../assets/LogoBosque.png";

const Ventas = () => {
  const [isOpen, setIsOpen] = useState(true);
  const [showDropdown, setShowDropdown] = useState(false);
  const [ventas, setVentas] = useState([]);
  const [clientes, setClientes] = useState([]);
  const [personas, setPersonas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showHelp, setShowHelp] = useState(false);

  const navigate = useNavigate();

  // Función helper para hacer fetch con manejo de errores
  const fetchWithErrorHandling = async (url, errorMessage) => {
    try {
      const response = await fetch(url);

      if (!response.ok) {
        throw new Error(
          `${errorMessage}: ${response.status} ${response.statusText}`,
        );
      }

      const data = await response.json();
      return data;
    } catch (error) {
      console.error(`Error fetching ${url}:`, error);
      // Retorna un arreglo vacío para evitar fallos
      return [];
    }
  };

  useEffect(() => {
    const fetchVentas = async () => {
      setLoading(true);
      setError(null);

      try {
        const currentUser = authService.getCurrentUser();
        const idFinca = currentUser?.idFinca;

        // Llamadas en paralelo con manejo de errores
        const [ventasData, clientesData, personasData] = await Promise.all([
          fetchWithErrorHandling(
            `http://localhost:8080/api/ventas/finca/${idFinca}`,
            "Error al cargar ventas",
          ),
          fetchWithErrorHandling(
            "http://localhost:8080/api/clientes",
            "Error al cargar clientes",
          ),
          fetchWithErrorHandling(
            "http://localhost:8080/api/personas",
            "Error al cargar personas",
          ),
        ]);

        // Procesar personas con verificación de estructura
        let processedPersonas = [];
        if (Array.isArray(personasData)) {
          processedPersonas = personasData;
        } else if (personasData && typeof personasData === "object") {
          if (personasData.content && Array.isArray(personasData.content)) {
            processedPersonas = personasData.content;
          } else if (personasData.data && Array.isArray(personasData.data)) {
            processedPersonas = personasData.data;
          } else if (
            personasData.personas &&
            Array.isArray(personasData.personas)
          ) {
            processedPersonas = personasData.personas;
          }
        }

        setVentas(Array.isArray(ventasData) ? ventasData : []);
        setClientes(Array.isArray(clientesData) ? clientesData : []);
        setPersonas(processedPersonas);
      } catch (error) {
        console.error("Error general en fetchVentas:", error);
        setError(
          "Error al cargar los datos. Algunos datos pueden no estar disponibles.",
        );
        setVentas([]);
        setClientes([]);
        setPersonas([]);
      } finally {
        setLoading(false);
      }
    };

    fetchVentas();
  }, []);

  const toggleMenu = () => setIsOpen(!isOpen);
  const toggleDropdown = () => setShowDropdown(!showDropdown);
  const toggleHelp = () => setShowHelp(!showHelp);
  const handleLogout = () => authService.logout();
  const irARegistrarVenta = () => navigate("/registrar-venta");

  const obtenerNombreCliente = (idCliente) => {
    if (!Array.isArray(clientes) || !idCliente)
      return `ID ${idCliente || "N/A"}`;
    const cliente = clientes.find((c) => c.idCliente === idCliente);
    return cliente
      ? `${cliente.nombre} ${cliente.apellido}`
      : `ID ${idCliente}`;
  };

  const obtenerNombrePersona = (idPersona) => {
    if (!Array.isArray(personas) || !idPersona)
      return `ID ${idPersona || "N/A"}`;
    const persona = personas.find(
      (p) => Number(p.idPersona) === Number(idPersona),
    );
    return persona
      ? `${persona.nombre} ${persona.apellido}`
      : `ID ${idPersona}`;
  };

  // Estado de carga
  if (loading) {
    return (
      <div className="main-container">
        <div
          className="loading-container"
          style={{
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            height: "100vh",
            fontSize: "18px",
          }}
        >
          Cargando datos...
        </div>
      </div>
    );
  }

  return (
    <div className="main-container">
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
            <button onClick={() => navigate("/produccion")}>
              <FaCheck /> {isOpen && "Produccion"}
            </button>
            <button onClick={() => navigate("/ventas")}>
              <FaCreditCard /> {isOpen && "Ventas"}
            </button>
            {/*<button onClick={() => navigate("/documentos")}>
                <FaFile /> {isOpen && "Documentos"}
              </button>*/}
            <button onClick={() => navigate("/reportes-finca")}>
              <FaChartArea /> {isOpen && "Reportes"}
            </button>
            <button onClick={() => navigate("/cultivo")}>
              <FaTable /> {isOpen && "Cultivos"}
            </button>
          </div>

          <img src={logoMini} alt="Logo inferior" className="footer-img" />
        </div>

        <div className="main-content">
          <div className="ventas-container">
            <div className="ventas-header">
              <h2 className="ventas-title">Ventas</h2>
              <div
                style={{
                  display: "flex",
                  alignItems: "center",
                  position: "relative",
                }}
              >
                <button className="btn-registrar" onClick={irARegistrarVenta}>
                  <FaPlus /> Registrar venta
                </button>
                <button
                  className="help-button"
                  onMouseEnter={() => setShowHelp(true)}
                  onMouseLeave={() => setShowHelp(false)}
                  onClick={toggleHelp}
                >
                  ?
                </button>
                {showHelp && (
                  <div
                    className="help-tooltip"
                    onMouseEnter={() => setShowHelp(true)}
                    onMouseLeave={() => setShowHelp(false)}
                  >
                    <h4>Ayuda - Funciones de los botones</h4>
                    <ul>
                      <li>
                        <strong>Registrar Venta:</strong> Abre el formulario
                        para crear una nueva venta.
                      </li>
                    </ul>
                  </div>
                )}
              </div>
            </div>

            {error && (
              <div
                className="error-message"
                style={{
                  backgroundColor: "#ffe6e6",
                  border: "1px solid #ff9999",
                  color: "#cc0000",
                  padding: "10px",
                  marginBottom: "20px",
                  borderRadius: "4px",
                }}
              >
                {error}
              </div>
            )}

            <table className="ventas-table">
              <thead>
                <tr>
                  <th>Cliente</th>
                  <th>Vendedor</th>
                  <th>Fecha</th>
                  <th>Método Pago</th>
                  <th>Total</th>
                </tr>
              </thead>
              <tbody>
                {ventas.length === 0 ? (
                  <tr>
                    <td
                      colSpan="5"
                      style={{ textAlign: "center", padding: "20px" }}
                    >
                      No hay ventas registradas
                    </td>
                  </tr>
                ) : (
                  ventas.map((venta) => (
                    <tr key={venta.idVenta}>
                      <td>{obtenerNombreCliente(venta.idCliente)}</td>
                      <td>{obtenerNombrePersona(venta.idPersona)}</td>
                      <td>{venta.fechaVenta || "N/A"}</td>
                      <td>{venta.metodoPago || "N/A"}</td>
                      <td>${venta.total || "0.00"}</td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <div className="watermark">
        <img src={watermarkImage} alt="Marca de agua" />
      </div>
    </div>
  );
};

export default Ventas;
