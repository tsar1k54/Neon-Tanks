package game;

import model.ImageBank;
import model.TextureAtlas;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import static game.Direction.*;

public class Game {
    private String path = "border.png";
    private boolean gameOver;
    private ImageBank imageBank;
    private TextureAtlas atlas;
    private int score;
    private Player player;
    private ArrayList<Tank> enemies;
    private int lastEnemyID;
    private Map map;
    private Random random;
    private Timer upgradeEffect;
    private Timer upgradeSpawn;
    private Timer enemySpawn;
    private long seed;
    private Upgrade upgrade;
    private Timer timer;
    private GameButton selectedButton;
    private static float volume;

    public Game(){
        gameOver = false;
        imageBank = new ImageBank("res//Game//", path);
        atlas = new TextureAtlas("res//Game//atlas.png");
        score = 0;
        player = new Player(0);
        enemies = new ArrayList<Tank>();
        lastEnemyID = 1;
        for (int i = 0; i < 4; i++){
            enemies.add(new Tank(lastEnemyID));
            lastEnemyID++;
        }
        enemies.get(0).setLocation(1, 1);
        enemies.get(1).setLocation(6, 1);
        enemies.get(2).setLocation(10, 1);
        enemies.get(3).setLocation(15, 1);
        map = new Map();
        random = new Random();
        seed = 0;
        enemySpawn = new Timer();
        upgradeSpawn = new Timer();
        upgradeEffect = new Timer();
        upgrade = new Upgrade();
        timer = new Timer();
        setVolume(volume);
    }

    public Image getPlayerImage(){
        return atlas.getSprite(0,0).getScaledInstance(200, 200, BufferedImage.SCALE_SMOOTH);
    }

    private Image upgradeSprite(byte type){
        switch (type){
            case Upgrade.HASTE:
                return atlas.getSprite(2, 1);

            case Upgrade.SHIELD:
                return atlas.getSprite(2, 2);

            case Upgrade.DOUBLE_DAMAGE:
                return atlas.getSprite(2, 3);

            case Upgrade.INVISIBILITY:
                return atlas.getSprite(2, 4);

            case Upgrade.ENERGY:
                return atlas.getSprite(2, 5);

        }

        return atlas.getSprite(3, 0);
    }

    public Image getUpgradeSprite(){
        return upgradeSprite(upgrade.getType());
    }

    public Image getPlayerUpgradeImage(){
        Image img = upgradeSprite(player.getUpgrade().getType());
        return img.getScaledInstance(75, 75, BufferedImage.SCALE_SMOOTH);
    }

    public BufferedImage getBlockSprite(){
        return atlas.getSprite(1, 0);
    }

    public BufferedImage getPlayerSprite(){
        switch (player.getDirection()){
            case UP:
                if (player.isMoving())
                    return atlas.getSprite(0, 1);
                else return atlas.getSprite(0, 0);

            case DOWN:
                if (player.isMoving())
                    return atlas.getSprite(0, 5);
                else return atlas.getSprite(0, 4);

            case RIGHT:
                if (player.isMoving())
                    return atlas.getSprite(0, 7);
                else return atlas.getSprite(0, 6);

            case LEFT:
                if (player.isMoving())
                    return atlas.getSprite(0, 3);
                else return atlas.getSprite(0, 2);
        }

        return null;
    }

    public BufferedImage getShieldSprite(){
        return atlas.getSprite(2, 6);
    }

    public BufferedImage getEnemySprite(Direction direction, boolean isMoving){

        switch (direction){
            case UP:
                if (isMoving)
                    return atlas.getSprite(1, 2);
                else return atlas.getSprite(1, 1);

            case DOWN:
                if (isMoving)
                    return atlas.getSprite(1, 6);
                else return atlas.getSprite(1, 5);

            case RIGHT:
                if (isMoving)
                    return atlas.getSprite(1, 8);
                else return atlas.getSprite(1, 7);

            case LEFT:
                if (isMoving)
                    return atlas.getSprite(1, 4);
                else return atlas.getSprite(1, 3);
        }

        return null;
    }

    public BufferedImage getLaserSprite(int id, Direction direction){
        BufferedImage img;
        if (id == 0) img = atlas.getSprite(0, 8);
        else img = atlas.getSprite(2, 0);

        if(direction == UP || direction == DOWN){
            return img.getSubimage(0,0, Laser.WIDTH, Laser.HEIGHT);
        } else {
            return img.getSubimage(Laser.WIDTH,0, Laser.HEIGHT, Laser.WIDTH);
        }

    }

