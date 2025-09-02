import React, { useState, useEffect } from "react";
import { useParams, useNavigate, useLocation } from "react-router-dom";
import { FaShoppingCart, FaExclamationTriangle } from "react-icons/fa";
import "./RegistrarCompra.css";

const RegistrarCompra = () => {
  const { idInsumo } = useParams();
  const location = useLocation();
  const navigate = useNavigate();

  // Estado para almacenar el insumo actual
  const [insumo, setInsumo] = useState(location.state?.insumo || null);

  // Lista de proveedores obtenida desde el backend
  const [proveedores, setProveedores] = useState([]);

  // Estado de carga y error
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Datos del formulario
  const [formData, setFormData] = useState({
    cantidad: "",
    precioUnitario: "",
    fechaCompra: new Date().toISOString().split("T")[0],
    idProveedor: location.state?.proveedor?.idProveedor || "",
  });

  // Efecto para cargar insumo y proveedores
  useEffect(() => {
    const fetchData = async () => {
      try {
        // Si no recibimos el insumo por location.state, lo cargamos
        if (!insumo) {
          const insumoResponse = await fetch(
            `http://localhost:8080/insumos/${idInsumo}`,
          );
          if (!insumoResponse.ok) throw new Error("Error al cargar insumo");
          setInsumo(await insumoResponse.json());
        }

        // Cargar lista de proveedores
        const proveedoresResponse = await fetch(
          "http://localhost:8080/api/proveedores",
        );
        if (!proveedoresResponse.ok)
          throw new Error("Error al cargar proveedores");

        const proveedoresData = await proveedoresResponse.json();
        setProveedores(proveedoresData);
        setLoading(false);
      } catch (error) {
        console.error("Error:", error);
        setError(error.message);
        setLoading(false);
      }
    };

    fetchData();
  }, [idInsumo, insumo]);

  // Manejar cambios en los inputs
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  // Manejar envío del formulario
  const handleSubmit = async (e) => {
    e.preventDefault();

    // Validaciones frontend
    if (
      !formData.cantidad ||
      !formData.precioUnitario ||
      !formData.idProveedor
    ) {
      setError("Todos los campos son obligatorios");
      return;
    }

    if (
      parseFloat(formData.cantidad) <= 0 ||
      parseFloat(formData.precioUnitario) <= 0
    ) {
      setError("La cantidad y el precio deben ser mayores que cero");
      return;
    }

    try {
      setLoading(true);
      setError(null);

      // Formatear fecha correctamente para el backend
      const fechaCompra = new Date(formData.fechaCompra)
        .toISOString()
        .split("T")[0];

      const compraData = {
        insumo: { idInsumo: parseInt(idInsumo, 10) },
        cantidad: parseFloat(formData.cantidad),
        precioUnitario: parseFloat(formData.precioUnitario),
        fechaCompra: fechaCompra, // Usar la fecha formateada
        proveedor: { idProveedor: parseInt(formData.idProveedor, 10) },
      };

      const response = await fetch("http://localhost:8080/api/compra-insumos", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${localStorage.getItem("authToken")}`,
        },
        body: JSON.stringify(compraData),
      });

      if (!response.ok) {
        // Manejar error de forma más robusta
        let errorMessage = "Error al registrar compra";
        try {
          const errorData = await response.text(); // Primero intenta como texto
          try {
            const jsonError = JSON.parse(errorData); // Luego intenta parsear a JSON
            errorMessage = jsonError.message || jsonError.error || errorData;
          } catch {
            errorMessage = errorData;
          }
        } catch {
          errorMessage = `Error ${response.status}: ${response.statusText}`;
        }
        throw new Error(errorMessage);
      }

      navigate("/insumos");
    } catch (error) {
      console.error("Error al registrar compra:", error);
      setError(error.message || "Ocurrió un error al registrar la compra");
    } finally {
      setLoading(false);
    }
  };

  // Si está cargando
  if (loading) return <div>Cargando...</div>;

  // Si no se encontró el insumo
  if (!insumo) return <div>Insumo no encontrado</div>;

  return (
    <div className="registro-compra-container">
      <h2 className="registro-title">
        <FaShoppingCart style={{ marginRight: "10px" }} />
        Registrar Compra para: {insumo.nombre}
      </h2>

      {/* Mensaje de error */}
      {error && (
        <div className="error-message">
          <FaExclamationTriangle style={{ marginRight: "5px" }} />
          {error}
        </div>
      )}

      {/* Formulario */}
      <form className="registro-form" onSubmit={handleSubmit}>
        {/* Campo Cantidad */}
        <div className="form-group">
          <label>Cantidad:</label>
          <input
            type="number"
            name="cantidad"
            className="registro-input"
            value={formData.cantidad}
            onChange={handleInputChange}
            min="0.01"
            step="0.01"
            required
          />
        </div>

        {/* Campo Precio Unitario */}
        <div className="form-group">
          <label>Precio Unitario:</label>
          <input
            type="number"
            name="precioUnitario"
            className="registro-input"
            value={formData.precioUnitario}
            onChange={handleInputChange}
            min="0.01"
            step="0.01"
            required
          />
        </div>

        {/* Campo Fecha de Compra */}
        <div className="form-group">
          <label>Fecha de Compra:</label>
          <input
            type="date"
            name="fechaCompra"
            className="registro-input"
            value={formData.fechaCompra}
            onChange={handleInputChange}
            max={new Date().toISOString().split("T")[0]}
            required
          />
          <small>No puede ser una fecha futura</small>
        </div>

        {/* Selector de Proveedor */}
        <div className="form-group">
          <label>Proveedor:</label>
          <select
            name="idProveedor"
            className="registro-input"
            value={formData.idProveedor}
            onChange={handleInputChange}
            required
            disabled={!!insumo.proveedor}
          >
            <option value="">Seleccione un proveedor</option>
            {proveedores.map((proveedor) => (
              <option key={proveedor.idProveedor} value={proveedor.idProveedor}>
                {proveedor.nombre}
              </option>
            ))}
          </select>
          {insumo.proveedor && (
            <small>
              Proveedor actual: {insumo.proveedor.nombre} (no se puede
              modificar)
            </small>
          )}
        </div>

        {/* Botones */}
        <div className="registro-botones">
          <button
            type="button"
            className="btn-cancelar"
            onClick={() => navigate("/insumos")}
            disabled={loading}
          >
            Cancelar
          </button>
          <button type="submit" className="btn-registrar" disabled={loading}>
            {loading ? "Registrando..." : "Registrar Compra"}
          </button>
        </div>
      </form>
    </div>
  );
};

export default RegistrarCompra;
