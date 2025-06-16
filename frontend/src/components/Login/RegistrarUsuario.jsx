import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import logo from "./../assets/APROAFA2.png";
import watermarkImage from "./../assets/LogoBosque.png";
import "./RegistrarUsuario.css";

const RegistrarUsuario = () => {
    const [formData, setFormData] = useState({
        nombre: "",
        apellido: "",
        numeroIdentificacion: "",
        tipoId: "1",
        telefono: "",
        direccion: "",
        correo: "",
        password: "",
        confirmPassword: "",
        idFinca: ""
    });

    const [error, setError] = useState(null);
    const [success, setSuccess] = useState(null);
    const [isLoading, setIsLoading] = useState(false);
    const navigate = useNavigate();

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({
            ...formData,
            [name]: value,
        });
    };

    const handleCancel = () => {
        navigate("/");
    };

    const handleRegister = async () => {
        setError(null);
        setSuccess(null);

        // Validaciones del formulario
        if (formData.password !== formData.confirmPassword) {
            setError("Las contraseñas no coinciden");
            return;
        }

        if (formData.password.length < 6) {
            setError("La contraseña debe tener al menos 6 caracteres");
            return;
        }

        if (!formData.idFinca) {
            setError("Debe ingresar un ID de finca");
            return;
        }

        if (!formData.nombre || !formData.apellido || !formData.numeroIdentificacion) {
            setError("Por favor complete todos los campos obligatorios");
            return;
        }

        setIsLoading(true);

        try {
            const response = await fetch("http://localhost:8080/usuarios/register", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    nombre: formData.nombre,
                    apellido: formData.apellido,
                    tipoId: parseInt(formData.tipoId),
                    numeroIdentificacion: formData.numeroIdentificacion,
                    telefono: formData.telefono,
                    direccion: formData.direccion,
                    email: formData.correo,
                    contraseña: formData.password,
                    tipoUsuario: "USER",
                    fincaId: parseInt(formData.idFinca)
                }),
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText || "Error al registrar el usuario");
            }

            setSuccess("Usuario registrado exitosamente. Por favor inicie sesión.");

            setTimeout(() => {
                navigate("/");
            }, 5000);
        } catch (error) {
            console.error("Error en el registro:", error);
            setError(error.message || "Error al registrar el usuario");
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="main-container">
            <div className="topbar">
                <img src={logo} alt="Logo" className="logo-mini" />
            </div>

            <div className="registro-persona-container" style={{ overflowY: "auto", maxHeight: "90vh" }}>
                <h2 className="registro-title">Registro de Nuevo Usuario</h2>

                {error && <div className="error-message">{error}</div>}
                {success && (
                    <div className="success-message usuario-id-message">
                        <strong>¡REGISTRO EXITOSO!</strong>
                        <p>Su número de identificación <span className="user-id-highlight">{formData.numeroIdentificacion}</span> es su usuario para iniciar sesión.</p>
                        <p>Será redirigido al login en breve...</p>
                    </div>
                )}

                <form className="registro-form" onSubmit={(e) => e.preventDefault()}>
                    <div className="form-group">
                        <label htmlFor="nombre">Nombre *</label>
                        <input
                            id="nombre"
                            type="text"
                            name="nombre"
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
                            className="registro-input"
                            value={formData.direccion}
                            onChange={handleInputChange}
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="correo">Correo Electrónico</label>
                        <input
                            id="correo"
                            type="email"
                            name="correo"
                            className="registro-input"
                            value={formData.correo}
                            onChange={handleInputChange}
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="password">Contraseña *</label>
                        <input
                            id="password"
                            type="password"
                            name="password"
                            className="registro-input"
                            value={formData.password}
                            onChange={handleInputChange}
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="confirmPassword">Confirmar Contraseña *</label>
                        <input
                            id="confirmPassword"
                            type="password"
                            name="confirmPassword"
                            className="registro-input"
                            value={formData.confirmPassword}
                            onChange={handleInputChange}
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="idFinca">ID de Finca *</label>
                        <input
                            id="idFinca"
                            type="number"
                            name="idFinca"
                            className="registro-input"
                            value={formData.idFinca}
                            onChange={handleInputChange}
                            required
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
                            {isLoading ? "Registrando..." : "Registrarse"}
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

export default RegistrarUsuario;