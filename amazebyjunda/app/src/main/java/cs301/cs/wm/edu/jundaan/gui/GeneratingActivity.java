package cs301.cs.wm.edu.jundaan.gui;


import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.os.Handler;
import android.os.Bundle;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Button;
import android.view.View;
import android.util.Log;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;
import android.os.AsyncTask;
import android.media.MediaPlayer;



import java.io.File;

import cs301.cs.wm.edu.jundaan.R;

import cs301.cs.wm.edu.jundaan.falstad.MazeFileReader;
import cs301.cs.wm.edu.jundaan.falstad.MazeFileWriter;
import cs301.cs.wm.edu.jundaan.falstad.MazePanel;

import cs301.cs.wm.edu.jundaan.falstad.StatePlaying;

import cs301.cs.wm.edu.jundaan.generation.MazeConfiguration;
import cs301.cs.wm.edu.jundaan.generation.MazeFactory;
import cs301.cs.wm.edu.jundaan.generation.Music;
import cs301.cs.wm.edu.jundaan.generation.Order;

import cs301.cs.wm.edu.jundaan.generation.Singleton;


public class GeneratingActivity extends AppCompatActivity implements Order{
    private ProgressBar progressBar;
    private int progress;
    private Handler handler = new Handler();
    private TextView txt;
    private String builder;
    private String driver;
    private String str_level;
    private boolean load;
    private MazeFactory mazeFactory = new MazeFactory();
    private MazeConfiguration mazeConfiguration;
    private long fileSize = 0;
    private Builder mazeBuilder;
    private int level;
    private StatePlaying maze = new StatePlaying();
    private MazePanel mazePanel;
    private String filename;
    private String Logv = "GeneratingActivity";


    /**
     * Creating generating activity page
     * if revisit was clicked in the previous state, load a stored maze
     * if explore was clicked in the previous state, create a new maze
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.generating_activity);
        mazePanel = new MazePanel(getApplicationContext());
        Singleton.setContext(getApplicationContext());
        //extract important maze information from the previous state
        Intent previousIntent = getIntent();
        builder = previousIntent.getStringExtra("builder");
        driver = previousIntent.getStringExtra("driver");
        str_level = previousIntent.getStringExtra("skillLevel");
        Log.v(Logv, "Driver received from AmazeActivity: " + driver);
        Log.v(Logv, "Builder received from AmazeActivity: " + builder);
        Log.v(Logv, "Skill level received from AmazeActivity: " + str_level);
        load = previousIntent.getBooleanExtra("load", false);
        if(builder.equals("DFS")){
            mazeBuilder = Builder.DFS;
        }
        if(builder.equals("Prim")){
            mazeBuilder = Builder.Prim;
        }
        if(builder.equals("Eller")){
            mazeBuilder = Builder.Eller;
        }
        Singleton.setBuilder(mazeBuilder);


        level = Integer.parseInt(str_level);
        Singleton.setLevel(level);

        //set up progress bar for loading
        progressBar = findViewById(R.id.progressBar);
        txt = findViewById(R.id.percent);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        new MyTask().execute(100);


    }

    protected void createNew(){
        mazeFactory.order(this);
    }

    public void loadSaved(){
        String filename = "maze" + str_level;
        MazeFileReader mazeFileReader = new MazeFileReader(filename, Singleton.getContext());
        if(mazeFileReader.exist) {
            Singleton.setMazeConfiguration(mazeFileReader.getMazeConfiguration());
        }
    }



    @Override
    public int getSkillLevel() {
        return level;
    }

    @Override
    public Builder getBuilder() {
        return mazeBuilder;
    }

    @Override
    public boolean isPerfect() {
        return false;
    }

    @Override
    public void deliver(MazeConfiguration mazeConfig) {
        Singleton.setMazeConfiguration(mazeConfig);
        if(this.getSkillLevel() <= 3){
            String filename = "maze" + Integer.toString(this.getSkillLevel());
            writeMazeFile(filename, mazeConfig);
        }
        Play();
    }

    @Override
    public void updateProgress(int percentage) {
        if(progress < percentage && percentage <= 100){
            progress = percentage;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    progressBar.setProgress(progress);
                    txt.setText("Running..." + progress + "%");
                    Log.v(Logv, "Loading: " + progress + "%");
                }
            });
        }
    }




    private void writeMazeFile(String filename, MazeConfiguration mazeConfig) {
        File file = new File(getApplicationContext().getFilesDir(), filename);
        MazeFileWriter mazeFileWriter = new MazeFileWriter();
        mazeFileWriter.store(filename, mazeConfig.getWidth(), mazeConfig.getHeight(), 0, 0, mazeConfig.getRootnode(), mazeConfig.getMazecells(), mazeConfig.getMazedists().getAllDistanceValues(), mazeConfig.getStartingPosition()[0], mazeConfig.getStartingPosition()[1],Singleton.getContext());
    }


    /**
     * Create an async task to simulate the process of creating a maze
     */

