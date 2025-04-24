import { useState, useEffect } from "react";
import authService from "../components/authService";

const Dashboard = () => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const currentUser = authService.getCurrentUser();
    setUser(currentUser);
    setLoading(false);
  }, []);

  const handleLogout = () => {
    authService.logout();
  };

  if (loading) {
    return <div>Cargando...</div>;
  }

  return (
    <div className="dashboard-container">
      <h1>Dashboard</h1>
      {user && (
        <div className="user-info">
          <h2>
            Bienvenido, {user.nombre} {user.apellido}
          </h2>
          <p>ID: {user.id}</p>
          <p>Tipo de usuario: {user.tipoUsuario}</p>
        </div>
      )}
      <button onClick={handleLogout} className="logout-button">
        Cerrar Sesión
      </button>
    </div>
  );
};

export default Dashboard;
