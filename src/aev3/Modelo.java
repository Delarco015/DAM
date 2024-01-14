package aev3;

import com.mongodb.MongoClient;
import com.mongodb.client.*;
import com.mongodb.client.model.Sorts;

import org.bson.Document;
import org.json.JSONObject;

import static com.mongodb.client.model.Filters.eq;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Clase Modelo en el patrón MVC para la aplicación AEV3.
 * Esta clase se encarga de manejar la lógica de negocio, incluyendo la interacción
 * con la base de datos MongoDB y el manejo de datos relacionados con la aplicación.
 */
public class Modelo {
	static MongoClient mongoClient;
    static MongoDatabase database;
    static MongoCollection<Document> imagenesCollection;
    static MongoCollection<Document> usuariosCollection;
    static MongoCollection<Document> recordsCollection;
    boolean isAuthenticated = false; 
    
    
    
    /**
     * Constructor del Modelo. Establece la conexión con la base de datos MongoDB
     * y configura las colecciones utilizadas.
     *
     * @throws Exception si hay un problema al leer la configuración o al conectar a la base de datos.
     */
    public Modelo() throws Exception {
    	
    	Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
		mongoLogger.setLevel(Level.SEVERE); // e.g. or Log.WARNING, etc.   
		
        // Leer la configuración de la base de datos desde un archivo JSON
        String jsonConfig = new String(Files.readAllBytes(Paths.get("dbconfig.json")));
        JSONObject config = new JSONObject(jsonConfig);

        String ip = config.getString("ip");
        String puerto = config.getString("puerto");
        String nombreBaseDatos = config.getString("nombreBaseDatos");
        JSONObject colecciones = config.getJSONObject("colecciones");
        
        // Conectar
        MongoClient mongoClient = new MongoClient(ip, Integer.parseInt(puerto));
		MongoDatabase database = mongoClient.getDatabase(nombreBaseDatos);

        imagenesCollection = database.getCollection(colecciones.getString("imagenes"));
        usuariosCollection = database.getCollection(colecciones.getString("usuarios"));
        recordsCollection = database.getCollection(colecciones.getString("records"));        
    }
    
    
    
