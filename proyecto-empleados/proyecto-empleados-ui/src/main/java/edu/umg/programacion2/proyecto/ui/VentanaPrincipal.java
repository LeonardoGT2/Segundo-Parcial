package edu.umg.programacion2.proyecto.ui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import edu.umg.programacion2.proyecto.dao.EmpleadoDAO;
import edu.umg.programacion2.proyecto.modelo.Empleado;

/**
 * Ventana principal: lista de empleados (JTable) + formulario + botones
 * de acción. Esta clase SOLO conoce Swing y el DAO de -core — nunca
 * importa java.sql.* directamente, para mantener la UI desacoplada de JDBC.
 */
public class VentanaPrincipal extends JFrame {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();

    private JTable tablaEmpleados;
    private DefaultTableModel modeloTabla;

    private JTextField txtNombre;
    private JTextField txtDepartamento;
    private JTextField txtSalario;
    private JTextField txtFecha;
    private JCheckBox chkActivo;
    private JComboBox<String> cmbTipoContrato;
    private JLabel lblTotales;
    private JLabel lblIdSeleccionado;

    /** id del empleado actualmente seleccionado en la tabla; -1 significa "ninguno / nuevo". */
    private int idSeleccionado = -1;

    public VentanaPrincipal() {
        super("Gestión de Empleados");
        construirInterfaz();
        cargarEmpleados();
    }

    // -----------------------------------------------------------------
    // Construcción de la interfaz
    // -----------------------------------------------------------------

