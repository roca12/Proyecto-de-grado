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
  FaTrash,
} from "react-icons/fa";
import authService from "../authService";
import logo from "./../assets/APROAFA2.png";
import logoMini from "./../assets/APROAFA.jpg";
import watermarkImage from "./../assets/LogoBosque.png";

const Personas = () => {
  const [isOpen, setIsOpen] = useState(true);
  const [showDropdown, setShowDropdown] = useState(false);
  const [user, setUser] = useState(null);
  const [personas, setPersonas] = useState([]);
  const [error, setError] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [personaEditando, setPersonaEditando] = useState(null);
  const [activeTab, setActiveTab] = useState("");
  const [mostrarDatos, setMostrarDatos] = useState(false);
  const [showHelp, setShowHelp] = useState(false);
  const [formData, setFormData] = useState({
    nombre: "",
    apellido: "",
    numeroIdentificacion: "",
    telefono: "",
    direccion: "",
  });

  const navigate = useNavigate();

  useEffect(() => {
    const fetchPersonas = async () => {
      try {
        const currentUser = authService.getCurrentUser();
        setUser(currentUser);

        if (!currentUser?.idFinca) {
          console.error("ID de finca no disponible");
          return;
        }

        let url = "";

        if (activeTab === "clientes") {
          url = `http://localhost:8080/api/clientes/finca/${currentUser.idFinca}`;
        } else if (activeTab === "proveedores") {
          url = `http://localhost:8080/api/proveedores/finca/${currentUser.idFinca}`;
        } else {
          return;
        }

        const res = await authService.authFetch(url);
        if (!res.ok) throw new Error(`Error al cargar los datos de ${activeTab}`);

        const data = await res.json();

        if (activeTab === "clientes") {
          setPersonas(
              data.map((c) => ({
                idPersona: c.idCliente || c.id,
                nombre: c.nombre || c.persona?.nombre || "N/A",
                apellido: c.apellido || c.persona?.apellido || "N/A",
                numeroIdentificacion: c.numeroIdentificacion || c.persona?.numeroIdentificacion || "N/A",
                telefono: c.telefono || c.persona?.telefono || "N/A",
                direccion: c.direccion || c.persona?.direccion || "N/A",
                tipo: "Cliente",
                tipoEntidad: "cliente",
              }))
          );
        } else if (activeTab === "proveedores") {
          setPersonas(
              data.map((p) => ({
                idPersona: p.idProveedor || p.id,
                nombre: p.nombre || p.persona?.nombre || "N/A",
                apellido: p.apellido || p.persona?.apellido || "N/A",
                numeroIdentificacion: p.numeroIdentificacion || p.persona?.numeroIdentificacion || "N/A",
                telefono: p.telefono || p.persona?.telefono || "N/A",
                direccion: p.direccion || p.persona?.direccion || "N/A",
                tipo: "Proveedor",
                tipoEntidad: "proveedor",
              }))
          );
        }

      } catch (e) {
        setError(e.message);
      }
    };

    if (mostrarDatos && activeTab) fetchPersonas();
  }, [activeTab, mostrarDatos]);

  const toggleMenu = () => setIsOpen(!isOpen);
  const toggleDropdown = () => setShowDropdown(!showDropdown);
  const handleLogout = () => authService.logout();
  const toggleHelp = () => setShowHelp(!showHelp);

  const irARegistrarPersona = () => {
    const tipo = activeTab === "clientes" ? "cliente" : "proveedor";
    navigate(`/registrar-persona?tipo=${tipo}`);
  };

  const handleEliminarPersona = async (persona) => {
    try {
      const endpoint =
          persona.tipo === "Cliente"
              ? `http://localhost:8080/api/clientes/${persona.idPersona}`
              : `http://localhost:8080/api/proveedores/${persona.idPersona}`;
      const res = await fetch(endpoint, { method: "DELETE" });
      if (!res.ok) throw new Error("Error al eliminar persona");
      setPersonas(
          personas.filter(
              (p) => p.idPersona !== persona.idPersona || p.tipo !== persona.tipo,
          ),
      );
    } catch (e) {
      setError(e.message);
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
    });
    setShowModal(true);
  };

  const cerrarModal = () => {
    setShowModal(false);
    setPersonaEditando(null);
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const endpoint =
          personaEditando.tipo === "Cliente"
              ? `http://localhost:8080/api/clientes/${personaEditando.idPersona}`
              : `http://localhost:8080/api/proveedores/${personaEditando.idPersona}`;

      let requestBody;

      if (personaEditando.tipo === "Cliente") {
        requestBody = {
          idCliente: personaEditando.idPersona,
          nombre: formData.nombre,
          apellido: formData.apellido,
          tipoId: 1,
          numeroIdentificacion: formData.numeroIdentificacion,
          telefono: formData.telefono,
          direccion: formData.direccion,
          idFinca: user?.idFinca,
          tipoCliente: "REGULAR",
          fechaRegistro: new Date().toISOString().split("T")[0],
        };
      } else {
        requestBody = {
          idProveedor: personaEditando.idPersona,
          nombre: formData.nombre,
          apellido: formData.apellido,
          tipoId: 1,
          numeroIdentificacion: formData.numeroIdentificacion,
          telefono: formData.telefono,
          direccion: formData.direccion,
          idFinca: user?.idFinca,
          contacto: formData.telefono,
        };
      }

      const res = await fetch(endpoint, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(requestBody),
      });

      if (!res.ok) throw new Error("Error al actualizar persona");

      setPersonas(
          personas.map((p) =>
              p.idPersona === personaEditando.idPersona &&
              p.tipo === personaEditando.tipo
                  ? { ...p, ...formData }
                  : p,
          ),
      );

      cerrarModal();
    } catch (e) {
      setError(e.message);
    }
  };

  return (
      <div className="main-container">
        <div className="topbar">
          <img src={logo} alt="Logo" className="logo-mini" />
          <div className="user-dropdown" onClick={toggleDropdown}>
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
        <div className="content-wrapper">
          <div className={`sidebar ${isOpen ? "open" : "collapsed"}`}>
            <button className="toggle-button" onClick={toggleMenu}>
              {isOpen ? <FaTimes /> : <FaBars />}{" "}
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
                <FaCheck /> {isOpen && "Producción"}
              </button>
              <button onClick={() => navigate("/ventas")}>
                <FaCreditCard /> {isOpen && "Ventas"}
              </button>
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
            <div className="personas-container">
              <div className="inicio-personas">
                <button
                    className="boton-seleccion"
                    onClick={() => {
                      setActiveTab("clientes");
                      setMostrarDatos(true);
                    }}
                >
                  Clientes
                </button>
                <button
                    className="boton-seleccion"
                    onClick={() => {
                      setActiveTab("proveedores");
                      setMostrarDatos(true);
                    }}
                >
                  Proveedores
                </button>
              </div>
              {mostrarDatos && (
                  <>
                    <div className="personas-header">
                      <h2 className="personas-title">Personas</h2>
                      <div style={{ display: 'flex', alignItems: 'center', position: 'relative' }}>
                        <button
                            className="btn-registrar"
                            onClick={irARegistrarPersona}
                        >
                          Registrar{" "}
                          {activeTab === "clientes" ? "cliente" : "proveedor"}
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
                            <div className="help-tooltip"
                                 onMouseEnter={() => setShowHelp(true)}
                                 onMouseLeave={() => setShowHelp(false)}>
                              <h4>Ayuda - Funciones de los botones</h4>
                              <ul>
                                <li><strong>Clientes/Proveedores:</strong> Selecciona el tipo de personas a gestionar.</li>
                                <li><strong>Registrar:</strong> Abre el formulario para crear un nuevo registro.</li>
                                <li><strong>Actualizar:</strong> Permite modificar los datos de un registro existente.</li>
                                <li><strong>Eliminar:</strong> Elimina permanentemente el registro seleccionado.</li>
                              </ul>
                            </div>
                        )}
                      </div>
                    </div>
                    {error && <div className="error-message">{error}</div>}
                    <table className="personas-table">
                      <thead>
                      <tr>
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
                  </>
              )}
            </div>
          </div>
        </div>
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
                  <div className="modal-buttons">
                    <button
                        type="button"
                        className="btn-cancelar"
                        onClick={cerrarModal}
                    >
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
        <div className="watermark">
          <img src={watermarkImage} alt="Marca de agua" />
        </div>
      </div>
  );
};

export default Personas;