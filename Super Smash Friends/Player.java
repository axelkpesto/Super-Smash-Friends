import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;

public class Player implements Runnable, Comparable<Player> {

    private int x, y, type, percentage, jumps, lives, startingX, startingY, charge;

    private double acceleration = .5;
    private DLList<Terrain> terrainList;

    private boolean running = true;
    private boolean damageable = true;

    private String lastDirection = "right";

    //Images
	private Image archer;
	private Image swordsman;
    private Image bowOne;
    private Image bowTwo;
    private Image bowThree;

    public Player(DLList<Terrain> terrainList, int x, int y, int type, int percentage, int jumps, int lives) {
        this.x = x;
        this.y = y;
        this.startingX = x;
        this.startingY = y;
        this.terrainList = terrainList;
        this.type = type;
        this.percentage = percentage;
        this.lives = lives;
        if(type == 1 || type == 2) {
            jumps = 1;
        } else if(type == 3) {
            jumps = 2;
        }
        charge = 0;
        this.jumps = jumps;

        try {
			archer = new ImageIcon("Images/Archer.png").getImage();
			swordsman = new ImageIcon("Images/Swordsperson.png").getImage();
            bowOne = new ImageIcon("Images/Bow1.png").getImage();
            bowTwo = new ImageIcon("Images/Bow2.png").getImage();
            bowThree = new ImageIcon("Images/Bow3.png").getImage();
		} catch (Exception e) {
			e.printStackTrace();
		}

    }

    //Position
    public int getX() {
        return x;
    }

    public int getJumps() {
        return jumps;
    }

    public void setJumps(int i) {
        jumps = i;
    }

    public void resetJumps() {
        if(type == 1) {
            jumps = 2;
        } else if(type == 2) {
            jumps = 1;
        }
    }

    public int getY() {
        return y;
    }

    public void setY(int i) {
        y = i;
    }

    public void resetPos() {
        x = startingX;
        y = startingY;
        acceleration = .5;
    }

    public void move(String direction) {
        lastDirection = direction;
        if(direction.toLowerCase().equals("right")) {
                lastDirection = "right";
                x+=3;
                direction = "";
        } else if(direction.toLowerCase().equals("left")) {
                lastDirection = "left";
                x-=3;
                direction = "";
        } else if(direction.toLowerCase().equals("down")) {
            if(!onPlatform() || (getCurrentPlatform() != null) && getCurrentPlatform().isPassable()) {
                y+=3;
                direction = "";
            }
        } else if(direction.toLowerCase().equals("up")) {
            y-=30;
            jumps--;
            direction = "";
        }
    }

    public void sendUp() {
        Rectangle check = new Rectangle(x+3,y+3,14,34);
        if(onPlatform() && (getCurrentPlatform() != null && !getCurrentPlatform().isPassable() && intersectsWith(getCurrentPlatform(),check))) {
            y = 435;
        }
    }

    //Gravity
    public void gravitize() {
        if(!onPlatform()) {
            y += .5*acceleration;
            accelerate();
            sleep(10);
        } else {
            acceleration = .5;
        }
    }

    public void accelerate() {
        if(acceleration<55) {
            if(running) {
                acceleration = acceleration*1.075;
            }
        }
    }

    //Type
    public int getType() {
        return type;
    }

    public boolean isArcher() {
        return type == 1;
    }

    public boolean isSwordsman() {
        return type == 2;
    }

    public int hashCode() {
		int hash = (x*31) + y;
		return hash;
    }

    //Health
    public int getHealth() {
        return percentage;
    }

    //Lives
    public int getLives() {
        return lives;
    }

    public void setLives(int i) {
        lives = i;
    }

    public void loseLife() {
        if(lives>0) {
            lives--;
            percentage = 0;
            resetPos();
        } else {
            running = false;
        }
    }

    public Image getBow() {
        if(charge == 0 || charge == 1) {
            return bowOne;
        } else if(charge == 2 || charge == 3) {
            return bowTwo;
        } else {
            return bowThree;
        }
    }

    public void drawBow(Graphics2D g2d) {
        if(isArcher()) {
            if(lastDirection.equals("left")) {
                g2d.drawImage(getBow(), x, y, -20, 40, null);
            } else {
                g2d.drawImage(getBow(), x+18, y, 20, 40, null);
            }
        }
    }

    public void setCharge(int i) {
        charge = i;
    }

    //Direction
    public String lastFacing() {
        if(lastDirection.equals("right")) {
            return "right";
        } else if(lastDirection.equals("left")) {
            return "left";
        } else {
            return "right";
        }
    }

    public String getDirection() {
        return lastDirection;
    }

