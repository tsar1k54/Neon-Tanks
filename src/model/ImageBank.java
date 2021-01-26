package model;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

public class ImageBank {
    protected Image[] images;

    public ImageBank(String dir, String... path){
        images = new Image[path.length];

        for(int i = 0; i < images.length; i++){
            try{
                images[i] =  ImageIO.read(new File(dir + path[i]));
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    public Image getImageAt(int i){
        return images[i];
    }

}
