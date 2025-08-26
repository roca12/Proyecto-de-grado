import "./Cultivos.css";
import { useState, useEffect, useMemo } from "react";
import { Calendar, dateFnsLocalizer } from "react-big-calendar";
import { format, parse, startOfWeek, getDay } from "date-fns";
import esES from "date-fns/locale/es";
import "react-big-calendar/lib/css/react-big-calendar.css";
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
} from "react-icons/fa";
import { useNavigate } from "react-router-dom";
import authService from "../authService";
import logo from "./../assets/APROAFA2.png";
import logoMini from "./../assets/APROAFA.jpg";
import watermarkImage from "./../assets/LogoBosque.png";

// Localización del calendario
const locales = { es: esES };
const localizer = dateFnsLocalizer({
  format,
  parse,
  startOfWeek,
  getDay,
  locales,
});

// Generar color pastel aleatorio
const generarColorAleatorio = () => {
  const hue = Math.floor(Math.random() * 360);
  return `hsl(${hue}, 70%, 80%)`;
};

const Cultivos = () => {
  const [isOpen, setIsOpen] = useState(true);
  const [showDropdown, setShowDropdown] = useState(false);
  const [user, setUser] = useState(null);
  const [actividades, setActividades] = useState([]);
  const [producciones, setProducciones] = useState([]);
  const [productos, setProductos] = useState([]);
  const [date, setDate] = useState(new Date());
  const [view, setView] = useState("month");

  const navigate = useNavigate();

  const toggleMenu = () => setIsOpen(!isOpen);
  const toggleDropdown = () => setShowDropdown(!showDropdown);
  const handleLogout = () => authService.logout();

  useEffect(() => {
    const fetchData = async () => {
      try {
        const currentUser = authService.getCurrentUser();
        setUser(currentUser);

        const actividadesRes = await fetch(
          `http://localhost:8080/actividades/finca/${currentUser.idFinca}`,
        );
        const actividadesData = await actividadesRes.json();
        setActividades(actividadesData);

        const produccionesRes = await fetch(
          "http://localhost:8080/produccion",
          {
            headers: {
              Authorization: `Bearer ${localStorage.getItem("authToken")}`,
            },
          },
        );
        const produccionesData = await produccionesRes.json();
        setProducciones(produccionesData);

        const productosRes = await fetch("http://localhost:8080/api/productos");
        const productosData = await productosRes.json();
        setProductos(productosData);
      } catch (error) {
        console.error("Error al cargar datos:", error.message);
      }
    };

    fetchData();
  }, []);

  const getNombreProducto = (id) => {
    const producto = productos.find((p) => p.idProducto === id);
    return producto ? producto.nombre : `Producto ${id}`;
  };

  const eventosCalendario = useMemo(() => {
    const coloresPorProducto = {};
    const eventos = [];

    actividades.forEach((actividad) => {
      eventos.push({
        title: `Actividad: ${actividad.descripcion}`,
        start: new Date(actividad.fechaInicio + "T08:00:00"),
        end: new Date(actividad.fechaInicio + "T09:00:00"),
        allDay: false,
        color: "#4A90E2",
      });
    });

    producciones.forEach((produccion) => {
      const idProducto = produccion.idProducto;
      if (!coloresPorProducto[idProducto]) {
        coloresPorProducto[idProducto] = generarColorAleatorio();
      }

      eventos.push({
        title: `Siembra: ${getNombreProducto(idProducto)}`,
        start: new Date(produccion.fechaSiembra + "T10:00:00"),
        end: new Date(produccion.fechaSiembra + "T11:00:00"),
        allDay: false,
        color: coloresPorProducto[idProducto],
      });

      if (produccion.fechaCosecha) {
        eventos.push({
          title: `Cosecha: ${getNombreProducto(idProducto)}`,
          start: new Date(produccion.fechaCosecha + "T14:00:00"),
          end: new Date(produccion.fechaCosecha + "T15:00:00"),
          allDay: false,
          color: coloresPorProducto[idProducto],
        });
      }
    });

    return eventos;
  }, [actividades, producciones, productos]);

  const eventStyleGetter = (event) => {
    return {
      style: {
        backgroundColor: event.color,
        borderRadius: "5px",
        opacity: 0.9,
        color: "black",
        border: "0px",
        display: "block",
      },
    };
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
            <button onClick={() => navigate("/reportes-finca")}>
              <FaChartArea /> {isOpen && "Reportes"}
            </button>
            <button>
              <FaTable /> {isOpen && "Cultivos"}
            </button>
          </div>

          <img src={logoMini} alt="Logo inferior" className="footer-img" />
        </div>

        <div className="main-content">
          <div className="cultivos-container">
            <div className="cultivos-header">
              <h2 className="cultivos-title">Calendario de Cultivos</h2>
            </div>
            <div className="calendario-wrapper">
              <Calendar
                localizer={localizer}
                events={eventosCalendario}
                startAccessor="start"
                endAccessor="end"
                style={{ height: "75vh" }}
                eventPropGetter={eventStyleGetter}
                messages={{
                  next: "Siguiente",
                  previous: "Anterior",
                  today: "Hoy",
                  month: "Mes",
                  week: "Semana",
                  day: "Día",
                  agenda: "Agenda",
                }}
                date={date}
                onNavigate={(newDate) => setDate(newDate)}
                view={view}
                onView={(newView) => setView(newView)}
              />
            </div>
          </div>
        </div>
      </div>

      <div className="watermark">
        <img src={watermarkImage} alt="Marca de agua" />
      </div>
    </div>
  );
};

export default Cultivos;
