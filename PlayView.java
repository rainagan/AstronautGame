import javax.swing.*;
import javax.vecmath.Point2d;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Observable;
import java.util.Observer;

// the actual game view
public class PlayView extends JPanel implements Observer {
	private GameModel gmodel;
	private boolean pause = true;
	private boolean crash = false;

	public PlayView(GameModel model) {
		gmodel = model;
		gmodel.addObserver(this);
		// needs to be focusable for keylistener
		setFocusable(true);
		// want the background to be black
		setBackground(Color.black);
		
		// constroller
		this.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				// wasd controls the ship
				if (!gmodel.ship.getCrash() && !gmodel.ship.getLand()) {
					if (e.getKeyCode() == KeyEvent.VK_W) {
						gmodel.ship.thrustUp();
						System.out.println("ship goes up");
					} else if (e.getKeyCode() == KeyEvent.VK_A) {
						gmodel.ship.thrustLeft();
						System.out.println("ship goes left");
					} else if (e.getKeyCode() == KeyEvent.VK_S) {
						gmodel.ship.thrustDown();
						System.out.println("ship goes down");
					} else if (e.getKeyCode() == KeyEvent.VK_D) {
						gmodel.ship.thrustRight();
						System.out.println("ship goes right");
					}
					// if space is pressed, pause/unpause the game
					else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
						pause = !pause;
						gmodel.ship.setPaused(pause);
						if (pause) {
							System.out.println("ship paused");
						} else {
							System.out.println("ship unpaused");
						}
					}
				} else {
					// if press space after CRASH or LANDED!, restart the game
					if (e.getKeyCode() == KeyEvent.VK_SPACE) {
						pause = true;
						gmodel.ship.reset(gmodel.ship.startPosition);
					}
				}
			}
		});
	}

	@Override
	public void update(Observable o, Object arg) {
		repaint();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, // antialiasing look nicer
				RenderingHints.VALUE_ANTIALIAS_ON);
		
		// check if ship crashes
		if (gmodel.terrain.intersects(gmodel.ship.getShape()) || !gmodel.getWorldBounds().intersects(gmodel.ship.getShape())) {
			gmodel.ship.stop();
			gmodel.ship.setCrash(true);
		}
		// check if ship lands
		if (gmodel.landingPad.intersects(gmodel.ship.getShape())) {
			if (gmodel.ship.getSpeed()<gmodel.ship.getSafeLandingSpeed()) {
				gmodel.ship.setLand(true);
			} else {
				gmodel.ship.setCrash(true);
			}
			gmodel.ship.stop();
		}

		// multiply in this shape's transform (uniform scale)
		Point2d p = gmodel.ship.getPosition();
		 g2.translate(350, 150);
		 g2.scale(3, 3);
		 g2.translate((-1)*p.x, (-1)*p.y);
			
		g2.setPaint(Color.lightGray);
		g2.fill(new Rectangle2D.Double(-10,-10,720,210));

		// draw gray terrain
		g2.setPaint(Color.darkGray);
		g2.fill(gmodel.terrain);
		
		// draw red landing pad
		g2.setPaint(Color.RED);
		g2.fill(gmodel.landingPad);
		// draw blue ship
		g2.setPaint(Color.BLUE);
		g2.fill(gmodel.ship.getShape());
	}
}
