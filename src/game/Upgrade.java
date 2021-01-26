package game;

import java.util.Random;

public class Upgrade extends Tile{
    public final static byte DOUBLE_DAMAGE = 0xA;
    public final static byte SHIELD = 0xB;
    public final static byte HASTE = 0xC;
    public final static byte INVISIBILITY = 0xD;
    public final static byte ENERGY = 0xE;
    public final static byte UNDEFINED = 0xF;

    private byte type;

    public Upgrade(){
        super(8, 3, false);
        setUndefined();
    }

    public byte getType() {
        return type;
    }

    public void generateType(Random random){
        int x = random.nextInt(100);

        if (x >= 0 && x <= 74){
            type = ENERGY;
        }
        else if (x >= 75 && x <= 84){
            type = SHIELD;
        }
        else if (x >= 85 && x <= 94){
            type = HASTE;
        }
        else if (x >= 95 && x <= 98){
            type = DOUBLE_DAMAGE;
        }
        else if (x == 99){
            type = INVISIBILITY;
        }

        randLocation(random);
    }

    public void setUndefined(){
        type = UNDEFINED;
    }

    protected void setType(byte type){
        this.type = type;
    }

    public boolean isUndefined(){
        return getType() == UNDEFINED;
    }

    private void randLocation(Random random){
        int x = random.nextInt(100);

        if (x >= 0 && x <= 19){
            setLocation(8, 3);
        }
        else if (x >= 20 && x <= 39){
            setLocation(6, 8);
        }
        else if (x >= 40 && x <= 59){
            setLocation(9, 13);
        }
        else if (x >= 60 && x <= 79){
            setLocation(10, 8);
        }
        else if (x >= 80 && x <= 99){
            setLocation(3, 8);
        }
    }

}
