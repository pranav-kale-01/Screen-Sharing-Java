import java.awt.AWTException;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class ScreenShare {
	ServerSocket  server;
	Socket socket;	
	boolean flag;

	public static void main(String[] args) {
		new ScreenShare().interactive();
	}

	private void loadGui() {
		Font large = new Font("Times New Roman", Font.PLAIN, 22 );

		JFrame f = new JFrame();
		f.setResizable(false);
		f.setVisible(true);
		f.setSize( 400, 600 ); 	
		f.setLayout( null ); 
		f.getContentPane().setBackground( Color.GRAY );

		// Join a meeting menu 
		Label h1 = new Label("Join a Meeting"); 
		h1.setFont( large ); 
		h1.setBounds( 30, 30, 200, 50 ); 
		h1.setForeground( Color.WHITE );
		f.add( h1 );

		Label l1 = new Label("Host Ip");
		l1.setFont( large );
		l1.setBounds( 80, 100, 300, 50 );
		l1.setForeground( Color.WHITE );
		f.add( l1 );

		TextField tf = new TextField(); 
		tf.setBounds( 80, 150, 200, 50 );
		tf.setFont( large );
		f.add( tf );

		Label l2 = new Label("Host Port");
		l2.setFont( large );
		l2.setBounds( 80, 200, 300, 50 );
		l2.setForeground( Color.WHITE );
		f.add( l2 );

		TextField tf2 = new TextField(); 
		tf2.setBounds( 80, 250, 200, 50 );
		tf2.setFont( large );
		f.add( tf2 );

		Button btn = new Button("Join");
		btn.setBounds( 80, 320, 200, 50 );
		btn.setFont( large ); 
		btn.addActionListener( new ActionListener( ) {
			public void actionPerformed( ActionEvent ae ) {
				// checking if the ip is valid
				String s = tf.getText();
				String s2 = tf2.getText();

				intepretCommand( "client " + s + " " + s2 );
					
			}
		});
		f.add( btn );


		// Host a meeting menu
		Button h2 = new Button("Host a Meeting"); 
		h2.setBounds( 30, 440, 200, 50 );
		h2.setFont( large ); 
		h2.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ae ) {
				intepretCommand("server 400" );
				// int startPortRange=1024;
				// int stopPortRange=65365;       
			
				// for(int i=startPortRange; i <=stopPortRange; i++)
				// {
				// 	ServerSocket ser=null;

				// 	try
				// 	{
				// 		ser = new ServerSocket(i);
				// 		final int port = i;
				// 		ser.close();

				// 		// port avaialable, Hosting the meeting... 
				// 		System.out.println("Hosting the meeting on port " + i );

				// 		server( port );
						
				// 		// Thread t1 = new Thread() {
				// 		// 	public void run() {
				// 		// 		server( port );
				// 		// 	}
				// 		// };

				// 		// t1.start();

				// 		return;
				// 	}
				// 	catch (Exception e)
				// 	{	
				// 		e.printStackTrace(); 
				// 	}
				// }
			}
		});
		f.add( h2 );
	}	

	private void interactive() {
		Scanner s = new Scanner(System.in);
		while (true) {
			try {
				InetAddress addr = InetAddress.getByName("localhost");
				System.out.print(addr.getCanonicalHostName() + ">>> ");
				if (s.hasNext()) {
					intepretCommand(s.nextLine());
				}
			} 
			catch (Exception e) {
				
			}
		}	
	}
	
	private void intepretCommand(String cmd) {
		StringTokenizer tokenizer = new StringTokenizer(cmd);
		String commandToken = tokenizer.nextToken();
		if (commandToken.equals("server")) {
			String port = tokenizer.nextToken();
			server(Integer.parseInt(port));
		} 
		else if (commandToken.equals("client")) {
			String serverAddr = tokenizer.nextToken();
			String port = tokenizer.nextToken();
			try {
				client(serverAddr, Integer.parseInt(port));
			}
			catch( Exception e ){
				e.printStackTrace();
			}
		} 
		else if (commandToken.equals("close")){
			close();
		} 
		else {
			System.out.println("Unrecognized Command");
		}

	}

	private void client(String serverAddr, int port) throws IOException {
		flag = true;
		JFrame frame = new JFrame();
		
		ImagePanel panel = new ImagePanel();
		ArrayList<BufferedImage> imageBuffer = new ArrayList<BufferedImage>();
		ArrayList<BufferedImage> renderBuffer = new ArrayList<BufferedImage>();

		frame.setTitle( "Virtual Classroom" );
		frame.setResizable(true);
		frame.setVisible(true);
		frame.getContentPane().setPreferredSize( new Dimension( 1366, 768 ) );
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				flag = false;
				frame.dispose();
			}
		});
		frame.setLayout( null );
		frame.add(panel);
		frame.pack();

		Dimension d = frame.getContentPane().getPreferredSize();
		panel.setSize( (int) d.getWidth()  , (int) d.getHeight() );

		try 
		{
			long startTime = System.currentTimeMillis();
			Random rand = new Random();

			int frameCount = 0 ;
			while(flag) {
				socket = new Socket(serverAddr, port);
				ObjectInputStream inputStream = new ObjectInputStream( socket.getInputStream() );
				
				panel.repaint();
		
				if( startTime > 1000 ){
					renderBuffer = ( ArrayList<BufferedImage> ) imageBuffer.clone();
					
					imageBuffer.clear();

					new RenderThread( renderBuffer, panel, panel.getSize() );
					startTime = System.currentTimeMillis();
				}

				imageBuffer.add( ImageIO.read( inputStream ) );
				socket.close();
			}
		} 
		catch( SocketException e ) {
			e.printStackTrace();
		}
		catch (IOException e) {
			frame.dispose();
			throw e;
		} 
		catch( Exception e ) {
			e.printStackTrace();
		}
	}
	
	private void close(){
		try {
			socket.close();
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void server(int port) {
		try {
			server = new ServerSocket(port);
			Robot r = new Robot();

			while(true){
				try{
					socket = server.accept();
					InetAddress addr = socket.getInetAddress();
					
					ObjectOutputStream outstream = new ObjectOutputStream(socket.getOutputStream());
					BufferedImage img;
					img = r.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));

					Point mouse = MouseInfo.getPointerInfo().getLocation();
					Graphics g = img.getGraphics();
					g.setColor(Color.RED);
					g.fillOval(mouse.x, mouse.y, 5, 5);

					ImageIO.write(img, "jpg", outstream);
					socket.close();
				} 
				catch( SocketException se ) {
					System.out.println("SE");
					throw se;
				}  
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		catch (AWTException e) {
			e.printStackTrace();
		} 
		catch (IOException e1) {
			e1.printStackTrace();
		} 
	}	
}