package model;

import java.awt.Image;



enum MenuButton{
    PLAY, SCORES, EXIT
}

public abstract class Menu {
    private static ImageBank imageBank;
    private static MenuButton selected = MenuButton.PLAY;

    public static void init(){
        imageBank = new ImageBank("res//Menu//", "bg.jpg", "Play.png", "Play1.png",
                "Scores.png", "Scores1.png", "Exit.png", "Exit1.png");
    }


    public static Image getBackgroundImage(){
        return imageBank.getImageAt(0);
    }

    public static Image getPlayButtonImage(){
        return (selected == MenuButton.PLAY)
                ? imageBank.getImageAt(1) : imageBank.getImageAt(2);
    }

    public static Image getScoresButtonImage(){
        return (selected == MenuButton.SCORES)
                ? imageBank.getImageAt(3) : imageBank.getImageAt(4);
    }

    public static Image getExitButtonImage(){
        return (selected == MenuButton.EXIT)
                ? imageBank.getImageAt(5) : imageBank.getImageAt(6);
    }

    public static MenuButton getSelectedButton(){
        return selected;
    }

    public static void switchSelectedButtonUp(){
        switch (Menu.getSelectedButton()){
            case PLAY:
                selected = MenuButton.EXIT;
                break;
            case SCORES:
                selected = MenuButton.PLAY;
                break;
            case EXIT:
                selected = MenuButton.SCORES;
                break;
        }
    }

    public static void switchSelectedButtonDown(){
        switch (Menu.getSelectedButton()){
            case PLAY:
                selected = MenuButton.SCORES;
                break;
            case SCORES:
                selected = MenuButton.EXIT;
                break;
            case EXIT:
                selected = MenuButton.PLAY;
                break;
        }
    }

}
