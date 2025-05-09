/**
 * @file authService.js
 * @description Servicio de autenticación que maneja token, usuario actual y peticiones autenticadas.
 */

/**
 * Obtiene el token de autenticación del localStorage.
 * @returns {string|null} Token JWT o null si no existe.
 */
const getAuthToken = () => {
  return localStorage.getItem("authToken");
};

/**
 * Verifica si el usuario está autenticado.
 * @returns {boolean} True si hay un token guardado, false en caso contrario.
 */
const isAuthenticated = () => {
  return !!getAuthToken();
};

/**
 * Obtiene los datos del usuario almacenados en localStorage.
 * @returns {Object|null} Objeto del usuario o null si no existe.
 */
const getCurrentUser = () => {
  const userData = localStorage.getItem("userData");
  return userData ? JSON.parse(userData) : null;
};

/**
 * Cierra la sesión del usuario eliminando los datos y redireccionando al login.
 */
const logout = () => {
  localStorage.removeItem("authToken");
  localStorage.removeItem("userData");
  window.location.href = "/login";
};

/**
 * Realiza una petición fetch con autenticación.
 * @param {string} url - URL del endpoint.
 * @param {Object} options - Opciones adicionales para fetch.
 * @returns {Promise<Response>} Respuesta de la petición.
 * @throws Error si no hay token o si la sesión expira.
 */
const authFetch = async (url, options = {}) => {
  const token = getAuthToken();

  if (!token) {
    throw new Error("No hay un token de autenticación");
  }

  const headers = {
    ...options.headers,
    Authorization: `Bearer ${token}`,
    "Content-Type": "application/json",
  };

  const response = await fetch(url, {
    ...options,
    headers,
  });

  if (response.status === 401) {
    logout();
    throw new Error("Sesión expirada. Por favor, inicie sesión de nuevo.");
  }

  return response;
};

const authService = {
  getAuthToken,
  isAuthenticated,
  getCurrentUser,
  logout,
  authFetch,
};

export default authService;
