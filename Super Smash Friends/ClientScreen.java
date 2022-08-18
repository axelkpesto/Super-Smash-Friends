import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.JPanel;
import java.awt.Font;
import java.awt.FontMetrics;
import java.net.URL;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.Image;
import javax.swing.ImageIcon;

public class ClientScreen extends JPanel implements ActionListener, MouseListener,  KeyListener, Runnable {

	public static final Dimension DIMENSION = new Dimension(800,600);
    private int portNumber;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Socket serverSocket;
	
	private PlayerAnimation p1Animation;
	private PlayerAnimation p2Animation;

	private DLList<Star> starList = new DLList<Star>();
	private Thread[] starThreads;

	private Projectile playerProjectile;
	private Projectile enemyProjectile;
	private Thread[] projectileThreads;

	private Sword playerSword;
	private Sword enemySword;

	private Thread playerSwordThread;
	private Thread enemySwordThread;

	private int playerType = 0;

	private PlayerTracker playerTracker;
	private Thread trackerThread;

	private boolean gameRunning = false;
	private boolean bothRunning = false;

	private DLList<Terrain> terrainList = new DLList<Terrain>();

	private int abilityCharge = 0;
	private int abilityCooldown = 3;
	private int jumpCooldown = 2;
	private int altCooldown = 3;

	private double timer = 0;

	private boolean left = false;
	private boolean right = false;
	private boolean up = false;
	private boolean down = false;
	private boolean altAbility = false;
	private boolean abCooldown = false;
	private boolean jmCooldown = false;
	private boolean clicked = false;

	private Player playerOne;
	private Player playerTwo;
	private Thread[] playerThreads;

	private Image backGround;

	private Clip currentSongD;
	private Clip currentSongDm;
	private Clip currentSongG;

