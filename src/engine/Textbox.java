package engine;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Stack;

import gameObjects.LetterBomb;
import map.Room;



public class Textbox extends GameObject {
	//Jeffrey please comment your code
	//Alternatively, Tbox can be used
	int timer;
	int spaceManipulation;
	public Sprite textBoxTop;
	public Sprite textBoxBottum;
	public Sprite textBoxBackground;
	public Sprite textBoxSides;
	public Sprite fontSheet;
	
	String text;
	String font;
	
	int width;
	int height;
	int textSize = 16;
	int defaultSize = 16;
	
	int endSize = 16;
	
	int largestSize = 0;
	
	//hackyest shit Ive ever done in my life
	int changeTextShake = 0;
	int newLine = 0;
	
	double lineSpacing = 2;

	ArrayList <double []> shakeInfo = new ArrayList <double []> ();
	
	boolean renderBox;
	
	String tempColor = "text (red)";
	
	private static HashMap<String, Sprite> resourceCache = new HashMap<String, Sprite> ();

	
	int amountScrolled = 0;
	int scrollSpeed = 0;
	int scrollPaused = 0;
	
	boolean defaultSizeUsed = true;
	
	long curTime = System.currentTimeMillis();
	
	ArrayList <String> reserveMessages = new ArrayList <String> ();
	
	QueryWindow query = null;
	int queryResult = -1;
	
	boolean textEmptied = false;
	
	boolean queryRecentlyEnded = false;
	
	
	// put filepath of fontsheet to use as the font
	public Textbox (String textToDisplay){
		super();
		renderBox = true;
		this.setFont ("normal");
		this.setBox ("Automobiles");
		spaceManipulation = 0;
		text = textToDisplay;
		this.setDimentions();
		if (!(this instanceof QueryWindow)) {
			query = new QueryWindow(""); //query windows dont need query windows
		}
	}
	//changes wheather or not to unpause the game after the textbox is done
	public void changeWidth (int newWidth) {
		width = newWidth * 16;
	}
	public void changeHeight(int newHeigh) {
		height = newHeigh * 16;
	}
	public void changeBoxVisability () {
		renderBox = !renderBox;
	}
	public void changeText(String newText) {
		text = newText;
	}
	public String getText () {
		return text;
	}
	
