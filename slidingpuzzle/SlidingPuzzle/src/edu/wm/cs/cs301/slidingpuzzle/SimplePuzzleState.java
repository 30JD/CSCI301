package edu.wm.cs.cs301.slidingpuzzle;

import java.util.Arrays;
import java.util.Random;

public class SimplePuzzleState implements PuzzleState {

    private int [][] Board;
    private PuzzleState ParentState;
    private Operation Operation;
    private int PathLength;
    
	public SimplePuzzleState() {
		super();
		this.Board = null;
		this.ParentState = null;
		this.Operation = null;
		this.PathLength = 0;
	}
	
	public SimplePuzzleState(int[][] Board, PuzzleState ParentState, Operation Operation, int PathLength ) {
		this.Board = Board;
		this.ParentState = ParentState;
		this.Operation = Operation;
		this.PathLength = PathLength;
	}
    
	@Override


	public void setToInitialState(int dimension, int numberOfEmptySlots) {
	    int column = dimension;
	    int row = dimension;		
		this.Board = new int[dimension][dimension];
		//construct the board
		int value = 0;
	    for (int i = 0; i < row; i++) {
	    	for (int j = 0; j < column; j++) {
	    		value ++;
	    		if (value > dimension * dimension - numberOfEmptySlots) {
	    			this.Board[i][j] = 0;
	    		}
	    		else {
	    			this.Board[i][j] = value;
	    		}
	    		ParentState = null;
	    	}
	    }
	    	
		// TODO Auto-generated method stub

	}

	@Override
	public int getValue(int row, int column) {
		// TODO Auto-generated method stub
		return this.Board[row][column];
	}

	@Override
	public PuzzleState getParent() {
		// TODO Auto-generated method stub
		return this.ParentState;
	}

	@Override
	public Operation getOperation() {
		// TODO Auto-generated method stub
		return this.Operation;
	}

	@Override
	public int getPathLength() {
		// TODO Auto-generated method stub
		return this.PathLength;
	}

	@Override
	public PuzzleState move(int row, int column, Operation op) {
		// TODO Auto-generated method stub
		if (Board[row][column] == 0) {
			return null;
		}
		
		//duplicate the board 
		else {
			int[][] NewBoard = new int[Board.length][Board[0].length];
			for (int r = 0; r < Board.length; r++) {
				for(int c = 0; c < Board[0].length; c++) {
					NewBoard[r][c] = Board[r][c];
				}
			}
			//update the board after each move
			switch(op) {
			case MOVELEFT:
				if (column - 1 >= 0) {
					if (NewBoard[row][column-1] == 0) {
						NewBoard[row][column-1] = NewBoard[row][column];
						NewBoard[row][column] = 0;
					}
					else {
						return null;
					}
				}
				else {
					return null;
				}
				break;
				
			case MOVERIGHT:
				if (column + 1 < Board.length) {
					if (NewBoard[row][column+1] == 0) {
						NewBoard[row][column+1] = NewBoard[row][column];
						NewBoard[row][column] = 0;
					}
					else {
						return null;
					}
				}
				else {
					return null;
				}
				break;
				
			case MOVEUP:
				if (row - 1 >= 0) {
					if (NewBoard[row-1][column] == 0) {
						NewBoard[row-1][column] = NewBoard[row][column];
						NewBoard[row][column] = 0;
					}
					else {
						return null;
					}
				}
				else {
					return null;
				}
				break;
			
			case MOVEDOWN:
				if (row + 1 < Board[0].length) {
					if (NewBoard[row+1][column] == 0) {
						NewBoard[row+1][column] = NewBoard[row][column];
						NewBoard[row][column] = 0;
					}
					else {
						return null;
					}
				}
				else {
					return null;
				}
				break;
			default:
				break;
			}
        return new SimplePuzzleState(NewBoard, this, op, this.PathLength+1);
		}
	}

