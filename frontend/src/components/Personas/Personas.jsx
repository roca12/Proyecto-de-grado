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
  const [formData, setFormData] = useState({
    nombre: "",
    apellido: "",
    numeroIdentificacion: "",
    telefono: "",
    direccion: "",
    correo: "",
  });

  const navigate = useNavigate();

  useEffect(() => {
    const fetchPersonas = async () => {
      try {
        const currentUser = authService.getCurrentUser();
        setUser(currentUser);

        if (activeTab === "clientes") {
          const res = await fetch("http://localhost:8080/api/clientes");
          if (!res.ok) throw new Error("Error al cargar los datos de clientes");
          const data = await res.json();
          setPersonas(data.map(c => ({
            idPersona: c.idCliente || c.id,
            nombre: c.nombre || c.persona?.nombre || "N/A",
            apellido: c.apellido || c.persona?.apellido || "N/A",
            numeroIdentificacion: c.numeroIdentificacion || c.persona?.numeroIdentificacion || "N/A",
            telefono: c.telefono || c.persona?.telefono || "N/A",
            direccion: c.direccion || c.persona?.direccion || "N/A",
            correo: c.correo || c.persona?.correo || "N/A",
            tipo: "Cliente",
            tipoEntidad: "cliente"
          })));
        } else if (activeTab === "proveedores") {
          const res = await fetch("http://localhost:8080/api/proveedores");
          if (!res.ok) throw new Error("Error al cargar los datos de proveedores");
          const data = await res.json();
          setPersonas(data.map(p => ({
            idPersona: p.idProveedor || p.id,
            nombre: p.nombre || p.persona?.nombre || "N/A",
            apellido: p.apellido || p.persona?.apellido || "N/A",
            numeroIdentificacion: p.numeroIdentificacion || p.persona?.numeroIdentificacion || "N/A",
            telefono: p.telefono || p.persona?.telefono || "N/A",
            direccion: p.direccion || p.persona?.direccion || "N/A",
            correo: p.correo || p.persona?.correo || "N/A",
            tipo: "Proveedor",
            tipoEntidad: "proveedor"
          })));
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

  // ✅ CORREGIDO: pasar tipo=cliente o tipo=proveedor (singular)
  const irARegistrarPersona = () => {
    const tipo = activeTab === "clientes" ? "cliente" : "proveedor";
    navigate(`/registrar-persona?tipo=${tipo}`);
  };

  const handleEliminarPersona = async (persona) => {
    try {
      const endpoint = persona.tipo === "Cliente"
          ? `http://localhost:8080/api/clientes/${persona.idPersona}`
          : `http://localhost:8080/api/proveedores/${persona.idPersona}`;
      const res = await fetch(endpoint, { method: "DELETE" });
      if (!res.ok) throw new Error("Error al eliminar persona");
      setPersonas(personas.filter(p => p.idPersona !== persona.idPersona || p.tipo !== persona.tipo));
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
      correo: persona.correo,
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

  // ... todas las importaciones sin cambio ...

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const endpoint = personaEditando.tipo === "Cliente"
          ? `http://localhost:8080/api/clientes/${personaEditando.idPersona}`
          : `http://localhost:8080/api/proveedores/${personaEditando.idPersona}`;

      let requestBody;

      if (personaEditando.tipo === "Cliente") {
        requestBody = {
          idCliente: personaEditando.idPersona,
          nombre: formData.nombre,
          apellido: formData.apellido,
          tipoId: 1, // podrías usar uno real si lo tienes guardado
          numeroIdentificacion: formData.numeroIdentificacion,
          telefono: formData.telefono,
          direccion: formData.direccion,
          email: formData.correo,
          idFinca: 1, // ajusta si tienes finca dinámica
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
          email: formData.correo,
          idFinca: 1,
          contacto: formData.telefono,
        };
      }

      const res = await fetch(endpoint, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(requestBody),
      });

      if (!res.ok) throw new Error("Error al actualizar persona");

      setPersonas(personas.map(p =>
          p.idPersona === personaEditando.idPersona && p.tipo === personaEditando.tipo
              ? { ...p, ...formData }
              : p
      ));

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
              {isOpen ? <FaTimes /> : <FaBars />} <span>{isOpen ? "Ocultar menú" : ""}</span>
            </button>
            <div className="menu-items">
              <button onClick={() => navigate("/actividades")}><FaAddressBook /> {isOpen && "Actividades"}</button>
              <button onClick={() => navigate("/personas")}><FaUser /> {isOpen && "Personas"}</button>
              <button onClick={() => navigate("/insumos")}><FaTruck /> {isOpen && "Insumos"}</button>
              <button onClick={() => navigate("/produccion")}><FaCheck /> {isOpen && "Producción"}</button>
              <button><FaCreditCard /> {isOpen && "Ventas"}</button>
              <button><FaFile /> {isOpen && "Documentos"}</button>
              <button><FaChartArea /> {isOpen && "Reportes"}</button>
              <button onClick={() => navigate("/cultivo")}><FaTable /> {isOpen && "Cultivos"}</button>
            </div>
            <img src={logoMini} alt="Logo inferior" className="footer-img" />
          </div>
          <div className="main-content">
            <div className="personas-container">
              <div className="inicio-personas">
                <button className="boton-seleccion" onClick={() => { setActiveTab("clientes"); setMostrarDatos(true); }}>Clientes</button>
                <button className="boton-seleccion" onClick={() => { setActiveTab("proveedores"); setMostrarDatos(true); }}>Proveedores</button>
              </div>
              {mostrarDatos && (
                  <>
                    <div className="personas-header">
                      <h2 className="personas-title">Personas</h2>
                      <button className="btn-registrar" onClick={irARegistrarPersona}>
                        Registrar {activeTab === "clientes" ? "cliente" : "proveedor"}
                      </button>
                    </div>
                    {error && <div className="error-message">{error}</div>}
                    <table className="personas-table">
                      <thead>
                      <tr>
                        <th>Nombre</th><th>Apellidos</th><th>Documento</th><th>Teléfono</th><th>Dirección</th><th>Acciones</th>
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
                              <button className="btn-actualizar" onClick={() => abrirModalActualizar(persona)}><FaEdit /> Actualizar</button>
                              <button className="btn-eliminar" onClick={() => handleEliminarPersona(persona)}><FaTrash /> Eliminar</button>
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
                  <div className="form-group"><label>Nombre:</label><input type="text" name="nombre" value={formData.nombre} onChange={handleInputChange} required /></div>
                  <div className="form-group"><label>Apellidos:</label><input type="text" name="apellido" value={formData.apellido} onChange={handleInputChange} required /></div>
                  <div className="form-group"><label>Documento:</label><input type="text" name="numeroIdentificacion" value={formData.numeroIdentificacion} onChange={handleInputChange} required /></div>
                  <div className="form-group"><label>Teléfono:</label><input type="text" name="telefono" value={formData.telefono} onChange={handleInputChange} /></div>
                  <div className="form-group"><label>Dirección:</label><input type="text" name="direccion" value={formData.direccion} onChange={handleInputChange} /></div>
                  <div className="form-group"><label>Correo electrónico:</label><input type="email" name="correo" value={formData.correo} onChange={handleInputChange} /></div>
                  <div className="modal-buttons">
                    <button type="button" className="btn-cancelar" onClick={cerrarModal}>Cancelar</button>
                    <button type="submit" className="btn-guardar">Guardar Cambios</button>
                  </div>
                </form>
              </div>
            </div>
        )}
        <div className="watermark"><img src={watermarkImage} alt="Marca de agua" /></div>
      </div>
  );
};

export default Personas;
