/**
 * @file authService.js
 * @description Servicio de autenticación para manejar el token JWT, los datos del usuario
 * y las peticiones autenticadas. Proporciona utilidades para verificar el estado de sesión,
 * obtener información del usuario y cerrar sesión.
 */

/**
 * Obtiene el token JWT almacenado en localStorage.
 *
 * @function
 * @returns {string|null} Token JWT o null si no existe.
 */
const getAuthToken = () => {
    return localStorage.getItem('authToken');
};

/**
 * Verifica si el usuario está autenticado.
 *
 * @function
 * @returns {boolean} `true` si existe un token válido, de lo contrario `false`.
 */
const isAuthenticated = () => {
    return !!getAuthToken();
};

/**
 * Obtiene los datos del usuario actual desde localStorage.
 *
 * @function
 * @returns {Object|null} Objeto con los datos del usuario o `null` si no hay datos.
 */
const getCurrentUser = () => {
    const userData = localStorage.getItem('userData');
    return userData ? JSON.parse(userData) : null;
};

/**
 * Cierra la sesión del usuario eliminando el token y los datos del usuario del almacenamiento local.
 * Luego redirige al usuario a la página de inicio de sesión.
 *
 * @function
 */
const logout = () => {
    localStorage.removeItem('authToken');
    localStorage.removeItem('userData');
    window.location.href = '/login';
};

/**
 * Realiza una petición HTTP autenticada con el token JWT en la cabecera.
 *
 * @async
 * @function
 * @param {string} url - URL a la que se realizará la petición.
 * @param {Object} [options={}] - Opciones adicionales para `fetch`.
 * @throws Lanza un error si no hay token o si la respuesta es 401 (no autorizado).
 * @returns {Promise<Response>} Respuesta de la petición.
 */
const authFetch = async (url, options = {}) => {
    const token = getAuthToken();

    if (!token) {
        throw new Error('No hay un token de autenticación');
    }

    const headers = {
        ...options.headers,
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
    };

    const response = await fetch(url, {
        ...options,
        headers,
    });

    if (response.status === 401) {
        logout();
        throw new Error('Sesión expirada. Por favor, inicie sesión de nuevo.');
    }

    return response;
};

/**
 * Objeto que agrupa los métodos de autenticación disponibles para su uso en la aplicación.
 */
const authService = {
    getAuthToken,
    isAuthenticated,
    getCurrentUser,
    logout,
    authFetch
};

export default authService;
