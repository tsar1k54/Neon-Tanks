package model;

import java.awt.*;

enum DisplayStates{
    MENU, RECORDS, PAUSE, GAME, GAME_OVER
}

public class Display extends Canvas {
    private DisplayStates state;

    public void setState(DisplayStates state){
        this.state = state;
    }

    public DisplayStates getState(){
        return this.state;
    }


}

