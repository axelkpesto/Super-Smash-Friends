import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

    public class Sword implements Runnable, Comparable<Sword> {

    private int x, y, charge;

    private double theta;

    private String direction;

    private Player p;

    private boolean swinging = false;

    private boolean existing = true;

    private Shape shape;

    private BufferedImage sword;
    
    public Sword(Player p, int charge) {
        x = p.getX();
        y = p.getY();
        this.charge = charge;
        direction = p.lastFacing();
        theta = 0;
        this.p = p;

		try{
            File newFile = new File("Images/Sword.png");
            InputStream fis = Sword.class.getResourceAsStream(newFile.toString());
			sword = ImageIO.read(fis);
		} catch (IOException e) {
			e.printStackTrace();
		}

        shape = new Rectangle(sword.getMinX()+10,sword.getMinY()-10,sword.getWidth(),sword.getHeight());
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getCharge() {
        return charge;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String s) {
        direction = s;
    }

    public void setCharge(int i) {
        charge = i;
    }

    private AffineTransform rotate(Image image) {

        int height = image.getHeight(null);
        int width = image.getWidth(null);

        AffineTransform affineTransform = new AffineTransform();
        affineTransform.translate(p.getX(),p.getY());

        if(direction.equals("right")) {
            affineTransform.translate(10, -10);
            affineTransform.rotate(Math.toRadians(theta), width/2, height-10);
        } else {
            affineTransform.translate(0, -10);
            affineTransform.rotate(Math.toRadians(-theta), width/2, height-10);
        }

        return affineTransform;
    }

    private Shape rotate(Shape s) {

        int c = p.getX();
        int h = p.getY();
        AffineTransform affineTransform = new AffineTransform();

        Shape create = new Rectangle(c,h,8,40);

        if(direction.equals("right")) {
            create = new Rectangle(c+12,h-10,6,40);
            affineTransform.rotate(Math.toRadians(theta), c+10, h+20);
        } else {
            create = new Rectangle(c+2,h-10,8,40);
            affineTransform.rotate(Math.toRadians(-theta), c+10, h+20);
        }

        Shape temp = affineTransform.createTransformedShape(create);

        return temp;
    }

    public void drawMe(Graphics g) {
        g.setColor(Color.CYAN);
        Graphics2D g2d = (Graphics2D)g;

        shape = rotate(shape);
        if(direction.equals("right")) {
            g2d.drawImage(sword,rotate(sword),null);
        } else {
            g2d.drawImage(sword,rotate(sword),null);
        }
    }

    public void swing() {
        theta+=(5*(charge/2));
        if(theta == 110) {
            reset();
        }
    }

    public void reset() {
        swinging = false;
        theta = 0;
        charge = 0;
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
        if(!isInt(arr[0]) || !isInt(arr[1]) || !isInt(arr[2]) || !isDouble(arr[3])) {
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

    public Shape getBounds() {
        return shape;
    }

    public void setBounds(Shape s) {
        shape = s;
    }

    public String animateString() {
        return x + "," + y + "," + charge + "," + theta + "," + direction + "," + swinging;
    }

    public void setAll(String s) {
        if(correctString(s) && correctFormat(s)) {
            String[] arr = s.split(",");
            this.x = Integer.parseInt(arr[0]);
            this.y = Integer.parseInt(arr[1]);
            this.charge = Integer.parseInt(arr[2]);
            this.theta = Double.parseDouble(arr[3]);
            this.direction = arr[4];
            this.swinging = Boolean.parseBoolean(arr[5]);
        }
    }

    public boolean checkCollision(Player p) {
        if(swinging) {
            if(((Shape)getBounds()).intersects(p.getBounds())) {
                return true;
            }
        } return false;
    }

    @Override
    public int compareTo(Sword o) {
        return 0;
    }

    public void sleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch(InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}

    public void setSwing(boolean b) {
        swinging = b;
    }

    public double getRotation() {
        return theta;
    }

    public void updatePosition() {
        if(!p.lastFacing().equals(direction)) {
            reset();
        }

        direction = p.lastFacing();

        if(direction.equals("right")) {
            x = p.getX()+20;
            y = p.getY()-15;
        } else {
            x = p.getX();
            y = p.getY()-15;
        }
    }

    @Override
    public void run() {
        Thread t = Thread.currentThread();

        while(existing) {
            if(swinging) {
                swing();
            }
            updatePosition();
            sleep(10);
            if(t.isInterrupted()) {
                break;
            }
        }
    }

    public String toString() {
        return "X: " + x + ", Y: " + y + ", Radians: " + theta;
    }

}