import { Link } from "react-router-dom";

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
