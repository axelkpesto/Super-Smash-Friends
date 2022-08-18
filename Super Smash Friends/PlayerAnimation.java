import java.io.Serializable;

public class PlayerAnimation implements Serializable {

    private String playerString, weaponString;

    private boolean running;

    private int damage;


    public PlayerAnimation(String playerString, String weaponString) {
        this.playerString = playerString;
        this.weaponString = weaponString;
    }

    public PlayerAnimation(boolean running) {
        this.running = running;
    }

    public PlayerAnimation(String playerString, String weaponString, boolean running) {
        this.playerString = playerString;
        this.weaponString = weaponString;
        this.running = running;
    }

    public PlayerAnimation(String playerString, String weaponString, boolean running, int damage) {
        this.playerString = playerString;
        this.weaponString = weaponString;
        this.running = running;
        this.damage = damage;
    }



    public String getPlayer() {
        return playerString;
    }

    public String getWeapon() {
        return weaponString;
    }

    public boolean isRunning() {
        return running;
    }

    public int getDamage() {
        return damage;
    }

    public String toString() {
        return "Player: " + playerString + ", Weapon: " + weaponString;
    }

}