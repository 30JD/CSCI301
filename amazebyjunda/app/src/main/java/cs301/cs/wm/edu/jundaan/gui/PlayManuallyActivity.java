package cs301.cs.wm.edu.jundaan.gui;

import android.content.Context;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
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
import android.widget.ToggleButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import cs301.cs.wm.edu.jundaan.R;
import cs301.cs.wm.edu.jundaan.falstad.BasicRobot;
import cs301.cs.wm.edu.jundaan.falstad.Constants;
import cs301.cs.wm.edu.jundaan.falstad.ManualDriver;
import cs301.cs.wm.edu.jundaan.falstad.MazePanel;
import cs301.cs.wm.edu.jundaan.falstad.Robot;
import cs301.cs.wm.edu.jundaan.falstad.RobotDriver;
import cs301.cs.wm.edu.jundaan.falstad.StatePlaying;
import cs301.cs.wm.edu.jundaan.generation.MazeConfiguration;
import cs301.cs.wm.edu.jundaan.generation.Singleton;


public class PlayManuallyActivity extends AppCompatActivity{
    private MazePanel mazePanel;
    private Button win;
    private Button lost;
    private ToggleButton sol;
    private ToggleButton map;
    private ToggleButton wall;
    private Button forward;
    private Button left;
    private Button right;
    private Button in;
    private Button out;
    private StatePlaying maze;
    private BasicRobot robot;
    private RobotDriver robotDriver;
    private MazeConfiguration mazeConfiguration;

    private String Logv = "PlayManuallyActivity";
    private String driver;
    private int shortestPath;
    private int pathLength;
    private int energyConsumption;
    private ProgressBar energyBar;

    /**
     * Create play manually page
     * GO to winning page, if the robot gets out of the maze successfully
     * Otherwise, go to losing page
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_manually_activity);
        mazePanel = findViewById(R.id.mazePanel);
        //extract driver information from the previous activity
        Intent previousIntent = getIntent();
        mazeConfiguration = Singleton.getMazeConfiguration();
        driver = previousIntent.getStringExtra("driver");
        robot = new BasicRobot();
        robotDriver = new ManualDriver();
        maze = new StatePlaying();
        robot.setMaze(maze);
        robotDriver.setRobot(robot);
        maze.setRobotAndDriver(robot, robotDriver);
        maze.setMazeConfiguration(mazeConfiguration);
        maze.setPlayManuallyActivity(this);

        System.out.print("xxhhy");
        maze.start(mazePanel);

        System.out.print("xxhh" + maze.getMazeConfiguration().getStartingPosition()[0] + maze.getMazeConfiguration().getStartingPosition()[1]);

        energyConsumption = 3000 - robot.getBatteryLevel();
        //set up solution, map, wall toggle buttons
        sol = findViewById(R.id.toggleButtonsol);
        sol.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Log.v(Logv, "Show the solution");
                    maze.keyDown(Constants.UserInput.ToggleSolution, 1);
                    Toast.makeText(getApplicationContext(), "Solution Button on", Toast.LENGTH_LONG).show();
                }
                else{
                    Log.v(Logv, "Hide the solution");
                    maze.keyDown(Constants.UserInput.ToggleSolution, 1);
                    Toast.makeText(getApplicationContext(), "Solution Button off", Toast.LENGTH_LONG).show();
                }
            }
        });

        map = findViewById(R.id.toggleButtonmap);
        map.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Log.v(Logv, "Show the map");
                    maze.keyDown(Constants.UserInput.ToggleLocalMap, 1);
                    Toast.makeText(getApplicationContext(), "Map Button on", Toast.LENGTH_LONG).show();
                }
                else{
                    Log.v(Logv, "Hide the map");
                    maze.keyDown(Constants.UserInput.ToggleLocalMap, 1);
                    Toast.makeText(getApplicationContext(), "Map Button off", Toast.LENGTH_LONG).show();
                }
            }
        });

        wall = findViewById(R.id.toggleButtonwall);
        wall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Log.v(Logv, "Show walls");
                    maze.keyDown(Constants.UserInput.ToggleFullMap, 1);
                    Toast.makeText(getApplicationContext(), "Wall Button on", Toast.LENGTH_LONG).show();
                }
                else{
                    Log.v(Logv, "Hide walls");
                    maze.keyDown(Constants.UserInput.ToggleFullMap, 1);
                    Toast.makeText(getApplicationContext(), "Wall Button off", Toast.LENGTH_LONG).show();
                }
            }
        });

        //set up direction buttons
        forward = findViewById(R.id.forward);
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(Logv, "Move Forward");
                robot.move(1, true);
                Toast.makeText(getApplicationContext(), "Move Forward", Toast.LENGTH_LONG).show();
            }
        });


        left = findViewById(R.id.left);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(Logv, "Turn Left");
                robot.rotate(Robot.Turn.LEFT);
                Toast.makeText(getApplicationContext(), "Turn Left", Toast.LENGTH_LONG).show();
            }
        });

        right = findViewById(R.id.right);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(Logv, "Turn Right");
                robot.rotate(Robot.Turn.RIGHT);
                Toast.makeText(getApplicationContext(), "Turn Right", Toast.LENGTH_LONG).show();
            }
        });

        //set up zoom_in and zoom_out buttons
        in = findViewById(R.id.zoom_in);
        in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(Logv, "Zoom In");
                maze.keyDown(Constants.UserInput.ZoomIn, 1);
                Toast.makeText(getApplicationContext(), "Zoom In", Toast.LENGTH_LONG).show();
            }
        });

        out = findViewById(R.id.zoom_out);
        out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(Logv, "Zoom Out");
                maze.keyDown(Constants.UserInput.ZoomOut, 1);
                Toast.makeText(getApplicationContext(), "Zoom Out", Toast.LENGTH_LONG).show();
            }
        });
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
     * go to winning state
     */
    public void go2Winning(){
        Intent intent = new Intent(this, WinningActivity.class);
        Log.v(Logv, "Go to winning state");
        energyConsumption = 3000 - robot.getBatteryLevel();
        Singleton.setEnergyCon(energyConsumption);
        shortestPath = maze.getMazeConfiguration().getDistanceToExit(maze.getMazeConfiguration().getMazedists().getStartPosition()[0], maze.getMazeConfiguration().getMazedists().getStartPosition()[1]);
        pathLength = robot.getOdometerReading();
        intent.putExtra("energy consumption", energyConsumption);
        intent.putExtra("driver", driver);
        intent.putExtra("shortest path", shortestPath);
        intent.putExtra("path length", pathLength);
        startActivity(intent);
        finish();
    }

    /**
     * go to losing state
     */
    public void go2Losing(){
        Intent intent = new Intent(this, LosingActivity.class);
        Log.v(Logv, "Go to losing state");
        energyConsumption = 3000 - robot.getBatteryLevel();
        Singleton.setEnergyCon(energyConsumption);
        shortestPath = maze.getMazeConfiguration().getDistanceToExit(maze.getMazeConfiguration().getMazedists().getStartPosition()[0], maze.getMazeConfiguration().getMazedists().getStartPosition()[1]);
        pathLength = robot.getOdometerReading();
        intent.putExtra("energy consumption", energyConsumption);
        intent.putExtra("driver", driver);
        intent.putExtra("shortest path", shortestPath);
        intent.putExtra("path length", pathLength);
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        Log.v(Logv, "You failed and the robot vibrated");
        vibrator.vibrate(800);
        startActivity(intent);
        finish();
    }

    /**
     * if back button is clicked, go back to title state
     */
    public void onBackPressed(){
        goBack();
    }
}


