package gameObjects;

import java.util.Random;

import engine.GameCode;
import engine.GameObject;
import engine.ObjectHandler;
import engine.Sprite;

public class MallCop extends GameObject{

	
	public static final Sprite UP_SPRITE = new Sprite ("resources/sprites/config/MallCopUp.txt");
	public static final Sprite DOWN_SPRITE = new Sprite ("resources/sprites/config/MallCopDown.txt");
	public static final Sprite SIDE_SPRITE = new Sprite ("resources/sprites/config/MallCopSides.txt");
	public static final Sprite IDLE_SPRITE = new Sprite ("resources/sprites/MallCop/MallCopIdle.png");
	public static final Sprite RADIO_SPRITE = new Sprite ("resources/sprites/config/MallCopRadio.txt");
	public static final Sprite RADIO_SEGWAY_SPRITE = new Sprite ("resources/sprites/config/MallCopRadioSegway.txt");
	
	public static final Sprite WATERING_CAN_SPRITE = new Sprite ("resources/sprites/config/MallCopWateringCan.txt");
	public static final Sprite STICK_SPRITE = new Sprite ("resources/sprites/config/MallCopStick.txt");
	public static final Sprite UNSTICK_SPRITE = new Sprite ("resources/sprites/config/MallCopUnstick.txt");
	public static final Sprite NO_SEGWAY = new Sprite ("resources/sprites/MallCop/MallCopNoSegway.png");
	
	double ogX;
	double ogY;
	
	double vx = 0;
	double vy = 0;
	
	//0 = not using an attack right now
	//1 = raming dickhead
	//2 = raming doucheman
	//3 = fake ram dickhead
	//4 = fake ram doucheman
	int currentAttack = 0;
	
	int attackDecisionBuffer = 0;
	
	//state is depended on attack used
	
	//if ramming
	//state 0 = ramming up towards dickhead or down towards docheman
	//state 1 = raming forward
	//state 2 = punched backwards
	//state 3 = getting back on segway after being punched back
	//state 4 = going back to starting position after failed attack
	//state 5 = going back to starting position after sucessful attack has not wrapped yet
	//state 6 = going back to starting position after sucessful attack has already wrapped
	
	
	//if fake ram
	//state 0 = ramming up towareds dickhead or down towards doucheman
	//state 1 = ramming forwards
	//state 2 = flying towards dickhead or doucheman after hitting rock
	//state 3 = getting back on segway
	//state 4 = riding back to starting position
	
	
	//if watering can attack
	//state 0 is expanding stick
	//state 1 is holding stick above head
	//state 2 is pulling stick back (sucessful attack)
	//state 3 is break stick
	
	//if car attack
	//state is unused
	
	int currentState = 0;
	
	
	Segway segway = new Segway ();
	
	GameObject end;
	
	Light light = new Light ();
	
	public MallCop () {
		this.setSprite(IDLE_SPRITE);
		this.useSpriteHitbox();
		//this.adjustHitboxBorders();
		light.declare();
	}
	
