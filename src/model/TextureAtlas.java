package model;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class TextureAtlas{
    private BufferedImage atlas;

    public final static int SPRITE_SIZE = 64;

    public TextureAtlas(String path){
        Image img = null;
        try {
            img = ImageIO.read(new File(path));
        }
        catch (IOException e){
            e.printStackTrace();
        }
        atlas = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        atlas.getGraphics().drawImage(img, 0,0,null);

    }

    public BufferedImage getSprite(int i, int j){
        return atlas.getSubimage(SPRITE_SIZE*j, SPRITE_SIZE*i, SPRITE_SIZE, SPRITE_SIZE);
    }

}
