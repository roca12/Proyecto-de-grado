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
  FaTable,
  FaEdit,
  FaTrash,
} from "react-icons/fa";
import authService from "../authService";
import logo from "./../assets/APROAFA2.png";
import logoMini from "./../assets/APROAFA.jpg";

const Actividades = () => {
  const [isOpen, setIsOpen] = useState(true);
  const [showDropdown, setShowDropdown] = useState(false);
  const [actividades, setActividades] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [actividadEditando, setActividadEditando] = useState(null);
  const [formData, setFormData] = useState({
    idTipoActividad: "",
    fechaInicio: "",
    fechaFin: "",
    descripcion: "",
  });
  const navigate = useNavigate();

  useEffect(() => {
    const fetchActividades = async () => {
      try {
        const userData = authService.getCurrentUser();

        if (!userData?.idFinca) {
          console.error("ID de finca no disponible");
          return;
        }

        const response = await fetch(
          `http://localhost:8080/actividades/finca/${userData.idFinca}`,
        );

        if (!response.ok) throw new Error("Error al obtener actividades");

        const data = await response.json();
        setActividades(data);
      } catch (error) {
        console.error("Error:", error.message);
      }
    };

    fetchActividades();
  }, []);

  const toggleMenu = () => setIsOpen(!isOpen);
  const toggleDropdown = () => setShowDropdown(!showDropdown);
  const handleLogout = () => authService.logout();
  const irARegistrarActividad = () => navigate("/registrar-actividad");

  const handleEliminarActividad = async (idActividad) => {
    try {
      const response = await fetch(
        `http://localhost:8080/actividades/${idActividad}`,
        {
          method: "DELETE",
        },
      );

      if (!response.ok) throw new Error("Error al eliminar actividad");

      setActividades(
        actividades.filter((act) => act.idActividad !== idActividad),
      );
    } catch (error) {
      console.error("Error:", error.message);
    }
  };

  const abrirModalActualizar = (actividad) => {
    setActividadEditando(actividad);
    setFormData({
      idTipoActividad: actividad.idTipoActividad,
      fechaInicio: actividad.fechaInicio.split("T")[0],
      fechaFin: actividad.fechaFin ? actividad.fechaFin.split("T")[0] : "",
      descripcion: actividad.descripcion,
    });
    setShowModal(true);
  };

  const cerrarModal = () => {
    setShowModal(false);
    setActividadEditando(null);
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await fetch(
        `http://localhost:8080/actividades/${actividadEditando.idActividad}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            ...formData,
            idFinca: actividadEditando.idFinca,
          }),
        },
      );

      if (!response.ok) throw new Error("Error al actualizar actividad");

      const data = await response.json();

      // Actualizar la lista de actividades
      setActividades(
        actividades.map((act) =>
          act.idActividad === actividadEditando.idActividad ? data : act,
        ),
      );

      cerrarModal();
    } catch (error) {
      console.error("Error:", error.message);
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
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                {actividades.map((actividad) => (
                  <tr key={actividad.idActividad}>
                    <td>{actividad.idFinca}</td>
                    <td>{actividad.idTipoActividad}</td>
                    <td>{actividad.fechaInicio}</td>
                    <td>{actividad.fechaFin || "-"}</td>
                    <td>{actividad.descripcion}</td>
                    <td className="actions-cell">
                      <button
                        className="btn-actualizar"
                        onClick={() => abrirModalActualizar(actividad)}
                      >
                        <FaEdit /> Actualizar
                      </button>
                      <button
                        className="btn-eliminar"
                        onClick={() =>
                          handleEliminarActividad(actividad.idActividad)
                        }
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
            <h3>Actualizar Actividad</h3>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label>Tipo de Actividad:</label>
                <input
                  type="text"
                  name="idTipoActividad"
                  value={formData.idTipoActividad}
                  onChange={handleInputChange}
                  required
                />
              </div>
              <div className="form-group">
                <label>Fecha de Inicio:</label>
                <input
                  type="date"
                  name="fechaInicio"
                  value={formData.fechaInicio}
                  onChange={handleInputChange}
                  required
                />
              </div>
              <div className="form-group">
                <label>Fecha de Finalización:</label>
                <input
                  type="date"
                  name="fechaFin"
                  value={formData.fechaFin}
                  onChange={handleInputChange}
                />
              </div>
              <div className="form-group">
                <label>Descripción:</label>
                <textarea
                  name="descripcion"
                  value={formData.descripcion}
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
    </div>
  );
};

export default Actividades;