    public Player player(){
        return player;
    }

    public Map map(){
        return map;
    }

    public Image getBorderImage(){
        return imageBank.getImageAt(0);
    }

    public int getEnemiesCount(){
        return enemies.size();
    }

    public Tank getEnemy(int id){
        Tank enemy = null;
        for (int i = 0; i < enemies.size(); i++){
            enemy = enemies.get(i);
            if (enemy.getId() == id) return enemy;
        }

        return enemy;
    }

    public void destroyEnemy(int id){
        Tank enemy;
        for (int i = 0; i < enemies.size(); i++){
            enemy = enemies.get(i);
            if (enemy.getId() == id)
                enemies.remove(i);
        }

        if (id % 5 == 0) score += 20;
        else score += 10;
    }

    public int isMovingObjectCollision(MovingObject obj, Direction direction, int speed){
        int collisionObjectID;

        switch (direction){
            case UP:
                if (objectCollisionToBlockUP(obj, speed)) return -1;
                if ((collisionObjectID = objectCollisionToPlayerUP(obj, speed)) == 0) return collisionObjectID;
                if ((collisionObjectID = objectCollisionToEnemyUP(obj, speed)) > 0) return collisionObjectID;
                break;

            case DOWN:
                if (objectCollisionToBlockDOWN(obj, speed)) return -1;
                if ((collisionObjectID = objectCollisionToPlayerDOWN(obj, speed)) == 0) return collisionObjectID;
                if ((collisionObjectID = objectCollisionToEnemyDOWN(obj, speed)) > 0) return collisionObjectID;
                break;

            case RIGHT:
                if (objectCollisionToBlockRIGHT(obj, speed)) return -1;
                if ((collisionObjectID = objectCollisionToPlayerRIGHT(obj, speed)) == 0) return collisionObjectID;
                if ((collisionObjectID = objectCollisionToEnemyRIGHT(obj, speed)) > 0) return collisionObjectID;
                break;

            case LEFT:
                if (objectCollisionToBlockLEFT(obj, speed)) return -1;
                if ((collisionObjectID = objectCollisionToPlayerLEFT(obj, speed)) == 0) return collisionObjectID;
                if ((collisionObjectID = objectCollisionToEnemyLEFT(obj, speed)) > 0) return collisionObjectID;
                break;
        }

        return -2;
    }

    public Direction randDirection(){
        random.setSeed(seed+System.nanoTime());
        seed++;
        switch (random.nextInt(4)){
            case 0:
                return UP;


            case 1:
                return Direction.DOWN;


            case 2:
                return Direction.RIGHT;


            case 3:
                return Direction.LEFT;

        }

        return UP;
    }

    private int objectCollisionToEnemyUP(MovingObject obj, int speed){
        Tank enemy;
        for (int i = 0; i < enemies.size(); i++){
            enemy = getEnemyAt(i);
            if (obj.getId() != enemy.getId() && ((obj.left() <= enemy.right() && obj.left() >= enemy.left()) ||
                    (obj.right() >= enemy.left() && obj.right() <= enemy.right())) &&
                    obj.top()-speed <= enemy.bottom() && obj.top()-speed >= enemy.top()){
                return enemy.getId();
            }
        }
        return -1;
    }

    private int objectCollisionToEnemyDOWN(MovingObject obj, int speed){
        Tank enemy;
        for (int i = 0; i < enemies.size(); i++){
            enemy = getEnemyAt(i);
            if (obj.getId() != enemy.getId() && ((obj.left() <= enemy.right() && obj.left() >= enemy.left()) ||
                    (obj.right() >= enemy.left() && obj.right() <= enemy.right())) &&
                    obj.bottom()+speed >= enemy.top() && obj.bottom()+speed <= enemy.bottom()){
                return enemy.getId();
            }
        }
        return -1;
    }

    private int objectCollisionToEnemyRIGHT(MovingObject obj, int speed){
        Tank enemy;
        for (int i = 0; i < enemies.size(); i++){
            enemy = getEnemyAt(i);
            if (obj.getId() != enemy.getId() && ((obj.top() <= enemy.bottom() && obj.top() >= enemy.top()) ||
                    (obj.bottom() >= enemy.top() && obj.bottom() <= enemy.bottom())) &&
                    obj.right()+speed >= enemy.left() && obj.right()+speed <= enemy.right()){
                return enemy.getId();
            }
        }
        return -1;
    }

