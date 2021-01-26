package game;

import static model.TextureAtlas.*;


public class Player extends Tank {
    Upgrade upgrade;

    public Player(int id){
        super(id);
        damage = 50;
        energyCapacity = 600;
        energyLevel = energyCapacity;
        this.tileX = 8;
        this.tileY = 13;
        this.x = tileX * SPRITE_SIZE;
        this.y = tileY * SPRITE_SIZE;
        upgrade = new Upgrade();
    }

    @Override
    public int getSpeed() {
        if (upgrade.getType() == Upgrade.HASTE){
            if (x % (speed*2) != 0 || y % (speed*2) != 0){
                switch (direction){
                    case UP:
                        y -= speed;
                        break;

                    case DOWN:
                        y += speed;
                        break;

                    case LEFT:
                        x -= speed;
                        break;

                    case RIGHT:
                        x += speed;
                        break;
                }
            }

            return speed * 2;
        }
        return speed;
    }

    public void setUpgrade(byte type) {
        upgrade.setType(type);
    }

    @Override
    public float getDamage() {
        if (upgrade.getType() == Upgrade.DOUBLE_DAMAGE){
            return damage * 2;
        }

        return damage;
    }

    @Override
    public void damage(float value) {
        if (!(upgrade.getType() == Upgrade.SHIELD))
            energyLevel -= value;
    }

    public void charge(){
        if (upgrade.getType() == Upgrade.ENERGY && energyLevel < energyCapacity)
            energyLevel += 0.25f;
        else energyLevel -= 0.05f;
    }


    public Upgrade getUpgrade() {
        return upgrade;
    }
}
