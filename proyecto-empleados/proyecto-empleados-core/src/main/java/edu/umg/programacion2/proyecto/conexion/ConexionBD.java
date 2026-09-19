package edu.umg.programacion2.proyecto.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Punto único donde se abre la conexión JDBC.
 *
 * Cambia aquí URL/usuario/contraseña según tu instalación local de
 * MySQL/MariaDB. Ni el DAO ni mucho menos la UI deberían tener estos
 * valores hardcodeados en más de un lugar.
 */
public class ConexionBD {

    private static final String URL = "jdbc:mysql://localhost:3306/empleados_db"
            + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USUARIO = "root";
    private static final String PASSWORD = "MyfirstBD.W";

    // Constructor privado: esta clase es solo un proveedor estático de conexiones.
    private ConexionBD() {
    }

    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, PASSWORD);
    }
}
