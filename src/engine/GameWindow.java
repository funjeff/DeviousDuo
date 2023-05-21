package engine;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.JFrame;

/**
 * The window which the game is displayed in. Also handles keyboard and mouse events.
 * @author nathan
 *
 */
public class GameWindow extends JFrame {

	/**
	 * Serial version ID, as specified by Swing?
	 */
	private static final long serialVersionUID = 8537802541411424289L;
	/**
	 * The image used as a drawing buffer
	 */
	private BufferedImage buffer;
	/**
	 * another image that is not scalled by resolution
	 */
	private BufferedImage nonScallableBuffer;
	
	
	private ArrayList <BufferedImage> inGameBufferes = new ArrayList <BufferedImage>();
	/**
	 * The InputManager used to detect input for this window
	 */
	private InputManager inputManager;
	
	/**
	 * Constructs a new GameWindow with the given width and height.
	 * @param width The initial width, in pixels, of the window content
	 * @param height The initial height, in pixels, of the window content
	 */
	public GameWindow (int width, int height) {
		//Makes sure that java closes when the window closes
		this.addWindowListener (new WindowAdapter() {
			public void windowClosing (WindowEvent e) {
				System.exit (0);
			}
		});
		//Sets the initial size of the window and makes it visible
		pack ();
		setSize (width + getInsets ().left + getInsets ().right, height + getInsets ().top + getInsets ().bottom);
		getContentPane ().setSize (width, height);
		setVisible (true);
		//Initializes the buffer for drawing
		buffer = new BufferedImage (width, height, BufferedImage.TYPE_4BYTE_ABGR);
		nonScallableBuffer = new BufferedImage (width, height, BufferedImage.TYPE_4BYTE_ABGR);
		//Sets up the input detection
		inputManager = new InputManager (this, this.getContentPane ());
	}
	
	/**
	 * Renders the contents of the buffers onto the window.
	 */
	public void refresh () {
		
		
		Graphics2D bufferGraphics = (Graphics2D) getNonscalableGraphics ();
		
		
		BufferedImage temp = new BufferedImage (getContentPane().getSize().width,getContentPane().getSize().height, BufferedImage.TYPE_4BYTE_ABGR);
		
		Graphics tempGraphics = temp.getGraphics();
		refreshScalableBuffer(tempGraphics);
		refreshForignBuffer(tempGraphics);
	
		tempGraphics.drawImage (nonScallableBuffer, 0, 0, getContentPane().getSize().width,getContentPane().getSize().height, null);
		bufferGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
		bufferGraphics.fillRect (0, 0, nonScallableBuffer.getWidth (), nonScallableBuffer.getHeight ());
		
		this.refreshFinal(temp);
		
		
	}
	
	/**
	 * Renders the contents of the buffer onto the window.
	 */
	public void refreshScalableBuffer (Graphics tempGraphics) {
		Graphics bufferGraphics = getBufferGraphics ();
		tempGraphics.drawImage (buffer, 0, 0, getContentPane().getSize().width,getContentPane().getSize().height, null);	
		
		bufferGraphics.setColor (new Color (0xC0C0C0));
		bufferGraphics.fillRect (0, 0, buffer.getWidth (), buffer.getHeight ());
	}
	
	public void refreshForignBuffer (Graphics tempGraphics) {
		for (int i = 0; i < inGameBufferes.size(); i++) {
			tempGraphics.drawImage (inGameBufferes.get(i), 0, 0, getContentPane().getSize().width,getContentPane().getSize().height, null);	
		}
		inGameBufferes = new ArrayList <BufferedImage> ();
	}
	
	public void refreshFinal(BufferedImage img) {
		Graphics windowGraphics = getContentPane ().getGraphics ();	
		windowGraphics.drawImage (img, 0, 0, getContentPane().getSize().width,getContentPane().getSize().height, null);
	}
	
	/**
	 * Gets the graphics object referring to this GameWindow's buffer.
	 * @return The buffer for this GameWindow's graphics
	 */
	public Graphics getBufferGraphics () {
		return buffer.getGraphics ();
	}
	/**
	 * Gets the graphics object referring to this GameWindow's nonsalable buffer.
	 * @return The buffer for this GameWindow's nonscalable graphics
	 */
	public Graphics getNonscalableGraphics () {
		return nonScallableBuffer.getGraphics ();
	}
	
	public void renderForignBuffer (BufferedImage toRender) {
		inGameBufferes.add(toRender);
	}
	
	/**
	 * Gets the BufferedImage used to draw to this GameWindow
	 * @return the BufferedImage used to draw to this GameWindow
	 */
	/*public BufferedImage getBuffer () {
		return buffer;
	}*/
	
	/**
	 * Gets the dimensions of the buffer, e.g. the resolution of the output.
	 * @return The dimensions of this GameWindow's buffer as an int array, in the format [width, height]
	 */
	public int[] getResolution () {
		return new int[] {buffer.getWidth (), buffer.getHeight ()};
	}
	/**
	 * Sets the resolution of the buffer to the given width and height; erases its contents.
	 * @param width The width to use, in pixels
	 * @param height The height to use, in pixels
	 */
	public void setResolution (int width, int height) {
		buffer = new BufferedImage (width, height, BufferedImage.TYPE_4BYTE_ABGR);
		
		Graphics bufferGraphics = buffer.getGraphics();
		
		bufferGraphics.setColor (new Color (0xC0C0C0));
		bufferGraphics.fillRect (0, 0, buffer.getWidth (), buffer.getHeight ());
	}
	
	public InputManager getInputImage () {
		return inputManager.createImage ();
	}
	public void zoom (double zoomVal) {
		this.setResolution((int)(960 * zoomVal), (int)(540 * zoomVal));
	}
	public void resetInputBuffers () {
		inputManager.resetKeyBuffers ();
		inputManager.resetMouseBuffers ();
	}
}