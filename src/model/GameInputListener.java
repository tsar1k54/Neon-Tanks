package model;

import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;

public class GameInputListener extends JComponent {
    private boolean[] keyMap;

    public  GameInputListener(){
        keyMap = new boolean[256];

        for (int i = 0; i < keyMap.length; i++){
            final int keyCode = i;

            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(i, 0, false), i*2);
            getActionMap().put(i*2, new AbstractAction(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    keyMap[keyCode] = true;
                }
            });

            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(i, 0, true), i*2 + 1);
            getActionMap().put(i*2 + 1, new AbstractAction(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    keyMap[keyCode] = false;
                }
            });
        }
    }

    public boolean getKey(int keyCode){
        return keyMap[keyCode];
    }

    public void setKeyReleased(int keyCode){
        keyMap[keyCode] = false;
    }
}