    private int objectCollisionToEnemyLEFT(MovingObject obj, int speed){
        Tank enemy;
        for (int i = 0; i < enemies.size(); i++){
            enemy = getEnemyAt(i);
            if (obj.getId() != enemy.getId() && ((obj.top() <= enemy.bottom() && obj.top() >= enemy.top()) ||
                    (obj.bottom() >= enemy.top() && obj.bottom() <= enemy.bottom())) &&
                    obj.left()-speed <= enemy.right() && obj.left()-speed >= enemy.left()){
                return enemy.getId();
            }
        }
        return -1;
    }

    private boolean objectCollisionToBlockUP(MovingObject obj, int speed){
        Tile block;
        for (int i = 0; i <  map.getBlocksCount(); i++){
            block = map.getBlock(i);
            if (((obj.left() <= block.right() && obj.left() >= block.left()) ||
                    (obj.right() >= block.left() && obj.right() <= block.right())) &&
                    obj.top()-speed <= block.bottom() && obj.top()-speed >= block.top()){
                return true;
            }
        }
        return false;
    }

    private boolean objectCollisionToBlockDOWN(MovingObject obj, int speed){
        Tile block;
        for (int i = 0; i <  map.getBlocksCount(); i++){

            block = map.getBlock(i);
            if (((obj.left() <= block.right() && obj.left() >= block.left()) ||
                    (obj.right() >= block.left() && obj.right() <= block.right())) &&
                    obj.bottom()+speed >= block.top() && obj.bottom()+speed <= block.bottom()){
                return true;
            }
        }
        return false;
    }

    private boolean objectCollisionToBlockRIGHT(MovingObject obj, int speed){
        Tile block;
        for (int i = 0; i <  map.getBlocksCount(); i++){
            block = map.getBlock(i);
            if (((obj.top() <= block.bottom() && obj.top() >= block.top()) ||
                    (obj.bottom() >= block.top() && obj.bottom() <= block.bottom())) &&
                    obj.right()+speed >= block.left() && obj.right()+speed <= block.right()){
                return true;
            }
        }
        return false;
    }

    private boolean objectCollisionToBlockLEFT(MovingObject obj, int speed){
        Tile block;
        for (int i = 0; i <  map.getBlocksCount(); i++){
            block = map.getBlock(i);
            if (((obj.top() <= block.bottom() && obj.top() >= block.top()) ||
                    (obj.bottom() >= block.top() && obj.bottom() <= block.bottom())) &&
                    obj.left()-speed <= block.right() && obj.left()-speed >= block.left()){
                return true;
            }
        }
        return false;
    }

    private int objectCollisionToPlayerUP(MovingObject obj, int speed){
        if (obj.getId() != 0 && ((obj.left() <= player.right() && obj.left() >= player.left()) ||
                (obj.right() >= player.left() && obj.right() <= player.right())) &&
                obj.top()-speed <= player.bottom() && obj.top()-speed >= player.top()){
            return player.getId();
        }
        return -1;
    }

    private int objectCollisionToPlayerDOWN(MovingObject obj, int speed){
        if (obj.getId() != 0 && ((obj.left() <= player.right() && obj.left() >= player.left()) ||
                (obj.right() >= player.left() && obj.right() <= player.right())) &&
                obj.bottom()+speed >= player.top() && obj.bottom()+speed <= player.bottom()){
            return player.getId();
        }
        return -1;
    }

    private int objectCollisionToPlayerRIGHT(MovingObject obj, int speed){
        if (obj.getId() != 0 && ((obj.top() <= player.bottom() && obj.top() >= player.top()) ||
                (obj.bottom() >= player.top() && obj.bottom() <= player.bottom())) &&
                obj.right()+speed >= player.left() && obj.right()+speed <= player.right()){
            return player.getId();
        }
        return -1;
    }

    private int objectCollisionToPlayerLEFT(MovingObject obj, int speed){
        if (obj.getId() != 0 && ((obj.top() <= player.bottom() && obj.top() >= player.top()) ||
                (obj.bottom() >= player.top() && obj.bottom() <= player.bottom())) &&
                obj.left()-speed <= player.right() && obj.left()-speed >= player.left()){
            return player.getId();
        }
        return -1;
    }

    public void damageTank(int id, float damageValue){
        if (id == 0){
            player.damage(damageValue);
            if (player.getEnergyLevel() <= 0){
                gameOver = true;
            }
        }
        else {
            Tank enemy = getEnemy(id);
            enemy.damage(damageValue);
            if (enemy.getEnergyLevel() <= 0)
                destroyEnemy(id);

        }
    }