	public void pushString(String toPush) {
		
		ArrayList <String> segments = bSplit(toPush);
		
		if (segments.size() != 1) {
			for (int i = 0; i < segments.size(); i++) {
				pushString(segments.get(i));
			}
		}
		
		
		//if we are posting a query with nothing else before it push it up immeditly
		if (toPush.charAt(0) == '~' && toPush.charAt(1) == 'Q' && reserveMessages.isEmpty()) {
			String[] choices = toPush.substring(2).split(":");
			
			if (query != null) {
				if (textSize > largestSize) {
					largestSize = textSize;
				}
				query.setDefultSize(this.largestSize);
				query.setX(this.getX());
				query.setY(this.getY());
				query.activate(choices);
			}
		} else {
			reserveMessages.add(toPush);
		}
		if (queryRecentlyEnded) {
			queryRecentlyEnded = false;
			advanceText();
		}
	}
	/**
	 * returns the text without any of the identifiers (ie stuff in tildes)
	 * @return
	 */
	public String getPureText() {
	String returnText = "";
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == '~') {
				i = this.simulateTilde(text, i);
			}
			returnText = returnText + text.charAt(i);
		}
		return returnText;
	}
	
	public void setTransparancy (float transparancy) {
		String clean = "";
		
		boolean read = true;
		boolean inTilde = false;
		
		for (int i = 0; i < text.length(); i++) {
			
			if (read) {
				if (text.charAt(i) == '~' && (text.charAt(i + 1) == 'T' && !inTilde)) {
					read = false;
				} else {
					if (text.charAt(i) == '~') {
						inTilde = !inTilde;
					}
					clean = clean + text.charAt(i);
				}
			} else {
				if (text.charAt(i) == '~') {
				 	read = true;
			 	}
			}
		}
		//clean now has no transparnsy identifyers in it
		
		clean = "~T" + transparancy + "~" + clean;
		this.changeText(clean);
	}
	
	/**
	 * @param pos the position of a charictar of intrest
	 * @return the real place that charictar is at in the string (takeing tildes into account)
	 */
	public int getRealPos (int pos) {
		int posSoFar = 0; //amount of charictars passed including tildes
		
		int distanceTraveled = 0; // amount of real charictars pased (non tildes)
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == '~') {
				int newI = this.simulateTilde(text, i);
				posSoFar = posSoFar + (newI - i);
				i = newI;
			}
			
			distanceTraveled = distanceTraveled + 1;
		
			
			
			if (distanceTraveled == pos) {
				return posSoFar;
			}
			posSoFar = posSoFar + 1;
		}
		return posSoFar;
	}
	
	/**
	 * the inverse of get real pos takes a position with tildes included and tells you where it would be without tildes
	 * if a tilde is inputed it will use the first charictar after that tilde instead
	 * @param pos the position of a charictar of intrest with tildes
	 * @return the real place that charictar would be in the string if tildes were gone 
	 */
	public int getFakePos (int pos) {
		
		int distanceTraveled = 0; // amount of real charictars pased (non tildes)
		
		for (int i = 0; i <= pos; i++) {
			if (text.charAt(i) == '~') {
				try {
					while (text.charAt(i) == '~') {
						i = this.simulateTilde(text, i); //fake charictar skip it
					}
				} catch (IndexOutOfBoundsException e) {
					break;
				}
			} else {
				distanceTraveled = distanceTraveled + 1; //real charictar
			}	
		}
		return distanceTraveled;
	}
	
	/**
	 * replaces an area of text with spaces starting at startPos
	 * leaves a few behind intentionally for an effect
	 * @param startPos the starting index for the area to clear out
	 */
	public void whiteOut (int startPos) {
		int currentPos = startPos;
		
		Random rand = new Random ();
		
		while (text.charAt(currentPos) != ' ' || text.charAt(currentPos + 1) != ' ' || text.charAt(currentPos + 2) != ' ') {
			if (text.charAt(currentPos) == '~') {
				currentPos = this.simulateTilde(text, currentPos);
			}
			if (rand.nextInt(5) != 2) {
				text = text.substring(0, currentPos) + ' ' + text.substring(currentPos + 1);
			}
			currentPos = currentPos + 1;
		}
	}
	public void whiteOut (int startPos, int endPos) {
		int currentPos = startPos;
		
		Random rand = new Random ();
		
		while (currentPos < endPos) {
			if (text.charAt(currentPos) == '~') {
				currentPos = this.simulateTilde(text, currentPos);
			}
			if (rand.nextInt(5) != 2) {
				text = text.substring(0, currentPos) + ' ' + text.substring(currentPos + 1);
			}
			currentPos = currentPos + 1;
		}
	}
	public double getLineSpacing() {
		return lineSpacing;
	}
	public void setLineSpacing(double lineSpacing) {
		this.lineSpacing = lineSpacing;
	}
	
	public boolean isFull () {
		if (this.getTextLength() >= this.getSpace()) {
			return true;
		} else {
			return false;
		}
	}
	
	
	public void setDimentions() {
		int [] size = getTextSpaceRequirments();
		
		width = size[0];
		height = size[1];
	}
	
	public int getTextLength () {
		int length = 0;
		
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == '~') {
				i = this.simulateTilde(text, i);
			}
			
			length = length + 1;
		}
		return length;
	}
	/**
	 * retuns the amount of space from this box thats actually used
	 */
	public int [] getTextSpaceUsage() {
		int textSize = this.textSize;
		int xSpaceUsed = 0;
		int ySpaceUsed = 0;
		int currentlyTrackingX = 0;
		
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == '~') {
				int [] data = this.simulateTildeForSpace(text, i);
				i = data[0];
				if (data[1] >= 1) {
					ySpaceUsed = ySpaceUsed + textSize;
					data[1] = data[1] - 1;
					if (currentlyTrackingX > xSpaceUsed) {
						xSpaceUsed = currentlyTrackingX;
					}
					currentlyTrackingX = 0;
				}
				if (data[2] != -1) {
					textSize = data[2];
				}
				for (int j = 0; j < data[1]; j++) {
					ySpaceUsed = ySpaceUsed + textSize;
				}
			}
			
			currentlyTrackingX = currentlyTrackingX + textSize;
			if (currentlyTrackingX > this.width) {
				xSpaceUsed = this.width;
				currentlyTrackingX = 0;
				ySpaceUsed = ySpaceUsed + textSize;
			}
			
		}
		if (currentlyTrackingX > xSpaceUsed) {
			xSpaceUsed = currentlyTrackingX;
		}
		if (ySpaceUsed == 0) {
			ySpaceUsed = textSize;
		}
		
		int [] spacesUsed = {xSpaceUsed,ySpaceUsed};
		
		return spacesUsed;
		
	}
	/**
	 * returns the box sizes required to store this text
	 */
	public int [] getTextSpaceRequirments() {
		int textSize = this.textSize;
		int xSpaceUsed = 0;
		int ySpaceUsed = 0;
		int currentlyTrackingX = 0;
		
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == '~') {
				int [] data = this.simulateTildeForSpace(text, i);
				i = data[0];
				while (data[1] >= 1) {
					ySpaceUsed = (int) (ySpaceUsed + (textSize* lineSpacing));
					data[1] = data[1] - 1;
					if (currentlyTrackingX > xSpaceUsed) {
						xSpaceUsed = currentlyTrackingX;
					}
					currentlyTrackingX = 0;
				}
				if (data[2] != -1) {
					textSize = data[2];
				}
			}
			currentlyTrackingX = currentlyTrackingX + textSize;
			
		}
		if (currentlyTrackingX > xSpaceUsed) {
			xSpaceUsed = currentlyTrackingX;
		}
		if (ySpaceUsed == 0) {
			ySpaceUsed = textSize + 16; //anyone up for a magic number
		}
		
		int [] spacesUsed = {xSpaceUsed,ySpaceUsed};
		
		return spacesUsed;
		
	}
	public static Sprite getTextboxResource (String path, String parseStr) {
		return getTextboxResource(path,parseStr,1);
	}
	
	public static Sprite getTextboxResource (String path, String parseStr, float opacity) {
		//Construct the cache string
		String hashStr = path + ":" + parseStr + ":" + opacity;
		if (resourceCache.containsKey (hashStr)) {
			//Resource is cached
			return resourceCache.get (hashStr);
		} else {
			//Resource is not cached, load it
			ArrayList<String> parseStrs = new ArrayList<String> ();
			parseStrs.add (parseStr);
			Sprite spr = new Sprite (path, new SpriteParser (parseStrs));
			if (opacity != 1) {
				spr.setOpacity(opacity);
			}
			resourceCache.put (hashStr, spr);
			return spr;
		}
	}
	public void setFont (String fontName) {
		
		if (fontSheet == null) {
			fontSheet = getTextboxResource ("resources/sprites/Text/" + fontName + ".png", "grid 16 16",1);
		} else {
			fontSheet = getTextboxResource ("resources/sprites/Text/" + fontName + ".png", "grid 16 16",fontSheet.getOpacity());
		}
		
		tempColor = fontName;
		
		font = fontName;
		
	}
	
	
	private void setFontTemporarily (String fontName) {
		fontSheet = getTextboxResource ("resources/sprites/Text/" + fontName + ".png", "grid 16 16",fontSheet.getOpacity());
		
		tempColor = fontName;
		if (textSize != 16) {
			
			fontSheet = new Sprite (Sprite.getScaledArr(getTextboxResource ("resources/sprites/Text/" + tempColor + ".png", "grid "+ 16 + " " + 16,fontSheet.getOpacity()), textSize, textSize));
			
		}
	}
	
	public void setScrollSpeed(int speed) {
		scrollSpeed = speed;
	}
	
	private void resetFont () {
		if (!font.equals(tempColor)) {
			this.setFont(font);
		}
	}
	
	
	//I think Im gonna rewrite this at some point
	/*public void giveName (String boxName) {
		if (!boxName.equals("null")) {
			name = boxName;
			Random rand = new Random();
			name = name + "." +extentions[rand.nextInt(extentions.length)];
		}
	}*/
	public void setBox (String color) {
		textBoxTop = getTextboxResource ("resources/sprites/Text/windowsprites" + color + ".png", "rectangle 0 0 8 8",1);
		textBoxBottum = getTextboxResource ("resources/sprites/Text/windowsprites" + color + ".png", "rectangle 24 0 8 1",1);
		textBoxSides= getTextboxResource ("resources/sprites/Text/windowsprites" + color + ".png", "rectangle 16 0 1 8",1);
		textBoxBackground = getTextboxResource ("resources/sprites/Text/windowsprites" + color + ".png", "rectangle 8 0 8 8",1);
	}
	
	public int getSpace () {
		return (width * height)/256;
	}
	public void setTextSize(int textSize) {	
		fontSheet = new Sprite (Sprite.getScaledArr(getTextboxResource ("resources/sprites/Text/" + tempColor + ".png", "grid "+ 16 + " " + 16, fontSheet.getOpacity()), textSize, textSize));
		
		
		this.textSize = textSize;
		
		if (textSize > largestSize) {
			largestSize = textSize;
		}
	}
	
	public void setDefultSize(int textSize) {	
		
		if (this.textSize != textSize) {

			fontSheet = new Sprite (Sprite.getScaledArr(getTextboxResource ("resources/sprites/Text/" + tempColor + ".png", "grid "+ 16 + " " + 16, fontSheet.getOpacity()), textSize, textSize));
			
			this.textSize = textSize;
		}
		defaultSize= textSize;
}

	//will only return true once per emptyness
	public boolean isEmpty() {
		boolean wasEmpty = textEmptied;
		textEmptied = false;
		return wasEmpty;
	}
	
	public int getQueryResult() {
		int queryResult = this.queryResult;
		this.queryResult = -1;
		query.resetResult();
		return queryResult;
	}
	
	/**
	 * splits the message into a list of strings split up by ~B
	 * this is diffrent from the default split becasue it takes into account ending tildes
	 * ie something like ~S16~BROOO wont be split because that ~B is clearly not intended to be a split
	 */
	public ArrayList <String> bSplit (String toParse){
		
		ArrayList <String> toReturn = new ArrayList <String>();
		
		int segIndex = 0;
		
		for (int i = 0; i < toParse.length(); i++) {
			if (toParse.charAt(i) == '~') {
				int [] data = this.simulateTildeForSpace(toParse, i);
				if (data[3] != 0) {
					toReturn.add(toParse.substring(segIndex,i));
				}
				i = data[0];
				
				if (data[3] != 0) {
					segIndex = i+1;
				}
				
			}
		}
		toReturn.add(toParse.substring(segIndex));
		return toReturn;
	}
	
	public void advanceText() {
	//	System.out.println(reserveMessages);
		if (!reserveMessages.isEmpty()) {
			if (this.font.equals("Bomb")) {
				this.explodeOldText();
			}
			
			//pop the new regular message into the box
			//its worth noteing that there will NEVER be a query message as the next message for here because if there was it would have been poped on automatically before it makes it here
			text = "~S" + defaultSize + "~" + reserveMessages.get(0);
			amountScrolled = 0;
			reserveMessages.remove(0);
			
			if (!reserveMessages.isEmpty()) {
				//if there is a new query activate it immeditly
				if (reserveMessages.get(0).charAt(0) == '~' && reserveMessages.get(0).charAt(1) == 'Q') {
					String[] choices = reserveMessages.get(0).substring(2).split(":");
					
					if (query != null) {
						query.setDefultSize(this.largestSize);
						query.setX(this.getX());
						query.setY(this.getY());
						query.activate(choices);
					}
					reserveMessages.remove(0);
				}
			}
			
		} else {
			textEmptied = true;
		}
		
	}
	public void pauseScrolling (int howLong) {
		scrollPaused = howLong;
	}
	
	
	@Override
	public void frameEvent () {
		
		
		if (scrollPaused <= 0) {
			if ((System.currentTimeMillis() > curTime + scrollSpeed || (System.currentTimeMillis() > curTime + (scrollSpeed/2) && GameCode.keyCheck('X',this))) && scrollSpeed != 0) {
				long diffrence = System.currentTimeMillis() - curTime;
				if (!GameCode.keyCheck('X',this)) {
					amountScrolled = amountScrolled + (int)(diffrence/scrollSpeed);
				} else {
					amountScrolled = amountScrolled + (int)(diffrence/(scrollSpeed/2));
				}
				curTime = System.currentTimeMillis();
			}
		} else {
			if (scrollPaused + curTime < System.currentTimeMillis()) {
				curTime = curTime + scrollPaused;
				scrollPaused = 0;
			}
		}
		if (GameCode.keyCheck('C',this)) {
			amountScrolled = text.length();
		}
		if (GameCode.keyPressed(KeyEvent.VK_ENTER,this) && (amountScrolled >= getFakePos(text.length() - 1) || scrollSpeed == 0)) {
			advanceText();	
		}
	}
	
	//2017 Jeffrey appologizes for this garbage code (he would never admit it though)
	//EDIT I FINALLY FUCKIN REWROTE IT AFTER 5 FUCKING YEARS geez I can't belive ive been doing this for so long
