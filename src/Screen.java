/*
 * The Amazing Triangular Maze program
 * author: Nam Nguyen
 * program description: this program creates triangular mazes, and solves them
 * start date: 29 Nov 14
 * end date: 10 Dec 14
 */

//importing classes
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class Screen extends JPanel{

	private boolean[][][] maze; //for storing information about maze walls
	private Point[][] mazePositions; //for storing the coordinates of each maze cell
	
	private MazeGenerator g; //generates mazes
	private MazeSolver s; // solves mazes
	
	private int time = 1;//time for pausing in ms
	
	private boolean solving; // let's program know if the MazeSolver started solving
	private boolean generating; // lets program know when to generateMaze;
	
	private Graphics2D g2d; // for drawing to the Screen
	private Mouse m; //mouse listener
	private JButton generateButton;
	private JButton solveButton;
	private JButton saveButton;
	private JButton loadButton;
	
	private int scale = 18; // for scaling the size of maze
	private double triangleScale = 1.0;//for scaling the size of triangles to be drawn
	private int xOffset = 80; // for shifting the maze horizontally
	private int yOffset = 100; // for shifting maze vertically
	
	//main method
	public static void main(String[] args) {
		Screen sc = new Screen();
		sc.start();
	}
	
	//constructor
	Screen(){
		
		//setting up frame and buttons
		JFrame f = new JFrame("AMazing! Triangular Maze!");
		JPanel p = new JPanel();
		m = new Mouse();
		
		generateButton = new JButton("Generate!");
		solveButton = new JButton("Solve!");
		saveButton = new JButton("Save!");
		loadButton = new JButton("Load!");
		
		
		addMouseListener(m);
		addMouseMotionListener(m);
		addMouseWheelListener(m);
		generateButton.addMouseListener(m);
		solveButton.addMouseListener(m);
		saveButton.addMouseListener(m);
		loadButton.addMouseListener(m);
		
		//set Jpanel
		p.add(generateButton);
		p.add(solveButton);
		p.add(saveButton);
		p.add(loadButton);
		
		//set frame
		f.add(BorderLayout.SOUTH, p);
		f.setDefaultCloseOperation(f.EXIT_ON_CLOSE);
		f.setSize(900,700);
		f.add(BorderLayout.CENTER, this);
		f.setVisible(true);
		
		//creating maze generator
		g = new MazeGenerator(51,20, (int)(scale/triangleScale), this);
		
		//creating maze solver
		s = new MazeSolver(maze, this);
		
		//ASKING USER IF HE/SHE WANTS TO READ ISTRUCTIONS
		int choice = JOptionPane.showConfirmDialog(null, "Would you like to see instructions?", "Instructions", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				
		if(choice == 0){
			displayInstructions();
		}
		
	}//end constructor
	
	//method for starting the program
	private void start(){
		
		//keep on checking whether the user has clicked to generate or solve the maze
		while(true){
			pause();
			if(generating){
				g.generateMaze();
			}
			else if(solving){
				s.solve();
			}
		}
	}//end start()
	
	//method for painting the maze on the screen
	public void paint(Graphics g){
		super.paintComponent(g);//calling the superclass to paint
		
		g2d = (Graphics2D)g;//casting graphics object to Graphics2D
		
		//in case the maze coordinates have not been created don't paint the maze
		if(mazePositions != null)drawMaze();
	}//end paintComponent()
	
	//method that draws triangles for the maze
	private void drawMaze(){
		
		//cycle through all the positions in the maze
		for(int y = 0; y < mazePositions[0].length; y++){
			for(int x = 0; x < mazePositions.length; x++){
					
				//if both row and column are the same in terms of being odd and even then draw the triangle upright
				 if(x%2 == y%2){
					 //draw upright
					 
					 //get the center of the position
					 int xc = mazePositions[x][y].getX() + xOffset;
					 int yc = mazePositions[x][y].getY() + yOffset;
					 
					 //draw the solver's path
					 if(!generating && maze[0][x][y] == false){
						 g2d.fillOval((int)(xc-scale/6.4), (int)(yc-scale/6.4), (int)(scale/3.2), (int)(scale/3.2));
					 }
					 
					//draw each side of the triangle
					 if(maze[1][x][y]){//left side
						 g2d.setStroke(new BasicStroke(2));
						 g2d.drawLine(xc,(int)(yc-scale/Math.sqrt(3)), (int)(xc - scale/2), (int)(yc + scale/2/Math.sqrt(3)));
					 }
					 if(maze[3][x][y]){//right side
						 g2d.setStroke(new BasicStroke(1));
						 g2d.drawLine(xc,(int)(yc-scale/Math.sqrt(3)), (int)(xc + scale/2), (int)(yc + scale/2/Math.sqrt(3)));
					 }
					 if(maze[2][x][y]){//middle side
						 g2d.setStroke(new BasicStroke(1));
						 g2d.drawLine((int)(xc - scale/2), yc + (int)(scale/2/Math.sqrt(3)), (int)(xc + scale/2), (int)(yc + scale/2/Math.sqrt(3)));
					 }
					 
					
				 }
				 else{
					 
					 //get center of the position
					 int xc = mazePositions[x][y].getX() + xOffset;
					 int yc = mazePositions[x][y].getY() + yOffset;
					 
					 //draw the path of the solver if the maze is not being generated
					 if(!generating && maze[0][x][y] == false){
						 g2d.fillOval((int)(xc-scale/4), (int)(yc-scale/4), (int)(scale/2), (int)(scale/2));
					 }
					 
					 //draw downright
					 //draw each side of the triangle
					 if(maze[1][x][y]){//left side
						 g2d.setStroke(new BasicStroke(1));
						 g2d.drawLine(xc,(int)(yc+scale/Math.sqrt(3)), (int)(xc - scale/2), (int)(yc - scale/2/Math.sqrt(3)));
					 }
					 if(maze[3][x][y]){//right side
						 g2d.setStroke(new BasicStroke(2));
						 g2d.drawLine(xc,(int)(yc+scale/Math.sqrt(3)), (int)(xc + scale/2), (int)(yc - scale/2/Math.sqrt(3)));
					 }
					 if(maze[2][x][y]){//middle side
						 g2d.setStroke(new BasicStroke(1));
						 g2d.drawLine((int)(xc - scale/2), (int)(yc - scale/2/Math.sqrt(3)), (int)(xc + scale/2), (int)(yc - scale/2/Math.sqrt(3)));
					 }
				 
				}
			}
		}
	}//end drawMaze()

	//METHOD FOR DISPLAYING INSTRUCTIONS
	private void displayInstructions(){
		//CREATING NEW FRAME
		JFrame instructionFrame = new JFrame("Instructions");
		
		//CREATING TEXT AREA
		JTextArea text = new JTextArea("\n"
				+ "\n    WELCOME TO THE AMAZING TRIANGULAR MAZE!!!"
				+ "\n"
				+ "\n   This application allows you to generate random mazes"
				+ "\n          and it will solve them for you!"
				+ "\n"
				+ "\n           there are 4 basic functions you can do:"
				+ "\n             (each having its own button)"
				+ "\n            - you can generate a maze"
				+ "\n            - you can solve the maze"
				+ "\n            - you can save the maze for later use"
				+ "\n            - you can load the saved maze"
				+ "\n"
				+ "\n         also, you can use your mouse to drag the maze around the screen"
				+ "\n             and you can zoom in or out by scrolling"
				+ "\n"
				+ "\n   With all of this in mind, you are ready to play with mazes!"
				+ "\n           HAVE FUN!!!");
		
		text.setEditable(false);//so that the user can't change the instructions
		
		//setting the frame
		instructionFrame.getContentPane().add(BorderLayout.CENTER,text);
		instructionFrame.setSize(430,500);
		instructionFrame.setResizable(false);
		instructionFrame.setVisible(true);
	}//END displayInstructions()
	
	//method for updating the screen
	public void update(){
		repaint();
		validate();
	}//end update()
	
	//getter method for xOffset
	public int getXOffset(){
		return xOffset;
	}//end getXOffset()
	
	//getter method for xOffset
	public int getYOffset(){
		return yOffset;
	}//end getXOffset()
	
	//getter method for scale
	public int getScale(){
		return scale;
	}//end getScale()
	
	//set method for xOffset
	public void setXOffset(int i){
		xOffset = i;
	}//end setXOffset()
	
	//getter method for xOffset
	public void setYOffset(int i){
		yOffset = i;
	}//end setYOffset()
	
	//set method for scale
	public void setScale(int i){
		scale = i;
	}//end getXOffset()
	
	//setter method for setting the generating variable
	public void setGenerating(boolean b){
		generating = b;
	}//end setGenerating()
	
	//setter method for setting the solving variable
	public void setSolving(boolean b){
		solving = b;
	}//end setSolving()
	
	//set method for setting the maze
		public void setMaze(boolean[][][] m){
			maze = m;
		}//end setMaze()
		
	//set method for the maze positions
	public void setMazePositions(Point[][] m){
		mazePositions = m;
	}//end setMazePositions()
	
	//Method for pausing
	public void pause(){
		
		//take initial time
		long last = System.currentTimeMillis();
		
		long now = 0;//for storing the current time
		
		do{//take current time and check it against the initial time
			now = System.currentTimeMillis();
		}//if certain amount of time passes, break out of the loop
		while(now - last < time);
		
	}//end pause()
	
	//inner class for mouse events
	class Mouse implements MouseMotionListener, MouseListener, MouseWheelListener{

		//class variables
		int x, y;
		int xOff, yOff;
		
		
		//when mouse is dragged update the offsets to shift the maze vertically and horizontally
		public void mouseDragged(MouseEvent e) {
			xOffset=e.getX()-x+xOff;
			yOffset=e.getY()-y+yOff;
			System.out.println("x: "+xOffset+"y: "+yOffset);
			repaint();
		}//end mouseDragged()
		
		//when mouse is released save the coordinates of the offsets
		public void mouseReleased(MouseEvent arg0) {
			xOff = xOffset;
			yOff = yOffset;
		}//end mouseReleased()
		
		//Method for zooming in and out of maze
		public void mouseWheelMoved(MouseWheelEvent e) {
			scale+=e.getWheelRotation();
			g.setScale((int)(scale/triangleScale));
			g.calculatePositions();
			repaint();
			//System.out.println(scale);
		}//end mouseWheeled()
		
		//getting coordinates when mouse is pressed
		public void mousePressed(MouseEvent e) {
			x = e.getX();
			y = e.getY();
			System.out.println("x: "+x+"y: "+y);
		}//end mousePressed()
		
		
		//if any buttons are pressed do the appropriate action
		public void mouseClicked(MouseEvent e) {
			if(e.getSource().equals(generateButton)){//if generate button was pressed
				if(!generating && !solving){
					Screen.this.generating = true;
				}
			}else if(e.getSource().equals(solveButton)){
				if(!solving && !generating){
					Screen.this.solving = true;
				}
			}else if(e.getSource().equals(saveButton)){
				if(!solving && !generating){
					g.saveMaze();
				}
			}else if(e.getSource().equals(loadButton)){
				if(!solving && !generating){
					g.loadMaze();
					g.setScale((int)(scale/triangleScale));
					g.calculatePositions();
					xOff = xOffset;
					yOff = yOffset;
					repaint();
				}
			}
		}//end mouseClicked()
		
		//implemented methods not being used
		
		public void mouseMoved(MouseEvent e) {}

		public void mouseEntered(MouseEvent arg0) {}

		public void mouseExited(MouseEvent arg0) {}

	}//end Mouse class
	
}//end Screen class
