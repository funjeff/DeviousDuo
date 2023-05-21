package gameObjects;

import engine.GameCode;
import engine.GameObject;
import engine.Sprite;
import gameObjects.MallCop.Light;

public class CopCar extends GameObject {

	boolean potentAttack = true;
	
	Light light = new Light ();
	
	public CopCar () {
		this.setSprite(new Sprite ("resources/sprites/config/CopCar.txt"));
		this.useSpriteHitbox();
		this.setRenderPriority(-1);
		
	}
	
	@Override
	public void frameEvent () {
		
		if (!light.declared()) {
			light.declare();
		}
		
		light.setX(this.getX() - 34);
		light.setY(this.getY() - 25);
		
		this.setX(this.getX() - 26);
		if (this.getX() + this.hitbox().width < 0) {
			forget();
		}
		
		if (this.isColliding(GameCode.getDickhead())) {
			GameCode.getDickhead().launchUp(30);
		}
		
		if (this.isColliding(GameCode.getDoucheman())) {
			GameCode.getDoucheman().launchUp(30);
		}
	}
	
	@Override
	public void gettingJumpedOn(){
		potentAttack = false;
	}
	
	@Override
	public void onForget () {
		light.forget();
	}
	
	
	public class Light extends GameObject {
		
		public Light () {
			
			this.setRenderPriority(100);
			this.setSprite(new Sprite("resources/sprites/config/CopLight.txt"));
			this.getAnimationHandler().setFrameTime(100);
		}
	}
}
