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
  FaEdit,
  FaTrash
} from "react-icons/fa";
import authService from "../authService";
import logo from "./../assets/APROAFA2.png";
import logoMini from "./../assets/APROAFA.jpg";

const Personas = () => {
  const [isOpen, setIsOpen] = useState(true);
  const [showDropdown, setShowDropdown] = useState(false);
  const [user, setUser] = useState(null);
  const [personas, setPersonas] = useState([]);
  const [error, setError] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [personaEditando, setPersonaEditando] = useState(null);
  const [formData, setFormData] = useState({
    nombre: '',
    apellido: '',
    numeroIdentificacion: '',
    telefono: '',
    direccion: '',
    correo: ''
  });

  const navigate = useNavigate();

  useEffect(() => {
    const fetchPersonas = async () => {
      try {
        const currentUser = authService.getCurrentUser();
        setUser(currentUser);

        const [clientesResponse, proveedoresResponse] = await Promise.all([
          fetch("http://localhost:8080/api/clientes"),
          fetch("http://localhost:8080/api/proveedores")
        ]);

        if (!clientesResponse.ok || !proveedoresResponse.ok) {
          throw new Error("Error al cargar los datos de personas");
        }

        const clientesData = await clientesResponse.json();
        const proveedoresData = await proveedoresResponse.json();

        const clientes = clientesData.map(cliente => ({
          idPersona: cliente.idCliente || cliente.id,
          nombre: cliente.nombre || cliente.persona?.nombre || "N/A",
          apellido: cliente.apellido || cliente.persona?.apellido || "N/A",
          numeroIdentificacion: cliente.numeroIdentificacion || cliente.persona?.numeroIdentificacion || "N/A",
          telefono: cliente.telefono || cliente.persona?.telefono || "N/A",
          direccion: cliente.direccion || cliente.persona?.direccion || "N/A",
          correo: cliente.correo || cliente.persona?.correo || "N/A",
          tipo: "Cliente",
          tipoEntidad: "cliente"
        }));

        const proveedores = proveedoresData.map(proveedor => ({
          idPersona: proveedor.idProveedor || proveedor.id,
          nombre: proveedor.nombre || proveedor.persona?.nombre || "N/A",
          apellido: proveedor.apellido || proveedor.persona?.apellido || "N/A",
          numeroIdentificacion: proveedor.numeroIdentificacion || proveedor.persona?.numeroIdentificacion || "N/A",
          telefono: proveedor.telefono || proveedor.persona?.telefono || "N/A",
          direccion: proveedor.direccion || proveedor.persona?.direccion || "N/A",
          correo: proveedor.correo || proveedor.persona?.correo || "N/A",
          tipo: "Proveedor",
          tipoEntidad: "proveedor"
        }));

        const todasLasPersonas = [...clientes, ...proveedores].sort((a, b) =>
            (a.nombre || "").localeCompare(b.nombre || "")
        );

        setPersonas(todasLasPersonas);
      } catch (error) {
        console.error("Error al cargar personas:", error);
        setError(error.message);
      }
    };

    fetchPersonas();
  }, []);

  const toggleMenu = () => setIsOpen(!isOpen);
  const toggleDropdown = () => setShowDropdown(!showDropdown);
  const handleLogout = () => authService.logout();
  const irARegistrarPersona = () => navigate("/registrar-persona");

  const handleEliminarPersona = async (persona) => {
    try {
      const endpoint = persona.tipo === "Cliente"
          ? `http://localhost:8080/api/clientes/${persona.idPersona}`
          : `http://localhost:8080/api/proveedores/${persona.idPersona}`;

      const response = await fetch(endpoint, {
        method: 'DELETE'
      });

      if (!response.ok) throw new Error("Error al eliminar persona");

      setPersonas(personas.filter(p =>
          p.idPersona !== persona.idPersona || p.tipo !== persona.tipo
      ));
    } catch (error) {
      console.error("Error:", error.message);
      setError(error.message);
    }
  };

  const abrirModalActualizar = (persona) => {
    setPersonaEditando(persona);
    setFormData({
      nombre: persona.nombre,
      apellido: persona.apellido,
      numeroIdentificacion: persona.numeroIdentificacion,
      telefono: persona.telefono,
      direccion: persona.direccion,
      correo: persona.correo
    });
    setShowModal(true);
  };

  const cerrarModal = () => {
    setShowModal(false);
    setPersonaEditando(null);
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const endpoint = personaEditando.tipo === "Cliente"
          ? `http://localhost:8080/api/clientes/${personaEditando.idPersona}`
          : `http://localhost:8080/api/proveedores/${personaEditando.idPersona}`;

      // Construir el objeto según el tipo de persona
      const requestBody = {
        nombre: formData.nombre,
        apellido: formData.apellido,
        numeroIdentificacion: formData.numeroIdentificacion,
        telefono: formData.telefono,
        direccion: formData.direccion,
        correo: formData.correo
      };

      // Si es cliente, agregar campos específicos si existen
      if (personaEditando.tipo === "Cliente") {
        requestBody.idCliente = personaEditando.idPersona;
      }
      // Si es proveedor, agregar campos específicos si existen
      else {
        requestBody.idProveedor = personaEditando.idPersona;
      }

      const response = await fetch(endpoint, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(requestBody)
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || "Error al actualizar persona");
      }

      const data = await response.json();

      // Actualizar la lista de personas
      setPersonas(personas.map(persona =>
          persona.idPersona === personaEditando.idPersona && persona.tipo === personaEditando.tipo
              ? {
                ...persona,
                nombre: formData.nombre,
                apellido: formData.apellido,
                numeroIdentificacion: formData.numeroIdentificacion,
                telefono: formData.telefono,
                direccion: formData.direccion,
                correo: formData.correo
              }
              : persona
      ));

      cerrarModal();
    } catch (error) {
      console.error("Error:", error.message);
      setError(error.message);
    }
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
              <button onClick={() => navigate("/produccion")}>
                <FaCheck /> {isOpen && "Produccion"}
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
              <button onClick={() => navigate("/cultivo")}>
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

              {error && <div className="error-message">{error}</div>}

              <table className="personas-table">
                <thead>
                <tr>
                  <th>Tipo</th>
                  <th>Nombre</th>
                  <th>Apellidos</th>
                  <th>Documento</th>
                  <th>Teléfono</th>
                  <th>Dirección</th>
                  <th>Acciones</th>
                </tr>
                </thead>
                <tbody>
                {personas.map((persona) => (
                    <tr key={`${persona.tipo}-${persona.idPersona}`}>
                      <td>{persona.tipo}</td>
                      <td>{persona.nombre}</td>
                      <td>{persona.apellido}</td>
                      <td>{persona.numeroIdentificacion}</td>
                      <td>{persona.telefono}</td>
                      <td>{persona.direccion}</td>
                      <td className="actions-cell">
                        <button
                            className="btn-actualizar"
                            onClick={() => abrirModalActualizar(persona)}
                        >
                          <FaEdit /> Actualizar
                        </button>
                        <button
                            className="btn-eliminar"
                            onClick={() => handleEliminarPersona(persona)}
                        >
                          <FaTrash /> Eliminar
                        </button>
                      </td>
                    </tr>
                ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>

        {/* Modal de actualización */}
        {showModal && (
            <div className="modal-overlay">
              <div className="modal-content">
                <h3>Actualizar {personaEditando?.tipo}</h3>
                <form onSubmit={handleSubmit}>
                  <div className="form-group">
                    <label>Nombre:</label>
                    <input
                        type="text"
                        name="nombre"
                        value={formData.nombre}
                        onChange={handleInputChange}
                        required
                    />
                  </div>
                  <div className="form-group">
                    <label>Apellidos:</label>
                    <input
                        type="text"
                        name="apellido"
                        value={formData.apellido}
                        onChange={handleInputChange}
                        required
                    />
                  </div>
                  <div className="form-group">
                    <label>Documento:</label>
                    <input
                        type="text"
                        name="numeroIdentificacion"
                        value={formData.numeroIdentificacion}
                        onChange={handleInputChange}
                        required
                    />
                  </div>
                  <div className="form-group">
                    <label>Teléfono:</label>
                    <input
                        type="text"
                        name="telefono"
                        value={formData.telefono}
                        onChange={handleInputChange}
                    />
                  </div>
                  <div className="form-group">
                    <label>Dirección:</label>
                    <input
                        type="text"
                        name="direccion"
                        value={formData.direccion}
                        onChange={handleInputChange}
                    />
                  </div>
                  <div className="form-group">
                    <label>Correo electrónico:</label>
                    <input
                        type="email"
                        name="correo"
                        value={formData.correo}
                        onChange={handleInputChange}
                    />
                  </div>
                  <div className="modal-buttons">
                    <button type="button" className="btn-cancelar" onClick={cerrarModal}>
                      Cancelar
                    </button>
                    <button type="submit" className="btn-guardar">
                      Guardar Cambios
                    </button>
                  </div>
                </form>
              </div>
            </div>
        )}
      </div>
  );
};

export default Personas;