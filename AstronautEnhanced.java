/* my AstronautEnhanced.java add arrow key (KeyEvent.VK_UP, KeyEvent.VK_LEFT, KeyEvent.VK_DOWN, KeyEvent.VK_RIGHT)
   in addition to wasd key to control the ship
*/
/* use source code from /cs349-code/java/9-undo:
MainMenuView.java, Model.java
*/
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class AstronautEnhanced extends JPanel {

Astronaut() {
        // create the model
        GameModel model = new GameModel(60, 700, 200, 20);

        JPanel PlayViewEnhanced = new PlayViewEnhanced(model);
        JPanel editView = new EditView(model);
        editView.setPreferredSize(new Dimension(700, 200));

        // layout the views
        setLayout(new BorderLayout());

        add(new MessageView(model), BorderLayout.NORTH);

        // nested Border layout for edit view
        JPanel editPanel = new JPanel();
        editPanel.setLayout(new BorderLayout());
        editPanel.add(new ToolBarView(model), BorderLayout.NORTH);
        editPanel.add(editView, BorderLayout.CENTER);
        add(editPanel, BorderLayout.SOUTH);

        // main playable view will be resizable
        add(PlayViewEnhanced, BorderLayout.CENTER);

        // for getting key events into PlayView
        PlayViewEnhanced.requestFocusInWindow();

    }

    public static void main(String[] args) {
        // create the window
        JFrame f = new JFrame(Astronaut); // jframe is the app window
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(700, 600); // window size
        f.setContentPane(newAstronaut
    ()); // add main panel to jframe
        f.setVisible(true); // show the window
    }
}
