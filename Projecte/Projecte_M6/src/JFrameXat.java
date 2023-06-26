import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import java.awt.Font;
import javax.swing.JTextArea;
import javax.swing.JTable;
import javax.swing.JScrollBar;
import javax.swing.JList;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.awt.event.ActionEvent;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.toedter.calendar.JCalendar;

import Bean.AjudesBean;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;

public class JFrameXat extends JFrame {

	private JPanel contentPane;
	private JTextField txtEscriuElSeu;
	private JTextField textField;
	private final static String newline = "\n";
	private JTextField textField_1;
	private Thread repMsg;
    private int dia = 0;

	/**
	 * Launch the application.
	 */
	/*public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JFrameXat frame = new JFrameXat("prova",null);
					frame.setVisible(false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}*/

	/**
	 * Create the frame.
	 * @throws IOException 
	 */
	public JFrameXat(String nom, DB db) throws IOException {

		Socket clientSocket = new Socket();
		
		//InetSocketAddress addr = new InetSocketAddress("192.168.3.2", 5556);
		InetSocketAddress addr = new InetSocketAddress("192.168.2.63", 5556);
		clientSocket.connect(addr);
		
		InputStream is=clientSocket.getInputStream();
		OutputStream os=clientSocket.getOutputStream();

		os.write(nom.getBytes());

		DBCollection historial = db.getCollection("Historial");
		
		JPanel middlePanel = new JPanel ();
	    middlePanel.setBorder ( new TitledBorder(null, "Xat En Directe", TitledBorder.LEADING, TitledBorder.TOP, null, null) );

	    // create the middle panel components

	    JTextArea display = new JTextArea ( 16, 58 );
	    display.setEditable ( false ); // set textArea non-editable
	    
	    repMsg = new Thread(() -> {
			try {
				while(true) {
				    byte[] missatge = new byte[clientSocket.getReceiveBufferSize()];
				    is.read(missatge);

				    try {
				    	display.append(new String(missatge).trim() + newline);
				    }catch(Exception e) {}

				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    });
	    repMsg.start();
	    
	    JScrollPane scroll = new JScrollPane ( display );
	    scroll.setVerticalScrollBarPolicy ( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );

	    //Add Textarea in to middle panel
	    middlePanel.add ( scroll );

	    // My code
	    JFrame frame = new JFrame ();
	    frame.getContentPane().add ( middlePanel );
	    
	    JPanel panelBottom = new JPanel();
	    frame.getContentPane().add(panelBottom, BorderLayout.SOUTH);
	    
		AjudesBean ajudesBean = new AjudesBean();
		panelBottom.add(ajudesBean);
	    
	    textField_1 = new JTextField();
	    panelBottom.add(textField_1);
	    textField_1.setColumns(50);
	    

	    ajudesBean.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int seleccion =  ajudesBean.getSelectedIndex();
                textField_1.setText(ajudesBean.getSelect(seleccion));
            }
        });
	    
	    JButton btnNewButton = new JButton("Enviar");
	    btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
	    panelBottom.add(btnNewButton); 
	    
	    JButton btnNewButton_1 = new JButton("Eliminar Ultim Missatge");
	    btnNewButton_1.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {

	    		BasicDBObject query = new BasicDBObject();
	            query.put("Usuari", nom );
	            
	            DBCursor cur = historial.find(query).sort(new BasicDBObject("Data", -1)).limit(1);
	            
	            if(cur.hasNext()) {
	            	historial.remove(cur.next());
	            }
	    	}
	    });
	    panelBottom.add(btnNewButton_1);
	    btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					LocalDate data = LocalDate.now();

					String msg = textField_1.getText();
					os.write(msg.getBytes());
					
					String[] parts = msg.split(" ");
					
					String missatge = "";
					
					for(int i = 1;i < parts.length;i++) {
						missatge = missatge + " " + parts[i]; 
					}
					
			        DBObject document = new BasicDBObject();
			        document.put("Data", data.toString());
			        document.put("Missatge", missatge);
			        document.put("Usuari", nom);
			        
			        historial.insert(document);
					
				    
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} 
		});
	    
	    JPanel panelRight = new JPanel();
	    frame.getContentPane().add(panelRight, BorderLayout.EAST);
	    panelRight.setLayout(new BoxLayout(panelRight, BoxLayout.Y_AXIS));
	    
	    JCalendar calendari = new JCalendar();
	    
	    
	    calendari.getDayChooser().addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (dia == 2) {
                	//display.setText("");
                	LocalDate dia = calendari.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                	
					BasicDBObject query = new BasicDBObject();
			        query.put("Data", dia.toString());
			        
			        DBCursor cur = historial.find(query);
			        
			        while(cur.hasNext()) {
			        	DBObject user = cur.next();
				    	display.append(user.get("Usuari") + ":" + user.get("Missatge") + newline);
			        }
                	
                } else {
                	dia++;
                }
            }
        });
	    
	    panelRight.add(calendari);
	    
	    
	    JLabel label = new JLabel("Per veure usuaris “/u”");
	    panelRight.add(label);
	    
	    JLabel lblNewLabel = new JLabel("Per escriure missatge al grup “/g missatge”");
	    panelRight.add(lblNewLabel);
	    
	    JLabel lblNewLabel_1 = new JLabel("Per escriure missatge privat“/p nom missatge”");
	    panelRight.add(lblNewLabel_1);
	    frame.pack ();
	    frame.setLocationRelativeTo(null);
	    frame.setVisible(true);
	    
	    frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {

				try {
	            	String msg = "/e";
					os.write(msg.getBytes());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            	
            	dispose();
            }
        });

        
	}
}
