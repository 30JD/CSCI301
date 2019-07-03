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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;
import android.widget.ToggleButton;
import java.util.ArrayList;
import java.util.List;

import cs301.cs.wm.edu.jundaan.R;
import cs301.cs.wm.edu.jundaan.falstad.BasicRobot;
import cs301.cs.wm.edu.jundaan.falstad.Constants;
import cs301.cs.wm.edu.jundaan.falstad.Explorer;
import cs301.cs.wm.edu.jundaan.falstad.ManualDriver;
import cs301.cs.wm.edu.jundaan.falstad.MazePanel;
import cs301.cs.wm.edu.jundaan.falstad.Pledge;
import cs301.cs.wm.edu.jundaan.falstad.Robot;
import cs301.cs.wm.edu.jundaan.falstad.RobotDriver;
import cs301.cs.wm.edu.jundaan.falstad.StatePlaying;
import cs301.cs.wm.edu.jundaan.falstad.WallFollower;
import cs301.cs.wm.edu.jundaan.falstad.Wizard;
import cs301.cs.wm.edu.jundaan.generation.CardinalDirection;
import cs301.cs.wm.edu.jundaan.generation.Cells;
import cs301.cs.wm.edu.jundaan.generation.MazeConfiguration;
import cs301.cs.wm.edu.jundaan.generation.Music;
import cs301.cs.wm.edu.jundaan.generation.Singleton;
import cs301.cs.wm.edu.jundaan.generation.SingleRandom;


public class PlayAnimationActivity extends AppCompatActivity{
    private Button win;
    private Button lost;
    private ProgressBar energyBar;
    private ToggleButton sol;
    private ToggleButton map;
    private ToggleButton wall;
    private Button in;
    private Button out;
    private ToggleButton start;
    private MazePanel mazePanel;
    private BasicRobot robot;
    private RobotDriver robotDriver;
    private MazeConfiguration mazeConfiguration;
    private StatePlaying maze;
    private Handler handler = new Handler();
    private ToggleButton musicPlay;
    private Music music;
    private boolean musicPlaying = true;


    private int distanceToLeft;
    private int distanceToFront;
    private int counter;
    private int[][] visits;
    private Cells cells;
    private List<List<int[]>> mainList = new ArrayList<List<int[]>>();
    private List<int[]> doorList = new ArrayList<int[]>();
    private SingleRandom random = SingleRandom.getRandom();


    private String Logv = "PlayAnimationActivity";
    private String driver;
    private int shortestPath;
    private int pathLength;
    private int energyConsumption;

