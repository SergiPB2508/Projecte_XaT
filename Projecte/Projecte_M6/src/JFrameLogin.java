import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.FlowLayout;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.event.ActionEvent;
import java.awt.Font;
import javax.swing.SwingConstants;

public class JFrameLogin extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JPasswordField  textField_1;
	private DB db;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JFrameLogin frame = new JFrameLogin();
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
	public JFrameLogin() {
		Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
		mongoLogger.setLevel(Level.SEVERE);

		MongoClient conn = crearConexion();
		if(conn != null){
			db  = conn.getDB("ProjecteXat");
		}
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();

		setContentPane(contentPane);
		
		JPanel panel_titol = new JPanel();
		FlowLayout fl_panel_titol = (FlowLayout) panel_titol.getLayout();
		fl_panel_titol.setVgap(20);
		fl_panel_titol.setHgap(100);
		contentPane.add(panel_titol);
		
		JLabel Titol = new JLabel("Menú Login");
		Titol.setFont(new Font("Tahoma", Font.BOLD, 22));
		panel_titol.add(Titol);
		
		JPanel panel_nickname = new JPanel();
		FlowLayout fl_panel_nickname = (FlowLayout) panel_nickname.getLayout();
		fl_panel_nickname.setVgap(10);
		fl_panel_nickname.setHgap(50);
		contentPane.add(panel_nickname);
		
		JLabel labelNickname = new JLabel("Usuari: ");
		labelNickname.setFont(new Font("Tahoma", Font.PLAIN, 20));
		panel_nickname.add(labelNickname);
		
		textField = new JTextField();
		textField.setFont(new Font("Tahoma", Font.PLAIN, 15));
		panel_nickname.add(textField);
		textField.setColumns(10);
		
		JPanel panel_2 = new JPanel();
		FlowLayout flowLayout_2 = (FlowLayout) panel_2.getLayout();
		flowLayout_2.setVgap(10);
		flowLayout_2.setHgap(30);
		contentPane.add(panel_2);
		
		JLabel labelContrasenya = new JLabel("Contrasenya: ");
		labelContrasenya.setFont(new Font("Tahoma", Font.PLAIN, 20));
		panel_2.add(labelContrasenya);
		
		textField_1 = new JPasswordField();
		
		textField_1.setFont(new Font("Tahoma", Font.PLAIN, 15));
		panel_2.add(textField_1);
		textField_1.setColumns(10);
		
		JPanel panel_3 = new JPanel();
		FlowLayout flowLayout_3 = (FlowLayout) panel_3.getLayout();
		flowLayout_3.setHgap(100);
		flowLayout_3.setVgap(15);
		contentPane.add(panel_3);
		
		JButton BotoLogin = new JButton("Iniciar Sessió");
		BotoLogin.setFont(new Font("Tahoma", Font.PLAIN, 20));
		BotoLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					
					DBCollection users = db.getCollection("Users");

					BasicDBObject query = new BasicDBObject();
			        query.put("usuari", textField.getText() );
			        
			        DBCursor cur2 = users.find(query);
			        
		            MessageDigest md = MessageDigest.getInstance("SHA-256");
		            byte[] hashBytes = md.digest(textField_1.getText().getBytes());

		            StringBuilder hexString = new StringBuilder();
		            for (byte hashByte : hashBytes) {
		                String hex = Integer.toHexString(0xff & hashByte);
		                if (hex.length() == 1) hexString.append('0');
		                hexString.append(hex);
		            }

			        if(cur2.hasNext()) {
			        	
			        	DBObject user = cur2.next();
			        	
			        	if(hexString.toString().equals(user.get("contrasenya").toString())) {
							dispose();
							JFrameXat xat;
							xat = new JFrameXat(textField.getText(),db);
							xat.setVisible(false);
			        	} else {
			        		JOptionPane.showMessageDialog(contentPane, "Contrasenya incorecta", "Aviso", JOptionPane.ERROR_MESSAGE);
			        	}
			        	
			        } else { 
		        		JOptionPane.showMessageDialog(contentPane, "Usuari incorecta", "Aviso", JOptionPane.ERROR_MESSAGE);
			        }
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		panel_3.add(BotoLogin);
	}

    private static MongoClient crearConexion() {
        MongoClient mongo = null;
        try {
            mongo = new MongoClient("localhost", 27017);
        } catch (Exception e) {
            e.printStackTrace();
        }
 
        return mongo;
    }

}
