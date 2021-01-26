package model;


import java.awt.Image;
import java.io.*;
import java.util.ArrayList;


public abstract class Scores {
    private static ArrayList<String> scores;

    public static void init(){
        scores = new ArrayList<String>();
        try {
            File file = new File("res//Scores//data");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String info;

            while ((info = bufferedReader.readLine()) != null){
                scores.add(0, info);
            }

            bufferedReader.close();
            fileReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    public static Image getBackgroundImage(){
        return Menu.getBackgroundImage();
    }

    public static String getScoreAt(int index){
        return scores.get(index);
    }

    public static void addScore(String info){
        scores.add(0, info);
    }

    public static void saveScores(){
        try{
            File file = new File("res//Scores//data");
            FileWriter fileWriter = new FileWriter(file, false);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            for (int i = 0; i < scores.size(); i++) {
                bufferedWriter.write(getScoreAt(i));
                bufferedWriter.newLine();
            }

            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static int getScoresCount(){
        return scores.size();
    }





}