    /**
     * Create play animation activity page
     * GO to winning page, if the robot gets out of the maze successfully
     * Otherwise, go to losing page
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_animation_activity);

        //extract driver information from the previous state
        Intent previousIntent = getIntent();
        driver = previousIntent.getStringExtra("driver");

        //initialize energy bar
        energyBar = findViewById(R.id.energyBar);
        energyBar.setMax(3000);
        energyBar.setProgress(3000);

        //set up go2winning and go2losing buttons
        mazePanel = findViewById(R.id.mazePanel);
        mazeConfiguration = Singleton.getMazeConfiguration();
        driver = previousIntent.getStringExtra("driver");
        robot = new BasicRobot();
        switch(driver){
            case "Wizard":
                robotDriver = new Wizard();
                break;

            case "WallFollower":
                robotDriver = new WallFollower();
                break;

            case "Pledge":
                robotDriver = new Pledge();
                break;

            case "Explorer":
                robotDriver = new Explorer();
                break;
        }
        maze = new StatePlaying();
        robot.setMaze(maze);
        robotDriver.setRobot(robot);
        maze.setRobotAndDriver(robot, robotDriver);
        maze.setMazeConfiguration(mazeConfiguration);
        maze.setPlayAnimationActivity(this);
        Singleton.setPlayAnimationActivity(this);
        maze.start(mazePanel);

        music = new Music();
        music.bgmMusic(this);
        Singleton.setContext(getApplicationContext());
        Singleton.setPlayer(music);
        Singleton.getPlayer().playMusic();
        musicPlaying = true;
        initVisits();


        //set up solution, map, and wall toggle buttons
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

        //set up toggle button for start and pause
        start = findViewById(R.id.sandp);
        start.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Log.v(Logv, "Start the animation");
                    handler.post(robotRun);
                    Toast.makeText(getApplicationContext(), "Start the animation", Toast.LENGTH_LONG).show();
                }
                else{
                    Log.v(Logv, "Pause the animation");
                    handler.removeCallbacks(robotRun);
                    Toast.makeText(getApplicationContext(), "Pause the animation", Toast.LENGTH_LONG).show();
                }
            }
        });

        musicPlay = findViewById(R.id.musicplay2);
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

    private Runnable robotRun = new Runnable(){

        @Override
        public void run() {
            if(driver.equals("Wizard")){
                int[] neighborPosition;
                if(robot.getBatteryLevel() >= 3){
                    try{
                        neighborPosition = maze.getMazeConfiguration().getNeighborCloserToExit(robot.getCurrentPosition()[0], robot.getCurrentPosition()[1]);
                        CardinalDirection neighborDirection = CardinalDirection.getDirection(neighborPosition[0] - robot.getCurrentPosition()[0], neighborPosition[1] - robot.getCurrentPosition()[1]);
                        rotate(robot.getCurrentDirection(), neighborDirection);
                        if(robot.getBatteryLevel() >= 5){
                            robot.move(1, true);
                            updateEnergyBar();
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            else if(driver.equals("Pledge")){
                if (counter == 0) {
                    if (robot.distanceToObstacle(Robot.Direction.FORWARD) == 0) {
                        robot.rotate(Robot.Turn.LEFT);
                        updateEnergyBar();
                        counter -= 1;
                    } else {

                        robot.move(1, true);
                        updateEnergyBar();
                    }
                } else if (counter < 0) {
                    if (robot.distanceToObstacle(Robot.Direction.RIGHT) != 0) {
                        robot.rotate(Robot.Turn.RIGHT);
                        updateEnergyBar();
                        counter += 1;
                        robot.move(1, true);
                        updateEnergyBar();
                    } else if (robot.distanceToObstacle(Robot.Direction.FORWARD) != 0) {
                        robot.move(1, true);
                        updateEnergyBar();
                    } else {
                        robot.rotate(Robot.Turn.LEFT);
                        updateEnergyBar();
                        counter -= 1;
                    }
                } else if (counter > 0) {
                    if (robot.distanceToObstacle(Robot.Direction.LEFT) != 0) {
                        robot.rotate(Robot.Turn.LEFT);
                        updateEnergyBar();
                        counter -= 1;
                        robot.move(1, true);
                        updateEnergyBar();
                    } else if (robot.distanceToObstacle(Robot.Direction.FORWARD) != 0) {
                        robot.move(1, true);
                        updateEnergyBar();
                    } else {
                        robot.rotate(Robot.Turn.RIGHT);
                        updateEnergyBar();
                        counter += 1;
                    }
                }
            }

            else if(driver.equals("Explorer")){
                try {
                    visits[robot.getCurrentPosition()[0]][robot.getCurrentPosition()[1]]++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(!robot.isInsideRoom()) {
                    try {
                        rotateToNextMove(robot.getCurrentPosition(),robot.getCurrentDirection());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else {
                    //find the position of the four corners of the room
                    int xRight = 0;
                    try {
                        xRight = findBorderOfRoom1(robot.getCurrentPosition()[0], robot.getCurrentPosition()[1]);
                        int xLeft = findBorderOfRoom2(robot.getCurrentPosition()[0], robot.getCurrentPosition()[1]);
                        int yUp = findBorderOfRoom3(robot.getCurrentPosition()[0], robot.getCurrentPosition()[1]);
                        int yDown = findBorderOfRoom4(robot.getCurrentPosition()[0], robot.getCurrentPosition()[1]);
                        //add all the doors in the south/north side to the doorList
                        for (int a = xLeft; a <= xRight; a ++) {
                            if (cells.hasNoWall(a, yDown, CardinalDirection.North)) {
                                int[] door = {a, yDown, 0};
                                if (checkIfInList(mainList, door)) {
                                    doorList.add(door);
                                }
                                else {
                                    doorList = mainList.get(indexOfDoorList(mainList, door));
                                }
                            }
                            if (cells.hasNoWall(a, yUp, CardinalDirection.South)) {
                                int[] door = {a, yUp, 0};
                                if (checkIfInList(mainList, door)) {
                                    doorList.add(door);
                                }
                                else {
                                    doorList = mainList.get(indexOfDoorList(mainList, door));
                                }
                            }
                        }
                        //add all the door on the east/west side to the doorList
                        for (int b = yDown; b <= yUp; b ++) {
                            if (cells.hasNoWall(xLeft, b, CardinalDirection.West)) {
                                int[] door = {xLeft, b, 0};
                                if (checkIfInList(mainList, door)) {
                                    doorList.add(door);
                                }
                                else {
                                    doorList = mainList.get(indexOfDoorList(mainList, door));
                                }
                            }
                            if (cells.hasNoWall(xRight, b, CardinalDirection.East)) {
                                int[] door = {xRight, b, 0};
                                if (checkIfInList(mainList, door)) {
                                    doorList.add(door);
                                }
                                else {
                                    doorList = mainList.get(indexOfDoorList(mainList, door));
                                }
                            }
                        }
                        if (addDoorListToMainList(mainList, doorList)) {
                            mainList.add(doorList);
                        }

                        //add 1 to the door where it went through in
                        for (int j = 0; j < doorList.size(); j ++) {
                            if (doorList.get(j)[0] == robot.getCurrentPosition()[0] && doorList.get(j)[1] == robot.getCurrentPosition()[1]) {
                                doorList.get(j)[2] += 1;
                                break;
                            }
                        }
                        //select the door to escape room
                        int min = doorList.get(0)[2];

                        for (int m = 0; m < doorList.size(); m++) {
                            if (doorList.get(m)[2] < min){
                                min = doorList.get(m)[2];
                            }
                        }
                        List<int[]> doorUsageList = new ArrayList<int[]>(doorList);
                        for (int n = 0; n < doorUsageList.size(); n ++) {
                            if (doorUsageList.get(n)[2] > min) {
                                doorUsageList.remove(n);
                                n -= 1;
                            }
                        }

                        int r = random.nextIntWithinInterval(0, doorUsageList.size() - 1);
                        int q = doorList.indexOf(doorUsageList.get(r));

                        //move robot to the selected door
                        int doorx = doorList.get(q)[0];
                        int doory = doorList.get(q)[1];
                        int initx = robot.getCurrentPosition()[0];
                        int inity = robot.getCurrentPosition()[1];

                        //add 1 to the visit where it went out
                        visits[doorx][doory] += 1;
                        if (initx > doorx) {
                            rotate(robot.getCurrentDirection(), CardinalDirection.West);
                            robot.move(initx - doorx, true);
                            updateEnergyBar();
                            if (inity > doory) {
                                rotate(robot.getCurrentDirection(), CardinalDirection.North);
                                robot.move(inity - doory, true);
                                updateEnergyBar();
                            }
                            if (doory > inity) {
                                rotate(robot.getCurrentDirection(), CardinalDirection.South);
                                robot.move(doory - inity, true);
                                updateEnergyBar();
                            }
                        }
                        else if (initx < doorx) {
                            rotate(robot.getCurrentDirection(), CardinalDirection.East);
                            robot.move(doorx - initx, true);
                            updateEnergyBar();
                            if (inity > doory) {
                                rotate(robot.getCurrentDirection(), CardinalDirection.North);
                                robot.move(inity - doory, true);
                                updateEnergyBar();
                            }
                            if (doory > inity) {
                                rotate(robot.getCurrentDirection(), CardinalDirection.South);
                                robot.move(doory - inity, true);
                                updateEnergyBar();
                            }
                        }
                        else {
                            if (inity > doory) {
                                rotate(robot.getCurrentDirection(), CardinalDirection.North);
                                robot.move(inity - doory, true);
                                updateEnergyBar();
                            }
                            else if (doory > inity) {
                                rotate(robot.getCurrentDirection(), CardinalDirection.South);
                                robot.move(doory - inity, true);
                                updateEnergyBar();
                            }
                        }
                        for (CardinalDirection cdd : CardinalDirection.values()) {
                            if (maze.getMazeConfiguration().getMazecells().hasNoWall(robot.getCurrentPosition()[0], robot.getCurrentPosition()[1], cdd) && !maze.getMazeConfiguration().getMazecells().isInRoom(robot.getCurrentPosition()[0] + cdd.getDirection()[0], robot.getCurrentPosition()[1] + cdd.getDirection()[1])){
                                if (robot.getBatteryLevel() >= 3) {
                                    rotate(robot.getCurrentDirection(), cdd);
                                    updateEnergyBar();
                                }
                                else {
                                    throw new Exception();
                                }
                                if (robot.getBatteryLevel() >= 5) {
                                    for (int k = 0; k < doorList.size(); k ++) {
                                        if (doorList.get(k)[0] == robot.getCurrentPosition()[0] && doorList.get(k)[1] == robot.getCurrentPosition()[1]) {
                                            doorList.get(k)[2] += 1;
                                            break;
                                        }
                                    }
                                    //walk out of the room
                                    robot.move(1, true);
                                    updateEnergyBar();
                                    visits[robot.getCurrentPosition()[0]][robot.getCurrentPosition()[1]] += 1;



                                    break;
                                }
                                else {
                                    throw new Exception();
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            }

            else if(driver.equals("WallFollower")){
                if(robot.getBatteryLevel()>=1) {
                    distanceToLeft = robot.distanceToObstacle(Robot.Direction.LEFT);
                }

                if(robot.getBatteryLevel()>=1) {
                    distanceToFront = robot.distanceToObstacle(Robot.Direction.FORWARD);
                }

                if (robot.getOdometerReading() == 0 && distanceToLeft != 0){
                    robot.rotate(Robot.Turn.LEFT);
                    updateEnergyBar();
                    robot.move(robot.distanceToObstacle(Robot.Direction.FORWARD),true);
                    updateEnergyBar();
                    robot.rotate(Robot.Turn.RIGHT);
                    updateEnergyBar();
                }

                else if(distanceToLeft == 0) {
                    if(distanceToFront == 0) {
                        if(robot.getBatteryLevel()>=3) {
                            robot.rotate(Robot.Turn.RIGHT);
                            updateEnergyBar();
                        }
                    }
                    else {
                        if(robot.getBatteryLevel()>=5) {
                            robot.move(1, true);
                            updateEnergyBar();
                        }
                    }
                }

                //if distance to left is not zero, then turn left and follow the wall
                else {
                    if(robot.getBatteryLevel()>=3) {
                        robot.rotate(Robot.Turn.LEFT);
                        updateEnergyBar();
                    }

                    if(robot.getBatteryLevel()>=5) {
                        robot.move(1, true);
                        updateEnergyBar();
                    }
                }
            }

            if(robot.isAtExit()) {
                handler.removeCallbacks(robotRun);
                if(robot.canSeeExit(Robot.Direction.LEFT)) {
                    robot.rotate(Robot.Turn.LEFT);
                    updateEnergyBar();
                    robot.move(1, false);
                    updateEnergyBar();
                }

                else if(robot.canSeeExit(Robot.Direction.RIGHT)) {
                    robot.rotate(Robot.Turn.RIGHT);
                    updateEnergyBar();
                    robot.move(1, false);
                    updateEnergyBar();
                }

                else {
                    robot.move(1, false);
                    updateEnergyBar();
                    //checkStopped();
                }
                handler.removeCallbacks(robotRun);
            }
            handler.postDelayed(robotRun, 100);
        }
    };



    private void rotate(CardinalDirection current, CardinalDirection next){
        if(current == CardinalDirection.North) {
            if(next == CardinalDirection.North) {
                return;
            }
            if(next == CardinalDirection.South) {
                robot.rotate(Robot.Turn.AROUND);
            }
            if(next == CardinalDirection.West) {
                robot.rotate(Robot.Turn.RIGHT);
            }
            if(next == CardinalDirection.East) {
                robot.rotate(Robot.Turn.LEFT);
            }
        }
        if(current == CardinalDirection.South) {
            if(next == CardinalDirection.North) {
                robot.rotate(Robot.Turn.AROUND);
            }
            if(next == CardinalDirection.South) {
                return;
            }
            if(next == CardinalDirection.West) {
                robot.rotate(Robot.Turn.LEFT);
            }
            if(next == CardinalDirection.East) {
                robot.rotate(Robot.Turn.RIGHT);
            }
        }
        if(current == CardinalDirection.West) {
            if(next == CardinalDirection.North) {
                robot.rotate(Robot.Turn.LEFT);
            }
            if(next == CardinalDirection.South) {
                robot.rotate(Robot.Turn.RIGHT);
            }
            if(next == CardinalDirection.West) {
                return;
            }
            if(next == CardinalDirection.East) {
                robot.rotate(Robot.Turn.AROUND);
            }
        }
        if(current == CardinalDirection.East) {
            if(next == CardinalDirection.North) {
                robot.rotate(Robot.Turn.RIGHT);
            }
            if(next == CardinalDirection.South) {
                robot.rotate(Robot.Turn.LEFT);
            }
            if(next == CardinalDirection.West) {
                robot.rotate(Robot.Turn.AROUND);
            }
            if(next == CardinalDirection.East) {
                return;
            }
        }
    }

    private void rotateToNextMove(int[] currentPosition, CardinalDirection currentDirection) throws Exception {
        int minVisit = Integer.MAX_VALUE;
        ArrayList<Robot.Direction> directionList = new ArrayList<Robot.Direction>();
        int canGo = 0;
        //put the cells that cango into the list
        for(Robot.Direction d: Robot.Direction.values()) {
            if (robot.distanceToObstacle(d) != 0) {
                directionList.add(d);
                canGo += 1;
                if(visits[directionToCellValue(d,currentDirection,currentPosition)[0]][directionToCellValue(d,currentDirection,currentPosition)[1]]<minVisit) {
                    minVisit = visits[directionToCellValue(d,currentDirection,currentPosition)[0]][directionToCellValue(d,currentDirection,currentPosition)[1]];
                }
            }
        }
        //extract the minimum visited go-able cells to be candidates
        int candidates = 0;
        ArrayList<Robot.Direction> directionCandidate = new ArrayList<Robot.Direction>();
        for(Robot.Direction d: directionList) {
            if(visits[directionToCellValue(d,currentDirection,currentPosition)[0]][directionToCellValue(d,currentDirection,currentPosition)[1]]==minVisit) {
                directionCandidate.add(d);
                candidates += 1;
            }
        }
        //rotate to that direction
        Robot.Direction direction = directionCandidate.get(random.nextIntWithinInterval(0, candidates-1));
        if(direction == Robot.Direction.BACKWARD) {
            robot.rotate(Robot.Turn.AROUND);
        }
        else if(direction == Robot.Direction.LEFT) {
            robot.rotate(Robot.Turn.LEFT);
        }
        else if(direction == Robot.Direction.RIGHT) {
            robot.rotate(Robot.Turn.RIGHT);
        }

        //move to that cell
        robot.move(1, true);

        //increase the number of times that the robot passed that cell
        if(canGo == 1) {
            visits[currentPosition[0]][currentPosition[1]]++;
        }
        visits[robot.getCurrentPosition()[0]][robot.getCurrentPosition()[1]]++;

    }

    /**
     * Increase the number of times that the cell is passed.
     * @param direction
     * @param currentDirection
     * @param currentPosition
     * @return
     */
    private int[] directionToCellValue(Robot.Direction direction, CardinalDirection currentDirection, int[] currentPosition) {
        if(currentDirection == CardinalDirection.East) {
            if(direction == Robot.Direction.FORWARD) {
                int[] position = {currentPosition[0]+1, currentPosition[1]};
                return position;
            }
            else if(direction == Robot.Direction.BACKWARD) {
                int[] position = {currentPosition[0]-1, currentPosition[1]};
                return position;
            }
            else if(direction == Robot.Direction.LEFT) {
                int[] position = {currentPosition[0], currentPosition[1]+1};
                return position;
            }
            else {
                int[] position = {currentPosition[0], currentPosition[1]-1};
                return position;
            }
        }
        else if(currentDirection == CardinalDirection.West) {
            if(direction == Robot.Direction.FORWARD) {
                int[] position = {currentPosition[0]-1, currentPosition[1]};
                return position;
            }
            else if(direction == Robot.Direction.BACKWARD) {
                int[] position = {currentPosition[0]+1, currentPosition[1]};
                return position;
            }
            else if(direction == Robot.Direction.LEFT) {
                int[] position = {currentPosition[0], currentPosition[1]-1};
                return position;
            }
            else {
                int[] position = {currentPosition[0], currentPosition[1]+1};
                return position;
            }
        }
        else if(currentDirection == CardinalDirection.North) {
            if(direction == Robot.Direction.FORWARD) {
                int[] position = {currentPosition[0], currentPosition[1]-1};
                return position;
            }
            else if(direction == Robot.Direction.BACKWARD) {
                int[] position = {currentPosition[0], currentPosition[1]+1};
                return position;
            }
            else if(direction == Robot.Direction.LEFT) {
                int[] position = {currentPosition[0]+1, currentPosition[1]};
                return position;
            }
            else {
                int[] position = {currentPosition[0]-1, currentPosition[1]};
                return position;
            }
        }
        else {
            if(direction == Robot.Direction.FORWARD) {
                int[] position = {currentPosition[0], currentPosition[1]+1};
                return position;
            }
            else if(direction == Robot.Direction.BACKWARD) {
                int[] position = {currentPosition[0], currentPosition[1]-1};
                return position;
            }
            else if(direction == Robot.Direction.LEFT) {
                int[] position = {currentPosition[0]-1, currentPosition[1]};
                return position;
            }
            else {
                int[] position = {currentPosition[0]+1, currentPosition[1]};
                return position;
            }
        }
    }