	@Override
	public void frameEvent () {
		
		
		if (this.getSprite().equals(IDLE_SPRITE)) {
			light.setX(this.getX() - 34);
			light.setY(this.getY() - 25);
			light.setDrawRotation(0);
		}
		
		if (this.getSprite().equals(RADIO_SEGWAY_SPRITE)) {
			light.setX(this.getX() - 48);
			light.setY(this.getY() - 15);
			light.setDrawRotation(-.1);
		}
		
		if (this.getSprite().equals(WATERING_CAN_SPRITE)) {
			light.setX(this.getX() - 34);
			light.setY(this.getY() - 25);
			light.setDrawRotation(0);
		}
		
		if (this.getSprite().equals(STICK_SPRITE)) {
			light.setX(this.getX() - 34);
			light.setY(this.getY() - 25);
			light.setDrawRotation(0);
		}	
		if (this.getSprite().equals(DOWN_SPRITE)) {
			light.setX(this.getX() + 9);
			light.setY(this.getY() - 25);
			light.setDrawRotation(.2);
		}
		
		if (this.getSprite().equals(UP_SPRITE)) {
			light.setX(this.getX() - 67);
			light.setY(this.getY() - 15);
			light.setDrawRotation(-.2);
		}
		
		if (this.getSprite().equals(SIDE_SPRITE)) {
			light.setX(this.getX() - 68);
			light.setY(this.getY() - 15);
			light.setDrawRotation(-.1);
			
			if (this.getAnimationHandler().flipHorizontal()) {
				light.setX(this.getX() - 12);
				light.setY(this.getY() - 32);
				light.setDrawRotation(.1);
				
			}
			
		}
		
		if (this.getSprite().equals(NO_SEGWAY)) {
			
			if (this.getDrawRotation() == -1.5) {
				light.show();
				light.setX(this.getX() - 56);
				light.setY(this.getY()  + 47);
				light.setDrawRotation(-1.5);
			} else {
				light.hide();
			}
			
		} else {
			
			if (!this.getSprite().equals(RADIO_SPRITE)) {
				light.show();
			}
		}
		
		
		if (this.getX() + this.hitbox().width < 0) {
			this.setX(960);
		}
		
		if (currentAttack == 0) {
			
			if (attackDecisionBuffer == 0) {
			
				Random r = new Random ();
				
				int attackChoice = r.nextInt(40);
//	//			
				if (attackChoice == 0) {
					currentAttack = 1;
					this.getAnimationHandler().setFrameTime(100);
					this.setSprite(UP_SPRITE);
					currentState = 0;
				}
//	//			
				if (attackChoice == 1) {
					currentAttack = 2;
					this.getAnimationHandler().setFrameTime(100);
					this.setSprite(DOWN_SPRITE);
					currentState = 0;
				}
//	////
				if (attackChoice == 2) {
					currentAttack = 3;
					this.getAnimationHandler().setFrameTime(100);
					this.setSprite(UP_SPRITE);
					Rock rock = new Rock ();
					
					rock.declare(GameCode.getDickhead().ogX + 200,GameCode.getDickhead().ogY + 80);
					currentState = 0;
				}
//	////			
				if (attackChoice == 3) {
					currentAttack = 4;
					this.getAnimationHandler().setFrameTime(100);
					this.setSprite(DOWN_SPRITE);
////	//				
					Rock rock = new Rock ();
////	//				
					rock.declare(GameCode.getDoucheman().ogX + 260,GameCode.getDoucheman().ogY + 50);
////	//				
					currentState = 0;
				}
//				
				if (attackChoice == 4) {
					currentAttack = 5;
					this.setSprite(WATERING_CAN_SPRITE);
////					
					this.getAnimationHandler().setFrameTime(100);
					currentState = 0;
				}
//				
				if (attackChoice == 5) {
					currentAttack = 6;
					this.setSprite(WATERING_CAN_SPRITE);
//					
					this.getAnimationHandler().setFrameTime(100);
					currentState = 0;
				}
				if (attackChoice == 6) {
					currentAttack = 7;
					this.setSprite(RADIO_SEGWAY_SPRITE);
//					
					this.getAnimationHandler().setFrameTime(100);
					currentState = 0;
				}
				
				
			} else {
				attackDecisionBuffer = attackDecisionBuffer - 1;
			}
			
		}
		
		if (currentAttack == 1) {
			if (currentState == 0) {
				this.setX(this.getX() - 7);
				this.setY(this.getY() - 7);
				
				if (this.getY() < GameCode.getDickhead().ogY) {
					currentState = 1;
					this.setSprite (SIDE_SPRITE);
				}
			}
			
			if (currentState == 1) {
				this.setX(this.getX() - 10);
				if (this.isColliding(GameCode.getDickhead())) {
					GameCode.getDickhead().launchUp(10);
					
					currentState = 5;
				}
			}
			
			if (currentState == 2) {
				
				
				this.setX(this.getX() + vx);
				this.setY(this.getY() - vy);
				
				this.setDrawRotation(this.getDrawRotation() + .03);
				
				vx = vx - .3;
				vy = vy -.3;
				
				boolean xGood = false;
				boolean yGood = false;
				
				if (vx < 0) {
					vx = 0;
					xGood = true;
					
				} 
				
				if (vy < -5) {
					vy = -5;
					yGood = true;
				} 
				
				if (xGood && yGood) {
					currentState = 3;
					this.setSprite(RADIO_SPRITE);
					segway.getAnimationHandler().setFrameTime(100);
					this.getAnimationHandler().setFrameTime(100);
				}
				
			}
			
			if (currentState == 3) {
				if (this.getAnimationHandler().isAnimationDone()) {
					this.getAnimationHandler().setFrameTime(0);
					this.getAnimationHandler().setAnimationFrame(this.getSprite().getFrameCount()-1);
					if (segway.getX() < this.getX() - 120) {
						segway.setX(segway.getX() + 4);
					} else {
						
						int check = 0;
						
						if (this.getDrawRotation() > 0) {
							this.setDrawRotation(this.getDrawRotation() - 0.09);
						} else {
							check = check + 1;
						}
						if (this.getX() > segway.getX() + 25) {
							this.setX(this.getX() - 5);
						} else {
							check = check + 1;
						}
						
						if (this.getY() > segway.getY() - 50) {
							this.setY(this.getY() - 5);
						} else {
							check = check + 1;
						}
						
						if (check == 3) {
							currentState = 4;
							segway.forget();
							this.setSprite(SIDE_SPRITE);
							this.getAnimationHandler().setFrameTime(100);
							this.getAnimationHandler().setFlipHorizontal(true);
						}
					}
				}
			}
			
			if (currentState == 4) {
				
				boolean xGood = false;
				
				if (this.getX() < ogX) {
					this.setX(this.getX() + 5);
				} else {
					this.setX(ogX);
					xGood = true;
				}
				
				boolean yGood = false;
				
				if (this.getY() != ogY) {
					
					if (this.getY() > ogY) {
						this.setY(this.getY() - 5);
						if (this.getY() < ogY) {
							this.setY(ogY);
						}
					}
					
					if (this.getY() < ogY) {
						this.setY(this.getY() + 5);
						if (this.getY() > ogY) {
							this.setY(ogY);
						}
					}
					
				} else {
					this.setY(ogY);
					yGood = true;
				}
				
				if (xGood && yGood) {
					currentAttack = 0;
					currentState = 0;
					this.setSprite(IDLE_SPRITE);
					attackDecisionBuffer = 50;
					this.getAnimationHandler().setFrameTime(0);
					this.getAnimationHandler().setFlipHorizontal(false);
				}
				
			}
			
			if (currentState == 6) {
				
				boolean xGood = false;
				boolean yGood = false;

				if (this.getX() > ogX) {
					this.setX(this.getX() - 12);
				} else {
					xGood = true;
				}
				
				if (this.getY() < ogY ) {
					this.setSprite(DOWN_SPRITE);
					this.setY(this.getY() + 12);
				} else {
					yGood = true;
					this.setSprite(SIDE_SPRITE);
				}
				
				if (xGood && yGood) {
					currentAttack = 0;
					currentState = 0;
					this.setSprite(IDLE_SPRITE);
					attackDecisionBuffer = 50;
					this.getAnimationHandler().setFrameTime(0);
				}
				
			}
			
			if (currentState == 5) {
								
				this.setX(this.getX() - 12);
				
				if (this.getX() + this.hitbox().width < 0) {
					currentState = 6;
				}
			}
					
		}
		
		
		
		if (currentAttack == 2) {
			if (currentState == 0) {
				this.setX(this.getX() - 7);
				this.setY(this.getY() + 7);
				
				if (this.getY() > GameCode.getDoucheman().ogY) {
					currentState = 1;
					this.setSprite (SIDE_SPRITE);
				}
			}
			
			if (currentState == 1) {
				this.setX(this.getX() - 10);
				if (this.isColliding(GameCode.getDoucheman())) {
					GameCode.getDoucheman().launchUp(10);
					currentState = 5;
				}
			}
			
			if (currentState == 2) {
				
				
				this.setX(this.getX() + vx);
				this.setY(this.getY() - vy);
				
				this.setDrawRotation(this.getDrawRotation() + .03);
				
				vx = vx - .3;
				vy = vy -.3;
				
				boolean xGood = false;
				boolean yGood = false;
				
				if (vx < 0) {
					vx = 0;
					xGood = true;
					
				} 
				
				if (vy < -5) {
					vy = -5;
					yGood = true;
				} 
				
				if (xGood && yGood) {
					currentState = 3;
					this.setSprite(RADIO_SPRITE);
					segway.getAnimationHandler().setFrameTime(100);
					this.getAnimationHandler().setFrameTime(100);
				}
				
			}
			
			if (currentState == 3) {
				if (this.getAnimationHandler().isAnimationDone()) {
					this.getAnimationHandler().setFrameTime(0);
					this.getAnimationHandler().setAnimationFrame(this.getSprite().getFrameCount()-1);
					if (segway.getX() < this.getX() - 120) {
						segway.setX(segway.getX() + 4);
					} else {
						
						int check = 0;
						
						if (this.getDrawRotation() > 0) {
							this.setDrawRotation(this.getDrawRotation() - 0.09);
						} else {
							check = check + 1;
						}
						if (this.getX() > segway.getX() + 25) {
							this.setX(this.getX() - 5);
						} else {
							check = check + 1;
						}
						
						if (this.getY() > segway.getY() - 50) {
							this.setY(this.getY() - 5);
						} else {
							check = check + 1;
						}
						
						if (check == 3) {
							currentState = 4;
							segway.forget();
							this.setSprite(SIDE_SPRITE);
							this.getAnimationHandler().setFrameTime(100);
							this.getAnimationHandler().setFlipHorizontal(true);
						}
					}
				}
			}
			
			if (currentState == 4) {
				
				boolean xGood = false;
				
				if (this.getX() < ogX) {
					this.setX(this.getX() + 5);
				} else {
					this.setX(ogX);
					xGood = true;
				}
				
				boolean yGood = false;
				
				if (this.getY() != ogY) {
					
					if (this.getY() > ogY) {
						this.setY(this.getY() - 5);
						if (this.getY() < ogY) {
							this.setY(ogY);
						}
					}
					
					if (this.getY() < ogY) {
						this.setY(this.getY() + 5);
						if (this.getY() > ogY) {
							this.setY(ogY);
						}
					}
					
				} else {
					this.setY(ogY);
					yGood = true;
				}
				
				if (xGood && yGood) {
					currentAttack = 0;
					currentState = 0;
					this.setSprite(IDLE_SPRITE);
					attackDecisionBuffer = 50;
					this.getAnimationHandler().setFrameTime(0);
					this.getAnimationHandler().setFlipHorizontal(false);
				}
				
			}
			
			if (currentState == 6) {
				
				boolean xGood = false;
				boolean yGood = false;

				if (this.getX() > ogX) {
					this.setX(this.getX() - 12);
				} else {
					xGood = true;
				}
				
				if (this.getY() > ogY ) {
					this.setSprite(UP_SPRITE);
					this.setY(this.getY() - 12);
				} else {
					yGood = true;
					this.setSprite(SIDE_SPRITE);
				}
				
				if (xGood && yGood) {
					currentAttack = 0;
					currentState = 0;
					this.setSprite(IDLE_SPRITE);
					attackDecisionBuffer = 50;
					this.getAnimationHandler().setFrameTime(0);
				}
				
			}
			
			if (currentState == 5) {
								
				this.setX(this.getX() - 10);
				
				if (this.getX() + this.hitbox().width < 0) {
					currentState = 6;
				}
			}
					
		}
		
	
		if (currentAttack == 3) {
			if (currentState == 0) {
				this.setX(this.getX() - 7);
				this.setY(this.getY() - 7);
				
				if (this.getY() < GameCode.getDickhead().ogY) {
					currentState = 1;
					this.setSprite (SIDE_SPRITE);
				}
			}
			
			if (currentState == 1) {
				this.setX(this.getX() - 10);
				if (this.isColliding("Rock")) {
					this.getCollisionInfo().getCollidingObjects().get(0).forget();
					currentState = 2;
					this.dontUseSpriteHitbox();
					this.setHitboxAttributes(0, -30, 37, 15);
					this.setSprite(NO_SEGWAY);
					segway.declare(this.getX(),this.getY() + 30);
					this.setY(this.getY() + 20);
					segway.setHitboxAttributes(0, 0);
				}
			}
			
			if (currentState == 2) {
				if (this.getDrawRotation() > -1.5) {
					this.setDrawRotation(this.getDrawRotation() - .3);
				}
				
				if (this.getX() > segway.getX()) {
					segway.useSpriteHitbox();
				}
				
				if (segway.getDrawRotation() > -1) {
					segway.setY(segway.getY() + 9);
					segway.setDrawRotation(segway.getDrawRotation() - .3);
				}
				
				this.setY(this.getY() + 2);
				this.setX(this.getX() - 25);
				
				if (this.isColliding(GameCode.getDickhead())) {
					GameCode.getDickhead().launchUp(10);
				}
				
				if (this.isColliding(segway)) {
					currentState = 3;
					this.useSpriteHitbox();
				}
			}
			
			if (currentState == 3) {
				
				int check = 0;
				
				if (this.getDrawRotation() < 0) {
					this.setDrawRotation(this.getDrawRotation() + .3);
					this.setY( this.getY() - 20);
				} else {
					this.setDrawRotation(0);
					check = check + 1;
				}
				
				
				if (segway.getDrawRotation() < 0) {
					segway.setY(segway.getY() - 9);
					segway.setDrawRotation(segway.getDrawRotation() + .3);
				} else {
					check = check + 1;
				}
				
				
				if (check == 2) {
					currentState = 4;
					this.setSprite(SIDE_SPRITE);
					this.getAnimationHandler().setFlipHorizontal(true);
					segway.forget();
					
				}
			}
			
			if (currentState == 4) {
				
				boolean xGood = false;
				
				if (this.getX() < ogX) {
					this.setX(this.getX() + 5);
				} else {
					this.setX(ogX);
					xGood = true;
				}
				
				boolean yGood = false;
				
				if (this.getY() != ogY) {
					
					if (this.getY() > ogY) {
						this.setY(this.getY() - 5);
						if (this.getY() < ogY) {
							this.setY(ogY);
						}
					}
					
					if (this.getY() < ogY) {
						this.setY(this.getY() + 5);
						if (this.getY() > ogY) {
							this.setY(ogY);
						}
					}
					
				} else {
					this.setY(ogY);
					yGood = true;
				}
				
				if (xGood && yGood) {
					currentAttack = 0;
					currentState = 0;
					this.setSprite(IDLE_SPRITE);
					attackDecisionBuffer = 50;
					this.getAnimationHandler().setFrameTime(0);
					this.getAnimationHandler().setFlipHorizontal(false);
				}
				
			}
			
		}
		
		
		if (currentAttack == 4) {
			if (currentState == 0) {
				this.setX(this.getX() - 7);
				this.setY(this.getY() + 7);
				
				if (this.getY() > GameCode.getDoucheman().ogY) {
					currentState = 1;
					this.setSprite (SIDE_SPRITE);
				}
			}
			
			if (currentState == 1) {
				this.setX(this.getX() - 10);
				if (this.isColliding("Rock")) {
					this.getCollisionInfo().getCollidingObjects().get(0).forget();
					currentState = 2;
					this.dontUseSpriteHitbox();
					this.setHitboxAttributes(0, -30, 37, 15);
					this.setSprite(NO_SEGWAY);
					segway.declare(this.getX(),this.getY() + 30);
					this.setY(this.getY() + 20);
					segway.setHitboxAttributes(0, 0);
				}
			}
			
			if (currentState == 2) {
				if (this.getDrawRotation() > -1.5) {
					this.setDrawRotation(this.getDrawRotation() - .3);
				}
				
				if (this.getX() > segway.getX()) {
					segway.useSpriteHitbox();
				}
				
				if (segway.getDrawRotation() > -1) {
					segway.setY(segway.getY() + 9);
					segway.setDrawRotation(segway.getDrawRotation() - .3);
				}
				
				this.setY(this.getY() + 2);
				this.setX(this.getX() - 25);
				
				if (this.isColliding(GameCode.getDoucheman())) {
					GameCode.getDoucheman().launchUp(10);
				}
				
				if (this.isColliding(segway)) {
					currentState = 3;
					this.useSpriteHitbox();
				}
			}
			
			if (currentState == 3) {
				
				int check = 0;
				
				if (this.getDrawRotation() < 0) {
					this.setDrawRotation(this.getDrawRotation() + .3);
					this.setY( this.getY() - 20);
				} else {
					this.setDrawRotation(0);
					check = check + 1;
				}
				
				
				if (segway.getDrawRotation() < 0) {
					segway.setY(segway.getY() - 9);
					segway.setDrawRotation(segway.getDrawRotation() + .3);
				} else {
					check = check + 1;
				}
				
				
				if (check == 2) {
					currentState = 4;
					this.setSprite(SIDE_SPRITE);
					this.getAnimationHandler().setFlipHorizontal(true);
					segway.forget();
					
				}
			}
			
			if (currentState == 4) {
				
				boolean xGood = false;
				
				if (this.getX() < ogX) {
					this.setX(this.getX() + 5);
				} else {
					this.setX(ogX);
					xGood = true;
				}
				
				boolean yGood = false;
				
				if (this.getY() != ogY) {
					
					if (this.getY() > ogY) {
						this.setY(this.getY() - 5);
						if (this.getY() < ogY) {
							this.setY(ogY);
						}
					}
					
					if (this.getY() < ogY) {
						this.setY(this.getY() + 5);
						if (this.getY() > ogY) {
							this.setY(ogY);
						}
					}
					
				} else {
					this.setY(ogY);
					yGood = true;
				}
				
				if (xGood && yGood) {
					currentAttack = 0;
					currentState = 0;
					this.setSprite(IDLE_SPRITE);
					attackDecisionBuffer = 50;
					this.getAnimationHandler().setFrameTime(0);
					this.getAnimationHandler().setFlipHorizontal(false);
				}
				
			}
		}
		
		if (currentAttack == 5) {
			if (currentState == 0) {
				if (this.getAnimationHandler().isAnimationDone() && this.getSprite().equals(WATERING_CAN_SPRITE)) {
					this.setSprite(STICK_SPRITE);
					WateringCan can = new WateringCan ();
					can.declare(this.getX() - 10, this.getY() + 50);
					can.setDrawRotation(-1);
					end = can;
				}
				
				if (this.getAnimationHandler().isAnimationDone() && this.getSprite().equals(STICK_SPRITE)) {
					Stick stick = new Stick (end);
					
					stick.setDrawRotation(-1);
					
					stick.setHitbox(0,0,20, 10);
					stick.declare();
					stick.setX(this.getX() - 10);
					stick.setY(this.getY() + 50);
					
					end = stick;
					this.getAnimationHandler().setAnimationFrame(0);
					
					Random r = new Random ();
					
					if (((Stick)end).getStickCount() % 3 == 0) {
						Crap c = new Crap ();
						c.declare(this.getX(), this.getY() + 30);
						if (r.nextBoolean()) {
							c.throwObj(GameCode.getDickhead().getCenterX() + 20,GameCode.getDickhead().getCenterY() + 20, 12);
						} else {
							c.throwObj(GameCode.getDoucheman().getCenterX() + 20,GameCode.getDoucheman().getCenterY() + 20, 12);
						}
					}
				}
				
				
				if (end instanceof Stick) {
					if (((Stick) end).getStickCount() == 25) {
						currentState = 1;
						this.getAnimationHandler().setFrameTime(0);
					}
				}
				
				
				Random r = new Random ();
				
				if (r.nextInt(20) == 0) {
					Crap c = new Crap ();
					c.declare(this.getX(), this.getY() + 30);
					c.throwObj(r.nextDouble() * 6.28, 12);
				}
				
				
			}
			
			if (currentState == 1) {
				WateringCan can = ((Stick)end).getCan();
				
				if (can.isEmpty()) {
					currentState = 2;
					this.setSprite(UNSTICK_SPRITE);
					this.getAnimationHandler().setFrameTime(35);
				}
				
			}
			
			if (currentState == 2) {
				
				if (this.getAnimationHandler().isAnimationDone()) {
					
					end.forget();
					
					if (end instanceof WateringCan) {
						currentAttack = 0;
						currentState = 0;
						this.setSprite(IDLE_SPRITE);
						attackDecisionBuffer = 50;
						this.getAnimationHandler().setFrameTime(0);
						
					} else {
						end = ((Stick)end).getEnd();
						end.setX(this.getX() - 10);
						end.setY(this.getY() + 50);
					}
					this.getAnimationHandler().setAnimationFrame(0);
				}
					
					
			}
			if (currentState == 3) {
				((Stick)end).breakSticks();
				end = null;
				currentAttack = 0;
				currentState = 0;
				this.setSprite(IDLE_SPRITE);
				attackDecisionBuffer = 50;
				this.getAnimationHandler().setFrameTime(0);
				this.getAnimationHandler().setFlipHorizontal(false);
			}
		}
		
		
		if (currentAttack == 6) {
			if (currentState == 0) {
				if (this.getAnimationHandler().isAnimationDone() && this.getSprite().equals(WATERING_CAN_SPRITE)) {
					this.setSprite(STICK_SPRITE);
					WateringCan can = new WateringCan ();
					can.declare(this.getX() - 10, this.getY() + 35);
					can.setDrawRotation(-1.5);
					end = can;
					can.setHeight(30);
				}
				
				if (this.getAnimationHandler().isAnimationDone() && this.getSprite().equals(STICK_SPRITE)) {
					Stick stick = new Stick (end);
					
					stick.setDrawRotation(-1.6);
					
					stick.setHitbox(0,0,20, -2);
					stick.declare();
					stick.setX(this.getX() - 10);
					stick.setY(this.getY() + 50);
					
					end = stick;
					this.getAnimationHandler().setAnimationFrame(0);
					
					Random r = new Random ();
					
					if (((Stick)end).getStickCount() % 3 == 0) {
						Crap c = new Crap ();
						c.declare(this.getX(), this.getY() + 30);
						if (r.nextBoolean()) {
							c.throwObj(GameCode.getDickhead().getCenterX() + 20,GameCode.getDickhead().getCenterY() + 20, 12);
						} else {
							c.throwObj(GameCode.getDoucheman().getCenterX() + 20,GameCode.getDoucheman().getCenterY() + 20, 12);
						}
					}
				}
				
				
				if (end instanceof Stick) {
					if (((Stick) end).getStickCount() == 22) {
						currentState = 1;
						this.getAnimationHandler().setFrameTime(0);
					}
				}
				
				
				Random r = new Random ();
				
				if (r.nextInt(20) == 0) {
					Crap c = new Crap ();
					c.declare(this.getX(), this.getY() + 30);
					c.throwObj(r.nextDouble() * 6.28, 12);
				}
				
				
			}
			
			if (currentState == 1) {
				WateringCan can = ((Stick)end).getCan();
				
				if (can.isEmpty()) {
					currentState = 2;
					this.setSprite(UNSTICK_SPRITE);
					this.getAnimationHandler().setFrameTime(35);
				}
				
			}
			
			if (currentState == 2) {
				
				if (this.getAnimationHandler().isAnimationDone()) {
					
					end.forget();
					
					if (end instanceof WateringCan) {
						currentAttack = 0;
						currentState = 0;
						this.setSprite(IDLE_SPRITE);
						attackDecisionBuffer = 50;
						this.getAnimationHandler().setFrameTime(0);
						
					} else {
						end = ((Stick)end).getEnd();
						end.setX(this.getX() - 10);
						end.setY(this.getY() + 50);
					}
					this.getAnimationHandler().setAnimationFrame(0);
				}
					
					
			}
			if (currentState == 3) {
				((Stick)end).breakSticks();
				end = null;
				currentAttack = 0;
				currentState = 0;
				this.setSprite(IDLE_SPRITE);
				attackDecisionBuffer = 50;
				this.getAnimationHandler().setFrameTime(0);
				this.getAnimationHandler().setFlipHorizontal(false);
			}
		}
		
		if (currentAttack == 7) {
			if (this.getAnimationHandler().getFrameTime() != 0 && this.getAnimationHandler().isAnimationDone()) {
				CopCar c1 = new CopCar ();
				CopCar c2 = new CopCar ();
				
				Random r = new Random ();
				
				c1.declare(960 + c1.hitbox().width + r.nextInt(500), GameCode.getDickhead().getY() + 60);
				c2.declare(960 + c2.hitbox().width + r.nextInt(500), GameCode.getDoucheman().getY() + 60);
				this.getAnimationHandler().setFrameTime(0);
				this.getAnimationHandler().setAnimationFrame(this.getSprite().getFrameCount()-1);
				
			}
			
			if (this.getAnimationHandler().getFrameTime() == 0 && (ObjectHandler.getObjectsByName("CopCar") == null || ObjectHandler.getObjectsByName("CopCar").isEmpty())) {
				currentAttack = 0;
				currentState = 0;
				this.setSprite(IDLE_SPRITE);
				attackDecisionBuffer = 50;
				this.getAnimationHandler().setFrameTime(0);
				this.getAnimationHandler().setFlipHorizontal(false);
			}
		}
		
	}

	
	@Override
	public void gettingPunched() {
		if (currentAttack == 1) {
			if (currentState == 1) {
				currentState = 2;
				this.setSprite(NO_SEGWAY);
				segway.declare(this.getX(),this.getY() + 30);
				vx = 12;
				vy = 5;
			}
		}
		
		if (currentAttack == 2) {
			if (currentState == 1) {
				currentState = 2;
				this.setSprite(NO_SEGWAY);
				segway.declare(this.getX(),this.getY() + 30);
				vx = 12;
				vy = 5;
			}
		}
	}
	