	@Override
	public PuzzleState drag(int startRow, int startColumn, int endRow, int endColumn) {
		// TODO Auto-generated method stub
		
		//duplicate the board
		PuzzleState ChildState = new SimplePuzzleState();
		ChildState = this;
		int difference_in_row = endRow - startRow;
		int difference_in_column = endColumn - startColumn;
		
		while (difference_in_row != 0 || difference_in_column != 0) {
			//check if it is a valid move
			if (isEmpty(startRow, startColumn)) {
				return null;
			}
			
			if (!isEmpty(endRow, endColumn)) {
				return null;
			}
			
			//recursion by using moveoperation
			if (isEmpty(startRow, startColumn - 1) && difference_in_column < 0) {
				ChildState = ChildState.move(startRow, startColumn, Operation.MOVELEFT);
				startColumn --;
				difference_in_column ++;
				return ChildState.drag(startRow, startColumn, endRow, endColumn);
			}
			
			if (isEmpty(startRow, startColumn + 1) && difference_in_column > 0) {
				ChildState = ChildState.move(startRow, startColumn, Operation.MOVERIGHT);
				startColumn ++;
				difference_in_column --;
				return ChildState.drag(startRow, startColumn, endRow, endColumn);
			}
			
			if (isEmpty(startRow - 1, startColumn) && difference_in_row < 0) {
				ChildState = ChildState.move(startRow, startColumn, Operation.MOVEUP);
				startRow --;
				difference_in_row ++;
				return ChildState.drag(startRow, startColumn, endRow, endColumn);
			}
			
			if (isEmpty(startRow + 1, startColumn) && difference_in_row > 0) {
				ChildState = ChildState.move(startRow, startColumn, Operation.MOVEDOWN);
				startRow ++;
				difference_in_row --;
				return ChildState.drag(startRow, startColumn, endRow, endColumn);
			}
		}
		
		return this;
	}

	@Override
	public PuzzleState shuffleBoard(int pathLength) {
		// TODO Auto-generated method stub
		PuzzleState ChildState = new SimplePuzzleState();
		ChildState = this;
		

		while (pathLength > 0) {
			
			//determine a random empty tile
			int []RandomEmptyTile = new int[2];
			int []EmptyTile1 = null;
			int []EmptyTile2 = null;
			int []EmptyTile3 = null;
			int i = 0;
			for (int r = 0; r < Board.length; r++) {
				for (int c = 0; c < Board[0].length; c++) {
					if (isEmpty(r, c)) {
						i ++;
						if (i == 1) {
							EmptyTile1 = new int[2];
							EmptyTile1[0] = r;
							EmptyTile1[1] = c;
						}
						
						if (i == 2) {
							EmptyTile2 = new int[2];
							EmptyTile2[0] = r;
							EmptyTile2[1] = c;
						}
						
						if (i == 3) {
							EmptyTile3 = new int[2];
							EmptyTile3[0] = r;
							EmptyTile3[1] = c;
						}
					}
				}
			}
			
			Random randomGenerator = new Random();
			int RandomNumber = randomGenerator.nextInt(i);
			if (RandomNumber == 0) {
				RandomEmptyTile = EmptyTile1;
			}
			
			if (RandomNumber == 1) {
				RandomEmptyTile = EmptyTile2;
			}
			
			if (RandomNumber == 2) {
				RandomEmptyTile = EmptyTile3;
			}

			int row = RandomEmptyTile[0];
			int column = RandomEmptyTile[1];
			
			//shuffle			
			Operation RandomMove = null;
			int RandomMoveNumber = randomGenerator.nextInt(4);
			switch(RandomMoveNumber) {
			
			case 0:
				RandomMove = Operation.MOVELEFT;
				column++;
				break;
				
			case 1:
				RandomMove = Operation.MOVERIGHT;
				column--;
				break;
				
			case 2:
				RandomMove = Operation.MOVEUP;
				row++;
				break;
				
			case 3:
				RandomMove = Operation.MOVEDOWN;
				row--;
				break;
			
			default:
				break;
			}
			
			//avoid row or column out of bound
			if (row < 0 || row >= Board.length || column < 0 || column >= Board[0].length) {
				continue;
			}
			
			//avoid move empty slot
			if (Board[row][column] == 0) {
				continue;
			}
			
			//avoid a path that contains a cycle
			if (this.getOperation() == Operation.MOVELEFT && RandomMove == Operation.MOVERIGHT) {
				continue;
			}
			
			if (this.getOperation() == Operation.MOVERIGHT && RandomMove == Operation.MOVELEFT) {
				continue;
			}
			
			if (this.getOperation() == Operation.MOVEDOWN && RandomMove == Operation.MOVEUP) {
				continue;
			}
			
			if (this.getOperation() == Operation.MOVEUP && RandomMove == Operation.MOVEDOWN) {
				continue;
			}

			ChildState = ChildState.move(row, column, RandomMove);
			pathLength--;
		    return ChildState.shuffleBoard(pathLength);
	    }
		
		return this;
	}
				



	@Override
	public boolean isEmpty(int row, int column) {
		// TODO Auto-generated method stub
		if (row >= 0 && column >= 0 && row < Board.length && column < Board[0].length) {
			if (this.Board[row][column] == 0) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public PuzzleState getStateWithShortestPath() {
		// TODO Auto-generated method stub
		return this;
	}
	
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (obj == null) {
			return false;
		}
		
		if (getClass() != obj.getClass()) {
			return false;
		}
		
		SimplePuzzleState other = (SimplePuzzleState) obj;
		if (!Arrays.deepEquals(Board, other.Board)) {
			return false;
		}
		return true;
	}
	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.deepHashCode(Board);
		return result;
	}
	
}

