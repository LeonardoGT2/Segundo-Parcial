package edu.umg.programacion2.proyecto;

import edu.umg.programacion2.proyecto.ui.VentanaPrincipal;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Punto de entrada de la aplicación. Su único trabajo es lanzar la ventana
 * principal en el Event Dispatch Thread de Swing (así debe arrancar toda
 * UI de Swing, para que sea segura frente a hilos).
 */
public class MainUI {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Si el look and feel del sistema no está disponible, seguimos con el
            // look and feel por defecto de Swing; no es un error que deba detener la app.
        }

        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal();
            ventana.setVisible(true);
        });
    }
}
