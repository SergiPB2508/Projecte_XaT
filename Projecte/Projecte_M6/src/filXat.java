import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class filXat extends Thread {


	private Socket newSocket = null;
	private String nom;
	public filXat(Socket cs){
		newSocket=cs;
	}	
	
	public void run(){
		try{
			// atenc al client
			InputStream is=newSocket.getInputStream();
			
			byte[] user = new byte[newSocket.getReceiveBufferSize()];
			//rep missatge
			is.read(user);
			
			String nom = new String(user);
			
			this.setNom(nom.trim());
			
			nouUser();
			
			while(true){
				byte[] missatge = new byte[newSocket.getReceiveBufferSize()];
				is.read(missatge);
				
				String msg = new String(missatge);
				
				String[] parts = msg.split(" ");
				
				if(parts[0].trim().equals("/e")) {
					userDesc(this.nom);
					servidorXat.users.remove(this);
					this.newSocket.close();
					break;
				}else if(parts[0].trim().equals("/u")) {
					users(this);
				} else if(parts[0].equals("/g")) {
					enviar(this.nom, parts);
				} else if(parts[0].equals("/p")) {

					String priv = parts[1];
					
					enviarPriv(this.nom,parts,priv);
				}
			}
		
		} catch(IOException e){
			e.printStackTrace();
		}
	}

	public Socket getNewSocket() {
		return newSocket;
	}

	public String getNom() {
		return nom;
	}
	
	public void setNom(String nom){
		this.nom = nom;
	}

	public static void userDesc(String nom) {
		ArrayList<filXat> users = servidorXat.users;
		String msg = nom + " s'ha desconectat";
		OutputStream os = null;
		for(filXat user : users ) {
			try {
				os = user.getNewSocket().getOutputStream();
				os.write(msg.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				servidorXat.users.remove(user);
			}
		}
	}

	public static void nouUser() {
			ArrayList<filXat> users = servidorXat.users;
			String msg = "Usuaris Conectats:";
			for(filXat user : users) {
				msg = msg+" "+user.getNom().trim();
			}
			
			OutputStream os = null;
			for(filXat user : users ) {
				try {
					os = user.getNewSocket().getOutputStream();
					os.write(msg.getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					servidorXat.users.remove(user);
				}
			}
			
	}
	
	public static void users(filXat user) throws IOException {
		ArrayList<filXat> users = servidorXat.users;
		String msg = "Usuaris Conectats:";
		for(filXat user2 : users) {
			msg = msg+" "+user2.getNom().trim();
		}
		
		OutputStream os = null;
		os = user.getNewSocket().getOutputStream();
		os.write(msg.getBytes());
	}
	
	public static void enviarPriv(String nom,String[] missatge, String priv) throws IOException {
			ArrayList<filXat> users = servidorXat.users;
			String msg = "";
			for(int i = 2;i < missatge.length;i++) {
				msg = msg + " " + missatge[i]; 
			}
			
			String msgPriv = "Missatge privat "+nom+": "+msg;
			
			OutputStream os = null;
			for(filXat user : users ) {
				if(priv.trim().equals(user.getNom()) || nom.equals(user.getNom())){
					os = user.getNewSocket().getOutputStream();
					os.write(msgPriv.getBytes());
					//System.out.println(msg);
				}
			}
			
	}
	
	public static void enviar(String nom,String[] missatge) throws IOException {

			ArrayList<filXat> users = servidorXat.users;
			String msg = nom+":";
			
			for(int i = 1;i < missatge.length;i++) {
				msg = msg + " " + missatge[i]; 
			}
			
			OutputStream os = null;
			for(filXat user : users ) {
					os = user.getNewSocket().getOutputStream();
					os.write(msg.getBytes());
					//System.out.println(msg);
			}
	}
	
	
}
