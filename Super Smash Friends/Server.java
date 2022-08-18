import javax.swing.JFrame;
import java.io.*;

public class Server{
  public static void main(String[] args) throws IOException, ClassNotFoundException {

    JFrame frame = new JFrame("Super Smash Friends - Player Two");

    ServerScreen sc = new ServerScreen();
    frame.add(sc);

    frame.setSize(600, 600);
    frame.setLocation(5, 5);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();

    frame.setVisible(true);

    sc.listen();

  }
}
