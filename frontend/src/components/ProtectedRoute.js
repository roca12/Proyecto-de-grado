import { Navigate, Outlet } from "react-router-dom";
import authService from "../components/authService";

// Componente para proteger rutas que requieren autenticación
const ProtectedRoute = ({ requiredRole }) => {
  const isAuthenticated = authService.isAuthenticated();
  const currentUser = authService.getCurrentUser();

  if (!isAuthenticated) {
    // Si no está autenticado, redirigir a login
    return <Navigate to="/login" />;
  }

  // Si se requiere un rol específico y el usuario no lo tiene, redirigir
  if (requiredRole && currentUser.tipoUsuario !== requiredRole) {
    return <Navigate to="/unauthorized" />;
  }

  // Si está autenticado y tiene los permisos necesarios, mostrar el contenido
  return <Outlet />;
};

export default ProtectedRoute;
