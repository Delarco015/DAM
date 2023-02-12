package evaluable3;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.text.SimpleDateFormat;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class GestorHTTP<Delincuente> implements HttpHandler{
	List<Delincuente> delincuentes = new ArrayList<>();
	
	/**
	 *
	 */
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		
        // Añadir la IP del cliente y el timestamp de conexión al archivo log.txt
        String direccionIP = exchange.getRemoteAddress().getAddress().toString(); // Obtener la IP del cliente
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()); // obtener el timestamp de conexión
        // Escribir la información en el archivo log.txt
        try (FileWriter fw = new FileWriter("log.txt", true)) {   // true para que no sobreescriba el archivo log.txt
        fw.write("IP del cliente: "+ direccionIP + " Timestamp: " + timestamp + System.lineSeparator());  // escribimos en el archivo log.txt  
        } catch (IOException e) {
        System.out.println("Error al escribir en el archivo log.txt: " + e.getMessage());
        }   
		
        
        
		String requestParamValue = null;    
		if("GET".equals(exchange.getRequestMethod())) {
			requestParamValue = handleGETRequest(exchange); //recogemos la info que recibimos del cliente			
			handleGETResponse(exchange, requestParamValue); //respuesta que le mandamos
		} else if ("POST".equals(exchange.getRequestMethod())) {
			try {
				requestParamValue = handlePOSTRequest(exchange);    //recogemos la info que recibimos del cliente
			}catch (InterruptedException e1) {e1.printStackTrace();} 						
			handlePOSTResponse(exchange, requestParamValue);    //respuesta que le mandamos
		}		
	}	
	
	// cuando recibimos una peticion GET del cliente procesamos la uri aqui
	/**
	 * @param exchange
	 * @return
	 */
	private String handleGETRequest(HttpExchange exchange) {
		System.out.println("Recibida URI GET: " + exchange.getRequestURI().toString()); 
		String[] splitURI = exchange.getRequestURI().toString().split("/"); //separamos la uri por /
		if(splitURI.length > 2) {   //si la uri tiene mas de 2 partes devolvemos la ultima
		return splitURI[splitURI.length - 1];   
		}
		return "";  //si no devolvemos un string vacio
	}
	
	// cuando recibimos una peticion POST del cliente procesamos la uri aqui
	/**
	 * @param exchange
	 * @return
	 * @throws InterruptedException
	 */
	private String handlePOSTRequest(HttpExchange exchange) throws InterruptedException {
		  System.out.println("Recibida URI tipo POST: " + exchange.getRequestURI().toString()); 
		  InputStream is = exchange.getRequestBody(); //recogemos el cuerpo de la peticion
		  InputStreamReader isr = new InputStreamReader(is);    
		  BufferedReader br = new BufferedReader(isr);  
		  StringBuilder sb = new StringBuilder();   
		  String line;  
		  try {
		    while((line = br.readLine()) != null) { //leemos el cuerpo de la peticion
		      sb.append(line);  
		    }
		    br.close();
		  } catch(IOException e) {  
		    e.printStackTrace();    
		  }

		  String body = sb.toString();  //guardamos el cuerpo de la peticion en un string
		  if (exchange.getRequestURI().toString().equals("/servidor/nuevo")) {
		    try {		  
		      JsonObject delincuenteJson = new JsonParser().parse(body).getAsJsonObject();  //parseamos el string a un objeto json
		      String alias = delincuenteJson.get("alias").getAsString();    
		      String nombreCompleto = delincuenteJson.get("nombreCompleto").getAsString();  
		      String fechaNacimiento = delincuenteJson.get("fechaNacimiento").getAsString();    
		      String nacionalidad = delincuenteJson.get("nacionalidad").getAsString();  
		      String fotografia = delincuenteJson.get("fotografia").getAsString();
		
		  	  datosEmail(alias, nombreCompleto, fechaNacimiento, nacionalidad, fotografia);   //llamamos al metodo que guarda los datos del delincuente nuevo , y este contiene el metodo que envia el email

		      // Guardar la información del delincuente en el archivo json
		      Delincuentes nuevoDelincuente = new Delincuentes(alias, nombreCompleto, fechaNacimiento, nacionalidad, fotografia);  //creamos un objeto delincuente con los datos del post

		      // Leemos el archivo json y lo convertimos en un array de objetos
		      FileReader fileReader = new FileReader("delincuentes.json");
		      JsonArray delincuentes = new JsonParser().parse(fileReader).getAsJsonArray();
		      fileReader.close();
		      Gson gson = new Gson();   //creamos un objeto gson
		      JsonElement nuevoDelincuenteJson = gson.toJsonTree(nuevoDelincuente); //convertimos el objeto delincuente a un elemento json
		    
		      delincuentes.add(nuevoDelincuenteJson);   //añadimos el objeto al array de objetos json
			  
		      // Escribir la lista completa en el archivo
		      Gson gson1 = new GsonBuilder().create();
		      FileWriter fileWriter = new FileWriter("delincuentes.json");
		      gson.toJson(delincuentes, fileWriter);
		      fileWriter.close();

		      return "Información del delincuente recibida y guardada"; // el return sera el valor del requestParamValue en la respuesta POST
		    } catch (Exception e) {
		      e.printStackTrace();
		      return "Error al procesar la información del delincuente"; // el return sera el valor del requestParamValue en la respuesta POST
		    }
		  } else {
		    return "Error: URI desconocido"; // el return sera el valor del requestParamValue en la respuesta POST
		  }

		}
					
	// Método que lee el archivo delincuentes.json que se utiliza en la respuesta GET
	/**
	 * @return
	 */
	public ArrayList<Delincuentes> leerDelincuentesDesdeJSON() {
		ArrayList<Delincuentes> delincuentes = new ArrayList<>();
		try {
			Gson gson = new Gson();
			Reader reader = new FileReader("delincuentes.json"); 
			
			Delincuentes[] delincuentesArray = gson.fromJson(reader, Delincuentes[].class);
			Collections.addAll(delincuentes, delincuentesArray);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return delincuentes;
	}
	//respuesta GET
	/**
	 * @param exchange
	 * @param requestParamValue
	 * @throws IOException
	 */
	private void handleGETResponse(HttpExchange exchange, String requestParamValue) throws IOException {
	    OutputStream outputStream = exchange.getResponseBody();
	    
	    ArrayList<Delincuentes> delincuentes = leerDelincuentesDesdeJSON(); //leemos el archivo json y lo convertimos en un array de objetos
	    String htmlResponse = null;
	    if(requestParamValue.equals("mostrarTodos")) {  
	        StringBuilder response = new StringBuilder();
	        response.append("<html><body><ul>");
	        for (Delincuentes delincuente : delincuentes) { //recorremos el array de objetos y mostramos los alias de cada delincuente
	            response.append("<li>" + delincuente.getAlias() + "</li>"); 
	        }
	        response.append("</ul></body></html>");
	        htmlResponse = response.toString();
		    exchange.sendResponseHeaders(200, htmlResponse.length());
		    outputStream.write(htmlResponse.getBytes());
		    outputStream.flush();
		    outputStream.close();
		    System.out.println("Devuelve respuesta HTML: " + htmlResponse);  
	    }
	    
	    if(requestParamValue.split("\\?")[0].equals("mostrarUno")) { 
	        String alias = requestParamValue.split("=")[1]; //recogemos el alias del delincuente que queremos mostrar
	        Delincuentes delincuente = null;        //creamos un objeto delincuente
	        for (Delincuentes d : delincuentes) {   //recorremos el array de objetos y buscamos el delincuente con el alias que hemos recibido
	          if (d.getAlias().equals(alias)) {
	            delincuente = d;                //si lo encontramos lo guardamos en el objeto delincuente
	            break;
	          }
	        }
	        
	        if (delincuente != null) {  //si el objeto delincuente no es nulo mostramos la información del delincuente
	          StringBuilder response = new StringBuilder();
		        response.append("<html><body><ul>");
	            response.append("<li>" + delincuente.getAlias() + "</li>");
	            response.append("<li>" + delincuente.getNombreCompleto() + "</li>");
	            response.append("<li>" + delincuente.getFechaNacimiento() + "</li>");
	            response.append("<li>" + delincuente.getNacionalidad() + "</li>");
	            response.append("<li><img src='" + delincuente.getFotografia() + "'></li>");
		        response.append("</ul></body></html>");
	          htmlResponse = response.toString();
	        } else {
	          htmlResponse = "<html><body>No se encontró un delincuente con el alias " + alias + "</body></html>";  
	        }	        
		    exchange.sendResponseHeaders(200, htmlResponse.getBytes().length);  //enviamos la respuesta
		    outputStream.write(htmlResponse.getBytes());    
		    outputStream.flush();
		    outputStream.close();
		    System.out.println("Devuelve respuesta HTML: " + htmlResponse);	        
	    }	
	    else{
	          htmlResponse = "<html><body>El valor de la petición GET no es correcta </body></html>";   //si el valor de la petición GET no es correcto mostramos este mensaje de error
			    exchange.sendResponseHeaders(200, htmlResponse.getBytes().length);  //enviamos la respuesta
			    outputStream.write(htmlResponse.getBytes());
			    outputStream.flush();
			    outputStream.close();
			    System.out.println("Devuelve respuesta HTML: " + htmlResponse);	 
	        }		   
	}
	//respuesta POST
	/**
	 * @param exchange
	 * @param requestParamValue
	 * @throws IOException
	 */
	private void handlePOSTResponse(HttpExchange exchange, String requestParamValue) throws IOException{    
		OutputStream outputStream = exchange.getResponseBody(); 
		exchange.sendResponseHeaders(200, requestParamValue.getBytes().length); //enviamos la respuesta al cliente
		outputStream.write(requestParamValue.getBytes());   
		outputStream.flush();
		outputStream.close();
		System.out.println("Devuelve respuesta HTML: " + requestParamValue);
	}

    //metodo que guarda los datos del nuevo delincuente y crea todos los datos necesarios para enviar el email
	/**
	 * @param mensajeAlias
	 * @param mensajeNombre
	 * @param mensajeFecha
	 * @param mensajeNacionalidad
	 * @param mensajeImagen
	 */
	public void datosEmail(String mensajeAlias, String mensajeNombre, String mensajeFecha, String mensajeNacionalidad, String mensajeImagen ) {
		
		Scanner sc = new Scanner(System.in);
		System.out.print("Introduce la contraseña: ");
		String remitePass = sc.nextLine(); 		// la contraseña la pedimos por teclado
		
		System.out.println("Enviando Email");		
		
		String mensaje_alias = mensajeAlias;
		String mensaje_nombre = mensajeNombre;
		String mensaje_fecha = mensajeFecha;
		String mensaje_nacionalidad = mensajeNacionalidad;
		String mensaje_imagen = mensajeImagen;
		String asunto = "NUEVO DELINCUENTE";
		String emailRemite = "pruebasdam@outlook.es"; //correo del que envia		
		String hostEmail = "smtp.office365.com";    //host de outlook
		String portEmail = "587"; //como trabajamos con TSL usamos el 587
		String[] emailsDestino = {"judepo@floridauniversitaria.es"}; //creamos un array para introducir los destinatarios que necesitemos, podemos añadir utilizando "," entre cada email
		
		
		try {
			enviarEmail(mensaje_alias,mensaje_nombre,mensaje_fecha,mensaje_nacionalidad,mensaje_imagen, asunto, emailRemite, remitePass, hostEmail, portEmail, emailsDestino);  //llamamos al metodo enviarEmail
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		
		System.out.println("Email enviado!!");
	}
	
    //metodo para enviar el email
	/**
	 * @param mensaje_alias
	 * @param mensaje_nombre
	 * @param mensaje_fecha
	 * @param mensaje_nacionalidad
	 * @param mensaje_imagen
	 * @param asunto
	 * @param mail_remite
	 * @param mail_remite_pass
	 * @param host_mail
	 * @param port_mail
	 * @param mails_destino
	 * @throws AddressException
	 * @throws MessagingException
	 */
	public void enviarEmail(String mensaje_alias,String mensaje_nombre,String mensaje_fecha,String mensaje_nacionalidad,String mensaje_imagen, String asunto, String mail_remite, String mail_remite_pass, String host_mail, String port_mail, String[] mails_destino ) throws AddressException, MessagingException  {
			
		Properties props = System.getProperties();
		props.put("mail.smtp.host", host_mail); 
		props.put("mail.smtp.user", mail_remite);   
		props.put("mail.smtp.clave", mail_remite_pass);     
		props.put("mail.smtp.starttls.enable", "true"); 
		props.put("mail.smtp.port", port_mail); 
		
		Session session = Session.getDefaultInstance(props);
		
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(mail_remite));
		//recorre un for y va anyadiendo los destinatarios , en este caso solo hay uno
		for(int i=0; i< mails_destino.length; i++) {
			message.addRecipients(Message.RecipientType.TO, mails_destino[i]);
		}
		
		message.setSubject(asunto);
		
		//creamos el cuerpo del mensaje
		Multipart multipart = new MimeMultipart();  //creamos un objeto multipart para poder añadir el texto y la imagen
		
		String textoHTML = "<html><body><p><b>Datos del delincuente:</b></p>"   //creamos el texto que queremos que aparezca en el email
                + "<p><b>Alias: </b>" + mensaje_alias + "</p>"
                + "<p><b>Nombre completo: </b>" + mensaje_nombre + "</p>"
                + "<p><b>Fecha nacimiento: </b>" + mensaje_fecha + "</p>"
                + "<p><b>Nacionalidad: </b>" + mensaje_nacionalidad + "</p>"
              + "<p><img src='" + mensaje_imagen + "'></p></body></html>";  //añadimos la imagen

        MimeBodyPart contenidoHTML = new MimeBodyPart();    //creamos un objeto MimeBodyPart para poder añadir el texto
        contenidoHTML.setContent(textoHTML, "text/html");   //añadimos el texto al objeto MimeBodyPart
        multipart.addBodyPart(contenidoHTML);	            //añadimos el objeto MimeBodyPart al multipart
            
        message.setContent(multipart);					    //añadimos el multipart al mensaje
                        
        Transport transport = session.getTransport("smtp"); //creamos un objeto Transport para poder enviar el mensaje
        transport.connect(host_mail, mail_remite, mail_remite_pass);    //conectamos con el host y el usuario y contraseña
        transport.sendMessage(message, message.getAllRecipients());    //enviamos el mensaje
        transport.close();
    }
}