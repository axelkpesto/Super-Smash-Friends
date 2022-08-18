import java.awt.Color;
import java.awt.Graphics;

public class Star implements Comparable<Star>, Runnable {
	private int x, y, width, height;
	private Color white;
    private boolean running = true;
	
	public Star() {
		white = new Color(255,255,255);
		x = (int)(Math.random()*801);
		y = (int)(Math.random()*601);
        width = (int)(Math.random()*3+1);
        height = (int)(Math.random()*3+1);
	}
	
	public void drawStar(Graphics g) {
		g.setColor(white);
		g.fillOval(x,y,width,height);
	}
	
	public void move() {
		x-=(width+height)/2;
		if(x<0) {
			x = 800;
			y = (int)(Math.random()*601);
		}
	}

    @Override
    public void run() {
        Thread t = Thread.currentThread();

        while(running) {
            move();
            sleep(20);
            if(t.isInterrupted()) {
                running = false;
                break;
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

    public int compareTo(Star s) {
        return 0;
    }

    public String toString() {
        return "X: " + x + ", Y: " + y;
    }
}
