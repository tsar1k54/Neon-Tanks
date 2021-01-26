package game;


import javax.sound.sampled.*;

import static model.TextureAtlas.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Tank extends MovingObject {
    protected float energyCapacity;
    protected float energyLevel;
    protected Timer shooting;
    protected ArrayList<Laser> lasers;
    protected Clip shot;
    protected FloatControl volumeControl;
    protected float damage;

    public Tank(int id){
        this.id = id;
        if(id <= 10){
            energyCapacity = 100.0f;
            damage = 20;
        }
        else {
            energyCapacity = 100.0f * (1 + id / 10);
            damage = 20 * (1.0f + id / 10 );
        }
        energyLevel = energyCapacity;
        speed = Math.round(SPRITE_SIZE / 32);
        if (id % 5 == 0){
            speed *= 2;
        }
        direction = Direction.DOWN;
        moving = false;
        lasers = new ArrayList<Laser>();
        try {
            File file = new File("res//Game//shot.wav");
            AudioInputStream stream = AudioSystem.getAudioInputStream(file);
            shot = AudioSystem.getClip();
            shot.open(stream);
            volumeControl = (FloatControl) shot.getControl(FloatControl.Type.MASTER_GAIN);
            shot.setFramePosition(0);
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e){
            e.printStackTrace();
            System.exit(1);
        }
        shooting = new Timer();
        setGameVolume();
    }

    public void shoot(){
        if (canShoot()){
            shooting.restart();
            lasers.add(new Laser(this));
            playShotSound();
        }
    }

    public int getLasersCount(){
        return lasers.size();
    }

    public Laser getLaser(int i){
        return lasers.get(i);
    }

    public void damage(float value){
        energyLevel -= value;
    }

    public void removeLaser(int i){
        lasers.remove(i);
    }

    public void playShotSound(){
        shot.stop();
        shot.setFramePosition(0);
        shot.start();
    }

    public boolean canShoot(){
        if(energyLevel > Math.round(energyCapacity*0.10) && shooting.getElapsedTimeSec() > 1.0f && !shot.isRunning()) {
            return true;
        }

        return false;
    }

    public int getEnergyLevel() {
        return (int)energyLevel;
    }

    public int getEnergyCapacity() {
        return (int)energyCapacity;
    }

    public float getDamage() {
        return damage;
    }

    public void setGameVolume() {
        float x = Game.getVolume();
        float min = volumeControl.getMinimum();
        float max = volumeControl.getMaximum();
        volumeControl.setValue((max - min) * x + min);
    }


}
