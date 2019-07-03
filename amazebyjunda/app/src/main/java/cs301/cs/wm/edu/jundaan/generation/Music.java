package cs301.cs.wm.edu.jundaan.generation;

import android.content.Context;
import android.media.MediaPlayer;
import cs301.cs.wm.edu.jundaan.R;

public class Music {
    private MediaPlayer player;
    public Music(){

    }
    public void selectMusic(Context context){
        player = MediaPlayer.create(context.getApplicationContext(), R.raw.select);
        player.setVolume(.6f, .6f);
        player.setLooping(true);
    }


    public void bgmMusic(Context context){
        player = MediaPlayer.create(context.getApplicationContext(), R.raw.bgm);
        player.setVolume(.6f, .6f);
        player.setLooping(true);
    }

    public void winMusic(Context context){
        player = MediaPlayer.create(context.getApplicationContext(), R.raw.win);
        player.setVolume(.6f, .6f);
    }

    public void loseMusic(Context context){
        player = MediaPlayer.create(context.getApplicationContext(), R.raw.lose);
        player.setVolume(.6f,.6f);
    }

    public void playMusic(){
        if(player != null){
            player.start();
        }
    }

    public void pauseMusic(){
        if(player != null && player.isPlaying()){
            player.pause();
        }
    }
}