	public ClientScreen() throws IOException {

		this.setLayout(null);
		setFocusable(true);
		addKeyListener(this);
		addMouseListener(this);

		portNumber = 1024;

		p1Animation = new PlayerAnimation(null,"",gameRunning);	
		p2Animation = new PlayerAnimation(null,"",gameRunning);	

		start();

		try {
			String hostName = "localhost";
			serverSocket = new Socket(hostName, portNumber);

			out = new ObjectOutputStream(serverSocket.getOutputStream());
			in = new ObjectInputStream(serverSocket.getInputStream());

			p2Animation = (PlayerAnimation) in.readObject();

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			backGround = new ImageIcon("Images/Background2.0.png").getImage();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Thread animation = new Thread(this);
		animation.start();
	}

	public void listen() throws IOException {
		while(true) {
			if(gameRunning) {
				try {
					p2Animation = (PlayerAnimation) in.readObject();

					if(p2Animation.isRunning() && gameRunning) {
						bothRunning = true;
					} else {
						bothRunning = false;
					}

					if(p2Animation.getPlayer() != null && p2Animation.getWeapon() != "" && playerTwo != null) {
						playerTwo.setAll(p2Animation.getPlayer());
						enemySword.setAll(p2Animation.getWeapon());
						enemyProjectile.setAll(p2Animation.getWeapon());
					}

					if(playerOne.getLives()<1 || playerTwo.getLives()<1) {
						reset();
					}
					repaint();
				} catch (Exception e) {
					break;
				}
			}
		}
	}

	private void write() throws IOException{
		if(out != null) {
			out.reset();
			p1Animation = new PlayerAnimation(null,"",gameRunning);	
			if(playerOne != null && playerOne.getType() == 1) {
				p1Animation = new PlayerAnimation(playerOne.animateString(), playerProjectile.toString(),gameRunning);
			} else if(playerOne != null && playerOne.getType() == 2) {
				p1Animation = new PlayerAnimation(playerOne.animateString(), playerSword.animateString(),gameRunning);
			}
			out.writeObject(p1Animation);
		}
	}

	public Dimension getPreferredSize() {
		return DIMENSION;
	}

	public void playSoundDm() {
		try {
			URL url = this.getClass().getClassLoader().getResource("Sound/damage_sound.wav");
			currentSongDm = AudioSystem.getClip();
			currentSongDm.open(AudioSystem.getAudioInputStream(url));
			currentSongDm.start();
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}
	
	public void playSoundD() {
		try {
			URL url = this.getClass().getClassLoader().getResource("Sound/death_sound.wav");
			currentSongD = AudioSystem.getClip();
			currentSongD.open(AudioSystem.getAudioInputStream(url));
			currentSongD.start();
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}


	public void playSongA() {
		try {
			URL url = this.getClass().getClassLoader().getResource("Sound/main_theme.wav");
			currentSongG = AudioSystem.getClip();
			currentSongG.open(AudioSystem.getAudioInputStream(url));
			currentSongG.loop(Clip.LOOP_CONTINUOUSLY);
			currentSongG.start();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
	}

	private void massReset() {
		playerSetup();
		projectileSetup();
		swordSetup();
	}

	private void reset() {
		playerOne.setLives(0);
		playerTwo.setLives(0);
		gameRunning = false;
		bothRunning = false;
		clicked = false;
		playerType = 0;
		massReset();
	}

	private void terrainSetup() {
		terrainList.clear();
		// terrainList.add(new Terrain(225, 350, 105, 15, "Passable"));
		// terrainList.add(new Terrain(450, 350, 105, 15, "Passable"));
		terrainList.add(new Terrain(100, 475, 600, 15, "Unpassable"));
	}

	private void start() {
		playSongA();
		starSetup();
		terrainSetup();
		playerSetup();
		projectileSetup();
		swordSetup();
	}

	private void starSetup() {
		starList.clear();
		for(int i=0; i<250; i++) {
			starList.add(new Star());
		}

		starThreads =  new Thread[starList.size()];
		for(int i = 0; i<starThreads.length; i++) {
			starThreads[i] = new Thread(starList.get(i));
			starThreads[i].start();
		}
	}
	
	private void playerSetup() {
		playerOne = new Player(terrainList, 225, 400, playerType, 0, getJumps(playerType),3);
		playerTwo = new Player(terrainList, 555, 400, 0, 0, 0,3);

		playerThreads = new Thread[2];
		playerThreads[0] = new Thread(playerOne);
		playerThreads[1] = new Thread(playerTwo);

		for(int i=0; i<playerThreads.length; i++) {
			playerThreads[i].start();
		}
	
		playerTracker = new PlayerTracker(playerOne);
		trackerThread = new Thread(playerTracker);
		trackerThread.start();
		
	}

	public int getJumps() {
		if(playerType == 1) {
			return 2;
		} else if(playerType == 2) {
			return 1;
		} return 0;
	}

	private void projectileSetup() {
		playerProjectile = new Projectile(terrainList, playerOne, 0);
		enemyProjectile = new Projectile(terrainList, playerOne, 0);
		playerProjectile.setExisting(false);
		enemyProjectile.setExisting(false);

		projectileThreads = new Thread[2];
		projectileThreads[0] = new Thread(playerProjectile);
		projectileThreads[1] = new Thread(enemyProjectile);

		for(int i=0; i<projectileThreads.length; i++) {
			projectileThreads[i].start();
		}
	}

	private void swordSetup() {
		playerSword = new Sword(playerOne, abilityCharge);
		enemySword = new Sword(playerTwo, abilityCharge);

		playerSwordThread = new Thread(playerSword);
		enemySwordThread = new Thread(enemySword);
		playerSwordThread.start();
		enemySwordThread.start();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.black);
		g.drawImage(backGround, 0, 0, null);

		for(int i=0; i<starList.size(); i++) {
			starList.get(i).drawStar(g);
		}

		if(!gameRunning) {
			
			g.setFont(new Font("Serif", Font.ITALIC, 12));
			g.setColor(Color.WHITE);
			/*
			 * Everyone has the same goal: to come out on top. 
			 * Evade your opponent by pressing W, A, S, and D or the respective arrow keys
			 * 
			 * If you fall off the map, you lose a life
			 * 
			 * If you get hit, you will recieve damage
			 * The more damage you take, the more knockback you will recieve
			 * 
			 * Choose one of the two player types, a long ranged archer or a close range knight, and battle to become the next winner of the arena!
				
			*/

			drawText(g, "Everyone has the same goal: to come out on top. Evade your opponent by pressing W, A, S, and D or the respective arrow keys, and Q or space to attack. Press CTRL or E to use alternate ability. If you fall off the map, you lose a life. If you get hit, you will recieve damage, and the more damage you take, the more knockback you will recieve. Choose one of the two player types, a long ranged archer or a close range knight, and battle to become the champion!", 500);
			g.setColor(Color.RED);
			g.drawRect(225, 375, 125, 125);
			g.drawRect(450, 375, 125, 125);
			Player arch = new Player(terrainList, 278, 385, 1, 0, 0,3);
			Player knight = new Player(terrainList, 503, 385, 2, 0, 0,3);
			arch.drawMe(g);
			knight.drawMe(g);
			Font f = new Font("Serif", Font.BOLD, 8);
			g.setFont(f);
			g.drawString("*  Damage: ***", 235, 445);
			g.drawString("*  Damage: ****", 460, 445);
			g.drawString("*  Knockback: ***", 235, 455);
			g.drawString("*  Knockback: ****", 460, 455);
			g.drawString("*  Range: *****", 235, 465);
			g.drawString("*  Range: **", 460, 465);
			g.drawString("*  Passive: Instakill Arrow", 235, 475);
			g.drawString("*  Passive: Knockback Resist", 460, 475);
			if(clicked) {
				g.fillRect(322, 525, 150, 35);
				Font fr = new Font("Serif", Font.BOLD, 12);
				g.setFont(fr);
				g.setColor(Color.BLACK);
				g.drawString("Click to Start", 360, 545);
			}
		}

		if(gameRunning) {
			for(int i=0; i<terrainList.size(); i++) {
				if(terrainList.get(i) != null) {
					terrainList.get(i).drawMe(g);
				}
			}

			g.setColor(Color.blue);
			g.drawString(Integer.toString(playerOne.getHealth()), 100, 100);
			g.drawString(Integer.toString(playerTwo.getHealth()), 150, 100);
			
			playerOne.drawMe(g);
			playerTracker.drawMe(g);
			playerTwo.drawMe(g);
			
			if(playerProjectile != null && playerOne.getType() == 1) {
				playerProjectile.drawMe(g);
			}

			if(playerSword != null && playerOne.getType() == 2) {
				playerSword.drawMe(g);
			}

			if(enemyProjectile != null && playerTwo.getType() == 1) {
				enemyProjectile.drawMe(g);
			}

			if(enemySword != null && playerTwo.getType() == 2) {
				enemySword.drawMe(g);
			}

		}

	}

	public int getJumps(int i) {
		if(i==1) {
			return 2;
		} else if(i==2) {
			return 1;
		} else {
			return 0;
		}
	}

	public void drawText(Graphics g, String text, int width) {

		Font f = new Font("Serif", Font.ITALIC, 12);
		g.setFont(f);

		String totalText = text;
		String currentText = "";
		g.setColor(Color.WHITE);
		
		FontMetrics fontMetrics = g.getFontMetrics(f);
		String[] arr = totalText.split(" ");
		DLList<String> splitString = new DLList<String>();
		for(int i=0; i<arr.length; i++) {
			if((fontMetrics.stringWidth(currentText + arr[i])<width) && !(i==arr.length-1)) {
				currentText += arr[i] + " ";
			} else if(i==arr.length-1) {
				currentText += arr[i] + " ";
				splitString.add(currentText);
			} else {
				splitString.add(currentText);
				currentText = arr[i] + " ";
			}
		}

		for(int i=0; i<splitString.size(); i++) {
			g.drawString(splitString.get(i),162, 195+(i*25));
		}
	}

	public void sleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch(InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}

	@Override
	public void run() {
		while(true) { 
			synchronized(this) {
				if(bothRunning) {
					if(playerOne.getType()==1) {
						if(playerProjectile.checkCollision(playerTwo)) {
							playSoundDm();
							playerTwo.damage(playerOne, "Projectile", playerProjectile.getCharge());
						}
					}

					if(playerTwo.getType()==1) {
						if(enemyProjectile.checkCollision(playerOne)) {
							playSoundDm();
							playerOne.damage(playerTwo, "Projectile", enemyProjectile.getCharge());
						}
					}

					if(playerOne.getType()==2) {
						if(playerSword.checkCollision(playerTwo)) {
							playSoundDm();
							playerTwo.damage(playerOne, "Sword", playerSword.getCharge());
						}
					}

					if(playerTwo.getType() == 2) {
						if(enemySword.checkCollision(playerOne)) {
							playSoundDm();
							playerOne.damage(playerTwo, "Sword", enemySword.getCharge());
						}
					}

					if(playerOne.getX()>900 || playerOne.getX()<-100 || playerOne.getY()>900 || playerOne.getY()<-100) {
						playSoundD();
						playerOne.loseLife();
					}

					if(playerTwo.getX()>900 || playerTwo.getX()<-100 || playerTwo.getY()>900 || playerTwo.getY()<-100) {
						playSoundD();
						playerTwo.loseLife();
					}

					playerOne.setCharge(abilityCharge);

					if(abilityCooldown>0 && ((int)timer)%9 == 0) {
						abilityCooldown--;
					}

					if(abilityCooldown<=0) {
						abCooldown = false;
					}
					
					if(jumpCooldown>0 && ((int)timer)%9 == 0) {
						jumpCooldown--;
					}

					if(jumpCooldown==0 && playerOne.onPlatform()) {
						jmCooldown = false;
						jumpCooldown = 3;
						playerOne.resetJumps();
					}

					if(altCooldown>0 && ((int)timer)%9 == 0) {
						altCooldown--;
					}

					if(altCooldown<=0) {
						altAbility = false;
						playerTwo.setDamageable(true);
					}

					if(((int)timer)%10 == 0) {
						up = false;
					}

					if(left && up && playerOne.getJumps()>0) {
						playerOne.move("up");
						playerOne.move("left");
					} else if(left && down) {
						playerOne.move("down");
						playerOne.move("left");
					} else if(right && up && playerOne.getJumps()>0) {
						playerOne.move("up");
						playerOne.move("right");
					} else if(right && down) {
						playerOne.move("right");
						playerOne.move("down");
					} else if(left) {
						playerOne.move("left");
					} else if(up && playerOne.getJumps()>0) {
						playerOne.move("up");
					} else if(right) {
						playerOne.move("right");
					} else if(down) {
						playerOne.move("down");
					}
				}

				if(playerOne.getLives()<1 || playerTwo.getLives()<1) {
					reset();
				}

				repaint();
				timer+=0.1;

				try {
					write();
				} catch (IOException e) {
					e.printStackTrace();
				}

				try {
					Thread.sleep(10);
				} catch(InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}

	//KeyPresses
	public void keyPressed(KeyEvent e) {
		if(bothRunning) {
			if(e.getKeyCode() == 68 || e.getKeyCode() == 39){
				right = true;
			}
	
			//Left (left arrow or a)
			if(e.getKeyCode() == 65 || e.getKeyCode() == 37){
				left = true;
			}
	
			//Up (up arrow or w)
			if(e.getKeyCode() == 87 || e.getKeyCode() == 38) {
				if(!jmCooldown && playerOne.getJumps()>0) {
					up = true;
					jmCooldown = true;
					playerOne.setJumps(0);
				}
			}
	
			//Down (down arrow or s)
			if(e.getKeyCode() == 83 || e.getKeyCode() == 40){
				down = true;
			}
	
			if(e.getKeyCode() == 32 || e.getKeyCode() == 81) {
				if(abilityCharge<4 && !abCooldown) {
					abilityCharge++;
				}
			}

			if(e.getKeyCode()==17 || e.getKeyCode()==69) {
				if(!altAbility) {
					altAbility = true;
					altCooldown = 3;
					playerOne.setDamageable(false);
				}
			}
		}
	}

	public void abilityReset() {
		abilityCharge = 0;
		abilityCooldown = 3;
		abCooldown = true;
	}

	//Extra Methods
	public void keyReleased(KeyEvent e){
		if(bothRunning) {
			if(e.getKeyCode() == 32 || e.getKeyCode() == 81) {
				if(!abCooldown) {
					if(playerOne.getType() == 1) {
						playerProjectile = new Projectile(terrainList, playerOne, abilityCharge);
						projectileThreads[0] = new Thread(playerProjectile);
						projectileThreads[0].start();
						abilityReset();
					} else if(playerOne.getType() == 2) {
						playerSword.setCharge(abilityCharge);
						playerSword.setSwing(true);
						abilityReset();
					}
				}
			}
	
			// Right (right arrow or d)
			if(e.getKeyCode() == 68 || e.getKeyCode() == 39){
				right = false;
			}
	
			//Left (left arrow or a)
			if(e.getKeyCode() == 65 || e.getKeyCode() == 37){
				left = false;
			}
	
			//Up (up arrow or w)
			if(e.getKeyCode() == 87 || e.getKeyCode() == 38){
				up = false;
			}
	
			//Down (down arrow or s)
			if(e.getKeyCode() == 83 || e.getKeyCode() == 40){
				down = false;
			}
		}
	}

	public void mouseClicked(MouseEvent e) {
		if(!gameRunning) {
			if(e.getX()>225 && e.getX()<350 && e.getY()>375 && e.getY()<500) {
				playerType = 1;
				clicked = true;
			} else if(e.getX()>450 && e.getX()<575 && e.getY()>375 && e.getY()<500) {
				playerType = 2;
				clicked = true;
			} else if(e.getX()>322 && e.getX()<472 && e.getY()>525 && e.getY()<560 && clicked) {
				playerOne.setType(playerType);
				gameRunning = true;
			}
		}
	}

	//Extra Methods
	public void keyTyped(KeyEvent e){}
	public void actionPerformed(ActionEvent e) {}
	public void mousePressed(MouseEvent e){}
	public void mouseMoved(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
}
