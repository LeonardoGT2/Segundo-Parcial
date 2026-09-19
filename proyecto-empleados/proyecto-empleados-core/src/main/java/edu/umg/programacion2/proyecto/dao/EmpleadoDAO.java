package edu.umg.programacion2.proyecto.dao;

import edu.umg.programacion2.proyecto.conexion.ConexionBD;
import edu.umg.programacion2.proyecto.modelo.Empleado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO de Empleado: las 4 operaciones CRUD contra la tabla `empleados`.
 *
 * Mismo contrato que en clase08: crear/listarTodos/buscarPorId/actualizar/eliminar.
 * Todo con PreparedStatement (nunca concatenación de Strings en el SQL).
 * Esta clase es la única en todo el proyecto que sabe que existe JDBC.
 */
public class EmpleadoDAO {

    public Empleado crear(Empleado item) throws SQLException {
        String sql = "INSERT INTO empleados "
                + "(nombre_completo, departamento, salario_mensual, fecha_contratacion, activo, tipo_contrato) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, item.getNombreCompleto());
            ps.setString(2, item.getDepartamento());
            ps.setBigDecimal(3, item.getSalarioMensual());
            ps.setDate(4, java.sql.Date.valueOf(item.getFechaContratacion()));
            ps.setBoolean(5, item.isActivo());
            ps.setString(6, item.getTipoContrato());

            ps.executeUpdate();

            try (ResultSet generadas = ps.getGeneratedKeys()) {
                if (generadas.next()) {
                    item.setId(generadas.getInt(1));
                }
            }
        }
        return item;
    }

    public List<Empleado> listarTodos() throws SQLException {
        String sql = "SELECT id, nombre_completo, departamento, salario_mensual, "
                + "fecha_contratacion, activo, tipo_contrato FROM empleados ORDER BY id";
        List<Empleado> resultado = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                resultado.add(mapearFila(rs));
            }
        }
        return resultado;
    }

    public Optional<Empleado> buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nombre_completo, departamento, salario_mensual, "
                + "fecha_contratacion, activo, tipo_contrato FROM empleados WHERE id = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearFila(rs));
                }
            }
        }
        return Optional.empty();
    }

    public boolean actualizar(Empleado item) throws SQLException {
        String sql = "UPDATE empleados SET nombre_completo = ?, departamento = ?, "
                + "salario_mensual = ?, fecha_contratacion = ?, activo = ?, tipo_contrato = ? WHERE id = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, item.getNombreCompleto());
            ps.setString(2, item.getDepartamento());
            ps.setBigDecimal(3, item.getSalarioMensual());
            ps.setDate(4, java.sql.Date.valueOf(item.getFechaContratacion()));
            ps.setBoolean(5, item.isActivo());
            ps.setString(6, item.getTipoContrato());
            ps.setInt(7, item.getId());
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM empleados WHERE id = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        }
    }

    /** Convierte la fila actual de un ResultSet en un objeto Empleado. */
    private Empleado mapearFila(ResultSet rs) throws SQLException {
        Empleado emp = new Empleado(
                rs.getInt("id"),
                rs.getString("nombre_completo"),
                rs.getString("departamento"),
                rs.getBigDecimal("salario_mensual"),
                rs.getDate("fecha_contratacion").toLocalDate(),
                rs.getBoolean("activo")
        );
        emp.setTipoContrato(rs.getString("tipo_contrato"));
        return emp;
    }
    }

