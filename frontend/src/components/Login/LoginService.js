/**
 * Envía una solicitud de inicio de sesión al backend con el ID del usuario y la contraseña.
 *
 * @async
 * @function loginUsuario
 * @param {string|number} idUsuario - ID del usuario (se convierte a número entero).
 * @param {string} contraseña - Contraseña del usuario.
 * @throws {Error} Lanza un error si la respuesta del servidor no es exitosa.
 * @returns {Promise<Object>} Los datos del usuario autenticado, incluyendo el token JWT y detalles del perfil.
 */
export async function loginUsuario(idUsuario, contraseña) {
    const response = await fetch("http://localhost:8080/usuarios/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            idPersona: parseInt(idUsuario),
            contraseña,
        }),
    });

    if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || "Error al iniciar sesión");
    }

    const data = await response.json();
    console.log("Login exitoso:", data);
    return data;
}
