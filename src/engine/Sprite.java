package engine;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.imageio.ImageIO;




/**
 * Represents a drawable image
 * @author nathan
 *
 */
public class Sprite {
	/**
	 * The global image cache
	 */
	private static HashMap<String, CacheNode> cache = new HashMap<String, CacheNode> ();
	/**
	 * The image data this sprite contains
	 */
	private BufferedImage[] images;
	/**
	 * The filepath to this sprite, if applicable
	 */
	private String imagePath;
	/**
	 * The filepath to the parsing parameters, if applicable
	 */
	private String parsePath;
	/**
	 * Whether the sprite is animated or not
	 */
	private boolean isAnimated;
	/**
	 * does this get scalled by the changes of resolution
	 */
	protected boolean doesScale = true;
	
	private float opacity = 1;
	
	/**
	 * 
	 * Constructs a sprite with the given image filepath and parsing parameter filepath.
	 * @param imagepath The filepath to the image to use
	 * @param parsepath The filepath to the parsing parameters to use
	 */
	public Sprite (String imagepath, String parsepath) {
		this.imagePath = imagepath;
		this.parsePath = parsepath;
		String key = imagepath + ":" + parsepath;
		CacheNode data = cache.get (key);
		if (data == null) {
			SpriteParser parser = new SpriteParser (parsepath);
			File imageFile = new File (imagepath);
			BufferedImage img = null;
			try {
				img = ImageIO.read (imageFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			images = parser.parse (img);
			cache.put (key, new CacheNode (key, images));
		} else {
			images = data.getData ();
		}
		if (images.length > 1) {
			isAnimated = true;
		} else {
			isAnimated = false;
		}
	}
	
	/**
	 * Constructs a sprite with the given image and parsing parameter filepath. Does not support caching.
	 * @param image The image to use
	 * @param parsepath The filepath to the parsing parameters to use
	 */
	public Sprite (BufferedImage image, String parsepath) {
		this.parsePath = parsepath;
		SpriteParser parser = new SpriteParser (parsepath);
		images = parser.parse (image);
		if (images.length > 1) {
			isAnimated = true;
		} else {
			isAnimated = false;
		}
	}
	
	/**
	 * Constructs a sprite with the given image and parser. Does not support caching.
	 * @param image The image to use
	 * @param parser The parser to use
	 */
	public Sprite (BufferedImage image, SpriteParser parser) {
		images = parser.parse (image);
		if (images.length > 1) {
			isAnimated = true;
		} else {
			isAnimated = false;
		}
	}
	/**
	 * Constructs a sprite with the given image path and parser. Does not support caching.
	 * @param imagepath The image to use
	 * @param parser The parser to use
	 */
	public Sprite (String imagepath, SpriteParser parser) {
		this.imagePath = imagepath;
		File imageFile = new File (imagepath);
		BufferedImage img = null;
		try {
			img = ImageIO.read (imageFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		images = parser.parse (img);
		if (images.length > 1) {
			isAnimated = true;
		} else {
			isAnimated = false;
		}
	}
	
	/**
	 * Constructs a sprite with the given image filepath--or uses only the filepath of the parser if given a .txt filepath.
	 * @param imagepath The filepath to use for the image
	 */
	public Sprite (String imagepath) {
		String[] splitPath = imagepath.split ("\\.");
		if (splitPath.length != 0 && splitPath [splitPath.length - 1].equals ("txt")) {
		
			parsePath = imagepath;
			images = new SpriteParser (imagepath).parse ();
			if (images.length > 1) {
				isAnimated = true;
			} else {
				isAnimated = false;
			}
		} else {
			this.imagePath = imagepath;
			CacheNode data = cache.get (imagepath);
			if (data == null) {
				File imageFile = new File (imagepath);
				BufferedImage img = null;
				try {
					System.out.println(imageFile);
					img = ImageIO.read (imageFile);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				images = new BufferedImage[] {img};
				cache.put (imagepath, new CacheNode (imagepath, images));
			} else {
				images = data.getData ();
			}
			isAnimated = false;
		}
	}
	
	/**
	 * Copy constructor for ease of inheretence
	 * @param sprite The Sprite object to copy
	 */
	public Sprite (Sprite sprite) {
		this.images = sprite.images;
		this.isAnimated = sprite.isAnimated;
		this.imagePath = sprite.imagePath;
		this.parsePath = sprite.parsePath;
	}
	
	/**
	 * Gets the frame count of this sprite.
	 * @return The number of frames in this sprite
	 */
	public int getFrameCount () {
		return images.length;
	}
	/**
	 * returns the width of the sprite
	 * @return the width of the sprite
	 */
	public int getWidth() {
		return images[0].getWidth();
	}
	/**
	 * returns the height of the sprite
	 * @return the height of the sprite
	 */
	public int getHeight() {
		return images[0].getHeight();
	}
	/**
	 * Constructs a sprite with the given image. Does not support caching.
	 * @param image The image to use
	 */
	public Sprite (BufferedImage image) {
		images = new BufferedImage[] {image};
	}
	/**
	 * Constructs a sprite with the given images. Does not support caching.
	 * @param image The image to use
	 * why did it take so long to make this?
	 */
	public Sprite (BufferedImage [] frames) {
		images = frames;
	}
	
	/**
	 * Draws the first frame of this sprite at the given x and y coordinates.
	 * @param usedX The x coordinate to draw this sprite at
	 * @param usedY The y coordinate to draw this sprite at
	 */
	public void draw (int usedX, int usedY) {
		draw (usedX, usedY, 0);
	}
	public void drawRotated (int x, int y, int frame, double anchorX, double anchorY, double rotation) {
			AffineTransform transform = new AffineTransform ();
			transform.translate (x, y);
			transform.rotate (rotation, anchorX, anchorY);
			
			draw (x, y, frame, transform);
	}
	public void draw (double usedX, double usedY, int frame, AffineTransform transform) {
		Graphics2D windowGraphics;
			if (doesScale) {
				windowGraphics = (Graphics2D)RenderLoop.wind.getBufferGraphics ();
			} else {
				windowGraphics = (Graphics2D)RenderLoop.wind.getNonscalableGraphics ();
			}
			windowGraphics.drawImage (getFrame (frame), transform, null);
	}
	/**
	 * Draws the given frame of this sprite at the given x and y coordinates.
	 * @param usedX The x coordinate to draw this sprite at
	 * @param usedY The y coordinate to draw this sprite at
	 * @param frame The frame of this sprite to draw
	 */
	public void draw (int usedX, int usedY, int frame, BufferedImage toDraw) {
		if (frame < images.length) {
			toDraw.getGraphics().drawImage (images [frame], usedX, usedY, null);
		}
	}
	/**
	 * Draws the given frame of this sprite at the given x and y coordinates. with the given dimentions
	 * @param usedX The x coordinate to draw this sprite at
	 * @param usedY The y coordinate to draw this sprite at
	 * @param frame The frame of this sprite to draw
	 * @param width the width to cut the sprite off at
	 * @param height the height to cut the sprite off at
	 */
	public void draw (int usedX, int usedY, int frame, int width,int height, BufferedImage toDraw) {
		if (frame < images.length) {
			toDraw.getGraphics().drawImage (images[frame].getSubimage(0, 0, width, height), usedX, usedY, null);
		}
	}
	/**
	 * Draws the given frame of this sprite at the given x and y coordinates.
	 * @param usedX The x coordinate to draw this sprite at
	 * @param usedY The y coordinate to draw this sprite at
	 * @param flipHorizontal whether to apply horizontal flip
	 * @param flipVertical whether to apply vertical flip
	 * @param frame The frame of this sprite to draw
	 */
	public void draw (int usedX, int usedY, boolean flipHorizontal, boolean flipVertical, int frame, BufferedImage toDraw) {
		//Yeaaaaaaah this doesn't actually do anything special right now. TODO
		int x1, x2, y1, y2;
		if (flipHorizontal) {
			x1 = getFrame (frame).getWidth ();
			x2 = 0;
		} else {
			x1 = 0;
			x2 = getFrame (frame).getWidth ();
		}
		if (flipVertical) {
			y1 = getFrame (frame).getHeight ();
			y2 = 0;
		} else {
			y1 = 0;
			y2 = getFrame (frame).getHeight ();
		}
		if (frame < images.length) {
			toDraw.getGraphics().drawImage (getFrame (frame), usedX, usedY, usedX + getFrame (frame).getWidth (), usedY + getFrame (frame).getHeight (), x1, y1, x2, y2, null);
		}
	}
	/**
	 * Draws the given frame of this sprite at the given x and y coordinates.
	 * @param usedX The x coordinate to draw this sprite at
	 * @param usedY The y coordinate to draw this sprite at
	 * @param flipHorizontal whether to apply horizontal flip
	 * @param flipVertical whether to apply vertical flip
	 * @param frame The frame of this sprite to draw
	 * @param width The widht to draw too
	 * @param height the height to draw too
	 */
	public void draw (int usedX, int usedY, boolean flipHorizontal, boolean flipVertical, int frame, int width, int height, BufferedImage toDraw) {
		
		int x1, x2, y1, y2;
		if (flipHorizontal) {
			x1 = getFrame (frame).getWidth ();
			x2 = 0;
		} else {
			x1 = 0;
			x2 = getFrame (frame).getWidth ();
		}
		if (flipVertical) {
			y1 = getFrame (frame).getHeight ();
			y2 = 0;
		} else {
			y1 = 0;
			y2 = getFrame (frame).getHeight ();
		}
		if (frame < images.length) {
			toDraw.getGraphics().drawImage (getFrame (frame).getSubimage(0, 0, width, height), usedX, usedY, usedX + getFrame(frame).getWidth(), usedY + + getFrame(frame).getHeight(), x1, y1, x2, y2, null);
		}
	}
	/**
	 * Draws the given frame of this sprite at the given x and y coordinates.
	 * @param usedX The x coordinate to draw this sprite at
	 * @param usedY The y coordinate to draw this sprite at
	 * @param frame The frame of this sprite to draw
	 */
	public void draw (int usedX, int usedY, int frame) {
		if (frame < images.length) {
			if (doesScale) {
				RenderLoop.wind.getBufferGraphics ().drawImage (images [frame], usedX, usedY, null);
			} else {
				RenderLoop.wind.getNonscalableGraphics ().drawImage (images [frame], usedX, usedY, null);
			}
		}
	}
	/**
	 * Draws the given frame of this sprite at the given x and y coordinates. with the given dimentions
	 * @param usedX The x coordinate to draw this sprite at
	 * @param usedY The y coordinate to draw this sprite at
	 * @param frame The frame of this sprite to draw
	 * @param width the width to cut the sprite off at
	 * @param height the height to cut the sprite off at
	 */
	public void draw (int usedX, int usedY, int frame, int width,int height) {
		if (frame < images.length) {
			if (doesScale) {
				RenderLoop.wind.getBufferGraphics ().drawImage (	images[frame].getSubimage(0, 0, width, height), usedX, usedY, null);
			} else {
				RenderLoop.wind.getNonscalableGraphics ().drawImage (	images[frame].getSubimage(0, 0, width, height), usedX, usedY, null);
			}
		}
	}
	/**
	 * Draws the given frame of this sprite at the given x and y coordinates.
	 * @param usedX The x coordinate to draw this sprite at
	 * @param usedY The y coordinate to draw this sprite at
	 * @param flipHorizontal whether to apply horizontal flip
	 * @param flipVertical whether to apply vertical flip
	 * @param frame The frame of this sprite to draw
	 */
	public void draw (int usedX, int usedY, boolean flipHorizontal, boolean flipVertical, int frame) {
		//Yeaaaaaaah this doesn't actually do anything special right now. TODO
		int x1, x2, y1, y2;
		if (flipHorizontal) {
			x1 = getFrame (frame).getWidth ();
			x2 = 0;
		} else {
			x1 = 0;
			x2 = getFrame (frame).getWidth ();
		}
		if (flipVertical) {
			y1 = getFrame (frame).getHeight ();
			y2 = 0;
		} else {
			y1 = 0;
			y2 = getFrame (frame).getHeight ();
		}
		if (frame < images.length) {
			if (doesScale) {
				RenderLoop.wind.getBufferGraphics ().drawImage (getFrame (frame), usedX, usedY, usedX + getFrame (frame).getWidth (), usedY + getFrame (frame).getHeight (), x1, y1, x2, y2, null);
			} else {
				RenderLoop.wind.getNonscalableGraphics ().drawImage (getFrame (frame), usedX, usedY, usedX + getFrame (frame).getWidth (), usedY + getFrame (frame).getHeight (), x1, y1, x2, y2, null);
			}
		}
	}
	/**
	 * Draws the given frame of this sprite at the given x and y coordinates.
	 * @param usedX The x coordinate to draw this sprite at
	 * @param usedY The y coordinate to draw this sprite at
	 * @param flipHorizontal whether to apply horizontal flip
	 * @param flipVertical whether to apply vertical flip
	 * @param frame The frame of this sprite to draw
	 * @param width The widht to draw too
	 * @param height the height to draw too
	 */
	public void draw (int usedX, int usedY, boolean flipHorizontal, boolean flipVertical, int frame, int width, int height) {
		
		int x1, x2, y1, y2;
		if (flipHorizontal) {
			x1 = getFrame (frame).getWidth ();
			x2 = 0;
		} else {
			x1 = 0;
			x2 = getFrame (frame).getWidth ();
		}
		if (flipVertical) {
			y1 = getFrame (frame).getHeight ();
			y2 = 0;
		} else {
			y1 = 0;
			y2 = getFrame (frame).getHeight ();
		}
		if (frame < images.length) {
			if (doesScale) {
				RenderLoop.wind.getBufferGraphics ().drawImage (getFrame (frame).getSubimage(0, 0, width, height), usedX, usedY, usedX + getFrame(frame).getWidth(), usedY + + getFrame(frame).getHeight(), x1, y1, x2, y2, null);
			} else {
				RenderLoop.wind.getNonscalableGraphics ().drawImage (getFrame (frame).getSubimage(0, 0, width, height), usedX, usedY, usedX + getFrame(frame).getWidth(), usedY + + getFrame(frame).getHeight(), x1, y1, x2, y2, null);
			}
		}
	}
	/**
	 * Gets the BufferedImage representing the given frame of the sprite.
	 * @param frame The frame to get
	 * @return the given frame
	 */
	public BufferedImage getFrame (int frame) {
		return images [frame];
	}
	/**
	 * sets any frame of the animation to anything you want
	 * @param frame the frame to change
	 * @param newImage the new image
	 */
	public void setFrame (int frame, BufferedImage newImage) {
		images[frame] = newImage;
	}
	/**
	 * Gets the filepath of the image used to create this sprite.
	 * @return the filepath of this sprite's image; returns null if not applicable
	 */
	public String getImagePath () {
		return imagePath;
	}
	
	/**
	 * Gets the filepath of the file used to parse this sprite.
	 * @return the filepath used to parse this sprite; returns null if not applicable
	 */
	public String getParsePath () {
		return parsePath;
	}

	public static void scale (Sprite toScale, int width, int height) {
		for (int i = 0; i < toScale.getFrameCount(); i++) {
			Image img = toScale.getFrame(i).getScaledInstance(width, height, Image.SCALE_FAST);
			BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
			Graphics2D bGr = bimage.createGraphics();
		    bGr.drawImage(img, 0, 0, null);
		    bGr.dispose();
			toScale.setFrame(i,bimage);
		}
	}
	
	/**
	 * Gets the BufferedImage associated with the given filepath.
	 * @param path the filepath to use
	 * @return the resulting image; null if no image is found
	 */
	public static BufferedImage getImage (String path) {
		CacheNode data = cache.get (path);
		if (data == null) {
			File imageFile = new File (path);
			BufferedImage img = null;
			try {
				img = ImageIO.read (imageFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			BufferedImage[] currentImg = new BufferedImage[] {img};
			cache.put (path, new CacheNode (path, currentImg));
			return currentImg [0];
		} else {
			return data.getData () [0];
		}
	}
	
	public boolean doesScale() {
		return doesScale;
	}

	public void setScale(boolean doesScale) {
		this.doesScale = doesScale;
	}
	
	public static BufferedImage [] getScaledArr (Sprite toScale, int width, int height) {
		
		BufferedImage [] buff = new BufferedImage [toScale.getFrameCount()];
		
		for (int i = 0; i < toScale.getFrameCount(); i++) {
			Image img = toScale.getFrame(i).getScaledInstance(width, height, Image.SCALE_FAST);
			BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
			Graphics2D bGr = bimage.createGraphics();
		    bGr.drawImage(img, 0, 0, null);
		    
		    buff[i] = bimage;
		}
		return buff;
	}
	
	//lol got this from stack overflow
		public void setOpacity (float opacity, int frame) {
			
			String key = this.getImagePath();
			
			if (this.getParsePath() != null) {
				key = key + ":" + this.getParsePath();
			}
			
			this.images = cache.get(key).data;	
			
			BufferedImage [] arrCopy = new BufferedImage [this.images.length];
			
			for (int i = 0; i < arrCopy.length; i++) {
				arrCopy[i] = new BufferedImage (this.getFrame(i).getWidth(), this.getFrame(i).getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
				Graphics2D g = (Graphics2D) arrCopy[i].getGraphics();
				g.drawImage(this.getFrame(i), 0, 0, arrCopy[i].getWidth(), arrCopy[i].getHeight(), null);
			}
			
			cache.put(key, new CacheNode (key,arrCopy));
			
			BufferedImage newImg = new BufferedImage (this.getFrame(frame).getWidth(), this.getFrame(frame).getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
			Graphics2D g = (Graphics2D) newImg.getGraphics();
			AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,(float) opacity);
			g.setComposite(ac);
			g.drawImage(this.getFrame(frame), 0, 0, newImg.getWidth(), newImg.getHeight(), null);
			this.setFrame(frame, newImg);
			this.opacity = opacity;
			
			
		}
		
		public void setOpacity (float opacity) {
			
			String key = this.getImagePath();
			
			if (this.getParsePath() != null) {
				key = key + ":" + this.getParsePath();
			}
			
			this.images = cache.get(key).data;
			
			BufferedImage [] arrCopy = new BufferedImage [this.images.length];
			
			for (int i = 0; i < arrCopy.length; i++) {
				arrCopy[i] = new BufferedImage (this.getFrame(i).getWidth(), this.getFrame(i).getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
				Graphics2D g = (Graphics2D) arrCopy[i].getGraphics();
				g.drawImage(this.getFrame(i), 0, 0, arrCopy[i].getWidth(), arrCopy[i].getHeight(), null);
			}
			
			cache.put(key, new CacheNode (key,arrCopy));
			
			for (int i = 0; i < this.getFrameCount(); i++) {
				BufferedImage newImg = new BufferedImage (this.getFrame(i).getWidth(), this.getFrame(i).getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
				Graphics2D g = (Graphics2D) newImg.getGraphics();
				AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,opacity);
				g.setComposite(ac);
				g.drawImage(this.getFrame(i), 0, 0, newImg.getWidth(), newImg.getHeight(), null);
				this.setFrame(i, newImg);
				this.opacity = opacity;
			}
			
		}
		
		public float getOpacity () {
			return opacity;
		}
	
	private static class CacheNode {
		
		/**
		 * The number of times the data in this node has been accessed
		 */
		private int accessCount;
		/**
		 * The images stored in this node
		 */
		private BufferedImage[] data;
		/**
		 * The key associated with this node
		 */
		private String key;
		
		/**
		 * Construct a new CacheNode indexed by the given key, holding the given data.
		 * @param key The key to index this node
		 * @param data The data to store in this node
		 */
		public CacheNode (String key, BufferedImage[] data) {
			accessCount = 0;
			this.key = key;
			this.data = data;
			cache.put (key, this);
		}
		
		/**
		 * Gets the key value associated with this node.
		 * @return This node's key
		 */
		public String getKey () {
			return key;
		}
		
		/**
		 * Gets the data stored by this node.
		 * @return This node's data
		 */
		public BufferedImage[] getData () {
			accessCount ++;
			return data;
		}
		
		/**
		 * Gets the number of times getData has been called on this node.
		 * @return The number of calls to getData
		 */
		public int getAccessCount () {
			return accessCount;
		}
		
		/**
		 * Removes this element from the cache.
		 * @return True if the element was removed; false oftherwise
		 */
		public boolean remove () {
			return cache.remove (key, this);
		}
	}
}
