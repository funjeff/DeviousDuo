package gameObjects;

import engine.GameObject;
import engine.Sprite;

public class HealthBar extends GameObject{
	
	DickheadHead dickhead = new DickheadHead ();
	DouchemanHead douchehead = new DouchemanHead ();
	
	
	public HealthBar () {
		this.setSprite(new Sprite ("resources/sprites/health gauge.png"));
		this.useSpriteHitbox();
	}
	
	@Override
	public void onDeclare () {
		
		dickhead.declare();
		douchehead.declare();
		
		dickhead.setX(this.getX() - 10);
		dickhead.setY(this.getY() + 5);
		
		douchehead.setX(this.getX() + this.hitbox().width - 10);
		douchehead.setY(this.getY() + 5);
	}
	
	public void damageDickhead (int amount) {
		dickhead.setX(dickhead.getX() + amount);
		
		if (dickhead.getX() + dickhead.hitbox().width > douchehead.getX()) {
			this.gameOver();
		}
	}
	
	public void damageDoucheman (int amount) {
		douchehead.setX(douchehead.getX() - amount);
		
		if (dickhead.getX() + dickhead.hitbox().width  > douchehead.getX()) {
			this.gameOver();
		}
	}
	
	public void gameOver () {
		
	}
	
	public DickheadHead getDichead () {
		return dickhead;
	}
	
	public DouchemanHead getDouchehead () {
		return douchehead;
	}
	
	public class DickheadHead extends GameObject {
		public DickheadHead () {
			this.setSprite(new Sprite ("resources/sprites/Dickhead/DickheadHead.png"));
			this.useSpriteHitbox();
		}
	}

	public class DouchemanHead extends GameObject {
		public DouchemanHead () {
			this.setSprite(new Sprite ("resources/sprites/Doucheman/douchemanHead.png"));
			this.getAnimationHandler().setFlipHorizontal(true);
			this.useSpriteHitbox();
		}
	}
	
}
