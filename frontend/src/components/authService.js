const getAuthToken = () => {
  return localStorage.getItem("authToken");
};

const isAuthenticated = () => {
  return !!getAuthToken();
};

const getCurrentUser = () => {
  const userData = localStorage.getItem("userData");
  return userData ? JSON.parse(userData) : null;
};

const logout = () => {
  localStorage.removeItem("authToken");
  localStorage.removeItem("userData");
  window.location.href = "/login";
};

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
