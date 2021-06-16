import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.Observable;
import java.util.Observer;

public class MessageView extends JPanel implements Observer {
	private GameModel gmodel;
    // status messages for game
    JLabel fuel = new JLabel("Fuel: 50");
    JLabel speed = new JLabel("Speed: 0.00");
    JLabel message = new JLabel("(Paused)");

    public MessageView(GameModel model) {
    		gmodel = model;
    		gmodel.addObserver(this);
        // want the background to be black
        setBackground(Color.BLACK);

        setLayout(new FlowLayout(FlowLayout.LEFT));
        
        speed.setForeground(Color.GREEN);
        fuel.setForeground(Color.WHITE);
        message.setForeground(Color.WHITE);
        add(fuel);
        add(speed);
        add(message);
        for (Component c: this.getComponents()) {
            c.setPreferredSize(new Dimension(100, 20));
        }
    }
    
    // make JLabel has the correct decimal format
    DecimalFormat dc = new DecimalFormat("0.00");
    DecimalFormat dc1 = new DecimalFormat("0");
    @Override
    public void update(Observable o, Object arg) {
    		// fuel message
    		// if fuel<=10, set the label to be red
    		if (gmodel.ship.getFuel() <= 10) {
    			fuel.setForeground(Color.RED);
    		} 
    		// otherwise, set the label to be white
    		else {
    			fuel.setForeground(Color.WHITE);
    		}
    		// set fuel label to be integer
    		fuel.setText("Fuel: "+dc1.format(gmodel.ship.getFuel()));
    		
    		// speed message
		// if speed is safe, set the label to be green
    		if (gmodel.ship.getSpeed() < gmodel.ship.getSafeLandingSpeed()) {
        		speed.setForeground(Color.GREEN);
    		} 
		// otherwise, set the label to be white
    		else {
    			speed.setForeground(Color.WHITE);
    		}
    		// set speed label to have 2 decimal places
    		speed.setText("Speed: "+dc.format(gmodel.ship.getSpeed()));
    		
    		// state message
    		if (gmodel.ship.isPaused()) {
    			// if the ship crash, show CRASH
    			if (gmodel.ship.getCrash()) {
    				message.setText("CRASH");
    			}
    			// if the ship lands with safe speed, show LANDED!
    			else if (gmodel.ship.getLand()) {
    				if (gmodel.ship.getSpeed() < gmodel.ship.getSafeLandingSpeed()) {
        				message.setText("LANDED!");
    				}
    			}
    			// if the game is paused, show (Paused)
    			else {
    				message.setText("(Paused)");
    			}
    		}
    		// while the ship is flying, no state shows up
    		else if (!gmodel.ship.isPaused()) {
    			message.setText("");
    		} 
    }
}