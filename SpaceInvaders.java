package SpaceInvaders;
//Made by Andrew Xue
//a3xue@edu.uwaterloo.ca
//SPACE INVADERS! Use the arrow keys to control your tank. You can press and hold a key
//    for continuous movement. Press the space bar to shoot upwards at the aliens. Hide
//    behind the shields to block alien bullets, and destroy all the aliens to win! Be
//    careful, the aliens will speed up as more and more are destroyed.
//Part of a project to learn Java over the winter break and create retro video games

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.JFrame;


public class SpaceInvaders {
	JFrame window = new JFrame();
	
	boolean dead=false; 
	
	static int brokenshield[][]={{0,10,10,10,20,10},{0,20,10,10,20,0},{0,0,10,10,20,20}};
	static ArrayList<ArrayList<Integer>> gamestate = new ArrayList<ArrayList<Integer>>();
	static ArrayList<ArrayList<Integer>> alienshots = new ArrayList<ArrayList<Integer>>();
	static ArrayList<Integer> shieldstate = new ArrayList<Integer>();
	int playerx=50;
	int dx;
	int score=0;
	boolean win=false;
	
	int move=0;
	int delay=60;
	int playerspeed=4;
	int bulletspeed=15;
	
	boolean alienmoveright = true;
	int firstalien=5;
	int alieny=70;
	int target=50/3;
	int killed;
	int aliensize=40;
	int leftrowkilled;
	int rightrowkilled;
	int shootchance=5000;
	
	boolean playbullet=false;
	int playbulletx;
	int playbullety;
	Random shoot = new Random();
	
	public static void main(String[] args){
		// Adds to the gamestate ArrayList for storage of alien status
		for (int x=0; x<5; x++)gamestate.add(new ArrayList<Integer>());
		for (int x=0; x<5; x++){
			for (int y=0; y<10; y++){
				gamestate.get(x).add(1);
			}
		}
		// Adds to the sheieldstate ArrayList for storage of shield status
		for (int x=0; x<20; x++){
			shieldstate.add(4);
		}
		new SpaceInvaders().go();
	}
	// Creates the JFrame and adds the KeyListener and game elements. Also starts the game
	void go() {
		window.setSize(815, 845);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
		window.setResizable(false);
		window.add(new spacegrid());
		window.addKeyListener(new keyactions());
		window.repaint();
		move();
	}
	// A JComponent containing the player position, all the aliens, the shields, as well as
	//    the bullet if it is in play
	private class spacegrid extends JComponent {
		public void paintComponent(Graphics g){
			Graphics2D grap = (Graphics2D) g;
			grap.setColor(Color.RED);
			grap.fillRect(0, 0, 1000, 1000);
			grap.setColor(Color.BLACK);
			grap.fillRect(5, 5, 800, 800);
			
			grap.setColor(Color.GREEN);
			grap.setFont(new Font ("Arial Black", Font.BOLD, 35));
			// Draws game over screen
			if (dead){
				grap.setFont(new Font("Arial Black", Font.BOLD, 40));
				grap.drawString("GAME OVER!", 250, 300);
				grap.drawString("Score: "+Integer.toString(score), 250, 400);
			}
			// Draws victory screen
			else if (win){
				grap.setFont(new Font("Arial Black", Font.BOLD, 40));
				grap.drawString("YOU WIN!", 300, 300);
				grap.drawString("Score: "+Integer.toString(score), 250, 400);}
			
			else{
			// Game Score
		    grap.drawString("Score: "+Integer.toString(score), 300, 45);
		    // Position
			grap.fillRect(playerx, 780, 50, 20);
			grap.fillRect(playerx+21, 772, 9, 9);
			// Draws in the aliens
			for (int x=0; x<5; x++){
				for (int y=0; y<10; y++){
					if (gamestate.get(x).get(y)>=1){
						if (alieny+(x*70)+40>=780){dead=true; window.repaint();}
						grap.fillRect(firstalien+(y*70), alieny+(x*70), aliensize, aliensize);
						grap.setColor(Color.BLACK);
						
						for (int i=0; i<=10; i+=5){
							grap.fillRect(firstalien+(y*70), alieny+(x*70)+i, 15-i, 5);
							grap.fillRect(firstalien+(y*70)+25+i, alieny+(x*70)+i, 15-i, 5);}
						
						grap.fillRect(firstalien+(y*70)+10, alieny+(x*70)+35, 20, 5);
						grap.fillRect(firstalien+(y*70)+15, alieny+(x*70)+30, 10, 5);
						grap.setColor(Color.GREEN);
						if (shoot.nextInt(shootchance-(killed*90))==0){
							alienshots.add(new ArrayList<Integer>());
							alienshots.get(alienshots.size()-1).add(firstalien+(y*70)+16);
							alienshots.get(alienshots.size()-1).add(alieny+(x*70)+40);
						}
					}
				}
				}
			// Draws in the shields
			for (int x=0; x<16; x++){
				if (shieldstate.get(x)>0){
					grap.setColor(Color.GREEN);
					int mod=(x%4);
					int numshield = x/4;
					if (mod==0){
						grap.fillRect(40+(numshield*200), 700, 30, 30);
					}
					else if (mod<=2){
						grap.fillRect(40+(numshield*200)+(mod*30), 670, 30, 30);
					}
					else grap.fillRect(130+(numshield*200), 700, 30, 30);
					grap.setColor(Color.BLACK);
					if (shieldstate.get(x)<=3){
						for (int i=shieldstate.get(x)-1;i<3;i++){
							for (int y=0; y<=4;y+=2){
								if (mod==0){
									grap.fillRect(40+(numshield*200)+brokenshield[i][y], 700+brokenshield[i][y+1], 10, 10);
								}
								else if (mod<=2){
									grap.fillRect(40+(numshield*200)+(mod*30)+brokenshield[i][y], 670+brokenshield[i][y+1], 10, 10);
								}
								else{
									grap.fillRect(130+(numshield*200)+brokenshield[i][y], 700+brokenshield[i][y+1], 10, 10);
								}
							}
							
						}
					}
				}
			}
			grap.setColor(Color.GREEN);
			// Draws in the bullet
			if (playbullet)grap.fillRect(playbulletx, playbullety, 9, 9);
			grap.setColor(Color.RED);
			// Draws the alien bullets
			for (int x=0; x<alienshots.size(); x++){
				grap.fillRect(alienshots.get(x).get(0), alienshots.get(x).get(1), 8, 8);
				alienshots.get(x).set(1, alienshots.get(x).get(1)+3);
				alienshoot(x, alienshots.get(x).get(0), alienshots.get(x).get(1));
			}
			}
		}
	}
	
