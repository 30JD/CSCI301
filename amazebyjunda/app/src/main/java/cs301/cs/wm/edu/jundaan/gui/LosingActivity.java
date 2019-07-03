package cs301.cs.wm.edu.jundaan.gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import cs301.cs.wm.edu.jundaan.R;
import cs301.cs.wm.edu.jundaan.generation.Music;
import cs301.cs.wm.edu.jundaan.generation.Singleton;

public class LosingActivity extends AppCompatActivity{
    private String Logv = "LosingActivity";
    private TextView txt0;
    private TextView txt1;
    private TextView txt2;

    private int shortestPath;
    private int pathLength;
    private int energyCon;
    private Music music;

    /**
     * Create the losing page
     * This means the robot fails to get out of the maze
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.losing_activity);
        music = new Music();
        music.bgmMusic(this);
        Singleton.setContext(getApplicationContext());
        Singleton.setPlayer(music);
        Singleton.getPlayer().playMusic();
        //extract information from the previous activity
        Intent previousIntent = getIntent();
        energyCon = previousIntent.getIntExtra("energy consumption", 0);
        pathLength = previousIntent.getIntExtra("path length", 0);
        shortestPath = previousIntent.getIntExtra("shortest path", 30);
        //set up text for the losing page
        txt0 = findViewById(R.id.energyCon);
        txt1 = findViewById(R.id.pathLength);
        txt2 = findViewById(R.id.shortestPath);
        txt0.setText("Energy Consumption: " + Integer.toString(energyCon));
        txt1.setText("Path Length: " + Integer.toString(pathLength));
        txt2.setText("Shortest Path: " + Integer.toString(shortestPath));
    }

    /**
     * Go back to title state
     */
    private void goBack(){
        Intent intent = new Intent(this, AMazeActivity.class);
        Log.v(Logv, "Go to the title page");
        startActivity(intent);
        finish();
    }

    /**
     * if back button is pressed, go back to title state
     */
    public void onBackPressed(){
        goBack();
    }
}
