package edu.umg.programacion2.proyecto.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Modelo de dominio para un empleado (Variante A).
 *
 * Esta clase vive en el módulo -core y es una POJO simple: no importa
 * java.sql.* ni javax.swing.*. Ni el DAO le exige más de lo que un empleado
 * "es" en el negocio, ni la UI necesita saber cómo se guarda.
 */
public class Empleado {

    private int id;
    private String nombreCompleto;
    private String departamento;
    private BigDecimal salarioMensual;
    private LocalDate fechaContratacion;
    private boolean activo;

    /** Constructor vacío: útil para ir llenando el objeto desde el formulario de la UI. */
    public Empleado() {
    }

    /** Constructor para crear un empleado nuevo (sin id todavía; lo asigna la BD). */
    public Empleado(String nombreCompleto, String departamento, BigDecimal salarioMensual,
                     LocalDate fechaContratacion, boolean activo) {
        this.nombreCompleto = nombreCompleto;
        this.departamento = departamento;
        this.salarioMensual = salarioMensual;
        this.fechaContratacion = fechaContratacion;
        this.activo = activo;
    }

    /** Constructor completo (incluye id): lo usa el DAO al reconstruir filas desde el ResultSet. */
    public Empleado(int id, String nombreCompleto, String departamento, BigDecimal salarioMensual,
                     LocalDate fechaContratacion, boolean activo) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.departamento = departamento;
        this.salarioMensual = salarioMensual;
        this.fechaContratacion = fechaContratacion;
        this.activo = activo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public BigDecimal getSalarioMensual() {
        return salarioMensual;
    }

    public void setSalarioMensual(BigDecimal salarioMensual) {
        this.salarioMensual = salarioMensual;
    }

    public LocalDate getFechaContratacion() {
        return fechaContratacion;
    }

    public void setFechaContratacion(LocalDate fechaContratacion) {
        this.fechaContratacion = fechaContratacion;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return "Empleado{" +
                "id=" + id +
                ", nombreCompleto='" + nombreCompleto + '\'' +
                ", departamento='" + departamento + '\'' +
                ", salarioMensual=" + salarioMensual +
                ", fechaContratacion=" + fechaContratacion +
                ", activo=" + activo +
                '}';
    }
}