	@Override
	public void onDeclare () {
		ogX = this.getX();
		ogY = this.getY();
	}
	
	
	public class Segway extends GameObject {
		
		public Segway () {
			this.setSprite(new Sprite ("resources/sprites/config/MallSegwaySide.txt"));
			//this.adjustHitboxBorders();
		}
		
	}
	
	public class Rock extends GameObject {
		public Rock () {
			this.setSprite(new Sprite ("resources/sprites/rock.png"));
			this.setHitboxAttributes(9,9);
		}
	}
	
	public class WateringCan extends GameObject {
		
		int height = 0;
		
		int timer = 0;
		
		int canHealth = 5;
		
		public WateringCan () {
			this.setSprite(new Sprite ("resources/sprites/MallCop/WateringCan.png"));
			this.useSpriteHitbox();
		}
		
		public void setHeight (int newHeight) {
			height = newHeight;
		}
		
		@Override
		public void frameEvent () {
			
			timer = timer + 1;
			Random r = new Random ();
			
			
			if (this.getThrowDirection() != -1) {
				this.despawnAllCoolLike(25);
			} else {
				if (r.nextInt(2) == 0 && timer < 380) {
					WaterDrop d = new WaterDrop (height);
					d.declare(this.getX() + r.nextInt(10) - 5, this.getY());
				}
			}
			
		}
		
