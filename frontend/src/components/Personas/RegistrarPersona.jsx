import React, { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import authService from "../authService";
import logo from "./../assets/APROAFA2.png";
import watermarkImage from "./../assets/LogoBosque.png";
import { FaSignOutAlt } from "react-icons/fa";
import "./Personas.css";
import "./RegistrarPersona.css";

const RegistrarPersona = () => {
  const [user, setUser] = useState(null);
  const [showDropdown, setShowDropdown] = useState(false);
  const [formData, setFormData] = useState({
    nombre: "",
    apellido: "",
    numeroIdentificacion: "",
    tipoId: "1",
    telefono: "",
    direccion: "",
    idFinca: 1,
  });
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);
  const [isLoading, setIsLoading] = useState(false);

  const navigate = useNavigate();
  const location = useLocation();
  const queryParams = new URLSearchParams(location.search);
  const tipoPersona = queryParams.get("tipo") || "cliente";

  useEffect(() => {
    const currentUser = authService.getCurrentUser();
    setUser(currentUser);

    if (currentUser?.idFinca) {
      setFormData((prevFormData) => ({
        ...prevFormData,
        idFinca: currentUser.idFinca,
      }));
    } else {
      console.warn("El usuario no tiene idFinca asignado.");
    }
  }, []);

  const toggleDropdown = () => {
    setShowDropdown(!showDropdown);
  };

  const handleLogout = () => {
    authService.logout();
    navigate("/");
  };

  const handleCancel = () => {
    navigate("/personas");
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  const handleRegister = async () => {
    setError(null);
    setSuccess(null);
    setIsLoading(true);

    try {
      const endpoint =
          tipoPersona === "cliente"
              ? "http://localhost:8080/api/clientes"
              : "http://localhost:8080/api/proveedores";

      const requestBody =
          tipoPersona === "cliente"
              ? {
                nombre: formData.nombre,
                apellido: formData.apellido,
                tipoId: parseInt(formData.tipoId),
                numeroIdentificacion: formData.numeroIdentificacion,
                telefono: formData.telefono,
                direccion: formData.direccion,
                idFinca: formData.idFinca,
                tipoCliente: "REGULAR",
                fechaRegistro: new Date().toISOString().split("T")[0],
              }
              : {
                nombre: formData.nombre,
                apellido: formData.apellido,
                tipoId: parseInt(formData.tipoId),
                numeroIdentificacion: formData.numeroIdentificacion,
                telefono: formData.telefono,
                direccion: formData.direccion,
                idFinca: formData.idFinca,
                contacto: formData.telefono,
              };

      const response = await fetch(endpoint, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(requestBody),
      });

      let data;
      const contentType = response.headers.get("content-type");
      if (contentType && contentType.includes("application/json")) {
        data = await response.json();
      } else {
        const text = await response.text();
        try {
          data = JSON.parse(text);
        } catch {
          data = { message: text };
        }
      }

      if (!response.ok) {
        const errorMessage =
            data.message || `Error al registrar ${tipoPersona}`;
        console.error("Error en la respuesta:", data);
        throw new Error(errorMessage);
      }

      console.log("Registro exitoso:", data);
      setSuccess(
          `${tipoPersona === "cliente" ? "Cliente" : "Proveedor"} registrado exitosamente`
      );
      setTimeout(() => {
        navigate("/personas");
      }, 1500);
    } catch (error) {
      console.error("Detalles del error:", error);
      if (error.message.includes("Duplicate entry")) {
        if (error.message.includes("id_persona")) {
          setError(
              "Esta persona ya está registrada como proveedor en el sistema. Por favor, use una identificación diferente."
          );
        } else {
          setError(
              "Datos duplicados detectados. Por favor, verifique la información e intente nuevamente."
          );
        }
      } else {
        setError(error.message);
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
      <div className="main-container">
        <div className="topbar">
          <img src={logo} alt="Logo" className="logo-mini" />
          <div className="user-dropdown" onClick={toggleDropdown}>
            <span className="username">{user ? user.username : "Usuario"} ▼</span>
            {showDropdown && (
                <div className="dropdown-menu">
                  <button className="dropdown-btn" onClick={handleLogout}>
                    <FaSignOutAlt style={{ marginRight: "8px" }} /> Cerrar sesión
                  </button>
                </div>
            )}
          </div>
        </div>

        <div
            className="registro-persona-container"
            style={{ overflowY: "auto", maxHeight: "90vh" }}
        >
          <h2 className="registro-title">
            Registrar {tipoPersona === "cliente" ? "Cliente" : "Proveedor"}
          </h2>

          {error && <div className="error-message">{error}</div>}
          {success && <div className="success-message">{success}</div>}

          <form className="registro-form" onSubmit={(e) => e.preventDefault()}>
            <div className="form-group">
              <label htmlFor="nombre">Nombre *</label>
              <input
                  id="nombre"
                  type="text"
                  name="nombre"
                  placeholder="Nombre"
                  className="registro-input"
                  value={formData.nombre}
                  onChange={handleInputChange}
                  required
              />
            </div>

            <div className="form-group">
              <label htmlFor="apellido">Apellidos *</label>
              <input
                  id="apellido"
                  type="text"
                  name="apellido"
                  placeholder="Apellidos"
                  className="registro-input"
                  value={formData.apellido}
                  onChange={handleInputChange}
                  required
              />
            </div>

            <div className="form-group">
              <label htmlFor="tipoId">Tipo de Documento *</label>
              <select
                  id="tipoId"
                  name="tipoId"
                  className="registro-input"
                  value={formData.tipoId}
                  onChange={handleInputChange}
                  required
              >
                <option value="1">Cédula de Ciudadanía</option>
                <option value="2">Tarjeta de Identidad</option>
                <option value="3">Cédula de Extranjería</option>
              </select>
            </div>

            <div className="form-group">
              <label htmlFor="numeroIdentificacion">Número de Documento *</label>
              <input
                  id="numeroIdentificacion"
                  type="text"
                  name="numeroIdentificacion"
                  placeholder="Número de Documento"
                  className="registro-input"
                  value={formData.numeroIdentificacion}
                  onChange={handleInputChange}
                  required
              />
            </div>

            <div className="form-group">
              <label htmlFor="telefono">Teléfono</label>
              <input
                  id="telefono"
                  type="text"
                  name="telefono"
                  placeholder="Teléfono"
                  className="registro-input"
                  value={formData.telefono}
                  onChange={handleInputChange}
              />
            </div>

            <div className="form-group">
              <label htmlFor="direccion">Dirección</label>
              <input
                  id="direccion"
                  type="text"
                  name="direccion"
                  placeholder="Dirección"
                  className="registro-input"
                  value={formData.direccion}
                  onChange={handleInputChange}
              />
            </div>

            <div className="registro-botones">
              <button
                  type="button"
                  onClick={handleCancel}
                  className="btn-cancelar"
                  disabled={isLoading}
              >
                Cancelar
              </button>
              <button
                  type="button"
                  onClick={handleRegister}
                  className="btn-registrar"
                  disabled={isLoading}
              >
                {isLoading ? "Registrando..." : "Registrar"}
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

export default RegistrarPersona;