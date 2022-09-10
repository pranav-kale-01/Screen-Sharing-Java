import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.awt.image.BufferedImage;

public class ClientWorker extends SwingWorker {
    final String serverAddr;
    Socket socket;	
    ServerSocket  server;
    final Integer portNumber;
	boolean flag;
	RenderThread runningThread;

    public ClientWorker(String serverAddr, Integer portNumber) {
        this.serverAddr = serverAddr;
        this.portNumber = portNumber;
    }

    @Override
    protected Object doInBackground() throws Exception {
        System.out.println("Running this thing in the Client Worker");
        
        ArrayList<BufferedImage> imageBuffer = new ArrayList<BufferedImage>();
		ArrayList<BufferedImage> renderBuffer = new ArrayList<BufferedImage>();
		
		JFrame frame = new JFrame();
		ImagePanel panel = new ImagePanel();

		// configuring frame
		frame.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
		frame.setTitle( "Virtual Classroom" );
		frame.setResizable(true);
		frame.getContentPane().setPreferredSize(new Dimension( 1366, 768) );
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing( WindowEvent we) {
                try {
					if( runningThread != null && runningThread.isAlive() ) {
						runningThread.interrupt();
					}
					
					flag = false;
                    socket.close();
                }
                catch( Exception e ) {
                    e.printStackTrace();    
                }
				
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
			flag = true;
			while(flag) {
				System.out.println("Runnning Thread");
				socket = new Socket(serverAddr, portNumber);
				ObjectInputStream inputStream = new ObjectInputStream( socket.getInputStream() );
				
				panel.repaint();
		
				if( startTime > 1000 ){
					renderBuffer = ( ArrayList<BufferedImage> ) imageBuffer.clone();
					imageBuffer.clear();

					runningThread = new RenderThread( renderBuffer, panel, panel.getSize() );
					startTime = System.currentTimeMillis();
				}
				else {
					startTime += 1;
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
        return null;
    }
}