		@Override
		public void setY(double val) {
			if (this.getY() != 0) {
				height = (int) (height + Math.abs(this.getY() - val)/3);
			}
			super.setY(val);
		}
		
		public boolean isEmpty () {
			return timer > 380;
		}
		
		public void damageCan () {
			canHealth = canHealth - 1;
			if (canHealth == 0) {
				currentState = 3;
			}
		}
		
		
	}
	
	public class Stick extends GameObject {
		GameObject end;
		
		public Stick (GameObject end) {
			this.end = end;
			this.setSprite(new Sprite ("resources/sprites/MallCop/stick.png"));
		}
		
		
		@Override
		public void frameEvent () {
			super.frameEvent();
			if (this.getThrowDirection() != -1) {
				Random r = new Random ();
				this.setDrawRotation(this.getDrawRotation() + ((r.nextDouble() * .6) -.3));
				this.despawnAllCoolLike(25);
			}
		}
		
		@Override
		public void setX(double newX) {
			super.setX(newX);
			
			
			if (this.getThrowDirection() == -1) {
				if (end instanceof WateringCan) {
					end.setX(newX - (this.hitbox().width  - 5));
				} else {
					end.setX(newX - this.hitbox().width);
				}
			}
		}
		
		@Override
		public void setY(double newY) {
			super.setY(newY);
			
			if (this.getThrowDirection() == -1) {
				end.setY(newY - this.hitbox().height);
			}
		}
		
