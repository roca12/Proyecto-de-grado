import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate,
} from "react-router-dom";

import Login from "./components/Login/Login";
import RegistrarUsuario from "./components/Login/RegistrarUsuario";
import Menu from "./components/Menu/Menu";
import Personas from "./components/Personas/Personas";
import RegistrarPersona from "./components/Personas/RegistrarPersona";
import Insumos from "./components/Insumos/Insumos";
import RegistrarInsumo from "./components/Insumos/RegistrarInsumo";
import Actividades from "./components/Actividades/Actividades";
import Produccion from "./components/Produccion/Produccion";
import RegistrarProduccion from "./components/Produccion/RegistrarProduccion";
import RegistrarActividad from "./components/Actividades/RegistrarActividad";
import Unauthorized from "./components/Unauthorized";
import ProtectedRoute from "./components/ProtectedRoute";
import "./App.css";
import authService from "./components/authService";
import Cultivos from "./components/Cultivos/Cultivos";

function App() {
  return (
    <Router>
      <div className="app-container">
        <Routes>
          {/* Ruta pública para login */}
          <Route path="/login" element={<Login />} />
          <Route path="/Registro" element={<RegistrarUsuario/>}/>
          {/* Ruta pública para acceso no autorizado */}
          <Route path="/unauthorized" element={<Unauthorized />} />

          {/* Rutas protegidas para todos los usuarios autenticados */}
          <Route element={<ProtectedRoute />}>
            <Route path="/menu" element={<Menu />} />
            <Route path="/personas" element={<Personas />} />
            <Route path="/registrar-persona" element={<RegistrarPersona />} />
            <Route path="/insumos" element={<Insumos />} />
            <Route path="/registrar-insumo" element={<RegistrarInsumo />} />
            <Route path="/actividades" element={<Actividades />} />
            <Route path="/produccion" element={<Produccion />} />
            <Route
              path="/registrar-produccion"
              element={<RegistrarProduccion />}
            />
            <Route path="/cultivo" element={<Cultivos />} />
          </Route>

          {/* Rutas protegidas solo para administradores */}
          <Route element={<ProtectedRoute requiredRole="ADMIN" />}>
            <Route path="/menu" element={<Menu />} />
            <Route path="/personas" element={<Personas />} />
            <Route path="/registrar-persona" element={<RegistrarPersona />} />
            <Route path="/insumos" element={<Insumos />} />
            <Route path="/registrar-insumo" element={<RegistrarInsumo />} />
            <Route path="/actividades" element={<Actividades />} />
            <Route path="/produccion" element={<Produccion />} />
            <Route
              path="/registrar-produccion"
              element={<RegistrarProduccion />}
            />
            <Route
              path="/registrar-actividad"
              element={<RegistrarActividad />}
            />
          </Route>

          {/* Redirección dependiendo del estado de autenticación */}
          <Route
            path="/"
            element={
              authService.isAuthenticated() ? (
                <Navigate to="/menu" />
              ) : (
                <Navigate to="/login" />
              )
            }
          />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