    public int findBorderOfRoom1(int x, int y) {
        for (int i = x; i < 401; i ++) {
            if (!cells.isInRoom(i, y)) {
                return i - 1;
            }
        }
        return -1;
    }
    public int findBorderOfRoom2(int x, int y) {
        for (int i = x; i >= 0; i --) {
            if (!cells.isInRoom(i, y)) {
                return i + 1;
            }
        }
        return -1;
    }
    public int findBorderOfRoom3(int x, int y) {
        for (int i = y; i < 401; i ++) {
            if (!cells.isInRoom(x, i)) {
                return i - 1;
            }
        }
        return -1;
    }
    public int findBorderOfRoom4(int x, int y) {
        for (int i = y; i >= 0; i --) {
            if (!cells.isInRoom(x, i)) {
                return i + 1;
            }
        }
        return -1;
    }

    public boolean checkIfInList(List<List<int[]>> mainList, int[] door) {
        for (int i = 0; i < mainList.size(); i ++) {
            for (int j = 0; j < mainList.get(i).size(); j ++) {
                if (mainList.get(i).get(j)[0] == door[0] && mainList.get(i).get(j)[1] == door[1]) {
                    return false;
                }
            }
        }
        return true;
    }

    public int indexOfDoorList(List<List<int[]>> mainList, int[] door) {
        for (int i = 0; i < mainList.size(); i ++) {
            for (int j = 0; j < mainList.get(i).size(); j ++) {
                if (mainList.get(i).get(j)[0] == door[0] && mainList.get(i).get(j)[1] == door[1]) {
                    return i;
                }
            }
        }
        return -1;
    }

