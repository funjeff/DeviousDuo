package gameObjects;

import java.util.ArrayList;

import engine.GameCode;
import engine.GameObject;
import engine.Sprite;

public class Dickhead extends GameObject{

	public static final Sprite IDLE = new Sprite ("resources/sprites/config/DickheadIdle.txt");
	public static final Sprite JUMP = new Sprite ("resources/sprites/config/DickheadJump.txt");
	public static final Sprite KICK = new Sprite ("resources/sprites/config/DickheadKick.txt");
	public static final Sprite DUCK = new Sprite ("resources/sprites/config/DickheadDuck.txt");
	public static final Sprite UNDUCK = new Sprite ("resources/sprites/config/DickheadUnduck.txt");
	public static final Sprite KNOCKED_OVER = new Sprite ("resources/sprites/config/DickheadFallenOver.txt");
	
	
	public static final Sprite PUNCH_CHARGE = new Sprite ("resources/sprites/config/DickheadPunchWindup.txt");
	public static final Sprite PUNCH_RELEASE = new Sprite ("resources/sprites/config/DickheadPunchRelease.txt");
	public static final Sprite PUNCH_HOLD = new Sprite ("resources/sprites/config/DickheadPunchHold.txt");
	
	
	boolean ducking = false;
	boolean kicking = false;
	
	boolean jumping = false;
	
	boolean punchCharged = false;
	
	boolean launchedUp = false;
	
	boolean knockedOver = false;
	
	double ogX = 0;
	double ogY = 0;
	double vy = 0;
	
	int downTime = 0;
	
	AttackHitbox box = new AttackHitbox ();
	
	DamageStar star = new DamageStar ();
	
	public Dickhead () {
		this.setSprite(IDLE);
		this.getAnimationHandler().setFrameTime(200);
		this.useSpriteHitbox();
		//this.adjustHitboxBorders();
		this.enablePixelCollisions();
	}
	
	@Override
	public void onDeclare () {
		ogY = this.getY();
		ogX = this.getX();
	}
	
	@Override
	public void frameEvent () {
		
		
		if (!launchedUp && !knockedOver) {
	
			if (GameCode.keyCheck('S', this) && !this.getSprite().equals(UNDUCK) && !jumping && !punchCharged && !this.getSprite().equals(PUNCH_CHARGE) && !this.getSprite().equals(JUMP) && !this.getSprite().equals(PUNCH_RELEASE)) {
				if (!ducking) {
					this.setSprite(DUCK);
					this.getAnimationHandler().setFrameTime(70);
					this.setX(this.getX() - 22);
				}
				
				if (this.getAnimationHandler().isAnimationDone() && this.getSprite().equals(DUCK)) {
					this.getAnimationHandler().setFrameTime(0);
					this.getAnimationHandler().setAnimationFrame(this.getSprite().getFrameCount() - 1);
				}
				
				if (this.getAnimationHandler().getFrameTime() == 0 && GameCode.keyPressed('D', this)) {
					kicking = true;
					this.setSprite(KICK);
					
					this.setHitboxAttributes(0,0,this.getSprite().getWidth() - 20, this.getSprite().getHeight());
					this.dontUseSpriteHitbox();
					
					if (!box.declared()) {
						box.declare(this.getX() + 60, this.getY() + 50);
						box.setType(1);
						box.setHitboxAttributes(0,0,30,30);
					}
					
					this.getAnimationHandler().setFrameTime(100);
					this.setY(this.getY() + 18);
				}
				
				if (this.getAnimationHandler().getFrame() == 1 && this.getSprite().equals(KICK)) {
					this.getAnimationHandler().setFrameTime(40);
				}
				
				
				ducking = true;
				
			} else {
				if (!kicking) {
					if (ducking) {
						ducking = false;
						this.setSprite(UNDUCK);
						this.getAnimationHandler().setFrameTime(70);
					}
				}
			}
			
			if (this.getAnimationHandler().isAnimationDone() && this.getSprite().equals(KICK)) {
				this.getAnimationHandler().setFrameTime(0);
				kicking = false;
				this.setSprite(DUCK);
				this.useSpriteHitbox();
				box.forget();
				this.setY(this.getY() - 18);
				this.getAnimationHandler().setAnimationFrame(this.getSprite().getFrameCount() - 1);
			}
			
			if (this.getAnimationHandler().isAnimationDone() && this.getSprite().equals(UNDUCK)) {
				this.setSprite(IDLE);
				this.getAnimationHandler().setFrameTime(200);
				this.setX(this.getX() + 22);
			}
			
			if (GameCode.keyPressed('W', this) && !jumping && !ducking && !kicking && !punchCharged && !this.getSprite().equals(PUNCH_CHARGE) && !this.getSprite().equals(PUNCH_RELEASE) && !this.getSprite().equals(UNDUCK)) {
				this.setSprite(JUMP);
				this.getAnimationHandler().setFrameTime(30);
				this.setX(this.getX() + 2);
			}
			
			if (this.getAnimationHandler().getFrame() == 4 && this.getSprite().equals(JUMP) && !jumping) {
				jumping = true;
				vy = -10;
			}
			
			if (this.getAnimationHandler().isAnimationDone() && this.getSprite().equals(JUMP) && this.getAnimationHandler().getFrameTime() != 0) {
				this.getAnimationHandler().setFrameTime(0);
				this.getAnimationHandler().setAnimationFrame(this.getSprite().getFrameCount() - 1);
			}
			
			if (jumping) {
	
				vy = vy + .8;
				
				if (vy > 0) {
					if (!box.declared()) {
						box.declare(this.getX() + 2, this.getY() + 90);
						box.setType(2);
						box.setHitboxAttributes(0,0,44,15);
					}
					box.setY(this.getY() + 90);
				}
				
				this.setY(this.getY() + vy);
				if (this.getY() > ogY) {
					
					box.forget();
					this.setX(this.getX() - 2);
					this.setY(ogY);
					jumping = false;
					vy = 0;
					this.setSprite(IDLE);
					this.getAnimationHandler().setFrameTime(200);
				}
			}
			
			if (GameCode.keyCheck('A',this) && !jumping && !ducking && !kicking && !this.getSprite().equals(JUMP) && !this.getSprite().equals(UNDUCK) && !this.getSprite().equals(PUNCH_RELEASE)) {
				if (!punchCharged && !this.getSprite().equals(PUNCH_CHARGE)) {
					this.setSprite(PUNCH_CHARGE);
					this.getAnimationHandler().setFrameTime(130);
					this.setX(this.getX() - 32);
				}
			} else {
				if (punchCharged) {
					this.setSprite(PUNCH_RELEASE);
					this.getAnimationHandler().setFrameTime(60);
					punchCharged = false;
					this.setY(this.getY() - 12);
					
					
				}
				if (this.getSprite().equals(PUNCH_CHARGE)) {
					this.setSprite(IDLE);
					this.getAnimationHandler().setFrameTime(200);
					this.setX(this.getX() + 32);
				}
			}
			
			if (this.getAnimationHandler().getFrame() == this.getSprite().getFrameCount() - 2 && this.getSprite().equals(PUNCH_RELEASE)) {
				if (!box.declared()) {
					box.declare(this.getX() + 80, this.getY() + 10);
					box.setType(0);
					box.setHitboxAttributes(0,0,40,40);
				}
			}
			
			if (this.getAnimationHandler().isAnimationDone() && this.getSprite().equals(PUNCH_RELEASE)) {
				this.setSprite(IDLE);
				this.getAnimationHandler().setFrameTime(200);
				this.setX(this.getX() + 32);
				this.useSpriteHitbox();
				box.forget();
			}
			
			if (this.getAnimationHandler().isAnimationDone() && this.getSprite().equals(PUNCH_CHARGE)) {
				punchCharged = true;
				this.setSprite(PUNCH_HOLD);
				
				this.dontUseSpriteHitbox();
				
				this.setHitboxAttributes(0,0,this.getSprite().getWidth() - 20, this.getSprite().getHeight());
				
				this.getAnimationHandler().setFrameTime(150);
				this.setY(this.getY() + 12);
			}
		} else {
			
			if (launchedUp) {
				vy = vy + .8;
				
				this.setDrawRotation(this.getDrawRotation() + .08);
				
				this.setSprite(new Sprite ("resources/sprites/Dickhead/dickheadFreakOut.png"));
				
				this.setY(this.getY() + vy);
				if (this.getY() > ogY) {
					this.setDrawRotation(0);
					this.setY(ogY);
					launchedUp = false;
					vy = 0;
					this.setSprite(IDLE);
					
					star.declare(this.getX() + 100, this.getY() - 50);
					
					this.getAnimationHandler().setFrameTime(200);
				}
			}
			
			if (knockedOver) {
				downTime = downTime - 1;
				if (downTime == 0) {
					knockedOver = false;
					this.setSprite(IDLE);
					this.getAnimationHandler().setFrameTime(200);
					this.setY(ogY);
				}
				
			}
			
		}
	}
	
