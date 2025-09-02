import "./Produccion.css";
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

const Produccion = () => {
  const [isOpen, setIsOpen] = useState(true);
  const [showDropdown, setShowDropdown] = useState(false);
  const [user, setUser] = useState(null);
  const [producciones, setProducciones] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [produccionEditando, setProduccionEditando] = useState(null);
  const [showHelp, setShowHelp] = useState(false);

  // Estados para insumos
  const [insumosDisponibles, setInsumosDisponibles] = useState([]);
  const [usosInsumos, setUsosInsumos] = useState([]);
  const [loadingInsumos, setLoadingInsumos] = useState(false);
  const [nombresInsumos, setNombresInsumos] = useState({});

  const [productos, setProductos] = useState([]);
  const [fincas, setFincas] = useState([]);
  const [fincaUsuario, setFincaUsuario] = useState(null);

  const [formData, setFormData] = useState({
    idProducto: "",
    idFinca: "",
    fechaSiembra: "",
    fechaCosecha: "",
    estado: "",
    cantidadCosechada: "",
  });

  const navigate = useNavigate();

  // Carga inicial de datos
  useEffect(() => {
    const fetchData = async () => {
      try {
        const currentUser = authService.getCurrentUser();
        setUser(currentUser);

        if (!currentUser?.idFinca) {
          throw new Error("El usuario no tiene una finca asociada.");
        }

        const idFincaUsuario = currentUser.idFinca;

        // Obtener datos en paralelo
        const [
          produccionesResponse,
          productosResponse,
          fincasResponse,
          insumosResponse,
        ] = await Promise.all([
          fetch(`http://localhost:8080/produccion/finca/${idFincaUsuario}`, {
            headers: {
              Authorization: `Bearer ${localStorage.getItem("authToken")}`,
            },
          }),
          fetch("http://localhost:8080/api/productos"),
          fetch("http://localhost:8080/api/fincas"),
          fetch("http://localhost:8080/insumos"),
        ]);

        if (!produccionesResponse.ok) {
          const errorData = await produccionesResponse.json().catch(() => ({}));
          throw new Error(
            errorData.message ||
              `Error ${produccionesResponse.status}: ${produccionesResponse.statusText}`,
          );
        }

        const produccionesData = await produccionesResponse.json();
        setProducciones(produccionesData);

        if (productosResponse.ok) {
          const productosData = await productosResponse.json();
          setProductos(productosData);
        }

        if (fincasResponse.ok) {
          const fincasData = await fincasResponse.json();
          setFincas(fincasData);

          // Solo para tener referencia de la finca del usuario
          if (currentUser && fincasData.length > 0) {
            const fincaDelUsuario =
              fincasData.find((finca) => finca.idUsuario === currentUser.id) ||
              fincasData[0];
            setFincaUsuario(fincaDelUsuario);
          }
        }

        if (insumosResponse.ok) {
          const insumosData = await insumosResponse.json();
          const nombresMap = {};
          insumosData.forEach((insumo) => {
            nombresMap[insumo.idInsumo] = insumo.nombre;
          });
          setNombresInsumos(nombresMap);
        }

        setLoading(false);
      } catch (error) {
        console.error("Error al cargar datos:", error);
        setError(error.message);
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  // Función para cargar insumos disponibles
  const cargarInsumosDisponibles = async () => {
    try {
      setLoadingInsumos(true);
      const response = await fetch("http://localhost:8080/insumos", {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("authToken")}`,
        },
      });
      if (!response.ok) throw new Error("Error al obtener insumos");
      const data = await response.json();
      setInsumosDisponibles(data);
    } catch (error) {
      console.error("Error al cargar insumos:", error);
      setError("Error al cargar la lista de insumos");
    } finally {
      setLoadingInsumos(false);
    }
  };

  const toggleMenu = () => setIsOpen(!isOpen);
  const toggleDropdown = () => setShowDropdown(!showDropdown);
  const toggleHelp = () => setShowHelp(!showHelp);
  const handleLogout = () => authService.logout();
  const irARegistrarProduccion = () => navigate("/registrar-produccion");

  const formatDate = (dateString) => {
    if (!dateString) return "-";
    const date = new Date(dateString);
    return date.toLocaleDateString("es-ES");
  };

  const formatDateForInput = (dateString) => {
    if (!dateString) return "";
    const date = new Date(dateString);
    return date.toISOString().split("T")[0];
  };

  // Funciones para manejar insumos
  const agregarInsumo = () => {
    setUsosInsumos([
      ...usosInsumos,
      {
        idInsumo: "",
        cantidad: "",
        fecha: formData.fechaSiembra || new Date().toISOString().split("T")[0],
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

  const obtenerNombreInsumo = (idInsumo) => {
    return nombresInsumos[idInsumo] || `Insumo ${idInsumo}`;
  };

  const handleEliminarProduccion = async (idProduccion) => {
    try {
      const response = await fetch(
        `http://localhost:8080/produccion/${idProduccion}`,
        {
          method: "DELETE",
          headers: {
            Authorization: `Bearer ${localStorage.getItem("authToken")}`,
          },
        },
      );

      if (!response.ok) throw new Error("Error al eliminar producción");

      setProducciones(
        producciones.filter((p) => p.idProduccion !== idProduccion),
      );
    } catch (error) {
      console.error("Error:", error.message);
      setError(error.message);
    }
  };

  const abrirModalActualizar = async (produccion) => {
    setProduccionEditando(produccion);

    // Cargar insumos disponibles
    await cargarInsumosDisponibles();

    // Cargar insumos de la producción
    if (produccion.idProduccion) {
      try {
        const response = await fetch(
          `http://localhost:8080/produccion/${produccion.idProduccion}/insumos`,
          {
            headers: {
              Authorization: `Bearer ${localStorage.getItem("authToken")}`,
            },
          },
        );

        if (response.ok) {
          const insumosData = await response.json();
          setUsosInsumos(insumosData || []);
        }
      } catch (error) {
        console.error("Error al cargar insumos de producción:", error);
      }
    }

    setFormData({
      idProducto: produccion.idProducto || "",
      idFinca: produccion.idFinca || (fincaUsuario ? fincaUsuario.idFinca : ""),
      fechaSiembra: formatDateForInput(produccion.fechaSiembra),
      fechaCosecha: formatDateForInput(produccion.fechaCosecha),
      estado: produccion.estado || "",
      cantidadCosechada: produccion.cantidadCosechada || "",
    });
    setShowModal(true);
  };

  const cerrarModal = () => {
    setShowModal(false);
    setProduccionEditando(null);
    setUsosInsumos([]);
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
      // Filtrar insumos válidos
      const insumosValidos = usosInsumos.filter(
        (insumo) => insumo.idInsumo && insumo.cantidad && insumo.cantidad > 0,
      );

      // Si es cosechado, llamar al endpoint específico
      if (formData.estado === "COSECHADO") {
        const response = await fetch(
          `http://localhost:8080/produccion/${produccionEditando.idProduccion}/cosechar?cantidadCosechada=${formData.cantidadCosechada}&fechaCosecha=${formData.fechaCosecha}`,
          {
            method: "PUT",
            headers: {
              Authorization: `Bearer ${localStorage.getItem("authToken")}`,
            },
          },
        );

        if (!response.ok) {
          const errorText = await response.text();
          throw new Error(`Error al cosechar producción: ${errorText}`);
        }
        // Recargar página después de actualizar
        window.location.reload();
        return;
      }

      // Actualizar el resto de datos
      const produccionData = {
        idProducto: parseInt(formData.idProducto),
        idFinca: fincaUsuario
          ? fincaUsuario.idFinca
          : parseInt(formData.idFinca),
        fechaSiembra: formData.fechaSiembra,
        fechaCosecha: formData.fechaCosecha,
        estado: formData.estado,
        cantidadCosechada: formData.cantidadCosechada
          ? parseFloat(formData.cantidadCosechada)
          : null,
        usosInsumos: insumosValidos,
      };

      const updateResponse = await fetch(
        `http://localhost:8080/produccion/${produccionEditando.idProduccion}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${localStorage.getItem("authToken")}`,
          },
          body: JSON.stringify(produccionData),
        },
      );

      if (!updateResponse.ok) {
        const errorText = await updateResponse.text();
        throw new Error(`Error al actualizar producción: ${errorText}`);
      }

      let updatedProduccion;
      try {
        updatedProduccion = await updateResponse.json();
      } catch {
        updatedProduccion = { ...produccionEditando, ...produccionData };
      }

      setProducciones(
        producciones.map((p) =>
          p.idProduccion === produccionEditando.idProduccion
            ? updatedProduccion
            : p,
        ),
      );

      cerrarModal();
    } catch (error) {
      console.error("Error:", error.message);
      setError(error.message);
    }
  };

  const getNombreProducto = (id) => {
    const producto = productos.find((p) => p.idProducto === id);
    return producto ? producto.nombre : id;
  };

  const getNombreFinca = (id) => {
    const finca = fincas.find((f) => f.idFinca === id);
    return finca ? finca.nombre : id;
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

        {/* Contenido principal */}
        <div className="main-content">
          <div className="produccion-container">
            <div className="produccion-header">
              <h2 className="produccion-title">Producción Agrícola</h2>
              <div
                style={{
                  display: "flex",
                  alignItems: "center",
                  position: "relative",
                }}
              >
                <button
                  style={{ marginRight: "10px" }}
                  className="btn-registrar"
                  onClick={irARegistrarProduccion}
                >
                  Registrar Producción
                </button>
                <button
                  className="btn-registrar"
                  onClick={() => navigate("/registrar-producto")}
                >
                  Registrar Producto
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
                        <strong>Registrar Producción:</strong> Abre el
                        formulario para crear una nueva producción.
                      </li>
                      <li>
                        <strong>Registrar Producto:</strong> Abre el formulario
                        para crear un nuevo producto.
                      </li>
                      <li>
                        <strong>Actualizar:</strong> Permite modificar los datos
                        de una producción existente.
                      </li>
                      <li>
                        <strong>Eliminar:</strong> Elimina permanentemente la
                        producción seleccionada.
                      </li>
                    </ul>
                  </div>
                )}
              </div>
            </div>

            {/* Mensajes de estado */}
            {loading && (
              <div className="loading-message">Cargando producciones...</div>
            )}
            {error && <div className="error-message">{error}</div>}

            {/* Tabla de producciones */}
            <table className="produccion-table">
              <thead>
                <tr>
                  <th>Producto</th>
                  <th>Finca</th>
                  <th>Fecha Siembra</th>
                  <th>Fecha Cosecha</th>
                  <th>Estado</th>
                  <th>Cantidad Cosechada</th>
                  <th>Insumos</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                {producciones.map((produccion) => (
                  <tr key={produccion.idProduccion}>
                    <td>{getNombreProducto(produccion.idProducto)}</td>
                    <td>{getNombreFinca(produccion.idFinca)}</td>
                    <td>{formatDate(produccion.fechaSiembra)}</td>
                    <td>{formatDate(produccion.fechaCosecha)}</td>
                    <td>{produccion.estado}</td>
                    <td>{produccion.cantidadCosechada || "-"}</td>
                    <td>
                      {produccion.usosInsumos &&
                      produccion.usosInsumos.length > 0 ? (
                        <ul style={{ paddingLeft: "1em", margin: 0 }}>
                          {produccion.usosInsumos.map((uso, idx) => (
                            <li key={idx}>
                              {obtenerNombreInsumo(uso.idInsumo)} -{" "}
                              {uso.cantidad} unidad(es){" "}
                              {uso.fechaUso ? `(${uso.fechaUso})` : ""}
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
                        onClick={() => abrirModalActualizar(produccion)}
                      >
                        <FaEdit /> Actualizar
                      </button>
                      <button
                        className="btn-eliminar"
                        onClick={() =>
                          handleEliminarProduccion(produccion.idProduccion)
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
          <div
            className="modal-content"
            style={{ maxWidth: "800px", maxHeight: "90vh", overflowY: "auto" }}
          >
            <h3>Actualizar Producción</h3>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label>Producto:</label>
                <select
                  name="idProducto"
                  value={formData.idProducto}
                  onChange={handleInputChange}
                  required
                  disabled={produccionEditando.estado === "COSECHADO"} // Deshabilitar si ya fue cosechado
                >
                  <option value="">Seleccione un producto</option>
                  {productos.map((producto) => (
                    <option
                      key={producto.idProducto}
                      value={producto.idProducto}
                    >
                      {producto.nombre}
                    </option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label>Finca:</label>
                <div className="finca-asignada">
                  {fincaUsuario
                    ? getNombreFinca(fincaUsuario.idFinca)
                    : "Cargando finca..."}
                </div>
              </div>

              <div className="form-group">
                <label>Fecha de Siembra:</label>
                <input
                  type="date"
                  name="fechaSiembra"
                  value={formData.fechaSiembra}
                  onChange={handleInputChange}
                  required
                  disabled={produccionEditando.estado === "COSECHADO"} // Deshabilitar si ya fue cosechado
                />
              </div>
              <div className="form-group">
                <label>Estado:</label>
                <select
                  name="estado"
                  value={formData.estado}
                  onChange={handleInputChange}
                  required
                  disabled={produccionEditando.estado === "COSECHADO"} // No cambiar estado si ya fue cosechado
                >
                  <option value="">Seleccione un estado</option>
                  <option value="SEMBRADO">SEMBRADO</option>
                  <option value="EN_CRECIMIENTO">EN CRECIMIENTO</option>
                  <option value="COSECHADO">COSECHADO</option>
                </select>
              </div>
              {formData.estado === "COSECHADO" && (
                <>
                  <div className="form-group">
                    <label>Fecha de Cosecha:</label>
                    <input
                      type="date"
                      name="fechaCosecha"
                      value={formData.fechaCosecha}
                      onChange={handleInputChange}
                      required={formData.estado === "COSECHADO"}
                      disabled={produccionEditando.estado === "COSECHADO"} // Evitar editar si ya está cosechado
                    />
                  </div>
                  <div className="form-group">
                    <label>Cantidad Cosechada:</label>
                    <input
                      type="number"
                      name="cantidadCosechada"
                      value={formData.cantidadCosechada}
                      onChange={handleInputChange}
                      step="0.01"
                      min="0"
                      required={formData.estado === "COSECHADO"}
                      disabled={produccionEditando.estado === "COSECHADO"} // Evitar editar
                    />
                  </div>
                </>
              )}

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
                    disabled={
                      loadingInsumos ||
                      produccionEditando.estado === "COSECHADO"
                    } // Evitar agregar insumos si está cosechado
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
                      disabled={produccionEditando.estado === "COSECHADO"} // Evitar cambios
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
                      disabled={produccionEditando.estado === "COSECHADO"} // Evitar cambios
                    />

                    <input
                      type="date"
                      value={insumo.fecha}
                      onChange={(e) =>
                        actualizarInsumo(index, "fecha", e.target.value)
                      }
                      style={{ flex: "1" }}
                      disabled={produccionEditando.estado === "COSECHADO"} // Evitar cambios
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
                      disabled={produccionEditando.estado === "COSECHADO"} // Evitar eliminar
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

export default Produccion;