    class MyTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            for (; progress <= 100; progress++) {
                try {
                    Thread.sleep(10);
                    Log.v(Logv, "Loading: " + progress + "%");
                    publishProgress(progress);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(isCancelled()){
                    break;
                }
            }

            return "Task Completed.";
        }
        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
            txt.setText("Suspect is running!");
            Log.v(Logv, "Maze has been created");
            Log.v(Logv, "Skill Level: " + str_level);
            Log.v(Logv, "Builder: " + builder);
            Log.v(Logv, "driver: " + driver);
            if(load == true){
                Log.v(Logv, "Load a stored maze.");
                loadSaved();
                Log.v(Logv, "Maze loaded");
                Toast.makeText(getApplicationContext(), "Maze loaded", Toast.LENGTH_SHORT).show();
                Play();
            }

            else{
                Log.v(Logv,"Create a new maze");
                createNew();
                mazeConfiguration = Singleton.getMazeConfiguration();
                maze.setMazeConfiguration(mazeConfiguration);
                Toast.makeText(getApplicationContext(), "Maze created", Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        protected void onPreExecute() {
            System.out.print(load);
            txt.setText("Task starting...");
            progress = 0;

            if(load == true){
                String filename = "maze" + str_level;
                MazeFileReader mazeFileReader = new MazeFileReader(filename, Singleton.getContext());
                if(!mazeFileReader.exist){
                    Log.v(Logv, "No such file");
                    Log.v(Logv, "So create a new maze");
                    txt.setText("Cannot find the file, so create a new maze");
                    //Toast.makeText(getApplicationContext(), "Cannot find the file, so create a new maze", Toast.LENGTH_SHORT).show();
                    load = false;
                }
            }
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
            txt.setText("Running..." + values[0] + "%");

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();

        }
    }

    public void Play(){
        if(driver.equals("Manual")){
            launchPlayManuallyActivity();
        }
        else{
            launchPlayAnimation();
        }
    }

    /**
     * Go to play manually activity state, if the robot driver is manual driver
     */
    private void launchPlayManuallyActivity(){
        Intent intent = new Intent(this, PlayManuallyActivity.class);
        intent.putExtra("driver", driver);
        Log.v(Logv,"Play the game maunually");
        startActivity(intent);
        finish();
    }

    /**
     * Go to play animation activity, if the robot driver is a driver other than manual driver
     */
    private void launchPlayAnimation(){
        Intent intent = new Intent(this, PlayAnimationActivity.class);
        intent.putExtra("driver", driver);
        Log.v(Logv,"Play the game with a automatic robot");
        startActivity(intent);
        finish();
    }

    /**
     * Go to the title page
     */
    private void goBack(){
        Intent intent = new Intent(this, AMazeActivity.class);
        Log.v(Logv, "Go to the title page");
        startActivity(intent);
        finish();
    }

    /**
     * Go to the title page, if back button is pressed
     */
    public void onBackPressed(){
        goBack();
    }

}
