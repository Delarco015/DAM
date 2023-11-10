package aev2;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Vista {

	private JFrame frame;
	private JPanel contentPane;
	private JTextField textUsuario;
	private JTextField textContrasenya;
	private JButton btnIniciarSesion;

    private JTextArea textAreaSQL;
    private JButton btnEjecutar;
    private JScrollPane scrollPane;
    private JTextField textConsulta;
    private JButton btnCerrarSesion;
    private JButton btnCerrarConexion;
    
    
	public Vista() {
		frame = new JFrame();
		frame.setBounds(100, 100, 1008, 804);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
	    contentPane = new JPanel();
	    frame.setContentPane(contentPane);
	    frame.setTitle("Ventana AEV2");
	    frame.setLocationRelativeTo(null);
		contentPane.setLayout(null);

		
		JPanel Login = new JPanel();
		Login.setBounds(10, 11, 392, 71);
		contentPane.add(Login);
		Login.setLayout(null);
		
		textUsuario = new JTextField();
		textUsuario.setBounds(84, 8, 169, 20);
		Login.add(textUsuario);
		textUsuario.setColumns(10);
		
		textContrasenya = new JTextField();
		textContrasenya.setColumns(10);
		textContrasenya.setBounds(84, 39, 169, 20);
		Login.add(textContrasenya);
		
        textAreaSQL = new JTextArea();
        scrollPane = new JScrollPane(textAreaSQL);
        scrollPane.setBounds(10, 100, 972, 620);
        contentPane.add(scrollPane);
		
		JLabel lblNewLabel = new JLabel("Usuario:");
		lblNewLabel.setBounds(10, 11, 62, 14);
		Login.add(lblNewLabel);
		
		JLabel lblContrasea = new JLabel("Contraseña: ");
		lblContrasea.setBounds(10, 42, 76, 14);
		Login.add(lblContrasea);
		
		btnIniciarSesion = new JButton("Iniciar Sesión");
		btnIniciarSesion.setBounds(260, 21, 122, 23);
		Login.add(btnIniciarSesion);
		
        btnEjecutar = new JButton("Ejecutar Consulta");
        btnEjecutar.setBounds(813, 23, 133, 59);
        contentPane.add(btnEjecutar);
        
        textConsulta = new JTextField();
        textConsulta.setBounds(441, 36, 362, 32);
        contentPane.add(textConsulta);
        textConsulta.setColumns(10);
        
        btnCerrarSesion = new JButton("Cerrar Sesión");
        btnCerrarSesion.setBounds(10, 731, 133, 30);
        contentPane.add(btnCerrarSesion);
        
        btnCerrarConexion = new JButton("Cerrar Conexión");
        btnCerrarConexion.setBounds(832, 731, 150, 30); 
        contentPane.add(btnCerrarConexion);
		
		frame.setVisible(true);

	}

    /**
     * @return El campo de texto para ingresar consultas
     */
    public JTextField getTextConsulta() {
		return textConsulta;
	}
	/**
	 * @return El campo de texto para el nombre de usuario
	 */
	public JTextField getTextUsuario() {
        return textUsuario;
    }
    /**
     * @return El campo de texto para la contraseña
     */
    public JTextField getTextContrasenya() {
        return textContrasenya;
    }
    /**
     * @return El botón para iniciar sesión.
     */
    public JButton getBtnIniciarSesion() {
        return btnIniciarSesion;
    }
    /**
     * @return El botón para ejecutar consultas SQL
     */
    public JButton getBtnEjecutar() {
        return btnEjecutar;
    }
    /**
     * @return El área de texto para mostrar las consultas SQL
     */
    public JTextArea getTextAreaSQL() {
        return textAreaSQL;
    }
    /**
     * @return El botón para cerrar sesión.
     */
    public JButton getBtnCerrarSesion() {
        return btnCerrarSesion;
    }
	/**
	 * @return El botón para cerrar la conexión a la base de datos.
	 */
	public JButton getBtnCerrarConexion() {
		return btnCerrarConexion;
	}
    

}
