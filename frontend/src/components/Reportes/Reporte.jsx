import "./Reporte.css";
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
  FaChartLine,
  FaShoppingCart,
  FaLeaf,
  FaSeedling,
  FaMoneyBillWave,
  FaHandshake,
  FaExclamationTriangle,
} from "react-icons/fa";
import authService from "../authService";
import logo from "./../assets/APROAFA2.png";
import logoMini from "./../assets/APROAFA.jpg";
import watermarkImage from "../assets/LogoBosque.png";

import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell,
  BarChart,
  Bar,
} from "recharts";

const COLORS = [
  "#0088FE",
  "#00C49F",
  "#FFBB28",
  "#FF8042",
  "#8884D8",
  "#82CA9D",
];
const Reporte = () => {
  const [isOpen, setIsOpen] = useState(true);
  const [showDropdown, setShowDropdown] = useState(false);
  const [user, setUser] = useState(null);
  const [showHelp, setShowHelp] = useState(false);
  const [clientes, setClientes] = useState([]);
  const [vistaSeleccionada, setVistaSeleccionada] = useState("simple");
  const [reportData, setReportData] = useState({
    compras: [],
    produccion: [],
    actividades: [],
    ventas: [],
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [periodoVentas, setPeriodoVentas] = useState("3meses");
  const [periodoCompras, setPeriodoCompras] = useState("3meses");
  const [periodoProduccion, setPeriodoProduccion] = useState("3meses");
  const [periodoActividades, setPeriodoActividades] = useState("3meses");
  const navigate = useNavigate();

  const toggleMenu = () => setIsOpen(!isOpen);
  const toggleDropdown = () => setShowDropdown(!showDropdown);
  const handleLogout = () => authService.logout();
  const toggleHelp = () => setShowHelp(!showHelp);

  // Cargar datos iniciales
  useEffect(() => {
    const fetchData = async () => {
      try {
        const currentUser = authService.getCurrentUser();
        setUser(currentUser);
        if (!currentUser?.idFinca) return;

        const idFinca = currentUser.idFinca;

        const [
          produccionRes,
          actividadesRes,
          comprasRes,
          ventasRes,
          insumosRes,
          clientesRes,
          proveedoresRes, // NUEVO
        ] = await Promise.all([
          authService.authFetch(
            `http://localhost:8080/produccion/finca/${idFinca}`,
          ),
          authService.authFetch(
            `http://localhost:8080/actividades/finca/${idFinca}`,
          ),
          fetch("http://localhost:8080/api/compra-insumos", {
            headers: {
              Authorization: `Bearer ${localStorage.getItem("authToken")}`,
            },
          }),
          authService.authFetch(
            `http://localhost:8080/api/ventas/finca/${idFinca}`,
          ),
          fetch(`http://localhost:8080/insumos/finca/${idFinca}`),
          authService.authFetch(
            `http://localhost:8080/api/clientes/finca/${idFinca}`,
          ),
          authService.authFetch(
            `http://localhost:8080/api/proveedores/finca/${idFinca}`,
          ), // NUEVA PETICIÓN
        ]);

        // PRIMERO obtener todos los datos
        const produccion = produccionRes.ok ? await produccionRes.json() : [];
        const actividades = actividadesRes.ok
          ? await actividadesRes.json()
          : [];
        const compras = comprasRes.ok ? await comprasRes.json() : [];
        const ventas = ventasRes.ok ? await ventasRes.json() : [];
        const insumos = insumosRes.ok ? await insumosRes.json() : [];
        const clientes = clientesRes.ok ? await clientesRes.json() : [];
        const proveedores = proveedoresRes.ok
          ? await proveedoresRes.json()
          : []; // NUEVO

        // DESPUÉS filtrar compras por proveedores de esta finca
        const comprasFiltradas = compras.filter((compra) =>
          proveedores.some(
            (proveedor) =>
              proveedor.idProveedor === compra.proveedor?.idProveedor,
          ),
        );

        setReportData({
          produccion,
          actividades,
          compras: comprasFiltradas, // Usar compras filtradas
          ventas,
          insumos,
          clientes,
          proveedores,
        });
        setLoading(false);
      } catch (err) {
        console.error("Error al cargar datos:", err);
        setError("Error al cargar información");
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  // Cálculos con los datos traídos
  const totalCompras = reportData.compras.reduce(
    (sum, c) => sum + (c.precioUnitario * c.cantidad || 0),
    0,
  );
  const comprasCount = reportData.compras.length;

  const totalProduccion = reportData.produccion.reduce(
    (sum, p) => sum + (p.cantidadCosechada || 0),
    0,
  );
  const produccionCount = reportData.produccion.length;

  const totalActividades = reportData.actividades.length;

  const totalVentas = reportData.ventas.reduce(
    (sum, v) => sum + (v.total || 0),
    0,
  );
  const ventasCount = reportData.ventas.length;
  const gananciaEstimada = totalVentas - totalCompras;

  // Calcular proveedor más usado
  const proveedorFrecuente = (() => {
    const contador = {};
    reportData.compras.forEach((compra) => {
      const nombre = compra.proveedor?.nombre || "Desconocido";
      contador[nombre] = (contador[nombre] || 0) + 1;
    });
    let maxProveedor = "No hay datos";
    let maxCompras = 0;
    for (const nombre in contador) {
      if (contador[nombre] > maxCompras) {
        maxProveedor = nombre;
        maxCompras = contador[nombre];
      }
    }
    return maxProveedor;
  })();

  // Calcular cliente más frecuente
  const clienteFrecuente = (() => {
    // Verificar si hay datos de ventas
    if (!reportData.ventas || reportData.ventas.length === 0) {
      return "No hay datos";
    }

    const contador = {};
    reportData.ventas.forEach((venta) => {
      const idCliente = venta.idCliente;
      if (idCliente) {
        // Buscar el cliente en los datos de ventas (similar a como se hace en Ventas.jsx)
        const cliente = reportData.clientes?.find(
          (c) => c.idCliente === idCliente,
        );
        const nombreCliente = cliente
          ? `${cliente.nombre} ${cliente.apellido}`
          : `Cliente ID ${idCliente}`;

        contador[nombreCliente] = (contador[nombreCliente] || 0) + 1;
      }
    });

    let maxCliente = "No hay datos";
    let maxVentas = 0;

    for (const nombre in contador) {
      if (contador[nombre] > maxVentas) {
        maxCliente = nombre;
        maxVentas = contador[nombre];
      }
    }

    return maxCliente;
  })();

  const obtenerNombreCliente = (idCliente) => {
    const cliente = clientes.find((c) => c.idCliente === idCliente);
    return cliente
      ? `${cliente.nombre} ${cliente.apellido}`
      : `ID ${idCliente}`;
  };

  const calcularInsumosExtremos = () => {
    if (!reportData.insumos || reportData.insumos.length === 0) {
      return {
        mayorStock: { nombre: "No hay datos", cantidad: 0 },
        menorStock: { nombre: "No hay datos", cantidad: 0 },
      };
    }

    let mayorStock = reportData.insumos[0];
    let menorStock = reportData.insumos[0];

    reportData.insumos.forEach((insumo) => {
      if (insumo.cantidadDisponible > mayorStock.cantidadDisponible) {
        mayorStock = insumo;
      }
      if (insumo.cantidadDisponible < menorStock.cantidadDisponible) {
        menorStock = insumo;
      }
    });

    return {
      mayorStock: {
        nombre: mayorStock.nombre,
        cantidad: mayorStock.cantidadDisponible,
      },
      menorStock: {
        nombre: menorStock.nombre,
        cantidad: menorStock.cantidadDisponible,
      },
    };
  };

  const { mayorStock, menorStock } = calcularInsumosExtremos();

  // Función para procesar datos de ventas por período
  const procesarDatosVentas = (periodo) => {
    const ahora = new Date();
    const resultado = [];
    const periodos = periodo === "3meses" ? 3 : 30;

    for (let i = periodos - 1; i >= 0; i--) {
      const fecha = new Date(ahora);
      if (periodo === "3meses") {
        fecha.setMonth(fecha.getMonth() - i);
        const mesNombre = fecha.toLocaleDateString("es-ES", {
          month: "long",
          year: "numeric",
        });

        const ventasDelMes = reportData.ventas
          .filter((venta) => {
            if (!venta.fechaVenta) return false;
            const fechaVenta = new Date(venta.fechaVenta);
            return (
              fechaVenta.getMonth() === fecha.getMonth() &&
              fechaVenta.getFullYear() === fecha.getFullYear()
            );
          })
          .reduce((sum, venta) => sum + (venta.total || 0), 0);

        resultado.push({
          periodo: mesNombre.charAt(0).toUpperCase() + mesNombre.slice(1),
          valor: ventasDelMes,
        });
      } else if (periodo === "30dias") {
        fecha.setDate(fecha.getDate() - i);
        const fechaString = fecha.toLocaleDateString("es-ES", {
          day: "2-digit",
          month: "2-digit",
        });

        const ventasDelDia = reportData.ventas
          .filter((venta) => {
            if (!venta.fechaVenta) return false;
            const fechaVenta = new Date(venta.fechaVenta);
            return fechaVenta.toDateString() === fecha.toDateString();
          })
          .reduce((sum, venta) => sum + (venta.total || 0), 0);

        resultado.push({
          periodo: fechaString,
          valor: ventasDelDia,
        });
      }
    }

    return resultado;
  };

  const procesarDatosCompras = (periodo) => {
    const ahora = new Date();
    const resultado = [];
    const periodos = periodo === "3meses" ? 3 : 30;

    for (let i = periodos - 1; i >= 0; i--) {
      const fecha = new Date(ahora);
      if (periodo === "3meses") {
        fecha.setMonth(fecha.getMonth() - i);
        const mesNombre = fecha.toLocaleDateString("es-ES", {
          month: "long",
          year: "numeric",
        });

        const comprasDelMes = reportData.compras
          .filter((compra) => {
            if (!compra.fechaCompra) return false;
            const fechaCompra = new Date(compra.fechaCompra);
            return (
              fechaCompra.getMonth() === fecha.getMonth() &&
              fechaCompra.getFullYear() === fecha.getFullYear()
            );
          })
          .reduce(
            (sum, compra) =>
              sum + (compra.precioUnitario || 0) * (compra.cantidad || 1),
            0,
          );

        resultado.push({
          periodo: mesNombre.charAt(0).toUpperCase() + mesNombre.slice(1),
          valor: comprasDelMes,
        });
      } else {
        fecha.setDate(fecha.getDate() - i);
        const fechaString = fecha.toLocaleDateString("es-ES", {
          day: "2-digit",
          month: "2-digit",
        });

        const comprasDelDia = reportData.compras
          .filter((compra) => {
            if (!compra.fechaCompra) return false;
            const fechaCompra = new Date(compra.fechaCompra);
            return fechaCompra.toDateString() === fecha.toDateString();
          })
          .reduce(
            (sum, compra) =>
              sum + (compra.precioUnitario || 0) * (compra.cantidad || 1),
            0,
          );

        resultado.push({
          periodo: fechaString,
          valor: comprasDelDia,
        });
      }
    }
    return resultado;
  };

  const procesarDatosProduccion = (periodo) => {
    const ahora = new Date();
    const resultado = [];
    const periodos = periodo === "3meses" ? 3 : 30;

    for (let i = periodos - 1; i >= 0; i--) {
      const fecha = new Date(ahora);
      if (periodo === "3meses") {
        fecha.setMonth(fecha.getMonth() - i);
        const mesNombre = fecha.toLocaleDateString("es-ES", {
          month: "long",
          year: "numeric",
        });

        const produccionDelMes = reportData.produccion
          .filter((prod) => {
            // Usar fechaCosecha si está disponible, sino fechaSiembra
            const fechaRef = prod.fechaCosecha || prod.fechaSiembra;
            if (!fechaRef) return false;
            const fechaProd = new Date(fechaRef);
            return (
              fechaProd.getMonth() === fecha.getMonth() &&
              fechaProd.getFullYear() === fecha.getFullYear()
            );
          })
          .reduce((sum, prod) => sum + (prod.cantidadCosechada || 0), 0);

        resultado.push({
          periodo: mesNombre.charAt(0).toUpperCase() + mesNombre.slice(1),
          valor: produccionDelMes,
        });
      } else {
        fecha.setDate(fecha.getDate() - i);
        const fechaString = fecha.toLocaleDateString("es-ES", {
          day: "2-digit",
          month: "2-digit",
        });

        const produccionDelDia = reportData.produccion
          .filter((prod) => {
            const fechaRef = prod.fechaCosecha || prod.fechaSiembra;
            if (!fechaRef) return false;
            const fechaProd = new Date(fechaRef);
            return fechaProd.toDateString() === fecha.toDateString();
          })
          .reduce((sum, prod) => sum + (prod.cantidadCosechada || 0), 0);

        resultado.push({
          periodo: fechaString,
          valor: produccionDelDia,
        });
      }
    }
    return resultado;
  };

  const procesarDatosActividades = (periodo) => {
    const ahora = new Date();
    const resultado = [];
    const periodos = periodo === "3meses" ? 3 : 30;

    for (let i = periodos - 1; i >= 0; i--) {
      const fecha = new Date(ahora);
      if (periodo === "3meses") {
        fecha.setMonth(fecha.getMonth() - i);
        const mesNombre = fecha.toLocaleDateString("es-ES", {
          month: "long",
          year: "numeric",
        });

        const actividadesDelMes = reportData.actividades.filter((actividad) => {
          if (!actividad.fechaInicio) return false;
          const fechaAct = new Date(actividad.fechaInicio);
          return (
            fechaAct.getMonth() === fecha.getMonth() &&
            fechaAct.getFullYear() === fecha.getFullYear()
          );
        }).length;

        resultado.push({
          periodo: mesNombre.charAt(0).toUpperCase() + mesNombre.slice(1),
          valor: actividadesDelMes,
        });
      } else {
        fecha.setDate(fecha.getDate() - i);
        const fechaString = fecha.toLocaleDateString("es-ES", {
          day: "2-digit",
          month: "2-digit",
        });

        const actividadesDelDia = reportData.actividades.filter((actividad) => {
          if (!actividad.fechaInicio) return false;
          const fechaAct = new Date(actividad.fechaInicio);
          return fechaAct.toDateString() === fecha.toDateString();
        }).length;

        resultado.push({
          periodo: fechaString,
          valor: actividadesDelDia,
        });
      }
    }
    return resultado;
  };

  const procesarDatosProveedores = () => {
    const contador = {};
    reportData.compras.forEach((compra) => {
      // Buscar el proveedor en los proveedores de la finca
      const proveedor = reportData.proveedores?.find(
        (p) => p.idProveedor === compra.proveedor?.idProveedor,
      );
      const nombre = proveedor?.nombre || "Sin proveedor";
      contador[nombre] = (contador[nombre] || 0) + 1;
    });

    return Object.entries(contador)
      .map(([nombre, cantidad]) => ({ nombre, cantidad }))
      .sort((a, b) => b.cantidad - a.cantidad)
      .slice(0, 6);
  };

  const procesarDatosClientes = () => {
    const contador = {};
    reportData.ventas.forEach((venta) => {
      const idCliente = venta.idCliente;
      if (idCliente) {
        const cliente = reportData.clientes?.find(
          (c) => c.idCliente === idCliente,
        );
        const nombreCliente = cliente
          ? `${cliente.nombre} ${cliente.apellido}`
          : `Cliente ${idCliente}`;
        contador[nombreCliente] = (contador[nombreCliente] || 0) + 1;
      }
    });

    return Object.entries(contador)
      .map(([nombre, cantidad]) => ({ nombre, cantidad }))
      .sort((a, b) => b.cantidad - a.cantidad)
      .slice(0, 6);
  };

  const procesarDatosInsumos = () => {
    return reportData.insumos
      .map((insumo) => ({
        nombre: insumo.nombre,
        cantidad: insumo.cantidadDisponible,
      }))
      .sort((a, b) => b.cantidad - a.cantidad)
      .slice(0, 8);
  };

  return (
    <div className="main-container">
      {/* Topbar */}
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
        {/* Sidebar */}
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
            {/*<button onClick={() => navigate("/documentos")}>
                <FaFile /> {isOpen && "Documentos"}
              </button>*/}
            <button>
              <FaChartArea /> {isOpen && "Reportes"}
            </button>
            <button onClick={() => navigate("/cultivo")}>
              <FaTable /> {isOpen && "Cultivos"}
            </button>
          </div>

          <img src={logoMini} alt="Logo inferior" className="footer-img" />
        </div>

        <div className="main-content">
          <div className="reportes-container">
            <div className="reportes-header">
              <h2 className="reportes-title">Reportes de Finca</h2>
              <div
                style={{ display: "flex", alignItems: "center", gap: "10px" }}
              >
                <button
                  className="btn-registrar"
                  onClick={() => setVistaSeleccionada("simple")}
                >
                  Vista Simple
                </button>
                <button
                  className="btn-registrar"
                  onClick={() => setVistaSeleccionada("avanzada")}
                >
                  Vista Avanzada
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
                  <div className="help-tooltip">
                    <h4>Ayuda - Explicación de Métricas</h4>
                    <ul>
                      <li>
                        <strong>Total Ventas:</strong> Suma del valor monetario
                        de todas las ventas registradas. Muestra cuánto ha
                        ingresado la finca por concepto de ventas.
                      </li>
                      <li>
                        <strong>Total Compras:</strong> Suma del valor monetario
                        de todas las compras de insumos. Representa cuánto ha
                        gastado la finca en insumos.
                      </li>
                      <li>
                        <strong>Producción Total:</strong> Cantidad total
                        cosechada (en kg) de todos los cultivos en estado
                        "COSECHADO". Indica la productividad de la finca.
                      </li>
                      <li>
                        <strong>Actividades:</strong> Número total de
                        actividades agrícolas registradas. Muestra la cantidad
                        de trabajo realizado en la finca.
                      </li>
                      <li>
                        <strong>Ganancia Estimada:</strong> Diferencia entre
                        ventas y compras (Ventas - Compras).
                        <span style={{ color: "#4CAF50" }}> Verde</span> indica
                        ganancias,
                        <span style={{ color: "#F44336" }}> rojo</span> indica
                        pérdidas.
                      </li>
                      <li>
                        <strong>Proveedor más usado:</strong> Proveedor del cual
                        se han realizado más compras. Ayuda a identificar las
                        relaciones comerciales más frecuentes.
                      </li>
                      <li>
                        <strong>Cliente más frecuente:</strong> Cliente que ha
                        realizado más compras. Identifica a los compradores más
                        importantes.
                      </li>
                      <li>
                        <strong>Insumo con mayor stock:</strong> Insumo que
                        tiene la mayor cantidad disponible. Útil para gestionar
                        inventarios y evitar sobreabastecimiento.
                      </li>
                      <li>
                        <strong>Insumo con menor stock:</strong> Insumo con la
                        menor cantidad disponible.
                        <span style={{ color: "#FF9800" }}>
                          Amarillo/naranja
                        </span>{" "}
                        indica que puede necesitar reabastecimiento pronto.
                      </li>
                    </ul>
                    <div
                      style={{
                        marginTop: "15px",
                        padding: "10px",
                        backgroundColor: "#f8f9fa",
                        borderRadius: "5px",
                      }}
                    >
                      <strong>Tip:</strong> Cambia a la vista avanzada para ver
                      gráficos detallados y análisis temporales.
                    </div>
                  </div>
                )}
              </div>
            </div>

            {loading ? (
              <p className="loading-message">Cargando información...</p>
            ) : error ? (
              <p className="error-message">{error}</p>
            ) : vistaSeleccionada === "simple" ? (
              <div className="summary-cards">
                <div className="card">
                  <FaMoneyBillWave size={24} color="#4CAF50" />
                  <h3>Total Ventas</h3>
                  <p>${totalVentas.toLocaleString()}</p>
                  <small>{ventasCount} ventas realizadas</small>
                </div>
                <div className="card">
                  <FaShoppingCart size={24} color="#2196F3" />
                  <h3>Total Compras</h3>
                  <p>${totalCompras.toLocaleString()}</p>
                  <small>{comprasCount} compras realizadas</small>
                </div>
                <div className="card">
                  <FaLeaf size={24} color="#8BC34A" />
                  <h3>Producción Total</h3>
                  <p>{totalProduccion.toLocaleString()} kg</p>
                  <small>{produccionCount} lotes</small>
                </div>
                <div className="card">
                  <FaSeedling size={24} color="#FF9800" />
                  <h3>Actividades</h3>
                  <p>{totalActividades}</p>
                  <small>Actividades realizadas</small>
                </div>
                <div className="card">
                  <FaChartLine
                    size={24}
                    color={gananciaEstimada >= 0 ? "#4CAF50" : "#F44336"}
                  />
                  <h3>Ganancia Estimada</h3>
                  <p
                    style={{
                      color: gananciaEstimada >= 0 ? "#4CAF50" : "#F44336",
                    }}
                  >
                    ${gananciaEstimada.toLocaleString()}
                  </p>
                  <small>
                    {gananciaEstimada >= 0 ? "Utilidad" : "Pérdida"}
                  </small>
                </div>
                <div className="card">
                  <FaTruck size={24} color="#795548" />
                  <h3>Proveedor más usado</h3>
                  <p>{proveedorFrecuente}</p>
                  <small>Con más compras registradas</small>
                </div>
                <div className="card">
                  <FaHandshake size={24} color="#673AB7" />
                  <h3>Cliente más frecuente</h3>
                  <p>{clienteFrecuente}</p>
                  <small>Con más compras realizadas</small>
                </div>
                <div className="card">
                  <FaChartLine size={24} color="#4CAF50" />
                  <h3>Insumo con mayor stock</h3>
                  <p>{mayorStock.nombre}</p>
                  <small>{mayorStock.cantidad} unidades disponibles</small>
                </div>

                {/* Tarjeta para insumo con menor stock */}
                <div className="card">
                  <FaExclamationTriangle size={24} color="#FF9800" />
                  <h3>Insumo con menor stock</h3>
                  <p>{menorStock.nombre}</p>
                  <small>Cuidado: solo {menorStock.cantidad} unidades</small>
                </div>
              </div>
            ) : (
              <div className="vista-avanzada">
                {/* Gráfica de Ventas */}
                <div className="grafica-container">
                  <div className="grafica-header">
                    <h3 className="grafica-title">
                      <FaMoneyBillWave />
                      Evolución de Ventas -{" "}
                      {periodoVentas === "3meses"
                        ? "Últimos 3 meses"
                        : "Últimos 30 días"}
                    </h3>
                    <select
                      className="periodo-selector"
                      value={periodoVentas}
                      onChange={(e) => setPeriodoVentas(e.target.value)}
                    >
                      <option value="3meses">Últimos 3 meses</option>
                      <option value="30dias">Últimos 30 días</option>
                    </select>
                  </div>
                  <ResponsiveContainer width="100%" height={300}>
                    <LineChart data={procesarDatosVentas(periodoVentas)}>
                      <CartesianGrid strokeDasharray="3 3" />
                      <XAxis
                        dataKey="periodo"
                        tick={{ fontSize: 12 }}
                        angle={-45}
                        textAnchor="end"
                        height={60}
                      />
                      <YAxis tick={{ fontSize: 12 }} />
                      <Tooltip
                        formatter={(value) => [
                          `$${value.toLocaleString()}`,
                          "Ventas",
                        ]}
                      />
                      <Line
                        type="monotone"
                        dataKey="valor"
                        stroke="#4CAF50"
                        strokeWidth={3}
                        dot={{ fill: "#4CAF50", strokeWidth: 2, r: 5 }}
                      />
                    </LineChart>
                  </ResponsiveContainer>
                </div>
                {/* Gráfica de Compras */}
                <div className="grafica-container">
                  <div className="grafica-header">
                    <h3 className="grafica-title-compras">
                      <FaShoppingCart />
                      Evolución de Compras -{" "}
                      {periodoCompras === "3meses"
                        ? "Últimos 3 meses"
                        : "Últimos 30 días"}
                    </h3>
                    <select
                      className="periodo-selector"
                      value={periodoCompras}
                      onChange={(e) => setPeriodoCompras(e.target.value)}
                    >
                      <option value="3meses">Últimos 3 meses</option>
                      <option value="30dias">Últimos 30 días</option>
                    </select>
                  </div>
                  <ResponsiveContainer width="100%" height={300}>
                    <LineChart data={procesarDatosCompras(periodoCompras)}>
                      <CartesianGrid strokeDasharray="3 3" />
                      <XAxis
                        dataKey="periodo"
                        tick={{ fontSize: 12 }}
                        angle={-45}
                        textAnchor="end"
                        height={60}
                      />
                      <YAxis tick={{ fontSize: 12 }} />
                      <Tooltip
                        formatter={(value) => [
                          `$${value.toLocaleString()}`,
                          "Compras",
                        ]}
                      />
                      <Line
                        type="monotone"
                        dataKey="valor"
                        stroke="#2196F3"
                        strokeWidth={3}
                        dot={{ fill: "#2196F3", strokeWidth: 2, r: 5 }}
                      />
                    </LineChart>
                  </ResponsiveContainer>
                </div>
                {/* Gráfica de Producción */}
                <div className="grafica-container">
                  <div className="grafica-header">
                    <h3 className="grafica-title-produccion">
                      <FaLeaf />
                      Evolución de Producción -{" "}
                      {periodoProduccion === "3meses"
                        ? "Últimos 3 meses"
                        : "Últimos 30 días"}
                    </h3>
                    <select
                      className="periodo-selector"
                      value={periodoProduccion}
                      onChange={(e) => setPeriodoProduccion(e.target.value)}
                    >
                      <option value="3meses">Últimos 3 meses</option>
                      <option value="30dias">Últimos 30 días</option>
                    </select>
                  </div>
                  <ResponsiveContainer width="100%" height={300}>
                    <LineChart
                      data={procesarDatosProduccion(periodoProduccion)}
                    >
                      <CartesianGrid strokeDasharray="3 3" />
                      <XAxis
                        dataKey="periodo"
                        tick={{ fontSize: 12 }}
                        angle={-45}
                        textAnchor="end"
                        height={60}
                      />
                      <YAxis tick={{ fontSize: 12 }} />
                      <Tooltip
                        formatter={(value) => [`${value} kg`, "Producción"]}
                      />
                      <Line
                        type="monotone"
                        dataKey="valor"
                        stroke="#8BC34A"
                        strokeWidth={3}
                        dot={{ fill: "#8BC34A", strokeWidth: 2, r: 5 }}
                      />
                    </LineChart>
                  </ResponsiveContainer>
                </div>
                {/* Gráfica de Actividades */}
                <div className="grafica-container">
                  <div className="grafica-header">
                    <h3 className="grafica-title-actividades">
                      <FaSeedling />
                      Evolución de Actividades -{" "}
                      {periodoActividades === "3meses"
                        ? "Últimos 3 meses"
                        : "Últimos 30 días"}
                    </h3>
                    <select
                      className="periodo-selector"
                      value={periodoActividades}
                      onChange={(e) => setPeriodoActividades(e.target.value)}
                    >
                      <option value="3meses">Últimos 3 meses</option>
                      <option value="30dias">Últimos 30 días</option>
                    </select>
                  </div>
                  <ResponsiveContainer width="100%" height={300}>
                    <LineChart
                      data={procesarDatosActividades(periodoActividades)}
                    >
                      <CartesianGrid strokeDasharray="3 3" />
                      <XAxis
                        dataKey="periodo"
                        tick={{ fontSize: 12 }}
                        angle={-45}
                        textAnchor="end"
                        height={60}
                      />
                      <YAxis tick={{ fontSize: 12 }} />
                      <Tooltip
                        formatter={(value) => [`${value}`, "Actividades"]}
                      />
                      <Line
                        type="monotone"
                        dataKey="valor"
                        stroke="#FF9800"
                        strokeWidth={3}
                        dot={{ fill: "#FF9800", strokeWidth: 2, r: 5 }}
                      />
                    </LineChart>
                  </ResponsiveContainer>
                </div>
                {/* Gráficas de Pastel */}
                <div className="grafica-pastel">
                  {/* Proveedores */}
                  <div className="grafica-container">
                    <h3 className="grafica-title-proveedores">
                      <FaTruck />
                      Distribución de Proveedores
                    </h3>
                    <ResponsiveContainer width="100%" height={300}>
                      <PieChart>
                        <Pie
                          data={procesarDatosProveedores()}
                          cx="50%"
                          cy="50%"
                          outerRadius={80}
                          fill="#8884d8"
                          dataKey="cantidad"
                          label={({ nombre, percent }) =>
                            `${nombre}: ${(percent * 100).toFixed(1)}%`
                          }
                        >
                          {procesarDatosProveedores().map((entry, index) => (
                            <Cell
                              key={`cell-${index}`}
                              fill={COLORS[index % COLORS.length]}
                            />
                          ))}
                        </Pie>
                        <Tooltip
                          formatter={(value) => [
                            `${value} compras`,
                            "Cantidad",
                          ]}
                        />
                      </PieChart>
                    </ResponsiveContainer>
                  </div>
                  {/* Clientes */}
                  <div className="grafica-container">
                    <h3 className="grafica-title-clientes">
                      <FaHandshake />
                      Distribución de Clientes
                    </h3>
                    <ResponsiveContainer width="100%" height={300}>
                      <PieChart>
                        <Pie
                          data={procesarDatosClientes()}
                          cx="50%"
                          cy="50%"
                          outerRadius={80}
                          fill="#8884d8"
                          dataKey="cantidad"
                          label={({ nombre, percent }) =>
                            `${nombre}: ${(percent * 100).toFixed(1)}%`
                          }
                        >
                          {procesarDatosClientes().map((entry, index) => (
                            <Cell
                              key={`cell-${index}`}
                              fill={COLORS[index % COLORS.length]}
                            />
                          ))}
                        </Pie>
                        <Tooltip
                          formatter={(value) => [`${value} ventas`, "Cantidad"]}
                        />
                      </PieChart>
                    </ResponsiveContainer>
                  </div>
                  {/* Gráfica de Insumos */}
                  <div className="grafica-container">
                    <h3 className="grafica-title-insumos">
                      <FaExclamationTriangle />
                      Inventario de Insumos (Stock Disponible)
                    </h3>
                    <ResponsiveContainer width="100%" height={400}>
                      <BarChart
                        data={procesarDatosInsumos()}
                        margin={{ top: 20, right: 30, left: 20, bottom: 60 }}
                      >
                        <CartesianGrid strokeDasharray="3 3" />
                        <XAxis
                          dataKey="nombre"
                          tick={{ fontSize: 11 }}
                          angle={-45}
                          textAnchor="end"
                          height={80}
                        />
                        <YAxis tick={{ fontSize: 12 }} />
                        <Tooltip
                          formatter={(value) => [`${value} unidades`, "Stock"]}
                        />
                        <Bar dataKey="cantidad" fill="#FF9800">
                          {procesarDatosInsumos().map((entry, index) => (
                            <Cell
                              key={`cell-${index}`}
                              fill={
                                entry.cantidad < 10
                                  ? "#F44336"
                                  : entry.cantidad < 50
                                    ? "#FF9800"
                                    : "#4CAF50"
                              }
                            />
                          ))}
                        </Bar>
                      </BarChart>
                    </ResponsiveContainer>
                    <div className="stock-legend">
                      <div className="legend-item">
                        <div
                          className="legend-color"
                          style={{ backgroundColor: "#F44336" }}
                        ></div>
                        Stock crítico (&lt;10)
                      </div>
                      <div className="legend-item">
                        <div
                          className="legend-color"
                          style={{ backgroundColor: "#FF9800" }}
                        ></div>
                        Stock bajo (10-49)
                      </div>
                      <div className="legend-item">
                        <div
                          className="legend-color"
                          style={{ backgroundColor: "#4CAF50" }}
                        ></div>
                        Stock normal (≥50)
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            )}
            <div className="watermark">
              <img src={watermarkImage} alt="Marca de agua" />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Reporte;
