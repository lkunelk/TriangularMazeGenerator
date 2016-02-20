import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;


public class MazeGenerator{

	//class variables
	private int count; //counts how many steps it took to create the maze
	private int time = 10; //the time delay for pausing
	private Screen screen; //for communicating with the screen object
	private boolean[][][] maze; //the maze walls
	private Point[][] mazePositions; //cell coordinates to be painted on the screen
	private byte decision; //helps with deciding which wall to remove
	private int scale; //when calculating coordinates of the maze it uses this to scale it
	
	//class fields
	final boolean OPEN = true;//spots that solver can try
	final boolean TRIED = false;//spots that solver already tried

	//constructor
	MazeGenerator(int width, int length, int scale, Screen s){
		
		//setting up the class variables
		screen = s;
		this.scale = scale;
		maze = new boolean[4][width][length];
		screen.setMaze(maze);
		
		//resetting the maze
		for(int z = 0; z < maze.length; z++){
			for(int x = 0; x < maze[0].length; x++){
				for(int y = 0; y < maze[0][0].length; y++){
					maze[z][x][y] = OPEN;
				}
			}
		}
		
		//calculating the coordinates of each maze cell and setting the screen's maze variable to it
		mazePositions = new Point[width][length];
		screen.setMazePositions(calculatePositions());
		System.out.print("the count is: "+count);
		
		screen.update();
	}//end constructor
	
	//method for generating the maze starting from the finish line
	//it makes it the maze harder to generate from finish than from start
	public void generateMaze(){
		
		//resetting the maze positions and walls
		for(int z = 0; z < maze.length; z++){
			for(int x = 0; x < maze[0].length; x++){
				for(int y = 0; y < maze[0][0].length; y++){
					maze[z][x][y] = OPEN;
				}
			}
		}
		
		//generate starting from bottom right corner
		generateMaze((short)(maze[0].length-1),(short)(maze[0][0].length-1));
		
		//let screen know we're finished generating
		screen.setGenerating(false);
		
		//resetting the position back to OPEN so that the screen doesn't paint them as the path taken by the solver
		for(int x = 0; x < maze[0].length; x++){
			for(int y = 0; y < maze[0][0].length; y++){
				maze[0][x][y] = OPEN;
			}
		}
	}//end generate();
	
	//method for generating the maze recursively
	private void generateMaze(short x, short y){
		
		//for tracking the number of steps taken
		count++;
		System.out.println("step: "+count);
		System.out.println("coods: "+x+", "+y);
		
		//set the current spot to tried so that the program doesn't come back to it
		maze[0][x][y] = TRIED;
		
		pause();
		
		//choose which way to go and remove a wall
		decide(x, y);
		removeWall(x, y);
		
		//choose the other path if still possible and remove the wall
		decide(x, y);
		removeWall(x, y);
	}//end generate()
	
	//method for removing the wall between 2 adjacent spots
	private void removeWall(short x, short y) {
		
		//the decision is set by the decide()
		
		//doing decision 1 - removing wall on the left
		if(decision == 1){
			maze[1][x][y] = false;
			maze[3][x-1][y] = false;
			screen.update();
			
			generateMaze((short)(x-1),y);
		}
		else if(decision == 3){//removing wall on the right
			maze[3][x][y] = false;
			maze[1][x+1][y] = false;
			screen.update();
			
			generateMaze((short)(x+1),y);
		}
		else if(decision == 2){//removing wall up or down depending on the position
			if(x%2 == y %2){//if both row and column are the same in terms of being odd and even
				maze[2][x][y] = false;
				maze[2][x][y+1] = false;
				screen.update();
				
				//generate maze from the new position
				generateMaze(x,(short)(y+1));
			}else{
				maze[2][x][y] = false;
				maze[2][x][y-1] = false;
				screen.update();
				
				//generate maze from the new position
				generateMaze(x,(short)(y-1));
			}
		}
	}//end removeWall()

