package aev2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Controlador {
    
    private Modelo modelo;
    private Vista vista;
    boolean isAuthenticated = false;

    public Controlador(Modelo modelo, Vista vista) {
        this.modelo = modelo;
        this.vista = vista;
        control();
    }        
    public void control() {    	
    	
        vista.getBtnIniciarSesion().addActionListener(new ActionListener() {
            /**
             * Obtiene el nombre de usuario y la contraseña de los campos de texto correspondientes, los utiliza para autenticar al usuario contra la base de datos
             * @param e acción que se activa al hacer clic en el botón de iniciar sesión.
             */
            public void actionPerformed(ActionEvent e) {
                try {
                    String username = vista.getTextUsuario().getText();
                    String password = vista.getTextContrasenya().getText();

                    // Comprueba las credenciales en la base de datos
                    isAuthenticated = modelo.authenticateUser(username, password);

                    if (isAuthenticated) {
		                JOptionPane.showMessageDialog(null, "Te has conectado correctamente a la base de datos.");
                    } else {
                        JOptionPane.showMessageDialog(new JFrame(), "Credenciales inválidas.", "ERROR", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(new JFrame(), ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        vista.getBtnEjecutar().addActionListener(new ActionListener() {
            /**
             *
             */
            public void actionPerformed(ActionEvent e) {
                String sql = vista.getTextConsulta().getText().trim();
                try {
                    String resultadoSQL = modelo.ejecutarSQL(sql);
                    vista.getTextAreaSQL().setText(resultadoSQL); 
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });    
   
        vista.getBtnCerrarSesion().addActionListener(new ActionListener() {
            /**
             *Este método obtiene la consulta SQL del campo de texto correspondiente, la ejecuta utilizando el método 'ejecutarSQL' de la clase 'Modelo', y muestra el resultado en el área de texto de SQL
             */
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(null, "¿Estás seguro de que deseas cerrar sesión?", "Confirmar cierre de sesión", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    modelo.cerrarSesion(); 
                    JOptionPane.showMessageDialog(null, "Has cerrado sesión correctamente.");
                    
                }
            }
        });
    

        vista.getBtnCerrarConexion().addActionListener(new ActionListener() {
            /**
             * Si el usuario confirma, se cierra la sesión actual mediante el método 'cerrarSesion' de la clase 'Modelo'       
             */
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(null, "¿Está seguro de que desea cerrar la conexión con la base de datos?", "Confirmar Cierre de Conexión", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    modelo.cerrarConexion();
                }
            }
        });

    
    }
}

