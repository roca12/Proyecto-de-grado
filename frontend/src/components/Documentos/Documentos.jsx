import "./Documentos.css";
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
    FaUpload,
    FaEye,
    FaTrash,
    FaSpinner
} from "react-icons/fa";
import authService from "../authService";
import logo from "./../assets/APROAFA2.png";
import logoMini from "./../assets/APROAFA.jpg";
import watermarkImage from "./../assets/LogoBosque.png";

const Documentos = () => {
    const [isOpen, setIsOpen] = useState(true);
    const [showDropdown, setShowDropdown] = useState(false);
    const [documentos, setDocumentos] = useState([]);
    const [archivo, setArchivo] = useState(null);
    const [subiendo, setSubiendo] = useState(false);
    const [showHelp, setShowHelp] = useState(false);
    const [cargando, setCargando] = useState(false);

    const navigate = useNavigate();

    const toggleMenu = () => setIsOpen(!isOpen);
    const toggleDropdown = () => setShowDropdown(!showDropdown);
    const handleLogout = () => authService.logout();
    const toggleHelp = () => setShowHelp(!showHelp);

    const fetchDocumentos = async () => {
        // Lógica para obtener documentos
    };

    const handleUpload = async (e) => {
        // Lógica para subir archivos
    };

    const handleDelete = async (fileKey) => {
        // Lógica para eliminar archivos
    };

    useEffect(() => {
        fetchDocumentos();
    }, []);

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
                        <button className="active">
                            <FaFile /> {isOpen && "Documentos"}
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
                    <div className="documentos-container">
                        <div className="documentos-header">
                            <h2 className="documentos-title">Gestión de Documentos</h2>
                            <div style={{ display: "flex", alignItems: "center", position: "relative" }}>
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
                                        <h4>Ayuda</h4>
                                        <ul>
                                            <li><strong>Subir:</strong> Selecciona un archivo (max 50MB) para subir a AWS S3</li>
                                            <li><strong>Ver:</strong> Abre el documento en una nueva pestaña</li>
                                            <li><strong>Eliminar:</strong> Borra permanentemente el archivo de AWS S3</li>
                                        </ul>
                                    </div>
                                )}
                            </div>
                        </div>

                        {/* Formulario de subida */}
                        <form className="upload-form" onSubmit={handleUpload}>
                            <input
                                type="file"
                                onChange={(e) => setArchivo(e.target.files[0])}
                            />
                            <button type="submit" disabled={subiendo}>
                                {subiendo ? (
                                    <>
                                        <FaSpinner className="spin" /> Subiendo...
                                    </>
                                ) : (
                                    <>
                                        <FaUpload /> Subir Documento
                                    </>
                                )}
                            </button>
                        </form>

                        {/* Tabla de documentos */}
                        <table className="documentos-table">
                            <thead>
                            <tr>
                                <th>Nombre</th>
                                <th>Tamaño</th>
                                <th>Última modificación</th>
                                <th>Acciones</th>
                            </tr>
                            </thead>
                            <tbody>
                            {cargando ? (
                                <tr>
                                    <td colSpan="4" style={{ textAlign: 'center' }}>
                                        <FaSpinner className="spin" /> Cargando documentos...
                                    </td>
                                </tr>
                            ) : documentos.length > 0 ? (
                                documentos.map((doc, idx) => (
                                    <tr key={idx}>
                                        <td>{doc.name}</td>
                                        <td>{new Date(doc.lastModified).toLocaleString()}</td>
                                        <td style={{ display: "flex", gap: "8px", justifyContent: "center" }}>
                                            <a
                                                href={doc.url}
                                                target="_blank"
                                                rel="noopener noreferrer"
                                                className="btn-ver"
                                            >
                                                <FaEye /> Ver
                                            </a>
                                            <button
                                                className="btn-eliminar"
                                                onClick={() => handleDelete(doc.key)}
                                            >
                                                <FaTrash /> Eliminar
                                            </button>
                                        </td>
                                    </tr>
                                ))
                            ) : (
                                <tr>
                                    <td colSpan="4">No hay documentos disponibles</td>
                                </tr>
                            )}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            <div className="watermark">
                <img src={watermarkImage} alt="Marca de agua" />
            </div>
        </div>
    );
};

export default Documentos;