package aev1_2;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.awt.event.ActionEvent;

public class Vista_2 extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtDirectorio;
	private JTextField txtString;
	private JTextField txtFusionar;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Vista_2 frame = new Vista_2();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Vista_2() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1213, 827);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		setTitle("Ventana AEV1_2"); //Modifica el título de la ventana
		setLocationRelativeTo(null); //Abre la ventana en el centro de la pantalla.
		contentPane.setLayout(null);
		
		 /**
	     * Areas de texto
	     */
		txtDirectorio = new JTextField();
		txtDirectorio.setColumns(10);
		txtDirectorio.setBounds(184, 58, 394, 35);
		contentPane.add(txtDirectorio);

		txtFusionar = new JTextField();
		txtFusionar.setColumns(10);
		txtFusionar.setBounds(184, 234, 394, 33);
		contentPane.add(txtFusionar);

		txtString = new JTextField();
		txtString.setBounds(184, 144, 394, 33);
		contentPane.add(txtString);
		txtString.setColumns(10);
		
		
		/**
		 * Paneles de texto
		 */
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(184, 309, 807, 385);
		contentPane.add(scrollPane);
		
		JTextArea textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		

		
		/**
		 * Botones
		 */
		JButton btnListar = new JButton("Listar");
		btnListar.addActionListener(new ActionListener() {
		    /**
		     *@param text muestra todos los ficheros que tengan extensión .txt con su nombre, extensión, tamanyo y última modificación. 
		     */
		    public void actionPerformed(ActionEvent e) {
		    	
		        String rutaDirectorio = txtDirectorio.getText();
		        File directorio = new File(rutaDirectorio);
		        
		        if (directorio.exists() && directorio.isDirectory()) {
		            // Se listan todos los archivos del directorio que terminen con ".txt", utilizando un FilenameFilter.
		            File[] archivosTxt = directorio.listFiles(new FilenameFilter() {
		                /**
		                 * @param text Método para comprobar la extensión del archivo
		                 * @param accept()
		                 * @return true si el nombre del archivo termina con ".txt", ignorando mayúsculas/minúsculas.
		                 */
		                public boolean accept(File dir, String name) {
		                   
		                    return name.toLowerCase().endsWith(".txt");
		                }
		            });		          
		          
		            StringBuilder info = new StringBuilder();
		           
		            for (File archivo : archivosTxt) {
		                // Se configura el formato de la fecha de última modificación.
		                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		                // Se añade al StringBuilder la información del archivo: nombre, extensión, tamanyo y fecha de última modificación.
		                info.append("Nombre: ").append(archivo.getName())
		                    .append(", Extensión: ").append(archivo.getName().substring(archivo.getName().lastIndexOf(".") + 1))
		                    .append(", tamanyo: ").append(archivo.length()).append(" bytes")
		                    .append(", Última Modificación: ").append(sdf.format(archivo.lastModified())).append("\n");
		            }
		            
		            textArea.setText(info.toString());
		        } else {		            
		            JOptionPane.showMessageDialog(null, "La ruta proporcionada no es válida o no es un directorio.");
		        }
		    }
		});
		btnListar.setBounds(649, 64, 89, 23);
		contentPane.add(btnListar);

			
		
		JButton btnOrdenarNombre = new JButton("Ordenar Nombre");
		btnOrdenarNombre.addActionListener(new ActionListener() {
		    /**
		     * @param text muestra los ficheros con orden ascendente o descendente.
		     */
		    public void actionPerformed(ActionEvent e) {
		        // Se muestra un diálogo con opciones para que el usuario elija cómo quiere ordenar los archivos: ascendente o descendente.
		        Object[] options = {"Ascendente", "Descendente"};
		        int eleccion = JOptionPane.showOptionDialog(null,
		            "¿En qué orden desea ordenar los archivos?",
		            "Ordenar por Nombre",
		            JOptionPane.YES_NO_CANCEL_OPTION,
		            JOptionPane.QUESTION_MESSAGE,
		            null,
		            options,
		            options[0]);

		        // Se crea un objeto File para representar el directorio cuya ruta se ha obtenido del campo de texto txtDirectorio.
		        File directorio = new File(txtDirectorio.getText());
		        
		        // Se listan todos los archivos dentro de ese directorio.
		        File[] archivos = directorio.listFiles();

		        // Se ordenan los archivos utilizando Arrays.sort con un Comparator.
		        Arrays.sort(archivos, new Comparator<File>() {
		            /**
		             * @param text Método para comparar el nombre de los ficheros.
		             * @param compare()
		             * @return dependiendo el orden que elegimos, ordena unos ficheros primero u otros.
		             */
		            public int compare(File fichero1, File fichero2) {
		  
		                if (eleccion == 0) { // ascendente.
		                    return fichero1.getName().compareTo(fichero2.getName());
		                } else { // descendente.
		                    return fichero2.getName().compareTo(fichero1.getName());
		                }
		            }
		        });

		        StringBuilder contenido = new StringBuilder();
		        
		        for (File archivo : archivos) {
		            if (archivo.isFile()) {
		                contenido.append(archivo.getName()).append("\n");
		            }
		        }		        
		        textArea.setText(contenido.toString());
		    }
		});
		btnOrdenarNombre.setBounds(649, 110, 168, 23);
		contentPane.add(btnOrdenarNombre);


		
		
		
		
		JButton btnOrdenartamanyo = new JButton("Ordenar tamanyo");

		btnOrdenartamanyo.addActionListener(new ActionListener() {
		    /**
		     * @param text muestra los ficheros por su tamanyo de bytes con orden descendente
		     */
		    public void actionPerformed(ActionEvent e) {
		        File directorio = new File(txtDirectorio.getText());
		        
		        File[] archivos = directorio.listFiles();

		        // Se ordenan los archivos por tamanyo en orden descendente
		        Arrays.sort(archivos, new Comparator<File>() {
		            /**
		             * @param text Método para comparar el tamanyo de los ficheros.
		             * @param compare()
		             * @return los ficheros ordenados por tamanyo descendente
		             */
		            public int compare(File fichero1, File fichero2) {
		                return Long.compare(fichero2.length(), fichero1.length());
		            }
		        });

		        StringBuilder contenido = new StringBuilder();
		        
		        for (File archivo : archivos) {
		            if (archivo.isFile()) {
		                contenido.append(archivo.getName()).append(" - ").append(archivo.length()).append(" bytes\n");
		            }
		        }

		        textArea.setText(contenido.toString());
		    }
		});
		btnOrdenartamanyo.setBounds(649, 144, 168, 23);
		contentPane.add(btnOrdenartamanyo);


		
			
		
		JButton btnOrdenarFecha = new JButton("Ordenar Fecha");
		btnOrdenarFecha.addActionListener(new ActionListener() {
		    /**
		     * @param text muestra los ficheros por orden de más antiguo a más actual con fecha de modificación.
		     */
		    public void actionPerformed(ActionEvent e) {
		    	
		    	File directorio = new File(txtDirectorio.getText());
		    	// Se listan todos los archivos dentro del directorio.
		        File[] archivos = directorio.listFiles();

		            Arrays.sort(archivos, new Comparator<File>() {
		                /**
		                 * @param text Método para comparar las fechas de modificación
		                 * @param compare()
		                 * @return los ficheros ordenados por fecha de modificación más antigua a más actual.
		                 */
		                public int compare(File fichero1, File fichero2) {
		                    //Orden ascendente
		                    return Long.compare(fichero1.lastModified(), fichero2.lastModified());
		                }
		            });

		            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		            StringBuilder contenido = new StringBuilder();
		            for (File archivo : archivos) {
		                if (archivo.isFile()) {
		                	
		                    String fechaModificacion = sdf.format(new Date(archivo.lastModified())); // Se formatea la fecha de última modificación del archivo.
		                    contenido.append(archivo.getName()).append(" - Última modificación: ").append(fechaModificacion).append("\n");
		                }
		            }

		            textArea.setText(contenido.toString());		        
		    }
		});
		btnOrdenarFecha.setBounds(649, 178, 168, 23);
		contentPane.add(btnOrdenarFecha);

		
			
		
		JButton btnBuscar = new JButton("BUSCAR");
		btnBuscar.addActionListener(new ActionListener() {
		    /**
		     * @param text busca el string de txtString en el contenido de todos los ficheros, y muestra cuantas coincidencias hay en cada fichero.
		     */
		    public void actionPerformed(ActionEvent e) {
		        String textoABuscar = txtString.getText();
		        
		        File directorio = new File(txtDirectorio.getText());
		        
		        // Listar todos los archivos dentro de ese directorio.
		        File[] archivos = directorio.listFiles();
		        
		        StringBuilder resultado = new StringBuilder();

		        for (File archivo : archivos) {
		        	
		            // Verificar si el objeto File es un archivo y no un directorio.
		            if (archivo.isFile()) {
		                try {
		                    // Crear un FileReader para leer el archivo.
		                    FileReader fr = new FileReader(archivo);
		                    BufferedReader br = new BufferedReader(fr); // Envolver el FileReader en un BufferedReader para leer texto de manera eficiente.

		                    String linea;

		                    int coincidencias = 0;

		                    // Leer el archivo línea por línea.
		                    while ((linea = br.readLine()) != null) {
		                        int index = linea.indexOf(textoABuscar);
		                        while (index >= 0) {
		                            coincidencias++;
		                            
		                            index = linea.indexOf(textoABuscar, index + 1);// Continuar buscando más adelante en la línea.
		                        }
		                    }

		                    br.close();
		                    fr.close();

		                    resultado.append(archivo.getName()).append(" -> ").append(coincidencias).append(" coincidencias\n");

		                } catch (IOException ex) {
		                    JOptionPane.showMessageDialog(null, "Error al leer el archivo " + archivo.getName() + "\n");
		                }
		            }
		        }
		        
		        textArea.setText(resultado.toString());
		    }
		});
		btnBuscar.setBounds(184, 188, 89, 23);
		contentPane.add(btnBuscar);

		
		
		
		
		
		JButton btnFusionar = new JButton("Fusionar");
		btnFusionar.addActionListener(new ActionListener() {
		    /**
		     * @param text Fuisona el contenido de dos ficheros en uno, y te solicita un nuevo nombre.
		     */
		    public void actionPerformed(ActionEvent e) {

		    	String fichero1 = txtString.getText();
		        String fichero2 = txtFusionar.getText();
		        File directorio = new File(txtDirectorio.getText());

		        // Crear objetos File para ambos archivos
		        File primerFichero = new File(directorio, fichero1);
		        File segundoFichero = new File(directorio, fichero2);

		        // Pedir al usuario el nombre del nuevo archivo fusionado
		        String nuevoNombre = JOptionPane.showInputDialog("Introduce el nombre para el nuevo archivo:");
		        if (nuevoNombre != null && !nuevoNombre.trim().isEmpty()) {
		            File archivoFusionado = new File(directorio, nuevoNombre);
		            StringBuilder contenidoFusionado = new StringBuilder();
		            
		            // Escribir el contenido de ambos archivos en el nuevo archivo fusionado
		            try (FileWriter fw = new FileWriter(archivoFusionado);
		                 BufferedWriter bw = new BufferedWriter(fw);
		                 FileReader fr1 = new FileReader(primerFichero);
		                 BufferedReader br1 = new BufferedReader(fr1);
		                 FileReader fr2 = new FileReader(segundoFichero);
		                 BufferedReader br2 = new BufferedReader(fr2)) {

		                String linea;
		                // Escribir el contenido del primer archivo
		                while ((linea = br1.readLine()) != null) {
		                	contenidoFusionado.append(linea).append("\n");
		                    bw.write(linea);
		                    bw.newLine();
		                }
		                // Escribir el contenido del segundo archivo
		                while ((linea = br2.readLine()) != null) {
		                	contenidoFusionado.append(linea).append("\n");
		                    bw.write(linea);
		                    bw.newLine();
		                }
		                // Mostrar el contenido fusionado en textArea
			            textArea.setText(contenidoFusionado.toString());		                
		                
		                JOptionPane.showMessageDialog(null, "Los archivos han sido fusionados correctamente.");
		            } catch (IOException ex) {
		                JOptionPane.showMessageDialog(null, "Error al fusionar los archivos: " + ex.getMessage());
		            }
		        } else {
		            JOptionPane.showMessageDialog(null, "El nombre del archivo fusionado no puede estar vacío.");
		        }
		    }
		});
		btnFusionar.setBounds(489, 188, 89, 23);
		contentPane.add(btnFusionar);		
		
		
		 /**
	     * Label
	     */
		JLabel lblDirectorio = new JLabel("DIRECTORIO");
		lblDirectorio.setBounds(94, 64, 80, 23);
		contentPane.add(lblDirectorio);
		
	}
}
