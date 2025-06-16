/**
 * @file Login.jsx
 * @description Componente que representa el formulario de inicio de sesión.
 * Permite a los usuarios autenticarse utilizando su número de identificación y contraseña.
 * Si las credenciales son válidas, guarda el token JWT y los datos del usuario
 * en el almacenamiento local, y redirige a la vista correspondiente.
 */

import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { loginUsuario } from "./LoginService";
import "./Login.css";
import logo from "./../assets/APROAFA.jpg";
import backgroundImg from "./../assets/prueba.jpg";
import watermarkImage from "./../assets/LogoBosque.png";

/**
 * Componente funcional para el inicio de sesión.
 *
 * @component
 * @returns {JSX.Element} Interfaz de usuario del login con campos de entrada y botones.
 */
const Login = () => {
    const [numeroIdentificacion, setNumeroIdentificacion] = useState("");
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
            const data = await loginUsuario(numeroIdentificacion, contraseña);

            // Guardar el token y datos del usuario en localStorage
            localStorage.setItem("authToken", data.token);
            localStorage.setItem(
                "userData",
                JSON.stringify({
                    id: data.idPersona,
                    nombre: data.nombre,
                    apellido: data.apellido,
                    tipoUsuario: data.tipoUsuario,
                    idFinca: data.idFinca,
                }),
            );

            // Redirigir según el tipo de usuario
            if (data.tipoUsuario === "ADMIN") {
                navigate("/menu");
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
                            placeholder="Número de identificación"
                            value={numeroIdentificacion}
                            onChange={(e) => setNumeroIdentificacion(e.target.value)}
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
                        <button type="button" onClick={() => navigate("/registro")}>Registrar</button>
                        <button type="submit">Iniciar</button>
                    </div>
                </form>
            </div>

            <div className="login-right">
                <img
                    src={backgroundImg}
                    alt="Imagen de login"
                    className="background-img"
                />
            </div>
            <div className="watermark">
                <img src={watermarkImage} alt="Marca de agua" />
            </div>
        </div>
    );
};

export default Login;
