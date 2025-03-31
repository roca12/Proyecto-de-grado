import { useState } from "react";

const Login = () => {
    const [idUsuario, setIdUsuario] = useState("");
    const [contraseña, setContraseña] = useState("");
    const [error, setError] = useState("");

    const handleLogin = async (e) => {
        e.preventDefault();
        setError("");

        try {
            const response = await fetch("http://localhost:8080/usuarios/login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ idPersona: idUsuario, contraseña }), // Importante: la clave debe coincidir con el backend
            });

            const data = await response.text(); // Convertimos la respuesta a texto para verla en la consola

            console.log("Respuesta del backend:", data);

            if (!response.ok) {
                throw new Error(data);
            }

            alert("Login exitoso");
        } catch (error) {
            setError(error.message);
        }
    };

    return (
        <div>
            <h2>Iniciar Sesión</h2>
            <form onSubmit={handleLogin}>
                <label>ID de Usuario:</label>
                <input type="text" value={idUsuario} onChange={(e) => setIdUsuario(e.target.value)} required />

                <label>Contraseña:</label>
                <input type="password" value={contraseña} onChange={(e) => setContraseña(e.target.value)} required />

                <button type="submit">Iniciar Sesión</button>
            </form>

            {error && <p style={{ color: "red" }}>{error}</p>}
        </div>
    );
};

export default Login;
