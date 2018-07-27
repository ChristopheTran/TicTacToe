import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
/**
 * TicTacToe game that incorporates GUI.
 *
 * @author Christophe Tran
 * @version November 29, 2017
 */
public class TicTacToe implements ActionListener
{
   public static final String PLAYER_X = "X"; // player using "X"
   public static final String PLAYER_O = "O"; // player using "O"
   public static final String EMPTY = " ";  // empty cell
   public static final String TIE = "T"; // game ended in a tie
   
   private String winner;   // winner: PLAYER_X, PLAYER_O, TIE, EMPTY = in progress
   private String player;   // current player (PLAYER_X or PLAYER_O
   private String board[][]; // 3x3 array representing the board
   private int numFreeSquares; // number of squares still free
   private int xWins; // number of times player X won
   private int oWins; // number of times player Y won
   private int ties; // number of ties
    
    
   private JLabel status; // player's turn
   private JTextField xScore; // total wins of player X
   private JTextField tieScore; // total ties
   private JTextField oScore; // total wins of player O
   private JButton[][] buttons;
   private ImageIcon X, O;
   private JMenuItem newGame;
   private JMenuItem quitGame;
   
   
   public TicTacToe()
   {
       X = new ImageIcon(this.getClass().getResource("X.png"));
       O = new ImageIcon(this.getClass().getResource("O.png"));   
       
       JFrame frame = new JFrame("TicTacToe");  
       Container contentPane = frame.getContentPane();
       contentPane.setLayout(new BorderLayout());
       
       JMenuBar menubar = new JMenuBar();
       frame.setJMenuBar(menubar);
       
       JMenu fileMenu = new JMenu("Game");
       menubar.add(fileMenu);
       
       newGame = new JMenuItem("New");
       fileMenu.add(newGame);
       
       quitGame = new JMenuItem("Quit");
       fileMenu.add(quitGame);
       // this allows us to use shortcuts (e.g. Ctrl-N and Ctrl-Q)
       final int SHORTCUT_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(); // to save typing       
       newGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, SHORTCUT_MASK));       
       quitGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, SHORTCUT_MASK));       
       
       //listen for menu selections
       newGame.addActionListener(this);
       quitGame.addActionListener(this);
       
       JPanel buttonPanel = new JPanel();
       buttonPanel.setLayout(new GridLayout(3,3));
       
       buttons = new JButton[3][3];
       for(int i=0; i<3; i++) {
           for(int j=0; j<3; j++) {
               buttons[i][j] = new JButton();
               buttonPanel.add(buttons[i][j]);
               buttons[i][j].addActionListener(this);
            }
        }
       contentPane.add(buttonPanel, BorderLayout.CENTER);
       
       status = new JLabel();
       status.setFont(new Font(null,Font.BOLD,18));
       contentPane.add(status,BorderLayout.SOUTH);
       
       JPanel scorePanel = new JPanel();
       scorePanel.setLayout(new BoxLayout(scorePanel,BoxLayout.X_AXIS));
       
       xScore = new JTextField();
       xScore.setEditable(false);
       xScore.setFont(new Font(null,Font.BOLD,12));
       //xScore.setText("Player X wins: 5");
       scorePanel.add(xScore);
       
       tieScore = new JTextField();
       tieScore.setEditable(false);
       tieScore.setFont(new Font(null,Font.BOLD,12));
       //tieScore.setText("Ties: 5");
       scorePanel.add(tieScore);
       
       oScore = new JTextField();
       oScore.setEditable(false);
       oScore.setFont(new Font(null,Font.BOLD,12));
       //oScore.setText("Player Y wins: 5");
       scorePanel.add(oScore);
       
       contentPane.add(scorePanel, BorderLayout.NORTH); // add scorePanel to the frame
       
       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       //frame.pack(); // This creates the size of the frame (just enough to fit everything)
       frame.setSize(500,500);
       frame.setResizable(true);
       frame.setVisible(true);
       
       // Create the game
       board = new String[3][3];
       clearBoard();
       xWins = 0;
       oWins = 0;
       ties = 0;
       updateStatus();
   }
   
   public void actionPerformed(ActionEvent e)
   {
       Object o = e.getSource(); //get the action
       if( o instanceof JButton && winner.equals(EMPTY)) { //see if it's a JButton and there is no winner yet
           //JButton button = (JButton) o;
           for(int i=0; i <3; i ++) {
               for(int j=0; j<3; j++) {
                   if(buttons[i][j] == e.getSource()) {
                       if(player == PLAYER_X) {
                           buttons[i][j].setIcon(X);
                           buttons[i][j].setEnabled(false); //Can no longer press same button
                           board[i][j] = player; //fill the virtual board square with player
                           numFreeSquares--;
                           if(haveWinner(i,j)) { // Check if there's a winner
                               winner = player;
                               xWins++;
                               for(int x=0; x <3; x ++) {
                                   for(int y=0; y<3; y++) {
                                       buttons[x][y].setEnabled(false); // game is over, so disable all buttons
                                   }
                               }
                           }
                           else if (numFreeSquares==0) {
                               winner = TIE; // board is full so it's a tie
                               ties++;
                           } 
                           player = PLAYER_O;
                           updateStatus();
                       }
                       else if(player == PLAYER_O) {
                           buttons[i][j].setIcon(O);
                           buttons[i][j].setEnabled(false); 
                           board[i][j] = player;
                           numFreeSquares--;
                           if(haveWinner(i,j)) {
                               winner = player;
                               oWins++;
                               for(int x=0; x <3; x ++) {
                                   for(int y=0; y<3; y++) {
                                       buttons[x][y].setEnabled(false); // game is over, so disable all buttons
                                   }
                               }
                           }
                           else if (numFreeSquares==0) {
                               winner = TIE; // board is full so it's a tie
                           } 
                           player = PLAYER_X;
                           updateStatus();
                       }
                   }
               }
           }
       }
       else { // it's a JMenuItem     
           //JMenuItem item = (JMenuItem) o;
           if(newGame == e.getSource()) {
               clearBoard();      // start a new game
               updateStatus();
           }
           else if(quitGame == e.getSource()) {
               System.exit(0); 
           }
       }
   }
   
   /**
    * Sets everything up for a new game.  Marks all squares in the Tic Tac Toe board as empty,
    * and indicates no winner yet, 9 free squares and the current player is player X.
    */
   public void clearBoard()
   {
      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 3; j++) {
            board[i][j] = EMPTY;
            buttons[i][j].setIcon(null);
            buttons[i][j].setEnabled(true); // re-enables all buttons (won't matter if starting first game)
                
         }
      }
      winner = EMPTY;
      numFreeSquares = 9;
      player = PLAYER_X;     // Player X always has the first turn.
   }
 
   private boolean haveWinner(int row, int col) 
   {
       // unless at least 5 squares have been filled, we don't need to go any further
       // (the earliest we can have a winner is after player X's 3rd move).

       if (numFreeSquares>4) return false;

       // Note: We don't need to check all rows, columns, and diagonals, only those
       // that contain the latest filled square.  We know that we have a winner 
       // if all 3 squares are the same, as they can't all be blank (as the latest
       // filled square is one of them).

       // check row "row"
       if ( board[row][0].equals(board[row][1]) &&
            board[row][0].equals(board[row][2]) ) return true;
       
       // check column "col"
       if ( board[0][col].equals(board[1][col]) &&
            board[0][col].equals(board[2][col]) ) return true;

       // if row=col check one diagonal
       if (row==col)
          if ( board[0][0].equals(board[1][1]) &&
               board[0][0].equals(board[2][2]) ) return true;

       // if row=2-col check other diagonal
       if (row==2-col)
          if ( board[0][2].equals(board[1][1]) &&
               board[0][2].equals(board[2][0]) ) return true;

       // no winner yet
       return false;
   }
   
   public void updateStatus()
   {
       if(winner.equals(EMPTY)) { //if no winners
           status.setText("Game in progress: Player " + player + "'s turn");
       }
       else { // if there's a winner or tie
           if(winner.equals(TIE)) {
               status.setText("Game Over: Game was a Tie");
           }
           else {
               status.setText("Game Over: " + winner + " wins");
           }
       }
       xScore.setText("Player X wins: " + xWins);
       tieScore.setText("Ties: " + ties);
       oScore.setText("Player O wins: " + oWins);
       
   }
}
