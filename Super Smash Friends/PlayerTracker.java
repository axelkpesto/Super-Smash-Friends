import java.awt.Color;
import java.awt.Graphics;

public class PlayerTracker implements Runnable{
    private int x, y;

    private Player p;

    private boolean running = false;

    public PlayerTracker(Player p) {
        this.x = p.getX();
        this.y = p.getY();
        this.p = p;
        running = true;
    }

    public void follow() {
        this.x = p.getX();
        this.y = p.getY();
    }

    public void drawMe(Graphics g) {
        g.setColor(Color.GRAY);
        int[] xPoints = {x-15,x+35,x+35,x+10,x-15};
        int[] yPoints = {y-30,y-30,y-20,y-5,y-20};
        g.fillPolygon(xPoints, yPoints, 5);
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
        Thread t = Thread.currentThread();

        while(running) {
            follow();
            sleep(10);
            if(t.isInterrupted()) {
                running = false;
                System.out.println("broke");
                break;
            }
        }
    }
}