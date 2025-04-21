/**
 * @file Unauthorized.js
 * @description Componente de interfaz que se muestra cuando un usuario intenta acceder a una ruta sin los permisos necesarios.
 */

import { Link } from "react-router-dom";

/**
 * Componente que renderiza un mensaje de acceso no autorizado.
 * Ofrece un enlace para volver al menú principal si el usuario tiene acceso a él.
 *
 * @component
 * @returns {JSX.Element} Vista de acceso no autorizado.
 */
const Unauthorized = () => {
    return (
        <div className="unauthorized-container">
            <h1>Acceso No Autorizado</h1>
            <p>No tienes permisos para acceder a esta página.</p>
            <Link to="/menu">Volver al Menú Principal</Link>
        </div>
    );
};

export default Unauthorized;
