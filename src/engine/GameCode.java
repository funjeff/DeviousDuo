package engine;

import java.util.ArrayList;

import gameObjects.Background;
import gameObjects.Dickhead;
import gameObjects.Doucheman;
import gameObjects.HealthBar;
import gameObjects.MallCop;
import map.Room;
import java.awt.event.KeyEvent;



public class GameCode {
	
	

	static ArrayList <Asker> askers = new ArrayList <Asker> ();
	
	
	static Dickhead dick = new Dickhead();
	static Doucheman douche = new Doucheman();

	static HealthBar bar = new HealthBar ();
	
	static Background b = new Background ();

	public static void testBitch () {
		
		
	}
	
	public static void beforeGameLogic () {
		
	}

	public static void afterGameLogic () {
		
	}

	public static void init () {
		
	
		dick.declare(100,150);
		
		douche.declare(160, 350);
		
		MallCop c = new MallCop ();
		c.declare(600, 226);
		
		bar.declare(100, 505);

		b.setSprite(new Sprite ("resources/sprites/mall.png"));
		
		b.declare(0,0);
		
	}
		

	
	public static Dickhead getDickhead () {
		return dick;
	}
	
	public static Doucheman getDoucheman () {
		return douche;
	}
	
	public static HealthBar getBar () {
		return bar;
	}
	
	public static void gameLoopFunc () {
		
		ObjectHandler.callAll();
		
		 for (int i = 0; i < askers.size(); i++) {
		    	for (int j = 0; j < askers.get(i).getKeys().size(); j++) {
		    		if (!GameLoop.getInputImage().keyDown(askers.get(i).heldKeys.get(j))) {
		    			askers.get(i).getKeys().remove(j);
		    			j--;
		    		}
		    	}
		    }
		
	}
	
	  public static void removeAsker(GameObject asker) {
		  Asker toAsk = getAsker(asker);
		  askers.remove(toAsk);
	  }
	  
	  public static boolean keyCheck(int keyCode, GameObject whosAsking) {
			boolean returnValue = GameLoop.getInputImage().keyDown(keyCode);
		    
			Asker asking = getAsker(whosAsking);
			
			if (returnValue) {
				
				asking.getKeys().add(keyCode);
			}
			
			
			return returnValue;
		  }
		
		public static Asker getAsker (GameObject whosAsking) {
		
			Asker asking = null;
			
			boolean foundAsker = false;
			
			for (int i = 0; i < askers.size(); i++) {
				if (askers.get(i).isAsker(whosAsking)) {
					asking = askers.get(i);
					foundAsker = true;
					break;
				}
			}
			
			if (!foundAsker) {
				askers.add(new Asker(whosAsking));
				asking = askers.get(askers.size() -1);
			}
			
			return asking;
		}
		  
		  public static boolean keyPressed(int keyCode, GameObject whosAsking) {
			boolean returnValue = GameLoop.getInputImage().keyPressed(keyCode);
			
			Asker asking = getAsker(whosAsking);
			
			if (returnValue && !asking.getKeys().contains(keyCode)) {
				asking.getKeys().add(keyCode);
				return returnValue;
			} else {
				return false;
			}
			
			
		  }
		  
		  public static boolean keyReleased(int keyCode) {
		    return GameLoop.getInputImage().keyReleased(keyCode);
		  }
	
	
	public static void renderFunc () {
		Room.render();
		ObjectHandler.renderAll();
	}
	
	public static void beforeRender() {
		
	}
	
	public static void afterRender()
	{
		
	}
		
	public static int getResolutionX() {
		return RenderLoop.wind.getResolution()[0];
	}
	public static int getResolutionY() {
		return RenderLoop.wind.getResolution()[1];
	}
	

	


	
}