    private void construirInterfaz() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        add(construirPanelTabla(), BorderLayout.CENTER);
        add(construirPanelFormulario(), BorderLayout.SOUTH);
        
    }

    private JScrollPane construirPanelTabla() {
        String[] columnas = {"ID", "Nombre completo", "Departamento", "Salario", "Fecha contratación", "Activo", "Tipo de contrato"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                // La tabla es de solo lectura: toda edición pasa por el formulario,
                // así garantizamos que se valide antes de tocar la BD.
                return false;
            }
        };

        tablaEmpleados = new JTable(modeloTabla);
        tablaEmpleados.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tablaEmpleados.getSelectionModel().addListSelectionListener(evento -> {
            if (!evento.getValueIsAdjusting()) {
                cargarFilaSeleccionadaEnFormulario();
            }
        });

        return new JScrollPane(tablaEmpleados);
    }

    private JPanel construirPanelFormulario() {
        JPanel contenedor = new JPanel(new BorderLayout(5, 5));
        contenedor.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel campos = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.HORIZONTAL;

        lblIdSeleccionado = new JLabel("Nuevo empleado (sin seleccionar)");

        txtNombre = new JTextField(20);
        txtDepartamento = new JTextField(20);
        txtSalario = new JTextField(20);
        txtFecha = new JTextField("yyyy-MM-dd", 20);
        chkActivo = new JCheckBox("Activo", true);
        cmbTipoContrato = new JComboBox<>();
        cmbTipoContrato.addItem("Seleccione...");
        for (String tipo : Empleado.TIPOS_CONTRATO) {
            cmbTipoContrato.addItem(tipo);
        }

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        campos.add(lblIdSeleccionado, c);
        c.gridwidth = 1;

        agregarFila(campos, c, 1, "Nombre completo:", txtNombre);
        agregarFila(campos, c, 2, "Departamento:", txtDepartamento);
        agregarFila(campos, c, 3, "Salario mensual:", txtSalario);
        agregarFila(campos, c, 4, "Fecha contratación (yyyy-MM-dd):", txtFecha);
        agregarFila(campos, c, 5, "Tipo de contrato:", cmbTipoContrato);

        c.gridx = 0;
        c.gridy = 6;
        c.gridwidth = 2;
        campos.add(chkActivo, c);
        
        lblTotales = new JLabel("Totales: presiona «Ver totales»");
        c.gridx = 0;
        c.gridy = 7;
        c.gridwidth = 2;
        campos.add(lblTotales, c);
        
        JPanel botones = new JPanel(new GridLayout(1, 6, 8, 0));
        JButton btnNuevo = new JButton("Nuevo");
        JButton btnCrear = new JButton("Crear");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnRefrescar = new JButton("Refrescar");
        JButton btnTotales = new JButton("Ver totales");

        btnNuevo.addActionListener(e -> limpiarFormulario());
        btnCrear.addActionListener(e -> crearEmpleado());
        btnActualizar.addActionListener(e -> actualizarEmpleado());
        btnEliminar.addActionListener(e -> eliminarEmpleado());
        btnRefrescar.addActionListener(e -> cargarEmpleados());
        btnTotales.addActionListener(e -> verTotales());

        botones.add(btnNuevo);
        botones.add(btnCrear);
        botones.add(btnActualizar);
        botones.add(btnEliminar);
        botones.add(btnRefrescar);
        botones.add(btnTotales);

        contenedor.add(campos, BorderLayout.CENTER);
        contenedor.add(botones, BorderLayout.SOUTH);
        return contenedor;
    }

    private void agregarFila(JPanel panel, GridBagConstraints c, int fila, String etiqueta, JComponent campo) {
        c.gridx = 0;
        c.gridy = fila;
        panel.add(new JLabel(etiqueta, SwingConstants.RIGHT), c);
        c.gridx = 1;
        panel.add(campo, c);
    }

    // -----------------------------------------------------------------
    // Carga de datos (Read)
    // -----------------------------------------------------------------

    private void cargarEmpleados() {
        try {
            List<Empleado> empleados = empleadoDAO.listarTodos();
            modeloTabla.setRowCount(0);
            for (Empleado emp : empleados) {
                modeloTabla.addRow(new Object[]{
                        emp.getId(),
                        emp.getNombreCompleto(),
                        emp.getDepartamento(),
                        "Q" + emp.getSalarioMensual().toPlainString(),
                        emp.getFechaContratacion().format(FORMATO_FECHA),
                        emp.isActivo() ? "Sí" : "No",
                        emp.getTipoContrato()
                });
            }
        } catch (SQLException ex) {
            mostrarError("No se pudo cargar la lista de empleados.", ex);
        }
    }

    private void cargarFilaSeleccionadaEnFormulario() {
        int filaVista = tablaEmpleados.getSelectedRow();
        if (filaVista < 0) {
            return;
        }
        int id = (int) modeloTabla.getValueAt(filaVista, 0);

        try {
            Optional<Empleado> encontrado = empleadoDAO.buscarPorId(id);
            if (encontrado.isPresent()) {
                Empleado emp = encontrado.get();
                idSeleccionado = emp.getId();
                lblIdSeleccionado.setText("Empleado #" + emp.getId() + " seleccionado");
                txtNombre.setText(emp.getNombreCompleto());
                txtDepartamento.setText(emp.getDepartamento());
                txtSalario.setText(emp.getSalarioMensual().toPlainString());
                txtFecha.setText(emp.getFechaContratacion().format(FORMATO_FECHA));
                chkActivo.setSelected(emp.isActivo());
                cmbTipoContrato.setSelectedItem(emp.getTipoContrato());
            } else {
                // La fila estaba en la tabla pero ya no existe en la BD (por ejemplo,
                // otro usuario la eliminó); simplemente refrescamos la lista.
                cargarEmpleados();
            }
        } catch (SQLException ex) {
            mostrarError("No se pudo cargar el empleado seleccionado.", ex);
        }
    }

    // -----------------------------------------------------------------
    // Create
    // -----------------------------------------------------------------

    private void crearEmpleado() {
        Empleado nuevo = leerFormularioValidado();
        if (nuevo == null) {
            return; // la validación ya mostró el mensaje correspondiente
        }
        try {
            empleadoDAO.crear(nuevo);
            JOptionPane.showMessageDialog(this, "Empleado registrado correctamente.");
            limpiarFormulario();
            cargarEmpleados();
        } catch (SQLException ex) {
            mostrarError("No se pudo registrar el empleado.", ex);
        }
    }

    // -----------------------------------------------------------------
    // Update
    // -----------------------------------------------------------------

    private void actualizarEmpleado() {
        if (idSeleccionado < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona primero un empleado en la tabla para actualizarlo.",
                    "Ningún empleado seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Empleado editado = leerFormularioValidado();
        if (editado == null) {
            return;
        }
        editado.setId(idSeleccionado);

        try {
            boolean actualizado = empleadoDAO.actualizar(editado);
            if (actualizado) {
                JOptionPane.showMessageDialog(this, "Empleado actualizado correctamente.");
                limpiarFormulario();
                cargarEmpleados();
            } else {
                JOptionPane.showMessageDialog(this,
                        "El empleado ya no existe (puede que otro usuario lo haya eliminado).",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
                cargarEmpleados();
            }
        } catch (SQLException ex) {
            mostrarError("No se pudo actualizar el empleado.", ex);
        }
    }

    // -----------------------------------------------------------------
    // Delete
    // -----------------------------------------------------------------

    private void eliminarEmpleado() {
        if (idSeleccionado < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona primero un empleado en la tabla para eliminarlo.",
                    "Ningún empleado seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Seguro que deseas eliminar al empleado #" + idSeleccionado + "? "
                        + "Esta acción borra la fila por completo y no se puede deshacer.",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean eliminado = empleadoDAO.eliminar(idSeleccionado);
            if (eliminado) {
                JOptionPane.showMessageDialog(this, "Empleado eliminado correctamente.");
            } else {
                JOptionPane.showMessageDialog(this,
                        "El empleado ya no existía.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
            limpiarFormulario();
            cargarEmpleados();
        } catch (SQLException ex) {
            mostrarError("No se pudo eliminar el empleado.", ex);
        }
    }

    // -----------------------------------------------------------------
    // Validación (antes de tocar la BD) y utilidades de formulario
    // -----------------------------------------------------------------

    /**
     * Lee y valida todos los campos del formulario. Si algo es inválido,
     * muestra el mensaje correspondiente y devuelve null (no llega a
     * construirse el objeto, y por lo tanto nunca se llama al DAO).
     */
    private Empleado leerFormularioValidado() {
        String nombre = txtNombre.getText().trim();
        String departamento = txtDepartamento.getText().trim();
        String salarioTexto = txtSalario.getText().trim();
        String fechaTexto = txtFecha.getText().trim();

        if (nombre.isEmpty()) {
            mostrarValidacion("El nombre completo no puede estar vacío.");
            return null;
        }
        if (departamento.isEmpty()) {
            mostrarValidacion("El departamento no puede estar vacío.");
            return null;
        }

        BigDecimal salario;
        try {
            salario = new BigDecimal(salarioTexto);
        } catch (NumberFormatException ex) {
            mostrarValidacion("El salario debe ser un número válido (ejemplo: 8500.00).");
            return null;
        }
        if (salario.compareTo(BigDecimal.ZERO) <= 0) {
            mostrarValidacion("El salario debe ser mayor a cero.");
            return null;
        }

        LocalDate fecha;
        try {
            fecha = LocalDate.parse(fechaTexto, FORMATO_FECHA);
        } catch (DateTimeParseException ex) {
            mostrarValidacion("La fecha de contratación debe tener el formato yyyy-MM-dd (ejemplo: 2024-03-15).");
            return null;
        }
        if (fecha.isAfter(LocalDate.now())) {
            mostrarValidacion("La fecha de contratación no puede ser una fecha futura.");
            return null;
        }

        String tipoContrato = (String) cmbTipoContrato.getSelectedItem();
        if (tipoContrato == null || !Arrays.asList(Empleado.TIPOS_CONTRATO).contains(tipoContrato)) {
            mostrarValidacion("Selecciona un tipo de contrato válido (Temporal, Permanente o Por hora).");
            return null;
        }

        Empleado empleado = new Empleado(nombre, departamento, salario, fecha, chkActivo.isSelected());
        empleado.setTipoContrato(tipoContrato);
        return empleado;
    }
    private void verTotales() {
        try {
            List<Empleado> lista = empleadoDAO.listarTodos();

            if (lista.isEmpty()) {
                lblTotales.setText("Totales: no hay empleados registrados.");
                JOptionPane.showMessageDialog(this,
                        "No hay empleados registrados para calcular totales.",
                        "Sin datos", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            BigDecimal suma = BigDecimal.ZERO;
            for (Empleado emp : lista) {
                suma = suma.add(emp.getSalarioMensual());
            }
            BigDecimal promedio = suma.divide(
                    BigDecimal.valueOf(lista.size()), 2, RoundingMode.HALF_UP);

            lblTotales.setText("Total: Q" + suma.setScale(2, RoundingMode.HALF_UP).toPlainString()
                    + "   |   Promedio: Q" + promedio.toPlainString()
                    + "   (" + lista.size() + " empleados)");
        } catch (SQLException ex) {
            mostrarError("No se pudieron calcular los totales.", ex);
        }
    }

    private void limpiarFormulario() {
        // -----------------------------------------------------------------
        // Totales (mejora #9)
        // -----------------------------------------------------------------

        idSeleccionado = -1;
        lblIdSeleccionado.setText("Nuevo empleado (sin seleccionar)");
        txtNombre.setText("");
        txtDepartamento.setText("");
        txtSalario.setText("");
        txtFecha.setText("");
        chkActivo.setSelected(true);
        cmbTipoContrato.setSelectedIndex(0);
        tablaEmpleados.clearSelection();
    }

    private void mostrarValidacion(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Datos inválidos", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Muestra un error de forma amigable, sin exponer el stacktrace completo
     * al usuario (solo el mensaje de la excepción, que ya suele ser legible).
     */
    private void mostrarError(String mensajeContexto, SQLException ex) {
        JOptionPane.showMessageDialog(this,
                mensajeContexto + "\nDetalle: " + ex.getMessage(),
                "Error de base de datos", JOptionPane.ERROR_MESSAGE);
    }
}
