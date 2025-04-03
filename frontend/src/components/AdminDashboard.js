import { useState, useEffect } from "react";
import authService from "../components/authService";

const AdminDashboard = () => {
  const [usuarios, setUsuarios] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const fetchUsuarios = async () => {
      try {
        const response = await authService.authFetch(
          "http://localhost:8080/usuarios",
        );

        if (!response.ok) {
          throw new Error("Error al cargar usuarios");
        }

        const data = await response.json();
        setUsuarios(data);
      } catch (error) {
        setError(error.message);
      } finally {
        setLoading(false);
      }
    };

    fetchUsuarios();
  }, []);

  const handleLogout = () => {
    authService.logout();
  };

  if (loading) {
    return <div>Cargando usuarios...</div>;
  }

  return (
    <div className="admin-dashboard">
      <h1>Panel de Administración</h1>

      {error && <p className="error-message">{error}</p>}

      <div className="usuarios-list">
        <h2>Lista de Usuarios</h2>
        {usuarios.length > 0 ? (
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Nombre</th>
                <th>Apellido</th>
                <th>Email</th>
                <th>Tipo Usuario</th>
              </tr>
            </thead>
            <tbody>
              {usuarios.map((usuario) => (
                <tr key={usuario.idPersona}>
                  <td>{usuario.idPersona}</td>
                  <td>{usuario.nombre}</td>
                  <td>{usuario.apellido}</td>
                  <td>{usuario.email}</td>
                  <td>{usuario.tipoUsuario}</td>
                </tr>
              ))}
            </tbody>
          </table>
        ) : (
          <p>No hay usuarios registrados</p>
        )}
      </div>

      <button onClick={handleLogout} className="logout-button">
        Cerrar Sesión
      </button>
    </div>
  );
};

export default AdminDashboard;
