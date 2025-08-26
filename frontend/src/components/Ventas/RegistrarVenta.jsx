import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import authService from "../authService";
import logo from "./../assets/APROAFA2.png";
import watermarkImage from "./../assets/LogoBosque.png";
import { FaSignOutAlt, FaPlus, FaTrash, FaShoppingCart } from "react-icons/fa";
import "./RegistrarVenta.css";

const RegistrarVenta = () => {
    const [user, setUser] = useState(null);
    const [clientes, setClientes] = useState([]);
    const [producciones, setProducciones] = useState([]);
    const [showDropdown, setShowDropdown] = useState(false);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const [formData, setFormData] = useState({
        idCliente: "",
        metodoPago: "EFECTIVO", // Back to Spanish since that's what your UI shows
        idMetodoPago: 0,
        total: 0,
        idPersona: null
    });

    const [detalles, setDetalles] = useState([]);
    const [detalleForm, setDetalleForm] = useState({
        idProduccion: "",
        cantidad: "",
        precioUnitario: ""
    });

    const navigate = useNavigate();

    // Mapping function for payment methods - try multiple common values
    const mapPaymentMethod = (method) => {
        const mappings = {
            'EFECTIVO': 'Efectivo',
            'TARJETA': 'Tarjeta',
            'TRANSFERENCIA': 'Transferencia',
            'Efectivo': 'Efectivo',
            'Tarjeta': 'Tarjeta',
            'Transferencia': 'Transferencia'
        };
        return mappings[method] || 'Otro'; // Usar 'Otro' como valor por defecto
    };

    // Función para decodificar el token JWT
    const decodeJWT = (token) => {
        try {
            if (!token) return null;
            const base64Url = token.split('.')[1];
            const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
            return JSON.parse(window.atob(base64));
        } catch (e) {
            console.error("Error decodificando token:", e);
            return null;
        }
    };

    useEffect(() => {
        const currentUser = authService.getCurrentUser();
        if (!currentUser) {
            navigate("/");
            return;
        }

        console.log("Persona actual desde localStorage:", currentUser);

        // Obtenemos datos adicionales del token JWT
        const token = authService.getAuthToken();
        const tokenData = decodeJWT(token);
        console.log("Datos del token JWT:", tokenData);
        const idFinca = currentUser.idFinca;

        // Establecemos el persona en el estado
        setUser(currentUser);

        // Determinamos el ID del persona de todas las posibles fuentes
        const personaId = currentUser.idPersona ||
            currentUser.idPersona ||
            currentUser.id ||
            (tokenData && (tokenData.idPersona || tokenData.idPersona || tokenData.id));

        console.log("ID de persona determinado:", personaId);

        if (!personaId) {
            console.error("No se pudo obtener el ID del persona. Datos completos:", {
                currentUser,
                tokenData
            });
            setError("Error crítico: No se pudo identificar al persona. Contacte al administrador.");
            return;
        }

        // Actualizamos el formData con el ID del persona
        setFormData(prev => ({
            ...prev,
            idPersona: personaId
        }));
        // Actualizamos el formData con el ID del persona y el ID de la finca del usuario
          setFormData(prev => ({
                  ...prev,
                  idPersona: personaId,
              idFinca: currentUser.idFinca
          }));

        // Función para cargar datos necesarios
        const fetchData = async () => {
            try {
                setLoading(true);

                // Cargar clientes
                const clientesRes = await authService.authFetch(`http://localhost:8080/api/clientes/finca/${idFinca}`);
                if (!clientesRes.ok) throw new Error("Error al cargar clientes");
                const clientesData = await clientesRes.json();
                setClientes(clientesData);

                // Cargar producciones
                const produccionRes = await authService.authFetch(`http://localhost:8080/produccion/finca/${idFinca}`);
                if (!produccionRes.ok) throw new Error("Error al cargar producciones");
                const produccionData = await produccionRes.json();

                // Filtrar solo producciones cosechadas y disponibles
                const disponibles = produccionData.filter(
                    (p) => p.estado === "COSECHADO" && p.cantidadCosechada > 0
                );
                setProducciones(disponibles);

                setError(null);
            } catch (err) {
                console.error("Error en fetchData:", err);
                setError(err.message || "Error al cargar datos necesarios");
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, [navigate]);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const handleDetalleInputChange = (e) => {
        const { name, value } = e.target;
        setDetalleForm({ ...detalleForm, [name]: value });
    };

    const agregarDetalle = () => {
        const { idProduccion, cantidad, precioUnitario } = detalleForm;

        // Validación de campos obligatorios
        if (!idProduccion || !cantidad || !precioUnitario) {
            setError("Todos los campos del detalle son obligatorios");
            return;
        }

        // Buscamos la producción seleccionada
        const prod = producciones.find((p) => p.idProduccion === parseInt(idProduccion));
        if (!prod) {
            setError("Producción no encontrada");
            return;
        }

        // Validación de cantidad disponible
        if (parseFloat(cantidad) > parseFloat(prod.cantidadCosechada)) {
            setError(`La cantidad no puede superar lo cosechado (${prod.cantidadCosechada} kg)`);
            return;
        }

        // Validación de valores positivos
        if (parseFloat(cantidad) <= 0 || parseFloat(precioUnitario) <= 0) {
            setError("La cantidad y el precio deben ser valores positivos");
            return;
        }

        // Calculamos subtotal
        const subtotal = parseFloat(cantidad) * parseFloat(precioUnitario);

        // Agregamos el nuevo detalle
        setDetalles([...detalles, {
            idProduccion: parseInt(idProduccion),
            cantidad: parseFloat(cantidad),
            precioUnitario: parseFloat(precioUnitario),
            subtotal
        }]);

        // Actualizamos el total
        setFormData((prev) => ({
            ...prev,
            total: prev.total + subtotal
        }));

        // Reseteamos el formulario de detalle
        setDetalleForm({
            idProduccion: "",
            cantidad: "",
            precioUnitario: ""
        });

        setError(null);
    };

    const eliminarDetalle = (index) => {
        // Obtenemos el detalle a eliminar
        const eliminado = detalles[index];

        // Filtramos el array para quitar el detalle
        const nuevosDetalles = detalles.filter((_, i) => i !== index);
        setDetalles(nuevosDetalles);

        // Actualizamos el total restando el subtotal eliminado
        setFormData((prev) => ({
            ...prev,
            total: prev.total - eliminado.subtotal
        }));
    };

    const handleRegister = async () => {
        // Validación de campos obligatorios
        if (!formData.idCliente) {
            setError("Debes seleccionar un cliente");
            return;
        }

        if (!formData.metodoPago) {
            setError("Debes seleccionar un método de pago");
            return;
        }

        if (detalles.length === 0) {
            setError("Debes agregar al menos un detalle de venta");
            return;
        }

        // Validación crítica del ID del persona
        if (!formData.idPersona) {
            console.error("ID de persona no definido al intentar registrar venta");
            setError("Error crítico: No se pudo identificar al persona. Recargue la página o contacte al administrador.");
            return;
        }

        // Preparamos el payload para la API - FIXED: Match the exact structure expected by backend
        const ventaPayload = {
            venta: {
                idCliente: parseInt(formData.idCliente),
                idPersona: parseInt(formData.idPersona),
                metodoPago: mapPaymentMethod(formData.metodoPago),
                idMetodoPago: formData.idMetodoPago,
                total: parseFloat(formData.total),
                idFinca: user?.idFinca
            },
            detalles: detalles.map(d => ({
                idProduccion: d.idProduccion,
                cantidad: d.cantidad,
                precioUnitario: parseFloat(d.precioUnitario)
            }))
        };

        console.log("Payload enviado al servidor:", JSON.stringify(ventaPayload, null, 2));

        try {
            setLoading(true);
            setError(null);

            // First, test with a simple test endpoint
            console.log("Testing connectivity with test endpoint...");
            const testRes = await authService.authFetch("http://localhost:8080/api/ventas/test", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Accept": "application/json"
                },
                body: JSON.stringify(ventaPayload)
            });

            console.log("Test response status:", testRes.status);
            const testText = await testRes.text();
            console.log("Test response:", testText);

            if (!testRes.ok) {
                throw new Error(`Test endpoint failed: ${testRes.status} - ${testText}`);
            }

            // Now try the actual endpoint
            console.log("Test passed, attempting actual endpoint: http://localhost:8080/api/ventas/con-detalles");

            // Realizamos la petición usando authFetch para manejar autenticación
            const res = await authService.authFetch("http://localhost:8080/api/ventas/con-detalles", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Accept": "application/json"
                },
                body: JSON.stringify(ventaPayload)
            });

            console.log("Response status:", res.status);
            console.log("Response headers:", res.headers);

            // Log the response text for debugging
            const responseText = await res.text();
            console.log("Response text:", responseText);

            // Si la respuesta no es OK, manejamos el error
            if (!res.ok) {
                let errorData = {};
                try {
                    errorData = JSON.parse(responseText);
                    console.log("Parsed error data:", errorData);
                } catch (e) {
                    console.log("Could not parse error response as JSON");
                }

                const errorMessage = errorData.error ||
                    errorData.message ||
                    responseText ||
                    `Error ${res.status}: ${res.statusText}`;

                throw new Error(errorMessage);
            }

            // Parse successful response
            let ventaGuardada = {};
            try {
                ventaGuardada = JSON.parse(responseText);
            } catch (e) {
                console.log("Could not parse success response as JSON, but request succeeded");
            }

            console.log("Venta registrada exitosamente:", ventaGuardada);

            try {
                for (const det of detalles) {
                    // 1) Buscar la producción en el state
                    let prod = producciones.find(p => p.idProduccion === det.idProduccion);

                    // 1a) Si no está en memoria, traerla del backend
                    if (!prod) {
                        const prodRes = await authService.authFetch(`http://localhost:8080/produccion/${det.idProduccion}`);
                        if (!prodRes.ok) {
                            const errText = await prodRes.text().catch(() => prodRes.statusText);
                            console.error(`No se pudo obtener producción ${det.idProduccion}:`, errText);
                            continue;
                        }
                        prod = await prodRes.json();
                    }

                    // 2) Calcular nueva cantidad cosechada
                    const cantidadActual = parseFloat(prod.cantidadCosechada || 0);
                    const resta = parseFloat(det.cantidad || 0);
                    const nuevaCantidad = Math.max(0, cantidadActual - resta);

                    if (nuevaCantidad < 0.000001) {
                        console.warn(`Producción ${det.idProduccion}: la resta dejó 0 o negativo. Se pondrá en 0.`);
                    }

                    // 3) Usar la misma fecha de cosecha o la fecha actual si no existe
                    const fechaCosecha = prod.fechaCosecha
                        ? prod.fechaCosecha.split("T")[0]
                        : new Date().toISOString().split("T")[0];

                    // 4) Llamada al endpoint /cosechar
                    const updateRes = await authService.authFetch(
                        `http://localhost:8080/produccion/${prod.idProduccion}/cosechar?cantidadCosechada=${nuevaCantidad}&fechaCosecha=${fechaCosecha}`,
                        { method: "PUT" }
                    );

                    if (!updateRes.ok) {
                        const text = await updateRes.text().catch(() => updateRes.statusText);
                        console.error(`Error actualizando producción ${prod.idProduccion}:`, text);
                        continue;
                    }

                    console.log(`Stock actualizado para producción ${prod.idProduccion}: nueva cantidadCosechada = ${nuevaCantidad}`);
                }
            } catch (err) {
                console.error("Error al actualizar stocks de producción:", err);
            }

            // Si todo sale bien, redirigimos a la lista de ventas
            navigate("/ventas");
        } catch (e) {
            console.error("Error en handleRegister:", e);
            setError(e.message || "Ocurrió un error al registrar la venta");
        } finally {
            setLoading(false);
        }
    };

    const handleCancel = () => {
        navigate("/ventas");
    };

    const handleLogout = () => {
        authService.logout();
        navigate("/");
    };

    return (
        <div className="main-container">
            <div className="topbar">
                <img src={logo} alt="Logo" className="logo-mini" />
                <div className="user-dropdown" onClick={() => setShowDropdown(!showDropdown)}>
                    <span className="username">{user?.nombre || "Persona"} ▼</span>
                    {showDropdown && (
                        <div className="dropdown-menu">
                            <button className="dropdown-btn" onClick={handleLogout}>
                                <FaSignOutAlt style={{ marginRight: "8px" }} /> Cerrar sesión
                            </button>
                        </div>
                    )}
                </div>
            </div>

            <div className="registro-venta-container">
                <h2 className="registro-title">Registrar Nueva Venta</h2>

                {error && (
                    <div className="error-message">
                        <strong>Error:</strong> {error}
                    </div>
                )}

                <form className="registro-form">
                    <div className="form-group">
                        <label htmlFor="idCliente">Cliente:</label>
                        <select
                            id="idCliente"
                            name="idCliente"
                            className="registro-input"
                            value={formData.idCliente}
                            onChange={handleInputChange}
                            required
                        >
                            <option value="">Seleccionar Cliente</option>
                            {clientes.map((c) => (
                                <option key={c.idCliente} value={c.idCliente}>
                                    {c.nombre} {c.apellido}
                                </option>
                            ))}
                        </select>
                    </div>

                    <div className="form-group">
                        <label htmlFor="metodoPago">Método de Pago:</label>
                        <select
                            id="metodoPago"
                            name="metodoPago"
                            className="registro-input"
                            value={formData.metodoPago}
                            onChange={handleInputChange}
                            required
                        >
                            <option value="Efectivo">Efectivo</option>
                            <option value="Tarjeta">Tarjeta</option>
                            <option value="Transferencia">Transferencia</option>
                            <option value="Otro">Otro</option>
                        </select>
                    </div>

                    <div className="registro-info">
                        <em>
                            Venta registrada por: <strong>{user?.nombre} {user?.apellido}</strong>
                            {formData.idPersona && ` (ID: ${formData.idPersona})`}
                        </em>
                    </div>

                    <div className="total-display">
                        <strong>Total: ${formData.total.toFixed(2)}</strong>
                    </div>

                    <h3 className="detalles-title">
                        <FaShoppingCart /> Detalles de Venta
                    </h3>

                    <div className="detalle-form">
                        <div className="form-group">
                            <label htmlFor="idProduccion">Producción:</label>
                            <select
                                id="idProduccion"
                                name="idProduccion"
                                className="registro-input"
                                value={detalleForm.idProduccion}
                                onChange={handleDetalleInputChange}
                            >
                                <option value="">Seleccionar Producción</option>
                                {producciones.map((p) => (
                                    <option key={p.idProduccion} value={p.idProduccion}>
                                        #{p.idProduccion} - {p.fechaCosecha?.split("T")[0] || "sin fecha"}
                                        (Disponible: {p.cantidadCosechada} kg)
                                    </option>
                                ))}
                            </select>
                        </div>

                        <div className="form-group">
                            <label htmlFor="cantidad">Cantidad (kg):</label>
                            <input
                                id="cantidad"
                                type="number"
                                name="cantidad"
                                min="0.1"
                                step="0.1"
                                placeholder="Cantidad"
                                className="registro-input"
                                value={detalleForm.cantidad}
                                onChange={handleDetalleInputChange}
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="precioUnitario">Precio Unitario:</label>
                            <input
                                id="precioUnitario"
                                type="number"
                                name="precioUnitario"
                                min="0.01"
                                step="0.01"
                                placeholder="Precio"
                                className="registro-input"
                                value={detalleForm.precioUnitario}
                                onChange={handleDetalleInputChange}
                            />
                        </div>

                        <button
                            type="button"
                            className="btn-agregar"
                            onClick={agregarDetalle}
                            disabled={loading}
                        >
                            <FaPlus /> Agregar
                        </button>
                    </div>

                    <div className="detalles-list">
                        {detalles.length > 0 ? (
                            <table className="detalles-table">
                                <thead>
                                <tr>
                                    <th>ID Producción</th>
                                    <th>Cantidad (kg)</th>
                                    <th>Precio Unitario</th>
                                    <th>Subtotal</th>
                                    <th>Acciones</th>
                                </tr>
                                </thead>
                                <tbody>
                                {detalles.map((d, i) => (
                                    <tr key={i}>
                                        <td>{d.idProduccion}</td>
                                        <td>{d.cantidad.toFixed(2)}</td>
                                        <td>${d.precioUnitario.toFixed(2)}</td>
                                        <td>${d.subtotal.toFixed(2)}</td>
                                        <td>
                                            <button
                                                className="btn-eliminar-detalle"
                                                onClick={() => eliminarDetalle(i)}
                                                disabled={loading}
                                            >
                                                <FaTrash />
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                        ) : (
                            <p className="no-detalles">No hay detalles agregados</p>
                        )}
                    </div>

                    <div className="registro-botones">
                        <button
                            type="button"
                            onClick={handleCancel}
                            className="btn-cancelar"
                            disabled={loading}
                        >
                            Cancelar
                        </button>
                        <button
                            type="button"
                            onClick={handleRegister}
                            className="btn-registrar"
                            disabled={loading || detalles.length === 0}
                        >
                            {loading ? (
                                <span>Registrando... <i className="fa fa-spinner fa-spin"></i></span>
                            ) : (
                                "Registrar Venta"
                            )}
                        </button>
                    </div>
                </form>
            </div>

            <div className="watermark">
                <img src={watermarkImage} alt="Marca de agua" />
            </div>
        </div>
    );
};

export default RegistrarVenta;