		public int getStickCount () {
			if (end instanceof Stick) {
				return ((Stick) end).getStickCount() + 1;
			} else {
				return 2;
			}
		}
		
		
		public WateringCan getCan () {
			if (end instanceof Stick) {
				return ((Stick)end).getCan();
			} else {
				return (WateringCan)(end);
			}
		}
		
		public GameObject getEnd () {
			return end;
		}
		
		public void breakSticks () {
			Random r = new Random ();
			
			this.throwObj(r.nextDouble() * 6.28, 10);
			
			if (end instanceof WateringCan) {
				end.throwObj(r.nextDouble()*6.28, 10);
			} else {
				((Stick)end).breakSticks();
			}
			
		}
	}
	
	public class WaterDrop extends GameObject {
		
		int timer = 0;
		
		int height = 0;
		
		public WaterDrop (int height) {
			this.setSprite(new Sprite ("resources/sprites/MallCop/waterDroplet.png"));
			this.height = height;
			this.setHitboxAttributes(3, 3);
		}
		
		@Override
		public void frameEvent () {
		
			timer = timer + 1;
			
			if (timer > height) {
				this.forget();
			}
			
			this.setY(this.getY() + 3);
			
			if (this.isColliding(GameCode.getDickhead())) {
				if (!GameCode.getDickhead().knockedOver) {	
					GameCode.getDickhead().takeDamage(1,10);
					GameCode.getDickhead().knockOver(8);
				}
				this.forget();
			}
			
			if (this.isColliding(GameCode.getDoucheman())) {
				if (!GameCode.getDoucheman().knockedOver) {
					GameCode.getDoucheman().takeDamage(1,10);
					GameCode.getDoucheman().knockOver(8);
				}
				this.forget();
			}
			
		}
		
	}
	
