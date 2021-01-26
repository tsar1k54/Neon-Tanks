package game;

import static model.TextureAtlas.*;

public class Laser extends MovingObject {
    public final static int WIDTH = 4, HEIGHT = 16;

    public Laser(final Tank tank){
        this.id = tank.id;
        this.direction = tank.direction;
        initLocation(tank.x, tank.y);
        this.tileX = tank.tileX;
        this.tileY = tank.tileY;
        this.speed = tank.speed * 4;

    }

    private void initLocation(int tankX, int tankY){
        switch (direction){
            case UP:
                this.x = tankX + SPRITE_SIZE / 2 - WIDTH / 2;
                this.y = tankY - HEIGHT;
                break;

            case DOWN:
                this.x = tankX + SPRITE_SIZE / 2 - WIDTH / 2;
                this.y = tankY + SPRITE_SIZE;
                break;

            case RIGHT:
                this.x = tankX + SPRITE_SIZE;
                this.y = tankY + SPRITE_SIZE / 2 - WIDTH / 2;
                break;

            case LEFT:
                this.x = tankX - HEIGHT;
                this.y = tankY + SPRITE_SIZE / 2 - WIDTH / 2;
                break;

        }
    }

    @Override
    public int left() {
        return x;
    }

    @Override
    public int right() {
        return x + WIDTH - 1;
    }

    @Override
    public int top() {
        return y;
    }

    @Override
    public int bottom() {
        return y + HEIGHT - 1;
    }
}
