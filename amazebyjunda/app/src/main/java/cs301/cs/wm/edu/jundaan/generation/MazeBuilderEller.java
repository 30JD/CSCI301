package cs301.cs.wm.edu.jundaan.generation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import cs301.cs.wm.edu.jundaan.generation.Cells;


public class MazeBuilderEller extends MazeBuilder implements Runnable{

	public MazeBuilderEller() {
		super();
		System.out.println("MazeBuilderPrim uses Eller's algorithm to generate maze.");
	}

	public MazeBuilderEller(boolean det) {
		super(det);
		System.out.println("MazeBuilderPrim uses Eller's algorithm to generate maze.");
	}

	private int[][] maze;

	@Override
	protected void generatePathways() {
		cells.initialize();
		maze = new int[width][height];
		for (int i = 0; i < width; i++) maze[i][0] = i + 1; 
		mergeCells(0);
		for (int i = 1; i < height - 1; i++) { // iterate over rows
			verticalExtend(i);
			mergeCells(i);
		}
		verticalExtend(height - 1); // last row
		mergeLastRow();
	}


	/**
	 * randomly merge adjacent sets of a given row
	 */
	private void mergeCells(int row) {
		int rannum = random.nextIntWithinInterval(0, 1);
		for(int c = 0; c < width - 1; c++) {
			Wall wall1 = new Wall(c, row, CardinalDirection.East);
			Wall wall2 = new Wall(c, row, CardinalDirection.South);
			if(rannum == 1 || cells.canGo(wall1) && cells.isInRoom(c+1, row)) {
				breakHorizontalWall(c, row, 1);
			}
			if(c != 0 && !cells.canGo(wall2)) {
				breakHorizontalWall(c, row, -1);
				breakHorizontalWall(c, row, 1);
			}
		}
	}

	/**
	 * delete the wall between a cell and the cell to its right or left, also updates the sets
	 */
	private void breakHorizontalWall(int x, int y, int d) {
		// don't break walls between cells in the same set
		if (maze[x][y] == maze[x + d][y]) return;

		int oldSet, newSet;
		if (maze[x][y] < maze[x + d][y]) {
			oldSet  =  maze[x + d][y];
			newSet = maze[x][y];
			maze[x + d][y] = maze[x][y];
		}

		else {
			oldSet = maze[x][y];
			newSet = maze[x + d][y];
			maze[x][y] = maze[x + d][y];
		}

		// update all other elements in that set on that row
		for (int i = 0; i < width; i++) {
			if (maze[i][y] == oldSet) maze[i][y] = newSet;
		}

		if(d == -1) {
			Wall wall = new Wall(x, y, CardinalDirection.West);
			cells.deleteWall(wall);
		}

		if(d == 1) {
			Wall wall = new Wall(x, y, CardinalDirection.East);
			cells.deleteWall(wall);
		}
	}


	private void verticalExtend(int row) {
		int uprow = row - 1;
		int[] up = new int[width];

		for (int i = 0; i < width; i++) {
			up[i] = maze[i][uprow];
		}

		// create a hash map
		HashMap<Integer, List<Integer>> hmap = new HashMap<Integer, List<Integer>>();
		for (int i = 0; i < up.length; i++) {
			Wall wall = new Wall(i, uprow, CardinalDirection.South);
			if (hmap.get(up[i]) == null) {
				hmap.put(up[i], new ArrayList<Integer>());
				}
			if (cells.canGo(wall)) {
				hmap.get(up[i]).add(i);
				}
		}


		Random random = new Random();
		Collection<Integer> keys = hmap.keySet();
		List<Integer> toRemove = new ArrayList<Integer>();
		for (Object key: keys) {
			List<Integer> rows = hmap.get(key);
			Collections.shuffle(rows);
			int take = random.nextInt(rows.size()) + 1;
			toRemove.addAll(hmap.get(key).subList(0, take));
			for (int i = 0; i < rows.size(); i++) {
				if (cells.isInRoom(rows.get(i), row) && !toRemove.contains(rows.get(i))) {
					toRemove.add(rows.get(i));
					}
			}
		}

		// delete walls in our `toRemove` list and combine those sets
		for (int i = 0; i < toRemove.size(); i++) {
			Wall wall = new Wall(toRemove.get(i), uprow, CardinalDirection.South);
			cells.deleteWall(wall);
			maze[toRemove.get(i)][row] = maze[toRemove.get(i)][uprow];
		}

		// assign the empty spots to new sets
		int setMax = max(row);
		for (int i = 0; i < width; i++)
			if (maze[i][row] == 0) {
				if (!cells.hasWall(i, row, CardinalDirection.West) && maze[i - 1][row] != 0) {
					maze[i][row] = maze[i - 1][row];
					}
				else {
					maze[i][row] = ++setMax;
					}
			}
	}

	/**
	 * Combine adjacent, disjoint sets on the last row of the maze
	 */
	private void mergeLastRow() {
		int row = height - 1;
		for(int col = 0; col < width-1; col++) {
			Wall wall = new Wall(col, row, CardinalDirection.East);
			if(maze[col][row] != maze[col+1][row] && cells.canGo(wall)) {
				cells.deleteWall(wall);
				maze[col+1][row] = maze[col][row];
			}
		}
	}


	/**
	 * Returns the largest set number for a given row
	 */
	private int max(int row) {
		int max = maze[0][row];
		for(int i = 0; i < width; i++) {
			if(maze[i][row] > max) {
				max = maze[i][row];
			}
		}
		return max;
	}


}
			