    public void spawnEnemy(){
        if (enemySpawn.getElapsedTimeSec() >= 5 && getEnemiesCount() < 10){
            Tank enemy = new Tank(lastEnemyID);
            enemy.setLocation(11, 4);
            enemies.add(enemy);
            lastEnemyID++;
            enemySpawn.restart();
        }
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public Tank getEnemyAt(int index){
        return enemies.get(index);
    }

    public void spawnUpgrade(){
        if (upgradeSpawn.getElapsedTimeSec() >= 15){
            random.setSeed(seed+System.nanoTime());
            seed++;
            upgrade.generateType(random);
            upgradeSpawn.restart();
        }
    }

    public void playerTakesUpgrade(){
        if ((player.top() >= upgrade.top() && player.top() <= upgrade.bottom() ||
                player.bottom() >= upgrade.top() && player.bottom() <= upgrade.bottom())
                && (player.left() >= upgrade.left() && player.left() <= upgrade.right() ||
                player.right() >= upgrade.left() && player.right() <= upgrade.left())
                && !upgrade.isUndefined()){
            player.setUpgrade(upgrade.getType());
            upgradeEffect.restart();
            upgrade.setUndefined();
        }
    }

    public void playerUpgradeEffect(){
        if (upgradeEffect.getElapsedTimeSec() >= 10){
            player.setUpgrade(Upgrade.UNDEFINED);
        }
    }

    public Upgrade getUpgrade() {
        return upgrade;
    }

    public int getScore() {
        return score;
    }

    public String getScoreInfo() {
        String time = String.format("time: %d sec %d ms", getTimerSec(), getTimerMs());
        return ("Score: " + score + ", " + time);
    }

    public float getTimer(){
        return timer.getElapsedTimeSec();
    }

    public int getTimerSec(){
        return (int)getTimer();
    }

    public int getTimerMs(){
        return (int)((getTimer() - (float) getTimerSec())*100.0f);
    }

    public float getEffectTime(){
        float elapsed = upgradeEffect.getElapsedTimeSec();
        if (elapsed <= 10 && !player.getUpgrade().isUndefined())
            return (10 - elapsed);
        else return 0.0f;
    }

    public GameButton getSelectedButton(){
        return selectedButton;
    }

    public void setSelectedButton(GameButton selectedButton) {
        this.selectedButton = selectedButton;
    }

    public void switchUpPauseButton(){
        switch (getSelectedButton()){
            case RESTART:
                selectedButton = GameButton.EXIT;
                break;

            case RESUME:
                selectedButton = GameButton.RESTART;
                break;

            case SOUND:
                selectedButton = GameButton.RESUME;
                break;

            case EXIT:
                selectedButton = GameButton.SOUND;
                break;
        }
    }

    public void switchDownPauseButton(){
        switch (getSelectedButton()){
            case RESTART:
                selectedButton = GameButton.RESUME;
                break;

            case RESUME:
                selectedButton = GameButton.SOUND;
                break;

            case SOUND:
                selectedButton = GameButton.EXIT;
                break;

            case EXIT:
                selectedButton = GameButton.RESTART;
                break;
        }
    }

    public void pause(){
        timer.stop();
        enemySpawn.stop();
        upgradeEffect.stop();
        upgradeSpawn.stop();
    }

    public void resume(){
        timer.start();
        enemySpawn.start();
        upgradeEffect.start();
        upgradeSpawn.start();
    }

    public static void initVolume(){
        try{
            File file = new File("res//Game//volume");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            volume = Float.parseFloat(bufferedReader.readLine());
            bufferedReader.close();
            fileReader.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void saveVolume(){
        try{
            File file = new File("res//Game//volume");
            FileWriter fileWriter = new FileWriter(file, false);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(String.valueOf(volume));
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public  void volumeUp(){
        setVolume(volume + 0.1f);
    }

    public  void volumeDown(){
        setVolume(volume - 0.1f);
    }

    private void setVolume(float vol){
        volume = vol;
        if (vol < 0.0f) volume = 0.0f;
        else if (vol > 1) volume = 1.0f;
        player.setGameVolume();
        for (int i = 0; i < getEnemiesCount(); i++) {
            enemies.get(i).setGameVolume();
        }
    }

    public static float getVolume() {
        return volume;
    }

    public void switchGameOverButton(){
        switch (getSelectedButton()){
            case RESTART:
                selectedButton = GameButton.BACK_TO_MENU;
                break;

            case BACK_TO_MENU:
                selectedButton = GameButton.RESTART;
                break;
        }
    }


}