public void drawBox () {

	//draws the box itself
	if (renderBox) {

		if (query != null) {
			query.drawBox();
			if (query.getResult() != -1) {
				
				queryRecentlyEnded = true;
				
				queryResult = query.getResult();
				query.deactivate();
			}
		}
		
		//draw the inside of the box tile by tile
		for (int i = 0; i < width/8; i++) {
			for (int j = 1; j < height/8; j++) {
				textBoxBackground.draw((int)((this.getX() - Room.getViewX()) + (i * 8)),(int) ( (this.getY() - Room.getViewY()) + (j * 8) -10));
			}
		}
		//deal with drawing the inside of the box when the width not a multple of 8
		if (width%8 != 0) {
			for (int j = 1; j < height/8; j++) {
				textBoxBackground.draw((int)((this.getX() - Room.getViewX()) + (this.width - (8 - (this.width%8)))),(int) ( (this.getY() - Room.getViewY()) + (j * 8) -10));
			}	
		}
		
		//deal with drawing the inside of the box when the height is not a multiple of 8
		if (height%8 != 0) {
			for (int j = 1; j < width/8; j++) {
				textBoxBackground.draw((int)((this.getX() - Room.getViewX()) + (j * 8)),(int) ( (this.getY() - Room.getViewY()) + (this.height - (8 - (this.height%8))) -10));
			}
		}
		
		//draw the borders on the top and bottom
		for (int i = 0; i < width/8; i++) {
			textBoxTop.draw((int)((this.getX() - Room.getViewX()) + (i*8)), (int)((this.getY() - Room.getViewY()) -10));
			textBoxBottum.draw((int)((this.getX() - Room.getViewX()) + (i*8)), (int)((this.getY() - Room.getViewY()) + (height) -10));
		}
		
		//deal with drawing the borders on the top and bottom when the box is not a multiple of 8 for size
		if (width%8 != 0) {
			textBoxTop.draw((int)((this.getX() - Room.getViewX()) + (this.width - (8 - (this.width%8)))), (int)((this.getY() - Room.getViewY()) -10));
			textBoxBottum.draw((int)((this.getX() - Room.getViewX()) + (this.width - (8 - (this.width%8)))), (int)((this.getY() - Room.getViewY()) + (height) -10));
		}
		
		//draw the borders on the side
		for (int i = 1; i < height/8; i++) {
			textBoxSides.draw((int)((this.getX() - Room.getViewX())), (int)((this.getY() - Room.getViewY())+ (i*8) -10));
			textBoxSides.draw((int)((this.getX() - Room.getViewX()) +(width)), (int)((this.getY()  - Room.getViewY())+ (i*8) -10));
		}
		//deal with drawing the borders on the sides when the box is not a multiple of 8 high
		
		if (height %8 != 0) {
			textBoxSides.draw((int)((this.getX() - Room.getViewX())), (int)((this.getY() - Room.getViewY())+ (this.height - (8 - (this.height%8))) -10));
			textBoxSides.draw((int)((this.getX() - Room.getViewX()) +(width)), (int)((this.getY()  - Room.getViewY())+ (this.height - (8 - (this.height%8))) -10));
		}
		
	}
		// translates the charictar in the message to a askii value that is used to specify position on the
		// text sheet run for every for every charitar in the message every frame
	
	int yPos = (int) this.getY();
	int xPos = (int) this.getX();
	
	
	boolean textShake = false;
	
	int shakeInfoNum = 0;
	

	
	
	for (int i = 0; i < text.length(); i++) {
		
		if (getFakePos(i) >= amountScrolled && scrollSpeed != 0) {
			break;
		}
		
		char drawChar = text.charAt(i);
		
		if (drawChar == '~') {
			int old = text.length();
			
			i = this.dealWithTilde(text, i);
			
			while (newLine != 0) {
				
				xPos = (int) this.getX();
				yPos = yPos + (int)(largestSize * lineSpacing);
				newLine = newLine - 1;
			}
			
			
			while (changeTextShake != 0) {
				textShake = !textShake;
				changeTextShake = changeTextShake - 1;
			}
			if (i >= text.length()) {
				break;
			}
			
			//still pretty jank lol
			//basically fixes the "letter appearing bug" by just ignoring it entirly
			//if it gets to a stop it just is done for now
			if (old != text.length()) {
				break;
			}
			
		}
		
		
		double shakeOffsetX = 0;
		double shakeOffsetY = 0;
		
		if (textShake) {
			double [] usableInfo;
			try {
				
				usableInfo = shakeInfo.get(shakeInfoNum);
				
			} catch (IndexOutOfBoundsException e) {
				Random rand = new Random ();
				
				double [] charInfo = new double [3];
				
				charInfo[0] = xPos;
				charInfo[1] = yPos;
				charInfo[2] = rand.nextInt(628) + 1;
				
				charInfo[2] = charInfo[2]/100;
				
				usableInfo = charInfo;
				
				shakeInfo.add(charInfo);
				
			}
			shakeInfoNum = shakeInfoNum + 1;
			
			usableInfo[0] = usableInfo[0] - Math.cos(usableInfo[2])/2;
			usableInfo[1] = usableInfo[1] - Math.sin(usableInfo[2])/2;
			
			shakeOffsetX = (xPos - usableInfo[0]);
			shakeOffsetY = (yPos - usableInfo[1]);
			
			
			if (Math.pow(shakeOffsetX,2) + Math.pow(shakeOffsetY,2) >= 4) {
			
				Random rand = new Random ();
				double newDirc = (rand.nextInt(157) + 1);
				
				
				if (shakeOffsetX > 0 && shakeOffsetY > 0) {
					if (!(usableInfo[2] > 3.14 && usableInfo[2] < 4.71)) {
						newDirc = newDirc + 314;
					} else {
						newDirc = usableInfo[2] * 100;
					}
				}
				
				if (shakeOffsetX < 0 && shakeOffsetY < 0) {
					if (usableInfo[2] > 1.57) {
						
					} else {
						newDirc = usableInfo[2] * 100;
					}
	
				}
				
				if (shakeOffsetX < 0 && shakeOffsetY > 0) {
					if (usableInfo[2] < 4.71) {
						newDirc = newDirc + 471;
					} else {
						newDirc = usableInfo[2] * 100;
					}
				}
				if (shakeOffsetX > 0 && shakeOffsetY < 0) {
					if (!(usableInfo[2] > 1.57 && usableInfo[2] < 3.14)) {
						newDirc = newDirc + 157;
					} else {
						newDirc = usableInfo[2] * 100;
					}
				}
				
				newDirc = newDirc/100.0;
				
				
				usableInfo[2] = newDirc;
				
			}
			
		}
		
		
		
		
		//makes it never seperate the same word on two lines
		//int lettersLeft = 0;
		int curPos = i;
		
		int wordDisplace = 0;
		
		int workingSize = textSize;
		
		while (curPos < text.length()) {
			if (text.charAt(curPos) == ' ') {
				break;
			}
			if (text.charAt(curPos) == '~') {
				int [] tildeInfo = simulateTildeForSpace(text, curPos);
				curPos = tildeInfo[0];
				if (tildeInfo[1] != 0) {
					break;
				}
				workingSize = tildeInfo[2];
			} else {
				curPos = curPos + 1;
				wordDisplace = wordDisplace + workingSize;
			}
		}
		
		
		int testSize = xPos + (wordDisplace);
		
		if ((testSize - this.getX()) > width) {
			xPos = (int) this.getX();
			yPos = yPos + (int)(largestSize * lineSpacing);
			if (yPos - this.getY() > height) {
				break;
			}
		}	
		
		if (xPos > Room.getViewX() && xPos < Room.getViewX() + GameCode.getResolutionX() && yPos > Room.getViewY() && yPos < Room.getViewY() + GameCode.getResolutionY()) {
		
			fontSheet.draw(xPos + (int)shakeOffsetX - Room.getViewX (), yPos + (int)shakeOffsetY - Room.getViewY (), text.charAt(i));
			if (textSize > largestSize) {
				largestSize = textSize;
			}
		}
		xPos = xPos + textSize;
		
	}
	endSize = textSize;
	if(textSize != defaultSize && defaultSizeUsed) {
		this.setTextSize(defaultSize);
	}
	this.resetFont();
}

