import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException; 
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.Socket;

import javax.imageio.ImageIO;
import javax.swing.JFrame; 

public class ServerResponseThread extends Thread { 
    Socket socket;

    ServerResponseThread( Socket s ) {
        this.socket = s;
        this.start();
    }

    public void run() {
        try {
            Robot r = new Robot();

            InetAddress addr = socket.getInetAddress();
            System.out.println( "Received Connection From " + addr.getCanonicalHostName() + " at " + addr.getHostAddress());
            ObjectOutputStream outstream = new ObjectOutputStream(socket.getOutputStream());

            JFrame frame = new JFrame();
            ImagePanel panel = new ImagePanel();

            while( true ) {
                try {
                    panel.repaint();

                    BufferedImage img;
                    img = r.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
                    ImageIO.write(img, "jpg", outstream);
                }
                catch( Exception e ) {
                    e.printStackTrace();
                    break;
                }
            }
        }
        catch( AWTException e ) { 
            e.printStackTrace();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }
}