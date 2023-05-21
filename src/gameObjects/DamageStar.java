package gameObjects;

import java.util.Random;

import engine.GameCode;
import engine.GameObject;
import engine.Sprite;

public class DamageStar extends GameObject {

	int damageAmount;

	boolean dickOrDouche = false;
	
	int displayTimer = 0;
	
	int endTimer = 20;
	
	public DamageStar () {
		Random r = new Random ();
		this.setSprite (new Sprite ("resources/sprites/star" + (r.nextInt(16) + 1) +".png"));
		this.useSpriteHitbox();
	}

	public void setDamage (int damage) {
		damageAmount = damage;
	}

	public void targetDickhead () {
		dickOrDouche = true;
	}
	
	public void targetDoucheman () {
		dickOrDouche = false;
	}
	
	public void setEndTimer (int end) {
		endTimer = end;
	}
	
	@Override
	public void frameEvent () {
		super.frameEvent();
		
		displayTimer = displayTimer + 1;
		if (displayTimer > endTimer) {
			if (dickOrDouche) {
				this.throwObj(GameCode.getBar().getDichead().getX(), GameCode.getBar().getDichead().getY(), 10);
				if (this.isColliding(GameCode.getBar().getDichead())) {
					GameCode.getBar().damageDickhead(damageAmount);
					this.forget();
				}
			} else {
				this.throwObj(GameCode.getBar().getDouchehead().getX(), GameCode.getBar().getDouchehead().getY(), 10);
				if (this.isColliding(GameCode.getBar().getDouchehead())) {
					GameCode.getBar().damageDoucheman(damageAmount);
					this.forget();
				}
			}
			
		}
	}
	
	
}
