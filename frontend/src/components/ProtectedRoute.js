/**
 * @file ProtectedRoute.js
 * @description Componente de orden superior que protege rutas según el estado de autenticación
 * y el rol del usuario. Utiliza React Router para redirigir cuando sea necesario.
 */

import { Navigate, Outlet } from 'react-router-dom';
import authService from '../components/authService';

/**
 * Componente que protege rutas que requieren autenticación y, opcionalmente, un rol específico.
 *
 * @component
 * @param {Object} props - Props del componente.
 * @param {string} [props.requiredRole] - Rol requerido para acceder a la ruta (por ejemplo, "Administrador").
 * @returns {JSX.Element} Contenido protegido si cumple con las condiciones, de lo contrario redirige.
 */
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