    public boolean addDoorListToMainList(List<List<int[]>> mainList, List<int[]> doorList) {
        for (int i = 0; i < mainList.size(); i ++) {
            for (int j = 0; j < mainList.get(i).size() && j < doorList.size(); j ++) {
                if (mainList.get(i).get(j).equals(doorList.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }

    private void initVisits() {
        visits = new int[maze.getMazeConfiguration().getWidth()][maze.getMazeConfiguration().getHeight()];
        for (int i = 0; i < maze.getMazeConfiguration().getWidth(); i++) {
            for (int j = 0; j < maze.getMazeConfiguration().getHeight(); j++) {
                visits[i][j] = 0;
            }

        }
    }

    /**
     * Go to title state
     */
    private void goBack(){
        Intent intent = new Intent(this, AMazeActivity.class);
        Log.v(Logv, "Go to the title page");
        startActivity(intent);
        finish();
    }

    /**
     * Go to winning state and store the important maze information
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
        if(musicPlaying == true) {
            Log.v(Logv, "Music Paused");
            Singleton.getPlayer().pauseMusic();
        }
        startActivity(intent);
        finish();
    }

    /**
     * Go to losing state and store the important maze information
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
        if(musicPlaying == true) {
            Log.v(Logv, "Music Paused");
            Singleton.getPlayer().pauseMusic();
        }
        Log.v(Logv, "You failed and the robot vibrated");
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(800);
        startActivity(intent);
        finish();
    }

    /**
     * update the energy bar
     */
    private void updateEnergyBar(){
        energyConsumption = 3000 - robot.getBatteryLevel();
        energyBar.setProgress(3000 - energyConsumption);
    }

    /**
     * if back button is clicked, go back to title state
     */
    public void onBackPressed(){
        if(musicPlaying == true) {
            Log.v(Logv, "Music Paused");
            Singleton.getPlayer().pauseMusic();
        }
        handler.removeCallbacks(robotRun);
        goBack();
    }

}


