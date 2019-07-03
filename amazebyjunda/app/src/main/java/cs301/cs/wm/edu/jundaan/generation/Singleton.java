package cs301.cs.wm.edu.jundaan.generation;

import android.content.Context;

import cs301.cs.wm.edu.jundaan.gui.PlayAnimationActivity;
import cs301.cs.wm.edu.jundaan.gui.PlayManuallyActivity;

public class Singleton {
    private static MazeConfiguration mazeConfiguration;
    private static Order.Builder builder;
    private static int level;
    private static Context context;
    private static float energyCon;
    private static Music player;
    private static PlayManuallyActivity playManuallyActivity = null;
    private static PlayAnimationActivity playAnimationActivity = null;


    public static void setBuilder(Order.Builder builder) {
        Singleton.builder = builder;
    }

    public static Order.Builder getBuilder(){
        return Singleton.builder;
    }



    public static void setMazeConfiguration(MazeConfiguration mazeConfiguration){
        Singleton.mazeConfiguration = mazeConfiguration;
    }

    public static MazeConfiguration getMazeConfiguration() {
        return Singleton.mazeConfiguration;
    }

    public static void setLevel(int level){
        Singleton.level = level;
    }

    public static int getLevel(){
        return Singleton.level;
    }

    public static void setEnergyCon(float ec){
        energyCon = ec;
    }

    public static float getEnergyCon() {
        return energyCon;
    }

    public static void setContext(Context context) {
        Singleton.context = context;
    }

    public static Context getContext() {
        return context;
    }

    public static void setPlayer(Music musicPlayer){
        player = musicPlayer;
    }

    public static Music getPlayer() {
        return player;
    }

    public static void setPlayManuallyActivity(PlayManuallyActivity playManuallyActivity1){
        playManuallyActivity = playManuallyActivity1;
    }

    public static PlayManuallyActivity getPlayManuallyActivity(){
        return playManuallyActivity;
    }

    public static void setPlayAnimationActivity(PlayAnimationActivity playAnimationActivity1){
        playAnimationActivity = playAnimationActivity1;
    }

    public static PlayAnimationActivity getPlayAnimationActivity(){
        return playAnimationActivity;
    }
}
