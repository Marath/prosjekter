import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Grid {

    //test main to test the gridlayout
    public static void main(String[] args) {	
	int r = 20;
	int c = 20;
	int x_dim = 500, y_dim = 500;
	Grid g = new Grid(r, c, x_dim, y_dim);
    }
    
    private int size, row, col, x_dim, y_dim;
    private Grid grid[][];
    private JLabel[][] gameBoard;
    
    Grid(int r, int c, int x, int y) {
	row = r;
	col = c;
	x_dim = x;
	y_dim = y;
	size = row * col;
	gameBoard = new JLabel[row][col];
	setUpGrid(row, col, x_dim, y_dim);
    }
    
    public int getRow() 
    {
	return row;
    }
    
    public int getCol() 
    {
	return col;
    } 

    public int getSize() 
    {
	return size;
    } 

    void setUpGrid(int rows, int cols, int x_dims, int y_dims) 
    {
	
	//set up the main frame
	JFrame frame = new JFrame("Battleships");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setSize(x_dims, y_dims);

	//set up the JPanel
	JPanel gamePanel = new JPanel(new GridLayout(rows, cols));

	//set up the gameBoard
	for(int i = 0; i < rows; i++) {
	    for(int j = 0; j < cols; j++) {
		gameBoard[i][j] = new JLabel();
		gameBoard[i][j].setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
		gameBoard[i][j].setForeground(Color.BLACK);
		gameBoard[i][j].setBackground(Color.LIGHT_GRAY);
					  
		gameBoard[i][j].setHorizontalAlignment(JTextField.CENTER);
	       
    		gamePanel.add(gameBoard[i][j]);
	    }
	}

	frame.getContentPane().add(gamePanel, BorderLayout.CENTER);
	gamePanel.setSize(x_dim-10, y_dim-10);
	frame.setVisible(true);
    }

}