public void explodeOldText()
{
	
	int yPos = (int) this.getY();
	int xPos = (int) this.getX();
	
	for (int i = 0; i < text.length(); i++) {
		
		char drawChar = text.charAt(i);
		
		
		
		if (drawChar == '~') {
			
			int [] data = this.simulateTildeForSpace(text, i);
			
			i = data[0];
			
			
			while (data[1] != 0) {
				
				xPos = (int) this.getX();
				yPos = yPos + (int)(largestSize * lineSpacing);
				data[1] =data[1] - 1;
			}
			
			if (i >= text.length()) {
				break;
			}
			
			
		}
		
		drawChar = text.charAt(i);
		
		//makes it never seperate the same word on two lines
		//int lettersLeft = 0;
		int curPos = i;
		
		int wordDisplace = 0;
		
		int workingSize = textSize;
		
		while (curPos < text.length()) {
			if (text.charAt(curPos) == ' ') {
				break;
			}
			if (text.charAt(curPos) == '~') {
				int [] tildeInfo = simulateTildeForSpace(text, curPos);
				curPos = tildeInfo[0];
				if (tildeInfo[1] != 0) {
					break;
				}
				workingSize = tildeInfo[2];
			} else {
				curPos = curPos + 1;
				wordDisplace = wordDisplace + workingSize;
			}
		}
		
		
		int testSize = xPos + (wordDisplace);
		
		if ((testSize - this.getX()) > width) {
			xPos = (int) this.getX();
			yPos = yPos + (int)(largestSize * lineSpacing);
			if (yPos - this.getY() > height) {
				break;
			}
		}	
		
		LetterBomb b = new LetterBomb (drawChar);
		b.declare(xPos + Room.getViewX(),yPos + Room.getViewY());
		b.setRenderPriority(this.getRenderPriority());
		
		xPos = xPos + textSize;
		
	}
}
private int simulateTilde (String message, int startI) {
		int i = startI + 1;
		char identifyingChar = message.charAt(i);
		switch (identifyingChar) {
			case 'C':
				i = i + 1;
				identifyingChar = message.charAt(i);
				while (identifyingChar != '~') {
					i = i + 1;
				
					identifyingChar = text.charAt(i);
				}
				i = i + 1;
				break;
			case 'S':
				i = i + 1;
				identifyingChar = message.charAt(i);
				
				String size = "";
				while (identifyingChar != '~') {
					size = size + identifyingChar;
					i = i + 1;
					identifyingChar = message.charAt(i);
				}
				i = i + 1;
				break;
			case 'T':
				i = i + 1;
				identifyingChar = message.charAt(i);
				
				while (identifyingChar != '~') {
					i = i + 1;
					identifyingChar = message.charAt(i);
				}
				i = i + 1;
				break;
			case 'A':
				i = i + 1;
				identifyingChar = message.charAt(i);
				
				while (identifyingChar != '~') {
					i = i + 1;
					identifyingChar = message.charAt(i);
				}
				i = i + 1;
				break;
			case 'R':
				i = i + 1;
				identifyingChar = message.charAt(i);
				
				while (identifyingChar != '~') {
					i = i + 1;
					identifyingChar = message.charAt(i);
				}
				i = i + 1;
				break;
			case 'P':
				i = i + 1;
				identifyingChar = message.charAt(i);
				
				while (identifyingChar != '~') {
					i = i + 1;
					identifyingChar = message.charAt(i);
				}
				i = i+1;
				break;
			case 'H':
				i = i +1;
				break;
			case 'N':
				i = i + 1;
				break;
			}
		
		try {
			if (message.charAt(i) == '~') {
				i = this.simulateTilde(message, i);
			}
		} catch (StringIndexOutOfBoundsException e) {
			
		}
		
		return i;
		}

