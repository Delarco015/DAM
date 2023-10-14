package aev1;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import java.awt.Color;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Vista extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtDirectorio;
	private JTextField textModificar;
	private JTextField textFichero;
	private JTextField textBuscar;
	private JTextField textReemplazar;

	/**
	 * @ Launch the application.
	 */
	public static void main(String[] args)   {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Vista frame = new Vista();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}	
	
	/**
	 * @ Create the frame.
	 */
	public Vista() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 993, 680);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		
		setTitle("Ventana AEV1"); //Modifica el título de la ventana
		setLocationRelativeTo(null); //Abre la ventana en el centro de la pantalla.
		
		contentPane.setLayout(null);		
		
		// Areas de texto
		txtDirectorio = new JTextField();
		txtDirectorio.setBounds(205, 26, 394, 35);
		contentPane.add(txtDirectorio);
		txtDirectorio.setColumns(10);
		
		textFichero = new JTextField();
		textFichero.setBounds(145, 80, 453, 35);
		textFichero.setColumns(10);
		contentPane.add(textFichero);

		textModificar = new JTextField();
		textModificar.setBounds(77, 126, 890, 35);
		contentPane.add(textModificar);
		textModificar.setColumns(10);
		
		textBuscar = new JTextField();
		textBuscar.setToolTipText("");
		textBuscar.setColumns(10);
		textBuscar.setBounds(501, 238, 215, 40);
		contentPane.add(textBuscar);
		
		textReemplazar = new JTextField();
		textReemplazar.setToolTipText("");
		textReemplazar.setColumns(10);
		textReemplazar.setBounds(726, 238, 215, 40);
		contentPane.add(textReemplazar);
		
		/**
		 * Paneles de texto
		 */
		JScrollPane scrollPane1 = new JScrollPane();
		scrollPane1.setBounds(27, 298, 917, 154);
		contentPane.add(scrollPane1);
		
		JTextArea textArea1 = new JTextArea(); 
		scrollPane1.setViewportView(textArea1);

		
		JScrollPane scrollPane2 = new JScrollPane();
		scrollPane2.setBounds(27, 463, 915, 152);
		contentPane.add(scrollPane2);
		
		JTextArea textArea2 = new JTextArea();
		scrollPane2.setViewportView(textArea2);

		
		
				
		/**
		 * Botones
		 */
			
		JButton btnInformacion = new JButton("Información");
		btnInformacion.addActionListener(new ActionListener() {
		    /**
		     * @param El boton informacion coge de txtDirectorioOFichero la ruta y muestra en el panel textArea el contenido del directorio, y si hay un fichero en textFichero, el boton muestra la información del fichero y no del directorio.
		     */
		    public void actionPerformed(ActionEvent e) {
		        String rutaDirectorio = txtDirectorio.getText();
		        String nombreFichero = textFichero.getText();

		        // Crea la ruta completa del fichero
		        String rutaCompleta = rutaDirectorio + "/" + nombreFichero;

		        File directorio = new File(rutaDirectorio);
		        File fichero = new File(rutaCompleta);

		        if (!nombreFichero.isEmpty() && fichero.exists() && fichero.isFile()) {
		            // Si se ha especificado un fichero y existe, muestra la informacion del fichero
		            StringBuilder infoFichero = new StringBuilder();
		            infoFichero.append("Nombre: ").append(fichero.getName()).append("\n");
		            infoFichero.append("Ruta: ").append(fichero.getAbsolutePath()).append("\n");
		            infoFichero.append("Tamaño: ").append(fichero.length()).append(" bytes\n");
		            infoFichero.append("Permiso lectura: ").append(fichero.canRead()).append("\n");
		            infoFichero.append("Permiso escritura: ").append(fichero.canWrite()).append("\n");
		            infoFichero.append("Permiso ejecucion: ").append(fichero.canExecute()).append("\n");
		            infoFichero.append("Fecha ultima modificacion: ").append(new Date(fichero.lastModified()));

		            textArea1.setText(infoFichero.toString());
		        } else if (directorio.exists() && directorio.isDirectory()) {
		            // Si solo se ha especificado un directorio y es válido, muestra el contenido del directorio
		            File[] archivos = directorio.listFiles();  // Listar los archivos/directorios
		            StringBuilder contenido = new StringBuilder();
		            
		            for (File archivo : archivos) {
		                contenido.append(archivo.getName()).append("\n");
		            }

		            textArea1.setText(contenido.toString());  // Mostrar el contenido en la JTextArea
		        } else {
		            JOptionPane.showMessageDialog(null, "La ruta proporcionada no es válida.");
		        }
		    }
		});
		btnInformacion.setBounds(624, 26, 125, 35);
		contentPane.add(btnInformacion);
		

		
		
		JButton btnContenido = new JButton("Contenido");
		btnContenido.addActionListener(new ActionListener() {
		    /**
		     * @param El botón Contenido muestra el contenido en textArea1 del fichero de textFichero .
		     */
		    public void actionPerformed(ActionEvent e) {
		        String nombreFichero = textFichero.getText();
		        File archivo = new File(txtDirectorio.getText(), nombreFichero);

		        if (archivo.exists() && !archivo.isDirectory()) {
		            StringBuilder contenido = new StringBuilder();
		            try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
		                String linea;
		                while ((linea = br.readLine()) != null) {
		                    contenido.append(linea).append("\n");
		                }
		                textArea1.setText(contenido.toString());
		            } catch (IOException ex) {
		                JOptionPane.showMessageDialog(null, "Hubo un error al leer el fichero.");
		            }
		        } else {
		            JOptionPane.showMessageDialog(null, "El fichero especificado no existe o es un directorio.");
		        }
		    }
		});
		btnContenido.setBounds(624, 80, 125, 35);
		contentPane.add(btnContenido);		
		
		
		
		
		JButton btnCrear = new JButton("CREAR");
		btnCrear.addActionListener(new ActionListener() {
		    /**
		     * @param Crea un fichero en la ruta de txtDirectorio con el nombre de textFichero.
		     */
		    public void actionPerformed(ActionEvent e) {
		        String nombreFichero = textFichero.getText();
		        String rutaDirectorio = txtDirectorio.getText();

		        // Crear la ruta completa del nuevo fichero
		        String rutaCompleta = rutaDirectorio + File.separator + nombreFichero; // File.separator es lo mismo que "/"

		        File nuevoFichero = new File(rutaCompleta);

		        try {
		            if (!nuevoFichero.exists()) { // Verificar que el fichero no exista antes
		                if (nuevoFichero.createNewFile()) { 
		                    JOptionPane.showMessageDialog(null, "Fichero creado con éxito.");
		                } else {
		                    JOptionPane.showMessageDialog(null, "Error al crear el fichero.");
		                }
		            } else {
		                JOptionPane.showMessageDialog(null, "El fichero ya existe.");
		            }
		        } catch (IOException ex) {
		            JOptionPane.showMessageDialog(null, "Error al crear el fichero: " + ex.getMessage());
		        }
		    }
		});
		btnCrear.setBounds(27, 193, 97, 23);
		contentPane.add(btnCrear);
		
		
		
		JButton btnRenombrar = new JButton("RENOMBRAR");
		btnRenombrar.addActionListener(new ActionListener() {
		    /**
		     * @param El botón renombrar selecciona del directorio de txtDirectorio el fichero de textFichero y lo renombra por textModificar, solicitando la confirmación al usuario con un JOptionPane.showMessageDialog.
		     */
		    public void actionPerformed(ActionEvent e) {
		        String rutaDirectorio = txtDirectorio.getText();
		        String nombreOriginal = textFichero.getText();
		        String nuevoNombre = textModificar.getText();

		        if (nombreOriginal.isEmpty() || nuevoNombre.isEmpty()) {
		            JOptionPane.showMessageDialog(null, "Por favor, asegúrate de especificar el nombre original y el nuevo nombre.");
		            return;
		        }

		        // Crear la ruta completa del fichero original y del nuevo fichero
		        File ficheroOriginal = new File(rutaDirectorio + File.separator + nombreOriginal); 
		        File ficheroNuevo = new File(rutaDirectorio + File.separator + nuevoNombre);

		        if (ficheroOriginal.exists()) {
		            int respuesta = JOptionPane.showConfirmDialog(null, "¿Estás seguro de que deseas renombrar el fichero?", "Confirmación", JOptionPane.YES_NO_OPTION);

		            if (respuesta == JOptionPane.YES_OPTION) {
		                if (ficheroOriginal.renameTo(ficheroNuevo)) {
		                    JOptionPane.showMessageDialog(null, "Fichero renombrado con éxito.");
		                } else {
		                    JOptionPane.showMessageDialog(null, "Error al renombrar el fichero.");
		                }
		            }
		        } else {
		            JOptionPane.showMessageDialog(null, "El fichero original no existe.");
		        }
		    }
		});
		btnRenombrar.setBounds(134, 193, 113, 23);
		contentPane.add(btnRenombrar);		
		
				
		JButton btnCopiar = new JButton("COPIAR");
		btnCopiar.addActionListener(new ActionListener() {
		    /**
		     * @param Este botón copia el fichero de textFichero y crea una copia con el mismo contenido y con el mismo nombre + _copia.
		     */
		    public void actionPerformed(ActionEvent e) {
		        String nombreOriginal = textFichero.getText();

		        File ficheroOriginal = new File(txtDirectorio.getText(), nombreOriginal);
		        if(ficheroOriginal.exists()) {

		            int posicionPunto = nombreOriginal.indexOf(".");
		            String nombreFicheroSinExtension = nombreOriginal.substring(0, posicionPunto);
		            String extension = nombreOriginal.substring(posicionPunto);
		            String nombreFicheroCopia = nombreFicheroSinExtension + "_copia" + extension;
		            File ficheroCopia = new File(txtDirectorio.getText(), nombreFicheroCopia);

		            try {					
		                FileReader fr = new FileReader(ficheroOriginal);
		                BufferedReader br = new BufferedReader(fr);
		                FileWriter fw = new FileWriter(ficheroCopia);
		                BufferedWriter bw = new BufferedWriter(fw);

		                String linea = br.readLine();
		                while (linea != null) {
		                    bw.write(linea);
		                    bw.newLine();
		                    linea = br.readLine();
		                }
		                JOptionPane.showMessageDialog(null, "El fichero se ha copiado con exito");
		                br.close();
		                bw.close();
		                fr.close();
		                fw.close();		        	
		            } catch (Exception e2) {
		                JOptionPane.showMessageDialog(null, "Error al copiar el fichero: " + e2.getMessage());
		            }
		        } else {
		            JOptionPane.showMessageDialog(null, "El fichero a copiar no existe.");
		        }
		    }
		});
		btnCopiar.setBounds(264, 193, 89, 23);
		contentPane.add(btnCopiar);	
		
		
		
		JButton btnSuprimir = new JButton("SUPRIMIR");
		btnSuprimir.addActionListener(new ActionListener() {
		    /**
		     * @param Elimina el fichero de textFichero, solicitando la confirmación al ususario.
		     */
		    public void actionPerformed(ActionEvent e) {
		        String nombreFichero = textFichero.getText();
		        File archivoAEliminar = new File(txtDirectorio.getText(), nombreFichero);

		        if (archivoAEliminar.exists()) {
		            // Solicitar confirmación al usuario
		            int respuesta = JOptionPane.showConfirmDialog(null, 
		                "¿Está seguro de que desea eliminar el fichero '" + nombreFichero + "'?",
		                "Confirmar eliminación",
		                JOptionPane.YES_NO_OPTION);

		            if (respuesta == JOptionPane.YES_OPTION) {
		                if (archivoAEliminar.delete()) {
		                    JOptionPane.showMessageDialog(null, "Fichero eliminado con éxito.");
		                } else {
		                    JOptionPane.showMessageDialog(null, "Hubo un error al eliminar el fichero.");
		                }
		            } else {
		                JOptionPane.showMessageDialog(null, "Eliminación cancelada.");
		            }
		        } else {
		            JOptionPane.showMessageDialog(null, "El fichero especificado no existe.");
		        }
		    }
		});
		btnSuprimir.setBounds(369, 193, 97, 23);
		contentPane.add(btnSuprimir);		
		
		
		
		JButton btnEscribir = new JButton("ESCRIBIR");
		btnEscribir.addActionListener(new ActionListener() {
		    /**
		     * @param El texto que hay escrito en textBuscar se lo añade al fichero (sin reemplazar). en textArea1 aparece el contenido original y en textArea2 el contenido nuevo.
		     */
		    public void actionPerformed(ActionEvent e) {
		        String nombreFichero = textFichero.getText();
		        File archivo = new File(txtDirectorio.getText(), nombreFichero);

		        if (archivo.exists() && !archivo.isDirectory()) {
		            StringBuilder contenidoOriginal = new StringBuilder();
		            try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
		                String linea;
		                while ((linea = br.readLine()) != null) {
		                    contenidoOriginal.append(linea).append("\n");
		                }

		                // Mostrar el contenido original en textArea1
		                textArea1.setText(contenidoOriginal.toString());

		                // Añadir el texto de textBuscar al contenido original
		                String textoNuevo = textBuscar.getText();
		                contenidoOriginal.append(textoNuevo);

		                // Mostrar el contenido modificado en textArea2
		                textArea2.setText(contenidoOriginal.toString());

		                // Escribir el contenido modificado de nuevo al fichero
		                try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {
		                    bw.write(contenidoOriginal.toString());
		                }

		            } catch (IOException ex) {
		                JOptionPane.showMessageDialog(null, "Hubo un error con el fichero.");
		            }
		        } else {
		            JOptionPane.showMessageDialog(null, "El fichero no existe.");
		        }
		    }
		});
		btnEscribir.setBounds(501, 193, 89, 23);
		contentPane.add(btnEscribir);
		

		
		JButton btnBuscar = new JButton("BUSCAR");
		btnBuscar.addActionListener(new ActionListener() {
		    /**
		     * @param Dentro del fichero de textFichero busca las coincidencias con textBuscar. Y las mustra en textArea2 con las coincidencias resaltadas en amarillo.
		     */
		    public void actionPerformed(ActionEvent e) {
		        int coincidencias = 0;
		        String nombreFichero = textFichero.getText();
		        String textoABuscar = textBuscar.getText();
		        File archivo = new File(txtDirectorio.getText(), nombreFichero);

		        try {
		            FileReader fr = new FileReader(archivo);
		            BufferedReader br = new BufferedReader(fr);
		            StringBuilder contenido = new StringBuilder();
		            String linea;
		            while ((linea = br.readLine()) != null) {
		                contenido.append(linea).append("\n");
		            }
		            br.close();
		            fr.close();

		            textArea2.setText(contenido.toString());

		            DefaultHighlighter highlighter = (DefaultHighlighter) textArea2.getHighlighter();
		            DefaultHighlighter.DefaultHighlightPainter painter = 
		                new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
		            
		            int offset = contenido.indexOf(textoABuscar);
		            while (offset != -1) {
		                coincidencias++;
		                int end = offset + textoABuscar.length();
		                highlighter.addHighlight(offset, end, painter);
		                offset = contenido.indexOf(textoABuscar, end);
		            }
		            JOptionPane.showMessageDialog(null, "Número de coincidencias: " + coincidencias);

		        } catch (IOException ex) {
		            JOptionPane.showMessageDialog(null, "Error al leer el archivo.");
		        } catch (BadLocationException ble) {
		            JOptionPane.showMessageDialog(null, "Error al resaltar el texto.");
		        }
		    }
		});
		btnBuscar.setBounds(606, 193, 89, 23);
		contentPane.add(btnBuscar);

	
		
		JButton btnReemplazar = new JButton("REEMPLAZAR");
		btnReemplazar.addActionListener(new ActionListener() {
		    /**
		     * @param Reemplaza el texto buscado en textBuscar por el texto de textReemplazar y muestra el nuevo contenido en textArea2.
		     */
		    public void actionPerformed(ActionEvent e) {
		        // Obtenemos el texto actual de textArea2
		        String contenido = textArea2.getText();

		        // Obtenemos el texto a buscar y el texto por el que vamos a reemplazar
		        String textoABuscar = textBuscar.getText();
		        String textoAReemplazar = textReemplazar.getText();

		        // Reemplazamos todas las coincidencias
		        contenido = contenido.replace(textoABuscar, textoAReemplazar);

		        // Actualizamos textArea2 con el nuevo contenido
		        textArea2.setText(contenido);
		    }
		});
		btnReemplazar.setBounds(705, 193, 125, 23);
		contentPane.add(btnReemplazar);

		
		
		JButton btnGuardar = new JButton("GUARDAR");
		btnGuardar.addActionListener(new ActionListener() {
		    /**
		     * @param Una vez reemplazado o añadido al contenido del fichero. El boton guardar nos da la opción de guardar el nuevo contenido en un fichero nuevo (con nombre que nosotros le pongamos) o reescribir el existente.
		     */
		    public void actionPerformed(ActionEvent e) {
		        Object[] options = {"Reescribir", "Guardar como nuevo", "Cancelar"};
		        int eleccion = JOptionPane.showOptionDialog(null,
		            "¿Desea reescribir el archivo existente o guardar como un nuevo archivo?",
		            "Guardar cambios",
		            JOptionPane.YES_NO_CANCEL_OPTION,
		            JOptionPane.QUESTION_MESSAGE,
		            null,
		            options,
		            options[2]);

		        switch (eleccion) {
		            case 0:  // Reescribir
		                try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(txtDirectorio.getText(), textFichero.getText())))) {
		                    bw.write(textArea2.getText());
		                    JOptionPane.showMessageDialog(null, "Archivo reescrito con éxito.");
		                } catch (IOException ex) {
		                    JOptionPane.showMessageDialog(null, "Error al reescribir el archivo.");
		                }
		                break;
		            case 1:  // Guardar como nuevo
		                String nuevoNombre = JOptionPane.showInputDialog("Introduce el nombre para el nuevo archivo:");
		                if (nuevoNombre != null && !nuevoNombre.trim().isEmpty()) {
		                    File nuevoArchivo = new File(txtDirectorio.getText(), nuevoNombre);
		                    try (BufferedWriter bw = new BufferedWriter(new FileWriter(nuevoArchivo))) {
		                        bw.write(textArea2.getText());
		                        JOptionPane.showMessageDialog(null, "Archivo guardado con éxito.");
		                    } catch (IOException ex) {
		                        JOptionPane.showMessageDialog(null, "Error al guardar el nuevo archivo.");
		                    }
		                } else {
		                    JOptionPane.showMessageDialog(null, "Nombre de archivo no válido.");
		                }
		                break;
		            case 2:  // Cancelar
		                // No hacer nada
		                break;
		        }
		    }
		});
		btnGuardar.setBounds(840, 193, 104, 23);
		contentPane.add(btnGuardar);
						
			
		 /**
	     * Label
	     */
		JLabel lblDirectorio = new JLabel("DIRECTORIO");
		lblDirectorio.setBounds(115, 32, 80, 23);
		contentPane.add(lblDirectorio);
		

		JLabel lblNuevo = new JLabel("NUEVO:");
		lblNuevo.setBounds(30, 132, 47, 23);
		contentPane.add(lblNuevo);
		
		
		JLabel lblFichero = new JLabel("FICHERO");
		lblFichero.setBounds(77, 86, 58, 23);
		contentPane.add(lblFichero);	
		

	}
}