    public void knockBack(Player p, String s, int charge) {
        if(damageable) {
            if(p.getDirection().equals("right")) {
                if(s.toLowerCase().equals("projectile")) {
                    x += (3*(charge+1)) + (percentage/20);
                } else if(s.toLowerCase().equals("sword")) {
                    x += (3*(charge+1)) + (percentage/15);
                }
            } else if(p.getDirection().equals("left")) {
                if(s.toLowerCase().equals("projectile")) {
                    x -= (3*(charge+1)) + (percentage/20);
                } else if(s.toLowerCase().equals("sword")) {
                    x -= (3*(charge+1)) + (percentage/15);
                }
            }
        }
    }

    public void damage(Player p, String s, int charge) {
        if(damageable) {
            if(s.toLowerCase().equals("projectile")) {
                int damage = (((((int)(Math.random()*3+1)) * (charge+1)))/2)+1;
                percentage = percentage + damage;
                knockBack(p, s, charge);
            } else if(s.toLowerCase().equals("sword")) {
                int damage = (((((int)(Math.random()*5+1)) * (charge+1)))/2)+3;
                percentage = percentage + damage;
                knockBack(p, s, charge);
            }
        }
    }

    public Image getImage() {
        if(isArcher()) {
            return archer;
        } else if(isSwordsman()) {
            return swordsman;
        } return null;
    }

    //DrawMe
    public void drawMe(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        if(running) {
            g.setColor(Color.blue);
            // g.drawRect(x, y, 20, 40);
            if(lastDirection.equals("left")) {
                g2d.drawImage(getImage(), x+20, y, -20, 40, null);
            } else {
                g2d.drawImage(getImage(), x, y, 20, 40, null);
            }

            drawBow(g2d);
        }
    }

    //Strings
    public String animateString() {
        return x + "," + y + "," + type + "," + percentage + "," + jumps+ "," + lives + "," + charge + "," + lastDirection + "," + damageable;
    }

    public boolean isInt(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean correctString(String s) {
        try {
            s.split(",");
            return true;
        } catch (NullPointerException e) {
            return false;
        }
    }

    public boolean correctFormat(String s) {
        String[] arr = s.split(",");
        if(!isInt(arr[0]) || !isInt(arr[1]) || !isInt(arr[2]) || !isDouble(arr[3]) || !isInt(arr[4]) ||  !isInt(arr[5]) || !isInt(arr[6])) {
            return false;
        } return true;
    }

    //SetAll
    public void setAll(String s) {
        if(correctString(s) && correctFormat(s)) {
            String[] arr = s.split(",");
            this.x = Integer.parseInt(arr[0]);
            this.y = Integer.parseInt(arr[1]);
            this.type = Integer.parseInt(arr[2]);
            this.percentage = Integer.parseInt(arr[3]);
            this.jumps = Integer.parseInt(arr[4]);
            // this.lives = Integer.parseInt(arr[5]);
            this.charge = Integer.parseInt(arr[6]);
            this.lastDirection = arr[7];
            this.damageable = Boolean.parseBoolean(arr[8]);
        }
    }

    public void setDamageable(boolean b) {
        damageable = b;
    }

    public boolean getDamageable() {
        return damageable;
    }

    public void reset() {
        percentage = 0;
        lives = 3;
        resetPos();
    }

    public void setType(int i) {
        type = i;
    }

    public String toString() {
        return "X: " + x + ", Y: " + y;
    }

    //Terrain
    public boolean intersectsOnce(Rectangle r) {
        boolean intersects = false;
        for(int i=0; i<terrainList.size(); i++) {
            if((r.intersects(terrainList.get(i).getBounds()))) {
                intersects = true;
            }
        }
        return intersects;
    }

    public boolean intersectsWith(Terrain t, Rectangle r) {
        return r.intersects(t.getBounds());
    }

    public Terrain getCurrentPlatform() {
        for(int i=0; i<terrainList.size(); i++) {
            if(getBounds().intersects(terrainList.get(i).getBounds())) {
                return terrainList.get(i);
            }
        } return null;
    }

    public boolean checkCollision(Terrain t) {
        return getBounds().intersects(t.getBounds());
    }

    public boolean onPlatform() {
        boolean onPlatform = false;
        for(int i=0; i<terrainList.size(); i++) {
            if(checkCollision(terrainList.get(i))) {
                onPlatform = true;
            }
        } return onPlatform;
    }

    //Hitbox
    public Rectangle getBounds() {
        return new Rectangle(x,y,20,40);
    }

    //Threading
    public void sleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch(InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}

    @Override
    public void run() {
        Thread t = Thread.currentThread();

        while(running) {
            gravitize();
            sendUp();
            sleep(1);
            if(t.isInterrupted()) {
                running = false;
                System.out.println("broke");
                break;
            }
        }
    }

    @Override
    public int compareTo(Player o) {
        return 0;
    }
}