private int [] simulateTildeForSpace (String message, int startI) {
	int newLineAmount = 0;
	int endSize = -1;
	int i = startI + 1;
	int splits = 0;
	
	
	char identifyingChar = message.charAt(i);
	switch (identifyingChar) {
		case 'C':
			i = i + 1;
			identifyingChar = message.charAt(i);
			while (identifyingChar != '~') {
				i = i + 1;
				identifyingChar = message.charAt(i);
			}
			i = i + 1;
			break;
		case 'S':
			i = i + 1;
			identifyingChar = message.charAt(i);
			
			String size = "";
			while (identifyingChar != '~') {
				size = size + identifyingChar;
				i = i + 1;
				identifyingChar = message.charAt(i);
			}
			endSize = Integer.parseInt(size);
			i = i + 1;
			break;
		case 'T':
			i = i + 1;
			identifyingChar = message.charAt(i);
			
			while (identifyingChar != '~') {
				i = i + 1;
				identifyingChar = message.charAt(i);
			}
			i = i + 1;
			break;
		case 'A':
			i = i + 1;
			identifyingChar = message.charAt(i);
			
			while (identifyingChar != '~') {
				i = i + 1;
				identifyingChar = message.charAt(i);
			}
			i = i + 1;
			break;
		case 'R':
			i = i + 1;
			identifyingChar = message.charAt(i);
			
			while (identifyingChar != '~') {
				i = i + 1;
				identifyingChar = message.charAt(i);
			}
			i = i + 1;
			break;
		case 'P':
			i = i + 1;
			identifyingChar = message.charAt(i);
			
			while (identifyingChar != '~') {
				i = i + 1;
				identifyingChar = message.charAt(i);
			}
			i = i+1;
			break;
		case 'H':
			i = i +1;
			break;
		case 'B':
			i = i + 1;
			splits = splits + 1;
			break;
		case 'N':
			newLineAmount = newLineAmount + 1;
			i = i + 1;
			break;
		}
	try {
		if (message.charAt(i) == '~') {
			int [] values = this.simulateTildeForSpace(message,i);
			i = values[0];
			newLineAmount = newLineAmount + values[1];
			if (values[2] != -1) {
				endSize = values[2];
			}
			splits = splits + values[3];
		}
	} catch (StringIndexOutOfBoundsException e) {
		
	}
	
	int [] returnValues = {i,newLineAmount,endSize,splits};
	return returnValues;
	}