	// Takes key inputs from the player. Uses a toggled change in movement variable for smooth
	//    movement
	private class keyactions implements KeyListener {
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode()==KeyEvent.VK_LEFT)dx=-playerspeed;
			else if (e.getKeyCode()==KeyEvent.VK_RIGHT)dx=playerspeed;
			
			// Puts a bullet into play at the player's gun position
			else if (e.getKeyCode()==KeyEvent.VK_SPACE&&!playbullet){
				playbulletx=playerx+21;
				playbullety=764;
				playbullet=true;
			}
		}

		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode()==KeyEvent.VK_LEFT||e.getKeyCode()==KeyEvent.VK_RIGHT) dx=0;
			}
		public void keyTyped(KeyEvent e) {}
		
	}
	// Moves all the game elements and executes logic
	private void move(){
		while (!dead){
			// Moves the player and restricting movement to within the game screen
			if (playerx>=5&&dx<0) playerx+=dx;
			else if (playerx<=755&&dx>0) playerx+=dx;
			// Speeds up the movement of the aliens
			if (target==killed){
				delay/=2;
				move=0;
				target+=Math.max(1,(50-killed)/3);
			}
			// Moves all the aliens every (delay) seconds
			if (move==delay){
			if (alienmoveright&&firstalien+40+((9-rightrowkilled)*70)<805){
				firstalien+=5;
			}
			// Changes the direction of the aliens if any hit the edge of the screen
			else if (alienmoveright&&firstalien+40+((9-rightrowkilled)*70)>=805){
				alieny+=50;
				alienmoveright=false;
			}
			else if (!alienmoveright&&(firstalien+(leftrowkilled*70))>5){
				firstalien-=5;
			}
			else if (!alienmoveright&&(firstalien+(leftrowkilled*70))<=5){
				alieny+=50;
				alienmoveright=true;}
			move=0;}
			else {move++;}
			// Moves the bullet and detect if it hits any aliens
			if (playbullet){
				playbullety-=bulletspeed;
				alienhit();
			// Removes the bullet if it exits the game screen
			if (playbullety<=0) {playbullet=false;}}
			window.repaint();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		
		}
	}
	// Make the aliens shoot and logic for alien shots
	private void alienshoot(int index, int xcoord, int ycoord){
		// Removes shots if the exit game screen
		if (ycoord>=805)alienshots.remove(index);
		else{
		// If the alienshots hit a shield, weaken the shield and remove the bullet
			for (int x=0; x<20; x++){
				if (shieldstate.get(x)>0){
						int mod=(x%4);
						int numshield = x/4;
						if (mod==0){
							if (xcoord+8>=40+(numshield*200)&&xcoord<=70+(numshield*200)
									&&ycoord+8>=700&&ycoord<=700+30){
								shieldstate.set(x, shieldstate.get(x)-1);
								alienshots.remove(index);
								break;} 
						}
						else if (mod<=2&&mod!=0){
							if (xcoord+8>=40+(numshield*200)+(mod*30)&&xcoord<=70+(numshield*200)+(mod*30)
									&&ycoord+8>=670&&ycoord<=670+30){
								shieldstate.set(x, shieldstate.get(x)-1);
								alienshots.remove(index);
								break;}
						}
						else if (xcoord+8>=130+(numshield*200)&&xcoord<=160+(numshield*200)
								&&ycoord+9>=700&&ycoord<=700+30){
							shieldstate.set(x, shieldstate.get(x)-1);
							alienshots.remove(index);
							break;}
				}
			}
		// If any alienbullet hits the player, the game is over
		for (int x=0; x<alienshots.size(); x++){
			int bullxcoord= alienshots.get(x).get(0);
			int bullycoord= alienshots.get(x).get(1);
			if (bullxcoord+8>=playerx&&bullxcoord<=(playerx+50)&&bullycoord+8>=780&&
					bullycoord<=800) {
					dead=true;
					window.repaint();}
			}
		}
	}
	// Detects if anything is killed and responds accordingly
	private void alienhit(){
		// if an alien is hit, if necessary the amount of movement from side to side is decreased
		for (int x=0; x<10; x++){
			for (int y=0; y<5; y++){
				if (gamestate.get(y).get(x)!=0){
				if (playbullety+9>=alieny+(y*70)&&playbullety<=alieny+(y*70)+aliensize&&
					playbulletx+9>=firstalien+(x*70)&&playbulletx<=firstalien+(x*70)+aliensize){
					gamestate.get(y).set(x, 0);
					playbullet=false;
					killed++;
					score+=100;
					
					
					while(rowkilled())rowkilled();}
					if (leftrowkilled==10){win=true; window.repaint();}
				}
			}
			}
		// If a shield is hit, decrease its health
		for (int x=0; x<20; x++){
			if (shieldstate.get(x)>0){
					int mod=(x%4);
					int numshield = x/4;
					if (mod==0){
						if (playbulletx+9>=40+(numshield*200)&&playbulletx<=70+(numshield*200)
								&&playbullety+9>=700&&playbullety<=700+30){
							shieldstate.set(x, shieldstate.get(x)-1);
							playbullet=false;}
						
						
					}
					else if (mod<=2){
						if (playbulletx+9>=40+(numshield*200)+(mod*30)&&playbulletx<=70+(numshield*200)+(mod*30)
								&&playbullety+9>=670&&playbullety<=670+30){
							shieldstate.set(x, shieldstate.get(x)-1);
							playbullet=false;}
					}
					else  if (playbulletx+9>=130+(numshield*200)&&playbulletx<=160+(numshield*200)
							&&playbullety+9>=700&&playbullety<=700+30){
						shieldstate.set(x, shieldstate.get(x)-1);
						playbullet=false;}
			}
		}
	}
	
	// Boolean function for detecting is an entire row of aliens have been killed. If so, it will
	//    increase the side to side movement needed. Also ends the game if all aliens have been killed.
	private boolean rowkilled(){
		boolean left=true;
		boolean right=true;
		if (leftrowkilled==10)return false;
		for (int x=0; x<5; x++){
			if (gamestate.get(x).get(leftrowkilled)!=0)left=false;
			if (gamestate.get(x).get(9-rightrowkilled)!=0)right=false;
		}
		if (left) {leftrowkilled+=1; return true;}
		else if (right){rightrowkilled+=1; return true;}
		else return false;
	}

	
}
