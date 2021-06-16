import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Observable;
import java.util.Observer;

// the edit toolbar
public class ToolBarView extends JPanel implements Observer {
	private GameModel gmodel;

    JButton undo;
    JButton redo;

    public ToolBarView(GameModel model) {
    		gmodel = model;
    		gmodel.addObserver(this);

        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        undo = new JButton("Undo");
        redo = new JButton("Redo");
        // prevent buttons from stealing focus
        undo.setFocusable(false);
        redo.setFocusable(false);
        // initially buttons are greyed out
        undo.setEnabled(gmodel.canUndo());
		redo.setEnabled(gmodel.canRedo());
        this.add(undo);
        this.add(redo);
        
        // add listener to undo and redo button
        undo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gmodel.undo();
			}
		});
        redo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gmodel.redo();
			}
		});
    }

    @Override
    public void update(Observable o, Object arg) {
		undo.setEnabled(gmodel.canUndo());
		redo.setEnabled(gmodel.canRedo());
    }
}
