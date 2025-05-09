 /**
 * @file Produccion.jsx
 * @description Componente que representa la página de gestión de producciones agrícolas.
 * Incluye navegación lateral, barra superior y una tabla para visualizar producciones registradas,
 * junto con un botón para acceder al formulario de registro de nuevas producciones.
 */

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
  FaTrash
} from "react-icons/fa";
import authService from "../authService";
import logo from "./../assets/APROAFA2.png";
import logoMini from "./../assets/APROAFA.jpg";

/**
 * Componente funcional que muestra la sección de Producciones, con acceso al registro de nuevas producciones
 * y una tabla donde se desplegarán los datos registrados. Incluye navegación lateral y barra superior.
 *
 * @component
 * @returns {JSX.Element} Página de gestión de producciones.
 */
const Produccion = () => {
  const [isOpen, setIsOpen] = useState(true); // Estado del menú lateral (expandido/colapsado)
  const [showDropdown, setShowDropdown] = useState(false); // Estado del dropdown del usuario
  const [user, setUser] = useState(null); // Información del usuario actual
  const [producciones, setProducciones] = useState([]); // Lista de producciones
  const [loading, setLoading] = useState(true); // Estado de carga
  const [error, setError] = useState(null); // Mensaje de error
  const [showModal, setShowModal] = useState(false); // Control de visibilidad del modal
  const [produccionEditando, setProduccionEditando] = useState(null); // Producción en edición

  // Estado para los productos y fincas (para el dropdown de selección)
  const [productos, setProductos] = useState([]);
  const [fincas, setFincas] = useState([]);
  const [fincaUsuario, setFincaUsuario] = useState(null); // Finca asignada al usuario

  // Estado para el formulario de actualización
  const [formData, setFormData] = useState({
    idProducto: '',
    idFinca: '',
    fechaSiembra: '',
    fechaCosecha: '',
    estado: '',
    cantidadCosechada: ''
  });

  const navigate = useNavigate();

  /**
   * Carga el usuario actual y la lista de producciones al iniciar el componente.
   */
  useEffect(() => {
    const fetchData = async () => {
      try {
        const currentUser = authService.getCurrentUser();
        setUser(currentUser);

        // Obtener datos en paralelo
        const [produccionesResponse, productosResponse, fincasResponse] = await Promise.all([
          fetch("http://localhost:8080/produccion", {
            headers: {
              'Authorization': `Bearer ${localStorage.getItem('authToken')}`
            }
          }),
          fetch("http://localhost:8080/api/productos"),
          fetch("http://localhost:8080/api/fincas")
        ]);

        // Verificar respuestas
        if (!produccionesResponse.ok) {
          const errorData = await produccionesResponse.json().catch(() => ({}));
          throw new Error(
              errorData.message || `Error ${produccionesResponse.status}: ${produccionesResponse.statusText}`
          );
        }

        // Procesar datos
        const produccionesData = await produccionesResponse.json();
        setProducciones(produccionesData);

        // Procesar productos y fincas si las respuestas son correctas
        if (productosResponse.ok) {
          const productosData = await productosResponse.json();
          setProductos(productosData);
        }

        if (fincasResponse.ok) {
          const fincasData = await fincasResponse.json();
          setFincas(fincasData);

          // Encontrar la finca asignada al usuario actual
          if (currentUser && fincasData.length > 0) {
            // Aquí deberías tener lógica para determinar cuál es la finca del usuario
            // Por ahora asumimos que hay algún campo que relaciona al usuario con su finca
            // o que simplemente tomamos la primera finca disponible como ejemplo
            const fincaDelUsuario = fincasData.find(finca => finca.idUsuario === currentUser.id) || fincasData[0];
            setFincaUsuario(fincaDelUsuario);
          }
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

  /** Alterna visibilidad del menú lateral */
  const toggleMenu = () => setIsOpen(!isOpen);

  /** Alterna visibilidad del dropdown del usuario */
  const toggleDropdown = () => setShowDropdown(!showDropdown);

  /** Cierra sesión del usuario actual */
  const handleLogout = () => authService.logout();

  /** Redirige a la página de registro de producción */
  const irARegistrarProduccion = () => {
    navigate("/registrar-produccion");
  };

  /** Formatea la fecha para mostrarla correctamente */
  const formatDate = (dateString) => {
    if (!dateString) return "-";
    const date = new Date(dateString);
    return date.toLocaleDateString('es-ES');
  };

  /** Formatea la fecha para input tipo date */
  const formatDateForInput = (dateString) => {
    if (!dateString) return "";
    const date = new Date(dateString);
    return date.toISOString().split('T')[0];
  };

  /**
   * Maneja la eliminación de una producción
   * @param {number} idProduccion - ID de la producción a eliminar
   */
  const handleEliminarProduccion = async (idProduccion) => {
    try {
      const response = await fetch(`http://localhost:8080/produccion/${idProduccion}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('authToken')}`
        }
      });

      if (!response.ok) throw new Error("Error al eliminar producción");

      // Actualizar el estado eliminando la producción
      setProducciones(producciones.filter(p => p.idProduccion !== idProduccion));
    } catch (error) {
      console.error("Error:", error.message);
      setError(error.message);
    }
  };

  /**
   * Abre el modal para actualizar una producción
   * @param {object} produccion - Datos de la producción a actualizar
   */
  const abrirModalActualizar = (produccion) => {
    setProduccionEditando(produccion);
    setFormData({
      idProducto: produccion.idProducto || '',
      idFinca: produccion.idFinca || (fincaUsuario ? fincaUsuario.idFinca : ''),
      fechaSiembra: formatDateForInput(produccion.fechaSiembra),
      fechaCosecha: formatDateForInput(produccion.fechaCosecha),
      estado: produccion.estado || '',
      cantidadCosechada: produccion.cantidadCosechada || ''
    });
    setShowModal(true);
  };

  /** Cierra el modal de actualización */
  const cerrarModal = () => {
    setShowModal(false);
    setProduccionEditando(null);
  };

  /**
   * Maneja el cambio en los campos del formulario
   * @param {Event} e - Evento de cambio del input
   */
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value
    });
  };

  /**
   * Maneja el envío del formulario de actualización
   * @param {Event} e - Evento de envío del formulario
   */
  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      // Si la producción tiene estado "COSECHADO", usar el endpoint de cosechar
      if (formData.estado === "COSECHADO") {
        const response = await fetch(
            `http://localhost:8080/produccion/${produccionEditando.idProduccion}/cosechar?cantidadCosechada=${formData.cantidadCosechada}&fechaCosecha=${formData.fechaCosecha}`,
            {
              method: 'PUT',
              headers: {
                'Authorization': `Bearer ${localStorage.getItem('authToken')}`
              }
            }
        );

        if (!response.ok) {
          const errorText = await response.text();
          throw new Error(`Error al cosechar producción: ${errorText}`);
        }
      }

      // En cualquier caso, actualizamos la producción con todos los datos
      const produccionData = {
        idProducto: parseInt(formData.idProducto),
        idFinca: fincaUsuario ? fincaUsuario.idFinca : parseInt(formData.idFinca), // Usamos la finca del usuario
        fechaSiembra: formData.fechaSiembra,
        fechaCosecha: formData.fechaCosecha,
        estado: formData.estado,
        cantidadCosechada: formData.cantidadCosechada ? parseFloat(formData.cantidadCosechada) : null
      };

      console.log("Enviando datos:", produccionData);

      // Recuperar la producción actualizada
      const getResponse = await fetch(`http://localhost:8080/produccion/${produccionEditando.idProduccion}`, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('authToken')}`
        }
      });

      if (!getResponse.ok) {
        throw new Error("Error al obtener datos actualizados");
      }

      const updatedProduccion = await getResponse.json();

      // Actualizar la lista de producciones
      setProducciones(producciones.map(p =>
          p.idProduccion === produccionEditando.idProduccion ? updatedProduccion : p
      ));

      cerrarModal();
    } catch (error) {
      console.error("Error:", error.message);
      setError(error.message);
    }
  };

  /**
   * Obtiene el nombre del producto según su ID
   * @param {number} id - ID del producto
   * @returns {string} Nombre del producto o el ID si no se encuentra
   */
  const getNombreProducto = (id) => {
    const producto = productos.find(p => p.idProducto === id);
    return producto ? producto.nombre : id;
  };

  /**
   * Obtiene el nombre de la finca según su ID
   * @param {number} id - ID de la finca
   * @returns {string} Nombre de la finca o el ID si no se encuentra
   */
  const getNombreFinca = (id) => {
    const finca = fincas.find(f => f.idFinca === id);
    return finca ? finca.nombre : id;
  };

  return (
      <div className="main-container">
        {/* Barra superior */}
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
            <div className="produccion-container">
              <div className="produccion-header">
                <h2 className="produccion-title">Producción Agrícola</h2>
                <button className="btn-registrar" onClick={irARegistrarProduccion}>
                  Registrar Producción
                </button>
              </div>

              {/* Mensajes de estado */}
              {loading && <div className="loading-message">Cargando producciones...</div>}
              {error && <div className="error-message">{error}</div>}

              <table className="produccion-table">
                <thead>
                <tr>
                  <th>Producto</th>
                  <th>Finca</th>
                  <th>Fecha Siembra</th>
                  <th>Fecha Cosecha</th>
                  <th>Estado</th>
                  <th>Cantidad Cosechada</th>
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
                      <td className="actions-cell">
                        <button
                            className="btn-actualizar"
                            onClick={() => abrirModalActualizar(produccion)}
                        >
                          <FaEdit /> Actualizar
                        </button>
                        <button
                            className="btn-eliminar"
                            onClick={() => handleEliminarProduccion(produccion.idProduccion)}
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
                <h3>Actualizar Producción</h3>
                <form onSubmit={handleSubmit}>
                  <div className="form-group">
                    <label>Producto:</label>
                    <select
                        name="idProducto"
                        value={formData.idProducto}
                        onChange={handleInputChange}
                        required
                    >
                      <option value="">Seleccione un producto</option>
                      {productos.map(producto => (
                          <option key={producto.idProducto} value={producto.idProducto}>
                            {producto.nombre}
                          </option>
                      ))}
                    </select>
                  </div>

                  {/* Eliminado el selector de finca, ahora se muestra el nombre de la finca del usuario */}
                  <div className="form-group">
                    <label>Finca:</label>
                    <div className="finca-asignada">
                      {fincaUsuario ? getNombreFinca(fincaUsuario.idFinca) : "Cargando finca..."}
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
                    />
                  </div>
                  <div className="form-group">
                    <label>Estado:</label>
                    <select
                        name="estado"
                        value={formData.estado}
                        onChange={handleInputChange}
                        required
                    >
                      <option value="">Seleccione un estado</option>
                      <option value="SEMBRADO">SEMBRADO</option>
                      <option value="EN_CRECIMIENTO">EN CRECIMIENTO</option>
                      <option value="COSECHADO">COSECHADO</option>
                    </select>
                  </div>
                  {/* Mostrar campos de cosecha si el estado es COSECHADO */}
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
                          />
                        </div>
                      </>
                  )}
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

export default Produccion;