    /**
     * Guarda las imágenes almacenadas en la colección de MongoDB en formato JPG
     * en el directorio local "img".
     */
    public void guardarImagenesComoJPG() {
        File dir = new File("img");
        if (!dir.exists()) dir.mkdirs(); // Crear el directorio si no existe
        MongoCursor<Document> cursor = imagenesCollection.find().iterator();
        int index = 0;
        while (cursor.hasNext()) {
            Document doc = cursor.next();
            String base64Image = doc.getString("base64"); 
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            try {
                BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBytes));
                File outputFile = new File(dir, "imagen" + index + ".jpg");
                ImageIO.write(img, "jpg", outputFile);
                index++;
            } catch (Exception e) {
                e.printStackTrace();
                // Manejar la excepción adecuadamente
            }
        }
    }
    
    /**
     * Autentica a un usuario verificando si el nombre de usuario y la contraseña
     * proporcionados coinciden con los almacenados en la base de datos.
     *
     * @param username Nombre de usuario.
     * @param password Contraseña.
     * @return true si las credenciales son correctas, false en caso contrario.
     */
    public boolean autenticarUsuario(String username, String password) {
        Document user = usuariosCollection.find(new Document("login", username)).first();
        if (user == null) {
            return false; // Usuario no encontrado
        }
        // Si el usuario existe, compara la contraseña hasheada
        String hashedPassword = hashPassword(password);
        if (hashedPassword.equals(user.getString("password"))) {
            return true; // Autenticación exitosa
        } else {
            return false; // Contraseña incorrecta
        }
    }

    /**
     * Registra un nuevo usuario en la base de datos.
     *
     * @param username Nombre de usuario.
     * @param password Contraseña.
     * @return true si el registro es exitoso, false si el usuario ya existe.
     */
    public boolean registrarUsuario(String username, String password) {
        // Verificar si el usuario ya existe
        if (usuariosCollection.countDocuments(new Document("login", username)) > 0) {
            return false; // Usuario ya existe
        }
        // Guardar nuevo usuario
        String hashedPassword = hashPassword(password);
        Document newUser = new Document("login", username).append("password", hashedPassword);
        usuariosCollection.insertOne(newUser);
        return true;
    }

    /**
     * Genera un hash de la contraseña utilizando SHA-256.
     *
     * @param password Contraseña a hashear.
     * @return String representando el hash de la contraseña.
     */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }   
    
    /**
     * Cierra la sesión actual.
     *
     * @return siempre false, indicando que no hay una sesión activa.
     */
    public boolean cerrarSesion() {
        isAuthenticated = false; 
        return false;
    }
    
    /**
     * Cierra la sesión actual.
     *
     * @return siempre false, indicando que no hay una sesión activa.
     */
    public List<String> prepararJuego(String dificultad) {
        int numeroDePares = dificultad.equals("2x4") ? 4 : 8;
        List<String> nombresDeImagenes = seleccionarImagenesAleatorias(numeroDePares);
        List<String> imagenesParaTablero = new ArrayList<>(nombresDeImagenes);       
        imagenesParaTablero.addAll(nombresDeImagenes); // Duplicar
        Collections.shuffle(imagenesParaTablero); // Mezclar
        return imagenesParaTablero;
    }

    /**
     * Selecciona un número específico de imágenes de manera aleatoria.
     *
     * @param numeroDePares Número de imágenes (pares) a seleccionar.
     * @return Lista de nombres de imágenes seleccionadas.
     */
    private List<String> seleccionarImagenesAleatorias(int numeroDePares) {
        File dir = new File("img");
        File[] archivos = dir.listFiles();
        List<String> nombresDeImagenes = new ArrayList<>();
        if (archivos != null) {
            for (File archivo : archivos) {
                nombresDeImagenes.add(archivo.getName());
            }
        }
        List<String> imagenesSeleccionadas = new ArrayList<>();
        for (int i = 0; i < numeroDePares && i < nombresDeImagenes.size(); i++) {
            imagenesSeleccionadas.add(nombresDeImagenes.get(i));
        }
        return imagenesSeleccionadas;
    }
    
    /**
     * Guarda un nuevo récord en la base de datos y muestra un mensaje si se
     * establece un nuevo récord de tiempo para una dificultad específica.
     *
     * @param timestamp  Timestamp de la partida.
     * @param usuario    Nombre del usuario.
     * @param dificultad Dificultad de la partida (8 o 16).
     * @param duracion   Duración de la partida en segundos.
     */
    public void guardarRecordBD(String timestamp, String usuario, int dificultad, int duracion) {
        Document mejorTiempo = recordsCollection.find(eq("dificultad", dificultad)).sort(Sorts.ascending("duracion")).first();
        boolean esTiempoRecord = mejorTiempo == null || duracion < mejorTiempo.getInteger("duracion");
        if (esTiempoRecord) {
        	JOptionPane.showMessageDialog(null, "¡Enhorabuena! Has establecido un nuevo récord de tiempo.", "Nuevo Récord", JOptionPane.INFORMATION_MESSAGE);
        }   
        Document record = new Document();
    	record.append("usuario", usuario);
    	record.append("dificultad", dificultad);
    	record.append("timestamp", timestamp);
    	record.append("duracion", duracion);
    	recordsCollection.insertOne(record);
    }
    
    /**
     * Muestra un diálogo con los récords de una dificultad específica,
     * ordenados por duración de menor a mayor.
     *
     * @param dificultad Dificultad de los récords a mostrar (8 o 16).
     */
    public void mostrarRecordsPorDificultad(int dificultad) {
    	FindIterable<Document> resultados = recordsCollection.find(eq("dificultad", dificultad)).sort(Sorts.ascending("duracion"));
        JDialog dialogoRecords = new JDialog(); 
        dialogoRecords.setTitle("Records Dificultad: " + dificultad);
        String[] columnas = {"Usuario", "Duración", "Timestamp"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0);
        for (Document record : resultados) {
            modelo.addRow(new Object[]{
                record.getString("usuario"),
                record.getInteger("duracion").toString(),
                record.get("timestamp").toString()
            });
        }
        JTable tablaRecords = new JTable(modelo);
        JScrollPane scrollPane = new JScrollPane(tablaRecords);
        dialogoRecords.add(scrollPane);
        dialogoRecords.pack();
        dialogoRecords.setLocationRelativeTo(null);
        dialogoRecords.setVisible(true);
    }    
}
