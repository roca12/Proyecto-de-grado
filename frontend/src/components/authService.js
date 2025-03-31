// Obtener el token del almacenamiento local
const getAuthToken = () => {
    return localStorage.getItem('authToken');
};

// Comprobar si el usuario está autenticado
const isAuthenticated = () => {
    return !!getAuthToken();
};

// Obtener los datos del usuario actual
const getCurrentUser = () => {
    const userData = localStorage.getItem('userData');
    return userData ? JSON.parse(userData) : null;
};

// Cerrar sesión
const logout = () => {
    localStorage.removeItem('authToken');
    localStorage.removeItem('userData');
    // Puedes redirigir al usuario a la página de inicio de sesión aquí si lo deseas
    window.location.href = '/';
};

// Realizar una petición autenticada con el token JWT
const authFetch = async (url, options = {}) => {
    const token = getAuthToken();

    if (!token) {
        throw new Error('No hay un token de autenticación');
    }

    // Configurar las cabeceras con el token JWT
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
        // Si recibimos un 401 Unauthorized, el token puede haber expirado
        logout();
        throw new Error('Sesión expirada. Por favor, inicie sesión de nuevo.');
    }

    return response;
};

const authService = {
    getAuthToken,
    isAuthenticated,
    getCurrentUser,
    logout,
    authFetch
};

export default authService;