package aev3;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.JLabel;
import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.Dimension;

public class Vista {

	 JFrame frame;
	 JPanel contentPane, tableroPanel,Login, NoLogin;
	 JTextField textUsuario, textContrasenya;
	 JButton btnIniciarSesion,botonSeleccionado, btnEmpezarJuego, btnCerrarSesion, btnMostrarRecords, btnCrearUsuario, btnGuardarRecord;
	 static JRadioButton rdbtn4x4, rdbtn2x4;
	 private static final ButtonGroup buttonGroup = new ButtonGroup();
	 private JTextArea textAreaRecords;
	 JScrollPane scrollPaneRecords;

	 private ArrayList<JButton> botonesDelTablero; 
	 
	 String primerNombreImagen = null;
	 String segundoNombreImagen= null;
	 JButton primerBotonSeleccionado = null;
	 JButton segundoBotonSeleccionado = null;
	 int numeroDeSelecciones = 0;
	 int puntuacion = 0;
	 boolean esperandoTimer = false;
	 private Timer cronometro;
	 private JLabel lblCronometro;
	 int tiempoTranscurrido = 0; // Tiempo transcurrido en segundo


    /**
     * Muestra el tablero de juego con los botones correspondientes a las imágenes.
     * 
     * @param nombresDeImagenes Lista de nombres de las imágenes a mostrar en el tablero.
     * @param dificultad        La dificultad del juego, afecta el tamaño del tablero.
     */
	 public void mostrarTablero(List<String> nombresDeImagenes, String dificultad) {
		    int filas = dificultad.equals("2x4") ? 2 : 4;
		    int columnas = 4;

		    tableroPanel.setLayout(new GridLayout(filas, columnas));
		    tableroPanel.removeAll();
		    botonesDelTablero.clear();

		    for (String nombreImagen : nombresDeImagenes) {
		        JButton boton = new JButton();
		        boton.setActionCommand(nombreImagen);

		        ImageIcon icono = null;
		        try {
		            BufferedImage imagen = ImageIO.read(new File("img/" + nombreImagen));
		            if (imagen != null) {
		                icono = new ImageIcon(imagen);
		            } else {
		                System.out.println("Imagen no encontrada: " + nombreImagen);
		            }
		        } catch (IOException e) {
		            e.printStackTrace();
		            System.out.println("Error al leer la imagen: " + nombreImagen);
		        }

		        boton.putClientProperty("iconoImagen", icono);

		        boton.addActionListener(new ActionListener() {
		            public void actionPerformed(ActionEvent evento) {
		            	 if (esperandoTimer) {
		                     return; // No hacer nada si el timer está activo
		                 }
		                JButton botonSeleccionado = (JButton) evento.getSource();
		                ImageIcon iconoSeleccionado = (ImageIcon) botonSeleccionado.getClientProperty("iconoImagen");
		                botonSeleccionado.setIcon(iconoSeleccionado); 

		                String nombreImagenSeleccionada = botonSeleccionado.getActionCommand();// Guarda el nombre de la imagen seleccionada en una variable

		                if (numeroDeSelecciones == 0) {
		                    primerBotonSeleccionado = botonSeleccionado;
		                    primerNombreImagen = nombreImagenSeleccionada;
		                    numeroDeSelecciones++;
		                } else if (numeroDeSelecciones == 1) {
		                	segundoBotonSeleccionado = botonSeleccionado;
		                	segundoNombreImagen = nombreImagenSeleccionada;
		                	
		                    if (primerNombreImagen.equals(segundoNombreImagen) && primerBotonSeleccionado != segundoBotonSeleccionado) {
		                   	  primerNombreImagen = null;
		                	  segundoNombreImagen= null;
		                	  primerBotonSeleccionado = null;
		                	  segundoBotonSeleccionado = null;
		                    	
		                    	puntuacion++;
		                        System.out.println("COINCIDEN");
		                        System.out.println("Puntuación: " + puntuacion);
		                        numeroDeSelecciones = 0;
		                    } else {
		                        System.out.println("NO COINCIDEN");
		                        esperandoTimer = true;
		                        Timer timer = new Timer(500, new ActionListener() {
		                            @Override
		                            public void actionPerformed(ActionEvent ae) {
				                        primerNombreImagen = null;
					                	segundoNombreImagen= null;
				                        primerBotonSeleccionado.setIcon(null);
				                        segundoBotonSeleccionado.setIcon(null);
				                        esperandoTimer = false;
		                            }
		                        });
		                        timer.setRepeats(false); 
		                        timer.start(); 
		                        numeroDeSelecciones = 0;
		                    }
		                }

		                // Verificar si el juego ha terminado
		                if (puntuacion == 4 && dificultad.equals("2x4")) {		                	
		                    System.out.println("¡Juego terminado!");
		                    puntuacion = 0;
		                    cronometro.stop();		   
		                } else if (puntuacion == 8 && dificultad.equals("4x4")) {
		                    System.out.println("¡Juego terminado!");
		                    puntuacion = 0;
		                    cronometro.stop();
		                }
		            }
		        });

		        tableroPanel.add(boton);
		    }

		    tableroPanel.revalidate();
		    tableroPanel.repaint();
		}

