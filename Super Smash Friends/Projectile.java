import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Image;
import javax.swing.ImageIcon;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

public class Projectile implements Runnable, Comparable<Projectile>{

    private int x, y, charge;

    private String direction = "";

    private boolean existing = true;

    private double acceleration = 1;

    private DLList<Terrain> terrainList;

    private Player p;

    private Image arrow;

    public Projectile(DLList<Terrain> terrainList, Player p, int charge) {
        x = p.getX();
        y = p.getY()+18;
        this.charge = charge;
        direction = p.getDirection();
        this.terrainList = terrainList;
        this.p = p;

        getSpot(p);

        try {
			arrow = new ImageIcon("Images/Arrow.png").getImage();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    private AffineTransform rotateImageCounterClockwise(Image image) {

        int imageWidth = image.getWidth(null);
        int imageHeight = image.getHeight(null);

        AffineTransform affineTransform = new AffineTransform();
        affineTransform.rotate(-Math.PI / 2, imageWidth / 2, imageHeight / 2);

        double offset = (imageWidth - imageHeight) / 2;
        affineTransform.translate(-offset, -offset);

        return affineTransform;
    }

    public void getSpot(Player p) {
        if(direction.equals("right")) {
            x = p.getX()+20;
        } else {
            x = p.getX();
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getDirection() {
        return direction;
    }

    public void setExisting(boolean set) {
        existing = set;
    }

    public void setDirection(String s) {
        direction = s;
    }

    public void drawMe(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        if(existing) {
            g.setColor(Color.pink);
            // g.drawRect(x, y, 5, 5);
            if(direction.equals("left")) {
                g2d.drawImage(arrow, x+5, y, -5, 5, null);
            } else if(direction.equals("right")) {
                g2d.drawImage(arrow, x, y, 5, 5, null);
            } else if(direction.equals("up")) {
                g2d.drawImage(arrow,rotateImageCounterClockwise(arrow),null);
            }
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x-1,y-1,7,7);
    }
    
    //main problem is that it blips out. program works fine if I dont change its existing status
    //maybe do collision detection inside of player?
    public boolean checkCollision(Player p) {
        if(existing && getBounds().intersects(p.getBounds())) {
            // existing = false;
            return true;
        } return false;
    }

    public boolean checkCollision(Terrain t) {
        if(getBounds().intersects(t.getBounds())) {
            existing = false;
            return true;
        } return false;
    }

    public boolean onPlatform() {
        if(intersectsOnce(getBounds())) {
            return true;
        } return false;
    }

    public boolean intersectsOnce(Rectangle r) {
        boolean intersects = false;
        for(int i=0; i<terrainList.size(); i++) {
            if((r.intersects(terrainList.get(i).getBounds()))) {
                intersects = true;
            }
        } return intersects;
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

    public boolean correctFormat(String s) {
        String[] arr = s.split(",");
        if(!isInt(arr[0]) || !isInt(arr[1]) || !isInt(arr[2]) || !isDouble(arr[4])) {
            return false;
        } return true;
    }

    public boolean correctString(String s) {
        try {
            s.split(",");
            return true;
        } catch (NullPointerException e) {
            return false;
        }
    }

    public boolean getExisting() {
        return existing;
    }

    public void reset() {
        getSpot(p);
        this.y = p.getY()+20;
        existing = false;
        acceleration = 1;
    }

    public void setAll(String s) {
        if(correctString(s) && correctFormat(s)) {
            String[] arr = s.split(",");
            this.x = Integer.parseInt(arr[0]);
            this.y = Integer.parseInt(arr[1]);
            this.charge = Integer.parseInt(arr[2]);
            this.direction = arr[3];
            this.acceleration = Double.parseDouble(arr[4]);
            this.existing = Boolean.parseBoolean(arr[5]);
        }
    }

    public void move(String direction) {
        if(direction.toLowerCase().equals("right")) {
            x+=(3*charge)+1;
            this.direction = direction;
        } else if(direction.toLowerCase().equals("left")) {
            x-=(3*charge)+1;
            this.direction = direction;
        } else if(direction.toLowerCase().equals("down")) {
            y+=(3*charge)+1;
            this.direction = direction;
        } else if(direction.toLowerCase().equals("up")) {
            y-=(3*charge)+1;
            this.direction = direction;
        }
    }

    public int getCharge() {
        return charge;
    }

    public void gravitize() {
        if(existing && charge<2) {
            y += .5*acceleration;
            accelerate();
            sleep(10);
        } else if(existing && charge>=2 && charge<4) {
            y += .5*acceleration;
            accelerate();
            sleep(10);
        } else if(existing && charge>3) {
            y += .5*acceleration;
            accelerate();
            sleep(10);
        }
    }

    public void accelerate() {
        if(acceleration<55) {
            if(existing && charge<2) {
                acceleration = acceleration*1.11;
            } else if(existing && charge>=2 && charge<4) {
                acceleration = acceleration*1.1;
            } else if(existing && charge>3) {
                acceleration = acceleration*1.035;
            }
        }
    }

    @Override
	public boolean equals(Object o) {
		Projectile p = (Projectile)o;
		if(p.getX() == x && p.getY()+20 == y && p.getCharge() == charge) {
			return true;
		}
		return false;
	}

    public void checkStatus() {
        if(existing && (x>900 || y<-100)) {
            existing = false;
        }
    }

    @Override
    public void run() {
        Thread t = Thread.currentThread();

        while(existing) {
            move(direction);
            gravitize();
            checkStatus();
            sleep(10);
            if(t.isInterrupted()) {
                existing = false;
                break;
            }

            if(onPlatform()) {
                existing = false;
            }
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
    public int compareTo(Projectile p) {
        return 0;
    }

    public String toString() {
        return x + "," + y + "," + charge + "," + direction + "," + acceleration + "," + existing;
    }

}