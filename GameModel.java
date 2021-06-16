import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import javax.swing.undo.*;
import javax.vecmath.*;
import java.util.Random;
import javax.swing.undo.*;

public class GameModel extends Observable {
	// landing pad data
	private double landingPadX;
	private double landingPadY;
	Rectangle2D.Double landingPad;
	// data used for dragging landing pad
	double tempX;
	double tempY;
	// world bound data
	Rectangle2D.Double worldBounds;
	private int width;
	private int height;
	// ship data
	public Ship ship;
	// terrain data
	Polygon terrain;
	int[] xpoints = new int[22];
	int[] ypoints = new int[22];
	private int circleNumber;
	// undo manager
	private UndoManager undoManager;

	public GameModel(int fps, int width, int height, int peaks) {
		undoManager = new UndoManager();
		ship = new Ship(60, width / 2, 50);
		worldBounds = new Rectangle2D.Double(0, 0, width, height);
		this.width = width;
		this.height = height;
		// initialize landing pad position and drag landing pad position
		this.landingPadX = 330;
		this.landingPadY = 100;
		this.tempX = 330;
		this.tempY = 100;
		landingPad = new Rectangle2D.Double(landingPadX, landingPadY, 40, 10);

		// initialize array of xpoints for terrain polygon
		xpoints[0] = 0;
		xpoints[21] = 700;
		for (int i = 1; i < 21; i++) {
			xpoints[i] = ((int) (i-1) * width / 19);
		}
		// initialize array of ypoints for terrain polygon
		ypoints[0] = 200;
		ypoints[21] = 200;
		Random rand = new Random();
		for (int i = 1; i < 21; i++) {
			ypoints[i] = rand.nextInt(100) + 100;
		}
		terrain = new Polygon(xpoints, ypoints, 22);

		// anonymous class to monitor ship updates
		ship.addObserver(new Observer() {
			public void update(Observable o, Object arg) {
				setChangedAndNotify();
			}
		});
	}

	// World
	// - - - - - - - - - - -
	public final Rectangle2D getWorldBounds() {
		return worldBounds;
	}

	// landing pad
	public double getLPX() {
		return landingPadX;
	}

	public double getLPY() {
		return landingPadY;
	}
	// constrain x position of landing pad
	public double constrainX(double x) {
		if (x < 0) {
			return 0;
		} else if (x > (width-40)) {
			return (width-40);
		} else {
			return x;
		}
	}
	// constrain y position of landing pad
	public double constrainY(double y) {
		if (y < 0) {
			return 0;
		} else if (y > (height-10)) {
			return (height-10);
		} else {
			return y;
		}
	}
	// set landing pad position after drag
	public void setLandingPad(double x, double y) {
		System.out.println("GameModel: set landing pad position to "+landingPadX+","+landingPadY);
		// create undoable edit
		UndoableEdit undoableEdit = new AbstractUndoableEdit() {
			// capture variables for closure
			final double oldLPX = landingPadX;
			final double newLPX = x;
			final double oldLPY = landingPadY;
			final double newLPY = y;
			
			// method that is called when we want to redo the undone action
			public void redo() throws CannotRedoException {
				super.redo();
				landingPadX = newLPX;
				landingPadY = newLPY;
				landingPad = new Rectangle2D.Double(landingPadX, landingPadY, 40, 10);
				System.out.println("GameModel: redo landing pad position to "+landingPadX+","+landingPadY);
				setChangedAndNotify();
			}
			// method that is called when we want to undo the done action
			public void undo() throws CannotUndoException {
				super.undo();
				landingPadX = oldLPX;
				landingPadY = oldLPY;
				landingPad = new Rectangle2D.Double(landingPadX, landingPadY, 40, 10);
				System.out.println("GameModel: undo landing pad position to "+landingPadX+","+landingPadY);
				setChangedAndNotify();
			}
		};
		// add this undoable edit to the undo manager
		undoManager.addEdit(undoableEdit);
		// finally, set the value and notify views
		landingPadX = x;
		landingPadY = y;
		landingPad = new Rectangle2D.Double(landingPadX, landingPadY, 40, 10);
		setChangedAndNotify();
	}

	// terrian
	public void setCircleNumber(int i) {
		circleNumber = i;
	}

	public int getCircleNumber() {
		return circleNumber;
	}

	public int getXPoint(int i) {
		return xpoints[i];
	}

	public int getYPoint(int i) {
		return ypoints[i];
	}
	public double constrainYPoints(double y) {
		if (y < 0) {
			return 0;
		} else if (y > height) {
			return height;
		} else {
			return y;
		}
	}
	public int[] setTempY(int i, double y) {
		ypoints[i] = (int) y;
		return ypoints;
	}
	public void setYPoints(int i, double y) {
		System.out.println("GameModel: set terrain point "+i+"'s height to "+y);
		// create undoable edit
		UndoableEdit undoableEdit = new AbstractUndoableEdit() {
			// capture variables for closure
			final double oldY = ypoints[circleNumber];
			final double newY = y;
			
			public void redo() throws CannotRedoException {
				super.redo();
				circleNumber = i;
				ypoints[circleNumber] = (int) newY;
				terrain = new Polygon(xpoints, ypoints, 22);
				System.out.println("GameModel: redo terrian point "+circleNumber+"'s height to "+newY);
				setChangedAndNotify();
			}
			public void undo() throws CannotUndoException {
				super.undo();
				circleNumber = i; 
				ypoints[circleNumber] = (int) oldY;
				terrain = new Polygon(xpoints, ypoints, 22);
				System.out.println("GameModel: undo terrian point "+circleNumber+"'s height to "+newY);
				setChangedAndNotify();
			}
		};
		undoManager.addEdit(undoableEdit);
		
		circleNumber = i;
		ypoints[i] = (int) y;
		terrain = new Polygon(xpoints, ypoints, 22);
		setChangedAndNotify();
	}

	// Observerable
	// helper function to do both
	void setChangedAndNotify() {
		setChanged();
		notifyObservers();
	}
	
	public void undo() {
		if (canUndo()) 
			undoManager.undo();
	}
	public void redo() {
		if (canRedo())
			undoManager.redo();
	}
	public boolean canUndo() {
		return undoManager.canUndo();
	}
	public boolean canRedo() {
		return undoManager.canRedo();
	}
}
