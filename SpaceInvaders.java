package SpaceInvaders;

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
	int shootchance=9000;
	
	boolean playbullet=false;
	int playbulletx;
	int playbullety;
	Random shoot = new Random();
	
	public static void main(String[] args){
		for (int x=0; x<5; x++)gamestate.add(new ArrayList<Integer>());
		for (int x=0; x<5; x++){
			for (int y=0; y<10; y++){
				gamestate.get(x).add(1);
			}
		}
		for (int x=0; x<20; x++){
			shieldstate.add(4);
		}
		new SpaceInvaders().go();
	}
	
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
	
	private class spacegrid extends JComponent {
		public void paintComponent(Graphics g){
			Graphics2D grap = (Graphics2D) g;
			grap.setColor(Color.RED);
			grap.fillRect(0, 0, 1000, 1000);
			grap.setColor(Color.BLACK);
			grap.fillRect(5, 5, 800, 800);
			
			grap.setColor(Color.GREEN);
			grap.setFont(new Font ("Arial Black", Font.BOLD, 35));
			if (dead){
				grap.setFont(new Font("Arial Black", Font.BOLD, 40));
				grap.drawString("GAME OVER!", 250, 300);
				grap.drawString("Score: "+Integer.toString(score), 250, 400);
			}
			else if (win){
				grap.setFont(new Font("Arial Black", Font.BOLD, 40));
				grap.drawString("YOU WIN!", 300, 300);
				grap.drawString("Score: "+Integer.toString(score), 250, 400);}
			
			else{
		    grap.drawString("Score: "+Integer.toString(score), 300, 45);
			grap.fillRect(playerx, 780, 50, 20);
			grap.fillRect(playerx+21, 772, 9, 9);
			for (int x=0; x<5; x++){
				for (int y=0; y<10; y++){
					if (gamestate.get(x).get(y)>=1){
						grap.fillRect(firstalien+(y*70), alieny+(x*70), aliensize, aliensize);
						if (shoot.nextInt(shootchance-(killed*30))==0){
							alienshots.add(new ArrayList<Integer>());
							alienshots.get(alienshots.size()-1).add(firstalien+(y*70)+16);
							alienshots.get(alienshots.size()-1).add(alieny+(x*70)+40);
						}
					}
				}
				}
			
			for (int x=0; x<16; x++){
				if (shieldstate.get(x)>0){
					int mod=(x%4);
					int numshield = x/4;
					if (mod==0){
						grap.fillRect(40+(numshield*200), 700, 30, 30);
					}
					else if (mod<=2){
						grap.fillRect(40+(numshield*200)+(mod*30), 670, 30, 30);
					}
					else grap.fillRect(130+(numshield*200), 700, 30, 30);
				}
			}
			if (playbullet)grap.fillRect(playbulletx, playbullety, 9, 9);
			grap.setColor(Color.RED);
			for (int x=0; x<alienshots.size(); x++){
				grap.fillRect(alienshots.get(x).get(0), alienshots.get(x).get(1), 8, 8);
				alienshots.get(x).set(1, alienshots.get(x).get(1)+3);
				alienshoot(x, alienshots.get(x).get(0), alienshots.get(x).get(1));
			}
			}
		}
	}
	
	private class keyactions implements KeyListener {
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode()==KeyEvent.VK_LEFT){
				dx=-playerspeed;
			}
			else if (e.getKeyCode()==KeyEvent.VK_RIGHT){
				dx=playerspeed;
			}
			else if (e.getKeyCode()==KeyEvent.VK_SPACE&&!playbullet){
				playbulletx=playerx+21;
				playbullety=764;
				playbullet=true;
			}
		}

		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode()==KeyEvent.VK_LEFT||e.getKeyCode()==KeyEvent.VK_RIGHT){
				dx=0;
			}
		}

		public void keyTyped(KeyEvent e) {}
		
	}
	
	private void move(){
		while (!dead){
			if (playerx>=5&&dx<0) playerx+=dx;
			else if (playerx<=755&&dx>0) playerx+=dx;
			if (target==killed){
				delay/=2;
				move=0;
				target+=Math.max(1,(50-killed)/3);
			}
			if (move==delay){
			if (alienmoveright&&firstalien+40+((9-rightrowkilled)*70)<805){
				firstalien+=5;
			}
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
			if (playbullet){
				playbullety-=bulletspeed;
				alienhit();
			if (playbullety<=0) {playbullet=false;}}
			window.repaint();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		
		}
	}
	
	private void alienshoot(int index, int xcoord, int ycoord){
		if (ycoord>=805)alienshots.remove(index);
		else{
		for (int x=0; x<20; x++){
			if (shieldstate.get(x)>0){
					int mod=(x%4);
					int numshield = x/4;
					if (mod==0){
						if (xcoord+8>=40+(numshield*200)&&xcoord<=70+(numshield*200)
								&&ycoord+8>=700&&ycoord<=700+30){
							shieldstate.set(x, shieldstate.get(x)-1);
							alienshots.remove(index);} 
					}
					else if (mod<=2){
						if (xcoord+8>=40+(numshield*200)+(mod*30)&&xcoord<=70+(numshield*200)+(mod*30)
								&&ycoord+8>=670&&ycoord<=670+30){
							shieldstate.set(x, shieldstate.get(x)-1);
							alienshots.remove(index);}
					}
					else  if (xcoord+8>=130+(numshield*200)&&xcoord<=160+(numshield*200)
							&&ycoord+9>=700&&ycoord<=700+30){
						shieldstate.set(x, shieldstate.get(x)-1);
						alienshots.remove(index);}
			}
			
		}
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
	
	private void alienhit(){
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
	
	
	private boolean rowkilled(){//1 for left row, 2 for right row
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
