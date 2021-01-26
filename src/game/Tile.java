package game;

import static model.TextureAtlas.*;

public class Tile implements SideBounds{
    private boolean isBlock;
    private int x;
    private int y;

    public Tile(int i, int j, boolean isBlock){
        x = j * SPRITE_SIZE;
        y = i * SPRITE_SIZE;
        this.isBlock = isBlock;
    }

    public boolean isBlock(){
        return isBlock;
    }

    @Override
    public int left() {
        return x;
    }

    @Override
    public int right() {
        return x + SPRITE_SIZE - 1;
    }

    @Override
    public int top() {
        return y;
    }

    @Override
    public int bottom() {
        return y + SPRITE_SIZE - 1;
    }

    public void setLocation(int i, int j){
        x = j * SPRITE_SIZE;
        y = i * SPRITE_SIZE;
    }
}
