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
import watermarkImage from "./../assets/LogoBosque.png";

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
  const [showHelp, setShowHelp] = useState(false);

  // Nuevos estados para insumos
  const [insumosDisponibles, setInsumosDisponibles] = useState([]);
  const [usosInsumos, setUsosInsumos] = useState([]);
  const [loadingInsumos, setLoadingInsumos] = useState(false);
  const [nombresInsumos, setNombresInsumos] = useState({}); // Nuevo estado para mapear IDs a nombres

  const navigate = useNavigate();

  // Tipos de actividad definidos
  const tiposActividad = [
    { id: 1, nombre: "Fertilización" },
    { id: 2, nombre: "Riego" },
    { id: 3, nombre: "Control de plagas" },
    { id: 4, nombre: "Siembra" },
    { id: 5, nombre: "Cosecha" },
  ];

  useEffect(() => {
    const fetchActividadesYInsumos = async () => {
      try {
        const userData = authService.getCurrentUser();

        if (!userData?.idFinca) {
          console.error("ID de finca no disponible");
          return;
        }

        // Obtener actividades
        const actividadesResponse = await fetch(
          `http://localhost:8080/actividades/finca/${userData.idFinca}`,
        );
        if (!actividadesResponse.ok)
          throw new Error("Error al obtener actividades");
        const actividadesData = await actividadesResponse.json();

        // Obtener todos los insumos para mapear nombres
        const insumosResponse = await authService.authFetch(
          "http://localhost:8080/insumos",
        );
        if (!insumosResponse.ok) throw new Error("Error al obtener insumos");
        const insumosData = await insumosResponse.json();

        // Crear un mapeo de ID a nombre
        const nombresMap = {};
        insumosData.forEach((insumo) => {
          nombresMap[insumo.idInsumo] = insumo.nombre;
        });
        setNombresInsumos(nombresMap);

        setActividades(actividadesData);
      } catch (error) {
        console.error("Error:", error.message);
      }
    };

    fetchActividadesYInsumos();
  }, []);

  // Función para cargar insumos disponibles
  const cargarInsumosDisponibles = async () => {
    try {
      setLoadingInsumos(true);
      const response = await authService.authFetch(
        "http://localhost:8080/insumos",
      );
      if (!response.ok) throw new Error("Error al obtener insumos");
      const data = await response.json();
      setInsumosDisponibles(data);
    } catch (error) {
      console.error("Error al cargar insumos:", error);
    } finally {
      setLoadingInsumos(false);
    }
  };

  const toggleMenu = () => setIsOpen(!isOpen);
  const toggleDropdown = () => setShowDropdown(!showDropdown);
  const handleLogout = () => authService.logout();
  const irARegistrarActividad = () => navigate("/registrar-actividad");
  const toggleHelp = () => setShowHelp(!showHelp);

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

  const abrirModalActualizar = async (actividad) => {
    setActividadEditando(actividad);

    // Cargar insumos disponibles
    await cargarInsumosDisponibles();

    setFormData({
      idTipoActividad: actividad.idTipoActividad,
      fechaInicio: actividad.fechaInicio.split("T")[0],
      fechaFin: actividad.fechaFin ? actividad.fechaFin.split("T")[0] : "",
      descripcion: actividad.descripcion,
    });

    // Cargar insumos actuales de la actividad (si los tiene)
    setUsosInsumos(actividad.usosInsumos || []);

    setShowModal(true);
  };

  const cerrarModal = () => {
    setShowModal(false);
    setActividadEditando(null);
    setUsosInsumos([]);
    setInsumosDisponibles([]);
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  // Funciones para manejar insumos
  const agregarInsumo = () => {
    setUsosInsumos([
      ...usosInsumos,
      {
        idInsumo: "",
        cantidad: "",
        fecha: formData.fechaInicio || new Date().toISOString().split("T")[0],
      },
    ]);
  };

  const actualizarInsumo = (index, campo, valor) => {
    const nuevosUsos = [...usosInsumos];
    nuevosUsos[index][campo] = valor;
    setUsosInsumos(nuevosUsos);
  };

  const eliminarInsumo = (index) => {
    const nuevosUsos = [...usosInsumos];
    nuevosUsos.splice(index, 1);
    setUsosInsumos(nuevosUsos);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      // Filtrar insumos válidos (que tengan idInsumo y cantidad)
      const insumosValidos = usosInsumos.filter(
        (insumo) => insumo.idInsumo && insumo.cantidad && insumo.cantidad > 0,
      );

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
            usosInsumos: insumosValidos,
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

  // Función para obtener el nombre del tipo de actividad
  const obtenerNombreTipoActividad = (idTipo) => {
    const tipo = tiposActividad.find((t) => t.id === idTipo);
    return tipo ? tipo.nombre : `Tipo ${idTipo}`;
  };

  // Función para obtener el nombre del insumo basado en el ID
  const obtenerNombreInsumo = (idInsumo) => {
    return nombresInsumos[idInsumo] || `Insumo ${idInsumo}`;
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

        {/* Vista de actividades */}
        <div className="main-content">
          <div className="actividades-container">
            <div className="actividades-header">
              <h2 className="actividades-title">Actividades</h2>
              <div
                style={{
                  display: "flex",
                  alignItems: "center",
                  position: "relative",
                }}
              >
                <button
                  className="btn-registrar"
                  onClick={irARegistrarActividad}
                >
                  Registrar actividad
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
                        <strong>Registrar actividad:</strong> Abre el formulario
                        para crear una nueva actividad.
                      </li>
                      <li>
                        <strong>Actualizar:</strong> Permite modificar los datos
                        de una actividad existente y gestionar insumos.
                      </li>
                      <li>
                        <strong>Eliminar:</strong> Elimina permanentemente la
                        actividad seleccionada.
                      </li>
                    </ul>
                  </div>
                )}
              </div>
            </div>

            <table className="actividades-table">
              <thead>
                <tr>
                  <th>Finca</th>
                  <th>Tipo de actividad</th>
                  <th>Fecha de inicio</th>
                  <th>Fecha de finalización</th>
                  <th>Descripción</th>
                  <th>Insumos</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                {actividades.map((actividad) => (
                  <tr key={actividad.idActividad}>
                    <td>{actividad.idFinca}</td>
                    <td>
                      {obtenerNombreTipoActividad(actividad.idTipoActividad)}
                    </td>
                    <td>{actividad.fechaInicio}</td>
                    <td>{actividad.fechaFin || "-"}</td>
                    <td>{actividad.descripcion}</td>
                    <td>
                      {actividad.usosInsumos &&
                      actividad.usosInsumos.length > 0 ? (
                        <ul style={{ paddingLeft: "1em", margin: 0 }}>
                          {actividad.usosInsumos.map((uso, idx) => (
                            <li key={idx}>
                              {obtenerNombreInsumo(uso.idInsumo)} -{" "}
                              {uso.cantidad} unidad(es){" "}
                              {uso.fecha ? `(${uso.fecha})` : ""}
                            </li>
                          ))}
                        </ul>
                      ) : (
                        "Sin insumos"
                      )}
                    </td>
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

      {/* Modal de actualización mejorado */}
      {showModal && (
        <div className="modal-overlay">
          <div
            className="modal-content"
            style={{ maxWidth: "800px", maxHeight: "90vh", overflowY: "auto" }}
          >
            <h3>Actualizar Actividad</h3>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label>Tipo de Actividad:</label>
                <select
                  name="idTipoActividad"
                  value={formData.idTipoActividad}
                  onChange={handleInputChange}
                  required
                >
                  <option value="">Seleccione un tipo</option>
                  {tiposActividad.map((tipo) => (
                    <option key={tipo.id} value={tipo.id}>
                      {tipo.nombre}
                    </option>
                  ))}
                </select>
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

              {/* Sección de insumos */}
              <div className="insumos-section">
                <div
                  style={{
                    display: "flex",
                    justifyContent: "space-between",
                    alignItems: "center",
                    marginBottom: "10px",
                  }}
                >
                  <h4>Insumos utilizados (opcional)</h4>
                  <button
                    type="button"
                    onClick={agregarInsumo}
                    className="btn-agregar-insumo"
                    disabled={loadingInsumos}
                  >
                    + Agregar insumo
                  </button>
                </div>

                {usosInsumos.map((insumo, index) => (
                  <div
                    key={index}
                    className="insumo-item"
                    style={{
                      display: "flex",
                      gap: "10px",
                      marginBottom: "10px",
                      alignItems: "center",
                      padding: "10px",
                      border: "1px solid #ddd",
                      borderRadius: "5px",
                      backgroundColor: "#f9f9f9",
                    }}
                  >
                    <select
                      value={insumo.idInsumo}
                      onChange={(e) =>
                        actualizarInsumo(
                          index,
                          "idInsumo",
                          parseInt(e.target.value),
                        )
                      }
                      style={{ flex: "2" }}
                    >
                      <option value="">Seleccione un insumo</option>
                      {insumosDisponibles.map((i) => (
                        <option key={i.idInsumo} value={i.idInsumo}>
                          {i.nombre} (Stock: {i.cantidadDisponible})
                        </option>
                      ))}
                    </select>

                    <input
                      type="number"
                      step="0.01"
                      min="0.01"
                      placeholder="Cantidad"
                      value={insumo.cantidad}
                      onChange={(e) =>
                        actualizarInsumo(
                          index,
                          "cantidad",
                          parseFloat(e.target.value),
                        )
                      }
                      style={{ flex: "1" }}
                    />

                    <input
                      type="date"
                      value={insumo.fecha}
                      onChange={(e) =>
                        actualizarInsumo(index, "fecha", e.target.value)
                      }
                      style={{ flex: "1" }}
                    />

                    <button
                      type="button"
                      onClick={() => eliminarInsumo(index)}
                      className="btn-eliminar-insumo"
                      style={{
                        backgroundColor: "#dc3545",
                        color: "white",
                        border: "none",
                        padding: "5px 10px",
                        borderRadius: "3px",
                        cursor: "pointer",
                      }}
                    >
                      ✕
                    </button>
                  </div>
                ))}

                {usosInsumos.length === 0 && (
                  <p style={{ color: "#666", fontStyle: "italic" }}>
                    No hay insumos agregados. Puedes agregar insumos
                    opcionalmente.
                  </p>
                )}
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

export default Actividades;