	    /**
	     * Inicia el cronómetro que cuenta el tiempo transcurrido durante el juego.
	     */
	    public void iniciarCronometro() {
	        tiempoTranscurrido = 0;
	        cronometro.start();
	    }
	    
	    /**
	     * Establece la habilitación de los componentes dentro del panel NoLogin.
	     * 
	     * @param enabled Estado de habilitación para los componentes del panel.
	     */
	    public void setEnabledComponentsInPanel(boolean enabled) {
	        for (Component comp : NoLogin.getComponents()) {
	            comp.setEnabled(enabled);
	        }
	    }

		/**
		 * @ Create the frame.
		 */
		public Vista() {

			
	    botonesDelTablero = new ArrayList<>();


		frame = new JFrame();
		frame.setBounds(100, 100, 1039, 809);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
	    contentPane = new JPanel();
	    frame.setContentPane(contentPane);
	    frame.setTitle("Ventana AEV3");
	    frame.setLocationRelativeTo(null);
		contentPane.setLayout(null);

		
		//Login
		Login = new JPanel();
		Login.setBounds(10, 11, 392, 71);
		contentPane.add(Login);
		Login.setLayout(null);
		
		tableroPanel = new JPanel();
		tableroPanel.setBounds(10, 93, 1003, 666);
		contentPane.add(tableroPanel);
		tableroPanel.setLayout(null);
		
		
		textAreaRecords =new JTextArea();
		textAreaRecords.setEditable(false); // Hacerlo no editable
		scrollPaneRecords = new JScrollPane(textAreaRecords);
		scrollPaneRecords.setBounds(10, 11, 983, 644);
		tableroPanel.add(scrollPaneRecords);
		scrollPaneRecords.setVisible(false);
		
		textUsuario = new JTextField();
		textUsuario.setBounds(84, 8, 169, 20);
		Login.add(textUsuario);
		textUsuario.setColumns(10);
		
		textContrasenya = new JTextField();
		textContrasenya.setColumns(10);
		textContrasenya.setBounds(84, 39, 169, 20);
		Login.add(textContrasenya);
		
		JLabel lblUsuario = new JLabel("Usuario:");
		lblUsuario.setBounds(10, 11, 62, 14);
		Login.add(lblUsuario);
		
		JLabel lblContrasenya = new JLabel("Contraseña: ");
		lblContrasenya.setBounds(10, 42, 76, 14);
		Login.add(lblContrasenya);
		
		btnIniciarSesion = new JButton("Iniciar Sesión");
		btnIniciarSesion.setBounds(263, 7, 122, 23);
		Login.add(btnIniciarSesion);
		
		
		btnCrearUsuario = new JButton("Crear Usuario");
		btnCrearUsuario.setBounds(263, 38, 122, 23);
		Login.add(btnCrearUsuario);
		
        
		//NoLogin
		NoLogin = new JPanel();
		NoLogin.setBounds(405, 11, 608, 71);
		contentPane.add(NoLogin);
        NoLogin.setLayout(null);
		
        NoLogin.setEnabled(false);
        
        JLabel lblDificultad = new JLabel("Dificultad:");
        lblDificultad.setBounds(0, 11, 62, 14);
        NoLogin.add(lblDificultad);
        
		
		btnEmpezarJuego = new JButton("Empezar Juego");
		btnEmpezarJuego.setBounds(66, 21, 126, 23);
		NoLogin.add(btnEmpezarJuego);
		
		rdbtn4x4 = new JRadioButton("4x4");
		buttonGroup.add(rdbtn4x4);
		rdbtn4x4.setFont(new Font("Tahoma", Font.PLAIN, 12));
		rdbtn4x4.setBounds(0, 47, 45, 17);
		NoLogin.add(rdbtn4x4);
		
		rdbtn2x4 = new JRadioButton("2x4");
		buttonGroup.add(rdbtn2x4);
		rdbtn2x4.setSelected(true);
		rdbtn2x4.setFont(new Font("Tahoma", Font.PLAIN, 12));
		rdbtn2x4.setBounds(0, 26, 45, 18);
		NoLogin.add(rdbtn2x4);

		btnGuardarRecord = new JButton("Guardar Record");
		btnGuardarRecord.setBounds(202, 21, 126, 23);
		NoLogin.add(btnGuardarRecord);

		btnMostrarRecords = new JButton("Mostrar Records");
		btnMostrarRecords.setBounds(338, 21, 134, 23);
		NoLogin.add(btnMostrarRecords);		
		
		lblCronometro = new JLabel("Tiempo: 0");
		lblCronometro.setMaximumSize(new Dimension(50, 14));
		lblCronometro.setBounds(98, 48, 71, 23);
		NoLogin.add(lblCronometro);
		
		cronometro = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				tiempoTranscurrido++;
				lblCronometro.setText("Tiempo: " + tiempoTranscurrido);
			}
		});

		btnCerrarSesion = new JButton("Cerrar Sesión");
		btnCerrarSesion.setBounds(482, 21, 119, 23);
		NoLogin.add(btnCerrarSesion);
		
		frame.setVisible(true);
					
	}

	
	// Getters y Setters
	public JButton getBtnGuardarRecord() {
			return btnGuardarRecord;
		}
	public void setBtnGuardarRecord(JButton btnGuardarRecord) {
		this.btnGuardarRecord = btnGuardarRecord;
		}
	public JButton getBtnEmpezarJuego() {
			return btnEmpezarJuego;
		}
	public void setBtnEmpezarJuego(JButton btnEmpezarJuego) {
		this.btnEmpezarJuego = btnEmpezarJuego;
	}
	public JFrame getFrame() {
		return frame;
	}
	public void setFrame(JFrame frame) {
		this.frame = frame;
	}
	public JPanel getContentPane() {
		return contentPane;
	}
	public void setContentPane(JPanel contentPane) {
		this.contentPane = contentPane;
	}
	public JTextField getTextUsuario() {
		return textUsuario;
	}
	public void setTextUsuario(JTextField textUsuario) {
		this.textUsuario = textUsuario;
	}
	public JTextField getTextContrasenya() {
		return textContrasenya;
	}
	public void setTextContrasenya(JTextField textContrasenya) {
		this.textContrasenya = textContrasenya;
	}
	public JButton getBtnIniciarSesion() {
		return btnIniciarSesion;
	}
	public void setBtnIniciarSesion(JButton btnIniciarSesion) {
		this.btnIniciarSesion = btnIniciarSesion;
	}
	public JButton getBtnRecords() {
		return btnMostrarRecords;
	}
	public void setBtnRecords(JButton btnRecords) {
		this.btnMostrarRecords = btnRecords;
	}
	public JButton getBtnCrearUsuario() {
		return btnCrearUsuario;
	}
	public void setBtnCrearUsuario(JButton btnCrearUsuario) {
		this.btnCrearUsuario = btnCrearUsuario;
	}
	public JButton getBtnCerrarSesion() {
		return btnCerrarSesion;
	}
	public void setBtnCerrarSesion(JButton btnCerrarSesion) {
		this.btnCerrarSesion = btnCerrarSesion;
	}
	public static JRadioButton getRdbtn4x4() {
		return rdbtn4x4;
	}
	public static void setRdbtn4x4(JRadioButton rdbtn4x4) {
		Vista.rdbtn4x4 = rdbtn4x4;
	}
	public static JRadioButton getRdbtn2x4() {
		return rdbtn2x4;
	}
	public static void setRdbtn2x4(JRadioButton rdbtn2x4) {
		Vista.rdbtn2x4 = rdbtn2x4;
	}
	public JPanel getTableroPanel() {
		return tableroPanel;
	}
	public void setTableroPanel(JPanel tableroPanel) {
		this.tableroPanel = tableroPanel;
	}
}
