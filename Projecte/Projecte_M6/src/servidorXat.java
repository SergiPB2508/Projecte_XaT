import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mongodb.DB;
import com.mongodb.MongoClient;

public class servidorXat {

	static ArrayList<filXat> users = new ArrayList<>();
	
	public static void main(String[] args) {
		
		try{
			
			ServerSocket serverSocket = new ServerSocket(5556);

			while(true){
				
				//System.out.println("Acceptant connexions");		
				Socket newSocket = serverSocket.accept();
				//System.out.println("Conexión rebuda "+conn);
				
				filXat user = new filXat(newSocket);
				users.add(user);
				user.start(); 
				
			}	
		} catch(IOException e){
			e.printStackTrace();
		}

	}
}
