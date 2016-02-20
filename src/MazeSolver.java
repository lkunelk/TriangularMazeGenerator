
public class MazeSolver {
	
	//class variables
	private byte time = 10;//rate at which it moves around the maze(move per ms)
	private Screen screen;//for letting the screen know when to repaint
	private int count;// counts number of steps
	private boolean[][][] maze;// the maze to be solved
	
	//class fields
	private final boolean OPEN = true;//spots that solver can try
	private final boolean TRIED = false;//spots that solver already tried
	
	//constructor
	MazeSolver(boolean[][][] m, Screen s){
		//setting the class variables
		screen = s;
		maze = m;
	}//end constructor
	
	//Method for solving the maze from point (0,0)
	public void solve(){
		
		//reset all the positions to open
		for(int x = 0; x < maze[0].length; x++){
			for(int y = 0; y < maze[0][0].length; y++){
				maze[0][x][y] = OPEN;
			}
		}
		
		//starting to solve from position 0,0 on the maze
		solve((short)0,(short)0);
		
		//letting the screen know that we are finished solving
		screen.setSolving(false);
		
	}//end solve()
	
	//Method for solving the maze recursively
	private boolean solve(short x, short y){
		
		//counts and prints number of steps taken to the finish line
		count++;
		System.out.println("step: "+count);
		System.out.println("coods: "+x+", "+y);
		
		//set the current position to TRIED so that program doesn't return to this spot
		maze[0][x][y] = TRIED;
		
		screen.update();
		
		pause();
		
		//if the current position is the bottom right corner then return true
		if(x == maze[0].length-1 && y == maze[0][0].length-1){
			return true;
		}
		
		//checking if left move is valid
		if(x>0 && maze[0][x-1][y] && !maze[1][x][y]){
			if(solve((short)(x-1), y)){
				return true;
			}
			pause();
		}
		
		//checking if right move is valid
		if(x<maze[0].length-1 && maze[0][x+1][y] && !maze[3][x][y]){
			if(solve((short)(x+1), y)){
				return true;
			}
			pause();
		}
		
		//checking if up or down move is valid
		//the rule whether program has to check down is determined by
		//whether both column and row are even or odd
		//and if the two are different, say odd and even then you can only go up
		if(x%2 == y%2){//down
			if(y<maze[0][0].length-1 && maze[0][x][y+1] && !maze[2][x][y]){
				if(solve(x, (short)(y+1))){
					return true;
				}
				pause();
			}
		}else{//up
			if(y>0 && maze[0][x][y-1] && !maze[2][x][y]){
				if(solve(x, (short)(y-1))){
					return true;
				}
				pause();
			}
			
		}
		
		
		maze[0][x][y] = true;
		screen.update();
		
		//if there is no good path from this position return false
		return false;
	}//end solve()
	
	//Method for pausing
	private  void pause(){
		
		if(time < 0){
			return;
		}
		
		//take initial time
		long last = System.currentTimeMillis();
		
		long now = 0;//for storing the current time
		
		do{//take current time and check it against the initial time
			now = System.currentTimeMillis();
		}//if certain amount of time passes, break out of the loop
		while(now - last < time);
	}//end pause()
	
}//end class
