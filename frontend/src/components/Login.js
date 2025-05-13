import { useState } from "react";
import { useNavigate } from "react-router-dom";

const Login = () => {
  const [idUsuario, setIdUsuario] = useState("");
  const [contraseña, setContraseña] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    setError("");

    try {
      const response = await fetch("http://localhost:8080/usuarios/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ idPersona: parseInt(idUsuario), contraseña }),
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || "Error al iniciar sesión");
      }

      const data = await response.json();
      console.log("Login exitoso:", data);

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
      <h2>Iniciar Sesión</h2>
      <form onSubmit={handleLogin}>
        <div className="form-group">
          <label>ID de Usuario:</label>
          <input
            type="text"
            value={idUsuario}
            onChange={(e) => setIdUsuario(e.target.value)}
            required
          />
        </div>

        <div className="form-group">
          <label>Contraseña:</label>
          <input
            type="password"
            value={contraseña}
            onChange={(e) => setContraseña(e.target.value)}
            required
          />
        </div>

        <button type="submit" className="login-button">
          Iniciar Sesión
        </button>
      </form>

      {error && <p className="error-message">{error}</p>}
    </div>
  );
};

export default Login;
