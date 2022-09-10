import java.util.ArrayList; 
import java.awt.image.BufferedImage;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Graphics2D;

public class RenderThread extends Thread {
	ArrayList<BufferedImage> renderBuffer = new ArrayList<BufferedImage>();
	ImagePanel panel;
    static Dimension dim; 

	public RenderThread( ArrayList<BufferedImage> renderBuffer, ImagePanel p, Dimension dim ) {
		this.renderBuffer = renderBuffer; 
		this.panel = p;
        RenderThread.dim = dim;
		this.start();
	}

	public void run (){
		for( int i=0 ; i< renderBuffer.size() ; i++ ) {
			panel.repaint(); 
			panel.setImg( resize( renderBuffer.get( i ) ) );

			try{ 
				Thread.sleep( 130 );
			}
			catch( Exception e ){ 

			}
		}
	}

    public static BufferedImage resize(BufferedImage img) { 
        int h = (int) dim.getHeight(); 
        int w = (int) dim.getWidth();

	    Image tmp = img.getScaledInstance( w, h, Image.SCALE_AREA_AVERAGING );
		tmp.setAccelerationPriority( 1.0f );
	    BufferedImage dimg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

	    Graphics2D g2d = dimg.createGraphics();
	    g2d.drawImage(tmp, 0, 0, null);
	    g2d.dispose();

	    return dimg;
	}  
}