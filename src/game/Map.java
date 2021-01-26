package game;

import java.io.*;
import java.util.ArrayList;

public class Map {
    public static final int WIDTH = 17;
    public static final int HEIGHT = 16;
    private Tile[][] tiles;
    private ArrayList<Tile> blocks;

    public Map() {
        tiles = new Tile[HEIGHT][WIDTH];

        try {
            File file = new File("res//Game//map");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            String[] b;
            for(int i = 0; i < HEIGHT; i++){
                line = bufferedReader.readLine();
                b = line.split("");

                for (int j = 0; j < WIDTH; j++){
                    boolean block = (b[j].equals("X")) ? true : false;
                    tiles[i][j] = new Tile(i, j, block);
                }
            }

            bufferedReader.close();
            fileReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        blocks = new ArrayList<Tile>();

        for(int i = 0; i < HEIGHT; i++){
            for (int j = 0; j < WIDTH; j++){
                Tile t = tileAt(i, j);
                if (t.isBlock())
                    blocks.add(t);
            }
        }

    }

    public Tile tileAt(int i, int j){
        return tiles[i][j];
    }

    public Tile getBlock(int i){
        return blocks.get(i);
    }

    public int getBlocksCount(){
        return blocks.size();
    }
}