/*
 * ~CcolorName~ changes text color to colorName
 * ~S8~ changes the text size to 8x8 
 * ~T.4~ changes the text transparency to .4
 * ~P100~ sets the scrollSpeed to 100 ms
 * ~H starts the text shake
 * ~N makes a newline at the current position
 * ~A100~ pauses the scrolling for 100 MS
 * ~B puts the next part in its own box
 * ~RClass.method.arg~ calls the "method" method in class Class with args arg the method must be 1 arg string containing no .'s you can leave it blank if you want and it will call it with a null instead 
 */
private int dealWithTilde (String message, int startI) {
	int i = startI;
	
	i = i + 1;
	char identifyingChar = message.charAt(i);
	switch (identifyingChar) {
		case 'C':
			i = i + 1;
			identifyingChar = message.charAt(i);
			
			String color = "";
			while (identifyingChar != '~') {
				color = color + identifyingChar;
				i = i + 1;
				identifyingChar = message.charAt(i);
			}
		
			i = i + 1;
			this.setFontTemporarily(color);
			break;
		case 'S':
			i = i + 1;
			identifyingChar = message.charAt(i);
			
			String size = "";
			while (identifyingChar != '~') {
				size = size + identifyingChar;
				i = i + 1;
				identifyingChar = message.charAt(i);
			}
			if (largestSize == 0) {
				
				defaultSizeUsed = false;
			}
			this.setTextSize(Integer.parseInt(size));
			i = i + 1;
			break;
		case 'T':
			i = i + 1;
			identifyingChar = message.charAt(i);
			
			String transparancy = "";
			while (identifyingChar != '~') {
				transparancy = transparancy + identifyingChar;
				i = i + 1;
				identifyingChar = message.charAt(i);
			}
			if (Float.parseFloat(transparancy) < 0) {
				transparancy = "0.0";
			}
			fontSheet = getTextboxResource ("resources/sprites/Text/" + tempColor + ".png", "grid "+ this.textSize + " " + this.textSize, Float.parseFloat(transparancy));
			break;
		case 'P':
			i = i + 1;
			identifyingChar = message.charAt(i);
			
			String speed = "";
			while (identifyingChar != '~') {
				speed = speed + identifyingChar;
				i = i + 1;
				identifyingChar = message.charAt(i);
			}
		
			i = i + 1;
			this.setScrollSpeed(Integer.parseInt(speed));
			break;
		case 'A':
			int tildPos = i;
			i = i + 1;
			identifyingChar = message.charAt(i);
			
			String time = "";
			while (identifyingChar != '~') {
				time = time + identifyingChar;
				i = i + 1;
				identifyingChar = message.charAt(i);
			} //pulls the info for the pause message
		
			i = i + 1;
			this.pauseScrolling(Integer.parseInt(time));
			
			//removes the pause message from the string so it can't be read again
		
			// I LOVE JANK
			// basically I don't know how to fix this "first letter appearing bug" so im just gonna always have the first letter be a space
			if (message.substring(i).charAt(0) != ' ') {
				message = message.substring(0,tildPos -1) + " " +  message.substring(i);
			} else {
				message = message.substring(0,tildPos -1) + message.substring(i);
			}
			this.changeText(message);

			i = tildPos - 2;
		//	System.out.println(this.getPureText().charAt(this.getFakePos(i)));
			//tells the textbox that it actually shouldant have scrollen this far yet
			if (amountScrolled > i) {
				amountScrolled = i;
			}
			
			break;
			
		case 'R':
			int tildPos2 = i;
			i = i + 1;
			identifyingChar = message.charAt(i);
			
			String fullArgs = "";
			while (identifyingChar != '~') {
				fullArgs = fullArgs + identifyingChar;
				i = i + 1;
				identifyingChar = message.charAt(i);
			} //pulls the info for the pause message
		
			i = i + 1;
			
			String [] indivdualArgs = fullArgs.split(".");
			
		try {
			Class <?> methClass = Class.forName(indivdualArgs[0]);
			
			String arg = null;
			
			try {
				arg = indivdualArgs[2];
			} catch (ArrayIndexOutOfBoundsException e2) {
				
			}
			
			try {
				methClass.getMethod(indivdualArgs[1],String.class).invoke(null,arg);
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
			
			//removes the pause message from the string so it can't be read again
		
			// I LOVE JANK
			// basically I don't know how to fix this "first letter appearing bug" so im just gonna always have the first letter be a space
		
			message = message.substring(0,tildPos2 -1) + message.substring(i);
			
			this.changeText(message);

			i = tildPos2 - 2;
		
			break;
			
		case 'H':
			changeTextShake = changeTextShake + 1;
			i = i +1;
			break;
		case 'N':
			newLine = newLine + 1;
			i = i + 1;
			break;
		}
	try {
		if (message.charAt(i) == '~') {
			i = this.dealWithTilde(message, i);
		}
	} catch (StringIndexOutOfBoundsException e) {
		
	}
	return i;
}

@Override
public void draw () {
		Rectangle thisRect = new Rectangle ((int)this.getX(), (int)this.getY(), this.width, this.height);
	
		Rectangle veiwport = new Rectangle ((int) Room.getViewX(), (int) Room.getViewY(), GameCode.getResolutionX (), GameCode.getResolutionY ());
		if (thisRect.intersects(veiwport)) {
			this.drawBox();
		}
	}

public class QueryWindow extends Textbox {
	String [] choices = null;
	
	Sprite selector = new Sprite ("resources/sprites/Text/selector.png");
	
	int ogY;
	
	boolean active = false;
	
	int result = -1;
	
	int hovered = 0;
	
	boolean enterDown = true;
	
	public QueryWindow (String options) {
		super (options);
		
	}
	
	public void activate (String[] newChoices) {
		active = true;
		choices = newChoices;
		result = -1;
		String choiceText = "~P0~";
		for (int i = 0; i < newChoices.length; i++) {
			if (i != newChoices.length) {
				choiceText = choiceText + newChoices[i] + "~N"; //I HAVE SOME CHOICE TEXT FOR YOU BUSTER
			} else {
				choiceText = choiceText + newChoices[i];
			}
		}
		ogY = (int)this.getY();
		this.changeText(choiceText);
		this.lineSpacing = 1.25;
		this.setDimentions();
		this.height = this.height + 8;
		this.setBox("Black");
		this.setFont("normal");
	}
	
	public void deactivate () {
		active = false;
		enterDown = true;
	}
	
	public int getResult() {
		return result;
	}
	
	public void resetResult () {
		result = -1;
	}
	
	@Override
	public void drawBox () {
		if (active || this.getY() != ogY) {
			if (active) {
				if (this.getY() > (ogY - (height - 5))) {
					this.setY(this.getY() - 8);
					if (this.getY() < (ogY - (height -5))) {
						this.setY(ogY - (height-5));
					}
				}
				
				
				super.drawBox();
				
				if (GameCode.keyPressed('S',this)) {
					if (hovered < choices.length -1) {
						hovered = hovered + 1;
					}
				}
				
				if (GameCode.keyPressed('W',this)) {
					if (hovered > 0) {
						hovered = hovered -1;
					}
				}
				
				selector.draw((int)(this.getX() -10), (int)this.getY() + ((int)(textSize*lineSpacing) *hovered));
				
				
				
				if (GameCode.keyCheck(KeyEvent.VK_ENTER,this)) {
					if (!enterDown) {
						result = hovered;
					}
				} else {
					enterDown = false; 
				}
				
			} else {
				this.setY(this.getY() +8);
				super.drawBox();
				if (this.getY() > ogY) {
					this.setY(ogY);
				}
			}
		}
	}
	
}
}