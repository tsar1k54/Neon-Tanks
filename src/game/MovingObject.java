package game;

import static game.Direction.*;
import static model.TextureAtlas.*;

public abstract class MovingObject implements SideBounds {
    protected int x;
    protected int y;
    protected int tileX;
    protected int tileY;
    protected int speed;
    protected boolean moving;
    protected Direction direction;
    protected int id;

    public boolean isMoving(){
        return moving;
    }

    private void moveUP(){
        direction = UP;
        moving = true;
        y -= getSpeed();
        if((y-getSpeed()) / SPRITE_SIZE < tileY-1){
            tileY--;
        }
    }

    private void moveDOWN(){
        direction = DOWN;
        moving = true;
        y += getSpeed();
        if(y / SPRITE_SIZE > tileY){
            tileY++;
        }
    }

    private void moveRIGHT(){
        direction = RIGHT;
        moving = true;
        x += getSpeed();
        if(x / SPRITE_SIZE > tileX){
            tileX++;
        }
    }

    private void moveLEFT(){
        direction = LEFT;
        moving = true;
        x -= getSpeed();
        if((x-getSpeed()) / SPRITE_SIZE < tileX-1){
            tileX--;
        }
    }

    public void stop(){
        moving = false;
    }

    public void setDirection(Direction direction){
        this.direction = direction;
    }

    public Direction getDirection(){
        return direction;
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

    public int getTileX(){
        return tileX;
    }

    public int getTileY(){
        return tileY;
    }

    public int getSpeed(){
        return speed;
    }

    public void setLocation(int tileX, int tileY){
        this.tileX = tileX;
        this.tileY = tileY;
        this.x = tileX * SPRITE_SIZE;
        this.y = tileY * SPRITE_SIZE;
    }

    public int getId(){
        return id;
    }

    public void move(Direction direction){
        switch (direction){
            case UP:
                moveUP();
                break;

            case DOWN:
                moveDOWN();
                break;

            case RIGHT:
                moveRIGHT();
                break;

            case LEFT:
                moveLEFT();
                break;
        }

    }
}
