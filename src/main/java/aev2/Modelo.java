package aev2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import org.w3c.dom.Document;
import java.io.File;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Modelo {
	
	String url;
	String user;
	String pass;
	String userType;
	String userPass;
    boolean isAuthenticated = false;
    
    private Connection con;

    /**
     * establecer una conexión con la base de datos utilizando credenciales predeterminadas.
     */
    public Modelo() {
        try {
        	con = DriverManager.getConnection("jdbc:mysql://localhost:3306/books", "client", "client"); // Establecer la conexión al crear una instancia de Modelo
        	System.out.println("Conexión establecida como 'client'.");        	
        } catch (Exception e) {
            e.printStackTrace();
            
        }}
    
    
    /**
     * Autentica a un usuario contra la base de datos.
	 * Este método comprueba si las credenciales proporcionadas corresponden a un usuario existente en la base de datos. Si la autenticación es exitosa, también maneja la configuración de la conexión a la base de datos según el tipo de usuario (admin o client) mediante la lectura de archivos XML.
	 *
     * @param username El nombre de usuario a autenticar
     * @param password La contraseña correspondiente al usuario.
     * @return boolean Verdadero si la autenticación es exitosa, falso en caso contrario.
     */
    public boolean authenticateUser(String username, String password) {
        isAuthenticated = false;

        // La consulta SQL para obtener la contraseña y tipo de usuario por el nombre de usuario
        String sql = "SELECT pass, type FROM users WHERE user = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, username); // Establece el nombre de usuario en el PreparedStatement
            try (ResultSet rs = stmt.executeQuery()) { 
                if (rs.next()) { 
                    userPass = rs.getString("pass"); 
                    userType = rs.getString("type"); 
                    System.out.println("Contraseña: " + userPass);
                    System.out.println("Tipo permisos: " + userType);
                    isAuthenticated = password.equals(userPass); // Devuelve true si la contraseña es correcta

                    if (isAuthenticated) {
                        if ("admin".equals(userType)) {
                            try {

                                // Parsear el archivo XML
                                File xmlFile = new File("db_admin_config.xml");
                                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                                Document doc = dBuilder.parse(xmlFile);
                                
                                // Normalizar el documento XML
                                doc.getDocumentElement().normalize();

                                // Leer la configuración de la base de datos
                                url = doc.getElementsByTagName("url").item(0).getTextContent();
                                user = doc.getElementsByTagName("user").item(0).getTextContent();
                                pass = doc.getElementsByTagName("pass").item(0).getTextContent();

                                // Reestablecer la conexión con los nuevos parámetros de administrador
                                con = DriverManager.getConnection(url, user, pass);
                                System.out.println("Reconexión con admin.xml");
                            } catch (Exception e) {
                                e.printStackTrace();
                              }
                            
                        } else if ("client".equals(userType)) {
                            try {                             

                                // Parsear el archivo XML
                                File xmlFile = new File("db_client_config.xml");
                                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                                Document doc = dBuilder.parse(xmlFile);
                                
                                // Normalizar el documento XML
                                doc.getDocumentElement().normalize();

                                // Leer la configuración de la base de datos
                                String url = doc.getElementsByTagName("url").item(0).getTextContent();
                                String user = doc.getElementsByTagName("user").item(0).getTextContent();
                                String pass = doc.getElementsByTagName("pass").item(0).getTextContent();

                                // Reestablecer la conexión con los nuevos parámetros de administrador
                                con = DriverManager.getConnection(url, user, pass);
                                System.out.println("Reconexión con client.xml");
                            } catch (Exception e) {
                                e.printStackTrace();
                              }                        	                                           	
                        }
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
        return isAuthenticated;
    }
    
  

    /**
     * Ejecuta una consulta SQL en la base de datos.
	 * Si el usuario está autenticado, este método ejecuta la consulta SQL.
	 * Para consultas SELECT, recupera los resultados y los formatea en una cadena. 
	 * Para operaciones de modificación de datos (INSERT, UPDATE, DELETE) y si el usuario es admin, solicita confirmación antes de ejecutar la consulta. Muestra mensajes de error si ocurre algún problema durante la ejecución de la consulta o si el usuario no tiene los permisos adecuados.
	 *
     * @param sql La consulta SQL a ejecutar.
     * @return String Resultados de la consulta
     */
    public String ejecutarSQL(String sql) {
        StringBuilder resultado = new StringBuilder();
        if (isAuthenticated) {  // Verifica que el usuario esté autenticado
            try {
                if (sql.toUpperCase().startsWith("SELECT")) {
            
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(sql);
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    // Agregar los nombres de las columnas
                    for (int i = 1; i <= columnCount; i++) {
                        resultado.append(String.format("%-30s", metaData.getColumnName(i)));
                    }
                    resultado.append("\n");

                    // Agregar los datos de las filas
                    while (rs.next()) {
                        for (int i = 1; i <= columnCount; i++) {
                            resultado.append(String.format("%-30s", rs.getObject(i)));
                        }
                        resultado.append("\n");
                    }
                    rs.close(); // Cerrar el ResultSet
                } else if ("admin".equals(userType)) {
                    int confirm = JOptionPane.showConfirmDialog(null, "¿Está seguro de que desea ejecutar esta operación?", "Confirmar operación", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (confirm == JOptionPane.YES_OPTION) {
                	// Ejecutar consultas INSERT, UPDATE, DELETE para usuarios "admin"
                    Statement stmt = con.createStatement();        
                    stmt.executeUpdate(sql);
                    JOptionPane.showMessageDialog(null, "Operación realizada con exito.", "Operación Realizada", JOptionPane.INFORMATION_MESSAGE);
                    }else {                        
                        JOptionPane.showMessageDialog(null, "Operación cancelada por el usuario.", "Operación Cancelada", JOptionPane.INFORMATION_MESSAGE);
                    }                    
                } else {
                    JOptionPane.showMessageDialog(null, "No tienes permisos de administrador para ejecutar esta operación.", "Permiso denegado", JOptionPane.WARNING_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Error al ejecutar consulta", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "No estás autenticado o no tienes permisos para conectarte a la base de datos.", "Autenticación fallida", JOptionPane.ERROR_MESSAGE);
        }
        return resultado.toString();
    }
    
    /**
     * Cierra la sesión actual.
	 * Este método restablece las variables de autenticación y tipo de usuario, terminando la sesión del usuario actual.
	 */
    public void cerrarSesion() {
        isAuthenticated = false; 
        userType = null;
    }

    /**
     * Cierra la conexión con la base de datos.
	 * Este método verifica si la conexión está activa y, en caso afirmativo la cierra. Muestra un mensaje indicando el estado de la operación.
     */
    public void cerrarConexion() {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
                JOptionPane.showMessageDialog(null, "Conexión con la base de datos cerrada.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cerrar la conexión: " + e.getMessage(),
                                          "Error de Conexión", JOptionPane.ERROR_MESSAGE);
        }
    }         
}



