package cs301.cs.wm.edu.jundaan.gui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Button;
import android.view.View;
import android.util.Log;
import android.content.Intent;
import android.widget.Toast;
import android.widget.ToggleButton;

import cs301.cs.wm.edu.jundaan.R;
import cs301.cs.wm.edu.jundaan.generation.Music;
import cs301.cs.wm.edu.jundaan.generation.Singleton;

import java.util.ArrayList;
import java.util.List;

public class AMazeActivity extends AppCompatActivity{
    private Button LaunchRevisitActivity;
    private Button LaunchExploreActivity;
    private String builder;
    private String load;
    private String str_level;
    private String driver;
    private String Logv = "AMazeActivity";
    private ToggleButton musicPlay;
    private Music music;
    private boolean musicPlaying;

    /**
     * Create title page
     * We use spinner and button to enable users to select game mode
     * Ready to generate maze according to user's input
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        music = new Music();
        setContentView(R.layout.a_maze_activity);
        Singleton.setContext(getApplicationContext());
        Singleton.setPlayer(music);
        music.selectMusic(this);
        music.playMusic();
        musicPlaying = true;


        //set up spinner for maze generating algorithm
        Spinner mazeSpinner = findViewById(R.id.maze);
        List<String> mazeList = new ArrayList<String>();
        mazeList.add("DFS");
        mazeList.add("Prim");
        mazeList.add("Eller");
        ArrayAdapter<String> mazeAdapter = new ArrayAdapter<String>(this, R.layout.spinner, mazeList);

        //set up spinner for robot algorithm
        Spinner robotSpinner = findViewById(R.id.robot);
        List<String> robotList = new ArrayList<String>();
        robotList.add("Manual");
        robotList.add("WallFollower");
        robotList.add("Wizard");
        robotList.add("Explorer");
        robotList.add("Pledge");
        ArrayAdapter<String> robotAdapter = new ArrayAdapter<String>(this, R.layout.spinner, robotList);


        LaunchExploreActivity = findViewById(R.id.explore);
        LaunchRevisitActivity = findViewById(R.id.revisit);

        /**
         * if user clicks on explore, a new maze with selected attributes will be generated
         */
        LaunchExploreActivity.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                SeekBar seekbar = findViewById(R.id.level);
                int level = seekbar.getProgress();
                str_level = String.valueOf(level);
                Log.v("Level", str_level);
                Spinner spinner = findViewById(R.id.maze);
                builder = spinner.getSelectedItem().toString();
                Log.v("Builder",builder);

                Spinner spinner2 = findViewById(R.id.robot);
                driver = spinner2.getSelectedItem().toString();
                Log.v("Driver",driver);


                Toast.makeText(AMazeActivity.this, "Level: " + (String) str_level + "\nBuilder: " + (String) builder + "\nDriver: " + (String) driver, Toast.LENGTH_SHORT).show();

                launchExploreActivity();
            }
        });

        /**
         * If the user clicks on revisit and the level selected is less than 3, a stored maze will be loaded
         * If the user clicks on revisit and the level selected is greater than 2, a new maze will be generated.
         */
        LaunchRevisitActivity.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                SeekBar seekBar = findViewById(R.id.level);
                int level = seekBar.getProgress();
                str_level = String.valueOf(level);

                if(0 <= level && level <=3){
                    switch (level){
                        case 0:
                            load = "maze0";
                            break;
                        case 1:
                            load = "maze1";
                            break;
                        case 2:
                            load = "maze2";
                            break;
                    }

                    Spinner spinner = findViewById(R.id.maze);
                    Log.v("Level", str_level);
                    builder = spinner.getSelectedItem().toString();
                    Log.v("Builder", builder);
                    Spinner spinner2 = findViewById(R.id.robot);
                    driver = spinner2.getSelectedItem().toString();
                    Log.v("Driver", driver);
                    Toast.makeText(AMazeActivity.this, "Load maze from level" + str_level +"\nLevel: " + (String) str_level + "\nBuilder: " + (String) builder + "\nDriver: " + (String) driver, Toast.LENGTH_SHORT).show();

                    launchRevisitActivity();
                }

                else{
                    Spinner spinner = findViewById(R.id.maze);
                    Log.v("Level", str_level);
                    builder = spinner.getSelectedItem().toString();
                    Log.v("Builder", builder);
                    Spinner spinner2 = findViewById(R.id.robot);
                    driver = spinner2.getSelectedItem().toString();
                    Log.v("Driver", driver);
                    Toast.makeText(AMazeActivity.this, "The level is too high to revist. Generate a new maze instead" + "\nLevel: " + (String) str_level + "\nBuilder: " + (String) builder + "\nDriver: " + (String) driver, Toast.LENGTH_SHORT).show();

                    launchExploreActivity();
                }
            }
        });

        musicPlay = findViewById(R.id.musicplay1);
        musicPlay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Log.v(Logv, "Music Paused");
                    music.pauseMusic();
                    Toast.makeText(getApplicationContext(), "Music on", Toast.LENGTH_LONG).show();
                }
                else{
                    Log.v(Logv, "Music Started");
                    music.playMusic();
                    Toast.makeText(getApplicationContext(), "Music off", Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    /**
     * go to generating state and pass important parameters
     */
    private void launchExploreActivity(){

        Intent intent = new Intent(this, GeneratingActivity.class);
        intent.putExtra("skillLevel", str_level);
        intent.putExtra("builder", builder);
        intent.putExtra("driver", driver);
        intent.putExtra("load", false);
        Log.v(Logv, "Ready to create a new maze");
        if(musicPlaying == true) {
            Log.v(Logv, "Music Paused");
            Singleton.getPlayer().pauseMusic();
        }
        startActivity(intent);
        finish();
    }

    /**
     * go to generating state and pass parameters
     */
    private void launchRevisitActivity(){
        Intent intent = new Intent(this, GeneratingActivity.class);
        intent.putExtra("skillLevel", str_level);
        intent.putExtra("builder", builder);
        intent.putExtra("driver", driver);
        intent.putExtra("load", true);
        Log.v(Logv, "Ready to load a stored maze");
        if(musicPlaying == true) {
            Log.v(Logv, "Music Paused");
            Singleton.getPlayer().pauseMusic();
        }
        startActivity(intent);
        finish();
    }
}