	public void takeDamage (int amount,int starTime) {
		star = new DamageStar ();
		star.declare(this.getX() + 100, this.getY() - 50);
		
		star.setDamage(amount);
		
		star.targetDickhead();
		star.setEndTimer(starTime);
	}
	
	public void launchUp (int damageAmount) {
		
		if (!launchedUp) {
			launchedUp = true;
			jumping = false;
			ducking = false;
			kicking = false;
			punchCharged = false;
			vy = -30;
			this.setX(ogX);
			this.setY(ogY);
			if (box.declared()) {
				box.forget();
			}
		}
		
		star = new DamageStar ();
		
		star.setDamage(damageAmount);
		
		star.targetDickhead();
	

		
	}
	
	public void knockOver (int downTime) {
		if (!knockedOver) {
			knockedOver = true;
			jumping = false;
			ducking = false;
			kicking = false;
			punchCharged = false;
			this.setSprite(KNOCKED_OVER);
			this.getAnimationHandler().setFrameTime(100);
			this.setX(ogX);
			this.setY(ogY + 30);
			if (box.declared()) {
				box.forget();
			}
		}
		this.downTime = downTime;
	}
	
	public class AttackHitbox extends GameObject {
		
		int attackType;
		
		//0 = punch hitbox
		//1 = kick hitbox
		//2 = jump on hitbox
		public AttackHitbox () {
			//this.adjustHitboxBorders();
			this.setGameLogicPriority(-100);
		}
		
		public void setType (int type) {
			attackType = type;
		}
		
		@Override
		public void frameEvent () {
			if (this.isCollidingChildren("GameObject")) {
				ArrayList <GameObject> colidingObjs = this.getCollisionInfo().getCollidingObjects();
				for (int i = 0; i < colidingObjs.size(); i++) {
					switch (attackType) {
					case 0:
						colidingObjs.get(i).gettingPunched();
					case 1:
						colidingObjs.get(i).gettingKicked();
					case 2:
						colidingObjs.get(i).gettingJumpedOn();
					}
				}
				
			}
		}
		
	}
	
}
