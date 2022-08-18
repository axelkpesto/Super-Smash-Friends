import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Image;
import javax.swing.ImageIcon;

public class Terrain implements Comparable<Terrain> {
    
    private int x, y, width, height;
    private String type;

    private Image terrain;

    public Terrain(int x, int y, int width, int height, String type) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.type = type;

        try {
			terrain = new ImageIcon("Images/Ground.png").getImage();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    public void drawMe(Graphics g) {
        g.drawImage(terrain, x, y, null);
    }

    public Rectangle getBounds() {
        return new Rectangle(x,y,width,height);
    }

    public int compareTo(Terrain t) {
        return 0;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getType() {
        return type;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String toString() {
        return "X: " + x + ", Y: " + y + ", Type: " + type;
    }

    public boolean isPassable() {
        if(type.equals("Passable")) {
            return true;
        } return false;
    }
}