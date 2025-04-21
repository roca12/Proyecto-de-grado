/**
 * @file Login.jsx
 * @description Componente que representa el formulario de inicio de sesión.
 * Permite a los usuarios autenticarse utilizando su ID de usuario y contraseña.
 * Si las credenciales son válidas, guarda el token JWT y los datos del usuario
 * en el almacenamiento local, y redirige a la vista correspondiente.
 */

import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { loginUsuario } from "./LoginService";
import "./Login.css";
import logo from "./../assets/APROAFA.jpg";
import backgroundImg from "./../assets/prueba.jpg";

/**
 * Componente funcional para el inicio de sesión.
 *
 * @component
 * @returns {JSX.Element} Interfaz de usuario del login con campos de entrada y botones.
 */
const Login = () => {
    const [idUsuario, setIdUsuario] = useState("");
    const [contraseña, setContraseña] = useState("");
    const [error, setError] = useState("");
    const navigate = useNavigate();

    /**
     * Maneja el evento de envío del formulario de login.
     * Autentica al usuario y guarda el token en localStorage si es exitoso.
     *
     * @async
     * @function
     * @param {React.FormEvent} e - Evento del formulario.
     */
    const handleLogin = async (e) => {
        e.preventDefault();
        setError("");

        try {
            const data = await loginUsuario(idUsuario, contraseña);

            // Guardar token y datos del usuario
            localStorage.setItem("authToken", data.token);
            localStorage.setItem("userData", JSON.stringify({
                id: data.idPersona,
                nombre: data.nombre,
                apellido: data.apellido,
                tipoUsuario: data.tipoUsuario
            }));

            // Redirigir según el tipo de usuario
            if (data.tipoUsuario === "ADMIN") {
                navigate("/admin-dashboard");
            } else {
                navigate("/menu");
            }

        } catch (error) {
            setError(error.message);
        }
    };

    return (
        <div className="login-container">
            <div className="login-left">
                <img src={logo} alt="Logo" className="logo-img" />

                <form onSubmit={handleLogin} className="login-form">
                    <div className="input-group">
                        <input
                            type="text"
                            placeholder="Usuario"
                            value={idUsuario}
                            onChange={(e) => setIdUsuario(e.target.value)}
                            required
                        />
                    </div>

                    <div className="input-group">
                        <input
                            type="password"
                            placeholder="Contraseña"
                            value={contraseña}
                            onChange={(e) => setContraseña(e.target.value)}
                            required
                        />
                    </div>

                    {error && <div className="error-message">{error}</div>}

                    <div className="btn-group">
                        <button type="button">Registrar</button>
                        <button type="submit">Iniciar</button>
                    </div>
                </form>
            </div>

            <div className="login-right">
                <img src={backgroundImg} alt="Imagen de login" className="background-img" />
            </div>
        </div>
    );
};

export default Login;