	//method for randomly deciding which way to go
	private void decide(int x, int y) {
		byte decision1 = -1; //first decision
		byte decision2 = -1; //second decision
		
		//checking if left move is valid
		if(x>0 && maze[0][x-1][y]){
			decision1 = 1;
		}
		
		//checking if right move is valid
		if(x<maze[0].length-1 && maze[0][x+1][y]){
			if(decision1 > -1){
				decision2 = 3;
			}else{
				decision1 = 3;
			}
		}
		
		//checking if up or down move is valid
		if(x%2 == y%2){//down
			if(y<maze[0][0].length-1 && maze[0][x][y+1]){
				if(decision1 > -1){
					decision2 = 2;
				}else{
					decision1 = 2;
				}
			}
		}else{//up
			if(y>0 && maze[0][x][y-1]){
				if(decision1 > -1){
					decision2 = 2;
				}else{
					decision1 = 2;
				}
			}
			
		}
		
		//randomly mix up the decisions
		if((int)(Math.random()*100)%2 == 0 && decision2 > -1){
			byte temp = decision1;
			decision1 = decision2;
			decision2 = temp;
		}
		
		//the maze will generate according to decision 1
		decision = decision1;
		
	}
	
	//method for calculating positions
	public Point[][] calculatePositions(){
		
		//for each position in the maze calculate the coordinates of the center of the triangle
		for(int y = 0; y < mazePositions[0].length; y++){
			for(int x = 0; x < mazePositions.length; x++){
				mazePositions[x][y] = getPosition(x, y); 
			}
		}
		
		return mazePositions;
	}//end calculatePositions()
	
	//method for calculating the center of the position based on the row and column
	public Point getPosition(int xi, int yi){
		return new Point( (short)(xi*scale/2) , (short)( (yi+ (yi+ (xi%2) )/2 )*scale/Math.sqrt(3) - ( ((xi%2) * scale/Math.sqrt(3)/2)))  );
	}//end getPosition();
	
	//set method for setting the scale of the maze
	public void setScale(int s){
		scale = s;
	}//end setScale()
	
	//method for saving maze
	public void saveMaze(){
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new File("maze.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		//save scale and offsets
		pw.println(""+screen.getScale());
		pw.println(""+screen.getXOffset());
		pw.println(""+screen.getYOffset());
		
		//saving all the boolean values in the array into the txt file
		for(int z = 1; z < maze.length; z++){
			for(int x = 0; x < maze[0].length; x++){
				for(int y = 0; y < maze[0][0].length; y++){
					if(maze[z][x][y]){
						pw.print("1");
					}
					else{
						pw.print("0");
					}
				}
			}
		}
		
		pw.close();
	}//end saveMaze()
	
	//method for loading the files
	public void loadMaze(){
		BufferedReader br = null;
		
		try {//create buffered reader
			br = new BufferedReader(new FileReader("maze.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		try{//reading in scale and offsets
			
			screen.setScale(Integer.parseInt(br.readLine()));
			screen.setXOffset(Integer.parseInt(br.readLine()));
			screen.setYOffset(Integer.parseInt(br.readLine()));
			
		}catch(Exception e){
			
		}
		
		//completely copy the information from text file
		for(int z = 1; z < maze.length; z++){
			for(int x = 0; x < maze[0].length; x++){
				for(int y = 0; y < maze[0][0].length; y++){
					
					int num = 0;
					
					try{//reading in next number
						num = br.read()-48;
					}catch(Exception e){
					}
					System.out.print(num);
					//if num is 1 then set the corresponding element true, else false
					if(num == 1){
						maze[z][x][y] = true;
					}
					else{
						maze[z][x][y] = false;
					}
				}
				System.out.println();
			}
			System.out.println();
		}
		
		//resetting the maze
		for(int x = 0; x < maze[0].length; x++){
			for(int y = 0; y < maze[0][0].length; y++){
				maze[0][x][y] = OPEN;
			}
		}
	}//end loadMaze()
	
	//Method for pausing
	private void pause(){
		
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
}
