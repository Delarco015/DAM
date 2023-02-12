package evaluable3;


import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.sun.net.httpserver.HttpServer;

public class Servidor {

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
    	
    	System.out.println("Arrancando el servidor");
    	
		String host = "127.0.0.1"; 	//ip del servidor localhost
		int port = 7777; 	//puerto del server
		InetSocketAddress direccionTCPIP = new InetSocketAddress(host, port);
		
		int backlog = 0;	//Numero de conexiones pendientes que el servidor puede mantener en cola
		
		//creamos servidor
		HttpServer servidor = HttpServer.create(direccionTCPIP, backlog);
		
		//creamos un objeto gestor
		GestorHTTP gestor = new GestorHTTP(); 	//Clase que gestionara los GETs, POSTs, etc.
		String rutaRespuesta = "/servidor";
		servidor.createContext(rutaRespuesta, gestor);	//Crea un contexto, asocia la ruta al gestor HTTP
		
		ThreadPoolExecutor tpex = (ThreadPoolExecutor)Executors.newFixedThreadPool(10);
		servidor.setExecutor(tpex);
		servidor.start();
		System.out.println("Servidor arranca en puerto: " + port);    	
    }

}