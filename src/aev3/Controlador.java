package aev3;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Clase Controlador en el patrón MVC para la aplicación AEV3.
 * Esta clase se encarga de manejar las interacciones entre la Vista y el Modelo.
 */
public class Controlador   {
    private Modelo modelo;
    private Vista vista;
    boolean isAuthenticated = false; 

    
    /**
     * Constructor para Controlador.
     * Inicializa el modelo y la vista asociados con este controlador.
     *
     * @param modelo Objeto Modelo que maneja la lógica de negocio.
     * @param vista  Objeto Vista que maneja la interfaz de usuario.
     */
    public Controlador(Modelo modelo, Vista vista) {
        this.modelo = modelo;
        this.vista = vista;
        control();       
    }

    /**
     * Configura y maneja las acciones y eventos de la interfaz de usuario.
     * Este método establece los listeners y define las acciones que deben
     * realizarse en respuesta a los eventos de la interfaz de usuario.
     */
    public void control() {
    	vista.btnIniciarSesion.setBackground(Color.RED);
    	modelo.guardarImagenesComoJPG();
    	vista.setEnabledComponentsInPanel(false);
    	
    	vista.getBtnIniciarSesion().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent a) {
            	vista.setEnabledComponentsInPanel(true);
                try {
                    String username = vista.getTextUsuario().getText();
                    String password = vista.getTextContrasenya().getText();
                    // Comprueba las credenciales en la base de datos
                    isAuthenticated = modelo.autenticarUsuario(username, password);
                    if (isAuthenticated) {
                    	vista.btnIniciarSesion.setBackground(Color.GREEN);
		                JOptionPane.showMessageDialog(null, "Te has conectado correctamente a la base de datos.");
                    } else {
                        JOptionPane.showMessageDialog(new JFrame(), "Credenciales inválidas.", "ERROR", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(new JFrame(), ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    	
	
    	vista.getBtnCrearUsuario().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent a) {
                try {
                    String username = vista.getTextUsuario().getText();
                    String password = vista.getTextContrasenya().getText();
                    // Comprueba las credenciales en la base de datos
                    isAuthenticated = modelo.registrarUsuario(username, password);
                    if (isAuthenticated) {
                    	vista.setEnabledComponentsInPanel(true);

                    	vista.btnIniciarSesion.setBackground(Color.GREEN);
		                JOptionPane.showMessageDialog(null, "Te has creado el usuario y conectado automáticamente a la base de datos.");
                    } else {
                        JOptionPane.showMessageDialog(new JFrame(), "Credenciales inválidas.Intenta con otras credenciales", "ERROR", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(new JFrame(), ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    	
	
        vista.getBtnCerrarSesion().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent a) {
                int confirm = JOptionPane.showConfirmDialog(null, "¿Está seguro de que desea cerrar la conexión con la base de datos?", "Confirmar Cierre de Conexión", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    isAuthenticated = modelo.cerrarSesion();
                	vista.btnIniciarSesion.setBackground(Color.RED);
                    vista.tableroPanel.removeAll();
                    vista.tableroPanel.revalidate();
                    vista.tableroPanel.repaint();
                	vista.setEnabledComponentsInPanel(false);
                }
            }
        });
    	

        vista.getBtnEmpezarJuego().addActionListener(new ActionListener() {                   
            public void actionPerformed(ActionEvent a) {
        	    vista.scrollPaneRecords.setVisible(false);
                vista.iniciarCronometro();
                String dificultad = vista.rdbtn4x4.isSelected() ? "4x4" : "2x4";
                List<String> imagenesParaTablero = modelo.prepararJuego(dificultad);
                vista.mostrarTablero(imagenesParaTablero, dificultad);
            }
        });    
	       
        
        vista.getBtnGuardarRecord().addActionListener(new ActionListener() {                   
            public void actionPerformed(ActionEvent a) {
               String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
               String usuario = vista.getTextUsuario().getText();
               int dificultad = vista.rdbtn4x4.isSelected() ? 16 : 8;
               int duracion = vista.tiempoTranscurrido;    
               modelo.guardarRecordBD(timestamp, usuario, dificultad, duracion);
            }
        }); 
        
      
        vista.getBtnRecords().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				 vista.tableroPanel.removeAll();				
				 modelo.mostrarRecordsPorDificultad(8);  
				 modelo.mostrarRecordsPorDificultad(16); 
			}
		});            
    }   
}
