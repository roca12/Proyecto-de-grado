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
  FaEdit,
  FaTrash,
} from "react-icons/fa";
import authService from "../authService";
import logo from "./../assets/APROAFA2.png";
import logoMini from "./../assets/APROAFA.jpg";
import watermarkImage from "./../assets/LogoBosque.png";

const Insumos = () => {
  const [isOpen, setIsOpen] = useState(true);
  const [showDropdown, setShowDropdown] = useState(false);
  const [user, setUser] = useState(null);
  const [insumos, setInsumos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [insumoEditando, setInsumoEditando] = useState(null);
  const [formData, setFormData] = useState({
    nombre: "",
    descripcion: "",
    unidadMedida: "",
    idProveedor: "",
    cantidadDisponible: "",
  });
  const [proveedores, setProveedores] = useState([]);

  const navigate = useNavigate();

  useEffect(() => {
    const fetchData = async () => {
      try {
        const currentUser = authService.getCurrentUser();
        setUser(currentUser);

        // Obtener insumos y proveedores en paralelo
        const [insumosResponse, proveedoresResponse] = await Promise.all([
          fetch("http://localhost:8080/insumos"),
          fetch("http://localhost:8080/api/proveedores"),
        ]);

        if (!insumosResponse.ok || !proveedoresResponse.ok) {
          throw new Error("Error al cargar los datos");
        }

        const insumosData = await insumosResponse.json();
        const proveedoresData = await proveedoresResponse.json();

        setInsumos(insumosData);
        setProveedores(proveedoresData);
        setLoading(false);
      } catch (error) {
        console.error("Error al cargar datos:", error);
        setError(error.message);
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  const toggleMenu = () => setIsOpen(!isOpen);
  const toggleDropdown = () => setShowDropdown(!showDropdown);
  const handleLogout = () => authService.logout();
  const irARegistrarInsumo = () => navigate("/registrar-insumo");

  const handleEliminarInsumo = async (idInsumo) => {
    try {
      const response = await fetch(
        `http://localhost:8080/insumos/${idInsumo}`,
        {
          method: "DELETE",
        },
      );

      if (!response.ok) throw new Error("Error al eliminar insumo");

      setInsumos(insumos.filter((i) => i.idInsumo !== idInsumo));
    } catch (error) {
      console.error("Error:", error.message);
      setError(error.message);
    }
  };

  const abrirModalActualizar = (insumo) => {
    setInsumoEditando(insumo);
    setFormData({
      nombre: insumo.nombre,
      descripcion: insumo.descripcion || "",
      unidadMedida: insumo.unidadMedida,
      idProveedor: insumo.proveedor?.idProveedor || "",
      cantidadDisponible: insumo.cantidadDisponible,
    });
    setShowModal(true);
  };

  const cerrarModal = () => {
    setShowModal(false);
    setInsumoEditando(null);
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
      const requestData = {
        nombre: formData.nombre,
        descripcion: formData.descripcion,
        unidadMedida: formData.unidadMedida,
        idProveedor: parseInt(formData.idProveedor),
        cantidadDisponible: parseFloat(formData.cantidadDisponible),
      };

      console.log("Enviando datos:", requestData);

      const response = await fetch(
        `http://localhost:8080/insumos/${insumoEditando.idInsumo}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(requestData),
        },
      );

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`Error al actualizar insumo: ${errorText}`);
      }

      const data = await response.json();

      // Actualizar la lista de insumos
      setInsumos(
        insumos.map((insumo) =>
          insumo.idInsumo === insumoEditando.idInsumo ? data : insumo,
        ),
      );

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
          <div className="insumos-container">
            <div className="insumos-header">
              <h2 className="insumos-title">Insumos</h2>
              <button className="btn-registrar" onClick={irARegistrarInsumo}>
                Registrar Insumo
              </button>
            </div>

            {/* Mensajes de estado */}
            {loading && (
              <div className="loading-message">Cargando insumos...</div>
            )}
            {error && <div className="error-message">{error}</div>}

            {/* Tabla de insumos */}
            <table className="insumos-table">
              <thead>
                <tr>
                  <th>Nombre</th>
                  <th>Descripción</th>
                  <th>Unidad de medida</th>
                  <th>Proveedor</th>
                  <th>Cantidad disponible</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                {insumos.map((insumo) => (
                  <tr key={insumo.idInsumo}>
                    <td>{insumo.nombre}</td>
                    <td>{insumo.descripcion || "-"}</td>
                    <td>{insumo.unidadMedida}</td>
                    <td>{insumo.proveedor?.nombre || "Sin proveedor"}</td>
                    <td>{insumo.cantidadDisponible}</td>
                    <td className="actions-cell">
                      <button
                        className="btn-actualizar"
                        onClick={() => abrirModalActualizar(insumo)}
                      >
                        <FaEdit /> Actualizar
                      </button>
                      <button
                        className="btn-eliminar"
                        onClick={() => handleEliminarInsumo(insumo.idInsumo)}
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
            <h3>Actualizar Insumo</h3>
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
                <label>Descripción:</label>
                <textarea
                  name="descripcion"
                  value={formData.descripcion}
                  onChange={handleInputChange}
                />
              </div>
              <div className="form-group">
                <label>Unidad de medida:</label>
                <input
                  type="text"
                  name="unidadMedida"
                  value={formData.unidadMedida}
                  onChange={handleInputChange}
                  required
                />
              </div>
              <div className="form-group">
                <label>Proveedor:</label>
                <select
                  name="idProveedor"
                  value={formData.idProveedor}
                  onChange={handleInputChange}
                >
                  <option value="">Seleccione un proveedor</option>
                  {proveedores.map((proveedor) => (
                    <option
                      key={proveedor.idProveedor}
                      value={proveedor.idProveedor}
                    >
                      {proveedor.nombre}
                    </option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label>Cantidad disponible:</label>
                <input
                  type="number"
                  name="cantidadDisponible"
                  value={formData.cantidadDisponible}
                  onChange={handleInputChange}
                  min="0"
                  step="0.01"
                  required
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

export default Insumos;