	@Override
	public void onForget () {
		light.forget();
	}
	
	
	public class Crap extends GameObject {
		
	
		boolean kickedTowardsCan = false;
		
		public Crap () {
			Random r = new Random ();
			
			switch (r.nextInt(5)) {
			case 0:
				this.setSprite(new Sprite ("resources/sprites/rock.png"));
				break;
			case 1:
				this.setSprite(new Sprite ("resources/sprites/MallCop/magnifyingGlass.png"));
				break;
			case 2:
				this.setSprite(new Sprite ("resources/sprites/MallCop/screwdriver.png"));
				break;
			case 3:
				this.setSprite(new Sprite ("resources/sprites/MallCop/stick small.png"));
				break;
			case 4:
				this.setSprite(new Sprite ("resources/sprites/MallCop/walkiTalki.png"));
				break;
			}
			this.useSpriteHitbox();
		}
		
		@Override
		public void frameEvent () {
			super.frameEvent();
			
			if (kickedTowardsCan) {
				if (end instanceof WateringCan) {
					this.throwObj(end.getX(), end.getY(), 12);
					if (this.isColliding(end)) {
						((WateringCan)end).damageCan();
						this.forget();
					}
				} else {
					try {
						this.throwObj(((Stick)end).getCan().getX(), ((Stick)end).getCan().getY(), 12);
						if (this.isColliding(((Stick)end).getCan())) {
							((Stick)end).getCan().damageCan();
							this.forget();
						}
					} catch (NullPointerException e) {
						this.forget();
					}
				}
			} else {
				if (this.isColliding(GameCode.getDickhead())) {
					GameCode.getDickhead().knockOver(10);
				}
				if (this.isColliding(GameCode.getDoucheman())) {
					GameCode.getDoucheman().knockOver(10);
				}
				
			}
			
			
		}
		
		@Override
		public void gettingKicked () {
			kickedTowardsCan = true;
		}
		
	}
	
	public class Light extends GameObject {
		
		public Light () {
			this.setSprite(new Sprite("resources/sprites/config/CopLight.txt"));
			this.getAnimationHandler().setFrameTime(100);
		}
	}
	
}
