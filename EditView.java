import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;

// the editable view of the terrain and landing pad
public class EditView extends JPanel implements Observer {
	private GameModel gmodel;
	// lock one component while the other is being dragged
	private boolean lockLP = false;
	private boolean lockTerrain = false;
	// data used to repaint selected circle or landing pad
	private boolean dragged = false;
	private boolean LPselect = false;
	private boolean pressed = false;
	private int selectCircle = -1;
	// mouse position
	private double x;
	private double y;

	public EditView(GameModel model_) {
		// want the background to be black
		setBackground(Color.black);

		gmodel = model_;
		gmodel.addObserver(this);

		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				// reset landing pad position if double click
				if (e.getClickCount() == 2) {
					gmodel.setLandingPad(gmodel.constrainX(e.getX()), gmodel.constrainY(e.getY()));
					repaint();
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (dragged) {
					// set landing pad position after drag
					if (lockTerrain) {
						gmodel.setLandingPad(gmodel.constrainX(e.getX() - 20), gmodel.constrainY(e.getY() - 5));
						gmodel.tempX = gmodel.getLPX();
						gmodel.tempY = gmodel.getLPY();
					} 
					// set one circle's height after drag
					else if (lockLP) {
						gmodel.setYPoints(gmodel.getCircleNumber(), gmodel.constrainYPoints(e.getY()));
					}
				}
				// reset data once mouse release
				lockLP = false;
				lockTerrain = false;
				LPselect = false;
				selectCircle = -1;
				pressed = false;
			}

			public void mousePressed(MouseEvent e) {
				dragged = false;
				pressed = true;
				x = e.getX();
				y = e.getY();
				// set one circle's height
				if (!lockTerrain && hittest(e.getX(), e.getY())) {
					gmodel.setYPoints(gmodel.getCircleNumber(), gmodel.constrainYPoints(e.getY()));
				}
			}
		});
		this.addMouseMotionListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent e) {
				dragged = true;
				x = e.getX();
				y = e.getY();
				double recMinX = gmodel.tempX;
				double recMinY = gmodel.tempY;
				double recMaxX = recMinX + 40;
				double recMaxY = recMinY + 10;
				// landing pad hittest
				if (!lockLP && x >= recMinX && x <= recMaxX && y >= recMinY && y <= recMaxY) {
					// highlight landing pad
					LPselect = true;
					// repaint landing pad
					gmodel.landingPad = new Rectangle2D.Double(gmodel.constrainX(e.getX() - 20),
							gmodel.constrainY(e.getY() - 5), 40, 10);
					// record current landing pad position
					gmodel.tempX = e.getX() - 20;
					gmodel.tempY = e.getY() - 5;
					// lock terrain such that if landing pad is on top of terrain, terrain doesn't move
					lockTerrain = true;
					repaint();
				}
				// terrain circles hittest
				else if (!lockTerrain && hittest(x, y)) {
					// lock landing pad such that if landing pad is on top of terrain, landing pad doesn't move
					lockLP = true;
					// repaint terrain
					gmodel.terrain = new Polygon(gmodel.xpoints, gmodel.setTempY(gmodel.getCircleNumber(), gmodel.constrainYPoints(y)), 22);
					repaint();
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
		
		g2.setPaint(Color.lightGray);
		g2.fill(new Rectangle2D.Double(0,0,700,200));

		// draw gray terrain
		g2.setPaint(Color.darkGray);
		g2.fill(gmodel.terrain);
		
		// draw light gray circle about terrian
		for (int i = 1; i < 21; i++) {
			// if select a circle, highlight it
			if (selectCircle == i) {
				g2.setStroke(new BasicStroke(3));
				g2.setPaint(Color.WHITE);
				g2.drawOval(gmodel.getXPoint(i) - 15, gmodel.getYPoint(i) - 15, 30, 30);
			} else {
				g2.setPaint(Color.GRAY);
				g2.setStroke(new BasicStroke(1));
				g2.drawOval(gmodel.getXPoint(i) - 15, gmodel.getYPoint(i) - 15, 30, 30);
			}
		}

		// draw red landing pad
		if (LPselect) {
			// if select landing pad, highlight it
			g2.setStroke(new BasicStroke(5));
			g2.setPaint(Color.WHITE);
			g2.drawRect((int)gmodel.tempX, (int)gmodel.tempY, 40, 10);
			g2.setStroke(new BasicStroke(1));
		}
		g2.setPaint(Color.RED);
		g2.fill(gmodel.landingPad);
		
		// draw circle around mouse cursor if mouse is pressed
		if (pressed) {
			g2.setPaint(Color.BLACK);
			g2.drawOval((int)x-23, (int)y-23, 46, 46);
		}
	}
	
	// hittest that checks if mouse is inside a circle
	public boolean hittest(double x, double y) {
		for (int i = 1; i < 21; i++) {
			double dist = Math.sqrt(Math.pow(x - gmodel.getXPoint(i), 2) + Math.pow(y - gmodel.getYPoint(i), 2));
			if (dist <= 15) {
				selectCircle = i;
				gmodel.setCircleNumber(i);
				return true;
			}
		}
		return false;
	}
}
